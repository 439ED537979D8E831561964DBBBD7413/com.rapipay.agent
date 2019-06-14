package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.pnsol.sdk.auth.AccountValidator;
import com.pnsol.sdk.interfaces.DeviceType;
import com.pnsol.sdk.interfaces.PaymentTransactionConstants;
import com.pnsol.sdk.miura.emv.tlv.ISOUtil;
import com.pnsol.sdk.payment.PaymentInitialization;
import com.pnsol.sdk.vo.AcquirerEmiDetailsVO;
import com.pnsol.sdk.vo.HostResponse;
import com.pnsol.sdk.vo.TransactionVO;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.AcquirerBanksListAdapter;
import com.rapipay.android.agent.adapter.EMITenureAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.WalletRequestHandler;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.CustomProgessDialog;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WalletAsyncMethod;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.CHIP_TRANSACTION_APPROVED;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.CHIP_TRANSACTION_DECLINED;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.DISPLAY_STATUS;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.ERROR_MESSAGE;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.FAIL;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.QPOS_DEVICE;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.QPOS_EMV_MULITPLE_APPLICATION;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.SOCKET_NOT_CONNECTED;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.SUCCESS;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.SWIP_TRANSACTION_APPROVED;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.SWIP_TRANSACTION_DECLINED;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.TRANSACTION_FAILED;
import static com.pnsol.sdk.interfaces.PaymentTransactionConstants.TRANSACTION_PENDING;

public class CashOutClass extends BaseCompactActivity implements View.OnClickListener, WalletRequestHandler, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CustomInterface {
    Handler handler;
    EditText input_mobile, input_amount;
    AppCompatButton btn_fund_mpos, receipt_details;
    ArrayList<AcquirerEmiDetailsVO> arrayLists;
    AccountValidator validator;
    String serviceType, requestChannel, requestType, reqFor, typeput;
    String tid = "", mid = "", orderID = "";
    JSONObject requestData = null;
    HostResponse type = null;
    String sign;
    private PaymentInitialization initialization;
    private String check = "", mobileNo, amount;
    ArrayList<AcquirerEmiDetailsVO> acquirerBanksList;
    private Spinner select_bank;
    private LinearLayout inflate_tenureee;
    private CustomProgessDialog progessDialog;
    private ListView lv_imflate;
    private AcquirerEmiDetailsVO vo = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashout_layout);
        mobileNo = getIntent().getStringExtra("mobileNo");
        amount = getIntent().getStringExtra("amount");
        typeput = getIntent().getStringExtra("typeput");
        serviceType = getIntent().getStringExtra("serviceType");
        requestChannel = getIntent().getStringExtra("requestChannel");
        requestType = getIntent().getStringExtra("requestType");
        reqFor = getIntent().getStringExtra("reqFor");
        setUpGClient();
        initialize_hanlder();
        initialize();
    }

    private void bankList() {
        initialization = new PaymentInitialization(getApplicationContext());
        initialization.getEMIBankList(handler);
        check = "list";
    }

    private void initialize_hanlder() {
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == SUCCESS) {
                    if (typeput.equalsIgnoreCase("EMI")) {
                        if (check.equalsIgnoreCase("lastvalidate")) {
                            progessDialog.hide_progress();
                            type = (HostResponse) msg.obj;
                            try {
                                Gson gson = new Gson();
                                String json = gson.toJson(type);
                                new WalletAsyncMethod(WebConfig.CASHOUT_URL, updateCashOutDetails(json).toString(), headerData, CashOutClass.this, getString(R.string.responseTimeOut), "UPDATE_MPOS").execute();
                            } catch (Exception e) {

                            }
                        } else if (check.equalsIgnoreCase("validate")) {
                            bankList();
                        } else if (check.equalsIgnoreCase("list")) {
                            progessDialog.hide_progress();
                            acquirerBanksList = new ArrayList<AcquirerEmiDetailsVO>();
                            acquirerBanksList = (ArrayList<AcquirerEmiDetailsVO>) msg.obj;
                            AcquirerBanksListAdapter adapter = new
                                    AcquirerBanksListAdapter(CashOutClass.this, acquirerBanksList);
                            select_bank.setVisibility(View.VISIBLE);
                            select_bank.setAdapter(adapter);
                            findViewById(R.id.btn_fund_mpos).setVisibility(View.GONE);
                            findViewById(R.id.getEmidetails).setVisibility(View.GONE);
                        } else if (check.equalsIgnoreCase("EMIDETAILS")) {
                            progessDialog.hide_progress();
                            arrayLists = new ArrayList<AcquirerEmiDetailsVO>();
                            vo = null;
                            arrayLists = (ArrayList<AcquirerEmiDetailsVO>) msg.obj;
                            findViewById(R.id.inflate_main).setVisibility(View.VISIBLE);
                            findViewById(R.id.getdetails).setVisibility(View.VISIBLE);
                            findViewById(R.id.getEmidetails).setVisibility(View.GONE);
                            if (arrayLists.size() != 0)
                                inflate_tenure(arrayLists, inflate_tenureee);
                        }
                    } else if (!typeput.equalsIgnoreCase("EMI")) {
                        if (!transactionFlag) {
                            try {
                                initialization = new PaymentInitialization(CashOutClass.this);
                                if (typeput.equalsIgnoreCase("CASHOUT"))
                                    initialization.initiateTransaction(handler, requestData.getInt("DEVICE_TYPE"), requestData.getString("BLUE_TOOTH_ID"), requestData.getString("TXN_AMOUNT") + ".00", requestData.getString("CAS_AT_POS"), requestData.getString("PAYMENT_TXN_CONST"),
                                            requestData.getString("AGENT_MOIBLE"), requestData.getDouble("LONGTITUDE"), requestData.getDouble("LATITUDE"), requestData.getString("ORDER_ID"), null, requestData.getInt("DEVICE_COM_MODE"));
                                else if (typeput.equalsIgnoreCase("SALE"))
                                    initialization.initiateTransaction(handler, requestData.getInt("DEVICE_TYPE"), requestData.getString("BLUE_TOOTH_ID"), requestData.getString("TXN_AMOUNT") + ".00", requestData.getString("CAS_AT_POS"), requestData.getString("PAYMENT_TXN_CONST"),
                                            requestData.getString("AGENT_MOIBLE"), requestData.getDouble("LONGTITUDE"), requestData.getDouble("LATITUDE"), requestData.getString("ORDER_ID"), null, requestData.getInt("DEVICE_COM_MODE"));
                                else if (typeput.equalsIgnoreCase("EMI"))
                                    initialization.initiateTransaction(handler, requestData.getInt("DEVICE_TYPE"), requestData.getString("BLUE_TOOTH_ID"), requestData.getString("TXN_AMOUNT") + ".00", requestData.getString("CAS_AT_POS"), requestData.getString("PAYMENT_TXN_CONST"),
                                            requestData.getString("AGENT_MOIBLE"), requestData.getDouble("LONGTITUDE"), requestData.getDouble("LATITUDE"), requestData.getString("ORDER_ID"), null, acquirerEmiDetailsVO, requestData.getInt("DEVICE_COM_MODE"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (transactionFlag) {
                            progessDialog.hide_progress();
                            try {
                                if (accessBluetoothDetails() != null) {
                                    type = (HostResponse) msg.obj;
                                    Gson gson = new Gson();
                                    String json = gson.toJson(type);
                                    new WalletAsyncMethod(WebConfig.CASHOUT_URL, updateCashOutDetails(json).toString(), headerData, CashOutClass.this, getString(R.string.responseTimeOut), "UPDATE_MPOS").execute();
                                } else
                                    Toast.makeText(CashOutClass.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {

                            }
                        }
                    }
                }
                if (msg.what == FAIL) {
                    try {
                        clear();
                        progessDialog.hide_progress();
                        transactionFlag = false;
                        customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", (String) msg.obj, CashOutClass.this);
//                        Toast.makeText(CashOutClass.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        if (accessBluetoothDetails() != null) {
                            JSONObject object = new JSONObject();
                            object.put("responseCode", msg.what);
                            object.put("responseValue", msg.obj);
                            new WalletAsyncMethod(WebConfig.CASHOUT_URL, updateCashOutDetails(object.toString()).toString(), headerData, CashOutClass.this, getString(R.string.responseTimeOut), "UPDATE_MPOS").execute();
                        } else
                            Toast.makeText(CashOutClass.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                }
                if (msg.what == SOCKET_NOT_CONNECTED)

                {
                    try {
                        progessDialog.hide_progress();
                        transactionFlag = false;
                        clear();
                        customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", (String) msg.obj, CashOutClass.this);
//                        Toast.makeText(CashOutClass.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        if (accessBluetoothDetails() != null) {
                            JSONObject object = new JSONObject();
                            object.put("responseCode", msg.what);
                            object.put("responseValue", msg.obj);
                            new WalletAsyncMethod(WebConfig.CASHOUT_URL, updateCashOutDetails(object.toString()).toString(), headerData, CashOutClass.this, getString(R.string.responseTimeOut), "UPDATE_MPOS").execute();
                        } else
                            Toast.makeText(CashOutClass.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                }
                if (msg.what == CHIP_TRANSACTION_APPROVED || msg.what == SWIP_TRANSACTION_APPROVED)

                {
                    try {
                        TransactionVO vo = (TransactionVO) msg.obj;
                        PaymentInitialization initialization = new PaymentInitialization(CashOutClass.this);
                        if (vo.getHostRequest().isPinVerifiedFlag()) {
                            initialization.initiateTransactionDetails(handler, vo, null);
                            transactionFlag = true;
                        } else {
                            initialization.initiateTransactionDetails(handler, vo, ISOUtil.hex2byte(sign));
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                if (msg.what == QPOS_DEVICE)

                {
                } else if (msg.what == TRANSACTION_FAILED || msg.what == CHIP_TRANSACTION_DECLINED || msg.what == SWIP_TRANSACTION_DECLINED)

                {
                    try {
                        progessDialog.hide_progress();
                        transactionFlag = false;
                        clear();
                        if (msg.what == 1032)
                            customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", ((TransactionVO) msg.obj).getStatus(), CashOutClass.this);
//                            Toast.makeText(CashOutClass.this, ((TransactionVO) msg.obj).getStatus(), Toast.LENGTH_SHORT).show();
                        else
                            customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", (String) msg.obj, CashOutClass.this);
//                            Toast.makeText(CashOutClass.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        if (accessBluetoothDetails() != null) {
                            JSONObject object = new JSONObject();
                            object.put("responseCode", msg.what);
                            object.put("responseValue", msg.obj);
                            new WalletAsyncMethod(WebConfig.CASHOUT_URL, updateCashOutDetails(object.toString()).toString(), headerData, CashOutClass.this, getString(R.string.responseTimeOut), "UPDATE_MPOS").execute();
                        } else
                            Toast.makeText(CashOutClass.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == ERROR_MESSAGE)

                {
                    try {
                        progessDialog.hide_progress();
                        transactionFlag = false;
                        clear();
                        customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", (String) msg.obj, CashOutClass.this);
//                        Toast.makeText(CashOutClass.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        if (accessBluetoothDetails() != null) {
                            JSONObject object = new JSONObject();
                            object.put("responseCode", msg.what);
                            object.put("responseValue", msg.obj);
                            new WalletAsyncMethod(WebConfig.CASHOUT_URL, updateCashOutDetails(object.toString()).toString(), headerData, CashOutClass.this, getString(R.string.responseTimeOut), "UPDATE_MPOS").execute();
                        } else
                            Toast.makeText(CashOutClass.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                } else if (msg.what == TRANSACTION_PENDING)

                {
                    try {
                        progessDialog.hide_progress();
                        transactionFlag = false;
                        clear();
                        customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", (String) msg.obj, CashOutClass.this);
//                        Toast.makeText(CashOutClass.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        if (accessBluetoothDetails() != null) {
                            JSONObject object = new JSONObject();
                            object.put("responseCode", msg.what);
                            object.put("responseValue", msg.obj);
                            new WalletAsyncMethod(WebConfig.CASHOUT_URL, updateCashOutDetails(object.toString()).toString(), headerData, CashOutClass.this, getString(R.string.responseTimeOut), "UPDATE_MPOS").execute();
                        } else
                            Toast.makeText(CashOutClass.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                } else if (msg.what == DISPLAY_STATUS)

                {
//                    customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", (String) msg.obj, CashOutClass.this);
                    Toast.makeText(CashOutClass.this,
                            (String) msg.obj, Toast.LENGTH_SHORT).show();
                } else if (msg.what == QPOS_EMV_MULITPLE_APPLICATION)

                {
                }
            }
        };
    }

    private void clear() {
        input_amount.setText("");
        input_mobile.setText("");
        findViewById(R.id.inflate_main).setVisibility(View.GONE);
        findViewById(R.id.getdetails).setVisibility(View.GONE);
        findViewById(R.id.getEmidetails).setVisibility(View.GONE);
        findViewById(R.id.btn_fund_mpos).setVisibility(View.VISIBLE);
    }

    AcquirerEmiDetailsVO acquirerEmiDetailsVO = null;

    private void initialize() {
        validator = new AccountValidator(getApplicationContext());
        heading = (TextView) findViewById(R.id.toolbar_title);
        if (balance != null)
            heading.setText(typeput + " (Balance : Rs." + balance + ")");
        else
            heading.setText(typeput);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_amount = (EditText) findViewById(R.id.input_amount);
//        if (!mobileNo.isEmpty()&& !amount.isEmpty()) {
//            input_mobile.setText(mobileNo);
//            input_mobile.setEnabled(false);
//            input_amount.setText(amount);
//            input_amount.setEnabled(false);
//        }
        btn_fund_mpos = (AppCompatButton) findViewById(R.id.btn_fund_mpos);
        receipt_details = (AppCompatButton) findViewById(R.id.reciept_details);
        receipt_details.setOnClickListener(this);
        btn_fund_mpos.setOnClickListener(this);
        select_bank = (Spinner) findViewById(R.id.select_bank);
        inflate_tenureee = (LinearLayout) findViewById(R.id.inflate_tenureee);
        lv_imflate = (ListView) findViewById(R.id.lv_imflate);
        final TextView input_text = (TextView) findViewById(R.id.input_texts);
        select_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                acquirerEmiDetailsVO = acquirerBanksList.get(position);
                findViewById(R.id.inflate_main).setVisibility(View.GONE);
                findViewById(R.id.getdetails).setVisibility(View.GONE);
                findViewById(R.id.getEmidetails).setVisibility(View.VISIBLE);
                inflate_tenureee.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lv_imflate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vo = arrayLists.get(position);
            }
        });
        input_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()!=0 && s.length()<10) {
                    input_text.setText(EnglishNumberToWords.convert(Integer.parseInt(s.toString())));
                    input_text.setVisibility(View.VISIBLE);
                }else
                    input_text.setVisibility(View.GONE);
            }
        });
    }

    private void inflate_tenure(final ArrayList<AcquirerEmiDetailsVO> arrayList, LinearLayout inflate_tenureee) {
        if (arrayList.size() != 0) {
            inflate_tenureee.setVisibility(View.VISIBLE);
            lv_imflate.setAdapter(new EMITenureAdapter(arrayList, this));
            inflate_tenureee.addView(lv_imflate);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fund_mpos:
                if (btnstatus == false) {
                    btnstatus = true;
                    hideKeyboard(CashOutClass.this);
                    type = null;
                    if (!ImageUtils.commonAmount(input_mobile.getText().toString())) {
                        input_mobile.setError("Please enter mobile number");
                        input_mobile.requestFocus();
                    } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                        input_amount.setError("Please enter amount");
                        input_amount.requestFocus();
                    } else {
                        if (accessBluetoothDetails() != null)
                            new WalletAsyncMethod(WebConfig.CASHOUT_URL, getCashOutDetails(input_mobile.getText().toString(), input_amount.getText().toString(), serviceType, requestChannel, reqFor, requestType, accessBluetoothDetails()).toString(), headerData, CashOutClass.this, getString(R.string.responseTimeOut), typeput).execute();
                        else
                            Toast.makeText(CashOutClass.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
                    }
                }
                handlercontrol();
                break;
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.getEmidetails:
                if (btnstatus == false) {
                    btnstatus = true;
                    if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                        input_amount.setError("Please enter amount");
                        input_amount.requestFocus();
                    } else if (acquirerEmiDetailsVO == null) {
                        select_bank.requestFocus();
                    } else {
                        progessDialog = new CustomProgessDialog(CashOutClass.this);
                        initialization.getSelectedBankEMITenureList(handler, input_amount.getText().toString(), acquirerEmiDetailsVO);
                        check = "EMIDETAILS";
                    }
                }
                handlercontrol();
                break;
            case R.id.getdetails:
                if (btnstatus == false) {
                    btnstatus = true;
                    if (vo != null) {
                        progessDialog = new CustomProgessDialog(CashOutClass.this);
                        initialization = new PaymentInitialization(getApplicationContext());
                        initialization.initiateTransaction(handler, DeviceType.ME30S, accessBluetoothDetails(),
                                input_amount.getText().toString() + ".00", PaymentTransactionConstants.EMI,
                                PaymentTransactionConstants.CREDIT, input_mobile.getText().toString(),
                                mylocation.getLatitude(), mylocation.getLongitude(),
                                orderID, null, vo, 1);
                        check = "lastvalidate";
                    }
                }
                handlercontrol();
                break;
        }
    }

    boolean transactionFlag = false;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    public JSONObject updateCashOutDetails(String hostResponse) {
        transactionFlag = false;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Update_Mpos_Txn");
            jsonObject.put("requestChannel", requestChannel);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("responseData", hostResponse);
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("orderID", orderID);
            jsonObject.put("requestType", requestType);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object, String hitfrom) {
        if (hitfrom.equalsIgnoreCase("UPDATE_MPOS"))
            generateReceipt(object);
    }

    private void generateReceipt(String object) {
        try {
            if (type != null) {
                ArrayList<String> left = new ArrayList<>();
                left.add("DATE:" + " " + type.getDate());
                left.add("MID:" + " " + mid);
                left.add("BATCH No:" + " " + type.getBatchNo());
                ArrayList<String> right = new ArrayList<>();
                right.add("TIME:" + " " + type.getDate());
                right.add("TID:" + " " + tid);
                right.add("INVOICE No:" + " " + type.getInvoiceNo());
                ArrayList<String> bottom = new ArrayList<>();
                bottom.add("CARD NUM : " + "XXXX-XXXX-XXXX-" + type.getCardNumber());
                bottom.add("CARD BRAND :" + type.getCardBrand() + "  " + "CARD TYPE :" + type.getCardType());
                bottom.add("RRN : " + type.getRRN() + "  " + "Transaction Id : " + type.getTransactionId());
                bottom.add("AID : " + type.getAID() + "  " + "TC : " + type.getTC());
                ArrayList<String> medium = new ArrayList<>();
                medium.add(typeput);
                medium.add(type.getCardBrand());
                transactionFlag = false;
                customReceiptCastSaleOut(list.get(0).getAgentName(), left, right, bottom, medium, String.valueOf(type.getAmount()), type.getCardHolderName(), CashOutClass.this);
            }
//            else
//                Toast.makeText(CashOutClass.this, new JSONObject(object).getJSONObject("responseData").getString("responseValue"), Toast.LENGTH_SHORT).show();
            clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chechStatus(JSONObject object, String hitfrom) {
        requestData = null;
        try {
            if (object.has("apiCommonResposne")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                String balance = object1.getString("runningBalance");
                heading.setText(typeput + " (Balance : Rs." + format(balance) + ")");
            }
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase(serviceType)) {
                    JSONArray array = object.getJSONArray("objMposData");
                    requestData = new JSONObject(object.getString("requestData"));
                    tid = requestData.getString("TERMINAL_ID");
                    mid = requestData.getString("MERCHANT_ID");
                    orderID = requestData.getString("ORDER_ID");
                    if (array.length() != 0)
                        customReceipt("Transaction Details", object, CashOutClass.this);
                }
                if (object.getString("serviceType").equalsIgnoreCase("Update_Mpos_Txn")) {
                    JSONArray array = object.getJSONArray("objHeaderData");
                    clear();
                    if (array.length() != 0)
                        generateReceipt(array);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int i = 0;

    private void generateReceipt(JSONArray array) {
        i = 0;
        try {
            ArrayList<String> left = new ArrayList<>();
            ArrayList<String> right = new ArrayList<>();
            ArrayList<String> bottom = new ArrayList<>();
            ArrayList<String> medium = new ArrayList<>();
            String custName = null, amount = null;
            while (i < array.length()) {
                JSONObject object = array.getJSONObject(i);
                switch (object.getString("headerData")) {
                    case "AID:":
                        bottom.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "Service Type":
                        medium.add(object.getString("headerValue"));
                        break;
                    case "Batch No.:":
                        left.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "MID:":
                        left.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "Txn Date:":
                        left.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "TID:":
                        right.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "Invoice No.:":
                        right.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "TC:":
                        bottom.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "Card Brand:":
                        bottom.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        medium.add(object.getString("headerValue"));
                        break;
                    case "Card Type.:":
                        bottom.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "CARD No.:":
                        bottom.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                    case "Txn.Amount:":
                        amount = object.getString("headerValue");
                        break;
                    case "Customer Name.:":
                        custName = object.getString("headerValue");
                        break;
                    case "Operator Reference ID:":
                        bottom.add(object.getString("headerData") + " " + object.getString("headerValue"));
                        break;
                }
                i++;
            }
            transactionFlag = false;
            if (left.size() != 0 && right.size() != 0 && bottom.size() != 0 && medium.size() != 0 && custName != null && amount != null)
                customReceiptCastSaleOut(list.get(0).getAgentName(), left, right, bottom, medium, amount, custName, CashOutClass.this);
        } catch (Exception e) {
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            bluetooth();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(CashOutClass.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(CashOutClass.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(CashOutClass.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    protected void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(CashOutClass.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(CashOutClass.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS_GPS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            getMyLocation();
                            break;
                        case Activity.RESULT_CANCELED:
                            finish();
                            break;
                    }
                    break;
                case CONTACT_PICKER_RESULT:
                    contactRead(data, input_mobile);
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        } else {
            if (dialog != null)
                dialog.dismiss();
        }
    }

    private void bluetooth() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Log.d("GoPosActivity", "bluetooth adapter is not null");
            if (!btAdapter.isEnabled()) {
                Log.d("GoPosActivity", "bluetooth is not enable");
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_BLUETOOTH);
            } else {
                Log.d("GoPosActivity", "bluetooth is enable");
                accessBluetoothDetails();
            }
        }
    }

    private String accessBluetoothDetails() {
        if (btAdapter.getBondedDevices() != null)
            if (btAdapter.getBondedDevices().size() > 0) {
                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                ArrayList<String> devices = new ArrayList<>();
                boolean isPosPaired = false;
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().startsWith("C-ME30S")) {
                        isPosPaired = true;
                        String bluetoothName = device.getName();
                        String bluetoothAddress = device.getAddress();
                        Log.d("GoPosActivity",
                                "bluetoothName: "
                                        + bluetoothName
                                        + " ,bluetoothAddress:"
                                        + bluetoothAddress);
                        if (!TextUtils.isEmpty(bluetoothAddress)) {
                            return bluetoothAddress;
                        }
                    } else {
                        isPosPaired = false;
                    }
                }
                if (!isPosPaired) {
                    return null;
                }
            } else {
                return null;
            }
        return null;
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("Transaction Details")) {
            try {
                JSONObject object = (JSONObject) ob;
                JSONArray array = object.getJSONArray("objMposData");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("tid"))
                        tid = jsonObject.getString("headerData");
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("mid"))
                        mid = jsonObject.getString("headerData");
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("orderId"))
                        orderID = jsonObject.getString("headerData");
                }
                if (!tid.isEmpty() && !mid.isEmpty()) {
                    progessDialog = new CustomProgessDialog(CashOutClass.this);
                    validator.accountActivation(handler, mid, tid);
                    check = "validate";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}

