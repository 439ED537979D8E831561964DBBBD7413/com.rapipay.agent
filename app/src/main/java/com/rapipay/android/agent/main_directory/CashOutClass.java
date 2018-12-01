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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.pnsol.sdk.interfaces.DeviceCommunicationMode;
import com.pnsol.sdk.interfaces.DeviceType;
import com.pnsol.sdk.interfaces.PaymentTransactionConstants;
import com.pnsol.sdk.miura.emv.tlv.ISOUtil;
import com.pnsol.sdk.payment.PaymentInitialization;
import com.pnsol.sdk.vo.AcquirerEmiDetailsVO;
import com.pnsol.sdk.vo.HostResponse;
import com.pnsol.sdk.vo.TransactionVO;
import com.rapipay.android.agent.Model.MPOSModel;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.AcquirerBanksListAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

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

public class CashOutClass extends BaseCompactActivity implements View.OnClickListener, RequestHandler, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CustomInterface {
    Handler handler;
    EditText input_mobile, input_amount;
    AppCompatButton btn_fund, receipt_details;
    ArrayList<AcquirerEmiDetailsVO> arrayLists;
    AccountValidator validator;
    String typeput;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    String tid = "", mid = "", orderID = "";
    private BluetoothAdapter btAdapter;
    private static int REQUEST_BLUETOOTH = 101;
    HostResponse type = null;
    String sign;
    private PaymentInitialization initialization;
    private String check = "";
    ArrayList<AcquirerEmiDetailsVO> acquirerBanksList;
    private Spinner select_bank;
    private LinearLayout inflate_tenureee;
    private ArrayList<MPOSModel> mposModelArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashout_layout);
        typeput = getIntent().getStringExtra("typeput");
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
                if (typeput.equalsIgnoreCase("EMI")) {
                    if (msg.what == SUCCESS) {
                        if (check.equalsIgnoreCase("lastvalidate")) {
                            for (int i = 0; i < mposModelArrayList.size(); i++) {
                                if (mposModelArrayList.get(i).isIsflag())
                                    for (int j = 0; j < arrayLists.size(); j++) {
                                        if (mposModelArrayList.get(i).getAmount().equalsIgnoreCase(String.valueOf(arrayLists.get(j).getEmiAmount()))) {
                                            initialization = new PaymentInitialization(getApplicationContext());
                                            initialization.initiateTransaction(handler, DeviceType.ME30S, accessBluetoothDetails(),
                                                    input_amount.getText().toString() + ".00", PaymentTransactionConstants.EMI,
                                                    PaymentTransactionConstants.CREDIT, input_mobile.getText().toString(),
                                                    mylocation.getLatitude(), mylocation.getLongitude(),
                                                    orderID, null, arrayLists.get(j));
                                        }
                                    }
                            }
                        } else if (check.equalsIgnoreCase("validate")) {
                            bankList();
                        } else if (check.equalsIgnoreCase("list")) {
                            acquirerBanksList = new ArrayList<AcquirerEmiDetailsVO>();
                            acquirerBanksList = (ArrayList<AcquirerEmiDetailsVO>) msg.obj;
                            AcquirerBanksListAdapter adapter = new
                                    AcquirerBanksListAdapter(CashOutClass.this, acquirerBanksList);
                            select_bank.setVisibility(View.VISIBLE);
                            select_bank.setAdapter(adapter);
                            findViewById(R.id.btn_fund).setVisibility(View.GONE);
                            findViewById(R.id.getEmidetails).setVisibility(View.GONE);
                        } else if (check.equalsIgnoreCase("EMIDETAILS")) {
                            arrayLists = new ArrayList<AcquirerEmiDetailsVO>();
                            arrayLists = (ArrayList<AcquirerEmiDetailsVO>) msg.obj;
                            findViewById(R.id.inflate_main).setVisibility(View.VISIBLE);
                            findViewById(R.id.getdetails).setVisibility(View.VISIBLE);
                            findViewById(R.id.getEmidetails).setVisibility(View.GONE);
                            mposModelArrayList = new ArrayList<>();
                            for (int i = 0; i < arrayLists.size(); i++) {
                                mposModelArrayList.add(new MPOSModel(String.valueOf(arrayLists.get(i).getEmiAmount()), String.valueOf(arrayLists.get(i).getEmiPercentage()), String.valueOf(arrayLists.get(i).getEmiTenure()), false));
                            }
                            if (mposModelArrayList.size() != 0)
                                inflate_tenure(mposModelArrayList, inflate_tenureee);
                        }
                    }
                } else{
                    if (msg.what == SUCCESS) {
                        if (!transactionFlag) {
                            // From server data we get bluetooth mac addressm and merchantrefno which will be changes here
                            initialization = new PaymentInitialization(CashOutClass.this);
                            if (typeput.equalsIgnoreCase("CASHOUT"))
                                initialization.initiateTransaction(handler, DeviceType.ME30S, accessBluetoothDetails(), input_amount.getText().toString() + ".00", PaymentTransactionConstants.CASH_AT_POS, PaymentTransactionConstants.DEBIT,
                                        input_mobile.getText().toString(), mylocation.getLatitude(), mylocation.getLongitude(), orderID, null, DeviceCommunicationMode.BLUETOOTHCOMMUNICATION);
                            else if (typeput.equalsIgnoreCase("SALE"))
                                initialization.initiateTransaction(handler, DeviceType.ME30S, accessBluetoothDetails(), input_amount.getText().toString() + ".00", PaymentTransactionConstants.SALE, PaymentTransactionConstants.DEBIT,
                                        input_mobile.getText().toString(), mylocation.getLatitude(), mylocation.getLongitude(), orderID, null, DeviceCommunicationMode.BLUETOOTHCOMMUNICATION);
                            else if (typeput.equalsIgnoreCase("EMI"))
                                initialization.initiateTransaction(handler, DeviceType.ME30S, accessBluetoothDetails(), input_amount.getText().toString() + ".00", PaymentTransactionConstants.EMI, PaymentTransactionConstants.CREDIT,
                                        input_mobile.getText().toString(), mylocation.getLatitude(), mylocation.getLongitude(), orderID, null, acquirerEmiDetailsVO, DeviceCommunicationMode.BLUETOOTHCOMMUNICATION);
                        }
                    }
                    if (transactionFlag) {
                        type = (HostResponse) msg.obj;
                        try {
                            Gson gson = new Gson();
                            String json = gson.toJson(type);
                            new AsyncPostMethod(WebConfig.CASHOUT_URL, updateCashOutDetails(json).toString(), headerData, CashOutClass.this).execute();
                        } catch (Exception e) {

                        }


                    }
                }
                if (msg.what == FAIL)

                {
                    Toast.makeText(CashOutClass.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                }
                if (msg.what == SOCKET_NOT_CONNECTED)

                {
//                    alertMessage((String) msg.obj);
                }
//                if (msg.what == PaymentTransactionConstants.SUCCESS) {
//
//                }
                if (msg.what == CHIP_TRANSACTION_APPROVED)

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
                    //get data from vo and pass in api to server
                    //   new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getCashOutTransaction(type).toString(), headerData, CashOutClass.this).execute();
                }
                if (msg.what == QPOS_DEVICE)

                {
//                    alertMessage((String) msg.obj);
                } else if (msg.what == TRANSACTION_FAILED || msg.what == CHIP_TRANSACTION_DECLINED || msg.what == SWIP_TRANSACTION_DECLINED)

                {
                    TransactionVO vo = (TransactionVO) msg.obj;
//                    alertMessage("Transaction Status : " + vo.getStatus());
                } else if (msg.what == ERROR_MESSAGE)

                {
//                    alertMessage((String) msg.obj);
                } else if (msg.what == TRANSACTION_PENDING)

                {
                    ;
//                    alertMessage((String) msg.obj);
                } else if (msg.what == DISPLAY_STATUS)

                {
                    Toast.makeText(CashOutClass.this,
                            (String) msg.obj, Toast.LENGTH_SHORT).show();
                } else if (msg.what == QPOS_EMV_MULITPLE_APPLICATION)

                {
                    ArrayList<String> applicationList = (ArrayList<String>) msg.obj;
//                    emvList = (ListView) findViewById(R.id.application_list);
//                    emvList.setVisibility(View.VISIBLE);
//                    ArrayAdapter<String> adapter = new
//                            ArrayAdapter<String>(DeviceSerialNumber.this, android.R.layout.simple_list_item_1,
//                            applicationList);
//                    emvList.setAdapter(adapter);
//                    emvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {24
//                        Strictly Private &Confidential –
//                        Property of
//                        Payswiff Solutions
//                        Pvt.Ltd.All Rights
//                        Reserved
//                        @Override
//
//                        public void onItemClick(AdapterView<?> parent, View view,
//                                                int position, long id) {
//                            if (initialization != null) {
//                                initialization.getQposListener().executeSelectedEMVApplication(position);
//                                emvList.setVisibility(View.GONE);
//                            }
//                        }
//                    });
                }
            }

        }

        ;

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
        btn_fund = (AppCompatButton) findViewById(R.id.btn_fund);
        receipt_details = (AppCompatButton) findViewById(R.id.reciept_details);
        receipt_details.setOnClickListener(this);
        btn_fund.setOnClickListener(this);
        select_bank = (Spinner) findViewById(R.id.select_bank);
        inflate_tenureee = (LinearLayout) findViewById(R.id.inflate_tenureee);
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
    }

    private void inflate_tenure(final ArrayList<MPOSModel> arrayList, LinearLayout inflate_tenureee) {
        if (arrayList.size() != 0) {
            inflate_tenureee.setVisibility(View.VISIBLE);
            inflate_tenureee.removeAllViews();
            LayoutInflater inflater = getLayoutInflater();
            for (int i = 0; i < arrayList.size(); i++) {
                View view = inflater.inflate(R.layout.tenure_layout, null);
                TextView amount = (TextView) view.findViewById(R.id.amount);
                TextView percentage = (TextView) view.findViewById(R.id.percentage);
                TextView tenure = (TextView) view.findViewById(R.id.tenure);
                CheckBox check_list = (CheckBox) view.findViewById(R.id.check_list);
                check_list.setId(i);
                check_list.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CheckBox box = (CheckBox) buttonView;
                        LinearLayout linearLayout = (LinearLayout) box.getParent().getParent();
                        int count = linearLayout.getChildCount();
                        for (int i = 0; i < count; i++) {
                            LinearLayout layout = (LinearLayout) linearLayout.getChildAt(i);
                            CheckBox boxs = (CheckBox) layout.getChildAt(3);
                            if (box.getId() == boxs.getId()) {
                                if (!box.isChecked()) {
                                    box.setChecked(isChecked);
                                    arrayList.get(box.getId()).setIsflag(true);
                                }
                            } else if (box.getId() != boxs.getId()) {
                                if (boxs.isChecked()) {
                                    boxs.setChecked(false);
                                    arrayList.get(boxs.getId()).setIsflag(false);
                                }
                            }
                        }
                    }
                });
                amount.setText(String.valueOf(arrayList.get(i).getAmount()));
                percentage.setText(String.valueOf(arrayList.get(i).getPercent()));
                tenure.setText(String.valueOf(arrayList.get(i).getTenure()));
                inflate_tenureee.addView(view);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fund:
              /*  break;
            case R.id.reciept_details:
                default:*/
                hideKeyboard(CashOutClass.this);
                if (!ImageUtils.commonAmount(input_mobile.getText().toString())) {
                    input_mobile.setError("Please enter mobile number");
                    input_mobile.requestFocus();
                } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                    input_amount.setError("Please enter amount");
                    input_amount.requestFocus();
                } else
                    new AsyncPostMethod(WebConfig.CASHOUT_URL, getCashOutDetails(input_mobile.getText().toString(), input_amount.getText().toString()).toString(), headerData, CashOutClass.this).execute();
                break;
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.getEmidetails:
                if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                    input_amount.setError("Please enter amount");
                    input_amount.requestFocus();
                } else if (acquirerEmiDetailsVO == null) {
                    select_bank.requestFocus();
                } else {
                    initialization.getSelectedBankEMITenureList(handler, input_amount.getText().toString(), acquirerEmiDetailsVO);
                    check = "EMIDETAILS";
                }
                break;
            case R.id.getdetails:
                validator.accountActivation(handler, mid, tid);
                check = "lastvalidate";

                break;
        }
    }

    boolean transactionFlag = false;

    @Override
    protected void onResume() {
        super.onResume();

    }

    public JSONObject getCashOutDetails(String mobile, String txnAmmount) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Initiate_Mpos_Txn");
            jsonObject.put("requestType", "HANDSET_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "IMT" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("customerMobile", mobile);
            jsonObject.put("senderName", "khjkhg");
            jsonObject.put("txnAmount", txnAmmount);
            jsonObject.put("bluetoothAddress", accessBluetoothDetails());
            jsonObject.put("deviceType", "ME30S");
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("langitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("txnType", typeput);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject updateCashOutDetails(String hostResponse) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Update_Mpos_Txn");
            jsonObject.put("requestType", "HANDSET_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "IMT" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("responseData", hostResponse);
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("orderID", orderID);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.has("apiCommonResposne")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                String balance = object1.getString("runningBalance");
                heading.setText("CashOut (Balance : Rs." + format(balance) + ")");
            }
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("Initiate_Mpos_Txn")) {
                    //parse data and put values in below line
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
                        validator.accountActivation(handler, mid, tid);
                        check = "validate";
                    }
//                }
                }
                if (object.getString("serviceType").equalsIgnoreCase("Update_Mpos_Txn")) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            Double latitude = mylocation.getLatitude();
            Double longitude = mylocation.getLongitude();
            bluetooth();
//            latitudeTextView.setText("Latitude : "+latitude);
//            longitudeTextView.setText("Longitude : "+longitude);
            //Or Do whatever you want with your location
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
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
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(CashOutClass.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(CashOutClass.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(CashOutClass.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
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
        }
    }

    private void bluetooth() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // Phone does not support Bluetooth so let the user know and exit.
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
        if (btAdapter.getBondedDevices().size() > 0) {
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            ArrayList<String> devices = new ArrayList<>();
            boolean isPosPaired = false;
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().startsWith("C")) {
                    isPosPaired = true;
                    String bluetoothName = device.getName();
                    String bluetoothAddress = device.getAddress();
                    Log.d("GoPosActivity",
                            "bluetoothName: "
                                    + bluetoothName
                                    + " ,bluetoothAddress:"
                                    + bluetoothAddress);
                    if (!TextUtils.isEmpty(bluetoothAddress)) {
                        return bluetoothAddress;                        //display blutooth address and name in dialog
//                        showBluetoothDetails(bluetoothName, bluetoothAddress);
                    }
                } else {
                    isPosPaired = false;
                }
            }
            if (!isPosPaired) {
                return "";
//                mBinder.buttonProceedToVoid.setEnabled(true);
            }
        } else {
            return "";
//            mBinder.buttonProceedToVoid.setEnabled(true);
        }
        return "";
    }

    @Override
    public void okClicked(String type, Object ob) {

    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}

