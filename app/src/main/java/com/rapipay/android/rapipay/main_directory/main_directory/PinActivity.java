package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Database.RapipayDB;
import com.rapipay.android.rapipay.main_directory.Model.BankDetailsPozo;
import com.rapipay.android.rapipay.main_directory.Model.RapiPayPozo;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.MasterClass;
import com.rapipay.android.rapipay.main_directory.utils.RouteClass;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;
import com.rapipay.android.rapipay.main_directory.view.PinEntryEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PinActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler {

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
        agentId = getIntent().getStringExtra("agentId");
        regTxnRefId = getIntent().getStringExtra("regTxnRefId");
        imeiNo = getIntent().getStringExtra("imeiNo");
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        otpRefId = getIntent().getStringExtra("otpRefId");
        sessionRefNo = getIntent().getStringExtra("sessionRefNo");
        sessionKey = getIntent().getStringExtra("sessionKey");
        TextView userid = (TextView) findViewById(R.id.userid);
        userid.setText(agentId);
        pinView = (PinEntryEditText) findViewById(R.id.pinView);
        confirmpinView = (PinEntryEditText) findViewById(R.id.confirmpinView);
        otppinView = (PinEntryEditText) findViewById(R.id.otppinView);
        flag_rapi = db.getDetails_Rapi();
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
                jsonObject.put("txnRefId", format.format(date));
                jsonObject.put("agentId", agentId);
                jsonObject.put("nodeAgentId", agentId);
                jsonObject.put("orgTxnRef", regTxnRefId);
                jsonObject.put("pin", pinView.getText().toString());
                jsonObject.put("imeiNo", imeiNo);
                jsonObject.put("sessionRefNo", sessionRefNo);
                jsonObject.put("deviceName", Build.MANUFACTURER);
                jsonObject.put("osType", "ANDROID");
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
                new AsyncPostMethod(WebConfig.UAT, getJson_Validate().toString(), "", PinActivity.this).execute();
                break;
        }
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("ProcessHandsetRegistration")) {
                    if (flag_rapi == false) {
                        String insertSQL = "INSERT INTO " + RapipayDB.TABLE_NAME + "\n" +
                                "(" + RapipayDB.COLOMN_SESSION + "," + RapipayDB.COLOMN_APIKEY + "," + RapipayDB.COLOMN_IMEI + "," + RapipayDB.COLOMN_MOBILENO + "," + RapipayDB.COLOMN_PRTXNID + "," + RapipayDB.COLOMN_SESSIONREFNO + ")\n" +
                                "VALUES \n" +
                                "( ?, ?, ?, ?, ?, ?);";
                        SQLiteDatabase dba = db.getWritableDatabase();
                        dba.execSQL(insertSQL, new String[]{object.getString("sessionId"), object.getString("agentApiKey"), imeiNo, agentId, object.getString("txnRefId"), object.getString("sessionRefNo")});
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
                        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge().toString(), headerData, PinActivity.this).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("UPDATE_DOWNLAOD_DATA_STATUS")) {
                    customDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject acknowledge() {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "UPDATE_DOWNLAOD_DATA_STATUS");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", "status"+format.format(date));
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


    private void callBankDetails() {
        boolean bank_flag = db.getDetails_Bank();
        if (bank_flag == false) {
            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getMaster_Validate().toString(), headerData, PinActivity.this).execute();
        }
    }

    public JSONObject getMaster_Validate() {
        list = db.getDetails();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_MASTER_DATA");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", "master"+format.format(date));
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

    private void customDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Pin Registration Successful, Do you want to proceed ?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new RouteClass(PinActivity.this, null, null, localStorage, null);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void chechStat(String object) {

    }
}
