package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.provider.ContactsContract.Contacts;
import android.widget.Toast;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Database.RapipayDB;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReChargeActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler {

    TextView input_amount, input_number;
    private static final int CONTACT_PICKER_RESULT = 1;
    Spinner select_operator;
    ArrayList<String> list_operator;
    String operator = "", operator_clicked, serviceType = "";
    AppCompatButton btn_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_layout);
        initialize();
    }

    private void initialize() {
        operator_clicked = getIntent().getStringExtra("OPERATOR");
        input_amount = (EditText) findViewById(R.id.input_amount);
        input_number = (EditText) findViewById(R.id.input_number);
        btn_contact = (AppCompatButton) findViewById(R.id.btn_contact);
        if (operator_clicked.equalsIgnoreCase("DTH")) {
            btn_contact.setVisibility(View.GONE);
            serviceType = "DTH_RECHARGE";
//            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            p.weight = 1;
//            input_number.setLayoutParams(p);
        } else if (operator_clicked.equalsIgnoreCase("PRE"))
            serviceType = "MOBILE_RECHARGE";
        else if (operator_clicked.equalsIgnoreCase("POST"))
            serviceType = "MOBILE_BILL_PAYMENT";
        select_operator = (Spinner) findViewById(R.id.select_operator);
        String condition = "where " + RapipayDB.COLOMN_OPERATORVALUE + "='" + operator_clicked + "'";
        list_operator = db.getOperatorDetail(condition);
        if (list_operator.size() != 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list_operator);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_operator.setAdapter(dataAdapter);
        }
        select_operator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operator = list_operator.get(position);
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
            case R.id.btn_contact:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
                break;
            case R.id.btn_submit:
                if (!operator.isEmpty() && !input_amount.getText().toString().isEmpty() && !input_number.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.RECHARGE_URL, reCharge_request().toString(), headerData, ReChargeActivity.this).execute();
                break;
        }
    }

    public JSONObject reCharge_request() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", serviceType);
            jsonObject.put("requestType", "UBP_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("serviceOperatorName", operator);
            jsonObject.put("rechargeType", operator_clicked);
            jsonObject.put("serviceName", "RECHARGE_SERVICE");
            jsonObject.put("rechargeAmount", input_amount.getText().toString());
            jsonObject.put("amount", input_amount.getText().toString());
            jsonObject.put("mobileNumber", input_number.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("MOBILE_RECHARGE")) {
                    customDialog(object.getString("responseMessage"));
                } else if (object.getString("serviceType").equalsIgnoreCase("DTH_RECHARGE")) {
                    customDialog(object.getString("responseMessage"));
                } else if (object.getString("serviceType").equalsIgnoreCase("MOBILE_BILL_PAYMENT")) {
                    customDialog(object.getString("responseMessage"));
                }
            }
        } catch (Exception e) {
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

    private void clear() {
        input_number.setText("");
        input_amount.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (Integer.valueOf(hasNumber) == 1) {
                        Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (numbers.moveToNext()) {
                            input_number.setText(numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void chechStat(String object) {

    }
}
