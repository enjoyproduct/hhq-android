package com.ntsoft.ihhq.controller.setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText etCurrentPass, etNewPass, etConfirmPass;
    Button btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ImageButton ibBack = (ImageButton)toolbar.findViewById(R.id.ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView)toolbar.findViewById(R.id.tv_title);
        tvTitle.setText("");
        etCurrentPass = (EditText)findViewById(R.id.et_current_password);
        etNewPass = (EditText)findViewById(R.id.et_new_password);
        etConfirmPass = (EditText)findViewById(R.id.et_confirm_password);
        btnUpdate = (Button)findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etCurrentPass.getText().toString().isEmpty()
                        && !etNewPass.getText().toString().isEmpty()
                        && etNewPass.getText().toString().equals(etConfirmPass.getText().toString())
                        ) {
                    if (etNewPass.getText().toString().length() > 5) {
                        updatePassword();
                    } else {
                        Utils.showOKDialog(ChangePasswordActivity.this, "Password length should be 5 at least");
                    }
                } else {
                    Utils.showOKDialog(ChangePasswordActivity.this, "Please fill all field");
                }
            }
        });
    }
    void updatePassword() {
        if (!Utils.haveNetworkConnection(this)) {
            Utils.showToast(this, "No internet connection");
            return;
        }

        Utils.showProgress(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("current_password", etCurrentPass.getText().toString());
        params.put("password", etNewPass.getText().toString());
        params.put("password_confirmation", etConfirmPass.getText().toString());
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomRequest customRequest = new CustomRequest(Request.Method.POST, API.CHANGE_PASSWORD, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        requestQueue.getCache().remove(API.CHANGE_PASSWORD);
                        Utils.saveToPreference(ChangePasswordActivity.this, Constant.PASSWORD, etNewPass.getText().toString());
                        Utils.showToast(ChangePasswordActivity.this, "Password updated successfully");
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(ChangePasswordActivity.this, "Network Timeout Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(ChangePasswordActivity.this, "Auth Failure Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(ChangePasswordActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(ChangePasswordActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(ChangePasswordActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(ChangePasswordActivity.this, "Unknown Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
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
