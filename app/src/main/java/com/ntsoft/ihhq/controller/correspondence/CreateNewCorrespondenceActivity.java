package com.ntsoft.ihhq.controller.correspondence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.utility.FileUtility;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateNewCorrespondenceActivity extends AppCompatActivity {


    LinearLayout llDepartment, llFileRef, llAttachFile;
    Button btnSubmit;
    TextView tvDepartment, tvFileRef, tvAvailability;
    EditText etMessage, etSubjectMatter;

    ArrayList<String> arrDepartments, arrFileRefs, arrAttachments;
    ArrayList<Integer> arrDepartmentIDs;

    String fileRef = "";
    int selectedDepartmentID = -1;
    String message, subject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_correspondence);
        initVariables();
        initUI();
        getAllFileRef();
        getAllCorrespondenceCategory();
    }
    void initVariables() {
        arrDepartments = new ArrayList<>();
        arrFileRefs = new ArrayList<>();
        arrAttachments = new ArrayList<>();
        arrDepartmentIDs = new ArrayList<>();
        if (getIntent().hasExtra("file_ref")) {
            fileRef = getIntent().getStringExtra("file_ref");
        } else {
            fileRef = "";
        }
        if (getIntent().hasExtra("category_id")) {
            selectedDepartmentID = getIntent().getIntExtra("category_id", -1);
        } else {
            selectedDepartmentID = -1;
        }
        message = "";
        subject = "";
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
        tvTitle.setText("NEW CORRESPONDENCE REQUEST");

        llDepartment = (LinearLayout)findViewById(R.id.ll_department);
        llDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDepartmentID < 0) {
                    showDepartmentDlg();
                }
            }
        });
        llFileRef = (LinearLayout)findViewById(R.id.ll_file_ref);
        llFileRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDepartmentID < 0) {
                    showFileRefDlg();
                }
            }
        });

        llAttachFile = (LinearLayout)findViewById(R.id.ll_attach_file);
        llAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttachmentDlg();
            }
        });

        tvDepartment = (TextView)findViewById(R.id.tv_department);
        tvAvailability = (TextView)findViewById(R.id.tv_availability);
        tvFileRef = (TextView)findViewById(R.id.tv_file_ref);
        if (!fileRef.isEmpty()) {
            tvFileRef.setText(fileRef);
            tvAvailability.setVisibility(View.GONE);
        }

        etSubjectMatter = (EditText)findViewById(R.id.et_subject_matter);
        etMessage = (EditText)findViewById(R.id.et_message);

        btnSubmit = (Button)findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSubjectMatter.getText().toString().isEmpty()) {
                    Utils.showOKDialog(CreateNewCorrespondenceActivity.this, "Please input subject matter");
                    return;
                }
                if (etMessage.getText().toString().isEmpty()) {
                    Utils.showOKDialog(CreateNewCorrespondenceActivity.this, "Please input message");
                    return;
                }
                if (selectedDepartmentID < 0) {
                    Utils.showOKDialog(CreateNewCorrespondenceActivity.this, "Please select department");
                    return;
                }
                submitCorrespondence();
            }
        });
    }
    private void getAllFileRef() {
        if (!Utils.haveNetworkConnection(this)) {
            Utils.showToast(this, "No internet connection");
            return;
        }

        Utils.showProgress(this);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest( API.GET_FILE_REFS, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                Utils.hideProgress();
                int count = response.length();
                try {
                    for (int i = 0; i < count; i ++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        arrFileRefs.add(jsonObject.getString("file_ref"));
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
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "Network Timeout Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "Auth Failure Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "Unknown Error", Toast.LENGTH_LONG).show();
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
        requestQueue.add(request);
    }
    private void getAllCorrespondenceCategory() {
        if (!Utils.haveNetworkConnection(this)) {
            Utils.showToast(this, "No internet connection");
            return;
        }

        Utils.showProgress(this);
        JsonArrayRequest request = new JsonArrayRequest( API.GET_TICKET_CATEGORY, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                Utils.hideProgress();
                int count = response.length();

                try {
                    for (int i = 0; i < count; i ++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        arrDepartments.add(jsonObject.getString("name"));
                        arrDepartmentIDs.add(jsonObject.getInt("category_id"));

                        if (jsonObject.getInt("category_id") == selectedDepartmentID) {
                            tvDepartment.setText(jsonObject.getString("name"));
                        }
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
                        Toast.makeText(CreateNewCorrespondenceActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    private void submitCorrespondence() {
        if (!Utils.haveNetworkConnection(this)) {
            Utils.showToast(this, "No internet connection");
            return;
        }

        Utils.showProgress(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(API.CREAT_NEW_TICKET,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        requestQueue.getCache().remove(API.CREAT_NEW_TICKET);
                        try {
                            Utils.showToast(CreateNewCorrespondenceActivity.this, "Created successfully");
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
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "TimeoutError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "AuthFailureError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "ServerError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "NetworkError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "ParseError", Toast.LENGTH_LONG).show();
                        } else {
                            //TODO
                            Toast.makeText(CreateNewCorrespondenceActivity.this, "UnknownError", Toast.LENGTH_LONG).show();
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
                .addStringPart("department_id", String.valueOf(selectedDepartmentID))
                .addStringPart("subject", etSubjectMatter.getText().toString())
                .addStringPart("message", etMessage.getText().toString());
        if (!fileRef.isEmpty()) {
            customMultipartRequest.addStringPart("file_ref", fileRef);
        }
        for (String path: arrAttachments) {
            customMultipartRequest.addDocumentPart("attachments[]", path);
        }

        requestQueue.add(customMultipartRequest);
    }

    public void showDepartmentDlg(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Choose Department");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrDepartments); //selected item will look like a spinner set from XML
        builder1.setCancelable(true);
        builder1.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvDepartment.setText(arrDepartments.get(which));
                selectedDepartmentID = arrDepartmentIDs.get(which);
                dialog.dismiss();
            }
        });
        builder1.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder1.create();
        alert.show();
    }
    public void showFileRefDlg(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Choose FileRef");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrFileRefs); //selected item will look like a spinner set from XML
        builder1.setCancelable(true);
        builder1.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvFileRef.setText(arrFileRefs.get(which));
                fileRef = arrFileRefs.get(which);
                dialog.dismiss();
            }
        });
        builder1.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder1.create();
        alert.show();
    }
    public void showAttachmentDlg(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Add Attachment");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrAttachments); //selected item will look like a spinner set from XML
        builder1.setCancelable(true);
        builder1.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               showDeleteDlg(which);
            }
        });
        builder1.setPositiveButton( "Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        browseFile();
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder1.setCancelable(false);
        AlertDialog alert = builder1.create();
        alert.show();
    }
    public void showDeleteDlg(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Attachment");
        builder.setMessage("Do you want to remove this attachment?");
        builder.setCancelable(true);
        builder.setPositiveButton( "Remove",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        arrAttachments.remove(index);
                        Utils.showToast(CreateNewCorrespondenceActivity.this, "Removed");
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
//        builder.setNeutralButton("Open", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                // TODO Auto-generated method stub
//                Toast.makeText(getApplicationContext(), "Close is clicked", Toast.LENGTH_LONG).show();
//
//            }
//        });
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    /////////
    private static final int FILE_SELECT_CODE = 0;
    void browseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/*");
        String[] mimetypes = {"application/pdf", "application/word", "application/vnd.ms-excel", "application/doc", "application/xls"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("CREATE_NEW_TICKET", "File Uri: " + uri.toString());
                    // Get the path
                    String filePath = FileUtility.getPath(CreateNewCorrespondenceActivity.this, uri);
                    if (filePath != null) {
                        arrAttachments.add(filePath);
                    }
                    Log.d("CREATE_NEW_TICKET", "File Path: " + filePath);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
