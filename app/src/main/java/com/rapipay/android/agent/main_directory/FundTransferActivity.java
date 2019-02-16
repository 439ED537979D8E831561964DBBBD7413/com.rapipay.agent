package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.LastTransactionPozo;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BeneficiaryAdapter;
import com.rapipay.android.agent.adapter.LastTransAdapter;
import com.rapipay.android.agent.adapter.PaymentAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;

public class FundTransferActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {

    EditText input_amount, input_account, input_name, input_mobile, input_otp;
    AppCompatButton btn_otpsubmit, btn_fund, btn_verify;
    LinearLayout sender_layout, otp_layout, fundlayout, beneficiary_layout, last_tran_layout;
    String otpRefId, fund_transferId, ifsc_code;
    TextView bank_select, text_ben,bank_select_bene;
    ImageView btn_sender, btn_search;
    RecyclerView beneficiary_details, trans_details;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<LastTransactionPozo> transactionPozoArrayList;
    BeneficiaryDetailsPozo pozo;
    String amount = "";
    TextView bene_number, bene_name, con_bene_number;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fundtransfer_layout);
        initialize();
    }

    private void clear() {
        input_amount.setText("");
        input_account.setText("");
        input_otp.setText("");
        bank_select.setText("Select Bank");
    }

    private void initialize() {
        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        reset.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        text_ben = (TextView) findViewById(R.id.text_ben);
        heading = (TextView) findViewById(R.id.toolbar_title);
        if (balance != null)
            heading.setText("BC Fund Transfer (Balance : Rs." + balance + ")");
        else
            heading.setText("BC Fund Transfer");
        input_amount = (EditText) findViewById(R.id.input_amount);
        input_account = (EditText) findViewById(R.id.input_account);
        input_name = (EditText) findViewById(R.id.input_name);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_otp = (EditText) findViewById(R.id.input_otp);
        btn_search = (ImageView) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        btn_fund = (AppCompatButton) findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        btn_verify = (AppCompatButton) findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
        btn_sender = (ImageView) findViewById(R.id.btn_sender);
        btn_sender.setOnClickListener(this);
        btn_sender.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        sender_layout = (LinearLayout) findViewById(R.id.sender_layout);
        otp_layout = (LinearLayout) findViewById(R.id.otp_layout);
        btn_otpsubmit = (AppCompatButton) findViewById(R.id.btn_otpsubmit);
        btn_otpsubmit.setOnClickListener(this);
        fundlayout = (LinearLayout) findViewById(R.id.fundlayout);
        beneficiary_layout = (LinearLayout) findViewById(R.id.beneficiary_layout);
        beneficiary_details = (RecyclerView) findViewById(R.id.beneficiary_details);
        last_tran_layout = (LinearLayout) findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);

        bank_select = (TextView) findViewById(R.id.bank_select);
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_bank = db.geBankDetails("");
                customSpinner(bank_select, "Select Bank", list_bank);
            }
        });
        beneficiary_details.addOnItemTouchListener(new RecyclerTouchListener(this, beneficiary_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                pozo = beneficiaryDetailsPozoslist.get(position);
                customDialog_Common("Fund Transfer", null, pozo, "Sure you want to Transfer?", "");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(this, trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LastTransactionPozo pozo = transactionPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.WALLETRECEIPTURL, receipt_request(pozo).toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        input_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10)
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate().toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                else
                    reset();
            }
        });
    }

    public JSONObject receipt_request(LastTransactionPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Get_Txn_Recipt");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("orgTxnRef", pozo.getRefundTxnId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("routeType", pozo.getTransferType());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getJson_Validate() {
        JSONObject jsonObject = new JSONObject();
        if (!input_name.getText().toString().isEmpty() && !bank_select.getText().toString().isEmpty() && !bank_select.getText().toString().equalsIgnoreCase("Select Bank")) {
            try {
                String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
                ifsc_code = db.geBankIFSC(condition).get(0);
                jsonObject.put("serviceType", "Money_Transfer");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("txnAmmount", input_amount.getText().toString());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("senderName", input_name.getText().toString());
                jsonObject.put("IFSC", ifsc_code);
                jsonObject.put("accountNo", input_account.getText().toString());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Text", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject getSender_Validate() {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "SENDER_COMPLETE_DETAILS");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("reqFor", "BC1");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject addBeneAccount(String accountNo, String ifsc_code, String bankAccountName, String CDFlag) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "ADD_BENEFICIARY_DETAILS");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", input_mobile.getText().toString());
            jsonObject.put("accountNo", accountNo);
            jsonObject.put("confirmAccountNo", accountNo);
            jsonObject.put("senderMobile", input_mobile.getText().toString());
            jsonObject.put("ifscCode", ifsc_code);
            jsonObject.put("accountName", bankAccountName);
            jsonObject.put("CDFlag", CDFlag);
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject addSender() {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10 && !input_name.getText().toString().isEmpty()) {
            try {
                jsonObject.put("serviceType", "ADD_SENDER_DETAILS");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("senderName", input_name.getText().toString());
                jsonObject.put("reqFor", "BC1");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Text", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject add_OtpDetails(String otpRefId, String fund_transferId) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10 && input_otp.getText().toString().length() == 6 && !input_otp.getText().toString().isEmpty() && !input_name.getText().toString().isEmpty()) {
            try {
                jsonObject.put("serviceType", "Verify_Mobile");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("senderMobile", input_mobile.getText().toString());
                jsonObject.put("fundTransferId", fund_transferId);
                jsonObject.put("otp", input_otp.getText().toString());
                jsonObject.put("otprefID", otpRefId);
                jsonObject.put("reqFor", "BC1");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Text", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            reset.setVisibility(View.VISIBLE);
            if (object.has("apiCommonResposne")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                String balance = object1.getString("runningBalance");
                heading.setText("BC Fund Transfer (Balance : Rs." + format(balance) + ")");
            }
            if (object.getString("responseCode").equalsIgnoreCase("1032")) {
                if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                    sender_layout.setVisibility(View.VISIBLE);
                    btn_sender.setVisibility(View.VISIBLE);
                    clear();
                    input_name.setText("");
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS") && object.has("otpRefId")) {
                    btn_sender.setVisibility(View.GONE);
                    otp_layout.setVisibility(View.VISIBLE);
                    clear();
                    otpRefId = object.getString("otpRefId");
                    fund_transferId = object.getString("transactionId");
                } else if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS")) {
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate().toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_SERVICE_FEE")) {
                    customDialog_Common("Fund Transfer Confirmation", object, pozo, "Sure you want to Transfer?", input_mobile.getText().toString());
                } else if (object.getString("serviceType").equalsIgnoreCase("Verify_Account")) {
                    customDialog_Common("Account Verify Details", object, pozo, "VerifyLayout", input_name.getText().toString());
                } else if (object.getString("serviceType").equalsIgnoreCase("Money_Transfer_Bene")) {
                    customDialog_Common("Fund Transfer Details", object, pozo, "VerifyLayout", input_name.getText().toString());
                } else if (object.getString("serviceType").equalsIgnoreCase("Money_Transfer")) {
                    if (object.has("getTxnReceiptDataList")) {
                        localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("Money Transfer", object, FundTransferActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("Cannot generate receipt now please try later!", object, pozo, "VerifyLayout", input_name.getText().toString());
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                    hideKeyboard(FundTransferActivity.this);
                    otp_layout.setVisibility(View.GONE);
                    btn_sender.setVisibility(View.GONE);
                    sender_layout.setVisibility(View.VISIBLE);
                    fundlayout.setVisibility(View.VISIBLE);
                    clear();
                    input_name.setText(object.getString("senderName"));
                    text_ben.setText("Beneficiary Details (Available Limit : Rs " + format(object.getString("remainingLimit")) + ")");
                    if (object.has("oldTxnList")) {
                        if (Integer.parseInt(object.getString("oldTxnCount")) > 0) {
                            last_tran_layout.setVisibility(View.VISIBLE);
                            insertLastTransDetails(object.getJSONArray("oldTxnList"));
                        }
                    }
                    if (object.has("beneListDetail")) {
                        if (Integer.parseInt(object.getString("beneCount")) > 0) {
                            beneficiary_layout.setVisibility(View.VISIBLE);
                            insertBenfDetails(object.getJSONArray("beneListDetail"));
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("Get_Txn_Recipt")) {
                    if (object.has("getTxnReceiptDataList")) {
                        localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("Transaction Receipt", object, FundTransferActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("Cannot generate receipt now please try later!", object, pozo, "VerifyLayout", input_name.getText().toString());
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("ADD_BENEFICIARY_DETAILS")) {
                    customDialog_Common("KYCLAYOUTS", null, null, null, null, object.getString("responseMessage"), FundTransferActivity.this);
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("101")) {
                customDialog_Common("Money Transfer", null, null, "KYCLAYOUT", object.getString("responseMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_search:
                hideKeyboard(FundTransferActivity.this);
                loadIMEI();
                break;
            case R.id.btn_fund:
                hideKeyboard(FundTransferActivity.this);
                addBeneDetails("FUNDTRANSFER", "Add Beneficiary Detail");
                break;
            case R.id.btn_sender:
                hideKeyboard(FundTransferActivity.this);
                if (!input_name.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, addSender().toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                else
                    Toast.makeText(FundTransferActivity.this, "Please enter text", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_otpsubmit:
                if (!otpRefId.isEmpty() && !fund_transferId.isEmpty() && !input_otp.getText().toString().isEmpty()) {
                    hideKeyboard(FundTransferActivity.this);
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, add_OtpDetails(otpRefId, fund_transferId).toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                } else
                    Toast.makeText(FundTransferActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_verify:
                hideKeyboard(FundTransferActivity.this);
                if (bank_select.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select.setError("Please enter mandatory field");
                    bank_select.requestFocus();
                } else if (!ImageUtils.commonAccount(input_account.getText().toString(), 5, 30)) {
                    input_account.setError("Please enter valid account number.");
                    input_account.requestFocus();
                } else if (input_amount.getText().toString().isEmpty()) {
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, verify_Account().toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                } else {
                    Toast.makeText(this, "Input Amount is not applicable for verify account.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reset:
                reset();
                input_mobile.setText("");
                break;
            case R.id.delete_all:
                addBeneDetails("FUNDTRANSFER", "Add Beneficiary Detail");
                break;
        }
    }

    private void addBeneDetails(final String type, String msg) {
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        try {
            if (type.equalsIgnoreCase("FUNDTRANSFER")) {
                alertLayout.findViewById(R.id.bc_add_bene_details).setVisibility(View.VISIBLE);
                bene_number = (TextView) alertLayout.findViewById(R.id.bc_bene_number);
                bene_name = (TextView) alertLayout.findViewById(R.id.bc_bene_name);
                con_bene_number = (TextView) alertLayout.findViewById(R.id.bc_con_bene_num);
                bank_select_bene = (TextView) alertLayout.findViewById(R.id.bank_select_bene);
                bank_select_bene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> list_bank = db.geBankDetails("");
                        customSpinner(bank_select_bene, "Select Bank", list_bank);
                    }
                });
                dialog.setView(alertLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bank_select_bene.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select_bene.setError("Please enter mandatory field");
                    bank_select_bene.requestFocus();
                } else if (!ImageUtils.commonAccount(bene_number.getText().toString(), 5, 30)) {
                    bene_number.setError("Please enter valid beneficiary account number");
                    bene_number.requestFocus();
                } else if (bene_name.getText().toString().isEmpty()) {
                    bene_name.setError("Please enter valid data");
                    bene_name.requestFocus();
                } else if (!ImageUtils.commonAccount(con_bene_number.getText().toString(), 5, 30)) {
                    con_bene_number.setError("Please enter valid confirm beneficiary account number");
                    con_bene_number.requestFocus();
                } else if (!bene_number.getText().toString().equalsIgnoreCase(con_bene_number.getText().toString())) {
                    con_bene_number.setError("Account number not matched");
                    con_bene_number.requestFocus();
                } else {
                    newdialog.dismiss();
                    String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select_bene.getText().toString() + "'";
                    ifsc_code = db.geBankIFSC(condition).get(0);
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, addBeneAccount(bene_number.getText().toString(), ifsc_code, bene_name.getText().toString(), "D").toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newdialog.dismiss();
            }
        });
        newdialog = dialog.show();
    }

    private void reset() {
        otp_layout.setVisibility(View.GONE);
        sender_layout.setVisibility(View.GONE);
        fundlayout.setVisibility(View.GONE);
        btn_search.setVisibility(View.VISIBLE);
        btn_sender.setVisibility(View.GONE);
        reset.setVisibility(View.GONE);
        beneficiary_layout.setVisibility(View.GONE);
        last_tran_layout.setVisibility(View.GONE);
        bank_select.setText("Select Bank");
    }


    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                reset();
                contactRead(data, input_mobile);
            }
        }
    }

    public JSONObject verify_Account() {
        JSONObject jsonObject = new JSONObject();
        try {
            String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
            ifsc_code = db.geBankIFSC(condition).get(0);
            jsonObject.put("serviceType", "Verify_Account");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("senderName", input_name.getText().toString());
            jsonObject.put("IFSC", ifsc_code);
            jsonObject.put("accountNo", input_account.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", input_mobile.getText().toString());
            jsonObject.put("txnAmmount", "1");
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject service_fee(String txnAmmount, String subType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_SERVICE_FEE");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("subType", subType);
            jsonObject.put("txnAmmount", txnAmmount);
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void insertBenfDetails(JSONArray array) {
        beneficiaryDetailsPozoslist = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("bc_BENE_ID").isEmpty() || !object.getString("bc_BENE_ID").equalsIgnoreCase("null"))
                    beneficiaryDetailsPozoslist.add(new BeneficiaryDetailsPozo(object.getString("bank_ACCOUNT_NAME"), object.getString("account_NUMBER"), object.getString("account_IFSC"), object.getString("bank_Name"), object.getString("bc_BENE_ID")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (beneficiaryDetailsPozoslist.size() != 0)
            initializeBenAdapter(beneficiaryDetailsPozoslist);
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), object.getString("serviceProviderTXNID"), object.getString("transferType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeBenAdapter(ArrayList<BeneficiaryDetailsPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        beneficiary_details.setLayoutManager(layoutManager);
        beneficiary_details.setAdapter(new BeneficiaryAdapter(this, list));
    }

    private void initializeTransAdapter(ArrayList<LastTransactionPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new LastTransAdapter(this, trans_details, list));
    }

    public JSONObject getMoney_Validate(String amount, String beneficiaryId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Money_Transfer_Bene");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("txnAmmount", amount);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("senderName", input_name.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", input_mobile.getText().toString());
            jsonObject.put("beneficiaryId", beneficiaryId);
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    protected void customDialog_Common(final String type, final JSONObject object, Object ob, String msg, String input) {
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        text.setText(type);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        try {
            if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
                alertLayout.findViewById(R.id.custom_service).setVisibility(View.VISIBLE);
                if (!object.getString("subType").equalsIgnoreCase("Money_Transfer"))
                    serviceFee(alertLayout, object, (BeneficiaryDetailsPozo) ob, msg, input);
                else {
                    String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
                    ifsc_code = db.geBankIFSC(condition).get(0);
                    moneyTransgerFee(alertLayout, object, input_account.getText().toString(), ifsc_code, input_name.getText().toString(), msg, input);
                }
            } else if (type.equalsIgnoreCase("Fund Transfer")) {
                alertLayout.findViewById(R.id.ben_layout).setVisibility(View.VISIBLE);
                customDialog_Ben(alertLayout, (BeneficiaryDetailsPozo) ob);
            } else if (msg.equalsIgnoreCase("VerifyLayout")) {
                if (type.equalsIgnoreCase("Fund Transfer Details"))
                    customReceiptNew(type, object, FundTransferActivity.this);
                else {
                    btn_ok.setText("Add Beneficiary");
                    alertLayout.findViewById(R.id.verifytransferlayout).setVisibility(View.VISIBLE);
                    verifyTransferFee(alertLayout, object);
                }
            } else if (msg.equalsIgnoreCase("KYCLAYOUT")) {
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, input);
            } else if (type.equalsIgnoreCase("Money Transfer"))
                btn_cancel.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(FundTransferActivity.this);
                alertDialog.dismiss();
                if (type.equalsIgnoreCase("Fund Transfer Confirmation"))
                    try {
                        if (!object.getString("subType").equalsIgnoreCase("Money_Transfer"))
                            new AsyncPostMethod(WebConfig.BCRemittanceApp, getMoney_Validate(ben_amount.getText().toString(), pozo.getBeneficiaryId()).toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                        else
                            new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getJson_Validate().toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else if (type.equalsIgnoreCase("Fund Transfer")) {
                    if (!ImageUtils.commonAmount(ben_amount.getText().toString())) {
                        ben_amount.setError("Please enter valid amount.");
                        ben_amount.requestFocus();
                    } else {
                        new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, service_fee(ben_amount.getText().toString(), "Money_Transfer_Bene").toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                    }
                } else if (type.equalsIgnoreCase("Money Transfer") || type.equalsIgnoreCase("Account Verify Details") || type.equalsIgnoreCase("Cannot generate receipt now please try later!")) {
                    input_account.setText("");
                    input_amount.setText("");
                    bank_select.setText("Select Bank");
                    if (type.equalsIgnoreCase("Account Verify Details")) {
                        try {
                            new AsyncPostMethod(WebConfig.BCRemittanceApp, addBeneAccount(object.getString("accountNo"), object.getString("ifscCode"), object.getString("bankAccountName"), "C").toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("Money Transfer") || type.equalsIgnoreCase("Transaction Receipt"))
            clear();
        else if (type.equalsIgnoreCase("KYCLAYOUTS"))
            new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate().toString(), headerData, FundTransferActivity.this,getString(R.string.responseTimeOutTrans)).execute();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

}
