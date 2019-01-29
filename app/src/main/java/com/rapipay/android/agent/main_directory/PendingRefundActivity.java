package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
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

import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.LastTransactionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BeneficiaryAdapter;
import com.rapipay.android.agent.adapter.LastTransAdapter;
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

public class PendingRefundActivity extends BaseCompactActivity implements RequestHandler, CustomInterface {

    EditText input_mobile;
    ImageView btn_search;
    RecyclerView beneficiary_details, trans_details, pending_details, refund_details;
    LinearLayout beneficiary_layout, last_tran_layout, refund_tran_layout, pending_tran_layout;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<LastTransactionPozo> transactionPozoArrayList, pendingPozoArrayList, refundPozoArrayList;
    int refundPosition;
    LastTransAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_refund_layout);
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Pending & Refund");
        btn_search = (ImageView) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                refundPosition = position;
                LastTransactionPozo pozo = refundPozoArrayList.get(position);
                if (!pozo.getServiceProviderTXNID().equalsIgnoreCase("DMT"))
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, getrefund_Validate(pozo.getRefundTxnId()).toString(), headerData, PendingRefundActivity.this).execute();
                else
                    new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, getrefundDmt(pozo).toString(), headerData, PendingRefundActivity.this).execute();
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
                LastTransactionPozo pozo = pendingPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.BCRemittanceApp, getpending_Validate(pozo.getRefundTxnId()).toString(), headerData, PendingRefundActivity.this).execute();
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
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate(input_mobile.getText().toString()).toString(), headerData, PendingRefundActivity.this).execute();
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

    private void change_View(JSONObject object) {
        try {
            if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS") || object.getString("serviceType").equalsIgnoreCase("GET_TXN_STATUS")) {
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
                        pending_tran_layout.setVisibility(View.VISIBLE);
                        insertPendingTransDetails(object.getJSONArray("pendingTxnList"));
                    }
                }
                if (object.has("beneListDetail")) {
                    if (Integer.parseInt(object.getString("beneCount")) > 0) {
                        beneficiary_layout.setVisibility(View.VISIBLE);
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
    public void chechStatus(JSONObject object) {
        try {
            if (object.has("responseCode")) {
                if (object.getString("responseCode").equalsIgnoreCase("75077")) {
                    if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                        customDialog_Common("KYCLAYOUTS", object, null, getResources().getString(R.string.Alert), null, object.getString("responseMsg"), PendingRefundActivity.this);
                    }
                } else if (object.getString("responseCode").equalsIgnoreCase("200") || object.getString("responseCode").equalsIgnoreCase("300")) {
                    if (object.getString("serviceType").equalsIgnoreCase("BC_Refund") && object.has("otpRefId"))
                        customDialog_Ben(object.getString("responseMessage"), object.getString("otpRefId"), object.getString("transactionId"), object.getString("serviceType"));
                    else if (object.getString("serviceType").equalsIgnoreCase("BC_Refund"))
                        customDialog_Common("PENDINGREFUND", object, null, "BC Refund", input_mobile.getText().toString(), object.getString("responseMessage"), PendingRefundActivity.this);
                    else if (object.getString("serviceType").equalsIgnoreCase("REFUND_TXN") && object.has("otpId"))
                        customDialog_Ben("Initiate Refund", object.getString("otpId"), "", object.getString("serviceType"));
                    else if (object.getString("serviceType").equalsIgnoreCase("REFUND_TXN")) {
                        localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
                        customDialog_Common("REFUNDTXN", object, null, "REFUND TXN", null, object.getString("output"), PendingRefundActivity.this);
                    } else
                        change_View(object);
                }
            } else if (object.has("responsecode")) {
                if (object.getString("responsecode").equalsIgnoreCase("75077")) {
                    if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                        customDialog_Common("KYCLAYOUTSS", object, null, getResources().getString(R.string.Alert), null, object.getString("responseMsg"), PendingRefundActivity.this);
                    }
                } else if (object.getString("responsecode").equalsIgnoreCase("300")) {
                    customDialog_Common("PENDINGREFUND", object, null, "Transaction Pending For Refund", object.getString("senderMobNo"), object.getString("responseMessage"), PendingRefundActivity.this);
                } else if (object.getString("responsecode").equalsIgnoreCase("101")) {
                    customDialog_Common("KYCLAYOUTSS", object, null, "Transaction Pending", null, object.getString("responseMessage"), PendingRefundActivity.this);
                } else if (object.getString("responsecode").equalsIgnoreCase("200")) {
                    customDialog_Common("KYCLAYOUTSS", object, null, "Transaction Status", null, object.getString("responseMessage"), PendingRefundActivity.this);
                }
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


    public JSONObject getSender_Validate(String number) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("serviceType", "SENDER_COMPLETE_DETAILS");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", number);
            jsonObject.put("reqFor", "BC1");
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

    public JSONObject getrefund_Validate(String transactionID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "BC_Refund");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("fundtransactionID", transactionID);
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
            jsonObject.put("serviceType", "REFUND_TXN");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionId", pozo.getRefundTxnId());
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("customerId", pozo.getTransferType());
            jsonObject.put("mobileNo", input_mobile.getText().toString());
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
                transactionPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName")));
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
                    refundPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), object.getString("refundType")));
                else
                    refundPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), object.getString("refundType"), object.getString("customerId")));
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
                pendingPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pendingPozoArrayList.size() != 0)
            initializePendingAdapter(pendingPozoArrayList);
    }

    private void initializePendingAdapter(ArrayList<LastTransactionPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pending_details.setLayoutManager(layoutManager);
        pending_details.setAdapter(new LastTransAdapter(this, pending_details, list));
    }

    int maxLength = 0;

    private void customDialog_Ben(String msg, final String otpRefId, final String transactionId, final String serviceType) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.refund_layout, null);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(msg);
        final EditText text = (EditText) alertLayout.findViewById(R.id.input_amount_ben);
        if (serviceType.equalsIgnoreCase("REFUND_TXN")) {
            maxLength = 4;
            text.setHint("Enter 4 digit OTP for Refund");
        } else if (serviceType.equalsIgnoreCase("BC_Refund")) {
            maxLength = 6;
            text.setHint("Enter 6 digit OTP for Refund");
        }
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(FilterArray);
        dialog.setView(alertLayout);
        dialog.setCancelable(false);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().isEmpty() && text.length() == 6 && !transactionId.equalsIgnoreCase("") && serviceType.equalsIgnoreCase("BC_Refund"))
                    new AsyncPostMethod(WebConfig.BCRemittanceApp, add_OtpDetails(otpRefId, transactionId, text.getText().toString()).toString(), headerData, PendingRefundActivity.this).execute();
                else if (!text.getText().toString().isEmpty() && text.length() == 4 && transactionId.equalsIgnoreCase("") && serviceType.equalsIgnoreCase("REFUND_TXN"))
                    new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, processOtp(text.getText().toString(), otpRefId).toString(), headerData, PendingRefundActivity.this).execute();
                else
                    Toast.makeText(PendingRefundActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }

    public JSONObject processOtp(String otp, String otpRefId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "PROCESS_OTP");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
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
            jsonObject.put("senderMobile", input_mobile.getText().toString());
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
    public void chechStat(String object) {

    }

    @Override
    public void okClicked(String type, Object ob) {
        if (!type.equalsIgnoreCase("PENDINGREFUND") && !type.equalsIgnoreCase("KYCLAYOUT") && !type.equalsIgnoreCase("REFUNDTXN") && !type.equalsIgnoreCase("KYCLAYOUTS") && !type.equalsIgnoreCase("KYCLAYOUTSS"))
            new AsyncPostMethod(WebConfig.BCRemittanceApp, getSender_Validate(type).toString(), headerData, PendingRefundActivity.this).execute();
        else if (type.equalsIgnoreCase("REFUNDTXN")) {
            refundPozoArrayList.remove(refundPosition);
            adapter.notifyDataSetChanged();
            hideKeyboard(PendingRefundActivity.this);
        } else if (type.equalsIgnoreCase("KYCLAYOUTS"))
            input_mobile.setText("");
        input_mobile.setHint(getResources().getString(R.string.p_cususerid));
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
