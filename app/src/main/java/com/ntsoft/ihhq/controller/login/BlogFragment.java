package com.ntsoft.ihhq.controller.login;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.utility.UIUtility;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlogFragment extends Fragment implements View.OnClickListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    Activity mActivity;
    LinearLayout llSlider;
    TextView tvDescription;
    Button btnLogin, btnCreateAccount, btnTerms;
    SliderLayout sliderLayout;
    PagerIndicator pagerIndicator;

    ArrayList<String> arrDescriptions = new ArrayList<>();

    public BlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        mActivity = getActivity();
        initUI(view);
        return view;
    }
    void initUI(View view) {
        llSlider = (LinearLayout)view.findViewById(R.id.ll_slider);
        UIUtility.setLinearLayoutSize(llSlider, UIUtility.getScreenWidth(mActivity), UIUtility.getScreenWidth(mActivity));
        tvDescription = (TextView)view.findViewById(R.id.tv_description);
        btnLogin = (Button)view.findViewById(R.id.btn_login);
        btnCreateAccount = (Button)view.findViewById(R.id.btn_create_account);
        btnTerms = (Button)view.findViewById(R.id.btn_terms);
        sliderLayout = (SliderLayout)view.findViewById(R.id.slider);
        pagerIndicator = (PagerIndicator)view.findViewById(R.id.custom_indicator);

        btnLogin.setOnClickListener(this);
        btnCreateAccount.setOnClickListener(this);
        btnTerms.setOnClickListener(this);

        //init slider
        arrDescriptions.add("Legal access redefined.");
        arrDescriptions.add("Your Privacy, Our Priority.");
        arrDescriptions.add("Empowering everybody with Law.");
        arrDescriptions.add("Precedents as a starting point.");
        tvDescription.setText("Legal access redefined.");

        ArrayList<String> arrImageNames = new ArrayList<>();
        arrImageNames.add("blog1");
        arrImageNames.add("blog2");
        arrImageNames.add("blog3");
        arrImageNames.add("blog4");

        ArrayList<Integer> arrImageIds = new ArrayList<>();
        arrImageIds.add(R.drawable.blog1);
        arrImageIds.add(R.drawable.blog2);
        arrImageIds.add(R.drawable.blog3);
        arrImageIds.add(R.drawable.blog4);

        for(int i = 0; i < arrImageNames.size(); i ++){
            String name = arrImageNames.get(i);
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description("")
                    .image(arrImageIds.get(i))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(10000);
        sliderLayout.setCustomIndicator((PagerIndicator) view.findViewById(R.id.custom_indicator));
        sliderLayout.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            ((StartActivity)mActivity).pushFragment(1);
        } else if (v == btnCreateAccount) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(API.REGISTER));
            startActivity(browserIntent);
        } else if (v == btnTerms) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(API.TERMS_AND_POLICY));
            startActivity(browserIntent);
        }
    }
    //slider callbacks
    @Override
    public void onSliderClick(BaseSliderView slider) {
        
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tvDescription.setText(arrDescriptions.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        sliderLayout.stopAutoCycle();
        super.onStop();
    }
}
