package com.ntsoft.ihhq.model;

import com.ntsoft.ihhq.utility.TimeUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

/**
 * Created by Administrator on 7/21/2017.
 */

public class MessageModel {
    //type = 0: text, 1: attachment
    public int message_id, type, ticket_id, sender_id, client_id, status;
    public String message = "", attachmentName = "", attachmentPath = "", date = "", time = "";
    public String name = "", photo = "";
    public boolean isIncoming = false;
    public MessageModel(JSONObject jsonObject) {
        try {
            message_id = jsonObject.getInt("message_id");
            type = jsonObject.getInt("type");
            ticket_id = jsonObject.getInt("ticket_id");
            sender_id = jsonObject.getInt("sender_id");
            client_id = jsonObject.getInt("client_id");
            status = jsonObject.getInt("status");
            String str = jsonObject.getString("message");
            JSONObject msgObj = new JSONObject(str);
            message = msgObj.getString("text");
            photo = jsonObject.getString("photo");
            name = jsonObject.getString("name");
            String created_at = jsonObject.getString("created_at");
            time = TimeUtility.timeFormatter(created_at, "HH:mm");
            date = TimeUtility.timeFormatter("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public MessageModel() {

    }
}
