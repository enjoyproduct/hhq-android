package com.ntsoft.ihhq.controller.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.CustomMultipartRequest;
import com.android.volley.toolbox.Volley;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.utility.BitmapUtility;
import com.ntsoft.ihhq.utility.FileUtility;
import com.ntsoft.ihhq.utility.Utils;
import com.ntsoft.ihhq.utility.camera.AlbumStorageDirFactory;
import com.ntsoft.ihhq.utility.camera.BaseAlbumDirFactory;
import com.ntsoft.ihhq.utility.camera.FroyoAlbumDirFactory;
import com.ntsoft.ihhq.utility.image_downloader.UrlImageViewCallback;
import com.ntsoft.ihhq.utility.image_downloader.UrlImageViewHelper;
import com.ntsoft.ihhq.widget.MyCircularImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    MyCircularImageView myCircularImageView;
    ImageButton ibChange, ibDelete;
    TextView tvName, tvEmail, tvCountry, tvPassport, tvMobileNumber;
    EditText etAddress;

    String avatarPath;
    private static final int from_gallery = 1;
    private static final int from_camera = 2;
    private static final String JPEG_FILE_PREFIX = "iHHQ_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initVariables();
        initUI();
    }
    private void initVariables() {

        avatarPath = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    void initUI() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ImageButton imageButton = (ImageButton)toolbar.findViewById(R.id.ib_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView)toolbar.findViewById(R.id.tv_title);
        tvTitle.setText("PROFILE");
        Button btnDone = (Button)toolbar.findViewById(R.id.btn_done);
        btnDone.setVisibility(View.VISIBLE);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        ImageButton ibAdd = (ImageButton)toolbar.findViewById(R.id.ib_add);
        ibAdd.setVisibility(View.GONE);

        tvName = (TextView)findViewById(R.id.tv_name);
        tvEmail = (TextView)findViewById(R.id.tv_email);
        tvCountry = (TextView)findViewById(R.id.tv_country);
        tvPassport = (TextView)findViewById(R.id.tv_passport);
        tvMobileNumber = (TextView)findViewById(R.id.tv_mobile_number);

        myCircularImageView = (MyCircularImageView)findViewById(R.id.civ_avatar);
        if (!Global.getInstance().me.photo.isEmpty()) {
            String imageURL = API.BASE_IMAGE_URL + Global.getInstance().me.photo;
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
            myCircularImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.default_user));
        }
        etAddress = (EditText)findViewById(R.id.et_address);

        ibChange = (ImageButton)findViewById(R.id.ib_change);
        ibChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDialog(ProfileActivity.this, "Please pick a photo from");
            }
        });
        ibDelete = (ImageButton)findViewById(R.id.ib_delete);
        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarPath = "";
                myCircularImageView.setImageDrawable(getResources().getDrawable(R.drawable.default_user));
            }
        });

        tvName.setText(Global.getInstance().me.name);
        tvEmail.setText(Global.getInstance().me.email);
//        tvCountry.setText(Global.getInstance().me.country_id);
        tvPassport.setText(Global.getInstance().me.passport_no);
        tvMobileNumber.setText(Global.getInstance().me.mobile);
        etAddress.setText(Global.getInstance().me.address);
    }

    private void update() {
        if (!Utils.haveNetworkConnection(this)) {
            Utils.showToast(this, "No internet connection");
            return;
        }

        Utils.showProgress(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(API.UPDATE_PROFILE,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        requestQueue.getCache().remove(API.UPDATE_PROFILE);
                        try {
                            Global.getInstance().me.photo = response.getString("photo");
                            Global.getInstance().me.address = response.getString("address");
                            Utils.showToast(ProfileActivity.this, "Updated successfully");
                            finish();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(ProfileActivity.this, "TimeoutError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(ProfileActivity.this, "AuthFailureError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(ProfileActivity.this, "ServerError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(ProfileActivity.this, "NetworkError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(ProfileActivity.this, "ParseError", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(ProfileActivity.this, "UnknownError", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<String, String>();
                    header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                    return header;
                }
            };
            customMultipartRequest
                .addStringPart("address", etAddress.getText().toString());

        if (avatarPath.length() > 0) {
            customMultipartRequest
                    .addImagePart("photo", avatarPath);
        } else {
        }

        requestQueue.add(customMultipartRequest);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case from_gallery:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        avatarPath = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap bitmap = BitmapUtility.adjustBitmap(avatarPath);
                        avatarPath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "hhq", FileUtility.getFilenameFromPath(avatarPath));

                        myCircularImageView.setImageBitmap(bitmap);
                    }
                }
                break;



            case from_camera: {

                if (resultCode == Activity.RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
        }
    }

    ///photo choose dialog
    public void showChooseDialog(Context context, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton( "Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dispatchTakePictureIntent();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton( "Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        takePictureFromGallery();
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton( "Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();

            }
        });
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(false);
        AlertDialog alert = builder.create();
        alert.show();
    }
    //////////////////take a picture from gallery
    private void takePictureFromGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, from_gallery);
    }
    /////////////capture photo
    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();
            avatarPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            avatarPath = "";
        }
        startActivityForResult(takePictureIntent, from_camera);
    }
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        avatarPath = f.getAbsolutePath();
        return f;
    }
    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }
    private File getAlbumDir() {

        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir("AllyTours");
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    ///process result of captured photo
    private void handleBigCameraPhoto() {

        if (avatarPath != null) {
            setPic();
//            galleryAddPic();
        }
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = myCircularImageView.getWidth();
        int targetH = myCircularImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(avatarPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapUtility.adjustBitmapForAvatar(avatarPath);
        myCircularImageView.setImageBitmap(bitmap);

//        FileUtility.deleteFile(avatarPath);
        avatarPath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "hhq", FileUtility.getFilenameFromPath(avatarPath));


    }
}
