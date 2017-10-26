package com.ntsoft.ihhq.controller.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.model.MessageModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/31/2017.
 */

public class ChatAdapter extends BaseAdapter {

    ArrayList<MessageModel> arrMessages;
    Activity mActivity;

    public ChatAdapter (Activity activity, ArrayList<MessageModel> arrayList) {
        arrMessages = arrayList;
        mActivity  = activity;
    }

    @Override
    public int getCount() {
        return arrMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return arrMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageModel messageModel = arrMessages.get(position);

        if (messageModel.isIncoming) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_message_incoming, null);
        } else {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_message_outgoing, null);
        }
        TextView tvMessage = (TextView)convertView.findViewById(R.id.tv_message);
        TextView tvTime = (TextView)convertView.findViewById(R.id.tv_time);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tv_date);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.iv_image);

        tvDate.setText(messageModel.date);
        tvTime.setText(messageModel.time);
        if (messageModel.type == 0) {//text
            tvMessage.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            tvMessage.setText(messageModel.message);
        } else {//attachment
            tvMessage.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            if (messageModel.attachmentName.contains(".pdf")) {
                imageView.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.pdf));
            } else if (messageModel.attachmentName.contains(".doc") || messageModel.attachmentName.contains(".word")) {
                imageView.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.doc));
            } else if (messageModel.attachmentName.contains(".xls")){
                imageView.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.excel));
            } else if (messageModel.attachmentName.contains(".png") || messageModel.attachmentName.contains(".jpg") || messageModel.attachmentName.contains(".jpeg")) {
                imageView.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.picture));
            }
        }
        if (position == 0) {
            tvDate.setVisibility(View.VISIBLE);
        } else {
            if (messageModel.date.equals(arrMessages.get(position - 1).date)) {
                tvDate.setVisibility(View.GONE);
            } else {
                tvDate.setVisibility(View.VISIBLE);
            }
        }
        if (messageModel.isIncoming) {
            TextView tvUsername = (TextView) convertView.findViewById(R.id.tv_username);
            tvUsername.setText("@" + messageModel.name);
        } else {

        }
        return convertView;
    }
}
