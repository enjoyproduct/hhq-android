package com.ntsoft.ihhq.controller.correspondence;

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
import android.widget.ListView;
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
import com.ntsoft.ihhq.controller.adapter.ChatAdapter;
import com.ntsoft.ihhq.controller.home.UploadNewDocumentActivity;
import com.ntsoft.ihhq.model.CorrespondenceModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.model.MessageModel;
import com.ntsoft.ihhq.utility.FileUtility;
import com.ntsoft.ihhq.utility.TimeUtility;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CorrespondenceDetailActivity extends AppCompatActivity {
    private static final String TAG = "CorrespondenceDetailActivity";

    CorrespondenceModel correspondenceModel;
    String urlGetMessage, urlPostMessage;
    ArrayList<MessageModel> arrMessages;
    ChatAdapter mAdapter;
    ListView listView;
    private PullToRefreshListView mPullRefreshHomeListView;
    Button btnSend;
    ImageButton ibAttach;
    EditText etMessage;
    String filePath;
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
        arrMessages = new ArrayList<>();
        filePath = "";
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

        etMessage = (EditText)findViewById(R.id.et_edit);
        ibAttach = (ImageButton)findViewById(R.id.ib_attach);
        ibAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseFile();
            }
        });
        btnSend = (Button)findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().isEmpty()) {
                    return;
                }
                buildTextMessage(etMessage.getText().toString());
                if (!filePath.isEmpty()) {
                    buildAttachmentMessage(FileUtility.getFilenameFromPath(filePath), filePath);
                }
                sendMessage(etMessage.getText().toString(), filePath);
                etMessage.setText("");
                filePath = "";
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
                                if (messageModel.sender_id == Global.getInstance().me.id) {
                                    messageModel.isIncoming = false;
                                } else {
                                    messageModel.isIncoming = true;
                                }
                                arrMessages.add(messageModel);
                                //if has attachment
                                String str = jsonObject.getString("message");
                                JSONObject msgObj = new JSONObject(str);
                                if (msgObj.has("attachments")) {
                                    JSONArray arrAttachments = msgObj.getJSONArray("attachments");
                                    for (int k = 0; k < arrAttachments.length(); k ++) {
                                        JSONObject attchObj = arrAttachments.getJSONObject(k);

                                        messageModel.attachmentName = attchObj.getString("name");
                                        messageModel.attachmentPath = attchObj.getString("path");
                                        messageModel.type = 1;
                                        arrMessages.add(messageModel);
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

        CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest(urlPostMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
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
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(CorrespondenceDetailActivity.this, "TimeoutError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(CorrespondenceDetailActivity.this, "AuthFailureError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(CorrespondenceDetailActivity.this, "ServerError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(CorrespondenceDetailActivity.this, "NetworkError", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(CorrespondenceDetailActivity.this, "ParseError", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CorrespondenceDetailActivity.this, "UnknownError", Toast.LENGTH_LONG).show();
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
        customMultipartRequest.addStringPart("message", message);
        if (filePath.length() > 0) {
            customMultipartRequest.addDocumentPart("file", filePath);
        } else {
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(customMultipartRequest);
    }

    ////////////////////////
    private static final int FILE_SELECT_CODE = 0;
    void browseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        String[] mimetypes = {"application/pdf", "application/doc", "application/xls"};
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
                    String path = FileUtility.getPath(CorrespondenceDetailActivity.this, uri);
                    String message = etMessage.getText().toString();
                    if (path != null) {
                        filePath = path;
//                        if (message.isEmpty()) {
//                            filePath = path;
//                        } else {
//                            buildTextMessage(message);
//                            buildAttachmentMessage(FileUtility.getFilenameFromPath(path), path);
//                            sendMessage(message, path);
//                            etMessage.setText("");
//                        }
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
}
