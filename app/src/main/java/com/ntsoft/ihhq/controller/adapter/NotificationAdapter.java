package com.ntsoft.ihhq.controller.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.model.NotificationModel;
import com.ntsoft.ihhq.utility.TimeUtility;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 7/26/2017.
 */

public class NotificationAdapter extends BaseAdapter {
    Activity mActivity;
    ArrayList<NotificationModel> arrNotifications;

    public NotificationAdapter(Activity activity, ArrayList<NotificationModel> arrayList) {
        mActivity = activity;
        arrNotifications = arrayList;
    }
    @Override
    public int getCount() {
        return arrNotifications.size();
    }

    @Override
    public Object getItem(int position) {
        return arrNotifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_notification, null);
        }
        TextView tvCase = (TextView)convertView.findViewById(R.id.tv_case);
        TextView tvFileRef = (TextView)convertView.findViewById(R.id.tv_file_ref);
        TextView tvMessage = (TextView)convertView.findViewById(R.id.tv_message);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tv_date);
        NotificationModel notificationModel = arrNotifications.get(position);
        tvCase.setText(notificationModel.subject);
        tvFileRef.setText(notificationModel.file_ref);
        tvMessage.setText(notificationModel.message);
        tvDate.setText(TimeUtility.timeFormatter(notificationModel.created_at));
        return convertView;
    }
}
