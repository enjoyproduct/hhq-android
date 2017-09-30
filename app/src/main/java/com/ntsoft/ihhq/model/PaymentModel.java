package com.ntsoft.ihhq.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 7/21/2017.
 */

public class PaymentModel {
    public int payment_id, transaction_id;
    public String file_ref, purpose, amount, currency, remarks, status, date;

    public PaymentModel(JSONObject jsonObject) {
        try {
            payment_id = jsonObject.getInt("payment_id");
            transaction_id = jsonObject.getInt("transaction_id");
            file_ref = jsonObject.getString("file_ref");
            purpose = jsonObject.getString("purpose");
            amount = jsonObject.getString("amount");
            currency = jsonObject.getString("currency");
            remarks = jsonObject.getString("remarks");
            if (jsonObject.getString("remarks") == "null") {
                remarks = "";
            }
            status = jsonObject.getString("status");
            date = jsonObject.getString("created_at");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
