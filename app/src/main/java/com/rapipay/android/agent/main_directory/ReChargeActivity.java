package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.TbRechargePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReChargeActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {

    TextView input_amount, input_number;
    TextView select_operator;
    String operator_clicked, serviceType = "";
    ImageView btn_contact;
    RadioGroup radioGroup;
    RadioButton prepaid, postpaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_layout);
        initialize();
    }

    private void initialize() {
        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        heading = (TextView) findViewById(R.id.toolbar_title);
        operator_clicked = getIntent().getStringExtra("OPERATOR");
        input_amount = (EditText) findViewById(R.id.input_amount);
        input_number = (EditText) findViewById(R.id.input_number);
        btn_contact = (ImageView) findViewById(R.id.btn_contact);
        final TextView input_text = (TextView) findViewById(R.id.input_texts);
        newtpin = (EditText) findViewById(R.id.newtpin);
        if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y"))
            newtpin.setVisibility(View.VISIBLE);
        if (operator_clicked.equalsIgnoreCase("DTH")) {
            btn_contact.setVisibility(View.GONE);
            serviceType = "DTH_RECHARGE";
            heading.setText("DTH RECHARGE");
            input_number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        } else if (operator_clicked.equalsIgnoreCase("PRE")) {
            serviceType = "MOBILE_RECHARGE";
            heading.setText("PREPAID RECHARGE");
        } else if (operator_clicked.equalsIgnoreCase("POST")) {
            serviceType = "MOBILE_BILL_PAYMENT";
            heading.setText("POSTPAID BILL PAYMENT");
        } else {
            radioGroup.setVisibility(View.VISIBLE);
            heading.setText("MOBILE RECHARGE");
        }
        select_operator = (TextView) findViewById(R.id.select_operator);
        select_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operator_clicked.equalsIgnoreCase("PRE") || operator_clicked.equalsIgnoreCase("POST") || operator_clicked.equalsIgnoreCase("DTH")) {
                    String condition = operator_clicked;
                    ArrayList<String> list_operator = new ArrayList<>();
                    ArrayList<TbRechargePozo> list_operator1 = dbRealm.getOperatorDetail(condition);
                    for (int i = 0; i < list_operator1.size(); i++) {
                        list_operator.add(list_operator1.get(i).getOperatorsData());
                    }
                    customSpinner(select_operator, "Select Operator", list_operator);
                } else
                    Toast.makeText(ReChargeActivity.this, "Please select recharge mode", Toast.LENGTH_SHORT).show();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.prepaid) {
                    serviceType = "MOBILE_RECHARGE";
                    operator_clicked = "PRE";
                } else if (checkedId == R.id.postpaid) {
                    serviceType = "MOBILE_BILL_PAYMENT";
                    operator_clicked = "POST";
                }
            }
        });
        prepaid = (RadioButton) findViewById(R.id.prepaid);
        postpaid = (RadioButton) findViewById(R.id.postpaid);
        input_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && s.length() < 10) {
                    input_text.setText("");
                    input_text.setText(EnglishNumberToWords.convert(Integer.parseInt(s.toString())) + " rupee");
                    input_text.setVisibility(View.VISIBLE);
                } else
                    input_text.setVisibility(View.GONE);
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
                btn_contact.setClickable(false);
                loadIMEI();
                break;
            case R.id.btn_submit:
                v.findViewById(R.id.btn_submit).setClickable(false);
                if (operator_clicked.equalsIgnoreCase("DTH")) {
                    if (!ImageUtils.commonAccount(input_number.getText().toString(), 8, 15)) {
                        input_number.setError("Please enter valid mobile number");
                        input_number.requestFocus();
                        v.findViewById(R.id.btn_submit).setClickable(false);
                    } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                        input_amount.setError("Please enter valid data");
                        input_amount.requestFocus();
                        v.findViewById(R.id.btn_submit).setClickable(false);
                    } else if (select_operator.getText().toString().equalsIgnoreCase("Select Operator"))
                        select_operator.setError("Please enter mandatory field");
                    else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpin.getText().toString().isEmpty() || newtpin.getText().toString().length() != 4)) {
                        newtpin.setError("Please enter TPIN");
                        newtpin.requestFocus();
                        v.findViewById(R.id.btn_submit).setClickable(false);
                    } else if (BaseCompactActivity.ENABLE_TPIN != null && (BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && newtpin.getText().toString().length() == 4) || BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("N"))
                        new AsyncPostMethod(WebConfig.RECHARGENEW, reCharge_request().toString(), headerData, ReChargeActivity.this, "Response Time Out.Please Check your Transaction leadger or Contact Customer Support ... \\n\n" +
                                "  Recharge Number  -" + input_number.getText().toString() + "\n" +
                                "  Amount -" + input_amount.getText().toString() + "\n" +
                                "  Operater -" + select_operator.getText().toString() + "\n").execute();
                } else if (operator_clicked.equalsIgnoreCase("PRE") || operator_clicked.equalsIgnoreCase("POST") || operator_clicked.equalsIgnoreCase("MOBILE")) {
                    if (!ImageUtils.commonNumber(input_number.getText().toString(), 10)) {
                        input_number.setError("Please enter valid mobile number");
                        input_number.requestFocus();
                        v.findViewById(R.id.btn_submit).setClickable(false);
                    } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                        input_amount.setError("Please enter valid data");
                        input_amount.requestFocus();
                        v.findViewById(R.id.btn_submit).setClickable(false);
                    } else if (select_operator.getText().toString().equalsIgnoreCase("Select Operator"))
                        select_operator.setError("Please enter mandatory field");
                    else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpin.getText().toString().isEmpty() || newtpin.getText().toString().length() != 4)) {
                        newtpin.setError("Please enter TPIN");
                        newtpin.requestFocus();
                        v.findViewById(R.id.btn_submit).setClickable(false);
                    } else if (BaseCompactActivity.ENABLE_TPIN != null && (BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && newtpin.getText().toString().length() == 4) || BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("N"))
                        new AsyncPostMethod(WebConfig.RECHARGENEW, reCharge_request().toString(), headerData, ReChargeActivity.this, "Response Time Out.Please Check your Transaction leadger or Contact Customer Support ... \\n\n" +
                                "  Recharge Number  -" + input_number.getText().toString() + "\n" +
                                "  Amount -" + input_amount.getText().toString() + "\n" +
                                "  Operater -" + select_operator.getText().toString() + "\n").execute();
                }
                break;
        }
    }

    public void clickable() {
        btn_contact.setClickable(true);
        findViewById(R.id.btn_submit).setClickable(true);
    }

    @Override
    protected void onPause() {
        clickable();
        super.onPause();
    }

    public JSONObject reCharge_request() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", serviceType);
            jsonObject.put("requestType", "UBP_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("serviceOperatorName", select_operator.getText().toString());
            jsonObject.put("rechargeType", operator_clicked);
            jsonObject.put("serviceName", "RECHARGE_SERVICE");
            jsonObject.put("rechargeAmount", input_amount.getText().toString());
            jsonObject.put("amount", input_amount.getText().toString());
            jsonObject.put("mobileNumber", input_number.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("customerAccountNo", input_amount.getText().toString());
            if (newtpin.getText().toString().isEmpty())
                jsonObject.put("tPin", "");
            else
                jsonObject.put("tPin", ImageUtils.encodeSHA256(newtpin.getText().toString()));
            jsonObject.put("countryCode", "");
            jsonObject.put("stdCode", "");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200") || object.getString("responseCode").equalsIgnoreCase("101")) {
                if (object.getString("serviceType").equalsIgnoreCase("MOBILE_RECHARGE")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("PREPAID RECHARGE", object, ReChargeActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "PREPAID RECHARGE", "", "Cannot generate receipt now please try later!", ReChargeActivity.this);
                        }
                } else if (object.getString("serviceType").equalsIgnoreCase("DTH_RECHARGE")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("DTH RECHARGE", object, ReChargeActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "DTH RECHARGE", "", "Cannot generate receipt now please try later!", ReChargeActivity.this);
                        }
                } else if (object.getString("serviceType").equalsIgnoreCase("MOBILE_BILL_PAYMENT")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("POSTPAID BILL PAYMENT", object, ReChargeActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "POSTPAID BILL PAYMENT", "", "Cannot generate receipt now please try later!", ReChargeActivity.this);
                        }
                }
                clear();
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(this,object.getString("responseCode"),Toast.LENGTH_LONG).show();
                setBack_click1(this);
            } else {
                responseMSg(object);
            }
            findViewById(R.id.btn_submit).setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        input_amount.setText("");
        input_number.setText("");
        input_number.requestFocus();
        select_operator.setText("Select Operator");
        prepaid.setChecked(false);
        postpaid.setChecked(false);
        newtpin.setText("");
        if (operator_clicked.equalsIgnoreCase("PRE") || operator_clicked.equalsIgnoreCase("POST"))
            operator_clicked = "MOBILE";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK) {
                if (requestCode == CONTACT_PICKER_RESULT) {
                    contactRead(data, input_number);
                }
            } else if (requestCode == 2) {
                dialog.dismiss();
            }
        } else {
            if (dialog != null)
                dialog.dismiss();
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

