package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginScreenActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface, VersionListener {

    final private static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    EditText input_user;
    TextInputEditText input_password;
    AppCompatButton btn_login;
    ImageView image_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        loadIMEI();
    }

    private void initialize() {
        image_app = (ImageView) findViewById(R.id.image_app);
        if (BuildConfig.APPTYPE == 1 || BuildConfig.APPTYPE == 3)
            image_app.setImageDrawable(getResources().getDrawable(R.drawable.rapipay_agent));
        if (BuildConfig.APPTYPE == 2)
            image_app.setImageDrawable(getResources().getDrawable(R.drawable.rapipay_parter));
        findViewById(R.id.back_click).setVisibility(View.GONE);
        input_password = (TextInputEditText) findViewById(R.id.input_password);
        input_user = (EditText) findViewById(R.id.input_user);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }

    public JSONObject getJson_Validate() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "ValidCredentialService");
            jsonObject.put("requestType", "handset_CHannel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRefId", ImageUtils.miliSeconds());
            jsonObject.put("agentId", input_user.getText().toString());
            jsonObject.put("nodeAgentId", input_user.getText().toString());
//            jsonObject.put("password", ImageUtils.encodeSHA_256(input_password.getText().toString()));
            jsonObject.put("password", input_password.getText().toString());
            jsonObject.put("imeiNo", imei);
            jsonObject.put("domainName", BuildConfig.DOMAINNAME);
            jsonObject.put("clientRequestIP", ImageUtils.ipAddress(LoginScreenActivity.this));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(imei, jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public void loadIMEI() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestReadPhoneStatePermission();
        } else {
            doPermissionGrantedStuffs();
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(LoginScreenActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 7 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED && grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                doPermissionGrantedStuffs();
            } else {
                alertPerm(getString(R.string.permissions_not_granted_read_phone_state), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadIMEI();
                    }
                });
            }
        }
    }

    private void alertPerm(String msg, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
            localStorage.setActivityState(LocalStorage.EMI, imei);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (input_user.getText().toString().length() != 10)
                    input_user.setError("Please enter mandatory field");
                else if (input_password.getText().toString().isEmpty())
                    input_password.setError("Please enter mandatory field");
                else
//                    FirebaseInstanceId.getInstance().getInstanceId()
//                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                                    if (!task.isSuccessful()) {
//                                        Log.w(TAG, "getInstanceId failed", task.getException());
//                                        return;
//                                    }
//
//                                    // Get new Instance ID token
//                                    String token = task.getResult().getToken();
//
//                                    // Log and toast
//                                    String msg = getString(R.string.msg_token_fmt, token);
//                                    Log.d(TAG, msg);
//                                    Toast.makeText(LoginScreenActivity.this, msg, Toast.LENGTH_SHORT).show();
//                                }
//                            });

                    loadVersion(imei);
                break;
        }
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("serviceType").equalsIgnoreCase("APP_LIVE_STATUS")) {
                if (object.has("headerList")) {
                    JSONArray array = object.getJSONArray("headerList");
                    versionDetails(array, LoginScreenActivity.this);
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                new RouteClass(this, object, input_user.getText().toString(), localStorage, "PINENTERED");
            } else if (object.getString("responseCode").equalsIgnoreCase("75115")) {
                customDialog_Common("KYCLAYOUTS", null, null, "RapiPay Login Failed", null, object.getString("responseMessage"), LoginScreenActivity.this);
            } else if (object.getString("responseCode").equalsIgnoreCase("75115")) {
                customDialog_Common("KYCLAYOUTS", null, null, "RapiPay Login Failed", null, object.getString("responseMessage"), LoginScreenActivity.this);
            } else if (object.getString("responseCode").equalsIgnoreCase("75077")) {
                customDialog_Common("KYCLAYOUTS", null, null, "RapiPay Login Failed", null, object.getString("responseMessage"), LoginScreenActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chechStat(String object) {
        customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object, LoginScreenActivity.this);
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("KYCLAYOUTS")) {
            input_user.setText("");
            input_password.setText("");
        } else if (type.equalsIgnoreCase("KYCLAYOUTSS")) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(webIntent);
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    @Override
    public void checkVersion(ArrayList<VersionPozo> list) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() != null)
                if (list.get(i).getName().equalsIgnoreCase("PROD")) {
                    stringArrayList.add(list.get(i + 1).getValue());
//                    try {
//                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                        String version = pInfo.versionName;
//                        if (!version.equalsIgnoreCase(list.get(i + 1).getValue())) {
//                            customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", LoginScreenActivity.this);
//                        } else {
//                            new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", LoginScreenActivity.this, getString(R.string.responseTimeOut)).execute();
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
                } else if (list.get(i).getName().equalsIgnoreCase("APP_UPDATE_ST")) {
                    stringArrayList.add(list.get(i + 1).getValue());
//                    try {
//                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                        String version = pInfo.versionName;
//                        if (("F").equalsIgnoreCase(list.get(i + 1).getValue())) {
//                            customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", LoginScreenActivity.this);
//                        } else {
//                            new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", LoginScreenActivity.this, getString(R.string.responseTimeOut)).execute();
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
                }
        }
        if (stringArrayList.size() != 0) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                if (Double.valueOf(version)>=Double.valueOf(stringArrayList.get(0)) && (stringArrayList.get(1).equalsIgnoreCase("F") || stringArrayList.get(1).equalsIgnoreCase("N"))) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", LoginScreenActivity.this, getString(R.string.responseTimeOut)).execute();
                } else if (Double.valueOf(version)<Double.valueOf(stringArrayList.get(0)) && stringArrayList.get(1).equalsIgnoreCase("F")) {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", LoginScreenActivity.this);
                } else if (Double.valueOf(stringArrayList.get(0))!=Double.valueOf(version) && stringArrayList.get(1).equalsIgnoreCase("N")) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", LoginScreenActivity.this, getString(R.string.responseTimeOut)).execute();
                } else {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", LoginScreenActivity.this);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

