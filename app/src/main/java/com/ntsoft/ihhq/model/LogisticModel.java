package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/21/2017.
 */

public class LogisticModel {
    public int dispatch_id, client_id, courier_id, status, created_by;
    public String courier_name, courier_logo, file_ref, receiver, address, qr_code, desc, created_at;

    public LogisticModel(JSONObject object) {
        try {
            JSONObject jsonObject = object.getJSONObject("id");
            dispatch_id = jsonObject.getInt("dispatch_id");
            client_id = jsonObject.getInt("client_id");
            courier_id = jsonObject.getInt("courier_id");
            status = jsonObject.getInt("status");
//            created_by = jsonObject.getInt("created_by");
//            courier_name = jsonObject.getString("courier_name");
//            courier_logo = jsonObject.getString("courier_logo");
            file_ref = jsonObject.getString("file_ref");
            receiver = jsonObject.getString("receiver");
            address = jsonObject.getString("address");
            qr_code = jsonObject.getString("qr_code");
            desc = jsonObject.getString("description");
            created_at = jsonObject.getString("created_at");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
