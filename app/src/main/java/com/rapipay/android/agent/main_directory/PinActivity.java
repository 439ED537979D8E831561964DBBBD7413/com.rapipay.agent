package com.rapipay.android.agent.main_directory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

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
        if (dbRealm != null)
            flag_rapi = dbRealm.getDetails_Rapi();
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
                jsonObject.put("domainName", BuildConfig.DOMAINNAME);
                jsonObject.put("clientRequestIP", ImageUtils.ipAddress(PinActivity.this));
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
                btn_login.setClickable(false);
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", PinActivity.this, getString(R.string.responseTimeOut)).execute();
                break;
        }
    }
    RapiPayPozo rapiPayPozo;
    @Override
    public void chechStatus(final JSONObject object) {
        try {
            Log.e("GET_MASTER_DATA",object+"");
           rapiPayPozo = new RapiPayPozo();
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("ProcessHandsetRegistration")) {
                    if (!flag_rapi) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    rapiPayPozo.setSession(object.getString("sessionId"));
                                    rapiPayPozo.setApikey(object.getString("agentApiKey"));
                                    rapiPayPozo.setImei(imeiNo);
                                    rapiPayPozo.setMobilno(agentId);
                                    rapiPayPozo.setTxnRefId(object.getString("txnRefId"));
                                    rapiPayPozo.setSessionRefNo(object.getString("sessionRefNo"));
                                    rapiPayPozo.setAgentName(object.getString("agentName"));
                                    realm.copyToRealm(rapiPayPozo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        list = dbRealm.getDetails();
                        String whereArgs1 = list.get(0).getMobilno();
                        final RapiPayPozo pinPozo1 = realm.where(RapiPayPozo.class).equalTo("mobilno", whereArgs1).findFirst();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    pinPozo1.setSession(object.getString("sessionId"));
                                    pinPozo1.setApikey(object.getString("agentApiKey"));
                                    pinPozo1.setTxnRefId(object.getString("txnRefId"));
                                    pinPozo1.setSessionRefNo(object.getString("sessionRefNo"));
                                    pinPozo1.setAgentName(object.getString("agentName"));
                                    realm.copyToRealmOrUpdate(pinPozo1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    callBankDetails();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_MASTER_DATA")) {
                    if (new MasterClass().getMasterData(object, dbRealm, realm))
                        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge().toString(), headerData, PinActivity.this, getString(R.string.responseTimeOut)).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("UPDATE_DOWNLAOD_DATA_STATUS")) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getWLDetails().toString(), headerData, PinActivity.this, getString(R.string.responseTimeOut)).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("WL_DOMAIN_DETAILS")) {
                    if (object.has("invoiceLogo"))
                        insertImages("invoiceLogo", object);
                    if (object.has("loginLogo"))
                        insertImages("loginLogo", object);
                    if (object.has("leftLogo"))
                        insertImages("leftLogo", object);
                    localStorage.setActivityState(LocalStorage.ROUTESTATE, "PINVERIFIED");
                    customDialog_Common("KYCLAYOUTS", null, null, "Pin Registration", null, "Pin Registration Successful, Do you want to proceed ?", PinActivity.this);
                }
                btn_login.setClickable(true);
            } else {
                pinView.setText("");
                confirmpinView.setText("");
                otppinView.setText("");
                btn_login.setClickable(true);
                responseMSg(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertImages(final String imageName, final JSONObject object) {
        try {
            final ImagePozo imagePozo = new ImagePozo();
            final String wlimageName = imageName + ".jpg";
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        imagePozo.setImagePath(byteConvert(object.getString(imageName)));
                        imagePozo.setImageName(wlimageName);
                        realm.copyToRealm(imagePozo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
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
        boolean bank_flag = dbRealm.getDetails_Bank();
        if (bank_flag == false) {
            new AsyncPostMethod(WebConfig.CommonReport, getMaster_Validate().toString(), headerData, PinActivity.this, getString(R.string.responseTimeOut)).execute();
        }
    }

    public JSONObject getMaster_Validate() {
        list = dbRealm.getDetails();
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
        deleteTables();
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
        btn_login.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        customDialog_Common("SESSIONEXPIRRED", null, null, "Session Expired", null, "Your current session will get expired.", PinActivity.this);
    }
}
