package com.ntsoft.ihhq.controller.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.model.ContactModel;
import com.ntsoft.ihhq.model.CorrespondenceModel;
import com.ntsoft.ihhq.utility.SocialUtility;
import com.ntsoft.ihhq.utility.StringUtility;
import com.ntsoft.ihhq.utility.Utils;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/25/2017.
 */

public class HomeContactAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<ContactModel> arrContacts;

    public HomeContactAdapter(Activity activity, ArrayList<ContactModel> arrFiles) {
        this.mActivity = activity;
        this.arrContacts = arrFiles;
    }

    @Override
    public int getCount() {
        return arrContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return arrContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.item_home_contact, null);
        }
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        final TextView tvTelephone = (TextView) view.findViewById(R.id.tv_phone);
        TextView tvDepartment = (TextView) view.findViewById(R.id.tv_department);
        TextView tvEmail = (TextView) view.findViewById(R.id.tv_email);
        Button btnCall = (Button) view.findViewById(R.id.btn_call);

        LinearLayout llContainer = (LinearLayout) view.findViewById(R.id.ll_container);
        final ContactModel contactModel = arrContacts.get(position);
        if (contactModel.isExpanded) {
            llContainer.setVisibility(View.VISIBLE);
        } else {
            llContainer.setVisibility(View.GONE);
        }

        tvName.setText(contactModel.name);
        tvEmail.setText(contactModel.email);
        tvTelephone.setText("+60 " + contactModel.mobile);
        tvDepartment.setText(StringUtility.capitalize(contactModel.role));

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "+60" + contactModel.mobile;
                SocialUtility.call(mActivity, phoneNumber);
            }
        });
        Button btnEmail = (Button)view.findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialUtility.sendEmail(mActivity, new String[]{contactModel.email}, Constant.INDECATOR);
            }
        });
        return view;
    }
}
