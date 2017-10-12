package com.ntsoft.ihhq.controller.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.widget.MyCircularImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/24/2017.
 */

public class HomeAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<FileModel> arrFiles;
    public HomeAdapter(Activity activity, ArrayList<FileModel> arrFiles) {
        this.mActivity = activity;
        this.arrFiles = arrFiles;
    }
    @Override
    public int getCount() {
        return arrFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return arrFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.item_home, null);
        }
        TextView fileName = (TextView)view.findViewById(R.id.tv_file_name);
        TextView fileRef = (TextView)view.findViewById(R.id.tv_file_ref);
        TextView status = (TextView)view.findViewById(R.id.tv_status);
        TextView percent = (TextView)view.findViewById(R.id.tv_percent);
        TextView statusValue = (TextView)view.findViewById(R.id.tv_status_value);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        FileModel fileModel = arrFiles.get(position);

        fileName.setText(fileModel.project_name);
        fileRef.setText(fileModel.file_ref);
        int progress = Integer.parseInt(fileModel.percent);
        progressBar.setProgress(progress);
        if (progress <= 25) {
            progressBar.setProgressTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.red)));
        } else if (progress <= 50) {
            progressBar.setProgressTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.yellow)));
        } else if (progress <= 75) {
            progressBar.setProgressTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.lightGreen)));
        } else {
            progressBar.setProgressTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.green)));
        }
        percent.setText(fileModel.percent + "%");
        if (fileModel.status == 1) {
            statusValue.setText("Completed");
        } else {
            statusValue.setText("In progress");
        }
        return view;
    }
}