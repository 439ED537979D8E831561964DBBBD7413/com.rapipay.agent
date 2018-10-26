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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

//import com.crashlytics.android.Crashlytics;
import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;
import com.rapipay.android.agent.utils.WebConfig;

import java.util.ArrayList;

public class LoginScreenActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface, VersionListener {

    final private static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    EditText input_user;
    TextInputEditText input_password;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    AppCompatButton btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        loadIMEI();
    }

    private void initialize() {
        findViewById(R.id.back_click).setVisibility(View.GONE);
        input_password = (TextInputEditText) findViewById(R.id.input_password);
        input_user = (EditText) findViewById(R.id.input_user);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }

    public JSONObject getJson_Validate() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "ValidCredentialService");
            jsonObject.put("requestType", "handset_CHannel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRefId", "KYCPROCESS" + tsLong.toString());
            jsonObject.put("agentId", input_user.getText().toString());
            jsonObject.put("nodeAgentId", input_user.getText().toString());
            jsonObject.put("password", input_password.getText().toString());
            jsonObject.put("imeiNo", imei);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(imei, jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
//            checkAndRequestPermissions();
            requestReadPhoneStatePermission();
        } else {

            // READ_PHONE_STATE permission is already been granted.
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
                            new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 6 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
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
            localStorage.setActivityState(LocalStorage.EMI,imei);
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
                    loadVersion(imei);

//                    Crashlytics.getInstance().crash();

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
            }else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                new RouteClass(this, object, input_user.getText().toString(), localStorage, "PINENTERED");//
//                Intent intent = new Intent(LoginScreenActivity.this, PinActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("agentId", input_user.getText().toString());
//                intent.putExtra("regTxnRefId", object.getString("txnRefId"));
//                intent.putExtra("imeiNo", object.getString("imeiNo"));
//                intent.putExtra("otpRefId", object.getString("otpRefId"));
//                intent.putExtra("sessionRefNo", object.getString("sessionRefNo"));
//                intent.putExtra("sessionKey", object.getString("sessionKey"));
//                startActivity(intent);
//            } else if (object.getString("responseCode").equalsIgnoreCase("75059")) {
//                if (object.getString("kycType").equalsIgnoreCase("2")) {
//                    new RouteClass(this, object, input_user.getText().toString(), localStorage, "KYCENTERED");
////                    Intent intent = new Intent(this, WebViewClientActivity.class);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    intent.putExtra("mobileNo", input_user.getText().toString());
////                    intent.putExtra("parentId", object.getString("parentID"));
////                    intent.putExtra("sessionKey", object.getString("sessionKey"));
////                    intent.putExtra("sessionRefNo", object.getString("sessionRefNo"));
////                    startActivity(intent);
//                } else if (object.getString("kycType").equalsIgnoreCase("1")) {
//                    //Manual KYC
//                } else
//                    Toast.makeText(this, "KYC Mode is not Available", Toast.LENGTH_SHORT).show();
            } else if (object.getString("responseCode").equalsIgnoreCase("75115")) {
                customDialog_Common("KYCLAYOUTS", null, null, "RapiPay Login Failed", null, object.getString("responseMessage"), LoginScreenActivity.this);
//                customDialog(object.getString("responseMessage"));
            } else if (object.getString("responseCode").equalsIgnoreCase("75115")) {
                customDialog_Common("KYCLAYOUTS", null, null, "RapiPay Login Failed", null, object.getString("responseMessage"), LoginScreenActivity.this);
//                customDialog(object.getString("responseMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void customDialog(String msg) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.app_name);
//        //Setting message manually and performing action on button click
//        builder.setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

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
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() != null)
                if (list.get(i).getName().equalsIgnoreCase("PROD")) {
                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        String version = pInfo.versionName;
                        if (!version.equalsIgnoreCase(list.get(i + 1).getValue())) {
                            customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", LoginScreenActivity.this);
                        } else {
                            new AsyncPostMethod(WebConfig.UAT, getJson_Validate().toString(), "", LoginScreenActivity.this).execute();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}

