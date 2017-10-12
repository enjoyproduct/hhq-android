package com.ntsoft.ihhq.controller.main;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.ntsoft.ihhq.controller.adapter.NotificationAdapter;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.NotificationModel;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    Activity mActivity;
    ListView listView;
    NotificationAdapter mAdapter;
    private PullToRefreshListView mPullRefreshHomeListView;
    ArrayList<NotificationModel> arrNotifications;

    String api;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        initVariables();
        initUI(view);
        getAllNotifications();
        return view;

    }
    void initVariables() {
        mActivity = getActivity();
        arrNotifications = new ArrayList<>();
        api = API.GET_NOTIFICATIONS;
    }
    void initUI(View view) {
        ///create listview
        mPullRefreshHomeListView = (PullToRefreshListView)view.findViewById(R.id.lv_home);
        mPullRefreshHomeListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                getAllNotifications();
                mPullRefreshHomeListView.onRefreshComplete();
            }
        });

        listView = mPullRefreshHomeListView.getRefreshableView();
        mAdapter = new NotificationAdapter(mActivity, arrNotifications);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!arrNotifications.get(position - 1).message.replace(" ", "").isEmpty()) {
                    Utils.showDialog(mActivity, "Details", arrNotifications.get(position - 1).message);
                }
            }
        });
    }
    private void getAllNotifications() {
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
                                NotificationModel notificationModel = new NotificationModel(jsonObject);
                                arrNotifications.add(notificationModel);
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
