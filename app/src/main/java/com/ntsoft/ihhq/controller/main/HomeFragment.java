package com.ntsoft.ihhq.controller.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.CustomRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.controller.home.HomeActivity;
import com.ntsoft.ihhq.controller.adapter.HomeAdapter;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static Activity mActivity;
    TextView tvMyFiles, tvActiveCount;
    SearchView searchView;
    private ListView lvMain;
    private PullToRefreshListView mPullRefreshHomeListView;
    String api;
    ArrayList<FileModel> arrFiles;
    HomeAdapter mAdapter;
    int totalCount = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initVariable();
        initUI(view);
        getAllFiles();

        return view;
    }
    private void initVariable() {
        mActivity = getActivity();
        arrFiles = new ArrayList<>();
        api = API.GET_FILES;
    }

    private void initUI(View view) {
        ///create listview
        mPullRefreshHomeListView = (PullToRefreshListView)view.findViewById(R.id.lv_home);
        mPullRefreshHomeListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                getAllFiles();
                mPullRefreshHomeListView.onRefreshComplete();
            }
        });

        lvMain = mPullRefreshHomeListView.getRefreshableView();
        mAdapter = new HomeAdapter(mActivity, arrFiles);
        lvMain.setAdapter(mAdapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.putExtra("file", arrFiles.get(position - 1));
                startActivity(intent);
            }
        });
        tvMyFiles = (TextView)view.findViewById(R.id.tv_my_files);
        tvActiveCount = (TextView)view.findViewById(R.id.tv_active_count);
        searchView = (SearchView)view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    api = API.GET_FILES + "&q=" + query;
                } else {
                    api = API.GET_FILES;
                }
                arrFiles.clear();
                getAllFiles();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    api = API.GET_FILES;
                    arrFiles.clear();
                    getAllFiles();
                }
                return false;
            }
        });
    }
    private void getAllFiles() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        if (api.isEmpty()) {
            return;
        }
        Utils.showProgress(mActivity);
        Map<String, String> params = new HashMap<String, String>();
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

        CustomRequest customRequest = new CustomRequest(Request.Method.GET, api, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            requestQueue.getCache().remove(api);
                            String next_page_url = response.getString("next_page_url");
                            if (next_page_url == "null") {
                                api = "";
                            } else {
                                api = next_page_url;
                            }
                            totalCount = response.getInt("total");
                            if (totalCount > 1) {
                                tvActiveCount.setText(String.valueOf(totalCount) + " actives");
                            } else {
                                tvActiveCount.setText(String.valueOf(totalCount) + " active");
                            }
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i ++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                FileModel fileModel = new FileModel(jsonObject);
                                arrFiles.add(fileModel);
                            }
                            mAdapter.notifyDataSetChanged();
                        }catch (Exception e) {
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
        requestQueue.add(customRequest);
    }

}
