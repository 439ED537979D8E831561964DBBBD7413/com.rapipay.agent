//package com.rapipay.android.agent.main_directory;
//
//import android.Manifest;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.AppCompatButton;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.finopaytech.finosdk.activity.MainTransactionActivity;
//import com.finopaytech.finosdk.encryption.AES_BC;
//import com.finopaytech.finosdk.helpers.Utils;
//import com.finopaytech.finosdk.models.ErrorSingletone;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResult;
//import com.google.android.gms.location.LocationSettingsStatusCodes;
//import com.rapipay.android.agent.R;
//import com.rapipay.android.agent.interfaces.CustomInterface;
//import com.rapipay.android.agent.interfaces.RequestHandler;
//import com.rapipay.android.agent.utils.AsyncPostMethod;
//import com.rapipay.android.agent.utils.BaseCompactActivity;
//import com.rapipay.android.agent.utils.Constants;
//import com.rapipay.android.agent.utils.GenerateChecksum;
//import com.rapipay.android.agent.utils.ImageUtils;
//import com.rapipay.android.agent.utils.WebConfig;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//public class MICRO_AEPS_Activity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener, CustomInterface {
//    String serviceType, requestChannel, requestType, reqFor, typeput,updateServiceType;
//    EditText input_mobile, input_amount;
//    private ImageView btn_contact;
//    AppCompatButton btn_submit_aeps;
//    String requestData ,requestKey,headerDatas;
//    private static final int MATM_AEPS_Resposne = 140;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.aeps_bbps_layout);
//        typeput = getIntent().getStringExtra("typeput");
//        serviceType = getIntent().getStringExtra("serviceType");
//        updateServiceType = getIntent().getStringExtra("updateServiceType");
//        requestChannel = getIntent().getStringExtra("requestChannel");
//        requestType = getIntent().getStringExtra("requestType");
//        reqFor = getIntent().getStringExtra("reqFor");
//        setUpGClient();
//        initialize();
//    }
//
//    private void initialize() {
//        heading = (TextView) findViewById(R.id.toolbar_title);
//        btn_contact = (ImageView) findViewById(R.id.btn_contact);
//        if (balance != null)
//            heading.setText(typeput + " (Balance : Rs." + balance + ")");
//        else
//            heading.setText(typeput);
//        input_mobile = (EditText) findViewById(R.id.input_mobile);
//        input_amount = (EditText) findViewById(R.id.input_amount);
//        btn_submit_aeps = (AppCompatButton) findViewById(R.id.btn_submit_aeps);
//        btn_submit_aeps.setOnClickListener(this);
//        if(typeput.equalsIgnoreCase("Balance Enquiry")) {
//            input_amount.setText("0");
//            input_amount.setEnabled(false);
//        }
//    }
//
//    private synchronized void setUpGClient() {
//        googleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, 0, this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        googleApiClient.connect();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mylocation = location;
//        if (mylocation != null) {
//            bluetooth();
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        checkPermissions();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//    }
//
//    private void getMyLocation() {
//        if (googleApiClient != null) {
//            if (googleApiClient.isConnected()) {
//                int permissionLocation = ContextCompat.checkSelfPermission(MICRO_AEPS_Activity.this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                    LocationRequest locationRequest = new LocationRequest();
//                    locationRequest.setInterval(3000);
//                    locationRequest.setFastestInterval(3000);
//                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                            .addLocationRequest(locationRequest);
//                    builder.setAlwaysShow(true);
//                    LocationServices.FusedLocationApi
//                            .requestLocationUpdates(googleApiClient, locationRequest, this);
//                    PendingResult<LocationSettingsResult> result =
//                            LocationServices.SettingsApi
//                                    .checkLocationSettings(googleApiClient, builder.build());
//                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//
//                        @Override
//                        public void onResult(LocationSettingsResult result) {
//                            final Status status = result.getStatus();
//                            switch (status.getStatusCode()) {
//                                case LocationSettingsStatusCodes.SUCCESS:
//                                    int permissionLocation = ContextCompat
//                                            .checkSelfPermission(MICRO_AEPS_Activity.this,
//                                                    Manifest.permission.ACCESS_FINE_LOCATION);
//                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//                                        mylocation = LocationServices.FusedLocationApi
//                                                .getLastLocation(googleApiClient);
//                                    }
//                                    break;
//                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                    try {
//                                        status.startResolutionForResult(MICRO_AEPS_Activity.this,
//                                                REQUEST_CHECK_SETTINGS_GPS);
//                                    } catch (IntentSender.SendIntentException e) {
//                                    }
//                                    break;
//                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                                    break;
//                            }
//                        }
//                    });
//                }
//            }
//        }
//    }
//
//    protected void checkPermissions() {
//        int permissionLocation = ContextCompat.checkSelfPermission(MICRO_AEPS_Activity.this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION);
//        List<String> listPermissionsNeeded = new ArrayList<>();
//        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
//            if (!listPermissionsNeeded.isEmpty()) {
//                ActivityCompat.requestPermissions(this,
//                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
//            }
//        } else {
//            getMyLocation();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        int permissionLocation = ContextCompat.checkSelfPermission(MICRO_AEPS_Activity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//            getMyLocation();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CHECK_SETTINGS_GPS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        getMyLocation();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        finish();
//                        break;
//                }
//                break;
//            case CONTACT_PICKER_RESULT:
//                contactRead(data, input_mobile);
//                break;
//            case MATM_AEPS_Resposne:
//                String response;
//                if(data!=null) {
//                    if (data.hasExtra("ClientResponse")) {
//                        response = data.getStringExtra("ClientResponse");
//                        String strDecryptResponse = AES_BC.getInstance().decryptDecode(Utils.replaceNewLine(response), requestKey);
//                        new AsyncPostMethod(WebConfig.CASHOUT_URL, updateDetails(response, transactionIDAEPS).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
////                    Utils.showOneBtnDialog(this, getString(com.finopaytech.finosdk.R.string.STR_INFO), strDecryptResponse, false);
//                    } else if (data.hasExtra("ErrorDtls")) {
//                        response = data.getStringExtra("ErrorDtls");
//                        String errorMsg = "", errorDtlsMsg = "";
//                        if (!response.equalsIgnoreCase("")) {
//                            try {
//                                String[] error_dtls = response.split("\\|");
//                                if (error_dtls.length > 0) {
//                                    errorMsg = error_dtls[0];
//                                    new AsyncPostMethod(WebConfig.CASHOUT_URL, updateDetails(errorMsg, transactionIDAEPS).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
////                                Utils.showOneBtnDialog(this, getString(com.finopaytech.finosdk.R.string.STR_INFO), "Error Message : " + errorMsg , false);
//                                }
//                            } catch (ArrayIndexOutOfBoundsException exp) {
//                            }
//                        }
//                    }
//                    ErrorSingletone.getFreshInstance();
//                }
//                break;
//        }
//    }
//    private JSONObject updateDetails(String responseData, String orderID) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("serviceType", updateServiceType);
//            jsonObject.put("requestChannel", requestChannel);
//            jsonObject.put("typeMobileWeb", "mobile");
//            jsonObject.put("transactionID", ImageUtils.miliSeconds());
//            jsonObject.put("agentMobile", list.get(0).getMobilno());
//            jsonObject.put("responseData", responseData);
//            jsonObject.put("orderID", orderID);
//            jsonObject.put("reqFor", reqFor);
//            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
//            jsonObject.put("requestType", requestType);
//            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }
//    private void bluetooth() {
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (btAdapter == null) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Not compatible")
//                    .setMessage("Your phone does not support Bluetooth")
//                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            System.exit(0);
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
//        } else {
//            Log.d("GoPosActivity", "bluetooth adapter is not null");
//            if (!btAdapter.isEnabled()) {
//                Log.d("GoPosActivity", "bluetooth is not enable");
//                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBT, REQUEST_BLUETOOTH);
//            } else {
//                Log.d("GoPosActivity", "bluetooth is enable");
//                accessBluetoothDetails();
//            }
//        }
//    }
//
//    private String accessBluetoothDetails() {
//        if (btAdapter.getBondedDevices() != null)
//            if (btAdapter.getBondedDevices().size() > 0) {
//                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
//                ArrayList<String> devices = new ArrayList<>();
//                boolean isPosPaired = false;
//                for (BluetoothDevice device : pairedDevices) {
//                    if (device.getName().startsWith("C-ME30S")) {
//                        isPosPaired = true;
//                        String bluetoothName = device.getName();
//                        String bluetoothAddress = device.getAddress();
//                        Log.d("GoPosActivity",
//                                "bluetoothName: "
//                                        + bluetoothName
//                                        + " ,bluetoothAddress:"
//                                        + bluetoothAddress);
//                        if (!TextUtils.isEmpty(bluetoothAddress)) {
//                            return bluetoothAddress;
//                        }
//                    } else {
//                        isPosPaired = false;
//                    }
//                }
//                if (!isPosPaired) {
//                    return "";
//                }
//            } else {
//                return "";
//            }
//        return "";
//    }
//
//    @Override
//    public void chechStat(String object) {
//
//    }
//
//    @Override
//    public void chechStatus(JSONObject object) {
//        try {
//            if (object.has("apiCommonResposne")) {
//                JSONObject object1 = object.getJSONObject("apiCommonResposne");
//                String balance = object1.getString("runningBalance");
//                heading.setText(typeput + " (Balance : Rs." + format(balance) + ")");
//            }
//            if (object.getString("responseCode").equalsIgnoreCase("200")) {
//                if (object.getString("serviceType").equalsIgnoreCase(serviceType)) {
//                    JSONArray array = object.getJSONArray("objMposData");
//                    requestData = object.getString("requestData");
//                    requestKey = object.getString("requestKey");
//                    headerDatas = object.getString("headerData");
//                    if (array.length() != 0)
//                        customReceipt("Transaction Details", object, MICRO_AEPS_Activity.this);
//                }
//                if (object.getString("serviceType").equalsIgnoreCase(updateServiceType)) {
////                    JSONArray array = object.getJSONArray("objHeaderData");
////                    clear();
////                    if (array.length() != 0)
////                        generateReceipt(array);
//
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void okClicked(String type, Object ob) {
//        if (type.equalsIgnoreCase("Transaction Details")) {
//            try {
//                Intent intent = new Intent(this, MainTransactionActivity.class);
//                intent.putExtra("RequestData", requestData);
//                intent.putExtra("HeaderData", headerDatas);
//                intent.putExtra("ReturnTime", 5);// Application return time in second
//                startActivityForResult(intent, MATM_AEPS_Resposne);
////                resetData(cbMicroATM.getId());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    @Override
//    public void cancelClicked(String type, Object ob) {
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        setBack_click(this);
//        finish();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_contact:
//                loadIMEI();
//                break;
//            case R.id.btn_submit_aeps:
//                hideKeyboard(MICRO_AEPS_Activity.this);
//                if (!ImageUtils.commonAmount(input_mobile.getText().toString())) {
//                    input_mobile.setError("Please enter mobile number");
//                    input_mobile.requestFocus();
//                } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
//                    input_amount.setError("Please enter amount");
//                    input_amount.requestFocus();
//                } else {
//                    if (accessBluetoothDetails() != null && !accessBluetoothDetails().equalsIgnoreCase(""))
//                        new AsyncPostMethod(WebConfig.CASHOUT_URL, getCashOutDetails(input_mobile.getText().toString(), input_amount.getText().toString(), serviceType, requestChannel, reqFor, requestType, accessBluetoothDetails()).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
//                    else
//                        Toast.makeText(MICRO_AEPS_Activity.this, "Please pair device through bluetooth", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.back_click:
//                setBack_click(this);
//                finish();
//                break;
//        }
//    }
//}
