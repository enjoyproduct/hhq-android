package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 7/21/2017.
 */

public class CorrespondenceModel implements Serializable{
    public int ticket_id, client_id, stuff_id, category_id, status_id;
    public String file_ref, subject, category, created_at, client_name = "", client_photo = "";

    public CorrespondenceModel(JSONObject jsonObject) {
        try {
            ticket_id = jsonObject.getInt("ticket_id");
            file_ref = jsonObject.getString("file_ref");
            subject = jsonObject.getString("subject");
            category = jsonObject.getString("category");
            created_at = jsonObject.getString("created_at");
            if (jsonObject.getJSONObject("client") != null) {
                JSONObject client = jsonObject.getJSONObject("client");
                client_name = client.getString("name");
                client_photo = client.getString("photo");
            } else {
                client_photo = "";
                client_name = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
