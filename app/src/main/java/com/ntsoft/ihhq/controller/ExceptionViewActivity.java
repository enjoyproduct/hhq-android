package com.ntsoft.ihhq.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ntsoft.ihhq.R;

public class ExceptionViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_view);

        TextView textView = (TextView)findViewById(R.id.tv_error);
        String error = getIntent().getStringExtra("error");
        textView.setText(error);
    }
}
