package com.ntsoft.ihhq.controller.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.utility.FileUtility;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UploadNewDocumentActivity extends AppCompatActivity {

    private static final String TAG = "UploadNewDocumentActivity";
    EditText etName;
    TextView tvFileRef;
    Button btnUpload;
    LinearLayout llBrowse;
    TextView tvFilePath;

    FileModel fileModel;
    String filePath;
    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_new_document);

        initVariables();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ImageButton ibBack = (ImageButton)toolbar.findViewById(R.id.ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView)toolbar.findViewById(R.id.tv_title);
        tvTitle.setText("UPLOAD A NEW DOCUMENT");
        etName = (EditText)findViewById(R.id.et_name);
        tvFileRef = (TextView)findViewById(R.id.tv_file_ref);
        if (fileModel != null) {
            tvFileRef.setText("File Ref: " + fileModel.file_ref);
        }
        btnUpload = (Button)findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().isEmpty()) {
                    Utils.showOKDialog(UploadNewDocumentActivity.this, "Please input file name");
                    return;
                }
                if (filePath.isEmpty()) {
                    Utils.showOKDialog(UploadNewDocumentActivity.this, "Please choose file");
                    return;
                }
                uploadFile();
            }
        });
        llBrowse = (LinearLayout)findViewById(R.id.ll_attach_file);
        llBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseFile();
            }
        });
        tvFilePath = (TextView)findViewById(R.id.tv_filepath);
    }
    void initVariables() {
        fileModel = (FileModel) getIntent().getSerializableExtra("file");
        filePath = "";
        fileName = "";
    }
    private static final int FILE_SELECT_CODE = 0;
    void browseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    filePath = FileUtility.getPath(UploadNewDocumentActivity.this, uri);
                    if (filePath != null) {
                        tvFilePath.setText(filePath);
                    } else {
                        filePath = "";
                        Utils.showOKDialog(UploadNewDocumentActivity.this, "Cannot select this file");
                    }
                    Log.d(TAG, "File Path: " + filePath);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void uploadFile() {
        if (!Utils.haveNetworkConnection(this)) {
            Utils.showToast(this, "No internet connection");
            return;
        }

        Utils.showProgress(this);
        final String endPoint = String.format(API.UPLOAD_NEW_DOCUMENT, fileModel.file_id);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(endPoint,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        requestQueue.getCache().remove(API.UPLOAD_NEW_DOCUMENT);
                        try {
                            Utils.showToast(UploadNewDocumentActivity.this, "Uploaded successfully");
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
                            Toast.makeText(UploadNewDocumentActivity.this, "TimeoutError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(UploadNewDocumentActivity.this, "AuthFailureError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(UploadNewDocumentActivity.this, "ServerError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(UploadNewDocumentActivity.this, "NetworkError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(UploadNewDocumentActivity.this, "ParseError", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(UploadNewDocumentActivity.this, "UnknownError", Toast.LENGTH_LONG).show();
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
                .addStringPart("name", etName.getText().toString())
                .addStringPart("file_ref", fileModel.file_ref);

        if (filePath.length() > 0) {
            customMultipartRequest.addDocumentPart("file", filePath);
        } else {
        }

        requestQueue.add(customMultipartRequest);
    }
}
