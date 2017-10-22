package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/21/2017.
 */

public class NotificationModel {
    public int id, id_send;
    public String subject, message, file_ref, created_at;
    public NotificationModel(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            id_send = jsonObject.getInt("id_send");
            subject = jsonObject.getString("subject");
            message = jsonObject.getString("message");
            created_at = jsonObject.getString("created_at");
            if (jsonObject.has("file_ref")) {
                String fileRef = jsonObject.getString("file_ref");
                if (!fileRef.equals("null")) {
                    file_ref = fileRef;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
