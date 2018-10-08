package com.rapipay.android.rapipay.main_directory.main_directory;

import android.os.Bundle;
import android.os.Handler;
import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Database.RapipayDB;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.RouteClass;

import java.util.ArrayList;

public class SpashScreenActivity extends BaseCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        db = new RapipayDB(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                route();
            }
        }, 2500);
    }

    private void route() {
        new RouteClass(this,null,null,localStorage,null);
//        ArrayList<RapiPayPozo> list = db.getDetails();
//        if (list.size() != 0) {
//            Intent intent = new Intent(this, PinVerification.class);
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(this, LoginScreenActivity.class);
//            startActivity(intent);
//        }
        finish();
    }

}
