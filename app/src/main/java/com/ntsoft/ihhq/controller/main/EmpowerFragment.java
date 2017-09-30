package com.ntsoft.ihhq.controller.main;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmpowerFragment extends Fragment {

    Activity mActivity;
    WebView webView;
    public EmpowerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_empower, container, false);
        webView = (WebView)view.findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(API.EMPOWER_URL);
        return view;
    }

}
