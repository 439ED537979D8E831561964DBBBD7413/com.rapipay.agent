package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.BankDetailsPozo;
import com.rapipay.android.rapipay.main_directory.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.rapipay.main_directory.Model.LastTransactionPozo;
import com.rapipay.android.rapipay.main_directory.interfaces.ClickListener;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.RecyclerTouchListener;
import com.rapipay.android.rapipay.main_directory.utils.RouteClass;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WalletDetailsActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler {

    EditText input_amount, input_account, input_name, input_mobile, input_otp;
    AppCompatButton btn_search, btn_sender, btn_otpsubmit;
    LinearLayout sender_layout, otp_layout, fundlayout, beneficiary_layout, last_tran_layout;
    String otpRefId, fund_transferId, ifsc_code;
    Spinner bank_select;
    ArrayList<BankDetailsPozo> list_bank;
    RecyclerView beneficiary_details, trans_details;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<LastTransactionPozo> transactionPozoArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_details_layout);
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.heading);
        if (balance != null)
            heading.setText("Wallet Transfer (Balance : Rs. " + balance + " )");
        else
            heading.setText("Wallet Transfer");
        input_amount = (EditText) findViewById(R.id.input_amount);
        input_account = (EditText) findViewById(R.id.input_account);
        input_name = (EditText) findViewById(R.id.input_name);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_otp = (EditText) findViewById(R.id.input_otp);
        btn_search = (AppCompatButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        btn_sender = (AppCompatButton) findViewById(R.id.btn_sender);
        btn_sender.setOnClickListener(this);
        sender_layout = (LinearLayout) findViewById(R.id.sender_layout);
        otp_layout = (LinearLayout) findViewById(R.id.otp_layout);
        btn_otpsubmit = (AppCompatButton) findViewById(R.id.btn_otpsubmit);
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
                ifsc_code = list_bank.get(position).getIfsc();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        beneficiary_details.addOnItemTouchListener(new RecyclerTouchListener(this, beneficiary_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                customDialog_Ben(beneficiaryDetailsPozoslist.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_otpsubmit:
                if (!otpRefId.isEmpty() && !input_otp.getText().toString().isEmpty()) {
                    hideKeyboard(WalletDetailsActivity.this);
                    new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, processOtp().toString(), headerData, WalletDetailsActivity.this).execute();
                }
                break;
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_search:
                new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, getSender_Validate().toString(), headerData, WalletDetailsActivity.this).execute();
                break;
        }
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("serviceType").equalsIgnoreCase("GET_WALLET_STATUS")) {
                if (object.getString("responseCode").equalsIgnoreCase("75061")) {
                    confirmDialog(object.getString("responseMsg") + "\n" + "Do you want to continue for KYC", object.getString("responseCode"));
                } else if (object.getString("responseCode").equalsIgnoreCase("75063")) {
                    confirmDialog(object.getString("responseMsg"), object.getString("responseCode"));
                } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    if (object.getString("otpId").length() > 0) {
                        otp_layout.setVisibility(View.VISIBLE);
                        btn_search.setVisibility(View.GONE);
                        btn_sender.setVisibility(View.GONE);
                        otpRefId = object.getString("otpId");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmDialog(String msg, final String code) {
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
                        if (code.equalsIgnoreCase("75061"))
                            callKYC();
//                        else if (code.equalsIgnoreCase("75063"))
//
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void callKYC() {
        Intent intent = new Intent(WalletDetailsActivity.this, WebViewClientActivity.class);
        intent.putExtra("mobileNo", input_mobile.getText().toString());
        intent.putExtra("parentId", list.get(0).getMobilno());
        intent.putExtra("sessionKey", list.get(0).getPinsession());
        intent.putExtra("sessionRefNo", list.get(0).getAftersessionRefNo());
        intent.putExtra("nodeAgent", list.get(0).getMobilno());
        startActivity(intent);
    }

    public JSONObject getSender_Validate() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "GET_WALLET_STATUS");
                jsonObject.put("requestType", "DMT_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRef", format.format(date));
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

    public JSONObject processOtp() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("serviceType", "PROCESS_OTP");
                jsonObject.put("requestType", "DMT_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRef", format.format(date));
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNo", input_mobile.getText().toString());
                jsonObject.put("otpId", otpRefId);
                jsonObject.put("otp", input_otp.getText().toString());
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
