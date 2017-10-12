package com.ntsoft.ihhq.controller.home;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.controller.correspondence.CorrespondenceDetailActivity;
import com.ntsoft.ihhq.controller.adapter.CorrespondenceAdapter;
import com.ntsoft.ihhq.model.CorrespondenceModel;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeCorrespondenceFragment extends Fragment {

    Activity mActivity;
    ListView listView;
    CorrespondenceAdapter mAdapter;
    ArrayList<CorrespondenceModel> arrCorrespondences;
    FileModel fileModel;
    String api;
    View view;

    public HomeCorrespondenceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_correspondence, container, false);

        return view;
    }

    @Override
    public void onResume() {
        initVariable();
        initUI(view);
        getCorrespondences();
        super.onResume();
    }

    private void initVariable() {
        mActivity = getActivity();
        arrCorrespondences = new ArrayList<>();
        fileModel = ((HomeActivity)mActivity).fileModel;
    }
    void initUI(View view) {
        listView = (ListView)view.findViewById(R.id.listview);
        mAdapter = new CorrespondenceAdapter(mActivity, arrCorrespondences);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, CorrespondenceDetailActivity.class);
                intent.putExtra("correspondence", arrCorrespondences.get(position));
                intent.putExtra("fileModel", fileModel);
                startActivity(intent);
            }
        });
    }
    void getCorrespondences() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        if (fileModel == null) {
            return;
        }
        api = API.BASE_API_URL + "files/" + String.valueOf(fileModel.file_id) + "/tickets";

        arrCorrespondences.clear();
        Utils.showProgress(mActivity);
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JsonArrayRequest request = new JsonArrayRequest( api, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                Utils.hideProgress();
                int count = response.length();
                requestQueue.getCache().remove(api);
                try {
                    for (int i = 0; i < count; i ++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CorrespondenceModel correspondenceModel = new CorrespondenceModel(jsonObject);
                        arrCorrespondences.add(correspondenceModel);
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
}
