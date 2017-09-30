package com.ntsoft.ihhq.controller.home;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.adapter.HomeDocumentAdapter;
import com.ntsoft.ihhq.model.DocumentModel;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.utility.FileDownloadCompleteListener;
import com.ntsoft.ihhq.utility.FileDownloader;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeDocumentFragment extends Fragment {
    private static Activity mActivity;
    TextView tvDocumentCount;
    Button btnUpload;
    Spinner spinnerSortBy;
    private ListView lvMain;
    private PullToRefreshListView mPullRefreshHomeListView;
    String api;
    int sortBy;
    ArrayList<DocumentModel> arrDocuments;
    HomeDocumentAdapter mAdapter;
    FileModel fileModel;
    public HomeDocumentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_document, container, false);

        initVariable();
        initUI(view);
        return view;
    }
    private void initVariable() {
        mActivity = getActivity();
        arrDocuments = new ArrayList<>();
        sortBy = 0;
        fileModel = ((HomeActivity)mActivity).fileModel;
    }
    private void initUI(View view) {
        tvDocumentCount = (TextView)view.findViewById(R.id.tv_doc_count);
        btnUpload = (Button)view.findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, UploadNewDocumentActivity.class);
                intent.putExtra("file", fileModel);
                startActivity(intent);
            }
        });
        spinnerSortBy = (Spinner)view.findViewById(R.id.spinner_sort);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sort_by)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(spinnerArrayAdapter);
        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = position;
                getDocuments();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mPullRefreshHomeListView = (PullToRefreshListView)view.findViewById(R.id.lv_home);
        lvMain = mPullRefreshHomeListView.getRefreshableView();
        mAdapter = new HomeDocumentAdapter(mActivity, arrDocuments);
        lvMain.setAdapter(mAdapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                downloadFile(position - 1);
            }
        });
        if (Global.getInstance().me.role.equals(Constant.arrUserRoles[5])) {
            btnUpload.setVisibility(View.INVISIBLE);
            btnUpload.setEnabled(false);
        } else {
            btnUpload.setVisibility(View.VISIBLE);
            btnUpload.setEnabled(true);
        }
    }
    void getDocuments() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        if (fileModel == null) {
            return;
        }
        api = API.BASE_API_URL + "files/" + String.valueOf(fileModel.file_id) + "/documents";
        if (sortBy <= 1) {
            api = api + "?sort=date";
        } else if (sortBy == 2) {
            api = api + "?sort=name";
        }
        arrDocuments.clear();
        Utils.showProgress(mActivity);
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JsonArrayRequest request = new JsonArrayRequest( api, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                Utils.hideProgress();
                requestQueue.getCache().remove(api);
                int count = response.length();
                if (count > 1) {
                    tvDocumentCount.setText(String.valueOf(count) + " Documents");
                } else {
                    tvDocumentCount.setText(String.valueOf(count) + " Document");
                }
                try {
                    for (int i = 0; i < count; i ++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        DocumentModel documentModel = new DocumentModel(jsonObject);
                        arrDocuments.add(documentModel);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hideProgress();
                Toast.makeText(mActivity, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };
        requestQueue.add(request);
    }
    void downloadFile(int position) {
        String url = API.BASE_FILE_URL + String.valueOf(arrDocuments.get(position).document_id) + "/download";
        FileDownloader.downloadFile(mActivity, url, arrDocuments.get(position).name + "." + arrDocuments.get(position).file_extension, new FileDownloadCompleteListener() {
            @Override
            public void onComplete(String filePath) {
//                Toast.makeText(mActivity, filePath, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File(filePath);

                if (filePath.contains(".pdf")) {
                    intent.setDataAndType( Uri.fromFile( file ), "application/pdf" );
                } else if (filePath.contains(".doc") || filePath.contains(".word")) {
                    intent.setDataAndType( Uri.fromFile( file ), "application/msword");
                } else {
                    intent.setDataAndType( Uri.fromFile( file ), "application/vnd.ms-excel" );
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });
    }
}
