package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.rapipay.main_directory.Model.ChannelHistoryPozo;
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

public class FundTransferActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler {

    EditText input_amount, input_account, input_name, input_mobile, input_otp;
    AppCompatButton btn_search, btn_sender, btn_otpsubmit, btn_fund, btn_verify;
    LinearLayout sender_layout, otp_layout, fundlayout, beneficiary_layout, last_tran_layout;
    String otpRefId, fund_transferId, ifsc_code;
    Spinner bank_select;
    ArrayList<String> list_bank;
    RecyclerView beneficiary_details, trans_details;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<LastTransactionPozo> transactionPozoArrayList;
    BeneficiaryDetailsPozo pozo;
    String amount="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fundtransfer_layout);
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.heading);
        if (balance != null)
            heading.setText("Fund Transfer (Balance : Rs. " + balance + " )");
        else
            heading.setText("Fund Transfer");
        input_amount = (EditText) findViewById(R.id.input_amount);
        input_account = (EditText) findViewById(R.id.input_account);
        input_name = (EditText) findViewById(R.id.input_name);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_otp = (EditText) findViewById(R.id.input_otp);
        btn_search = (AppCompatButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        btn_fund = (AppCompatButton) findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        btn_verify = (AppCompatButton) findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
        btn_sender = (AppCompatButton) findViewById(R.id.btn_sender);
        btn_sender.setOnClickListener(this);
        sender_layout = (LinearLayout) findViewById(R.id.sender_layout);
        otp_layout = (LinearLayout) findViewById(R.id.otp_layout);
        btn_otpsubmit = (AppCompatButton) findViewById(R.id.btn_otpsubmit);
        btn_otpsubmit.setOnClickListener(this);
        fundlayout = (LinearLayout) findViewById(R.id.fundlayout);
        beneficiary_layout = (LinearLayout) findViewById(R.id.beneficiary_layout);
        beneficiary_details = (RecyclerView) findViewById(R.id.beneficiary_details);
//last tranction
        last_tran_layout = (LinearLayout) findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);

        bank_select = (Spinner) findViewById(R.id.bank_select);
        bank_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ifsc_code = list_bank.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        beneficiary_details.addOnItemTouchListener(new RecyclerTouchListener(this, beneficiary_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                pozo = beneficiaryDetailsPozoslist.get(position);
                customDialog_Ben(beneficiaryDetailsPozoslist.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(this, trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LastTransactionPozo pozo = transactionPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, receipt_request(pozo).toString(), headerData, FundTransferActivity.this).execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public JSONObject receipt_request(LastTransactionPozo pozo) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Get_Txn_Recipt");
            jsonObject.put("requestType", pozo.getTransferType());
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("orgTxnRef", pozo.getRefundTxnId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getJson_Validate() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (!input_name.getText().toString().isEmpty() && !ifsc_code.isEmpty()) {
            try {
                jsonObject.put("serviceType", "Money_Transfer");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", format.format(date));
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
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "SENDER_COMPLETE_DETAILS");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", format.format(date));
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject addSender() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10 && !input_name.getText().toString().isEmpty()) {
            try {
                jsonObject.put("serviceType", "ADD_SENDER_DETAILS");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", format.format(date));
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("senderName", input_name.getText().toString());
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
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10 && input_otp.getText().toString().length() == 6 && !input_otp.getText().toString().isEmpty() && !input_name.getText().toString().isEmpty()) {
            try {
                jsonObject.put("serviceType", "Verify_Mobile");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", format.format(date));
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("senderMobile", input_mobile.getText().toString());
                jsonObject.put("fundTransferId", fund_transferId);
                jsonObject.put("otp", input_otp.getText().toString());
                jsonObject.put("otprefID", otpRefId);
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
            if (object.getString("responseCode").equalsIgnoreCase("75077")) {
                if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                    btn_search.setVisibility(View.GONE);
                    sender_layout.setVisibility(View.VISIBLE);
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS") && object.has("otpRefId")) {
                    btn_search.setVisibility(View.GONE);
                    btn_sender.setVisibility(View.GONE);
                    otp_layout.setVisibility(View.VISIBLE);
                    otpRefId = object.getString("otpRefId");
                    fund_transferId = object.getString("transactionId");
                } else if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS")) {
                    new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getSender_Validate().toString(), headerData, FundTransferActivity.this).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_SERVICE_FEE")) {
                    customService(pozo,object.getString("chargeServiceFee"),object.getString("cgst"),object.getString("igst"),object.getString("sgst"));
                } else if (object.getString("serviceType").equalsIgnoreCase("Verify_Account")) {
                    customDialog("Account No :- " + object.getString("accountNo"));
                } else if (object.getString("serviceType").equalsIgnoreCase("Money_Transfer_Bene")) {
                    customDialog(object.getString("responseMessage"));
                } else if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                    otp_layout.setVisibility(View.GONE);
                    btn_search.setVisibility(View.GONE);
                    btn_sender.setVisibility(View.GONE);
                    sender_layout.setVisibility(View.VISIBLE);
                    fundlayout.setVisibility(View.VISIBLE);
                    input_name.setText(object.getString("senderName"));
                    list_bank = db.geBankDetails("");
                    if (list_bank.size() != 0) {
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, list_bank);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        bank_select.setAdapter(dataAdapter);
                    }
//                    if (list_bank.size() != 0) {
//                        SpinnerAdapters customAdapter = new SpinnerAdapters(getApplicationContext(), list_bank);
//                        bank_select.setAdapter(customAdapter);
//                    }
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
                }
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
                new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getSender_Validate().toString(), headerData, FundTransferActivity.this).execute();
                break;
            case R.id.btn_fund:
                hideKeyboard(FundTransferActivity.this);
                new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getJson_Validate().toString(), headerData, FundTransferActivity.this).execute();
                break;
            case R.id.btn_sender:
                hideKeyboard(FundTransferActivity.this);
                new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, addSender().toString(), headerData, FundTransferActivity.this).execute();
                break;
            case R.id.btn_otpsubmit:
                if (!otpRefId.isEmpty() && !fund_transferId.isEmpty()) {
                    hideKeyboard(FundTransferActivity.this);
                    new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, add_OtpDetails(otpRefId, fund_transferId).toString(), headerData, FundTransferActivity.this).execute();
                }
                break;
            case R.id.btn_verify:
                if (input_amount.getText().toString().isEmpty()) {
                    hideKeyboard(FundTransferActivity.this);
                    new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, verify_Account().toString(), headerData, FundTransferActivity.this).execute();
                } else {
                    Toast.makeText(this, "Amount is not applicable", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    public JSONObject verify_Account() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Verify_Account");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
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

    public JSONObject service_fee(String txnAmmount) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_SERVICE_FEE");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("subType", "Money_Transfer_Bene");
            jsonObject.put("txnAmmount", txnAmmount);
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
        beneficiary_details.setAdapter(new BeneficiaryAdapter(this, beneficiary_details, list));
    }

    private void initializeTransAdapter(ArrayList<LastTransactionPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new LastTransAdapter(this, trans_details, list));
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

    private void customDialog_Ben(final BeneficiaryDetailsPozo pozo) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_ben_layout, null);
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.btn_name);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.btn_account);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.btn_bank);
        if (!pozo.getAccountno().equalsIgnoreCase("null"))
            btn_account.setText(pozo.getAccountno());
        else
            btn_account.setText("NA");
        if (!pozo.getBank().equalsIgnoreCase("null"))
            btn_bank.setText(pozo.getBank());
        else
            btn_bank.setText("NA");
        if (!pozo.getName().equalsIgnoreCase("null"))
            btn_name.setText(pozo.getName());
        else
            btn_name.setText("NA");
        final TextView text = (TextView) alertLayout.findViewById(R.id.input_amount_ben);
        dialog.setView(alertLayout);
        dialog.setTitle(getResources().getString(R.string.app_name));

        dialog.setCancelable(true);
        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!text.getText().toString().isEmpty()) {
                    hideKeyboard(FundTransferActivity.this);
                    amount = text.getText().toString();
                    new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, service_fee(amount).toString(), headerData, FundTransferActivity.this).execute();
//                    confirmDialog("Sure you want to Transfer?", text, pozo);
                    dialog.dismiss();
                }
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

    private void confirmDialog(String msg, final String text, final BeneficiaryDetailsPozo pozo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getMoney_Validate(text, pozo.getBeneficiaryId()).toString(), headerData, FundTransferActivity.this).execute();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }


    public JSONObject getMoney_Validate(String amount, String beneficiaryId) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (!input_name.getText().toString().isEmpty() && !ifsc_code.isEmpty()) {
            try {
                jsonObject.put("serviceType", "Money_Transfer_Bene");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", format.format(date));
                jsonObject.put("txnAmmount", amount);
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("senderName", input_name.getText().toString());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("beneficiaryId", beneficiaryId);
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
    public void chechStat(String object) {

    }


    private void customService(final BeneficiaryDetailsPozo pozo,String serviceFee,String cgst,String igst, String sgst) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.service_layout, null);
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.btn_name);
        TextView btn_servicefee = (TextView) alertLayout.findViewById(R.id.btn_servicefee);
        btn_servicefee.setText(String.valueOf(Math.round(Double.parseDouble(serviceFee)* 100.0)/ 100.0));
        TextView btn_igst = (TextView) alertLayout.findViewById(R.id.btn_igst);
        btn_igst.setText(String.valueOf(Math.round(Double.parseDouble(igst)* 100.0)/ 100.0));
        TextView btn_cgst = (TextView) alertLayout.findViewById(R.id.btn_cgst);
        btn_cgst.setText(String.valueOf(Math.round(Double.parseDouble(cgst)* 100.0)/ 100.0));
        TextView btn_sgst = (TextView) alertLayout.findViewById(R.id.btn_sgst);
        btn_sgst.setText(String.valueOf(Math.round(Double.parseDouble(sgst)* 100.0)/ 100.0));
        TextView btn_sendname = (TextView) alertLayout.findViewById(R.id.btn_sendname);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.btn_account);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.btn_bank);
        if (!pozo.getAccountno().equalsIgnoreCase("null"))
            btn_account.setText(pozo.getAccountno());
        else
            btn_account.setText("NA");
            btn_sendname.setText(input_mobile.getText().toString());
        if (!pozo.getBank().equalsIgnoreCase("null"))
            btn_bank.setText(pozo.getBank());
        else
            btn_bank.setText("NA");
        if (!pozo.getName().equalsIgnoreCase("null"))
            btn_name.setText(pozo.getName());
        else
            btn_name.setText("NA");
        dialog.setView(alertLayout);
        dialog.setTitle(getResources().getString(R.string.app_name));

        dialog.setCancelable(true);
        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    hideKeyboard(FundTransferActivity.this);
                    confirmDialog("Sure you want to Transfer?", amount, pozo);
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

}
