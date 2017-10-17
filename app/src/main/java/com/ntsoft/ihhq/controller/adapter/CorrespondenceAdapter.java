package com.ntsoft.ihhq.controller.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.model.CorrespondenceModel;
import com.ntsoft.ihhq.utility.TimeUtility;
import com.ntsoft.ihhq.utility.image_downloader.UrlImageViewCallback;
import com.ntsoft.ihhq.utility.image_downloader.UrlImageViewHelper;
import com.ntsoft.ihhq.widget.MyCircularImageView;

import org.apache.http.params.CoreProtocolPNames;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/25/2017.
 */

public class CorrespondenceAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<CorrespondenceModel> arrCorrespondences;
    public CorrespondenceAdapter(Activity activity, ArrayList<CorrespondenceModel> arrFiles) {
        this.mActivity = activity;
        this.arrCorrespondences = arrFiles;
    }
    @Override
    public int getCount() {
        return arrCorrespondences.size();
    }

    @Override
    public Object getItem(int position) {
        return arrCorrespondences.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.item_correspondence, null);
        }
        MyCircularImageView myCircularImageView = (MyCircularImageView)view.findViewById(R.id.civ_avatar);
        TextView tvName = (TextView)view.findViewById(R.id.tv_name);
        TextView tvFileRef = (TextView)view.findViewById(R.id.tv_file_ref);
        TextView tvSubject = (TextView)view.findViewById(R.id.tv_subject);
        TextView tvTime = (TextView)view.findViewById(R.id.tv_date);
        CorrespondenceModel correspondenceModel = arrCorrespondences.get(position);
        tvName.setText(correspondenceModel.client_name + " (" + correspondenceModel.category + ")");
        tvSubject.setText(correspondenceModel.subject);
        tvTime.setText(TimeUtility.timeFormatter(correspondenceModel.created_at));

        if (!correspondenceModel.client_photo.isEmpty()) {
            String imageURL = API.BASE_IMAGE_URL + correspondenceModel.client_photo;
            UrlImageViewHelper.setUrlDrawable(myCircularImageView, imageURL, R.drawable.default_user, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    if (!loadedFromCache) {
                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                        scale.setDuration(100);
                        scale.setInterpolator(new OvershootInterpolator());
                        imageView.startAnimation(scale);
                    }
                }
            });
        } else {
            myCircularImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.default_user));
        }
        if (!correspondenceModel.file_ref.isEmpty() && !correspondenceModel.file_ref.equals("0")) {
            tvFileRef.setVisibility(View.VISIBLE);
            tvFileRef.setText("Ref No: " + correspondenceModel.file_ref);
        } else {
            tvFileRef.setVisibility(View.GONE);
        }
        return view;
    }
}
