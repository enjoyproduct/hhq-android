package com.ntsoft.ihhq.controller.correspondence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.adapter.ChatAdapter;
import com.ntsoft.ihhq.controller.home.UploadNewDocumentActivity;
import com.ntsoft.ihhq.model.CorrespondenceModel;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.MessageModel;
import com.ntsoft.ihhq.utility.BitmapUtility;
import com.ntsoft.ihhq.utility.FileDownloadCompleteListener;
import com.ntsoft.ihhq.utility.FileDownloader;
import com.ntsoft.ihhq.utility.FileUtility;
import com.ntsoft.ihhq.utility.StringUtility;
import com.ntsoft.ihhq.utility.TimeUtility;
import com.ntsoft.ihhq.utility.Utils;
import com.ntsoft.ihhq.utility.camera.AlbumStorageDirFactory;
import com.ntsoft.ihhq.utility.camera.BaseAlbumDirFactory;
import com.ntsoft.ihhq.utility.camera.FroyoAlbumDirFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CorrespondenceDetailActivity extends AppCompatActivity {
    private static final String TAG = "CorrespondenceDetailActivity";

    RelativeLayout rlEditorContainer;
    CorrespondenceModel correspondenceModel;
    String urlGetMessage, urlPostMessage;
    ArrayList<MessageModel> arrMessages;
    ChatAdapter mAdapter;
    ListView listView;
    private PullToRefreshListView mPullRefreshHomeListView;
    Button btnSend;
    ImageButton ibAttach;
    EditText etMessage;

    FileModel fileModel;
    String filePath;
    private static final int from_gallery = 1;
    private static final int from_camera = 2;
    private static final String JPEG_FILE_PREFIX = "HHQ_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correspondence_detail);

        initVariables();
        initUI();
        getMessages();
    }
    void initVariables() {
        correspondenceModel = (CorrespondenceModel) getIntent().getSerializableExtra("correspondence");
        if (correspondenceModel != null) {
            urlGetMessage = API.GET_TICKET_MESSAGE + String.valueOf(correspondenceModel.ticket_id) + "/messages?per_page=60";
            urlPostMessage = API.POST_TICKET_MESSAGE + String.valueOf(correspondenceModel.ticket_id) + "/messages";
        } else {
            urlGetMessage = "";
            urlPostMessage = "";
        }
        if (getIntent().hasExtra("fileModel")) {
            fileModel = (FileModel) getIntent().getSerializableExtra("fileModel");
        } else {
            fileModel = null;
        }

        arrMessages = new ArrayList<>();
        filePath = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }
    void initUI() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ImageButton ibBack = (ImageButton)toolbar.findViewById(R.id.ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView)toolbar.findViewById(R.id.tv_title);
        if (correspondenceModel != null) {
            tvTitle.setText(correspondenceModel.client_name + "(" + correspondenceModel.category + ") \n Ref: " + correspondenceModel.file_ref);
        }
        ///create listview
        mPullRefreshHomeListView = (PullToRefreshListView)findViewById(R.id.lv_home);
        mPullRefreshHomeListView.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {
            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
                if (state == PullToRefreshBase.State.RELEASE_TO_REFRESH && direction == PullToRefreshBase.Mode.PULL_FROM_START) {
                    getMessages();
                }
            }
        });


        listView = mPullRefreshHomeListView.getRefreshableView();
        mAdapter = new ChatAdapter(this, arrMessages);
        listView.setAdapter(mAdapter);
        listView.smoothScrollToPosition(arrMessages.size() - 1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrMessages.get(position - 1).type == 1) {
                    MessageModel msg = arrMessages.get(position - 1);
                    String endPoint = String.format(API.DOWNLOAD_TICKET_FILE, msg.attachmentPath, msg.attachmentName);
                    downloadFile(endPoint, msg.attachmentName);
                } else {

                }
            }
        });
        etMessage = (EditText)findViewById(R.id.et_edit);
        ibAttach = (ImageButton)findViewById(R.id.ib_attach);
        ibAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAttachmentType();
            }
        });
        btnSend = (Button)findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().isEmpty() && filePath.isEmpty()) {
                    return;
                }
                if (!etMessage.getText().toString().isEmpty()) {
                    buildTextMessage(etMessage.getText().toString());
                }

                sendMessage(etMessage.getText().toString(), "");
                etMessage.setText("");
                filePath = "";
            }
        });
        rlEditorContainer = (RelativeLayout) findViewById(R.id.rl_editor_container);
        if (fileModel != null) {
            if (fileModel.assigned_role.equals(Constant.arrUserRoles[6])) {
                rlEditorContainer.setVisibility(View.GONE);
            } else {
                rlEditorContainer.setVisibility(View.VISIBLE);
            }
        }

    }
    void downloadFile(String endpoint, final String fileName) {
        FileDownloader.downloadFile(this, endpoint, fileName, new FileDownloadCompleteListener() {
            @Override
            public void onComplete(String filePath) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File(filePath);
                if (fileName.contains(".pdf")) {
                    intent.setDataAndType( Uri.fromFile( file ), "application/pdf" );
                } else if (fileName.contains(".doc") || filePath.contains(".word")) {
                    intent.setDataAndType( Uri.fromFile( file ), "application/msword");
                }else if (fileName.contains(".xls")) {
                    intent.setDataAndType( Uri.fromFile( file ), "application/vnd.ms-excel" );
                } else {
                    intent.setDataAndType( Uri.fromFile( file ), "image/*" );
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (file.exists()) {
                    startActivity(intent);
                } else {
                    return;
                }
            }
        });
    }
    private void getMessages() {
        if (!Utils.haveNetworkConnection(this)) {
            Utils.showToast(this, "No internet connection");
            return;
        }

        if (urlGetMessage.isEmpty()) {
            return;
        }
        Utils.showProgress(this);
        Map<String, String> params = new HashMap<String, String>();
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomRequest customRequest = new CustomRequest(Request.Method.GET, urlGetMessage, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        requestQueue.getCache().remove(urlGetMessage);
                        try {
                            String next_page_url = response.getString("next_page_url");
                            if (next_page_url == "null") {
                                urlGetMessage = "";
                            } else {
                                urlGetMessage = next_page_url;
                            }
                            int totalCount = response.getInt("total");

                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i ++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MessageModel messageModel = new MessageModel(jsonObject);
                                messageModel.type = 0;
                                int senderId = messageModel.sender_id;
                                if (senderId == Global.getInstance().me.id) {
                                    messageModel.isIncoming = false;
                                } else {
                                    messageModel.isIncoming = true;
                                }
                                if (!messageModel.message.isEmpty()) {
                                    arrMessages.add(messageModel);
                                }
                                //if has attachment
                                String str = jsonObject.getString("message");
                                JSONObject msgObj = new JSONObject(str);
                                if (msgObj.has("attachments")) {
                                    JSONArray arrAttachments = msgObj.getJSONArray("attachments");
                                    for (int k = 0; k < arrAttachments.length(); k ++) {
                                        JSONObject attchObj = arrAttachments.getJSONObject(k);
                                        MessageModel messageModel1 = new MessageModel(jsonObject);
                                        messageModel1.attachmentName = attchObj.getString("name");
                                        messageModel1.attachmentPath = attchObj.getString("path");
                                        messageModel1.type = 1;
                                        if (senderId == Global.getInstance().me.id) {
                                            messageModel1.isIncoming = false;
                                        } else {
                                            messageModel1.isIncoming = true;
                                        }
                                        arrMessages.add(messageModel1);
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            listView.smoothScrollByOffset(arrMessages.size() - 1);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        Toast.makeText(CorrespondenceDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };

        requestQueue.add(customRequest);
    }
    private void buildTextMessage(String message) {
        MessageModel messageModel = new MessageModel();
        messageModel.isIncoming = false;
        messageModel.type = 0;
        messageModel.message = message;
        messageModel.name = Global.getInstance().me.name;
        messageModel.time = TimeUtility.getCurrentTimeByString();
        messageModel.date = TimeUtility.getCurrentTimeByString("dd MMM yyyy");
        appendNewMessage(messageModel);
    }
    
    private void buildAttachmentMessage(String fileName, String filePath) {
        MessageModel messageModel = new MessageModel();
        messageModel.isIncoming = false;
        messageModel.type = 1;
        messageModel.attachmentName = fileName;
        messageModel.name = Global.getInstance().me.name;
        messageModel.time = TimeUtility.getCurrentTimeByString();
        messageModel.date = TimeUtility.getCurrentTimeByString("dd MMM yyyy");

        appendNewMessage(messageModel);
    }
    private void appendNewMessage(MessageModel messageModel) {
        arrMessages.add(messageModel);
        mAdapter.notifyDataSetChanged();
        listView.smoothScrollByOffset(arrMessages.size() - 1);
    }
    private void sendMessage(String message, final String filePath) {
        if (!Utils.haveNetworkConnection(this)) {
            Utils.showToast(this, "No internet connection");
            return;
        }
        Utils.showProgress(this);
        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(urlPostMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String message = response.getString("message");
                            JSONObject msgObject = new JSONObject(message);
                            JSONArray attachments = msgObject.getJSONArray("attachments");
                            if (attachments.length() > 0) {
                                String path = attachments.getJSONObject(0).getString("path");
                                String name = attachments.getJSONObject(0).getString("name");
                                arrMessages.get(arrMessages.size() - 1).attachmentName = name;
                                arrMessages.get(arrMessages.size() - 1).attachmentPath = path;
                                mAdapter.notifyDataSetChanged();
                                listView.smoothScrollByOffset(arrMessages.size() - 1);
                            }
                            Utils.showToast(CorrespondenceDetailActivity.this, "success");
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            Toast.makeText(CorrespondenceDetailActivity.this, "TimeoutError", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof AuthFailureError) {
//                            Toast.makeText(CorrespondenceDetailActivity.this, "AuthFailureError", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ServerError) {
//                            Toast.makeText(CorrespondenceDetailActivity.this, "ServerError", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof NetworkError) {
//                            Toast.makeText(CorrespondenceDetailActivity.this, "NetworkError", Toast.LENGTH_LONG).show();
//                        } else if (error instanceof ParseError) {
//                            Toast.makeText(CorrespondenceDetailActivity.this, "ParseError", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(CorrespondenceDetailActivity.this, "UnknownError", Toast.LENGTH_LONG).show();
//                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + Global.getInstance().me.token);
                return header;
            }
        };
        customMultipartRequest.addStringPart("message", message);
        if (filePath.length() > 0) {
            String fileName = FileUtility.getFilenameFromPath(filePath);
            if (fileName.contains(".png") || fileName.contains(".jpg") || fileName.contains(".jpeg")) {
                customMultipartRequest.addImagePart("attachments", filePath);
            } else {
                customMultipartRequest.addDocumentPart("attachments", filePath);
            }
        } else {
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(customMultipartRequest);
    }

    ////////////////////////
    void selectAttachmentType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select attachment type");
        builder.setMessage("");
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
                        pickCorrespondenceImage();
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton( "Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                filePath = "";
                dialog.cancel();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
    void pickCorrespondenceImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select photo from");
        builder.setMessage("");
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
                filePath = "";
                dialog.cancel();

            }
        });
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(false);
        AlertDialog alert = builder.create();
        alert.show();
    }
    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case from_gallery:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = this.getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap bitmap = BitmapUtility.adjustBitmap(filePath);
                        filePath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "hhq", FileUtility.getFilenameFromPath(filePath));
                        buildAttachmentMessage(FileUtility.getFilenameFromPath(filePath), filePath);
                        sendMessage("", filePath);
                    }
                }
                break;
            case from_camera: {
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap = BitmapUtility.adjustBitmap(filePath);
                    filePath = BitmapUtility.saveBitmap(bitmap, Constant.MEDIA_PATH + "hhq", FileUtility.getFilenameFromPath(filePath));
                    buildAttachmentMessage(FileUtility.getFilenameFromPath(filePath), filePath);
                    sendMessage("", filePath);
                }
                break;
            }
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = FileUtility.getPath(CorrespondenceDetailActivity.this, uri);
                    String message = etMessage.getText().toString();
                    if (path != null) {
                        filePath = path;
                        buildAttachmentMessage(FileUtility.getFilenameFromPath(filePath), filePath);
                        sendMessage("", filePath);
                    } else {
                    }
                    Log.d(TAG, "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            filePath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            filePath = "";
        }
        startActivityForResult(takePictureIntent, from_camera);
    }
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        filePath = f.getAbsolutePath();
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
