package com.ntsoft.ihhq.utility;

import android.content.Context;
import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
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
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 3/3/2016.
 */
public class TimeZoneHelper {
    JsonObjectRequest jsonObjRequest;
    private RequestQueue mVolleyQueue;
    private static String GOOGLE_API_SERVER_KEY = "AIzaSyC6UbsP0vRgJ34ZbsNWJFtLE6H4ebJ8hKo";
    private final String TAG_REQUEST = "MY_TIME_ZONE";
    String latitude, logitude;
    String timestamp;
    Context mContext;
    ArrayList<String> arrOffsets;
    public TimeZoneHelper (Context context , String latitude, String logitude, String timestamp) {

        mContext = context;
        this.latitude = latitude;
        this.logitude = logitude;
        this.timestamp = timestamp;
        // Initialise Volley Request Queue.
        mVolleyQueue = Volley.newRequestQueue(mContext);
        arrOffsets = new ArrayList<>();
    }
    public void getTimeZone() {

        String url = "https://maps.googleapis.com/maps/api/timezone/json";
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("location", latitude + "," + logitude);
        builder.appendQueryParameter("timestamp", timestamp);
        builder.appendQueryParameter("key", GOOGLE_API_SERVER_KEY);

        jsonObjRequest = new JsonObjectRequest(Request.Method.GET, builder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        String rawOffset = response.getString("rawOffset");
//                        String dstOffset = response.getString("dstOffset");
//                        String timeZoneID = response.getString("timeZoneId");
//                        String timeZoneName = response.getString("timeZoneName");

                        Utils.saveToPreference(mContext, "rawOffset", rawOffset);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
                // For AuthFailure, you can re login with user credentials.
                // For ClientError, 400 & 401, Errors happening on client side when sending api request.
                // In this case you can check how client is forming the api and debug accordingly.
                // For ServerError 5xx, you can do retry or handle accordingly.
                if( error instanceof NetworkError) {
                } else if( error instanceof ServerError) {
                } else if( error instanceof AuthFailureError) {
                } else if( error instanceof ParseError) {
                } else if( error instanceof NoConnectionError) {
                } else if( error instanceof TimeoutError) {
                }

            }
        });

        //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions. Volley does retry for you if you have specified the policy.
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
    }

    public void getTimeZone1(final String cityName) {

        String url = "https://maps.googleapis.com/maps/api/timezone/json";
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("location", latitude + "," + logitude);
        builder.appendQueryParameter("timestamp", timestamp);
        builder.appendQueryParameter("key", GOOGLE_API_SERVER_KEY);

        jsonObjRequest = new JsonObjectRequest(Request.Method.GET, builder.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        String rawOffset = response.getString("rawOffset");
                        int hour = Integer.parseInt(rawOffset) / 3600;

//                        String dstOffset = response.getString("dstOffset");
//                        String timeZoneID = response.getString("timeZoneId");
//                        String timeZoneName = response.getString("timeZoneName");
                            arrOffsets.add(cityName + "=" + String.valueOf(hour));
//                        Utils.saveToPreference(mContext, "rawOffset", rawOffset);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
                // For AuthFailure, you can re login with user credentials.
                // For ClientError, 400 & 401, Errors happening on client side when sending api request.
                // In this case you can check how client is forming the api and debug accordingly.
                // For ServerError 5xx, you can do retry or handle accordingly.
                if( error instanceof NetworkError) {
                } else if( error instanceof ServerError) {
                } else if( error instanceof AuthFailureError) {
                } else if( error instanceof ParseError) {
                } else if( error instanceof NoConnectionError) {
                } else if( error instanceof TimeoutError) {
                }

            }
        });

        //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions. Volley does retry for you if you have specified the policy.
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(jsonObjRequest);
    }
}
