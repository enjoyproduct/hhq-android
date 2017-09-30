package com.ntsoft.ihhq.model;

import com.google.android.gms.nearby.messages.internal.Update;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 8/3/2017.
 */

public class UpdateModel {
    public MilestoneModel milestoneModel;
    public LogisticModel logisticModel;
    public String type;
    public int isSeperator = 0;
    public String date;
    public UpdateModel (JSONObject jsonObject) {
        try {

            type = jsonObject.getString("type");
            date = jsonObject.getString("key");
            if (type.equals("milestone")) {
                milestoneModel = new MilestoneModel(jsonObject);
            } else {
                logisticModel = new LogisticModel(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
