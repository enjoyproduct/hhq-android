package com.ntsoft.ihhq.controller.correspondence;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.ntsoft.ihhq.controller.adapter.CorrespondenceAdapter;
import com.ntsoft.ihhq.model.CorrespondenceModel;
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
public class CorrespondenceCompletedFragment extends Fragment {

    private Activity mActivity;
    Spinner spinnerSortBy;
    SearchView searchView;
    private ListView lvMain;
    private PullToRefreshListView mPullRefreshHomeListView;
    String api;
    int sortBy;
    ArrayList<CorrespondenceModel> arrCorrespondences;
    CorrespondenceAdapter mAdapter;

    public CorrespondenceCompletedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_correspondence_completed, container, false);

        initVariable();
        initUI(view);


        return view;
    }

    private void initVariable() {
        mActivity = getActivity();
        arrCorrespondences = new ArrayList<>();
        sortBy = 0;
    }
    private void initUI(View view) {
        spinnerSortBy = (Spinner)view.findViewById(R.id.spinner_sort);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sort_by)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(spinnerArrayAdapter);
        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = position;
                arrCorrespondences.clear();
                api = getURL();
                getAllCorrespondence();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ///create listview
        mPullRefreshHomeListView = (PullToRefreshListView)view.findViewById(R.id.lv_home);
        mPullRefreshHomeListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                getAllCorrespondence();
                mPullRefreshHomeListView.onRefreshComplete();
            }
        });
        lvMain = mPullRefreshHomeListView.getRefreshableView();
        mAdapter = new CorrespondenceAdapter(mActivity, arrCorrespondences);
        lvMain.setAdapter(mAdapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, CorrespondenceDetailActivity.class);
                intent.putExtra("correspondence", arrCorrespondences.get(position -1));
                startActivity(intent);
            }
        });
        searchView = (SearchView)view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    api = getURL() + "&q=" + query;
                } else {
                    api = getURL();
                }
                arrCorrespondences.clear();
                getAllCorrespondence();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    api = getURL();
                    arrCorrespondences.clear();
                    getAllCorrespondence();
                }
                return false;
            }
        });
    }
    private String getURL() {
        api = API.GET_TICKETS_CLOSED;
        if (sortBy <= 1) {
            api = api + "&sort=-date";
        } else if (sortBy == 2) {
            api = api + "&sort=subject";
        }
        return api;
    }
    private void getAllCorrespondence() {
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

                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i ++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                CorrespondenceModel correspondenceModel = new CorrespondenceModel(jsonObject);
                                arrCorrespondences.add(correspondenceModel);
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
