package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.HeaderePozo;
import com.rapipay.android.rapipay.main_directory.adapter.SpinnerAdapters;
import com.rapipay.android.rapipay.main_directory.adapter.StateAdapter;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class RegisterUserActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener{

    TextView input_name, input_number, input_address, input_email, input_code;
    Spinner  select_state;
    ArrayList<String> list_state;
    String state="";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_activity);
        initialize();
    }

    private void initialize(){
        input_number = (TextView) findViewById(R.id.input_number);
        input_address = (TextView) findViewById(R.id.input_address);
        input_email = (TextView) findViewById(R.id.input_email);
        input_code = (TextView) findViewById(R.id.input_code);
        input_name = (TextView) findViewById(R.id.input_name);
        select_state = (Spinner) findViewById(R.id.select_state);
        list_state = db.getState_Details();
//        if (list_state.size() != 0)
//            select_state.setAdapter(new StateAdapter(getApplicationContext(), list_state));
        if (list_state.size() != 0){
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list_state);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_state.setAdapter(dataAdapter);
        }
        select_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = list_state.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_fund:
                if ( !state.isEmpty() && !input_name.getText().toString().isEmpty() && !input_number.getText().toString().isEmpty() && !input_email.getText().toString().isEmpty() && !input_code.getText().toString().isEmpty() && !input_address.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.UAT, request_user().toString(), headerData, RegisterUserActivity.this).execute();
                break;
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
                    customDialog(object.getString("responseMessage"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void customDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clear();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void clear(){
        input_number.setText("");
        input_name.setText("");
        input_code.setText("");
        input_email.setText("");
        input_address.setText("");
    }
    public JSONObject request_user() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "B2BTempUserRequest");
            jsonObject.put("requestType", "HandSet_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRefId", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("companyName", input_code.getText().toString());
            jsonObject.put("eMail", input_email.getText().toString());
            jsonObject.put("address", input_address.getText().toString());
            jsonObject.put("state", state);
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
}
