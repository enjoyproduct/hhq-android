package com.ntsoft.ihhq.model;

import com.ntsoft.ihhq.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 7/21/2017.
 */

public class FileModel implements Serializable{
    public class CaseModel implements Serializable{
        int no = 0;
        boolean select = false;
        String status = "";
        String activity = "";
        int duration = 0;
        float milestone = 0;
    }
    public int file_id, department_id, category_id, sub_category_id, type_id, status, created_by, updated_by
            , closed_by;
    public String file_ref, project_name, subject_matter, subject_description, contact_name, contact
            , contact_email, introducer, percent, billplz_collection_id, currency
            , outstanding_address, mailing_address;
    public String assigned_role;
    public List<String> tags = new ArrayList<>();
    public  CaseModel cases = new CaseModel();

    public FileModel(JSONObject jsonObject) {
        try {
            file_id = jsonObject.getInt("file_id");
            department_id = jsonObject.getInt("department_id");
            category_id = jsonObject.getInt("category_id");
            sub_category_id = jsonObject.getInt("sub_category_id");
            status = jsonObject.getInt("status");
            created_by = jsonObject.getInt("created_by");
            updated_by = jsonObject.getInt("updated_by");
            closed_by = jsonObject.getInt("closed_by");
            if (jsonObject.has("role")) {
                assigned_role = jsonObject.getString("role");
            } else {
                assigned_role = Constant.arrUserRoles[0];
            }


            file_ref = jsonObject.getString("file_ref");
            project_name = jsonObject.getString("project_name");
            subject_matter = jsonObject.getString("subject_matter");
            subject_description = jsonObject.getString("subject_description");
            contact_name = jsonObject.getString("contact_name");
            contact = jsonObject.getString("contact");
            contact_email = jsonObject.getString("contact_email");
            introducer = jsonObject.getString("introducer");
            percent = jsonObject.getString("percent");
            billplz_collection_id = jsonObject.getString("billplz_collection_id");
            currency = jsonObject.getString("currency");
            outstanding_address = jsonObject.getString("outstanding_address");
            mailing_address = jsonObject.getString("mailing_address");
            String tagString = jsonObject.getString("tags");
            String[] tagArray = tagString.split(",");
            tags = Arrays.asList(tagArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
