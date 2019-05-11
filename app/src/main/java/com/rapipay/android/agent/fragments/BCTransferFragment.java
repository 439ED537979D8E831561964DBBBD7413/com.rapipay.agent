package com.rapipay.android.agent.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.LastTransactionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BCBeneficiaryAdapter;
import com.rapipay.android.agent.adapter.LastTransAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class BCTransferFragment extends BaseFragment implements View.OnClickListener, RequestHandler, CustomInterface {

    EditText input_amount, input_account, input_name, input_mobile, input_otp, searchfield;
    AppCompatButton btn_otpsubmit, btn_fund, btn_verify;
    LinearLayout sender_layout, otp_layout, fundlayout, beneficiary_layout, last_tran_layout;
    String otpRefId, fund_transferId, ifsc_code, reqFor;
    TextView bank_select, text_ben, bank_select_bene;
    ImageView btn_sender;
    RecyclerView beneficiary_details, trans_details;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<LastTransactionPozo> transactionPozoArrayList;
    BeneficiaryDetailsPozo pozo;
    String amount = "", mobileNo;
    TextView bene_number, bene_name, newtin;
    float limit;
    TextView limit_title;
    BCBeneficiaryAdapter adapter;
    boolean receipt_clicked = false;
    String headerData;
    View rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.fundtransfer_layout, container, false);
        localStorage = LocalStorage.getInstance(getActivity());
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        initialize(rv);

        return rv;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fundtransfer_layout);
//        initialize();
////        mobileNo = getIntent().getStringExtra("MOBILENO");
////        if (!mobileNo.isEmpty()) {
////            input_mobile.setText(mobileNo);
////        }
//    }

    private void clear() {
        input_amount.setText("");
        input_account.setText("");
        input_otp.setText("");
        bank_select.setText("Select Bank");
    }

    private void initialize(View rv) {
//        reset = (ImageView) rv.findViewById(R.id.reset);
//        reset.setOnClickListener(this);
//        reset.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        text_ben = (TextView) rv.findViewById(R.id.text_ben);
//        heading = (TextView) rv.findViewById(R.id.toolbar_title);
//        if (balance != null)
//            heading.setText("BC Fund Transfer (Balance : Rs." + balance + ")");
//        else
//            heading.setText("BC Fund Transfer");
        limit_title = (TextView) rv.findViewById(R.id.limit);
        input_amount = (EditText) rv.findViewById(R.id.input_amount);
        input_account = (EditText) rv.findViewById(R.id.input_account);
        input_name = (EditText) rv.findViewById(R.id.input_name);
        input_mobile = (EditText) rv.findViewById(R.id.input_mobile);
        input_otp = (EditText) rv.findViewById(R.id.input_otp);
        btn_fund = (AppCompatButton) rv.findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        btn_verify = (AppCompatButton) rv.findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
        btn_sender = (ImageView) rv.findViewById(R.id.btn_sender);
        btn_sender.setOnClickListener(this);
        btn_sender.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        sender_layout = (LinearLayout) rv.findViewById(R.id.sender_layout);
        otp_layout = (LinearLayout) rv.findViewById(R.id.otp_layout);
        btn_otpsubmit = (AppCompatButton) rv.findViewById(R.id.btn_otpsubmit);
        btn_otpsubmit.setOnClickListener(this);
        fundlayout = (LinearLayout) rv.findViewById(R.id.fundlayout);
        searchfield = (EditText) rv.findViewById(R.id.searchfield);
        beneficiary_layout = (LinearLayout) rv.findViewById(R.id.beneficiary_layout);
        beneficiary_details = (RecyclerView) rv.findViewById(R.id.beneficiary_details);
        last_tran_layout = (LinearLayout) rv.findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) rv.findViewById(R.id.trans_details);
        bank_select = (TextView) rv.findViewById(R.id.bank_select);
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_bank = BaseCompactActivity.db.geBankDetails("");
                customSpinner(bank_select, "Select Bank", list_bank, "");
            }
        });
        beneficiary_details.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), beneficiary_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (beneficiaryDetailsPozoslist.size() != 0) {
                    pozo = beneficiaryDetailsPozoslist.get(position);
                    if (pozo.getIsNEFT().equalsIgnoreCase("N") && pozo.getIsIMPS().equalsIgnoreCase("Y"))
                        customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", "IMPS");
//                    else if (pozo.getIsNEFT().equalsIgnoreCase("Y")  && pozo.getIsIMPS().equalsIgnoreCase("Y"))
                    else if (pozo.getIsNEFT().equalsIgnoreCase("Y") && pozo.getIsIMPS().equalsIgnoreCase("N"))
                        customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", "NEFT");
                    else
                        customDialog_Common("BENLAYOUT", null, pozo, "Fund Transfer Type", null);
//                    customDialog_Common("Fund Transfer", null, pozo, "Sure you want to Transfer?", "");
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (beneficiaryDetailsPozoslist.size() != 0) {
                    pozo = beneficiaryDetailsPozoslist.get(position);
                    customDialog_Common("Beneficiary Details", null, pozo, "Sure you want to Delete Beneficiary?", "");

                }
            }
        }));
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (transactionPozoArrayList.size() != 0 && !receipt_clicked) {
                    receipt_clicked = true;
                    LastTransactionPozo pozo = transactionPozoArrayList.get(position);
                    new AsyncPostMethod(WebConfig.WALLETRECEIPTURL, receipt_request(pozo).toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                }
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
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate().toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                else
                    reset();
            }
        });
        searchfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0)
                    adapter.filter(s.toString());
            }
        });
    }

    public JSONObject delete_Benef(BeneficiaryDetailsPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "DELETE_BENEFICIARY");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("beneficiaryId", pozo.getBeneficiaryId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
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
                ifsc_code = BaseCompactActivity.db.geBankIFSC(condition).get(0);
                jsonObject.put("serviceType", "Money_Transfer");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("txnAmmount", input_amount.getText().toString());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("senderName", input_name.getText().toString().trim());
                jsonObject.put("IFSC", ifsc_code);
                jsonObject.put("accountNo", input_account.getText().toString());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Enter Text", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject addBeneAccount(String accountNo, String ifsc_code, String bankAccountName, String CDFlag, String transfer_type) {
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
            jsonObject.put("transferType", transfer_type);
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
                jsonObject.put("senderName", input_name.getText().toString().trim());
                jsonObject.put("reqFor", "BC1");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Enter Text", Toast.LENGTH_SHORT).show();
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
                jsonObject.put("reqFor", reqFor);
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Enter Text", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.has("apiCommonResposne") && !object.getString("apiCommonResposne").equalsIgnoreCase("null")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                String balance = object1.getString("runningBalance");
//                heading.setText("BC Fund Transfer (Balance : Rs." + format(balance) + ")");
            }
            if (object.getString("responseCode").equalsIgnoreCase("1032")) {
                if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                    sender_layout.setVisibility(View.VISIBLE);
                    btn_sender.setVisibility(View.VISIBLE);
                    clear();
                    input_name.setText("");
                    rv.findViewById(R.id.warning).setVisibility(View.VISIBLE);
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS") && object.has("otpRefId")) {
                    btn_sender.setVisibility(View.GONE);
                    otp_layout.setVisibility(View.VISIBLE);
                    clear();
                    otpRefId = object.getString("otpRefId");
                    if (object.has("reqFor"))
                        reqFor = object.getString("reqFor");
                    fund_transferId = object.getString("transactionId");
                } else if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS")) {
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate().toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("DELETE_BENEFICIARY")) {
                    customDialog_Common("KYCLAYOUTLAY", object, null, "Payee Detail", object.getString("responseMessage"));
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_SERVICE_FEE")) {
                    customDialog_Common("Fund Transfer Confirmation", object, pozo, "Sure you want to Transfer?", input_mobile.getText().toString());
                } else if (object.getString("serviceType").equalsIgnoreCase("Verify_Account")) {
                    customDialog_Common("Account Verify Details", object, pozo, "VerifyLayout", object.getString("senderName").trim());
                } else if (object.getString("serviceType").equalsIgnoreCase("Money_Transfer_Bene")) {
                    customDialog_Common("Fund Transfer Details", object, pozo, "VerifyLayout", input_name.getText().toString().trim());
                } else if (object.getString("serviceType").equalsIgnoreCase("Money_Transfer")) {
                    if (object.has("getTxnReceiptDataList")) {
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("Money Transfer", object, BCTransferFragment.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("Cannot generate receipt now please try later!", object, pozo, "VerifyLayout", input_name.getText().toString().trim());
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                    hideKeyboard(getActivity());
                    otp_layout.setVisibility(View.GONE);
                    btn_sender.setVisibility(View.GONE);
                    sender_layout.setVisibility(View.VISIBLE);
                    fundlayout.setVisibility(View.VISIBLE);
                    clear();
                    input_name.setText(object.getString("senderName"));
                    rv.findViewById(R.id.warning).setVisibility(View.GONE);
//                    reset.setVisibility(View.VISIBLE);
                    input_name.setEnabled(false);
                    if (object.has("remainingLimit") && !object.getString("remainingLimit").equalsIgnoreCase("null")) {
                        String split_limit[] = object.getString("remainingLimit").split("~");
                        for (int i = 0; i < split_limit.length; i++) {
                            limit = limit + Float.valueOf(split_limit[i]);
                        }
                        limit_title.setText("Available Limit : Rs " + object.getString("remainingLimit").replace("~", "+"));
                        limit_title.setVisibility(View.VISIBLE);
                    }

                    text_ben.setText("Beneficiary Details (Tap to Fund Transfer & Long press to delete)");
//                    if (object.has("oldTxnList")) {
//                        if (Integer.parseInt(object.getString("oldTxnCount")) > 0) {
//                            last_tran_layout.setVisibility(View.VISIBLE);
//                            insertLastTransDetails(object.getJSONArray("oldTxnList"));
//                        }
//                    }
                    if (object.has("beneListDetail")) {
                        if (Integer.parseInt(object.getString("beneCount")) > 0) {
                            beneficiary_layout.setVisibility(View.VISIBLE);
                            insertBenfDetails(object.getJSONArray("beneListDetail"));
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("Get_Txn_Recipt")) {
                    if (object.has("getTxnReceiptDataList")) {
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("Transaction Receipt", object, BCTransferFragment.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("Cannot generate receipt now please try later!", object, pozo, "VerifyLayout", input_name.getText().toString().trim());
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("ADD_BENEFICIARY_DETAILS")) {
                    customDialog_Common("KYCLAYOUTS", null, null, null, null, object.getString("responseMessage"), BCTransferFragment.this);
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
            case R.id.btn_search:
                hideKeyboard(getActivity());
                loadIMEI();
                break;
            case R.id.btn_fund:
                hideKeyboard(getActivity());
                addBeneDetails("FUNDTRANSFER", "Add Beneficiary Detail");
                break;
            case R.id.btn_sender:
                hideKeyboard(getActivity());
                if (!input_name.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, addSender().toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                else
                    Toast.makeText(getActivity(), "Please enter correct text", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_otpsubmit:
                if (!otpRefId.isEmpty() && !fund_transferId.isEmpty() && !input_otp.getText().toString().isEmpty()) {
                    hideKeyboard(getActivity());
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, add_OtpDetails(otpRefId, fund_transferId).toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                } else
                    Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.btn_verify:
//                hideKeyboard(FundTransferActivity.this);
//                if (bank_select.getText().toString().equalsIgnoreCase("Select Bank")) {
//                    bank_select.setError("Please enter mandatory field");
//                    bank_select.requestFocus();
//                } else if (!ImageUtils.commonAccount(input_account.getText().toString(), 5, 30)) {
//                    input_account.setError("Please enter valid account number.");
//                    input_account.requestFocus();
//                }else if (BaseCompactActivity.ENABLE_TPIN !=null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpin.getText().toString().isEmpty() || newtpin.getText().toString().length() != 4)) {
//                    newtpin.setError("Please enter TPIN");
//                    newtpin.requestFocus();
//                } else if (input_amount.getText().toString().isEmpty()) {
//                    new AsyncPostMethod(WebConfig.BCRemittanceApp, verify_Account().toString(), headerData, FundTransferActivity.this, getString(R.string.responseTimeOutTrans)).execute();
//                } else {
//                    Toast.makeText(this, "Input Amount is not applicable for verify account.", Toast.LENGTH_SHORT).show();
//                }
//                break;
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
        final Dialog dialognew = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        AppCompatButton btn_regenerate = (AppCompatButton) alertLayout.findViewById(R.id.btn_regenerate);
        try {
            if (type.equalsIgnoreCase("FUNDTRANSFER")) {
                dialog_cancel.setVisibility(View.VISIBLE);
//                btn_ok.setText("Add NEFT Beneficiary");
                btn_ok.setVisibility(View.GONE);
                btn_cancel.setTextSize(11);
                btn_regenerate.setTextSize(11);
                btn_cancel.setText("Add Beneficiary");
                btn_regenerate.setText(getResources().getString(R.string.btnverify));
                btn_regenerate.setVisibility(View.VISIBLE);
                alertLayout.findViewById(R.id.bc_add_bene_details).setVisibility(View.VISIBLE);
                bene_number = (TextView) alertLayout.findViewById(R.id.bc_bene_number);
                bene_name = (TextView) alertLayout.findViewById(R.id.bc_bene_name);
                con_ifsc = (TextView) alertLayout.findViewById(R.id.bc_con_bene_num);
                bank_select_bene = (TextView) alertLayout.findViewById(R.id.bank_select_bene);
                bank_select_bene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> list_bank = BaseCompactActivity.db.geBankDetails("");
                        customSpinner(bank_select_bene, "Select Bank", list_bank, "BC");
                    }
                });
                newtin = (EditText) alertLayout.findViewById(R.id.newpin);
//                if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y"))
//                    newtin.setVisibility(View.VISIBLE);
                dialognew.setContentView(alertLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialognew.setCancelable(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bank_select_bene.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select_bene.setError("Please enter mandatory field");
                    bank_select_bene.requestFocus();
                } else if (!ImageUtils.commonAccount(bene_number.getText().toString(), 8, 30)) {
                    bene_number.setError("Please enter valid beneficiary account number");
                    bene_number.requestFocus();
                } else if (bene_name.getText().toString().isEmpty()) {
                    bene_name.setError("Please enter valid data");
                    bene_name.requestFocus();
                } else if (isNEFT && !con_ifsc.getText().toString().matches("^[a-zA-Z0-9]{1,50}$")) {
                    con_ifsc.setError("Please enter valid IFSC number");
                    con_ifsc.requestFocus();
//                } else if (!bene_number.getText().toString().equalsIgnoreCase(con_bene_number.getText().toString())) {
//                    con_bene_number.setError("Account number not matched");
//                    con_bene_number.requestFocus();
                } else {
                    dialognew.dismiss();
                    if (isNEFT && con_ifsc.getText().toString().matches("^[a-zA-Z0-9]{1,50}$"))
                        new AsyncPostMethod(WebConfig.BCRemittanceApp, addBeneAccount(bene_number.getText().toString(), con_ifsc.getText().toString(), bene_name.getText().toString(), "D", "NEFT").toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                    else if (!isNEFT) {
                        String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select_bene.getText().toString() + "'";
                        ifsc_code = BaseCompactActivity.db.geBankIFSC(condition).get(0);
                        new AsyncPostMethod(WebConfig.BCRemittanceApp, addBeneAccount(bene_number.getText().toString(), ifsc_code, bene_name.getText().toString(), "D", "IMPS").toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                    }
                }
            }
        });
        btn_regenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bank_select_bene.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select_bene.setError("Please enter mandatory field");
                    bank_select_bene.requestFocus();
                } else if (!ImageUtils.commonAccount(bene_number.getText().toString(), 5, 30)) {
                    bene_number.setError("Please enter valid beneficiary account number");
                    bene_number.requestFocus();
                } else if (isNEFT && !con_ifsc.getText().toString().matches("^[a-zA-Z0-9]{1,50}$")) {
                    con_ifsc.setError("Please enter valid IFSC number");
                    con_ifsc.requestFocus();
//                } else if (!bene_number.getText().toString().equalsIgnoreCase(con_bene_number.getText().toString())) {
//                    con_bene_number.setError("Account number not matched");
//                    con_bene_number.requestFocus();
//                } else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtin.getText().toString().isEmpty() || newtin.getText().toString().length() != 4)) {
//                    newtin.setError("Please enter TPIN");
//                    newtin.requestFocus();
                } else {
                    dialognew.dismiss();
                    if (isNEFT && con_ifsc.getText().toString().matches("^[a-zA-Z0-9]{1,50}$"))
                        new AsyncPostMethod(WebConfig.BCRemittanceApp, verify_Account(con_ifsc.getText().toString(), bene_number.getText().toString()).toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                    else if (!isNEFT) {
                        String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select_bene.getText().toString() + "'";
                        ifsc_code = BaseCompactActivity.db.geBankIFSC(condition).get(0);
//                    if (newtin.getText().toString().isEmpty())
                        new AsyncPostMethod(WebConfig.BCRemittanceApp, verify_Account(ifsc_code, bene_number.getText().toString()).toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                    }
//                    else
//                        new AsyncPostMethod(WebConfig.BCRemittanceApp, verify_Account(ifsc_code, bene_number.getText().toString(), newtin.getText().toString()).toString(), headerData, FundTransferActivity.this, getString(R.string.responseTimeOutTrans)).execute();
                }
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognew.dismiss();
            }
        });
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bank_select_bene.getText().toString().equalsIgnoreCase("Select Bank")) {
//                    bank_select_bene.setError("Please enter mandatory field");
//                    bank_select_bene.requestFocus();
//                } else if (!ImageUtils.commonAccount(bene_number.getText().toString(), 8, 30)) {
//                    bene_number.setError("Please enter valid beneficiary account number");
//                    bene_number.requestFocus();
//                } else if (bene_name.getText().toString().isEmpty()) {
//                    bene_name.setError("Please enter valid data");
//                    bene_name.requestFocus();
////                } else if (!ImageUtils.commonAccount(con_bene_number.getText().toString(), 8, 30)) {
////                    con_bene_number.setError("Please enter valid confirm beneficiary account number");
////                    con_bene_number.requestFocus();
////                } else if (!bene_number.getText().toString().equalsIgnoreCase(con_bene_number.getText().toString())) {
////                    con_bene_number.setError("Account number not matched");
////                    con_bene_number.requestFocus();
//                } else {
//                    dialognew.dismiss();
//                    String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select_bene.getText().toString() + "'";
//                    ifsc_code = BaseCompactActivity.db.geBankIFSC(condition).get(0);
//                    customAddneft("Fund Transfer",ifsc_code, bene_number.getText().toString(),"Add Beneficiary?",bene_name.getText().toString(),bank_select_bene.getText().toString());
////                    customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", btn_ok.getText().toString());
//                }
//            }
//        });
        dialognew.show();
        Window window = dialognew.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void reset() {
        otp_layout.setVisibility(View.GONE);
        sender_layout.setVisibility(View.GONE);
        fundlayout.setVisibility(View.GONE);
        btn_sender.setVisibility(View.GONE);
//        reset.setVisibility(View.GONE);
        beneficiary_layout.setVisibility(View.GONE);
        last_tran_layout.setVisibility(View.GONE);
        bank_select.setText("Select Bank");
        input_name.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                reset();
                contactRead(data, input_mobile);
            }
        }
    }

    public JSONObject verify_Account(String ifsc, String accountno) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Verify_Account");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("senderName", input_name.getText().toString().trim());
            jsonObject.put("IFSC", ifsc);
            jsonObject.put("accountNo", accountno);
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", input_mobile.getText().toString());
            jsonObject.put("txnAmmount", "1");
            jsonObject.put("reqFor", "BC1");
//            if (newtpin.isEmpty())
//                jsonObject.put("tPin", "");
//            else
//                jsonObject.put("tPin", ImageUtils.encodeSHA256(newtpin));
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
                    beneficiaryDetailsPozoslist.add(new BeneficiaryDetailsPozo(object.getString("bank_ACCOUNT_NAME"), object.getString("account_NUMBER"), object.getString("account_IFSC"), object.getString("bank_Name"), object.getString("bc_BENE_ID"), object.getString("isVerified"), object.getString("isIMPS"), object.getString("isNEFT")));
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
                transactionPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), object.getString("serviceProviderTXNID"), object.getString("transferType"), object.getString("txnRequestDate")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeBenAdapter(ArrayList<BeneficiaryDetailsPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        beneficiary_details.setLayoutManager(layoutManager);
        adapter = new BCBeneficiaryAdapter(getActivity(), list);
        beneficiary_details.setAdapter(adapter);
    }

    private void initializeTransAdapter(ArrayList<LastTransactionPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new LastTransAdapter(getActivity(), trans_details, list));
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
            jsonObject.put("senderName", input_name.getText().toString().trim());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", input_mobile.getText().toString());
            jsonObject.put("beneficiaryId", beneficiaryId);
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("transferType", transfer_type);
            jsonObject.put("IFSC", pozo.getIfsc());
            if (newtpin.getText().toString().isEmpty())
                jsonObject.put("tPin", "");
            else
                jsonObject.put("tPin", ImageUtils.encodeSHA256(newtpin.getText().toString()));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    protected void customAddneft(final String type, String ifsc, final String accountNo, String msg, final String beneName, String beneBank) {
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        text.setText(msg);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("Fund Transfer")) {
            alertLayout.findViewById(R.id.addneftben_layout).setVisibility(View.VISIBLE);
            customAddBeneneft(alertLayout, ifsc, accountNo, beneName, beneBank, dialog, "NEFT");
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                if (type.equalsIgnoreCase("Fund Transfer")) {
                    if (btn_ifsc.getText().toString().isEmpty()) {
                        btn_ifsc.setError("Please enter valid IFSC Code.");
                        btn_ifsc.requestFocus();
                    } else {
//                        transfer_type=input;
                        new AsyncPostMethod(WebConfig.BCRemittanceApp, addBeneAccount(accountNo, btn_ifsc.getText().toString(), beneName, "D", "NEFT").toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                        dialog.dismiss();
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bank_select.setText("Select Bank");
                input_account.setText("");
                input_amount.setText("");
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    String transfer_type;

    protected void customFundTransfer(final String type, final JSONObject object, Object ob, String msg, final String input) {
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        text.setText(type);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("Fund Transfer")) {
            alertLayout.findViewById(R.id.ben_layout).setVisibility(View.VISIBLE);
            customDialog_Ben(alertLayout, (BeneficiaryDetailsPozo) ob, dialog, input);
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                if (type.equalsIgnoreCase("Fund Transfer")) {
                    if (!ImageUtils.commonAmount(ben_amount.getText().toString())) {
                        ben_amount.setError("Please enter valid amount.");
                        ben_amount.requestFocus();
                    } else if (Integer.parseInt(ben_amount.getText().toString()) > 25000) {
                        ben_amount.setError("Maximum transfer amount would be 25000.");
                        ben_amount.requestFocus();
                    } else if (Integer.parseInt(ben_amount.getText().toString()) > limit) {
                        ben_amount.setError("Maximum transfer amount would be " + limit + ".");
                        ben_amount.requestFocus();
                    } else {
                        transfer_type = input;
                        new AsyncPostMethod(WebConfig.BCRemittanceApp, service_fee(ben_amount.getText().toString(), "Money_Transfer_Bene").toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                        dialog.dismiss();
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bank_select.setText("Select Bank");
                input_account.setText("");
                input_amount.setText("");
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected void customDialog_Common(final String type, final JSONObject object, Object ob, String msg, String input) {
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        text.setText(msg);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        final AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        final AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("BENLAYOUT")) {
            btn_cancel.setText("NEFT");
            btn_cancel.setTextSize(10);
            btn_ok.setText("IMPS");
            btn_ok.setTextSize(10);
            if (pozo.getIsIMPS().equalsIgnoreCase("Y") && pozo.getIsNEFT().equalsIgnoreCase("Y")) {
                btn_ok.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.VISIBLE);
            } else if (!pozo.getIsNEFT().equalsIgnoreCase("Y")) {
                btn_cancel.setVisibility(View.GONE);
                btn_ok.setVisibility(View.VISIBLE);
            } else if (!pozo.getIsIMPS().equalsIgnoreCase("Y")) {
                btn_ok.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.VISIBLE);
            }
            dialog_cancel.setVisibility(View.VISIBLE);
            dialog.setContentView(alertLayout);
        }
        try {
            if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
                alertLayout.findViewById(R.id.custom_service).setVisibility(View.VISIBLE);
                if (!object.getString("subType").equalsIgnoreCase("Money_Transfer"))
                    serviceFee(alertLayout, object, (BeneficiaryDetailsPozo) ob, msg, input);
                else {
                    String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
                    ifsc_code = BaseCompactActivity.db.geBankIFSC(condition).get(0);
                    moneyTransgerFee(alertLayout, object, input_account.getText().toString(), ifsc_code, input_name.getText().toString().trim(), msg, input);
                }
            } else if (type.equalsIgnoreCase("Fund Transfer")) {
                alertLayout.findViewById(R.id.ben_layout).setVisibility(View.VISIBLE);
                customDialog_Ben(alertLayout, (BeneficiaryDetailsPozo) ob, dialog, "");
            } else if (type.equalsIgnoreCase("Beneficiary Details")) {
                btn_cancel.setVisibility(View.VISIBLE);
                customView(alertLayout, msg, dialog);
            } else if (msg.equalsIgnoreCase("VerifyLayout")) {
                if (type.equalsIgnoreCase("Fund Transfer Details"))
                    customReceiptNew(type, object, BCTransferFragment.this);
                else {
                    btn_ok.setText("Add Beneficiary");
                    alertLayout.findViewById(R.id.verifytransferlayout).setVisibility(View.VISIBLE);
                    verifyTransferFee(alertLayout, object);
                }
            } else if (type.equalsIgnoreCase("KYCLAYOUTLAY")) {
                text.setText(msg);
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, input, dialog);
            } else if (msg.equalsIgnoreCase("KYCLAYOUT")) {
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, input, dialog);
            } else if (type.equalsIgnoreCase("Money Transfer"))
                btn_cancel.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                if (type.equalsIgnoreCase("BENLAYOUT")) {
                    customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", btn_ok.getText().toString());
                    dialog.dismiss();
                } else if (type.equalsIgnoreCase("Fund Transfer Confirmation"))
                    try {
                        if (!object.getString("subType").equalsIgnoreCase("Money_Transfer")) {
                            if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && newtpin.getText().toString().length() == 4) {
                                new AsyncPostMethod(WebConfig.BCRemittanceApp, getMoney_Validate(ben_amount.getText().toString(), pozo.getBeneficiaryId()).toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                                dialog.dismiss();
                            } else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpin.getText().toString().isEmpty() || newtpin.getText().toString().length() != 4)) {
                                newtpin.setError("Please enter TPIN");
                                newtpin.requestFocus();
                            } else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("N")) {
                                new AsyncPostMethod(WebConfig.BCRemittanceApp, getMoney_Validate(ben_amount.getText().toString(), pozo.getBeneficiaryId()).toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                                dialog.dismiss();
                            }
                        } else {
                            new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getJson_Validate().toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else if (type.equalsIgnoreCase("Fund Transfer")) {
                    if (!ImageUtils.commonAmount(ben_amount.getText().toString())) {
                        ben_amount.setError("Please enter valid amount.");
                        ben_amount.requestFocus();
                    } else if (Integer.parseInt(ben_amount.getText().toString()) > 25000) {
                        ben_amount.setError("Maximum transfer amount would be 25000.");
                        ben_amount.requestFocus();
                    } else if (Integer.parseInt(ben_amount.getText().toString()) > limit) {
                        ben_amount.setError("Maximum transfer amount would be " + limit + ".");
                        ben_amount.requestFocus();
                    } else {
                        new AsyncPostMethod(WebConfig.BCRemittanceApp, service_fee(ben_amount.getText().toString(), "Money_Transfer_Bene").toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                        dialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("KYCLAYOUTLAY")) {
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate().toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
                    dialog.dismiss();
                } else if (type.equalsIgnoreCase("Beneficiary Details")) {
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, delete_Benef((BeneficiaryDetailsPozo) pozo).toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "DELETEBENEFICIARY").execute();
                    dialog.dismiss();
                } else if (type.equalsIgnoreCase("Money Transfer") || type.equalsIgnoreCase("Account Verify Details") || type.equalsIgnoreCase("Cannot generate receipt now please try later!")) {
                    input_account.setText("");
                    input_amount.setText("");
                    bank_select.setText("Select Bank");
                    dialog.dismiss();
                    if (type.equalsIgnoreCase("Account Verify Details")) {
                        try {
                            new AsyncPostMethod(WebConfig.BCRemittanceApp, addBeneAccount(object.getString("accountNo"), object.getString("ifscCode"), object.getString("bankAccountName"), "C", "IMPS").toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
//                            alertDialog.dismiss();
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
                if (type.equalsIgnoreCase("BENLAYOUT")) {
                    customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", btn_cancel.getText().toString());
                }
                bank_select.setText("Select Bank");
                input_account.setText("");
                input_amount.setText("");
                dialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("Money Transfer") || type.equalsIgnoreCase("Transaction Receipt")) {
            clear();
            receipt_clicked = false;
        } else if (type.equalsIgnoreCase("KYCLAYOUTS"))
            new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate().toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
        else if (type.equalsIgnoreCase("Fund Transfer Details")) {
//            localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
            new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate().toString(), headerData, BCTransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans)).execute();
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

}

