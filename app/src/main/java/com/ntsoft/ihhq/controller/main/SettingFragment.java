package com.ntsoft.ihhq.controller.main;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.CustomRequest;
import com.android.volley.toolbox.Volley;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.controller.setting.ChangePasswordActivity;
import com.ntsoft.ihhq.controller.setting.ProfileActivity;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.NotificationModel;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    RelativeLayout rlUpdateProfile, rlTerms, rlNotification, rlChangePassword;
    Switch aSwitchNotificaion;
    Activity mActivity;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mActivity = getActivity();
        initUI(view);
        return view;
    }

    void initUI(View view) {
        rlUpdateProfile = (RelativeLayout)view.findViewById(R.id.rl_update_profile);
        rlUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        rlTerms = (RelativeLayout)view.findViewById(R.id.rl_terms);
        rlTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(API.TERMS_AND_POLICY));
                startActivity(browserIntent);
            }
        });
        rlNotification = (RelativeLayout)view.findViewById(R.id.rl_notification);
        rlNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        aSwitchNotificaion = (Switch)view.findViewById(R.id.switch_notification);
        updateNotificationSwitch();
        aSwitchNotificaion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableNotification(isChecked);
            }
        });
        rlChangePassword = (RelativeLayout)view.findViewById(R.id.rl_change_password);
        rlChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));

            }
        });

    }
    void updateNotificationSwitch() {
        if (Global.getInstance().me.is_enable_push == 1) {
            aSwitchNotificaion.setChecked(true);
        } else {
            aSwitchNotificaion.setChecked(false);
        }
    }
    private void enableNotification(final boolean isEnable) {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("enable", isEnable ? "1" : "0");
        CustomRequest customRequest = new CustomRequest(Request.Method.POST, API.ENABLE_NOTIFICATION, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            Global.getInstance().me.is_enable_push = isEnable ? 1 : 0;
                            updateNotificationSwitch();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(customRequest);
    }
}
