package com.ntsoft.ihhq.controller.home;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.API;
import com.ntsoft.ihhq.controller.adapter.HomeContactAdapter;
import com.ntsoft.ihhq.model.ContactModel;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;
import com.ntsoft.ihhq.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeContactsFragment extends Fragment {

    Activity mActivity;
    ListView listView;
    HomeContactAdapter mAdapter;
    ArrayList<ContactModel> arrContacts;
    int expandedNumber = 0;
    String api;
    FileModel fileModel;

    public HomeContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_contacts, container, false);
        initVariable();
        initUI(view);
        getContacts();
        return view;
    }
    private void initVariable() {
        mActivity = getActivity();
        arrContacts = new ArrayList<>();
        fileModel = ((HomeActivity)mActivity).fileModel;
    }
    void initUI(View view) {
        listView = (ListView)view.findViewById(R.id.listview);
        mAdapter = new HomeContactAdapter(mActivity, arrContacts);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                expandedNumber = position;
                initExpandState();
                mAdapter.notifyDataSetChanged();
            }
        });
    }
    private void initExpandState() {
        for (int i = 0; i < arrContacts.size(); i ++) {
            if (i == expandedNumber) {
                if (arrContacts.get(i).isExpanded) {
                    arrContacts.get(i).isExpanded = false;
                } else {
                    arrContacts.get(i).isExpanded = true;
                }
            } else {
                arrContacts.get(i).isExpanded = false;
            }
        }
    }
    void getContacts() {
        if (!Utils.haveNetworkConnection(mActivity)) {
            Utils.showToast(mActivity, "No internet connection");
            return;
        }

        if (fileModel == null) {
            return;
        }
        api = API.BASE_API_URL + "files/" + String.valueOf(fileModel.file_id) + "/contacts";

        arrContacts.clear();
        Utils.showProgress(mActivity);
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JsonArrayRequest request = new JsonArrayRequest( api, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                Utils.hideProgress();
                int count = response.length();
                requestQueue.getCache().remove(api);
                try {
                    for (int i = 0; i < count; i ++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        ContactModel contactModel = new ContactModel(jsonObject);
                        arrContacts.add(contactModel);
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
}
