package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.rapipay.main_directory.Model.LastTransactionPozo;
import com.rapipay.android.rapipay.main_directory.adapter.BeneficiaryAdapter;
import com.rapipay.android.rapipay.main_directory.adapter.LastTransAdapter;
import com.rapipay.android.rapipay.main_directory.interfaces.ClickListener;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.RecyclerTouchListener;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PendingRefundActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener {

    EditText input_mobile;
    AppCompatButton btn_search;
    RecyclerView beneficiary_details, trans_details, pending_details, refund_details;
    LinearLayout beneficiary_layout, last_tran_layout, refund_tran_layout, pending_tran_layout;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<LastTransactionPozo> transactionPozoArrayList, pendingPozoArrayList, refundPozoArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_refund_layout);
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.heading);
        if (balance != null)
            heading.setText("Pending & Refund Transfer (Balance : Rs. " + balance + " )");
        else
            heading.setText("Pending & Refund Transfer");
        btn_search = (AppCompatButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        beneficiary_layout = (LinearLayout) findViewById(R.id.beneficiary_layout);
        beneficiary_details = (RecyclerView) findViewById(R.id.beneficiary_details);
//last tranction
        last_tran_layout = (LinearLayout) findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
        //        refund transaction
        refund_tran_layout = (LinearLayout) findViewById(R.id.refund_tran_layout);
        refund_details = (RecyclerView) findViewById(R.id.refund_details);
        refund_details.addOnItemTouchListener(new RecyclerTouchListener(this, refund_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LastTransactionPozo pozo = refundPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getrefund_Validate(pozo.getRefundTxnId()).toString(), headerData, PendingRefundActivity.this).execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
//        pending transaction
        pending_tran_layout = (LinearLayout) findViewById(R.id.pending_tran_layout);
        pending_details = (RecyclerView) findViewById(R.id.pending_details);
        pending_details.addOnItemTouchListener(new RecyclerTouchListener(this, pending_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LastTransactionPozo pozo = pendingPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getpending_Validate(pozo.getRefundTxnId()).toString(), headerData, PendingRefundActivity.this).execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.has("responseCode")) {
                if (object.getString("responseCode").equalsIgnoreCase("200") || object.getString("responseCode").equalsIgnoreCase("300")) {
                    if (object.getString("serviceType").equalsIgnoreCase("BC_Refund") && object.has("otpRefId"))
                        customDialog_Ben(object.getString("responseMessage"),object.getString("otpRefId"),object.getString("transactionId"));
                    else if (object.getString("serviceType").equalsIgnoreCase("BC_Refund"))
                        customDialog(object.getString("responseMessage"),input_mobile.getText().toString());
                    else
                        change_View(object);
                }
            } else if (object.has("responsecode")) {
                if (object.getString("responsecode").equalsIgnoreCase("300")) {
                    customDialog(object.getString("responseMessage"), object.getString("senderMobNo"));
                }
            } else if (object.getString("responsecode").equalsIgnoreCase("101")) {
                customDialog(object.getString("responseMessage"));
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
                if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
                    new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getSender_Validate(input_mobile.getText().toString()).toString(), headerData, PendingRefundActivity.this).execute();
                } else {
                    Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    public JSONObject getSender_Validate(String number) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("serviceType", "SENDER_COMPLETE_DETAILS");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", number);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getpending_Validate(String transactionID) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("serviceType", "GET_TXN_STATUS");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
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
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("serviceType", "BC_Refund");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("fundtransactionID", transactionID);
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
        beneficiary_details.setAdapter(new BeneficiaryAdapter(this, beneficiary_details, list));
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
                refundPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName")));
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
        refund_details.setAdapter(new LastTransAdapter(this, refund_details, list));
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

    private void customDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void customDialog(String msg, final String mobileno) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getSender_Validate(mobileno).toString(), headerData, PendingRefundActivity.this).execute();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void customDialog_Ben(String msg, final String otpRefId, final String transactionId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        dialog.setTitle(msg);
        View alertLayout = inflater.inflate(R.layout.refund_layout, null);
        final EditText text = (EditText) alertLayout.findViewById(R.id.input_amount_ben);
        int maxLength = 6;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(FilterArray);
        text.setHint("Enter OTP for Refund");
        dialog.setView(alertLayout);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!text.getText().toString().isEmpty() && text.length() == 6)
                    new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, add_OtpDetails(otpRefId, transactionId, text.getText().toString()).toString(), headerData, PendingRefundActivity.this).execute();
                else
                    Toast.makeText(PendingRefundActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public JSONObject add_OtpDetails(String otpRefId, String fund_transferId, String otp) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Verify_Mobile");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
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
}
