package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/21/2017.
 */

public class DocumentModel {
    public int document_id;
    public String file_ref, name, path, file_extension, created_by, created_at;
    public int file_size;

    public DocumentModel(JSONObject jsonObject) {
        try {
            document_id = jsonObject.getInt("document_id");
            file_ref = jsonObject.getString("file_ref");
            name = jsonObject.getString("name");
            path = jsonObject.getString("path");
            file_extension = jsonObject.getString("extension");
            file_size = jsonObject.getInt("file_size");
            created_at = jsonObject.getString("created_at");
            JSONObject createrObject = jsonObject.getJSONObject("created_by");
            created_by = createrObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
