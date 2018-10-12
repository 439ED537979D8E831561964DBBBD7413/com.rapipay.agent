package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.os.Handler;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;

public class SpashScreenActivity extends BaseCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        db = new RapipayDB(this);
        localStorage.setActivityState(LocalStorage.LOGOUT, "0");
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
