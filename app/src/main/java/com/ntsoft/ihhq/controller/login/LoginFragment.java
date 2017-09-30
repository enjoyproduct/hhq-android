package com.ntsoft.ihhq.controller.login;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.ntsoft.ihhq.controller.main.MainActivity;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.UserModel;
import com.ntsoft.ihhq.utility.UIUtility;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    Activity mActivity;
    EditText etEmail, etPassword;
    Button btnLogin, btnForgotPassword;
    CheckBox chRememberMe;
    ImageButton ibBack;

    String email = "", password = "";
    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mActivity = getActivity();
        initUI(view);
        checkAutoLogin();
        return view;
    }
    void initUI(View view) {
        etEmail = (EditText)view.findViewById(R.id.et_email);
        etPassword = (EditText)view.findViewById(R.id.et_password);

        chRememberMe = (CheckBox)view.findViewById(R.id.ch_remember_me);

        btnLogin = (Button)view.findViewById(R.id.btn_login);
        btnForgotPassword = (Button)view.findViewById(R.id.btn_forgot_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
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
                if (etPassword.getText().toString().isEmpty()) {
                    Utils.showOKDialog(mActivity, "Please input password");
                    return;
                }
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                doLogin();
            }
        });
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)mActivity).pushFragment(2);
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
    void checkAutoLogin() {
        email = Utils.getFromPreference(mActivity, Constant.EMAIL);
        password = Utils.getFromPreference(mActivity, Constant.PASSWORD);
        if (!email.isEmpty() && !password.isEmpty()) {
            etEmail.setText(email);
            chRememberMe.setChecked(true);
            doLogin();
        }
    }
    void doLogin() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        Utils.showProgress(mActivity);
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Constant.DEVICE_TOKEN));
        params.put(Constant.DEVICE_TYPE, Constant.ANDROID);
        params.put("email", email);
        params.put("password", password);
        CustomRequest customRequest = new CustomRequest(Request.Method.POST, API.LOGIN, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            UserModel userModel = new UserModel(response);
                            Global.getInstance().setMe(userModel);
                            if (chRememberMe.isChecked()) {
                                Utils.saveToPreference(mActivity, Constant.EMAIL, email);
                                Utils.saveToPreference(mActivity, Constant.PASSWORD, password);
                            } else {
                                Utils.saveToPreference(mActivity, Constant.EMAIL, "");
                                Utils.saveToPreference(mActivity, Constant.PASSWORD, "");
                            }
                            startActivity(new Intent(mActivity, MainActivity.class));
                            mActivity.finish();
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
                            Toast.makeText(mActivity, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(customRequest);
    }
}
