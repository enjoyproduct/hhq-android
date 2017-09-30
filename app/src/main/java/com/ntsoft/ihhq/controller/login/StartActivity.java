package com.ntsoft.ihhq.controller.login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.push.GetNotificationRegID;
import com.ntsoft.ihhq.utility.ExceptionHandler;
import com.ntsoft.ihhq.utility.FileUtility;
import com.ntsoft.ihhq.utility.Utils;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    public  FragmentManager fragmentManager;
    private  int currentPageNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ///set exception handler
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        //
        FileUtility.createDirectory(Constant.MEDIA_PATH);

        if (Utils.getFromPreference(this, Constant.DEVICE_TOKEN).length() == 0) {
            GetNotificationRegID getNotificationRegID = new GetNotificationRegID(this);
            getNotificationRegID.registerInBackground();
        }
        fragmentManager = getSupportFragmentManager();
        String type = getIntent().getStringExtra("type");
        if (type != null) {
            pushFragment(1);
        } else {
            pushFragment(0);
        }
        checkPermission();
    }
    private final static int PERMISSION_REQUEST_CODE_FOR_PERMISSION = 201;
    public  void checkPermission() {
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraAccessPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        ArrayList<String> arrPermissionRequests = new ArrayList<>();
        if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            arrPermissionRequests.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            arrPermissionRequests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraAccessPermission != PackageManager.PERMISSION_GRANTED) {
            arrPermissionRequests.add(Manifest.permission.CAMERA);
        }
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            arrPermissionRequests.add(Manifest.permission.CALL_PHONE);
        }
        if (!arrPermissionRequests.isEmpty()) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//            }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAPTURE_VIDEO_OUTPUT)) {
//            } else {
//            }
            ActivityCompat.requestPermissions(this, arrPermissionRequests.toArray(new String[arrPermissionRequests.size()]), PERMISSION_REQUEST_CODE_FOR_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_FOR_PERMISSION: {

                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i ++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            // permission was granted, yay! Do the
                            // contacts-related task you need to do.

                        } else {

                            // permission denied, boo! Disable the
                            // functionality that depends on this permission.
                            Utils.showOKDialog(this, "You should allow this permission to use full functions of this app.");
                        }
                    }
                }
                // If request is cancelled, the result arrays are empty.

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public  void pushFragment(int pageNum) {
        //set animation
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        switch (pageNum) {
            case 0:
                transaction
                        .replace(R.id.fragment_container, new BlogFragment())
                        .addToBackStack("BlogFragment")
                        .commit();
                currentPageNum = 0;
                break;
            case 1:
                transaction
                        .replace(R.id.fragment_container, new LoginFragment())
                        .addToBackStack("LoginFragment")
                        .commit();
                currentPageNum = 1;
                break;
            case 2:
                transaction
                        .replace(R.id.fragment_container, new ForgotPassword())
                        .addToBackStack("ForgotPassword")
                        .commit();
                currentPageNum = 2;
                break;
        }
    }
    public void popFragment() {

        int fragmentCount = fragmentManager.getBackStackEntryCount();
        if (fragmentCount > 1) {
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
            transaction
                    .remove(fragmentManager.getFragments().get(fragmentCount - 1))
                    .commit();
            fragmentManager.popBackStack();
        } else {
            StartActivity.this.finish();
        }

    }

    @Override
    public void onBackPressed() {
        popFragment();
    }
}
