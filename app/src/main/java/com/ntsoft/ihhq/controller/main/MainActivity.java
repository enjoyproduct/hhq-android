package com.ntsoft.ihhq.controller.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
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
import com.ntsoft.ihhq.controller.correspondence.CreateNewCorrespondenceActivity;
import com.ntsoft.ihhq.controller.login.StartActivity;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.UserModel;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton ibPlus;
    public  DrawerLayout mDrawerLayout;
    private  FragmentManager fragmentManager;
    private  MenuFragment mainMenuFragment;
    private  TextView tvTitle;

    private static int currentFragmentNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariables();
        initUI();
        menuNavigateTo(0);
    }
    private void initVariables() {
        fragmentManager = getSupportFragmentManager();
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandleMessageReceiver, new IntentFilter("pushData"));
    }
    public BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

        }
    };
    protected void onResume() {
        resetMenu();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandleMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public  void resetMenu() {
        mainMenuFragment = new MenuFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_menu_container, mainMenuFragment)
                .commit();
    }
    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvTitle = (TextView)toolbar.findViewById(R.id.tv_home_title);
        ibPlus = (ImageButton)toolbar.findViewById(R.id.ib_menu_left);
        ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNewCorrespondenceActivity.class);
                startActivity(intent);
            }
        });
        ImageButton ibMenu = (ImageButton)toolbar.findViewById(R.id.ib_menu);
        ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);

    }
    public void setTitle(String title) {
        tvTitle.setText(title);
    }
    public void menuNavigateTo(int num) {
        currentFragmentNum = num;
        showHidePlusButton(false);
        switch (num) {
            case 0:
                setTitle("HHQ TOUCH");
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                break;
            case 1:
                setTitle("NOTIFICATIONS");
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new NotificationFragment())
                        .commit();
                break;
            case 2:
                setTitle("EMPOWER");
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new EmpowerFragment())
                        .commit();
                break;
            case 3:
                setTitle("CORRESPONDENCE");
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new CorrespondenceContainerFragment())
                        .commit();
                break;
            case 4:
                setTitle("SCAN QR CODE");
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new ScanQRCodeFragment())
                        .commit();
                break;
            case 5:
                setTitle("SETTINGS");
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new SettingFragment())
                        .commit();
                break;
            case 6:
                showLogoutDialog();
                break;

        }
        mDrawerLayout.closeDrawers();
    }

    public void showHidePlusButton(boolean isShow) {
        if (isShow) {
            ibPlus.setVisibility(View.VISIBLE);
            ibPlus.setClickable(true);
        } else {
            ibPlus.setVisibility(View.INVISIBLE);
            ibPlus.setClickable(false);
        }
    }

    private void showLogoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Constant.INDECATOR);
        builder.setMessage("Confirm logout?");
        builder.setCancelable(true);
        builder.setPositiveButton( "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doLogout();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    void doLogout() {
        Utils.showProgress(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.DEVICE_TOKEN, Utils.getFromPreference(this, Constant.DEVICE_TOKEN));
        params.put(Constant.DEVICE_TYPE, Constant.ANDROID);
        params.put("email", Global.getInstance().getMe().email);
        params.put("password", Global.getInstance().getMe().password);
        CustomRequest customRequest = new CustomRequest(Request.Method.POST, API.LOGOUT, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        Utils.saveToPreference(MainActivity.this, Constant.EMAIL, "");
                        Utils.saveToPreference(MainActivity.this, Constant.PASSWORD, "");
                        Global.getInstance().me = new UserModel();
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(MainActivity.this, "Network Timeout Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(MainActivity.this, "Auth Failure Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(MainActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(MainActivity.this, "Unknown Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(customRequest);
    }
}
