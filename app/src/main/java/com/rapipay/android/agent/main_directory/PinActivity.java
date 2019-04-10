package com.rapipay.android.agent.main_directory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.MasterClass;
import com.rapipay.android.agent.utils.RouteClass;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.PinEntryEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PinActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {

    String agentId, regTxnRefId, imeiNo, otpRefId, sessionRefNo, sessionKey;
    PinEntryEditText otppinView, confirmpinView, pinView;
    boolean flag_rapi = false;
    AppCompatButton btn_login;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinhandset_registration);
        initialize();
    }

    private void initialize() {
        findViewById(R.id.back_click).setVisibility(View.GONE);
        agentId = getIntent().getStringExtra("agentId");
        regTxnRefId = getIntent().getStringExtra("regTxnRefId");
        imeiNo = getIntent().getStringExtra("imeiNo");
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        otpRefId = getIntent().getStringExtra("otpRefId");
        sessionRefNo = getIntent().getStringExtra("sessionRefNo");
        sessionKey = getIntent().getStringExtra("sessionKey");
        TextView userid = (TextView) findViewById(R.id.userid);
        userid.setText("Agent RMN : " + agentId);
        pinView = (PinEntryEditText) findViewById(R.id.pinView);
        confirmpinView = (PinEntryEditText) findViewById(R.id.confirmpinView);
        otppinView = (PinEntryEditText) findViewById(R.id.otppinView);
        if (db != null)
            flag_rapi = db.getDetails_Rapi();
        else
            dbNull(PinActivity.this);
    }

    public JSONObject getJson_Validate() {
        JSONObject jsonObject = new JSONObject();
        if (!pinView.getText().toString().isEmpty() && !otppinView.getText().toString().isEmpty() && !confirmpinView.getText().toString().isEmpty() && pinView.getText().toString().equalsIgnoreCase(confirmpinView.getText().toString())) {
            try {
                jsonObject.put("serviceType", "ProcessHandsetRegistration");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("otp", otppinView.getText().toString());
                jsonObject.put("otpRefId", otpRefId);
                jsonObject.put("txnRefId", ImageUtils.miliSeconds());
                jsonObject.put("agentId", agentId);
                jsonObject.put("nodeAgentId", agentId);
                jsonObject.put("orgTxnRef", regTxnRefId);
                jsonObject.put("pin", pinView.getText().toString());
                jsonObject.put("imeiNo", imeiNo);
                jsonObject.put("sessionRefNo", sessionRefNo);
                jsonObject.put("deviceName", Build.MANUFACTURER);
                jsonObject.put("osType", "ANDROID");
                jsonObject.put("domainName",BuildConfig.DOMAINNAME);
                jsonObject.put("clientRequestIP",ImageUtils.ipAddress(PinActivity.this));
                jsonObject.put("checkSum", GenerateChecksum.checkSum(sessionKey, jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Text", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", PinActivity.this,getString(R.string.responseTimeOut)).execute();
                break;
        }
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("ProcessHandsetRegistration")) {
                    if (!flag_rapi) {
                        String insertSQL = "INSERT INTO " + RapipayDB.TABLE_NAME + "\n" +
                                "(" + RapipayDB.COLOMN_SESSION + "," + RapipayDB.COLOMN_APIKEY + "," + RapipayDB.COLOMN_IMEI + "," + RapipayDB.COLOMN_MOBILENO + "," + RapipayDB.COLOMN_PRTXNID + "," + RapipayDB.COLOMN_SESSIONREFNO + "," + RapipayDB.COLOMN_AGENTNAME + ")\n" +
                                "VALUES \n" +
                                "( ?, ?, ?, ?, ?, ?,?);";
                        SQLiteDatabase dba = db.getWritableDatabase();
                        dba.execSQL(insertSQL, new String[]{object.getString("sessionId"), object.getString("agentApiKey"), imeiNo, agentId, object.getString("txnRefId"), object.getString("sessionRefNo"), object.getString("agentName")});
                    } else {
                        list = db.getDetails();
                        SQLiteDatabase dba = db.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(RapipayDB.COLOMN_SESSION, object.getString("sessionId"));
                        contentValues.put(RapipayDB.COLOMN_APIKEY, object.getString("agentApiKey"));
                        contentValues.put(RapipayDB.COLOMN_PRTXNID, object.getString("txnRefId"));
                        contentValues.put(RapipayDB.COLOMN_SESSIONREFNO, object.getString("sessionRefNo"));
                        String whereClause = RapipayDB.COLOMN_MOBILENO + "=?";
                        String whereArgs[] = {list.get(0).getMobilno()};
                        dba.update(RapipayDB.TABLE_NAME, contentValues, whereClause, whereArgs);
                    }
                    callBankDetails();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_MASTER_DATA")) {
                    if (new MasterClass().getMasterData(object, db))
                        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge().toString(), headerData, PinActivity.this,getString(R.string.responseTimeOut)).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("UPDATE_DOWNLAOD_DATA_STATUS")) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getWLDetails().toString(), headerData, PinActivity.this,getString(R.string.responseTimeOut)).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("WL_DOMAIN_DETAILS")) {
                    if (object.has("invoiceLogo"))
                        insertImages("invoiceLogo", object);
                    if (object.has("loginLogo"))
                        insertImages("loginLogo", object);
                    if (object.has("leftLogo"))
                        insertImages("leftLogo", object);
                    customDialog_Common("KYCLAYOUTS", null, null, "Pin Registration", null, "Pin Registration Successful, Do you want to proceed ?", PinActivity.this);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertImages(String imageName, JSONObject object) {
        try {
            SQLiteDatabase dba = db.getWritableDatabase();
            ContentValues values = new ContentValues();
            String wlimageName = imageName + ".jpg";
            values.put(RapipayDB.IMAGE_PATH_WL, byteConvert(object.getString(imageName)));
            values.put(RapipayDB.IMAGE_NAME, wlimageName);
            dba.insert(RapipayDB.TABLE_IMAGES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject acknowledge() {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "UPDATE_DOWNLAOD_DATA_STATUS");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("DataDownloadFlag", "Y");
                jsonObject.put("agentMobile", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getSessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Blank Value", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject getWLDetails() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "WL_DOMAIN_DETAILS");
                jsonObject.put("requestType", "HANDSET_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("timeStamp", format.format(date));
                jsonObject.put("appType", BuildConfig.USERTYPE);
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Blank Value", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }


    private void callBankDetails() {
        boolean bank_flag = db.getDetails_Bank();
        if (bank_flag == false) {
            new AsyncPostMethod(WebConfig.CommonReport, getMaster_Validate().toString(), headerData, PinActivity.this,getString(R.string.responseTimeOut)).execute();
        }
    }

    public JSONObject getMaster_Validate() {
        list = db.getDetails();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_MASTER_DATA");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getSessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Blank Value", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("SESSIONEXPIRRED") || type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
        else if (type.equalsIgnoreCase("KYCLAYOUTS")) {
            localStorage.setActivityState(LocalStorage.ROUTESTATE, "PINVERIFIED");
            new RouteClass(PinActivity.this, null, null, localStorage, "KYCLAYOUTS");
        }
        finish();
    }

    @Override
    public void cancelClicked(String type, Object ob) {
        pinView.setText("");
        confirmpinView.setText("");
        otppinView.setText("");

    }

    @Override
    public void onBackPressed() {
        customDialog_Common("SESSIONEXPIRRED", null, null, "Session Expired", null, "Your current session will get expired.", PinActivity.this);
    }
}
