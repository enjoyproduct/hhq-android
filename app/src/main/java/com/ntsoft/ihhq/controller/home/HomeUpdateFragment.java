package com.ntsoft.ihhq.controller.home;


import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.controller.adapter.HomeUpdateAdapter;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.PaymentModel;
import com.ntsoft.ihhq.model.UpdateModel;
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
public class HomeUpdateFragment extends Fragment {

    Activity mActivity;
    ListView listView;
    HomeUpdateAdapter mAdapter;
    ArrayList<UpdateModel> arrUpdates;
    FileModel fileModel;

    TextView tvName, tvPercent;
    ProgressBar progressBar;

    public HomeUpdateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home_update, container, false);
        initVariables();
        initUI(view);
        getUpdates();
        return view;
    }
    void initVariables() {
        mActivity = getActivity();
        arrUpdates = new ArrayList<>();
        fileModel = ((HomeActivity)mActivity).fileModel;
    }
    void initUI(View view) {
        listView = (ListView)view.findViewById(R.id.listview);
        mAdapter = new HomeUpdateAdapter(mActivity);

        listView.setAdapter(mAdapter);

        tvName = (TextView) view.findViewById(R.id.tv_status);
        tvPercent = (TextView) view.findViewById(R.id.tv_percent);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
    }
    void getUpdates() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        if (fileModel == null) {
            return;
        }
        Utils.showProgress(mActivity);
        final String endPoint = String.format(API.GET_LOGISTICS_MILESTONES, fileModel.file_id);
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JsonArrayRequest request = new JsonArrayRequest( endPoint, new Response.Listener<JSONArray>(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(JSONArray response) {
                Utils.hideProgress();
                requestQueue.getCache().remove(endPoint);
                int count = response.length();
                try {
                    for (int i = 0; i < count; i ++) {
                        JSONObject jsonObject = response.getJSONObject(i);

                        if (i == 0) {
                            UpdateModel updateModel1 = new UpdateModel(jsonObject);
                            updateModel1.isSeperator = 1;
                            mAdapter.addSectionHeaderItem(updateModel1);
                        }
                        if (i > 0) {
                            String currentDate = jsonObject.getString("key");
                            String previousDate = response.getJSONObject(i - 1).getString("key");
                            if (currentDate.equals(previousDate)) {

                            } else {
                                UpdateModel updateModel2 = new UpdateModel(jsonObject);
                                updateModel2.isSeperator = 1;
                                mAdapter.addSectionHeaderItem(updateModel2);
                            }
                        }
                        UpdateModel updateModel = new UpdateModel(jsonObject);
                        mAdapter.addItem(updateModel);

                        ///
                        if (updateModel.type.equals("milestone")) {
                            tvName.setText(updateModel.milestoneModel.activity);
                            tvPercent.setText(updateModel.milestoneModel.percent + "%");
                            int progress = Integer.parseInt(updateModel.milestoneModel.percent);
                            progressBar.setProgress(progress);
                            if (progress <= 25) {
                                progressBar.setProgressTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.red)));
                            } else if (progress <= 50) {
                                progressBar.setProgressTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.yellow)));
                            } else if (progress <= 75) {
                                progressBar.setProgressTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.lightGreen)));
                            } else {
                                progressBar.setProgressTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.green)));
                            }
                        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1111) {
            if (resultCode == Activity.RESULT_OK) {
                int index = data.getIntExtra("index", -1);
                if (index >= 0) {
                    arrUpdates.get(index).logisticModel.status = 1;
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
