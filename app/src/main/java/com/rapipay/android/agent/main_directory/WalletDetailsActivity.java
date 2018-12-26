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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.WalletBeneficiaryAdapter;
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

public class WalletDetailsActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {

    EditText input_account, input_mobile, input_otp, input_ben_name;
    TextView input_name,spinner;
    AppCompatButton btn_otpsubmit;
    LinearLayout sender_layout, otp_layout, fundlayout, beneficiary_layout, last_tran_layout;
    String otpRefId, ifsc_code;
    TextView bank_select;
    ImageView btn_sender, btn_search;
    RecyclerView beneficiary_details, trans_details;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    TextView text_ben;
    String customerId;
    int benePosition;
    private WalletBeneficiaryAdapter adapter;
    String TYPE,mobileNo;

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
        input_ben_name.setHint("Receiver's Name");
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
        fundlayout = (LinearLayout) findViewById(R.id.fundlayout);
        beneficiary_layout = (LinearLayout) findViewById(R.id.beneficiary_layout);
        beneficiary_details = (RecyclerView) findViewById(R.id.beneficiary_details);
//last tranction
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
//        bank_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0) {
//                    String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + list_bank.get(position) + "'";
//                    ifsc_code = db.geBankIFSC(condition).get(0);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        beneficiary_details.addOnItemTouchListener(new RecyclerTouchListener(this, beneficiary_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                benePosition = position;
                customDialog_Common("BENLAYOUT", null, beneficiaryDetailsPozoslist.get(position), "RapiPay", null, null);
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
                    new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate().toString(), headerData, WalletDetailsActivity.this).execute();
                else
                    reset();
            }
        });
    }

    public JSONObject fund_transfer(BeneficiaryDetailsPozo pozo, String type, String amount) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "FUND_TRANSFER");
            jsonObject.put("requestType", "DMT_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", "FT" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("customerId", customerId);
            jsonObject.put("beneficiaryId", pozo.getBeneficiaryId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNo", input_mobile.getText().toString());
            jsonObject.put("txnAmount", amount);
            jsonObject.put("transferType", type);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject delete_Benef(BeneficiaryDetailsPozo pozo) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "DELETE_PAYEE");
            jsonObject.put("requestType", "DMT_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", "DP" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("customerId", customerId);
            jsonObject.put("beneficiaryId", pozo.getBeneficiaryId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNo", input_mobile.getText().toString());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_otpsubmit:
                if (input_otp.getText().toString().isEmpty())
                    input_otp.setError("Please enter mandatory field");
                else if (!otpRefId.isEmpty() && !input_otp.getText().toString().isEmpty()) {
                    hideKeyboard(WalletDetailsActivity.this);
                    new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, processOtp(input_otp.getText().toString(), otpRefId).toString(), headerData, WalletDetailsActivity.this).execute();
                }
                break;
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_search:
                hideKeyboard(WalletDetailsActivity.this);
                loadIMEI();
                break;
            case R.id.btn_verify:
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
                    new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, verify_Account().toString(), headerData, WalletDetailsActivity.this).execute();
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
                    new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, processPayee().toString(), headerData, WalletDetailsActivity.this).execute();

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
        bank_select.setText("Select Bank");
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
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
            ifsc_code = db.geBankIFSC(condition).get(0);
            jsonObject.put("serviceType", "Verify_Account");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "VA" + tsLong.toString());
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
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            reset.setVisibility(View.VISIBLE);
            if(object.has("apiCommonResposne")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                String balance = object1.getString("runningBalance");
                heading.setText("Wallet Fund Transfer (Balance : Rs." + format(balance) + ")");
            }
            if (object.getString("serviceType").equalsIgnoreCase("GET_WALLET_STATUS")) {
                if (object.getString("responseCode").equalsIgnoreCase("75061")) {
                    customDialog_Common("KYCLAYOUT", object, null, "KYC Registration", null, object.getString("responseMsg") + "\n" + "Proceed for KYC ?");

                } else if (object.getString("responseCode").equalsIgnoreCase("75063")) {
                    customDialog_Common("KYCLAYOUT", object, null, "Mobile Number Status", null, object.getString("responseMsg"));
                } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    if (object.has("otpId")) {
                        if (!object.getString("otpId").equalsIgnoreCase("null") && object.getString("otpId").length() > 0) {
                            otp_layout.setVisibility(View.VISIBLE);
                            btn_sender.setVisibility(View.GONE);
                            otpRefId = object.getString("otpId");
                            clear();
                        } else {
                            clear();
                            hideKeyboard(WalletDetailsActivity.this);
                            sender_layout.setVisibility(View.VISIBLE);
                            fundlayout.setVisibility(View.VISIBLE);
                            btn_sender.setVisibility(View.GONE);
                            input_name.setText(object.getString("customerName"));
                            customerId = object.getString("customerId");
                            text_ben.setText("Beneficiary Details Transfer Limit" + "\n" +  "Daily : Rs " + format(object.getString("dailyRemainigLimit"))  + "\n" +  "Monthly : Rs " + format(object.getString("monthlyRemainigLimit")));
//                            list_bank = db.geBankDetails("");
//                            if (list_bank.size() != 0) {
//                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                                        android.R.layout.simple_spinner_item, list_bank);
//                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                bank_select.setAdapter(dataAdapter);
//                            }

                            if (object.has("beneficiaries")) {
                                if (Integer.parseInt(object.getString("beneficiaryCount")) > 0) {
                                    beneficiary_layout.setVisibility(View.VISIBLE);
                                    insertBenfDetails(object.getJSONArray("beneficiaries"));
                                }
                            }
                        }
                    } else {
                        new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate().toString(), headerData, WalletDetailsActivity.this).execute();
                    }
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("Verify_Account")) {
                if (object.getString("responseCode").equalsIgnoreCase("200"))
                    customDialog_Common("VerifyLayout", object, null, "Verify Account Details", input_name.getText().toString(), null);
            } else if (object.getString("serviceType").equalsIgnoreCase("ADD_PAYEE")) {
                if (object.getString("responseCode").equalsIgnoreCase("200") && object.has("otpId"))
                    customDialog_Common("OTPLAYOUT", object, null, "Add Payee OTP", null, null);
                else
                    customDialog_Common("KYCLAYOUTL", object, null, "Payee Detail", null, object.getString("output"));
            } else if (object.getString("serviceType").equalsIgnoreCase("DELETE_PAYEE")) {
                if (object.getString("responseCode").equalsIgnoreCase("200"))
                    customDialog_Common("KYCLAYOUTLAY", object, null, "Payee Detail", null, object.getString("output"));
            } else if (object.getString("serviceType").equalsIgnoreCase("FUND_TRANSFER")) {
                if (object.getString("responseCode").equalsIgnoreCase("200") && object.has("otpId"))
                    customDialog_Common("OTPLAYOUT", object, null, "Fund Transfer OTP", null, null);
                else {
                    localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
                    if (object.has("getTxnReceiptDataList")) {
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("Fund Transfer Detail", object, WalletDetailsActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTL", object,null,"","","Cannot generate receipt now please try later!");
                        }
                    }
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("GET_SERVICE_FEE")) {
                if (object.getString("responseCode").equalsIgnoreCase("200"))
                    customDialog_Common("Fund Transfer Confirmation", object, beneficiaryDetailsPozoslist.get(benePosition), "Confirm Money Transfer?", input_mobile.getText().toString(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callKYC() {
        Intent intent = new Intent(WalletDetailsActivity.this, CustomerKYCActivity.class);
        intent.putExtra("mobileNo", input_mobile.getText().toString());
        intent.putExtra("customerType", "C");
//        intent.putExtra("parentId", list.get(0).getMobilno());
//        intent.putExtra("sessionKey", list.get(0).getPinsession());
//        intent.putExtra("sessionRefNo", list.get(0).getAftersessionRefNo());
//        intent.putExtra("nodeAgent", list.get(0).getMobilno());
        intent.putExtra("type", "internal");
        startActivity(intent);
    }

    public JSONObject getSender_Validate() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "GET_WALLET_STATUS");
                jsonObject.put("requestType", "DMT_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRef", "GWS" + tsLong.toString());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNo", input_mobile.getText().toString());
                jsonObject.put("secretKey", "");
                jsonObject.put("customerId", "");
                jsonObject.put("aadharNo", "");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject processOtp(String otp, String otpRefId) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "PROCESS_OTP");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", "POTP" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNo", input_mobile.getText().toString());
            jsonObject.put("otpId", otpRefId);
            jsonObject.put("otp", otp);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject processPayee() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
            ifsc_code = db.geBankIFSC(condition).get(0);
            jsonObject.put("serviceType", "ADD_PAYEE");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", "AP" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("beneficiaryName", input_ben_name.getText().toString());
            jsonObject.put("accountId", input_account.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNo", input_mobile.getText().toString());
            jsonObject.put("beneficiaryType", "B");
            jsonObject.put("accountProvider", "B");
            jsonObject.put("customerId", customerId);
            jsonObject.put("ifscCode", ifsc_code);
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
    public void chechStat(String object) {

    }

    private void insertBenfDetails(JSONArray array) {
        beneficiaryDetailsPozoslist = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                beneficiaryDetailsPozoslist.add(new BeneficiaryDetailsPozo(object.getString("beneficiaryName"), object.getString("accountId"), object.getString("ifscCode"), object.getString("accountName"), object.getString("beneficiaryId")));
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

    private void clear() {
        input_account.setText("");
        input_ben_name.setText("");
        bank_select.setText("Select Bank");
    }

    private void customFund_Transfer(final BeneficiaryDetailsPozo pozo, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_fundtransfer_layout, null);
        TextView texts = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texts.setText(title);
        spinner = (TextView) alertLayout.findViewById(R.id.bank_select);
        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> transferArrayList = db.getTransferDetails("");
                customSpinner(spinner, "Select Transfer Type", transferArrayList);
            }
        });
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
                if (spinner.getText().toString().equalsIgnoreCase("Select Transfer Type")) {
                    spinner.setError("Please select transfer type.");
                    spinner.requestFocus();
                } else if (!ImageUtils.commonAmount(ben_amount.getText().toString())) {
                    ben_amount.setError("Please enter valid amount.");
                    ben_amount.requestFocus();
                } else {
                    new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, service_fee(ben_amount.getText().toString()).toString(), headerData, WalletDetailsActivity.this).execute();
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
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_SERVICE_FEE");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "GSF" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("subType", "Fund_Transfer");
            jsonObject.put("txnAmmount", txnAmmount);
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected void customDialog_Common(final String type, final JSONObject object, final Object ob, String msg, String input, String output) {
        try {
            dialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
            text.setText(msg);
            AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
            AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
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
                    alertLayout.findViewById(R.id.otp_layout).setVisibility(View.VISIBLE);
                    otpView(alertLayout, object);
                } else if (type.equalsIgnoreCase("VerifyLayout")) {
                    btn_cancel.setVisibility(View.GONE);
                    alertLayout.findViewById(R.id.verifytransferlayout).setVisibility(View.VISIBLE);
                    verifyTransferFee(alertLayout, object);
                } else if (type.equalsIgnoreCase("KYCLAYOUT")) {
                    customView(alertLayout, output);
                } else if (type.equalsIgnoreCase("KYCLAYOUTLAY") || type.equalsIgnoreCase("KYCLAYOUTL")) {
                    btn_cancel.setVisibility(View.GONE);
                    customView(alertLayout, output);
                } else if (type.equalsIgnoreCase("FUNDTRANSFER")) {
                    alertLayout.findViewById(R.id.fundtransfer).setVisibility(View.VISIBLE);
//                    fundTransferView(alertLayout,(BeneficiaryDetailsPozo)ob);
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
                        if (type.equalsIgnoreCase("OTPLAYOUT")) {
                            if (!otpView.getText().toString().isEmpty()) {
                                hideKeyboard(WalletDetailsActivity.this);
                                new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, processOtp(otpView.getText().toString(), object.getString("otpId")).toString(), headerData, WalletDetailsActivity.this).execute();
                            }
                        } else if (type.equalsIgnoreCase("KYCLAYOUT") || type.equalsIgnoreCase("KYCLAYOUTL")) {
                            if (object.getString("responseCode").equalsIgnoreCase("75061"))
                                callKYC();
                            else if (object.getString("serviceType").equalsIgnoreCase("ADD_PAYEE")) {
                                clear();
                                new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate().toString(), headerData, WalletDetailsActivity.this).execute();
                            } else if (object.getString("serviceType").equalsIgnoreCase("DELETE_PAYEE")) {
                                beneficiaryDetailsPozoslist.remove(benePosition);
                                adapter.notifyDataSetChanged();
                            }
                        } else if (type.equalsIgnoreCase("BENLAYOUT")) {
                            customFund_Transfer((BeneficiaryDetailsPozo) ob, "RapiPay");
                        } else if (type.equalsIgnoreCase("Fund Transfer Confirmation"))
//                            if (spinner.getText().toString().equalsIgnoreCase("Select Transfer Type"))
//                                spinner.setError("Please select transfer type.");
//                            else
                            new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, fund_transfer(beneficiaryDetailsPozoslist.get(benePosition), spinner.getText().toString(), ben_amount.getText().toString()).toString(), headerData, WalletDetailsActivity.this).execute();
                        else if (type.equalsIgnoreCase("KYCLAYOUTLAY")) {
                            beneficiaryDetailsPozoslist.remove(benePosition);
                            adapter.notifyDataSetChanged();
                        } else if (type.equalsIgnoreCase("VerifyLayout") || type.equalsIgnoreCase("KYCLAYOUTL")) {
                            input_account.setText("");
                            input_ben_name.setText("");
//                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(WalletDetailsActivity.this,
//                                    android.R.layout.simple_spinner_item, list_bank);
//                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            bank_select.setText("Select Bank");
                        }
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equalsIgnoreCase("BENLAYOUT")) {
                        new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, delete_Benef((BeneficiaryDetailsPozo) ob).toString(), headerData, WalletDetailsActivity.this).execute();
                    }
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

    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
