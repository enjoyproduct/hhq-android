package com.ntsoft.ihhq.controller.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.qrscan.ScanQRCodeActivity;
import com.ntsoft.ihhq.model.CorrespondenceModel;
import com.ntsoft.ihhq.model.LogisticModel;
import com.ntsoft.ihhq.model.UpdateModel;
import com.ntsoft.ihhq.utility.Utils;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Administrator on 7/25/2017.
 */

public class HomeUpdateAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<UpdateModel> arrUpdates;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public HomeUpdateAdapter(Activity activity) {
        this.mActivity = activity;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arrUpdates = new ArrayList<>();
    }
    public void addItem(UpdateModel item) {
        arrUpdates.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(UpdateModel item) {
        arrUpdates.add(item);
        sectionHeader.add(arrUpdates.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return arrUpdates.size();
    }

    @Override
    public UpdateModel getItem(int position) {
        return arrUpdates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);
        final UpdateModel updateModel = arrUpdates.get(position);
        holder = new ViewHolder();
        switch (rowType) {
            case TYPE_ITEM:
                if (updateModel.type.equals("milestone")) {
                    convertView = mInflater.inflate(R.layout.item_home_update_milestone, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tvStatusValue = (TextView) convertView.findViewById(R.id.tv_status_value);
                    holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
                    //set data
                    holder.textView.setText(updateModel.milestoneModel.activity);
                    holder.tvStatusValue.setText(updateModel.milestoneModel.status);
                    if (updateModel.milestoneModel.status.equals("In Progress")) {
                        holder.tvStatusValue.setTextColor(mActivity.getResources().getColor(R.color.red));
                    } else {
                        holder.tvStatusValue.setTextColor(mActivity.getResources().getColor(R.color.green));
                    }
                    holder.ll_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           showDialog(mActivity, updateModel.milestoneModel.activity, updateModel.milestoneModel.remark);
                        }
                    });
                } else {
                    convertView = mInflater.inflate(R.layout.item_home_update_logistic, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);
                    holder.tvDescription1 = (TextView) convertView.findViewById(R.id.tv_description1);
                    holder.tvStatus1 = (TextView) convertView.findViewById(R.id.tv_status1);
                    holder.tvReceiver = (TextView) convertView.findViewById(R.id.tv_receiver);
                    holder.tvDescription= (TextView) convertView.findViewById(R.id.tv_description);
                    holder.tvMethod = (TextView) convertView.findViewById(R.id.tv_method);
                    holder.tvStatus2 = (TextView) convertView.findViewById(R.id.tv_status2);
                    holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
                    holder.llStatus = (LinearLayout) convertView.findViewById(R.id.ll_status);
                    holder.ibScan = (ImageButton)convertView.findViewById(R.id.ib_scan);

                    holder.textView.setText(updateModel.logisticModel.file_ref);
                    holder.tvDescription1.setText(updateModel.logisticModel.desc);
                    holder.tvStatus1.setText(Constant.arrLogisticStatus[updateModel.logisticModel.status]);
                    holder.tvReceiver.setText(updateModel.logisticModel.receiver);
                    holder.tvDescription.setText(updateModel.logisticModel.desc);
                    holder.tvMethod.setText(updateModel.logisticModel.address);
                    holder.tvStatus2.setText(Constant.arrLogisticStatus[updateModel.logisticModel.status]);
                    if (updateModel.logisticModel.status == 1) {
                        holder.llStatus.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.round_corner_bg_yellow));
                    } else {
                        holder.llStatus.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.round_corner_bg_skyblue));
                    }
                    holder.ll_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog(mActivity, updateModel.logisticModel.file_ref , updateModel.logisticModel.desc);
                        }
                    });
                    holder.ibScan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, ScanQRCodeActivity.class);
                            intent.putExtra("file_ref", updateModel.logisticModel.file_ref);
                            intent.putExtra("index", position);
                            mActivity.startActivityForResult(intent, 1111);
                        }
                    });
                }
                break;
            case TYPE_SEPARATOR:
                convertView = mInflater.inflate(R.layout.item_header, null);
                holder.textView = (TextView) convertView.findViewById(R.id.text);
                holder.textView.setText(updateModel.date);
                break;
        }
        convertView.setTag(holder);

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
        public TextView tvPercent, tvStatus, tvStatusValue;
        public ProgressBar progressBar;

        public TextView tvReceiver, tvDescription, tvDescription1, tvMethod, tvStatus1, tvStatus2;
        public LinearLayout ll_container, llStatus;
        public ImageButton ibScan;
    }
    void showDialog(Context context, String title,  String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView tvTitle = new TextView(context);
        tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(context.getResources().getColor(R.color.solid_black));
        tvTitle.setPadding(16,16,16,16);
        tvTitle.setText(title);
        builder.setCustomTitle(tvTitle);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton( "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
