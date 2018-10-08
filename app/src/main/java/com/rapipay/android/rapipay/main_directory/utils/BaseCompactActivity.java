package com.rapipay.android.rapipay.main_directory.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Database.RapipayDB;
import com.rapipay.android.rapipay.main_directory.Model.RapiPayPozo;
import com.rapipay.android.rapipay.main_directory.main_directory.MainActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BaseCompactActivity extends AppCompatActivity {
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected SimpleDateFormat format;
    protected static String balance = null;
    protected Date date;
    protected TextView heading;
    public static RapipayDB db;
    protected ArrayList<RapiPayPozo> list;
    protected LocalStorage localStorage;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        align_text_center();
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        date = new Date();
        localStorage = LocalStorage.getInstance(this);
        hideKeyboard(this);
        if (db != null && db.getDetails_Rapi())
            list = db.getDetails();
    }

    private void align_text_center() {
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.splash_screen));
        TextView tv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
        tv.setLayoutParams(lp);
        tv.setText(ab.getTitle());
        tv.setTextSize(24.0f);
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv.setGravity(Gravity.CENTER);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ab.setCustomView(tv);
    }

    public JSONObject getJson_Validate(String mobileNo, String kycType, String parentID, String sessionKey, String sessionRefNo, String nodeAgent) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "KYC_PROCESS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", format.format(date));
            jsonObject.put("agentId", parentID);
            jsonObject.put("mobileNo", mobileNo);
            jsonObject.put("kycType", kycType);
            jsonObject.put("responseUrl", WebConfig.RESPONSE_URL);
            if (nodeAgent.equalsIgnoreCase("")) {
                jsonObject.put("nodeAgentId", mobileNo);
                jsonObject.put("sessionRefNo", sessionRefNo);
                jsonObject.put("checkSum", GenerateChecksum.checkSum(sessionKey, jsonObject.toString()));
            }else {
                jsonObject.put("nodeAgentId", nodeAgent);
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void setBack_click(Context context) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
