package com.ntsoft.ihhq.controller.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.CustomRequest;
import com.android.volley.toolbox.Volley;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.qrscan.ScanActivity;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.UserModel;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanQRCodeFragment extends Fragment {

    Activity mActivity;
    Button btnScan, btnSubmit;
    TextView tvResult;
    String file_ref = "";
    String qrCode = "";

    int index = -1;

    private static int SCAN_REQUEST_CODE = 101;
    public ScanQRCodeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_qrcode, container, false);
        mActivity = getActivity();
        if (getArguments() != null) {
            file_ref = getArguments().getString("file_ref");
            if (file_ref == null) {
                file_ref = "";
            }
            index = getArguments().getInt("index", -1);
        }
        initUI(view);
        scan();
        return view;
    }

    void initUI(View view) {
        tvResult = (TextView)view.findViewById(R.id.tv_result);
        btnScan =  (Button) view.findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
        btnSubmit =  (Button) view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!qrCode.isEmpty()) {
                    submitQRCode(qrCode);
                }
            }
        });
    }
    void scan() {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                qrCode = data.getStringExtra("code");
                tvResult.setText(qrCode);
                tvResult.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                btnSubmit.setClickable(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void submitQRCode(String code) {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        Utils.showProgress(mActivity);
        Map<String, String> params = new HashMap<String, String>();
        params.put("qr_code", code);
        params.put("file_ref", file_ref);
        CustomRequest customRequest = new CustomRequest(Request.Method.POST, API.SUBMIT_QR_CODE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            if (response.has("error")) {
                                Toast.makeText(mActivity, response.getString("error"), Toast.LENGTH_LONG).show();
                                if (index >= 0) {
                                    Intent intent = new Intent();
                                    intent.putExtra("index", index);
                                    mActivity.setIntent(intent);
                                    mActivity.setResult(Activity.RESULT_OK);
                                    mActivity.finish();
                                }
                            } else {
                                Toast.makeText(mActivity, "Submit Success", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        Utils.showToast(mActivity, "qr code is not match");

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(customRequest);
    }
}
