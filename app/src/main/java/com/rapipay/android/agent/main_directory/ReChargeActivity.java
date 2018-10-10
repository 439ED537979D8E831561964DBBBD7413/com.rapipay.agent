package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

public class ReChargeActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {

    TextView input_amount, input_number;

    TextView select_operator;
    String operator_clicked, serviceType = "";
    ImageView btn_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_layout);
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        operator_clicked = getIntent().getStringExtra("OPERATOR");
        input_amount = (EditText) findViewById(R.id.input_amount);
        input_number = (EditText) findViewById(R.id.input_number);
        btn_contact = (ImageView) findViewById(R.id.btn_contact);
        if (operator_clicked.equalsIgnoreCase("DTH")) {
            btn_contact.setVisibility(View.GONE);
            serviceType = "DTH_RECHARGE";
            heading.setText("DTH RECHARGE");
            input_number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
//            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            p.weight = 1;
//            input_number.setLayoutParams(p);
        } else if (operator_clicked.equalsIgnoreCase("PRE")) {
            serviceType = "MOBILE_RECHARGE";
            heading.setText("PREPAID RECHARGE");
        } else if (operator_clicked.equalsIgnoreCase("POST")) {
            serviceType = "MOBILE_BILL_PAYMENT";
            heading.setText("POSTPAID BILL PAYMENT");
        }
        select_operator = (TextView) findViewById(R.id.select_operator);
        select_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String condition = "where " + RapipayDB.COLOMN_OPERATORVALUE + "='" + operator_clicked + "'";
                ArrayList<String> list_operator = db.getOperatorDetail(condition);
                customSpinner(select_operator, "Select Operator", list_operator);
            }
        });
//        String condition = "where " + RapipayDB.COLOMN_OPERATORVALUE + "='" + operator_clicked + "'";
//        list_operator = db.getOperatorDetail(condition);
//        if (list_operator.size() != 0) {
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                    android.R.layout.simple_spinner_item, list_operator);
//            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            select_operator.setAdapter(dataAdapter);
//        }
//        select_operator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                operator = list_operator.get(position);
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
            case R.id.btn_contact:
                loadIMEI();
                break;
            case R.id.btn_submit:
                if (operator_clicked.equalsIgnoreCase("DTH")) {
                    if (!ImageUtils.commonAccount(input_number.getText().toString(), 8, 15)) {
                        input_number.setError("Please enter valid mobile number");
                        input_number.requestFocus();
                    } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                        input_amount.setError("Please enter valid data");
                        input_amount.requestFocus();
                    } else if (select_operator.getText().toString().equalsIgnoreCase("Select Operator"))
                        select_operator.setError("Please enter mandatory field");
                    else
                        new AsyncPostMethod(WebConfig.RECHARGE_URL, reCharge_request().toString(), headerData, ReChargeActivity.this).execute();
                } else if (operator_clicked.equalsIgnoreCase("PRE") || operator_clicked.equalsIgnoreCase("POST")) {
                    if (!ImageUtils.commonNumber(input_number.getText().toString(), 10)) {
                        input_number.setError("Please enter valid mobile number");
                        input_number.requestFocus();
                    } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                        input_amount.setError("Please enter valid data");
                        input_amount.requestFocus();
                    } else if (select_operator.getText().toString().equalsIgnoreCase("Select Operator"))
                        select_operator.setError("Please enter mandatory field");
                    else
                        new AsyncPostMethod(WebConfig.RECHARGE_URL, reCharge_request().toString(), headerData, ReChargeActivity.this).execute();
                }
                break;
        }
    }

    public JSONObject reCharge_request() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", serviceType);
            jsonObject.put("requestType", "UBP_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "UBPC" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("serviceOperatorName", select_operator.getText().toString());
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
            if (object.getString("responseCode").equalsIgnoreCase("101")) {
                if (object.getString("serviceType").equalsIgnoreCase("MOBILE_RECHARGE")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("PREPAID RECHARGE", object, ReChargeActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "PREPAID RECHARGE", "", "Cannot generate receipt now please try later!", ReChargeActivity.this);
                        }
//                    customDialog_Common("KYCLAYOUTS", object, null, "PREPAID RECHARGE", null, object.getString("responseMessage"), ReChargeActivity.this);
//                    customDialog(object.getString("responseMessage"));
                }else if (object.getString("serviceType").equalsIgnoreCase("DTH_RECHARGE")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("DTH RECHARGE", object, ReChargeActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "DTH RECHARGE", "", "Cannot generate receipt now please try later!", ReChargeActivity.this);
                        }
//                    customDialog_Common("KYCLAYOUTS", object, null, "DTH RECHARGE", null, object.getString("responseMessage"), ReChargeActivity.this);
//                    customDialog(object.getString("responseMessage"));
                }else if (object.getString("serviceType").equalsIgnoreCase("MOBILE_BILL_PAYMENT")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("POSTPAID BILL PAYMENT", object, ReChargeActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "POSTPAID BILL PAYMENT", "", "Cannot generate receipt now please try later!", ReChargeActivity.this);
                        }
//                    customDialog_Common("KYCLAYOUTS", object, null, "POSTPAID BILL PAYMENT", null, object.getString("responseMessage"), ReChargeActivity.this);
//                    customDialog(object.getString("responseMessage"));
                }
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
//                        clear();
//                        dialog.dismiss();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

    private void clear() {
        input_amount.setText("");
        input_number.setText("");
        input_number.requestFocus();
        select_operator.setText("Select Operator");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                contactRead(data, input_number);
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

    @Override
    public void okClicked(String type, Object ob) {
        clear();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
