package com.ntsoft.ihhq.controller.main;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntsoft.ihhq.R;
import com.ntsoft.ihhq.constant.Constant;
import com.ntsoft.ihhq.controller.correspondence.CorrespondenceActiveFragment;
import com.ntsoft.ihhq.controller.correspondence.CorrespondenceCompletedFragment;
import com.ntsoft.ihhq.controller.correspondence.CorrespondenceFragment;
import com.ntsoft.ihhq.model.Global;

/**
 * A simple {@link Fragment} subclass.
 */
public class CorrespondenceContainerFragment extends Fragment {

    Activity mActivity;

    public CorrespondenceContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_correspondence_container, container, false);
        initUI(view);
        return view;
    }

    void initUI(View view) {
        mActivity = getActivity();
        CorrespondencePagerAdapter adapter = new CorrespondencePagerAdapter(getChildFragmentManager());
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        //
        ((MainActivity)mActivity).showHidePlusButton(true);
    }
    static class CorrespondencePagerAdapter extends FragmentStatePagerAdapter {

        public CorrespondencePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Fragment fragment1 = new CorrespondenceActiveFragment();
                    return fragment1;
                case 1:
                    Fragment fragment2 = new CorrespondenceFragment();
                    return fragment2;
                case 2:
                    Fragment fragment3 = new CorrespondenceCompletedFragment();
                    return fragment3;
                default:
                    return new CorrespondenceFragment();
            }
        }

        @Override
        public int getCount() {
            if (Global.getInstance().me.role.equals(Constant.arrUserRoles[0])) {
                return 3;
            } else {
                return 2;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Active";
                case 1:
                    return "Unassigned";
                case 2:
                    return "Completed";
                default:
                    return "";
            }
        }
    }
}
