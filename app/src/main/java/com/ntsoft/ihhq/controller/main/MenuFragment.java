package com.ntsoft.ihhq.controller.main;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.controller.main.MainActivity;
import com.ntsoft.ihhq.model.MenuModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    ListView listView;
    LayoutInflater mLayoutInflator;
    private Activity mActivity;

    String[] images = {"menu_home", "menu_notification", "menu_empower", "menu_support", "menu_scan_qr_code", "menu_setting", "menu_sign_out"};
    String[] titles = {"Home", "Notifications", "Empower", "My Correspondence","Scan QR Code", "Settings", "Logout"};

    ArrayList<MenuModel> arrMenus;
    int selectedNum = 0;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mLayoutInflator = getLayoutInflater(savedInstanceState);
        mActivity = getActivity();
        arrMenus = generateMenuModel();
        initSelectedState();
        initUI(view);
        return view;
    }
    private void initUI(View view) {

        listView = (ListView)view.findViewById(R.id.listview);
        final MenuAdapter menuAdapter = new MenuAdapter(generateMenuModel());
        listView.setAdapter(menuAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedNum = position;
                initSelectedState();
                menuAdapter.notifyDataSetChanged();
                ((MainActivity)mActivity).menuNavigateTo(position);
            }
        });
    }
    private ArrayList<MenuModel> generateMenuModel() {
        int[] images = {
                R.drawable.menu_home,
                R.drawable.menu_notification,
                R.drawable.menu_empower,
                R.drawable.menu_support,
                R.drawable.menu_scan_qr_code,
                R.drawable.menu_setting,
                R.drawable.menu_sign_out
        };
        ArrayList<MenuModel> arrayList = new ArrayList<>();
        for (int i = 0; i < images.length; i ++) {
            MenuModel menuModel = new MenuModel();
            menuModel.image = images[i];
            menuModel.title = titles[i];
            menuModel.isSelected = false;

            arrayList.add(menuModel);
        }
        return arrayList;
    }
    private void initSelectedState() {
        for (int i = 0; i < arrMenus.size(); i ++) {
            if (i == selectedNum) {
                arrMenus.get(i).isSelected = true;
            } else {
                arrMenus.get(i).isSelected = false;
            }
        }
    }
    private class MenuAdapter extends BaseAdapter {


        private MenuAdapter (ArrayList<MenuModel> arrayList) {
            arrMenus = arrayList;

        }
        @Override
        public int getCount() {
            return arrMenus.size();
        }

        @Override
        public Object getItem(int position) {
            return arrMenus.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mLayoutInflator.inflate(R.layout.item_menu, null);
            }
            ImageView imageView = (ImageView)view.findViewById(R.id.iv_menu);
            TextView textView = (TextView)view.findViewById(R.id.tv_menu);

            LinearLayout llContainer = (LinearLayout)view.findViewById(R.id.ll_container);
            RelativeLayout rlNotification = (RelativeLayout)view.findViewById(R.id.rl_notification_count);
            TextView tvNotificationCount = (TextView)view.findViewById(R.id.tv_notification_count);
            if (position == 1) {
//                rlNotification.setVisibility(View.VISIBLE);
            } else {
                rlNotification.setVisibility(View.INVISIBLE);
            }

            MenuModel  menuModel = arrMenus.get(position);
            imageView.setImageDrawable(getResources().getDrawable(menuModel.image));
            textView.setText(menuModel.title);
            if (menuModel.isSelected) {
                llContainer.setBackgroundColor(getResources().getColor(R.color.yellow));
            } else {
                llContainer.setBackgroundColor(getResources().getColor(R.color.leftMenuBGColor));
            }
            return view;
        }
    }
}
