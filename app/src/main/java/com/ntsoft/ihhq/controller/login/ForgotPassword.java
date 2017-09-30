package com.ntsoft.ihhq.controller.login;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.ntsoft.ihhq.utility.UIUtility;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword extends Fragment {

    Activity mActivity;
    EditText etEmail;
    Button btnReset;
    ImageButton ibBack;

    public ForgotPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        mActivity = getActivity();
        initUI(view);
        return view;
    }
    void initUI(View view) {
        etEmail = (EditText)view.findViewById(R.id.et_reset);
        btnReset = (Button)view.findViewById(R.id.btn_login);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmail.getText().toString().isEmpty()) {
                    Utils.showOKDialog(mActivity, "Please input email");
                    return;
                }
                if (!Utils.isEmailValid(etEmail.getText().toString())) {
                    Utils.showOKDialog(mActivity, "Invalid email");
                    return;
                }
                sendEmail();
            }
        });


        ibBack = (ImageButton)view.findViewById(R.id.ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtility.hideSoftKeyboard(mActivity);
                ((StartActivity)mActivity).popFragment();
            }
        });

    }

    void sendEmail() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        Utils.showProgress(mActivity);
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", etEmail.getText().toString());
        CustomRequest customRequest = new CustomRequest(Request.Method.POST, API.FORGOT_PASSWORD, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            showConfirmDlg();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(mActivity, "Network Timeout Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(mActivity, "Auth Failure Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(mActivity, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(mActivity, "Network Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(mActivity, "Parse Error", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(mActivity, "Unknown Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(customRequest);
    }
    void showConfirmDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(Constant.INDECATOR);
        builder.setMessage("Please check your email.");
        builder.setCancelable(true);
        builder.setPositiveButton( "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UIUtility.hideSoftKeyboard(mActivity);
                        ((StartActivity)mActivity).popFragment();
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
