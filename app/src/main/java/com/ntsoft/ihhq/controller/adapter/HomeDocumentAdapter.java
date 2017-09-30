package com.ntsoft.ihhq.controller.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.model.DocumentModel;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.utility.StringUtility;
import com.ntsoft.ihhq.utility.TimeUtility;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/25/2017.
 */

public class HomeDocumentAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<DocumentModel> arrDocuments;
    public HomeDocumentAdapter(Activity activity, ArrayList<DocumentModel> arrFiles) {
        this.mActivity = activity;
        this.arrDocuments = arrFiles;
    }
    @Override
    public int getCount() {
        return arrDocuments.size();
    }

    @Override
    public Object getItem(int position) {
        return arrDocuments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.item_home_document, null);
        }
        TextView tvName = (TextView)view.findViewById(R.id.tv_name);
        TextView tvCreatedBy = (TextView)view.findViewById(R.id.tv_department);
        TextView tvDate = (TextView)view.findViewById(R.id.tv_date);
        TextView tvSize = (TextView)view.findViewById(R.id.tv_size);
        ImageView ivType = (ImageView) view.findViewById(R.id.iv_type);

        DocumentModel documentModel = arrDocuments.get(position);
        tvName.setText(documentModel.name);
        tvCreatedBy.setText("by " + documentModel.created_by);
        tvDate.setText(TimeUtility.timeFormatter(documentModel.created_at));
        if (documentModel.file_extension.equals("pdf")) {
            ivType.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.pdf));
        } else if (documentModel.file_extension.equals("word") || documentModel.file_extension.equals("doc") || documentModel.file_extension.equals("docx")) {
            ivType.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.doc));
        } else {
            ivType.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.excel));
        }
        String size = StringUtility.getFileSize(documentModel.file_size);
        tvSize.setText(size);
        return view;
    }
}
