package com.ntsoft.ihhq.controller.home;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
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
import com.android.volley.request.CustomRequest;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.adapter.HomePaymentAdapter;
import com.ntsoft.ihhq.controller.correspondence.CorrespondenceDetailActivity;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.PaymentModel;
import com.ntsoft.ihhq.utility.BitmapUtility;
import com.ntsoft.ihhq.utility.FileUtility;
import com.ntsoft.ihhq.utility.Utils;
import com.ntsoft.ihhq.utility.camera.AlbumStorageDirFactory;
import com.ntsoft.ihhq.utility.camera.BaseAlbumDirFactory;
import com.ntsoft.ihhq.utility.camera.FroyoAlbumDirFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePaymentFragment extends Fragment {

    Activity mActivity;
    ListView listView;
    HomePaymentAdapter mAdapter;
    ArrayList<PaymentModel> arrPayments;
    FileModel fileModel;
    String api, billplzEmail;
    int index;
    boolean isBillCreated;

    String imagePath;
    String filePath;
    int selectedPaymentType;
    
    private static final int from_gallery = 1;
    private static final int from_camera = 2;
    private static final String JPEG_FILE_PREFIX = "iHHQ_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    public HomePaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_payment, container, false);
        initVariable();
        initUI(view);
        getPayments();
        return view;
    }

    @Override
    public void onResume() {
        if (isBillCreated) {
            checkBilling();
        }
        super.onResume();
    }

    private void initVariable() {
        mActivity = getActivity();
        arrPayments = new ArrayList<>();
        fileModel = ((HomeActivity)mActivity).fileModel;
        filePath = "";
        imagePath = "";
        billplzEmail = "";
        index = -1;
        isBillCreated = false;
        imagePath = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        selectedPaymentType = -1;
    }
    void initUI(View view) {
        listView = (ListView)view.findViewById(R.id.listview);
        mAdapter = new HomePaymentAdapter(mActivity, arrPayments);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isBillCreated = false;
                if (Global.getInstance().me.role.equals(Constant.arrUserRoles[0]) || Global.getInstance().me.role.equals(Constant.arrUserRoles[5])) {
                    if ( arrPayments.get(position ).status.equals(Constant.arrPaymentStatus[0])
                            && !fileModel.assigned_role.equals(Constant.arrUserRoles[6])) {
                        index = position ;
                        selectManualOrOnline();
                    }
                }

            }
        });
    }
    void selectManualOrOnline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Please select payment method");
        builder.setPositiveButton( Constant.arrPaymentMethod[0],
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        selectedPaymentType = 0;
                        selectReceiptType();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton( Constant.arrPaymentMethod[1],
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        selectedPaymentType = 1;
                        selectMethod();
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }
    //upload receipt
    void selectReceiptType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("");
        builder.setMessage("Do you want to upload receipt document or photo?");
        builder.setCancelable(true);
        builder.setPositiveButton( "Document",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        browseFile();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton( "Photo",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        pickReceiptImage();
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton( "Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                imagePath = "";
                dialog.cancel();

            }
        });
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(false);
        AlertDialog alert = builder.create();
        alert.show();
    }
    private static final int FILE_SELECT_CODE = 0;
    void browseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(mActivity, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    void pickReceiptImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Upload Receipt");
        builder.setMessage("Upload transaction slip/receipt from");
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
                imagePath = "";
                dialog.cancel();

            }
        });
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(false);
        AlertDialog alert = builder.create();
        alert.show();
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

                        Cursor cursor = mActivity.getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imagePath = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap bitmap = BitmapUtility.adjustBitmap(imagePath);
                        imagePath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "hhq", FileUtility.getFilenameFromPath(imagePath));
                        if (selectedPaymentType == 0) {
                            doUploadReceipt();
                        } else if (selectedPaymentType == 1) {
                            createBill(0);
                        }
                    }
                }
                break;
            case from_camera: {
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap = BitmapUtility.adjustBitmap(imagePath);
                    imagePath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "hhq", FileUtility.getFilenameFromPath(imagePath));
                    if (selectedPaymentType == 0) {
                        doUploadReceipt();
                    } else if (selectedPaymentType == 1) {
                        createBill(0);
                    }
                }
                break;
            }
            case FILE_SELECT_CODE:
                if (resultCode == mActivity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("HomePaymentFragment", "File Uri: " + uri.toString());
                    // Get the path
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        filePath = FileUtility.getPath(mActivity, uri);
                    }
                    if (filePath != null) {
                        if (selectedPaymentType == 0) {
                            doUploadReceipt();
                        } else if (selectedPaymentType == 1) {
                            createBill(0);
                        }
                    } else {
                        filePath = "";
                        Utils.showOKDialog(mActivity, "Cannot select this file");
                    }
                    Log.d("HomePaymentFragment", "File Path: " + filePath);
                }
                break;
        }
    }
    void doUploadReceipt() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        if (imagePath.isEmpty() && filePath.isEmpty()) {
            return;
        }
        Utils.showProgress(mActivity);
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(API.UPLOAD_RECEIPT,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        requestQueue.getCache().remove(API.UPDATE_PROFILE);
                        try {
                            String status = response.getString("status");
                            Utils.showToast(mActivity, "Waiting for staff review");
                            arrPayments.get(index).status = Constant.arrPaymentStatus[2];
                            mAdapter.notifyDataSetChanged();
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
                            Toast.makeText(mActivity, "TimeoutError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(mActivity, "AuthFailureError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(mActivity, "ServerError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(mActivity, "NetworkError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(mActivity, "ParseError", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(mActivity, "UnknownError", Toast.LENGTH_LONG).show();
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
                .addStringPart("payment_id", String.valueOf(arrPayments.get(index).payment_id))
                .addStringPart("amount", arrPayments.get(index).amount);

        if (imagePath.length() > 0) {
            customMultipartRequest.addImagePart("receipt", imagePath);
        } else if (filePath.length() > 0){
            customMultipartRequest.addDocumentPart("receipt", filePath);
        }

        requestQueue.add(customMultipartRequest);
    }
    void inputBillplzEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Please insert email");
        final EditText input = new EditText(mActivity);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                billplzEmail = input.getText().toString();
                createBill(1);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }
    void selectMethod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Please select payment method");
        builder.setSingleChoiceItems(Constant.arrBillplzMethod, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    selectReceiptType();
                } else if (which == 1) {
                    inputBillplzEmail();
                }
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }


    private void createBill(final int method) {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }
        if (method == 0) {
            if (filePath.isEmpty() && imagePath.isEmpty()) {
                Utils.showToast(mActivity, "Please select attachment");
                return;
            }
        }
        Utils.showProgress(mActivity);
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(API.CREATE_BILL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        requestQueue.getCache().remove(API.CREATE_BILL);
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                isBillCreated = true;
                                if (method == 1) {// in case of billplz
                                    String url = response.getString("url");
                                    launchBrowser(url);
                                } else {//there is no url in case of bank
                                    if (isBillCreated) {
                                        checkBilling();
                                    }
                                }
                            } else {
                                Utils.showOKDialog(mActivity, "Failed to create bill");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(mActivity, "TimeoutError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(mActivity, "AuthFailureError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(mActivity, "ServerError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(mActivity, "NetworkError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(mActivity, "ParseError", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mActivity, "UnknownError", Toast.LENGTH_LONG).show();
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
        customMultipartRequest.addStringPart("payment_id", String.valueOf(arrPayments.get(index).payment_id));
        customMultipartRequest.addStringPart("method", Constant.arrBillplzMethod[method]);
        customMultipartRequest.addStringPart("amount", arrPayments.get(index).amount);
        customMultipartRequest.addStringPart("email_billpls", billplzEmail);
        customMultipartRequest.addStringPart("return_url", "http://hhqtouch.com.my");
        if (method == 0) {//in case of bank
            if (filePath.length() > 0) {
                customMultipartRequest.addDocumentPart("receipt", filePath);
            } else if (imagePath.length() > 0){
                customMultipartRequest.addImagePart("receipt", imagePath);
            }
        }
        requestQueue.add(customMultipartRequest);
    }
    void launchBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
    void checkBilling() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        Utils.showProgress(mActivity);
        Map<String, String> params = new HashMap<String, String>();
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        CustomRequest customRequest = new CustomRequest(Request.Method.GET, API.CHECK_BILLING, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        requestQueue.getCache().remove(API.CHECK_BILLING);
                        arrPayments.get(index).status = Constant.arrPaymentStatus[1];
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(mActivity, "Network Timeout Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(mActivity, "Auth Failure Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(mActivity, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(mActivity, "Network Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(mActivity, "Parse Error", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(mActivity, "Unknown Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };
        requestQueue.add(customRequest);
    }
    void getPayments() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        if (fileModel == null) {
            return;
        }
        api = API.BASE_API_URL + "files/" + String.valueOf(fileModel.file_id) + "/payments";

        arrPayments.clear();
        Utils.showProgress(mActivity);
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JsonArrayRequest request = new JsonArrayRequest( api, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                Utils.hideProgress();
                requestQueue.getCache().remove(api);
                int count = response.length();
                try {
                    for (int i = 0; i < count; i ++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        PaymentModel paymentModel = new PaymentModel(jsonObject);
                        arrPayments.add(paymentModel);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        Toast.makeText(mActivity, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };
        requestQueue.add(request);
    }


    
    //////////////////////////////
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
            imagePath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            imagePath = "";
        }
        startActivityForResult(takePictureIntent, from_camera);
    }
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        imagePath = f.getAbsolutePath();
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

   
   
}
