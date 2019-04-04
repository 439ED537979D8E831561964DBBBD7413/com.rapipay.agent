package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ArrayAdapter;
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
import com.rapipay.android.agent.Model.WalletTransPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.WalletBeneficiaryAdapter;
import com.rapipay.android.agent.adapter.WalletTransactionAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.interfaces.WalletRequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.WalletAsyncMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;

public class WalletDetailsActivity extends BaseCompactActivity implements View.OnClickListener, WalletRequestHandler, CustomInterface {

    EditText input_account, input_mobile, input_otp, input_ben_name;
    TextView input_name;
    Spinner spinner;
    AppCompatButton btn_otpsubmit;
    LinearLayout sender_layout, otp_layout, fundlayout, beneficiary_layout, last_tran_layout;
    String otpRefId, ifsc_code;
    TextView bank_select;
    ImageView btn_sender, btn_search;
    RecyclerView beneficiary_details, trans_details;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<WalletTransPozo> walletTransPozoArrayList;
    TextView text_ben;
    int benePosition;
    private WalletBeneficiaryAdapter adapter;
    private WalletTransactionAdapter transactionAdapter;
    String TYPE, mobileNo;
    static String transactionID;
    String isVerifyAccount = "";
    int limit;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_details_layout);
        initialize();
        TYPE = getIntent().getStringExtra("type");
        mobileNo = getIntent().getStringExtra("mobileNo");
        if (TYPE.equalsIgnoreCase("internal")) {
            input_mobile.setText(mobileNo);
        }
    }

    private void initialize() {
        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        reset.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        heading = (TextView) findViewById(R.id.toolbar_title);
        text_ben = (TextView) findViewById(R.id.text_ben);
        if (balance != null)
            heading.setText("Wallet Fund Transfer (Balance : Rs." + balance + ")");
        else
            heading.setText("Wallet Fund Transfer");
        input_ben_name = (EditText) findViewById(R.id.input_ben_name);
        input_ben_name.setHint("Beneficiary Name");
        input_account = (EditText) findViewById(R.id.input_account);
        input_name = (TextView) findViewById(R.id.input_name);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_otp = (EditText) findViewById(R.id.input_otp);
        btn_search = (ImageView) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
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
        findViewById(R.id.btn_payee).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);
        last_tran_layout = (LinearLayout) findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
        findViewById(R.id.btn_verify).setOnClickListener(this);
        bank_select = (TextView) findViewById(R.id.bank_select);
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_bank = db.geBankDetails("");
                customSpinner(bank_select, "Select Bank", list_bank);
            }
        });
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(this, trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                WalletTransPozo pozo = walletTransPozoArrayList.get(position);
                new WalletAsyncMethod(WebConfig.WALLETRECEIPTURL, receipt_request(pozo).toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "GETTRANSRECEIPT").execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        beneficiary_details.addOnItemTouchListener(new RecyclerTouchListener(this, beneficiary_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                benePosition = position;
                customDialog_Common("BENLAYOUT", null, beneficiaryDetailsPozoslist.get(position), "RapiPay", null, null, "DELETE&FUND");
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
                    new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate("").toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "WALLETSTATUS").execute();
                else
                    reset();
            }
        });
    }

    public JSONObject fund_transfer(BeneficiaryDetailsPozo pozo, String type, String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "FUND_TRANSFER");
            jsonObject.put("requestType", "DMT_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("txnIP", ImageUtils.ipAddress(WalletDetailsActivity.this));
            jsonObject.put("initialReq", "Y");
            jsonObject.put("reqFor", "WALLET");
            jsonObject.put("beneficiaryId", pozo.getBeneficiaryId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("senderMobileNo", input_mobile.getText().toString());
            jsonObject.put("txnAmount", amount);
            jsonObject.put("transferType", type);
            if (value.equalsIgnoreCase("NEFT"))
                jsonObject.put("ifscCode", input_ifsc.getText().toString());
            else
                jsonObject.put("ifscCode", pozo.getIfsc());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject receipt_request(WalletTransPozo pozo) {
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

    public JSONObject delete_Benef(BeneficiaryDetailsPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "DELETE_BENEFICIARY");
            jsonObject.put("requestType", "DMT_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("beneficiaryId", pozo.getBeneficiaryId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("senderMobileNo", input_mobile.getText().toString());
            jsonObject.put("reqFor", "WALLET");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject reGenerate_OTP(JSONObject pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "REGENRATE_OTP");
            jsonObject.put("requestType", "DMT_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("otpRefId", pozo.getString("otpRefId"));
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("senderMobileNo", input_mobile.getText().toString());
            jsonObject.put("reqFor", "WALLET");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_otpsubmit:
//                if (input_otp.getText().toString().isEmpty())
//                    input_otp.setError("Please enter mandatory field");
//                else if (!otpRefId.isEmpty() && !input_otp.getText().toString().isEmpty()) {
//                    hideKeyboard(WalletDetailsActivity.this);
//                    new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, processOtp(input_otp.getText().toString(), otpRefId).toString(), headerData, WalletDetailsActivity.this,getString(R.string.responseTimeOutTrans)).execute();
//                }
//                break;
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_search:
                hideKeyboard(WalletDetailsActivity.this);
                loadIMEI();
                break;
            case R.id.btn_verify:
                if (isVerifyAccount.equalsIgnoreCase("Y")) {
                    hideKeyboard(WalletDetailsActivity.this);
                    if (bank_select.getText().toString().equalsIgnoreCase("Select Bank")) {
                        bank_select.setError("Please enter mandatory field");
                        bank_select.requestFocus();
                    } else if (!ImageUtils.commonAccount(input_account.getText().toString(), 5, 30)) {
                        input_account.setError("Please enter valid account number.");
                        input_account.requestFocus();
                    } else if (!ImageUtils.commonRegex(input_ben_name.getText().toString(), 150, " ")) {
                        input_ben_name.setError("Please enter mandatory field");
                        input_ben_name.requestFocus();
                    } else
                        new WalletAsyncMethod(WebConfig.BCRemittanceApp, verify_Account().toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "VERIFYACCOUNT").execute();
                } else {
                    customDialog_Common("KYCLAYOUTL", null, null, "Alert", null, "Senders number is not Qualified for Verifying Account. Please register the sender on our BC service", "hitFrom");
                }
                break;
            case R.id.btn_payee:
                hideKeyboard(WalletDetailsActivity.this);
                if (bank_select.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select.setError("Please enter mandatory field");
                    bank_select.requestFocus();
                } else if (!ImageUtils.commonAccount(input_account.getText().toString(), 5, 30)) {
                    input_account.setError("Please enter valid account number.");
                    input_account.requestFocus();
                } else if (!ImageUtils.commonRegex(input_ben_name.getText().toString(), 150, " ")) {
                    input_ben_name.setError("Please enter mandatory field");
                    input_ben_name.requestFocus();
                } else
                    new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, processPayee("N", "N").toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "ADDBENEFICIARY").execute();
                break;
            case R.id.reset:
                reset();
                input_mobile.setText("");
                break;
        }

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
//        isRegenrate = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                clear();
                reset();
                contactRead(data, input_mobile);
            }
        }
    }

    public JSONObject verify_Account() {
        JSONObject jsonObject = new JSONObject();
        try {
            transactionID = ImageUtils.miliSeconds();
            String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
            ifsc_code = db.geBankIFSC(condition).get(0);
            jsonObject.put("serviceType", "Verify_Account");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", transactionID);
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

    @Override
    public void chechStatus(JSONObject object, String hitFrom) {
        try {
            reset.setVisibility(View.VISIBLE);
            if (object.has("apiCommonResposne")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                String balance = object1.getString("runningBalance");
                heading.setText("Wallet Fund Transfer (Balance : Rs." + format(balance) + ")");
            }
            if (object.getString("serviceType").equalsIgnoreCase("GET_WALLET_STATUS")) {
                if (object.getString("responseCode").equalsIgnoreCase("75061")) {
                    customDialog_Common("KYCLAYOUT", object, null, "KYC Registration", null, object.getString("responseMessage") + "\n" + "Proceed for KYC ?", hitFrom);
                } else if (object.getString("responseCode").equalsIgnoreCase("75063")) {
                    customDialog_Common("KYCLAYOUT", object, null, "Mobile Number Status", null, object.getString("responseMessage"), hitFrom);
                } else if (object.getString("responseCode").equalsIgnoreCase("75062")) {
                    customDialog_Common("KYCLAYOUT", object, null, "Agent Re-registration", null, object.getString("responseMessage"), "REREGISTRATIONWALLET");
//                    customDialog_Common("OTPLAYOUT", object, null, "Agent Re-registration", null, object.getString("responseMsg"));
                } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    hideKeyboard(WalletDetailsActivity.this);
                    if (hitFrom.equalsIgnoreCase("REREGISTRATIONWALLET")) {
                        if (!object.getString("otpRefId").equalsIgnoreCase("null") && object.getString("otpRefId").length() > 0) {
                            customDialog_Common("OTPLAYOUTS", object, null, "Agent Re-registration", null, object.getString("responseMessage"), hitFrom);
//                            otp_layout.setVisibility(View.VISIBLE);
//                            btn_sender.setVisibility(View.GONE);
//                            otpRefId = object.getString("otpRefId");
//                            clear();
//                            isRegenrate = true;
                        }
                    } else if (hitFrom.equalsIgnoreCase("WALLETSTATUS")) {
                        clear();
                        sender_layout.setVisibility(View.VISIBLE);
                        fundlayout.setVisibility(View.VISIBLE);
                        btn_sender.setVisibility(View.GONE);
                        input_name.setText(object.getString("customerName"));
                        input_name.setEnabled(false);
                        isVerifyAccount = object.getString("isVerifyAccount");
                        limit = Integer.valueOf(object.getInt("dailyRemLimit"));
                        text_ben.setText("Add Beneficiary  Transfer Limit" + "\n" + "Monthly : Rs " + format(object.getString("monthlyRemLimit")));
                        if (object.has("beneficiaryDetailList")) {
                            if (Integer.parseInt(object.getString("numberOfBenCount")) > 0) {
                                beneficiary_layout.setVisibility(View.VISIBLE);
                                insertBenfDetails(object.getJSONArray("beneficiaryDetailList"));
                            }
                        }
                        if (object.has("transactionDetList")) {
                            if (Integer.parseInt(object.getString("numberOfTxnCount")) > 0) {
                                last_tran_layout.setVisibility(View.VISIBLE);
                                insertTransDetails(object.getJSONArray("transactionDetList"));
                            }
                        }
                    }
//                    } else {
//                        new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate().toString(), headerData, WalletDetailsActivity.this,getString(R.string.responseTimeOutTrans)).execute();
//                    }
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("Verify_Account")) {
                if (object.getString("responseCode").equalsIgnoreCase("200"))
                    customDialog_Common("VerifyLayout", object, null, "Verify Account Details", input_name.getText().toString(), null, hitFrom);
                else if (object.getString("responseCode").equalsIgnoreCase("101")) {
                    customDialog_Common("KYCLAYOUTLAY", null, null, "Verify Account Details", null, object.getString("responseMessage"), hitFrom);
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("ADD_BENEFICIARY")) {
                if (object.getString("responseCode").equalsIgnoreCase("200") && object.has("otpRefId"))
                    customDialog_Common("OTPLAYOUT", object, null, "Add Beneficiary OTP", null, null, hitFrom);
                else
                    customDialog_Common("KYCLAYOUTL", object, null, "Beneficiary Detail", null, object.getString("output"), hitFrom);
            } else if (object.getString("serviceType").equalsIgnoreCase("DELETE_BENEFICIARY")) {
                if (object.getString("responseCode").equalsIgnoreCase("200"))
                    customDialog_Common("KYCLAYOUTLAY", object, null, "Payee Detail", null, object.getString("responseMessage"), hitFrom);
            } else if (object.getString("serviceType").equalsIgnoreCase("FUND_TRANSFER")) {
                if (object.getString("responseCode").equalsIgnoreCase("200"))
                    customDialog_Common("OTPLAYOUT", object, null, "Fund Transfer OTP", null, null, hitFrom);
//                else {
//                    localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
//                    if (object.has("getTxnReceiptDataList")) {
//                        try {
//                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
//                            customReceiptNew("Fund Transfer Detail", object, WalletDetailsActivity.this);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            customDialog_Common("KYCLAYOUTL", object, null, "", "", "Cannot generate receipt now please try later!",hitFrom);
//                        }
//                    }
//                }
            } else if (object.getString("serviceType").equalsIgnoreCase("GET_SERVICE_FEE_WLT")) {
                if (object.getString("responseCode").equalsIgnoreCase("200"))
                    customDialog_Common("Fund Transfer Confirmation", object, beneficiaryDetailsPozoslist.get(benePosition), "Confirm Money Transfer?", input_mobile.getText().toString(), null, hitFrom);
            } else if (object.getString("serviceType").equalsIgnoreCase("PROCESS_OTP")) {
                if (object.getString("responseCode").equalsIgnoreCase("200") || object.getString("responseCode").equalsIgnoreCase("101"))
//                    if (object.has("getTxnReceiptDataList")) {
//                        if (!object.getString("getTxnReceiptDataList").equalsIgnoreCase("null")) {
//                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
//                            if (array.length() != 0)
                    if (hitFrom.equalsIgnoreCase("FUNDTRANSFER")) {
                        customReceiptNew("Fund Transfer Details", object, WalletDetailsActivity.this);
//                        localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
                    } else
                        customDialog_Common("KYCLAYOUTL", object, null, "Successfully Done", null, object.getString("responseMessage"), hitFrom);

            } else if (object.getString("serviceType").equalsIgnoreCase("WLT_RE_REGISTRATION_PROCESS")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    customDialog_Common("KYCLAYOUTLAY", object, null, "Registration Added Successfully", null, object.getString("responseMessage"), hitFrom);
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("REGENRATE_OTP")) {
                if (object.getString("responseCode").equalsIgnoreCase("200"))
                    customDialog_Common("OTPLAYOUT", object, null, "OTP Sent Again", null, null, hitFrom);
            } else if (object.getString("serviceType").equalsIgnoreCase("Get_Txn_Recipt")) {
                if (object.has("getTxnReceiptDataList")) {
                    try {
                        JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                        customReceiptNew("Last Transaction Detail", object, WalletDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        customDialog_Common("KYCLAYOUTL", object, null, "", "", "Cannot generate receipt now please try later!", hitFrom);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callKYC() {
        Intent intent = new Intent(WalletDetailsActivity.this, CustomerKYCActivity.class);
        intent.putExtra("mobileNo", input_mobile.getText().toString());
        intent.putExtra("customerType", "C");
        intent.putExtra("type", "internal");
        startActivity(intent);
    }

    public JSONObject getSender_Validate(String oprFlag) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "GET_WALLET_STATUS");
                jsonObject.put("requestType", "DMT_Channel");
                jsonObject.put("txnRef", ImageUtils.miliSeconds());
                jsonObject.put("senderMobileNo", input_mobile.getText().toString());
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("agentID", list.get(0).getMobilno());
                jsonObject.put("operationFlag", oprFlag);
                jsonObject.put("txnIp", ImageUtils.ipAddress(WalletDetailsActivity.this));
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("reqFor", "WALLET");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject processOtp(String otp, JSONObject object) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "PROCESS_OTP");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("senderMobileNo", input_mobile.getText().toString());
            jsonObject.put("otpRefId", object.getString("otpRefId"));
//            if (!object.getString("serviceType").equalsIgnoreCase("FUND_TRANSFER") && !object.getString("serviceType").equalsIgnoreCase("REGENRATE_OTP")) {
//                jsonObject.put("beneficiaryId", object.getString("beneficiaryId"));
//                jsonObject.put("bankBeneficiaryId", object.getString("bankBeneficiaryId"));
//                jsonObject.put("regRefId", object.getString("txnRef"));
//            }
            jsonObject.put("otp", otp);
            jsonObject.put("reqFor", "WALLET");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject walletReregistration(String otp, JSONObject object) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "WLT_RE_REGISTRATION_PROCESS");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("senderMobileNo", input_mobile.getText().toString());
            jsonObject.put("txnIp", ImageUtils.ipAddress(WalletDetailsActivity.this));
            jsonObject.put("otpRefId", object.getString("otpRefId"));
            jsonObject.put("regRefId", object.getString("txnRef"));
            jsonObject.put("otp", otp);
            jsonObject.put("reqFor", "WALLET");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject processPayee(String verificationTxnId, String verifyAccountFlag) {
        JSONObject jsonObject = new JSONObject();
        try {
            String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
            ifsc_code = db.geBankIFSC(condition).get(0);
            jsonObject.put("serviceType", "ADD_BENEFICIARY");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("beneficiaryName", input_ben_name.getText().toString());
            jsonObject.put("accountId", input_account.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("senderMobileNo", input_mobile.getText().toString());
            jsonObject.put("beneficiaryType", "B");
            jsonObject.put("verifyAccountFlag", verifyAccountFlag);
            jsonObject.put("ifscCode", ifsc_code);
            jsonObject.put("verificationTxnId", verificationTxnId);
            jsonObject.put("reqFor", "WALLET");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void chechStat(String object, String hitFrom) {

    }

    private void insertBenfDetails(JSONArray array) {
        beneficiaryDetailsPozoslist = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                beneficiaryDetailsPozoslist.add(new BeneficiaryDetailsPozo(object.getString("beneficiaryName"), object.getString("accountId"), object.getString("ifscCode"), object.getString("bankAccName"), object.getString("benId")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (beneficiaryDetailsPozoslist.size() != 0)
            initializeBenAdapter(beneficiaryDetailsPozoslist);
    }

    private void initializeBenAdapter(ArrayList<BeneficiaryDetailsPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        beneficiary_details.setLayoutManager(layoutManager);
        adapter = new WalletBeneficiaryAdapter(this, beneficiary_details, list);
        beneficiary_details.setAdapter(adapter);
    }

    private void insertTransDetails(JSONArray array) {
        walletTransPozoArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                walletTransPozoArrayList.add(new WalletTransPozo(object.getString("txnStatus"), object.getString("txnRequestedDate"), object.getString("refundTxnId"), object.getString("txnAmount"), object.getString("accountNumber"), object.getString("accountIfsc"), object.getString("bankAccountName"), object.getString("bcBeneId"), object.getString("bankName"), object.getString("rrn"), object.getString("transferType"), object.getString("txnMsg")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (walletTransPozoArrayList.size() != 0)
            initializeTransAdapter(walletTransPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<WalletTransPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trans_details.setLayoutManager(layoutManager);
        transactionAdapter = new WalletTransactionAdapter(this, trans_details, list);
        trans_details.setAdapter(transactionAdapter);
    }

    private void clear() {
        input_account.setText("");
        input_ben_name.setText("");
        bank_select.setText("Select Bank");
    }

    String value = "";
    TextView input_ifsc;
    private void customFund_Transfer(final BeneficiaryDetailsPozo pozo, String title, final String hitFrom) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_fundtransfer_layout, null);
        alertLayout.setKeepScreenOn(true);
        TextView texts = (TextView) alertLayout.findViewById(R.id.dialog_title);
        input_ifsc = (TextView) alertLayout.findViewById(R.id.input_ifsc);
        texts.setText(title);
        spinner = (Spinner) alertLayout.findViewById(R.id.bank_select);
        final ArrayList<String> transferArrayList = db.getTransferDetails("");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(WalletDetailsActivity.this,
                android.R.layout.simple_spinner_item, transferArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value = transferArrayList.get(position);
                if (value.equalsIgnoreCase("NEFT")) {
                    input_ifsc.setVisibility(View.VISIBLE);
                } else {
                    input_ifsc.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//            }
//        spinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ArrayList<String> transferArrayList = db.getTransferDetails("");
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerKYCActivity.this,
//                        android.R.layout.simple_spinner_item, items);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                customSpinner(spinner, "Select Transfer Type", transferArrayList);
//            }
//        });
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        btn_cancel.setText("Cancel");
        btn_cancel.setTextSize(10);
        btn_ok.setText("Done");
        btn_ok.setTextSize(10);
        ben_amount = (TextView) alertLayout.findViewById(R.id.input_amount_ben);
        dialog.setView(alertLayout);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(WalletDetailsActivity.this);
                if (value.equalsIgnoreCase("")) {
                    Toast.makeText(WalletDetailsActivity.this, "Please select Transfer Type", Toast.LENGTH_SHORT).show();
                } else if (!ImageUtils.commonAmount(ben_amount.getText().toString())) {
                    ben_amount.setError("Please enter valid amount.");
                    ben_amount.requestFocus();
                }else if(Integer.parseInt(ben_amount.getText().toString())>=25001){
                    ben_amount.setError("Maximum transfer amount would be 25000.");
                    ben_amount.requestFocus();
                }else if(Integer.parseInt(ben_amount.getText().toString())>=limit+1){
                    ben_amount.setError("Maximum transfer amount would be "+limit+".");
                    ben_amount.requestFocus();
                }else if (value.equalsIgnoreCase("NEFT") && input_ifsc.getText().toString().isEmpty()) {
                    input_ifsc.setError("Please enter ifsc code.");
                    input_ifsc.requestFocus();
                } else {
                    new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, service_fee(ben_amount.getText().toString()).toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), hitFrom).execute();
                    newdialog.dismiss();
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

    public JSONObject service_fee(String txnAmmount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_SERVICE_FEE_WLT");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("subType", "Fund_Transfer");
            jsonObject.put("txnAmmount", txnAmmount);
            jsonObject.put("reqFor", "WALLET");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected void customDialog_Common(final String type, final JSONObject object, final Object ob, String msg, String input, String output, final String hitFrom) {
        try {
            dialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            alertLayout.setKeepScreenOn(true);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
            text.setText(msg);
            AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
            AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
            final AppCompatButton btn_regenerate = (AppCompatButton) alertLayout.findViewById(R.id.btn_regenerate);
            if (type.equalsIgnoreCase("BENLAYOUT")) {
                btn_cancel.setText("Delete Beneficiary");
                btn_cancel.setTextSize(10);
                btn_ok.setText("Fund Transfer");
                btn_ok.setTextSize(10);
                dialog_cancel.setVisibility(View.VISIBLE);
                dialog.setView(alertLayout);
            }
            if (msg.equalsIgnoreCase("KYC Registration")) {
                btn_cancel.setText("No");
                btn_cancel.setTextSize(14);
                btn_ok.setText("Yes");
                btn_ok.setTextSize(14);
                dialog_cancel.setVisibility(View.VISIBLE);
                dialog.setView(alertLayout);
            }
            try {
                if (type.equalsIgnoreCase("OTPLAYOUT")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_regenerate.setVisibility(View.VISIBLE);
                        }
                    }, 15000);
                    alertLayout.findViewById(R.id.otp_layout).setVisibility(View.VISIBLE);
                    otpView(alertLayout, object);
                } else if (type.equalsIgnoreCase("OTPLAYOUTS")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_regenerate.setVisibility(View.VISIBLE);
                        }
                    }, 15000);
                    alertLayout.findViewById(R.id.otp_layout).setVisibility(View.VISIBLE);
                    otpView(alertLayout, object);
                } else if (type.equalsIgnoreCase("VerifyLayout")) {
                    btn_ok.setText("Add Beneficiary");
//                    btn_cancel.setVisibility(View.GONE);
                    alertLayout.findViewById(R.id.verifytransferlayout).setVisibility(View.VISIBLE);
                    verifyTransferFee(alertLayout, object);
                } else if (type.equalsIgnoreCase("KYCLAYOUT")) {
                    customView(alertLayout, output);
                } else if (type.equalsIgnoreCase("KYCLAYOUTLAY") || type.equalsIgnoreCase("KYCLAYOUTL")) {
                    btn_cancel.setVisibility(View.GONE);
                    customView(alertLayout, output);
                } else if (type.equalsIgnoreCase("FUNDTRANSFER")) {
                    alertLayout.findViewById(R.id.fundtransfer).setVisibility(View.VISIBLE);
                } else if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
                    alertLayout.findViewById(R.id.custom_service).setVisibility(View.VISIBLE);
                    serviceFee(alertLayout, object, (BeneficiaryDetailsPozo) ob, msg, input);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.setCancelable(false);
            dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        hideKeyboard(WalletDetailsActivity.this);
                        if (type.equalsIgnoreCase("OTPLAYOUTS")) {
                            if (!otpView.getText().toString().isEmpty()) {
                                hideKeyboard(WalletDetailsActivity.this);
                                new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, walletReregistration(otpView.getText().toString(), object).toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "WALLETREGISTRATION").execute();
                                alertDialog.dismiss();
                            } else {
                                otpView.setError("Please Enter Otp");
                                otpView.requestFocus();
                            }
                        } else if (type.equalsIgnoreCase("OTPLAYOUT")) {
                            if (!otpView.getText().toString().isEmpty()) {
                                hideKeyboard(WalletDetailsActivity.this);
                                if (object.getString("responseCode").equalsIgnoreCase("75061")) {
                                    callKYC();
                                } else
                                    new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, processOtp(otpView.getText().toString(), object).toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), hitFrom).execute();
                                alertDialog.dismiss();
                            } else {
                                otpView.setError("Please Enter Otp");
                                otpView.requestFocus();
                            }
                        } else if (type.equalsIgnoreCase("KYCLAYOUT") || type.equalsIgnoreCase("KYCLAYOUTL")) {
                            if (object != null) {
                                if (object.getString("responseCode").equalsIgnoreCase("75061"))
                                    callKYC();
                                else if (object.getString("responseCode").equalsIgnoreCase("75062")) {
                                    new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate("Y").toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "REREGISTRATIONWALLET").execute();
                                } else if (object.getString("serviceType").equalsIgnoreCase("DELETE_PAYEE")) {
                                    beneficiaryDetailsPozoslist.remove(benePosition);
                                    adapter.notifyDataSetChanged();
                                }
                                clear();
                            }
                            alertDialog.dismiss();
                        } else if (type.equalsIgnoreCase("BENLAYOUT")) {
                            customFund_Transfer((BeneficiaryDetailsPozo) ob, "RapiPay", "FUNDTRANSFER");
                            alertDialog.dismiss();
                        } else if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
                            if(BaseCompactActivity.ENABLE_TPIN !=null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && newtpin.getText().toString().length()==4) {
                                new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, fund_transfer(beneficiaryDetailsPozoslist.get(benePosition), value, ben_amount.getText().toString()).toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), hitFrom).execute();
                                alertDialog.dismiss();
                            }else if(BaseCompactActivity.ENABLE_TPIN !=null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpin.getText().toString().isEmpty() || newtpin.getText().toString().length()!=4)){
                                newtpin.setError("Please enter TPIN");
                                newtpin.requestFocus();
                            }else if(BaseCompactActivity.ENABLE_TPIN !=null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("N")){
                                new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, fund_transfer(beneficiaryDetailsPozoslist.get(benePosition), value, ben_amount.getText().toString()).toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), hitFrom).execute();
                                alertDialog.dismiss();
                            }
                        } else if (type.equalsIgnoreCase("KYCLAYOUTLAY")) {
                            if (object.getString("serviceType").equalsIgnoreCase("WLT_RE_REGISTRATION_PROCESS"))
                                new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate("").toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "WALLETSTATUS").execute();
                            else {
                                beneficiaryDetailsPozoslist.remove(benePosition);
                                adapter.notifyDataSetChanged();
                            }
                            clear();
                            alertDialog.dismiss();
                        } else if (type.equalsIgnoreCase("VerifyLayout") || type.equalsIgnoreCase("KYCLAYOUTL")) {
                            if (object.getString("serviceType").equalsIgnoreCase("Verify_Account"))
                                new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, processPayee(transactionID, "Y").toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "VERIFYACCOUNT").execute();
                            else
                                new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate("").toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "WALLETSTATUS").execute();
                            input_account.setText("");
                            input_ben_name.setText("");
                            bank_select.setText("Select Bank");
                            transactionID = "";
                            alertDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equalsIgnoreCase("BENLAYOUT")) {
                        new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, delete_Benef((BeneficiaryDetailsPozo) ob).toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "DELETEBENEFICIARY").execute();
                    }
//                    isRegenrate = false;
                    alertDialog.dismiss();
                }
            });
            btn_regenerate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hitFrom.equalsIgnoreCase("REREGISTRATIONWALLET"))
                        new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate("Y").toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "REREGISTRATIONWALLET").execute();
                    else
                        new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, reGenerate_OTP(object).toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), hitFrom).execute();
                    alertDialog.dismiss();
                }
            });
            alertDialog = dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okClicked(String type, Object ob) {
        new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate("").toString(), headerData, WalletDetailsActivity.this, getString(R.string.responseTimeOutTrans), "WALLETSTATUS").execute();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
