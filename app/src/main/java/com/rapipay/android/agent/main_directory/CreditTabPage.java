package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.CreditRequestFragment;
import com.rapipay.android.agent.fragments.CreditTransFragment;
import com.rapipay.android.agent.utils.BaseCompactActivity;

public class CreditTabPage extends BaseCompactActivity implements View.OnClickListener{

    TabLayout tabLayout;
    CreditRequestFragment fragment_credit;
    CreditTransFragment transFragment;
    TextView toolbar_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_tablayout);
        initialize();
    }

    private void initialize(){
        tabLayout = (TabLayout) findViewById(R.id.bottomNavigation);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(list.get(0).getAgentName());
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void setupTabLayout() {
        fragment_credit = new CreditRequestFragment();
        transFragment = new CreditTransFragment();

        tabLayout.addTab(tabLayout.newTab().setText("Credit Request"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Credit History"));
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
        }
    }
}
