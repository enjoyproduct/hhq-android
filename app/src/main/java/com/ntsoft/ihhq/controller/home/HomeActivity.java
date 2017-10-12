package com.ntsoft.ihhq.controller.home;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.correspondence.CreateNewCorrespondenceActivity;
import com.ntsoft.ihhq.model.FileModel;
import com.ntsoft.ihhq.model.Global;

public class HomeActivity extends AppCompatActivity {

    TextView tvTitle;
    ImageButton ibPlus;
    public FileModel fileModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fileModel = (FileModel) getIntent().getSerializableExtra("file");
        initUI();
    }
    void initUI() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        final ImageButton ibBack = (ImageButton)toolbar.findViewById(R.id.ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = (TextView)toolbar.findViewById(R.id.tv_title);
        ibPlus = (ImageButton)toolbar.findViewById(R.id.ib_add);
        ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreateNewCorrespondenceActivity.class);
                intent.putExtra("file_ref", fileModel.file_ref);
                intent.putExtra("category_id", fileModel.category_id);
                intent.putExtra("fromFile", true);
                startActivity(intent);
            }
        });
        //test
        setTitle(fileModel.project_name + "\n" + "File Ref: " + fileModel.file_ref);
        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 4 && !fileModel.assigned_role.equals(Constant.arrUserRoles[6])) {
                    ibPlus.setVisibility(View.VISIBLE);
                } else {
                    ibPlus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    private void setTitle(String title) {
        tvTitle.setText(title);
    }
    static class HomePagerAdapter extends FragmentStatePagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeDocumentFragment();
                case 1:
                    return new HomeUpdateFragment();
                case 2:
                    return new HomePaymentFragment();
                case 3:
                    return new HomeContactsFragment();
                case 4:
                    return new HomeCorrespondenceFragment();
                default:
                    return new HomeDocumentFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Documents";
                case 1:
                    return "Updates";
                case 2:
                    return "Payments";
                case 3:
                    return "Contacts";
                case 4:
                    return "Correspondence";
                default:
                    return "";
            }
        }
    }
}
