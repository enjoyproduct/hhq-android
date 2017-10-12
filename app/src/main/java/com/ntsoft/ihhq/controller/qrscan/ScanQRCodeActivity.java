package com.ntsoft.ihhq.controller.qrscan;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.controller.login.BlogFragment;
import com.ntsoft.ihhq.controller.login.ForgotPassword;
import com.ntsoft.ihhq.controller.login.LoginFragment;
import com.ntsoft.ihhq.controller.main.ScanQRCodeFragment;

public class ScanQRCodeActivity extends AppCompatActivity {
    public FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        fragmentManager = getSupportFragmentManager();

        String file_ref = getIntent().getStringExtra("file_ref");
        int index = getIntent().getIntExtra("index", -1);

        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ScanQRCodeFragment fragment = new ScanQRCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putString("file_ref", file_ref);
        fragment.setArguments(bundle);
        transaction
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
