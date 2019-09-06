package com.rapipay.android.agent.main_directory;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
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

import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.LastTransactionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BeneficiaryAdapter;
import com.rapipay.android.agent.adapter.LastTransAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.WalletRequestHandler;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WalletAsyncMethod;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PendingRefundActivity extends BaseCompactActivity implements WalletRequestHandler, CustomInterface {

    EditText input_mobile;
    ImageView btn_search;
    RecyclerView beneficiary_details, trans_details, pending_details, refund_details;
    LinearLayout beneficiary_layout, last_tran_layout, refund_tran_layout, pending_tran_layout;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<LastTransactionPozo> transactionPozoArrayList, pendingPozoArrayList, refundPozoArrayList;
    int refundPosition;
    LastTransAdapter adapter;
    LastTransactionPozo pozo;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_refund_layout);
        initialize();
        new WalletAsyncMethod(WebConfig.COMMONAPI, getSender_Validate("").toString(), headerData, PendingRefundActivity.this, getString(R.string.responseTimeOut), "GET_PENDING_REFUND_LIST").execute();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Pending & Refund");
        btn_search = (ImageView) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_search.setClickable(false);
                hideKeyboard(PendingRefundActivity.this);
                loadIMEI();
            }
        });
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        beneficiary_layout = (LinearLayout) findViewById(R.id.beneficiary_layout);
        beneficiary_details = (RecyclerView) findViewById(R.id.beneficiary_details);
        last_tran_layout = (LinearLayout) findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
        refund_tran_layout = (LinearLayout) findViewById(R.id.refund_tran_layout);
        refund_details = (RecyclerView) findViewById(R.id.refund_details);
        refund_details.addOnItemTouchListener(new RecyclerTouchListener(this, refund_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                mLastClickTime = now;
                refundPosition = position;
                pozo = refundPozoArrayList.get(position);
                if (!pozo.getServiceProviderTXNID().equalsIgnoreCase("DMT"))
                    new WalletAsyncMethod(WebConfig.BCRemittanceApp, getrefund_Validate(pozo).toString(), headerData, PendingRefundActivity.this, getString(R.string.responseTimeOut), "BC_Refund").execute();
                else
                    new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, getrefundDmt(pozo).toString(), headerData, PendingRefundActivity.this, getString(R.string.responseTimeOut), "WALLET_REFUND").execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        pending_tran_layout = (LinearLayout) findViewById(R.id.pending_tran_layout);
        pending_details = (RecyclerView) findViewById(R.id.pending_details);
        pending_details.addOnItemTouchListener(new RecyclerTouchListener(this, pending_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                mLastClickTime = now;
                LastTransactionPozo pozo = pendingPozoArrayList.get(position);
                new WalletAsyncMethod(WebConfig.BCRemittanceApp, getpending_Validate(pozo.getRefundTxnId()).toString(), headerData, PendingRefundActivity.this, getString(R.string.responseTimeOut), "GET_TXN_STATUS").execute();

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
                    new WalletAsyncMethod(WebConfig.COMMONAPI, getSender_Validate(input_mobile.getText().toString()).toString(), headerData, PendingRefundActivity.this, getString(R.string.responseTimeOut), "GET_PENDING_REFUND_LIST").execute();
                else
                    reset();
            }
        });
        findViewById(R.id.back_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBack_click(PendingRefundActivity.this);
                finish();
            }
        });
    }

    public boolean isClickable = false;

    public static boolean setClickable(View view, boolean clickable) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setClickable(viewGroup.getChildAt(i), clickable);
                }
            }
            view.setClickable(clickable);
        }
        return clickable;
    }

    private void change_View(JSONObject object, String hitfrom) {
        try {
            if (hitfrom.equalsIgnoreCase("GET_PENDING_REFUND_LIST") || hitfrom.equalsIgnoreCase("GET_TXN_STATUS")) {
                last_tran_layout.setVisibility(View.GONE);
                refund_tran_layout.setVisibility(View.GONE);
                pending_tran_layout.setVisibility(View.GONE);
                beneficiary_layout.setVisibility(View.GONE);
                if (object.has("oldTxnList")) {
                    if (Integer.parseInt(object.getString("oldTxnCount")) > 0) {
                        last_tran_layout.setVisibility(View.VISIBLE);
                        insertLastTransDetails(object.getJSONArray("oldTxnList"));
                    }
                }
                if (object.has("refundTxnList")) {
                    if (Integer.parseInt(object.getString("refundCount")) > 0) {
                        refund_tran_layout.setVisibility(View.VISIBLE);
                        insertRefundTransDetails(object.getJSONArray("refundTxnList"));
                    }
                }
                if (object.has("pendingTxnList")) {
                    if (Integer.parseInt(object.getString("pendingTxnCount")) > 0) {
                        insertPendingTransDetails(object.getJSONArray("pendingTxnList"));
                    }
                }
                if (object.has("beneListDetail")) {
                    if (Integer.parseInt(object.getString("beneCount")) > 0) {
                        insertBenfDetails(object.getJSONArray("beneListDetail"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reset() {
        last_tran_layout.setVisibility(View.GONE);
        beneficiary_layout.setVisibility(View.GONE);
        refund_tran_layout.setVisibility(View.GONE);
        pending_tran_layout.setVisibility(View.GONE);
    }

    @Override
    public void chechStatus(JSONObject object, String hitfrom) {
        try {
            if (object.has("responseCode")) {
                if (object.getString("responseCode").equalsIgnoreCase("75077")) {
                    if (hitfrom.equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                        customDialog_Common("KYCLAYOUTS", object, null, getResources().getString(R.string.Alert), null, object.getString("responseMsg"), PendingRefundActivity.this);
                    }
                } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                    Toast.makeText(this,object.getString("responseCode"),Toast.LENGTH_LONG).show();
                    setBack_click1(this);
                } else if (object.getString("responseCode").equalsIgnoreCase("200") || object.getString("responseCode").equalsIgnoreCase("300")) {
                    if (hitfrom.equalsIgnoreCase("BC_Refund") && object.has("otpRefId") && object.getString("otpRefId").equalsIgnoreCase("null"))
                        customDialog_Common("KYCEWLAYOUT", null, null, "Alert", "", object.getString("responseMessage"), PendingRefundActivity.this);
                    else if (hitfrom.equalsIgnoreCase("BC_Refund"))
                        customDialog_Common("PENDINGREFUND", object, null, "BC Refund", input_mobile.getText().toString(), object.getString("responseMessage"), PendingRefundActivity.this);
                    else if (hitfrom.equalsIgnoreCase("WALLET_REFUND") && object.has("otpRefId")) {
                        pending_details.setClickable(false);
                        customDialog_Ben("Initiate Refund", object.getString("otpRefId"), "", hitfrom);
                    } else if (hitfrom.equalsIgnoreCase("PROCESS_OTP")) {
                        dialog.dismiss();
                        customDialog_Common("REFUNDTXN", object, null, "REFUND TXN", null, object.getString("responseMessage"), PendingRefundActivity.this);
                    } else if (hitfrom.equalsIgnoreCase("Verify_Mobile")) {
                        dialog.dismiss();
                        customDialog_Common("KYCLAYOUTSS", object, null, getResources().getString(R.string.Alert), null, object.getString("responseMessage"), PendingRefundActivity.this);
                    } else
                        change_View(object, hitfrom);
                } else if (object.getString("responseCode").equalsIgnoreCase("201")) {
                    if (hitfrom.equalsIgnoreCase("BC_Refund") && object.has("otpRefId")) {
                        customDialog_Ben("Initiate Refund", object.getString("otpRefId"), object.getString("transactionId"), hitfrom);
                        pending_details.setClickable(false);
                    }
                } else {
                    responseMSg(object);
                }
            } else if (object.has("responsecode")) {
                if (object.getString("responsecode").equalsIgnoreCase("75077")) {
                    if (hitfrom.equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                        customDialog_Common("KYCLAYOUTSS", object, null, getResources().getString(R.string.Alert), null, object.getString("responseMsg"), PendingRefundActivity.this);
                    }
                } else if (object.getString("responsecode").equalsIgnoreCase("300")) {
                    customDialog_Common("PENDINGREFUND", object, null, "Transaction Pending For Refund", object.getString("senderMobNo"), object.getString("responseMessage"), PendingRefundActivity.this);
                } else if (object.getString("responsecode").equalsIgnoreCase("101")) {
                    customDialog_Common("KYCLAYOUTSS", object, null, "Transaction Pending", null, object.getString("responseMessage"), PendingRefundActivity.this);
                } else if (object.getString("responsecode").equalsIgnoreCase("200")) {
                    customDialog_Common("KYCLAYOUTSS", object, null, "Transaction Status", null, object.getString("responseMessage"), PendingRefundActivity.this);
                } else {
                    responseMSg(object);
                }
            } else {
                responseMSg(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public void clickable() {
        try {
            refund_details.setClickable(true);
            refund_details.setClickable(true);
            btn_search.setClickable(true);
            btn_ok.setClickable(true);
            btn_cancel.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        clickable();
        super.onPause();
    }

    @Override
    protected void onResume() {
        clickable();
        super.onResume();
    }

    public JSONObject getSender_Validate(String number) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("serviceType", "GET_PENDING_REFUND_LIST");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", number);
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getpending_Validate(String transactionID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_TXN_STATUS");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("fundTransferId", transactionID);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getrefund_Validate(LastTransactionPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "BC_Refund");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("fundtransactionID", pozo.getRefundTxnId());
            jsonObject.put("senderMobile", pozo.getCustomerId());
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getrefundDmt(LastTransactionPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "WALLET_REFUND");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("fundtransactionID", pozo.getRefundTxnId());
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("txnIP", ImageUtils.ipAddress(PendingRefundActivity.this));
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("reqFor", "WALLET");
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
                if (!object.getString("bc_BENE_ID").isEmpty() && !object.getString("bc_BENE_ID").equalsIgnoreCase("null"))
                    beneficiaryDetailsPozoslist.add(new BeneficiaryDetailsPozo(object.getString("bank_ACCOUNT_NAME"), object.getString("account_NUMBER"), object.getString("account_IFSC"), object.getString("bank_Name"), object.getString("bc_BENE_ID")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (beneficiaryDetailsPozoslist.size() != 0) {
            beneficiary_layout.setVisibility(View.VISIBLE);
            initializeBenAdapter(beneficiaryDetailsPozoslist);
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), ""));
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

    private void insertRefundTransDetails(JSONArray array) {
        refundPozoArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("refundType").equalsIgnoreCase("BC"))
                    refundPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), object.getString("refundType"), object.getString("txnRequestDate"), object.getString("customerId")));
                else
                    refundPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), object.getString("refundType"), object.getString("customerId"), object.getString("txnRequestDate"), object.getString("customerId")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (refundPozoArrayList.size() != 0)
            initializeRefundAdapter(refundPozoArrayList);
    }

    private void initializeRefundAdapter(ArrayList<LastTransactionPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        refund_details.setLayoutManager(layoutManager);
        adapter = new LastTransAdapter(this, refund_details, list);
        refund_details.setAdapter(adapter);
    }

    private void insertPendingTransDetails(JSONArray array) {
        pendingPozoArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                pendingPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), object.getString("txnRequestDate")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pendingPozoArrayList.size() != 0) {
            pending_tran_layout.setVisibility(View.VISIBLE);
            initializePendingAdapter(pendingPozoArrayList);
        }
    }

    private void initializePendingAdapter(ArrayList<LastTransactionPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pending_details.setLayoutManager(layoutManager);
        pending_details.setAdapter(new LastTransAdapter(this, pending_details, list));
    }

    int maxLength = 0;
    AppCompatButton btn_cancel, btn_ok;

    private void customDialog_Ben(String msg, final String otpRefId, final String transactionId, final String serviceType) {
        dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.refund_layout, null);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(msg);
        final EditText text = (EditText) alertLayout.findViewById(R.id.input_amount_ben);
        if (serviceType.equalsIgnoreCase("WALLET_REFUND")) {
            maxLength = 6;
            text.setHint("Enter 6 digit OTP for Refund");
        } else if (serviceType.equalsIgnoreCase("BC_Refund")) {
            maxLength = 6;
            text.setHint("Enter 6 digit OTP for Refund");
        }
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(FilterArray);
        dialog.setContentView(alertLayout);
        dialog.setCancelable(false);
        btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().isEmpty() && text.length() == 6 && !transactionId.equalsIgnoreCase("") && serviceType.equalsIgnoreCase("BC_Refund")) {
                    new WalletAsyncMethod(WebConfig.BCRemittanceApp, add_OtpDetails(otpRefId, transactionId, text.getText().toString()).toString(), headerData, PendingRefundActivity.this, getString(R.string.responseTimeOut), "Verify_Mobile").execute();
                } else if (!text.getText().toString().isEmpty() && text.length() == 6 && transactionId.equalsIgnoreCase("") && serviceType.equalsIgnoreCase("WALLET_REFUND")) {
                    new WalletAsyncMethod(WebConfig.WALLETTRANSFER_URL, processOtp(text.getText().toString(), otpRefId).toString(), headerData, PendingRefundActivity.this, getString(R.string.responseTimeOut), "PROCESS_OTP").execute();
                } else {
                    text.setError("Please Enter Otp");
                    text.requestFocus();
                    btn_cancel.setClickable(true);
                    Toast.makeText(PendingRefundActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public JSONObject processOtp(String otp, String otpRefId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "PROCESS_OTP");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("senderMobileNo", pozo.getCustomerId());
            jsonObject.put("otpRefId", otpRefId);
            jsonObject.put("otp", otp);
            jsonObject.put("reqFor", "WALLET");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject add_OtpDetails(String otpRefId, String fund_transferId, String otp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Verify_Mobile");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("senderMobile", pozo.getCustomerId());
            jsonObject.put("fundTransferId", fund_transferId);
            jsonObject.put("otp", otp);
            jsonObject.put("otprefID", otpRefId);
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
    public void chechStat(String object, String hitfrom) {

    }

    @Override
    public void okClicked(String type, Object ob) {
        if (!type.equalsIgnoreCase("PENDINGREFUND") && !type.equalsIgnoreCase("KYCLAYOUT") && !type.equalsIgnoreCase("REFUNDTXN") && !type.equalsIgnoreCase("KYCLAYOUTS") && !type.equalsIgnoreCase("KYCLAYOUTSS") && !type.equalsIgnoreCase("KYCEWLAYOUT"))
            new WalletAsyncMethod(WebConfig.COMMONAPI, getSender_Validate(type).toString(), headerData, PendingRefundActivity.this, getString(R.string.responseTimeOut), "GET_PENDING_REFUND_LIST").execute();
        else if (type.equalsIgnoreCase("REFUNDTXN") || type.equalsIgnoreCase("KYCEWLAYOUT")) {
            refundPozoArrayList.remove(refundPosition);
            adapter.notifyDataSetChanged();
            hideKeyboard(PendingRefundActivity.this);
        } else if (type.equalsIgnoreCase("KYCLAYOUTS")) {
            input_mobile.setText("");
            input_mobile.setHint(getResources().getString(R.string.p_cususerid));
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

}
