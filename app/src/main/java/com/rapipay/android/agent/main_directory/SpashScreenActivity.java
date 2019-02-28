package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;

import java.util.ArrayList;

public class SpashScreenActivity extends BaseCompactActivity implements CustomInterface {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        localStorage.setActivityState(LocalStorage.LOGOUT, "0");
        initialization();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                route();
            }
        }, 2500);
    }
    private void initialization(){
        imageView = (ImageView)findViewById(R.id.imageView);
        String condition = "where " + RapipayDB.IMAGE_NAME + "='loginLogo.jpg'";
        if(db!=null) {
            ArrayList<ImagePozo> imagePozoArrayList = db.getImageDetails(condition);
            if (imagePozoArrayList.size() != 0) {
                byteConvert(imageView, imagePozoArrayList.get(0).getImagePath());
            } else {
                route_path(false);
            }
        }else {
            route_path(true);
        }
    }

    private void route() {
        new RouteClass(this, null, null, localStorage, null);
        finish();
    }

    private void route_path(boolean flag) {
        if (BuildConfig.APPTYPE == 1 || BuildConfig.APPTYPE == 3)
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.rapipay_agent));
        if (BuildConfig.APPTYPE == 2)
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.rapipay_parter));
        if(flag) {
            dbNull(SpashScreenActivity.this);
        }
    }

    @Override
    public void okClicked(String type, Object ob) {
        if(type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
