package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/21/2017.
 */

public class CorrespondenceCategoryModel {
    public int category_id;
    public String name;

    public CorrespondenceCategoryModel(JSONObject jsonObject) {
        try {
            category_id = jsonObject.getInt("category_id");
            name = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
