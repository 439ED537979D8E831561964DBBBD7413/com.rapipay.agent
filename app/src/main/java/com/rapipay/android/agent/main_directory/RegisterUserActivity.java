package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

public class RegisterUserActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener, CustomInterface {
    final private static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    TextView input_name, input_number, input_address, input_email, input_code;
    TextView select_state;
    String TYPE, mobileNo;
    static String byteBase64;
    int scan_check=0;
    static Bitmap bitmap_trans=null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_activity);
        TYPE = getIntent().getStringExtra("type");
        mobileNo = getIntent().getStringExtra("mobileNo");
        loadIMEI();
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Add Network Partner");
        input_number = (TextView) findViewById(R.id.input_number);
        input_address = (TextView) findViewById(R.id.input_address);
        input_email = (TextView) findViewById(R.id.input_email);
        input_code = (TextView) findViewById(R.id.input_code);
        input_name = (TextView) findViewById(R.id.input_name);
        select_state = (TextView) findViewById(R.id.select_state);
        select_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_state = db.getState_Details();
                customSpinner(select_state, "Select State", list_state);
            }
        });
        if (!mobileNo.isEmpty())
            input_number.setText(mobileNo);
//        if (list_state.size() != 0) {
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                    android.R.layout.simple_spinner_item, list_state);
//            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            select_state.setAdapter(dataAdapter);
//        }
//        select_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0)
//                    state = list_state.get(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_fund:
                if (!ImageUtils.commonRegex(input_name.getText().toString(), 150, " ")) {
                    input_name.setError("Please enter valid data");
                    input_name.requestFocus();
                } else if (!ImageUtils.commonRegex(input_code.getText().toString(), 150, "0-9 .&") && !TYPE.equalsIgnoreCase("internal")) {
                        input_code.setError("Please enter valid data");
                        input_code.requestFocus();
                } else if (input_address.getText().toString().isEmpty()) {
                    input_address.setError("Please enter valid data");
                    input_address.requestFocus();
                } else if (select_state.getText().toString().equalsIgnoreCase("Select State")) {
                    select_state.setError("Please enter valid data");
                    select_state.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString()).matches() && !TYPE.equalsIgnoreCase("internal")) {
                        input_email.setError("Please enter valid data");
                        input_email.requestFocus();
                } else if (!ImageUtils.commonNumber(input_number.getText().toString(), 10)) {
                    input_number.setError("Please enter valid data");
                    input_number.requestFocus();
                } else {
                    if (TYPE.equalsIgnoreCase("internal")) {
                        try {
                            Intent intent = new Intent(RegisterUserActivity.this, WebViewClientActivity.class);
                            intent.putExtra("mobileNo", mobileNo);
                            String base64 = input_name.getText().toString() + "~" + input_email.getText().toString().trim() + "~" + input_code.getText().toString().trim() + "~" + input_address.getText().toString() + "~" + select_state.getText().toString()+ "~" +scan_check;
                            byte[] bytes = base64.getBytes("utf-8");
                            String imageEncoded = Base64.encodeToString(bytes, Base64.DEFAULT);
                            intent.putExtra("base64", imageEncoded);
                            intent.putExtra("parentId", list.get(0).getMobilno());
                            intent.putExtra("sessionKey", list.get(0).getPinsession());
                            intent.putExtra("sessionRefNo", list.get(0).getAftersessionRefNo());
                            intent.putExtra("nodeAgent", list.get(0).getMobilno());
                            intent.putExtra("type", "internal");
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        new AsyncPostMethod(WebConfig.UAT, request_user().toString(), headerData, RegisterUserActivity.this).execute();
                }
                break;
            case R.id.btn_scan_submit:
                bitmap_trans = null;
                Intent intent = new Intent(RegisterUserActivity.this, BarcodeActivity.class);
                intent.putExtra("type", "inside");
                startActivityForResult(intent, 1);
//                startActivityForResult(new Intent(RegisterUserActivity.this, BarcodeActivity.class), 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                JSONObject jsonObj = null;
                try {
                    String requiredValue = data.getStringExtra("Key");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap_trans.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    byteBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    jsonObj = XML.toJSONObject(requiredValue);
                    JSONObject jsonObject = jsonObj.getJSONObject("PrintLetterBarcodeData");
                    parseJson(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseJson(JSONObject object) {
        try {
            scan_check = 2;
            if (object.has("name"))
                input_name.setText(object.getString("name"));
            if (object.has("house") && object.has("street") && object.has("lm") && object.has("vtc") && object.has("dist")) {
                String add = object.getString("house") + ", " + object.getString("street") + ", " + object.getString("lm") + ", " + object.getString("vtc") + ", " + object.getString("dist");
                input_address.setText(add);
            }
            if (object.has("house") && object.has("street") && object.has("loc") && object.has("vtc") && object.has("dist")) {
                String add = object.getString("house") + ", " + object.getString("street") + ", " + object.getString("loc") + ", " + object.getString("vtc") + ", " + object.getString("dist");
                input_address.setText(add);
            }
            if (object.has("_house") && object.has("_street") && object.has("_lm") && object.has("_vtc") && object.has("_dist")) {
                String add = object.getString("_house") + ", " + object.getString("_street") + ", " + object.getString("_lm") + ", " + object.getString("_vtc") + ", " + object.getString("_dist");
                input_address.setText(add);
            }
            if (object.has("_loc") && object.has("vtc") && object.has("dist")) {
                String add = object.getString("_loc") + ", " + object.getString("vtc") + ", " + object.getString("dist");
                input_address.setText(add);
            }
            if (object.has("loc") && object.has("vtc") && object.has("dist")) {
                String add = object.getString("loc") + ", " + object.getString("vtc") + ", " + object.getString("dist");
                input_address.setText(add);
            }
            if (object.has("_loc") && object.has("_vtc") && object.has("_dist")) {
                String add = object.getString("_loc") + ", " + object.getString("vtc") + ", " + object.getString("dist");
                input_address.setText(add);
            }
            if (object.has("_lm") && object.has("_loc") && object.has("_vtc") && object.has("_dist")) {
                String add = object.getString("_lm") + ", " + object.getString("_loc") + ", " + object.getString("vtc") + ", " + object.getString("dist");
                input_address.setText(add);
            }
            if (object.has("state"))
                select_state.setText(object.getString("state"));
            if (object.has("_state"))
                select_state.setText(object.getString("_state"));
            if (input_name.getText().toString().isEmpty() || input_address.getText().toString().isEmpty() || select_state.getText().toString().isEmpty())
                Toast.makeText(RegisterUserActivity.this, "Please fill entry manually", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("B2BTempUserRequest")) {
                    try {
                        Intent intent = new Intent(RegisterUserActivity.this, WebViewClientActivity.class);
                        intent.putExtra("mobileNo", input_number.getText().toString());
                        String base64 = input_name.getText().toString() + "~" + input_email.getText().toString().trim() + "~" + input_code.getText().toString().trim() + "~" + input_address.getText().toString() + "~" + select_state.getText().toString()+ "~" +scan_check;
                        byte[] bytes = base64.getBytes("utf-8");
                        String imageEncoded = Base64.encodeToString(bytes, Base64.DEFAULT);
                        intent.putExtra("base64", imageEncoded);
                        intent.putExtra("parentId", list.get(0).getMobilno());
                        intent.putExtra("sessionKey", list.get(0).getPinsession());
                        intent.putExtra("sessionRefNo", list.get(0).getAftersessionRefNo());
                        intent.putExtra("nodeAgent", list.get(0).getMobilno());
                        intent.putExtra("type", "outside");
                        startActivity(intent);
                        clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    customDialog_Common("KYCLAYOUTS", object, null, "User Registration", null, object.getString("responseMessage"), RegisterUserActivity.this);
//                    customDialog(object.getString("responseMessage"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    private void customDialog(String msg) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.app_name);
//        //Setting message manually and performing action on button click
//        builder.setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        clear();
//                        dialog.dismiss();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

    private void clear() {
        input_number.setText("");
        input_name.setText("");
        input_code.setText("");
        input_email.setText("");
        input_address.setText("");
        select_state.setText("Select State");
    }

    public JSONObject request_user() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "B2BTempUserRequest");
            jsonObject.put("requestType", "HandSet_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRefId", "B2BT" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("companyName", input_code.getText().toString());
            jsonObject.put("eMail", input_email.getText().toString());
            jsonObject.put("address", input_address.getText().toString());
            jsonObject.put("state", select_state.getText().toString());
            jsonObject.put("password", String.valueOf(generatePin()));
            jsonObject.put("firstName", input_name.getText().toString());
            jsonObject.put("mobileNo", input_number.getText().toString());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public int generatePin() throws Exception {
        Random generator = new Random();
        generator.setSeed(System.currentTimeMillis());

        int num = generator.nextInt(99999) + 99999;
        if (num < 100000 || num > 999999) {
            num = generator.nextInt(99999) + 99999;
            if (num < 100000 || num > 999999) {
                throw new Exception("Unable to generate PIN at this time..");
            }
        }
        return num;
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void okClicked(String type, Object ob) {
        clear();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(RegisterUserActivity.this, Manifest.permission.CAMERA)
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterUserActivity.this,
                Manifest.permission.CAMERA)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(RegisterUserActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(RegisterUserActivity.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        new AlertDialog.Builder(RegisterUserActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(RegisterUserActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            selectImage();
        }
    }
}
