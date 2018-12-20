package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;

import java.util.ArrayList;

public class SpashScreenActivity extends BaseCompactActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        db = new RapipayDB(this);
//        if (BuildConfig.ROLL.equalsIgnoreCase("Branch2"))
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
        ArrayList<ImagePozo> imagePozoArrayList = db.getImageDetails(condition);
        if(imagePozoArrayList.size()!=0){
            byteConvert(imageView,imagePozoArrayList.get(0).getImagePath());
        }else {
            if (BuildConfig.APPTYPE == 1)
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
            if (BuildConfig.APPTYPE == 2)
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.rapipay_parter));
        }
    }

    private void route() {
        new RouteClass(this, null, null, localStorage, null);
        finish();
    }

}
