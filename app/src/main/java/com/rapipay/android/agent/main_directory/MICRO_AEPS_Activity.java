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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finopaytech.finosdk.activity.MainTransactionActivity;
import com.finopaytech.finosdk.encryption.AES_BC;
import com.finopaytech.finosdk.helpers.Utils;
import com.finopaytech.finosdk.models.ErrorSingletone;
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
import com.rapipay.android.agent.Model.AEPSPendingPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.MATMAEPSAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MICRO_AEPS_Activity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CustomInterface {
    String serviceType, requestChannel, requestType, reqFor, typeput, updateServiceType;
    EditText input_mobile, input_amount;
    AutoCompleteTextView input_deviceid;
    private ImageView btn_contact;
    AppCompatButton btn_submit_aeps;
    String requestData, requestKey, headerDatas, clientRefID;
    private static final int MATM_AEPS_Resposne = 140;
    String strDecryptResponse = "";
    ArrayList<String> deviceID;
    LinearLayout pending_tran_layout;
    RecyclerView pendingtrans_details;
    ArrayList<AEPSPendingPozo> pendingPozoArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aeps_bbps_layout);
        typeput = getIntent().getStringExtra("typeput");
        serviceType = getIntent().getStringExtra("serviceType");
        updateServiceType = getIntent().getStringExtra("updateServiceType");
        requestChannel = getIntent().getStringExtra("requestChannel");
        requestType = getIntent().getStringExtra("requestType");
        reqFor = getIntent().getStringExtra("reqFor");
        setUpGClient();
        initialize();
        if (!typeput.equalsIgnoreCase("Balance Enquiry"))
            loadUrl();
        else
            reset.setVisibility(View.GONE);
    }

    private void loadUrl() {
        new AsyncPostMethod(WebConfig.CASHOUT_URL, getPendingTransaction().toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        btn_contact = (ImageView) findViewById(R.id.btn_contact);
        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        reset.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        reset.setVisibility(View.VISIBLE);
        input_deviceid = (AutoCompleteTextView) findViewById(R.id.input_deviceid);
        if (balance != null)
            heading.setText(typeput + " (Balance : Rs." + balance + ")");
        else
            heading.setText(typeput);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_amount = (EditText) findViewById(R.id.input_amount);
        btn_submit_aeps = (AppCompatButton) findViewById(R.id.btn_submit_aeps);
        btn_submit_aeps.setOnClickListener(this);
        pending_tran_layout = (LinearLayout) findViewById(R.id.pending_tran_layout);
        pendingtrans_details = (RecyclerView) findViewById(R.id.pendingtrans_details);
        final TextView input_text = (TextView) findViewById(R.id.input_texts);
        if (typeput.equalsIgnoreCase("Balance Enquiry")) {
            input_amount.setText("0");
            input_amount.setEnabled(false);
        }
        if (reqFor.equalsIgnoreCase("AEPS")) {
            deviceID = new ArrayList<>();
//            if (MainActivity.deviceDetailsPozoArrayList != null && MainActivity.deviceDetailsPozoArrayList.size() != 0) {
//                deviceID.add("Enter Device ID");
            for (int i = 0; i < MainActivity.deviceDetailsPozoArrayList.size(); i++) {
                if (MainActivity.deviceDetailsPozoArrayList.get(i).getDeviceType().equalsIgnoreCase("AEPS")) {
                    deviceID.add(MainActivity.deviceDetailsPozoArrayList.get(i).getBluetoothID());
                }
            }
            if (deviceID.size() >= 1) {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MICRO_AEPS_Activity.this,
                        android.R.layout.simple_list_item_1, deviceID);
                input_deviceid.setAdapter(dataAdapter);
                input_deviceid.setThreshold(1);
            }
        } else
            input_deviceid.setVisibility(View.GONE);

        pendingtrans_details.addOnItemTouchListener(new RecyclerTouchListener(this, pendingtrans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (btnstatus == false) {
                    btnstatus = true;
                    if (pendingPozoArrayList.size() != 0) {
                        new AsyncPostMethod(WebConfig.CASHOUT_URL, getTransactionStatus(pendingPozoArrayList.get(position).getTransactionID()).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                    }
                }handlercontrol();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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

    public JSONObject getPendingTransaction() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "CASHOUT_PENDING_TXN_LIST");
            jsonObject.put("requestChannel", requestChannel);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getTransactionStatus(String orgTxnRefID) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (reqFor.equalsIgnoreCase("AEPS")) {
                jsonObject.put("serviceType", "AEPS_TRANSACTION_STATUS");
                jsonObject.put("requestType", "AEPS_STATUS");
            } else if (reqFor.equalsIgnoreCase("MATM")) {
                jsonObject.put("serviceType", "MATM_TRANSACTION_STATUS");
                jsonObject.put("requestType", "MATM_STATUS");
            }
            jsonObject.put("orgTxnRefID", orgTxnRefID);
            jsonObject.put("requestChannel", requestChannel);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
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
            if (!reqFor.equalsIgnoreCase("AEPS"))
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
                int permissionLocation = ContextCompat.checkSelfPermission(MICRO_AEPS_Activity.this,
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
                                            .checkSelfPermission(MICRO_AEPS_Activity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(MICRO_AEPS_Activity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(MICRO_AEPS_Activity.this,
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
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MICRO_AEPS_Activity.this,
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
                            if (btnstatus == false) {
                                btnstatus = true;
                                getMyLocation();
                            }
                            handlercontrol();
                            break;
                        case Activity.RESULT_CANCELED:
                            if (btnstatus == false) {
                                btnstatus = true;
                                finish();
                            }
                            handlercontrol();
                            break;
                    }
                    break;
                case CONTACT_PICKER_RESULT:
                    if (btnstatus == false) {
                        btnstatus = true;
                        contactRead(data, input_mobile);
                    }
                    handlercontrol();
                    break;
                case MATM_AEPS_Resposne:
                    if (btnstatus == false) {
                        btnstatus = true;
                        String response;
                        if (data != null) {
                            if (data.hasExtra("ClientResponse")) {
                                response = data.getStringExtra("ClientResponse");
                                strDecryptResponse = AES_BC.getInstance().decryptDecode(Utils.replaceNewLine(response), requestKey);
                                new AsyncPostMethod(WebConfig.CASHOUT_URL, updateDetails(updateDetailsResponseData(response, "0").toString(), transactionIDAEPS).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), "AEPS-MATM").execute();
//                    Utils.showOneBtnDialog(this, getString(com.finopaytech.finosdk.R.string.STR_INFO), strDecryptResponse, false);
                            } else if (data.hasExtra("ErrorDtls")) {
                                response = data.getStringExtra("ErrorDtls");
                                String errorMsg = "", errorDtlsMsg = "";
                                if (!response.equalsIgnoreCase("")) {
                                    try {
                                        String[] error_dtls = response.split("\\|");
                                        if (error_dtls.length > 0) {
                                            strDecryptResponse = error_dtls[0];
                                            new AsyncPostMethod(WebConfig.CASHOUT_URL, updateDetails(updateDetailsResponseData(strDecryptResponse, "1").toString(), transactionIDAEPS).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), "AEPS-MATM").execute();
//                                Utils.showOneBtnDialog(this, getString(com.finopaytech.finosdk.R.string.STR_INFO), "Error Message : " + errorMsg , false);
                                        }
                                    } catch (ArrayIndexOutOfBoundsException exp) {
                                    }
                                }
                            }
                            ErrorSingletone.getFreshInstance();
                        }
                    }
                    handlercontrol();
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

    private JSONObject updateDetailsResponseData(String errorMsg, String ResponseCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (ResponseCode.equalsIgnoreCase("1")) {
                jsonObject.put("ClientResponse", Utils.replaceNewLine(AES_BC.getInstance().encryptEncode(clientResponseData(errorMsg).toString(), requestKey)));
                jsonObject.put("DisplayMessage", errorMsg);
            } else if (ResponseCode.equalsIgnoreCase("0")) {
                jsonObject.put("ClientResponse", errorMsg);
                jsonObject.put("DisplayMessage", "Success");
            }
            jsonObject.put("ResponseCode", ResponseCode);
            jsonObject.put("ClientRefID", clientRefID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject clientResponseData(String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (reqFor.equalsIgnoreCase("MATM")) {
                jsonObject.put("TxnStatus", status);
                jsonObject.put("TxnAmt", "");
                jsonObject.put("CardNumber", "");
                jsonObject.put("TransactionDatetime", "");
                jsonObject.put("TerminalID", "");
            } else if (reqFor.equalsIgnoreCase("AEPS")) {
                jsonObject.put("Status", status);
                jsonObject.put("Amount", "");
                jsonObject.put("AdhaarNo", "");
                jsonObject.put("TxnTime", "");
                jsonObject.put("TxnDate", "");
                jsonObject.put("BankName", "");
                jsonObject.put("CustomerMobile", "");
                jsonObject.put("LedgerBalance", "");
            }
            jsonObject.put("AvailableBalance", "");
            jsonObject.put("RRN", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject updateDetails(String responseData, String orderID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", updateServiceType);
            jsonObject.put("requestChannel", requestChannel);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("responseData", responseData);
            jsonObject.put("orderID", orderID);
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("requestType", requestType);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
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
                    if (device.getName().startsWith("D180")) {
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
    public void chechStat(String object) {
        if (object.equalsIgnoreCase("AEPS-MATM")) {
            if (strDecryptResponse.contains("Amount") || strDecryptResponse.contains("TxnAmt") || strDecryptResponse.contains("AccountNo"))
                generateReceipt(strDecryptResponse);
            else {
                Utils.showOneBtnDialog(this, getString(com.finopaytech.finosdk.R.string.STR_INFO), strDecryptResponse, false);
                clear();
            }
        }
    }

    private void generateReceipt(String object) {
        try {
            JSONObject type = new JSONObject(object);
            if (serviceType.equalsIgnoreCase("AEPS_CASHOUT") || serviceType.equalsIgnoreCase("AEPS_BALANCE_ENQ")) {
                ArrayList<String> left = new ArrayList<>();
                left.add("TxnDate:" + " " + type.getString("TxnDate"));
                ArrayList<String> right = new ArrayList<>();
                right.add("TxnTime:" + " " + type.getString("TxnTime"));
                ArrayList<String> bottom = new ArrayList<>();
                bottom.add("Amount : " + type.getString("Amount"));
                bottom.add("AdhaarNo :" + type.getString("AdhaarNo"));
                bottom.add("BankName : " + type.getString("BankName"));
                bottom.add("CustomerMobile : " + input_mobile.getText().toString());
                bottom.add("RRN : " + type.getString("RRN"));
                bottom.add("AvailableBalance : " + type.getString("AvailableBalance"));
                bottom.add("Status : " + type.getString("Status"));
                ArrayList<String> medium = new ArrayList<>();
                medium.add(serviceType);
                if (serviceType.equalsIgnoreCase("AEPS_BALANCE_ENQ"))
                    customReceiptCastSaleOut(list.get(0).getAgentName(), left, right, bottom, medium, serviceType, type.getString("CustomerMobile"), MICRO_AEPS_Activity.this);
                else
                    customReceiptCastSaleOut(list.get(0).getAgentName(), left, right, bottom, medium, String.valueOf(type.getString("Amount")), type.getString("CustomerMobile"), MICRO_AEPS_Activity.this);
            } else if (serviceType.equalsIgnoreCase("MATM_CASHOUT")) {
                ArrayList<String> left = new ArrayList<>();
                left.add("TransactionDatetime:" + " " + type.getString("TransactionDatetime"));
                ArrayList<String> right = new ArrayList<>();
//                right.add("TxnTime:" + " " + type.getString("TxnTime"));
                ArrayList<String> bottom = new ArrayList<>();
                bottom.add("Amount : " + type.getString("TxnAmt"));
                bottom.add("TerminalID :" + type.getString("TerminalID"));
                bottom.add("CardNumber : " + type.getString("CardNumber"));
                bottom.add("RRN : " + type.getString("RRN"));
                bottom.add("AvailableBalance : " + type.getString("AvailableBalance"));
                bottom.add("Status : " + type.getString("TxnStatus"));
                ArrayList<String> medium = new ArrayList<>();
                medium.add(serviceType);
                customReceiptCastSaleOut(list.get(0).getAgentName(), left, right, bottom, medium, String.valueOf(type.getString("TxnAmt")), type.getString("CardNumber"), MICRO_AEPS_Activity.this);
            } else if (serviceType.equalsIgnoreCase("MATM_BALANCE_ENQ")) {
                ArrayList<String> left = new ArrayList<>();
                left.add("TransactionDatetime:" + " " + type.getString("TransactionDatetime"));
                ArrayList<String> right = new ArrayList<>();
//                right.add("TxnTime:" + " " + type.getString("TxnTime"));
                ArrayList<String> bottom = new ArrayList<>();
                bottom.add("AccountNo : " + type.getString("AccountNo"));
                bottom.add("TerminalID :" + type.getString("TerminalID"));
                bottom.add("CardNumber : " + type.getString("CardNumber"));
                bottom.add("RRN : " + type.getString("RRN"));
                bottom.add("AvailableBalance : " + type.getString("AvailableBalance"));
                bottom.add("BalanceEnquiryStatus : " + type.getString("BalanceEnquiryStatus"));
                ArrayList<String> medium = new ArrayList<>();
                medium.add(serviceType);
                customReceiptCastSaleOut(list.get(0).getAgentName(), left, right, bottom, medium, serviceType, type.getString("CardNumber"), MICRO_AEPS_Activity.this);
            }
            clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        if (typeput.equalsIgnoreCase("Balance Enquiry")) {
            input_amount.setText("0");
            input_amount.setEnabled(false);
        } else {
            input_amount.setText("");
        }
        input_mobile.setText("");
        input_deviceid.setText("");
        requestData = "";
        requestKey = "";
        headerDatas = "";
        clientRefID = "";
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.has("apiCommonResposne")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                String balance = object1.getString("runningBalance");
                heading.setText(typeput + " (Balance : Rs." + format(balance) + ")");
            }
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase(serviceType)) {
                    JSONArray array = object.getJSONArray("objMposData");
                    requestData = object.getString("requestData");
                    requestKey = object.getString("requestKey");
                    headerDatas = object.getString("headerData");
                    clientRefID = object.getString("clientRefID");
                    if (array.length() != 0)
                        customReceipt("Transaction Details", object, MICRO_AEPS_Activity.this);
                } else if (object.getString("serviceType").equalsIgnoreCase(updateServiceType)) {
                    generateReceipt(strDecryptResponse);
                } else if (object.getString("serviceType").equalsIgnoreCase("CASHOUT_PENDING_TXN_LIST")) {
                    if (object.has("cashoutPendingList")) {
                        insertPendingTransDetails(object.getJSONArray("cashoutPendingList"));
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("AEPS_TRANSACTION_STATUS") || object.getString("serviceType").equalsIgnoreCase("MATM_TRANSACTION_STATUS")) {
                    customReceiptNew("Transaction Status Receipt", object, MICRO_AEPS_Activity.this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertPendingTransDetails(JSONArray array) {
        pendingPozoArrayList = new ArrayList<AEPSPendingPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                pendingPozoArrayList.add(new AEPSPendingPozo(object.getString("transactionID"), object.getString("amount"), object.getString("serviceType"), object.getString("customerName"), object.getString("customerMobile"), object.getString("txnDateTime")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pendingPozoArrayList.size() != 0) {
            pending_tran_layout.setVisibility(View.VISIBLE);
            initializePendingAdapter(pendingPozoArrayList);
        }
    }

    private void initializePendingAdapter(ArrayList<AEPSPendingPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        pendingtrans_details.setLayoutManager(layoutManager);
        pendingtrans_details.setAdapter(new MATMAEPSAdapter(this, list));
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("Transaction Details")) {
            try {
                Intent intent = new Intent(this, MainTransactionActivity.class);
                intent.putExtra("RequestData", requestData);
                intent.putExtra("HeaderData", headerDatas);
                intent.putExtra("ReturnTime", 5);// Application return time in second
                startActivityForResult(intent, MATM_AEPS_Resposne);
//                resetData(cbMicroATM.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_contact:
                if (btnstatus == false) {
                    btnstatus = true;
                    loadIMEI();
                }
                handlercontrol();
                break;
            case R.id.btn_submit_aeps:
                if (btnstatus == false) {
                    btnstatus = true;
                    hideKeyboard(MICRO_AEPS_Activity.this);
                    if (!ImageUtils.commonAmount(input_mobile.getText().toString())) {
                        input_mobile.setError("Please enter mobile number");
                        input_mobile.requestFocus();
                    } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                        input_amount.setError("Please enter amount");
                        input_amount.requestFocus();
                    } else if (reqFor.equalsIgnoreCase("MATM") && !input_amount.getText().toString().isEmpty() && (Integer.valueOf(input_amount.getText().toString()) % 100 != 0)) {
                        input_amount.setError("Please enter amount in multiple of hundred");
                        input_amount.requestFocus();
                    } else {
                        if (reqFor.equalsIgnoreCase("MATM") && accessBluetoothDetails() != null && !accessBluetoothDetails().equalsIgnoreCase(""))
                            new AsyncPostMethod(WebConfig.CASHOUT_URL, getCashOutDetails(input_mobile.getText().toString(), input_amount.getText().toString(), serviceType, requestChannel, reqFor, requestType, accessBluetoothDetails()).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                        else if (reqFor.equalsIgnoreCase("AEPS") && !input_deviceid.getText().toString().isEmpty())
                            new AsyncPostMethod(WebConfig.CASHOUT_URL, getCashOutDetails(input_mobile.getText().toString(), input_amount.getText().toString(), serviceType, requestChannel, reqFor, requestType, input_deviceid.getText().toString()).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                        else
                            Toast.makeText(MICRO_AEPS_Activity.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
                    }
                }
                handlercontrol();
                break;
            case R.id.back_click:
                if (btnstatus == false) {
                    btnstatus = true;
                    setBack_click(this);
                    finish();
                }
                handlercontrol();
                break;
            case R.id.reset:
                if (btnstatus == false) {
                    btnstatus = true;
                    if (!typeput.equalsIgnoreCase("Balance Enquiry"))
                        loadUrl();
                }
                handlercontrol();
                break;
        }
    }
}
