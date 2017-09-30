package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/21/2017.
 */

public class UserModel {
    public int id, country_id, department_id, office_id,
        verified, is_allow, is_review, is_enable_push, is_enable_email;
    public String name, company_number, passport_no, email, mobile, photo, address,
        token, role, password;
    public UserModel(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            country_id = jsonObject.getInt("country_id");
            department_id = jsonObject.getInt("department_id");
            office_id = jsonObject.getInt("office_id");
            verified = jsonObject.getInt("verified");
            is_allow = jsonObject.getInt("is_allow");
            is_review = jsonObject.getInt("is_review");
            is_enable_push = jsonObject.getInt("is_enable_push");
            is_enable_email = jsonObject.getInt("is_enable_email");

            name = jsonObject.getString("name");
            company_number = jsonObject.getString("company_number");
            passport_no = jsonObject.getString("passport_no");
            email = jsonObject.getString("email");
            mobile = jsonObject.getString("mobile");
            photo = jsonObject.getString("photo");
            address = jsonObject.getString("address");
            token = jsonObject.getString("token");
            role = jsonObject.getString("role");
            password = jsonObject.getString("password");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public UserModel() {

    }
}
