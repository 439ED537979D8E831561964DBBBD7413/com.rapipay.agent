package com.rapipay.android.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.utils.BaseFragment;

public class TpinTab extends BaseFragment {
    TabLayout tabLayout;
    GenerateTPINFragment fragment_credit;
    EnableDiablePinFragment transFragment;
    View rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.tpin_tab_layout, container, false);
        initialize(rv);
        return rv;
    }

    private void initialize(View view){
        tabLayout = (TabLayout) view.findViewById(R.id.bottomNavigation);
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void setupTabLayout() {
        fragment_credit = new GenerateTPINFragment();
        transFragment = new EnableDiablePinFragment();

        tabLayout.addTab(tabLayout.newTab().setText("Generate TPIN"), true);
        tabLayout.addTab(tabLayout.newTab().setText("ENABLE / DISABLE TPIN"));
    }

    private void bindWidgetsWithAnEvent() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragment_credit);
                break;
            case 1:
                replaceFragment(transFragment);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}


