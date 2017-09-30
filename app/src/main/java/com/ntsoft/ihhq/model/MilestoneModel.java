package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 8/3/2017.
 */

public class MilestoneModel {
    public  int id;
    public String status, activity, verification, duration, date, remark;
    public String percent;

    public MilestoneModel(JSONObject object) {
        try {
            date = object.getString("key");
            percent = object.getString("percent");
            JSONObject jsonObject = object.getJSONObject("id");
            id = jsonObject.getInt("no");
            status = jsonObject.getString("status");
            activity = jsonObject.getString("activity");
            verification = jsonObject.getString("verification");
            duration = jsonObject.getString("duration");
            if (jsonObject.has("remark")) {
                remark = jsonObject.getString("remark");
            } else {
                remark = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
