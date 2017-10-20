package com.ntsoft.ihhq.controller.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.home.HomePaymentFragment;
import com.ntsoft.ihhq.model.CorrespondenceModel;
import com.ntsoft.ihhq.model.PaymentModel;
import com.ntsoft.ihhq.utility.FileDownloadCompleteListener;
import com.ntsoft.ihhq.utility.FileDownloader;
import com.ntsoft.ihhq.utility.FileUtility;
import com.ntsoft.ihhq.utility.StringUtility;
import com.ntsoft.ihhq.utility.TimeUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 7/25/2017.
 */

public class HomePaymentAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<PaymentModel> arrPayments;
    public HomePaymentAdapter(Activity activity, ArrayList<PaymentModel> arrFiles) {
        this.mActivity = activity;
        this.arrPayments = arrFiles;
    }
    @Override
    public int getCount() {
        return arrPayments.size();
    }

    @Override
    public Object getItem(int position) {
        return arrPayments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.item_home_payment, null);
        }
        TextView tvPurpose = (TextView)view.findViewById(R.id.tv_purpose);
        TextView tvAmount = (TextView)view.findViewById(R.id.tv_amount);
        TextView tvDateIssued = (TextView)view.findViewById(R.id.tv_date_issued);
        TextView tvRemark = (TextView)view.findViewById(R.id.tv_remark);
        TextView tvStatus = (TextView)view.findViewById(R.id.tv_status);
        TextView tvViewInvoice = (TextView)view.findViewById(R.id.tv_view_invoice);

        PaymentModel paymentModel = arrPayments.get(position);
        tvPurpose.setText(paymentModel.purpose);
        tvAmount.setText(paymentModel.currency + paymentModel.amount);
        tvDateIssued.setText(TimeUtility.timeFormatter(paymentModel.created_at));
        tvStatus.setText(paymentModel.status);
        tvRemark.setText(paymentModel.remarks);
        if (paymentModel.status.equals(Constant.arrPaymentStatus[0])) {
            tvViewInvoice.setVisibility(View.VISIBLE);
            tvViewInvoice.setText("View Invoice");
        } else if (paymentModel.status.equals(Constant.arrPaymentStatus[2])) {
            tvViewInvoice.setVisibility(View.VISIBLE);
            tvViewInvoice.setText("View Slip");
        } else {
            tvViewInvoice.setVisibility(View.VISIBLE);
            tvViewInvoice.setText("View Receipt");
        }
        tvViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(position);
            }
        });
        if (paymentModel.invoiceFilePath.isEmpty()) {
            tvViewInvoice.setVisibility(View.GONE);
        }
        return view;
    }
    void downloadFile(int position) {
        PaymentModel payment = arrPayments.get(position);
        String url = "";
        String fileName = "";
        if (payment.invoiceFilePath.isEmpty()) {
            fileName = payment.file_ref + "--" + String.valueOf(arrPayments.get(position).payment_id) + ".pdf"; //assumed file is pdf
        } else {
            fileName = FileUtility.getFilenameFromPath(payment.invoiceFilePath);
        }
        if (arrPayments.get(position).status.equals(Constant.arrPaymentStatus[0])) {
            url = String.format(API.DOWNLOAD_INVOICE, arrPayments.get(position).payment_id);
        } else if (arrPayments.get(position).status.equals(Constant.arrPaymentStatus[2])) {
            url = String.format(API.DOWNLOAD_RECEIPT, arrPayments.get(position).payment_id);
        } else {
            url = String.format(API.DOWNLOAD_RECEIPT, arrPayments.get(position).payment_id);
        }
        final String finalFileName = fileName;
        FileDownloader.downloadFile(mActivity, url,fileName, new FileDownloadCompleteListener() {
            @Override
            public void onComplete(String filePath) {
//                Toast.makeText(mActivity, filePath, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File(filePath);
                if (finalFileName.contains(".pdf")) {
                    intent.setDataAndType( Uri.fromFile( file ), "application/pdf" );
                } else if (finalFileName.contains(".doc") || finalFileName.contains(".word")) {
                    intent.setDataAndType(Uri.fromFile(file), "application/msword");
                } else if (finalFileName.contains(".png")) {
                    intent.setDataAndType(Uri.fromFile(file), "image/png");
                } else if (finalFileName.contains(".jpeg") || finalFileName.contains(".jpg")) {
                    intent.setDataAndType(Uri.fromFile(file), "image/jpg");
                } else {
                    intent.setDataAndType( Uri.fromFile( file ), "application/vnd.ms-excel" );
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (file.exists()) {
                    mActivity.startActivity(intent);
                } else {
                    return;
                }
            }
        });
    }
}
