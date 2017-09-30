package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/21/2017.
 */

public class ContactModel {
    public boolean isExpanded;
    public int id, country_id;
    public String email, mobile, name, role;

    public ContactModel(JSONObject jsonObject) {
        isExpanded = false;
        try {
            id = jsonObject.getInt("id");
            country_id = jsonObject.getInt("country_id");
            email = jsonObject.getString("email");
            mobile = jsonObject.getString("mobile");
            name = jsonObject.getString("name");
            role = jsonObject.getString("role");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
