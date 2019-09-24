package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.rapipay.android.agent.Model.AEPSPendingPozo;
import com.rapipay.android.agent.Model.microaeps.Microdata1;
import com.rapipay.android.agent.Model.microaeps.Microresponse1;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.MATMAEPSAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.CustomProgessDialog;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.realm.Realm;

import static com.example.rfplmantra.MantraActivity.callmantraDeviceinfo;
import static com.example.rfplmantra.MantraActivity.display;
import static com.example.rfplmorphof.MorphoActivity.callmophoDeviceinfo;
import static com.example.rfplstartek.StratekActivity.callStartekDeviceinfo;

public class MICRO_AEPS_Activity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CustomInterface {
    String serviceType, requestChannel, requestType, reqFor, typeput, updateServiceType, innno, devicetype, aeps_type, bankName;
    EditText input_mobile, input_amount, input_userid;
    AutoCompleteTextView input_deviceid;
    private ImageView btn_contact;
    AppCompatButton btn_submit_aeps, btn_capture;
    String requestData, requestKey, headerDatas, clientRefID;
    LinearLayout pending_tran_layout, ln_amount, ln_morpho, ln_mantra, ln_startek;
    RecyclerView pendingtrans_details;
    ArrayList<AEPSPendingPozo> pendingPozoArrayList;
    //  Spinner select_deviceid, select_device;
    TextView pending, bank_select, adhar_select;
    List<Microdata1> microdata1;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 1000;
    //  ArrayList<String> dpdevicetype;
    int flagdevicetype;
    RadioGroup radioGroup;
    RadioButton cashid, balanceid;
    boolean radioflag = false;
    ImageView img_morpho_check, img_mantra_check, img_startek_check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aeps_bbps_layout);
        localStorage = LocalStorage.getInstance(this);
        deletebankTables();
       /* dpdevicetype = new ArrayList<>();
        dpdevicetype.add("Select Device Type");
        dpdevicetype.add("Morpho");
        dpdevicetype.add("Mantra");
        dpdevicetype.add("StarTek");*/
        //  select_deviceid = findViewById(R.id.select_deviceid);
        //  select_device = findViewById(R.id.select_device);
        pending = findViewById(R.id.pending);
        bank_select = findViewById(R.id.bank_select);
        adhar_select = findViewById(R.id.adhar_select);
        adhar_select.setClickable(true);
        getBankDetails();
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_bank = new ArrayList<>();
                ArrayList<Microdata1> list_bank1 = BaseCompactActivity.dbRealm.geBankList("");
                for (int i = 0; i < list_bank1.size(); i++) {
                    list_bank.add(list_bank1.get(i).getBankName());
                }
                customSpinner(bank_select, "Select Bank", list_bank);
            }
        });
        adhar_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_deviceid.setEnabled(true);
                input_deviceid.setText("");
                adhar_select.setVisibility(View.GONE);
            }
        });
        /*select_device.setAdapter(new MicroAepsAdapter(this, dpdevicetype));
        select_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    flagdevicetype = position;
                } else if (position == 2) {
                    flagdevicetype = position;
                } else if (position == 3) {
                    flagdevicetype = position;
                }
            }

            @Overridel
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        initialize();
        setUpGClient();
    }

    private void getBankDetails() {
        //  String url = "https://fingpayap.tapits.in/fingpay/getBankDetailsMasterData";
        final Dialog dialogs = new Dialog(this);
        final CustomProgessDialog dialog = new CustomProgessDialog(this);
        String url = "https://fingpayap.tapits.in/fpaepsservice/api/bankdata/bank/details";
        RequestQueue queue = Volley.newRequestQueue(this);
        final Microdata1 microdatas1 = new Microdata1();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            final Microresponse1 microresponse = new Microresponse1();
                            dialogs.dismiss();
                            dialog.hide_progress();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    try {
                                        Gson gson = new Gson();
                                        Microresponse1 clicks = gson.fromJson(response, Microresponse1.class);
                                        microdata1 = clicks.getData();
                                        for (int i = 0; i < microdata1.size(); i++) {
                                            microdatas1.setId(microdata1.get(i).getId());
                                            microdatas1.setBankName(microdata1.get(i).getBankName());
                                            microdatas1.setDetails(microdata1.get(i).getDetails());
                                            microdatas1.setIINNo(microdata1.get(i).getIINNo());
                                            microdatas1.setRemarks(microdata1.get(i).getRemarks());
                                            microdatas1.setTimestamp(microdata1.get(i).getTimestamp());
                                            realm.copyToRealm(microdatas1);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //post_des.setText("That didn't work!");
                Log.e("response url", error + "");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void assignBundleValue(int position) {
        if (position == 0) {
            radioflag = true;
            typeput = "Cash Withdrawal";
            serviceType = "AEPS_CASHOUT";
            requestChannel = "AEPS_CHANNEL";
            requestType = "AEPS-CASHOUT";
            devicetype = "other";
            ln_amount.setVisibility(View.VISIBLE);
        } else if (position == 1) {
            radioflag = true;
            typeput = "Balance Enquiry";
            serviceType = "AEPS_BALANCE_ENQ";
            requestChannel = "AEPS_CHANNEL";
            requestType = "AEPS-BE";
            devicetype = "others";
            ln_amount.setVisibility(View.GONE);
        }
    }

    TextView input_text;

    private void initialize() {
        reqFor = "AEPS";
        aeps_type = "aeps2";
        if (typeput == null) {
            typeput = "Cash Withdrawal";
            devicetype = "other";
        }
        input_amount = findViewById(R.id.input_amount);
        input_userid = findViewById(R.id.input_userid);
        ln_amount = findViewById(R.id.ln_amount);
        radioGroup = findViewById(R.id.myRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cashid) {
                    assignBundleValue(0);
                } else if (checkedId == R.id.balanceid) {
                    assignBundleValue(1);
                }
            }
        });
        cashid = findViewById(R.id.cashid);
        balanceid = findViewById(R.id.balanceid);
        btn_contact = findViewById(R.id.btn_contact);
        reset = findViewById(R.id.reset);
        reset.setVisibility(View.GONE);
        /*reset = findViewById(R.id.reset);
        reset.setOnClickListener(this);
        reset.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        reset.setVisibility(View.VISIBLE);*/
        input_deviceid = findViewById(R.id.input_deviceid);
        heading = findViewById(R.id.toolbar_title);
        if (balance != null) {
            heading.setText("AEPS" + " (Balance : Rs." + balance + ")");
        } else {
            heading.setText("AEPS");
        }
        input_mobile = findViewById(R.id.input_mobile);
        btn_submit_aeps = findViewById(R.id.btn_submit_aeps);
        btn_submit_aeps.setOnClickListener(this);
        if (aeps_type.equalsIgnoreCase("aeps2")) {
            btn_submit_aeps.setVisibility(View.GONE);
            //   select_device.setVisibility(View.VISIBLE);
        } else {
            btn_submit_aeps.setVisibility(View.VISIBLE);
            //   select_device.setVisibility(View.GONE);
        }
        flagdevicetype = localStorage.getActivityIntState("devicetype");
        Log.e("device flag==", flagdevicetype + "");
        btn_capture = findViewById(R.id.btn_capture);
        img_morpho_check = findViewById(R.id.img_morpho_check);
        img_mantra_check = findViewById(R.id.img_mantra_check);
        img_startek_check = findViewById(R.id.img_startek_check);
        btn_capture.setOnClickListener(this);
        pending_tran_layout = findViewById(R.id.pending_tran_layout);
        //   ImageView img_morpho_check,img_mantra_check,img_startek_check;
        ln_morpho = findViewById(R.id.ln_morpho);
        ln_morpho.setOnClickListener(this);
        ln_morpho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagdevicetype = 1;
                localStorage.setActivityState("devicetype", flagdevicetype);
                img_morpho_check.setVisibility(View.VISIBLE);
                img_mantra_check.setVisibility(View.GONE);
                img_startek_check.setVisibility(View.GONE);
            }
        });
        ln_mantra = findViewById(R.id.ln_mantra);
        ln_mantra.setOnClickListener(this);
        ln_mantra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagdevicetype = 2;
                localStorage.setActivityState("devicetype", flagdevicetype);
                img_morpho_check.setVisibility(View.GONE);
                img_mantra_check.setVisibility(View.VISIBLE);
                img_startek_check.setVisibility(View.GONE);
            }
        });
        ln_startek = findViewById(R.id.ln_startek);
        ln_startek.setOnClickListener(this);
        ln_startek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagdevicetype = 3;
                localStorage.setActivityState("devicetype", flagdevicetype);
                img_morpho_check.setVisibility(View.GONE);
                img_mantra_check.setVisibility(View.GONE);
                img_startek_check.setVisibility(View.VISIBLE);
            }
        });

        if (flagdevicetype == 1) {
            img_morpho_check.setVisibility(View.VISIBLE);
            img_mantra_check.setVisibility(View.GONE);
            img_startek_check.setVisibility(View.GONE);
        } else if (flagdevicetype == 2) {
            img_morpho_check.setVisibility(View.GONE);
            img_mantra_check.setVisibility(View.VISIBLE);
            img_startek_check.setVisibility(View.GONE);
        } else if (flagdevicetype == 3) {
            img_morpho_check.setVisibility(View.GONE);
            img_mantra_check.setVisibility(View.GONE);
            img_startek_check.setVisibility(View.VISIBLE);
        }
        pendingtrans_details = findViewById(R.id.pendingtrans_details);
        input_text = findViewById(R.id.input_texts);
        if (typeput.equalsIgnoreCase("Balance Enquiry")) {
            input_text.setVisibility(View.GONE);
        }
        if ((typeput.equalsIgnoreCase("Balance Enquiry") || typeput.equalsIgnoreCase("Cash Withdrawal")) && aeps_type.equalsIgnoreCase("aeps2")) {
            bank_select.setVisibility(View.VISIBLE);
            pending_tran_layout.setVisibility(View.GONE);
            pendingtrans_details.setVisibility(View.GONE);
            btn_capture.setVisibility(View.VISIBLE);
            pending.setVisibility(View.GONE);
            if (devicetype.equalsIgnoreCase("other")) {
                input_amount.setVisibility(View.VISIBLE);
            } else {
                input_amount.setText("0");
                input_amount.setVisibility(View.GONE);
            }
        }
        pendingtrans_details.addOnItemTouchListener(new RecyclerTouchListener(this, pendingtrans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                mLastClickTime = now;
                if (pendingPozoArrayList.size() != 0) {
                    // new AsyncPostMethod(WebConfig.CASHOUT_URL, getTransactionStatus(pendingPozoArrayList.get(position).getTransactionID()).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                    new AsyncPostMethod(WebConfig.CASHOUT_URL, getTransactionStatus(pendingPozoArrayList.get(position).getTransactionID()).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                }
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
                if (s.length() != 0 && s.length() < 10) {
                    input_text.setText("");
                    try {
                        if (devicetype.equalsIgnoreCase("other"))
                            input_text.setText(EnglishNumberToWords.convert(Integer.parseInt(s.toString())) + " rupee");
                        input_text.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    input_text.setVisibility(View.GONE);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MICRO_AEPS_Activity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    DocumentBuilder builder = null;
    String pidData;
    Document doc;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == Activity.RESULT_OK) {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (data != null) {
                            display = "";
                            display = data.getStringExtra("PID_DATA");
                            // customDialog_Common("Device Info", display , MainActivitys.this);
                            if (display != null) {
                                try {
                                    builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                                } catch (ParserConfigurationException e) {
                                    e.printStackTrace();
                                }
                                InputSource src = new InputSource();
                                src.setCharacterStream(new StringReader(display));
                                try {
                                    doc = builder.parse(src);
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                pidData = doc.getElementsByTagName("PidData").item(0).getTextContent();
                                if (pidData.length() < 50) {
                                    btn_submit_aeps.setVisibility(View.GONE);
                                    btn_capture.setText("capture");
                                    Toast.makeText(this, "Finger print data not capture / worng device type, Please check your device type", Toast.LENGTH_LONG).show();
                                } else {
                                    btn_submit_aeps.setVisibility(View.VISIBLE);
                                    btn_capture.setText("Re-capture");
                                    Toast.makeText(this, "Successfully capture data", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error while deserialze pid data", e);
                    }
                }
            }

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

    String deviceName = "";

    public void initDevice(int no) {
        if (no == 1) {
            callmophoDeviceinfo(MICRO_AEPS_Activity.this);
            deviceName = "Morpho";
        } else if (no == 2) {
            deviceName = "Mantra";
            callmantraDeviceinfo(MICRO_AEPS_Activity.this);
        } else if (no == 3) {
            deviceName = "StarTek";
            callStartekDeviceinfo(MICRO_AEPS_Activity.this);
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


    private void generateReceipt1(String object, String adhar) {
        try {
            input_deviceid.setEnabled(false);
            adhar_select.setVisibility(View.VISIBLE);
            JSONObject type = new JSONObject(object);
            if (aeps_type.equalsIgnoreCase("aeps2") && serviceType.equalsIgnoreCase("AEPS_BALANCE_ENQ")) {
                ArrayList<String> left = new ArrayList<>();
                left.add("Date:" + " " + getCurrentDate() + " " + type.getString("requestTransactionTime"));
                ArrayList<String> right = new ArrayList<>();
                //  right.add("Time(hh:mm:ss:" + " " + type.getString("requestTransactionTime"));
                ArrayList<String> bottom = new ArrayList<>();
                /*if (type.getString("bcName").equals("null") || type.getString("bcName").equals(""))
                    bottom.add("BC Name: " + list.get(0).getAgentName());
                else
                    bottom.add("BC Name: " + type.getString("bcName"));*/
                if (type.getString("agentId").equals("null") || type.getString("agentId").equals(""))
                    bottom.add("Agent Id :" + list.get(0).getMobilno());
                else
                    bottom.add("Agent Id :" + type.getString("agentId"));
                //  bottom.add("BC Location : " + type.getString("BankName"));
                bottom.add("Customer Adhar No : " + "XXXXXX" + adhar);
                //  bottom.add("Customer Name : " + type.getString("RRN"));
               /* if (type.getString("stan").equals("null") || type.getString("stan").equals(""))
                    bottom.add("STAN : " + "");
                else bottom.add("STAN : " + type.getString("stan"));*/
                bottom.add("RRN: : " + type.getString("bankRRN"));
                /*if (type.getString("uidaiAuthCode").equals("null") || type.getString("uidaiAuthCode").equals(""))
                    bottom.add("UIDAI Auth. Code: : " + "");
                else
                    bottom.add("UIDAI Auth. Code: : " + type.getString("uidaiAuthCode"));*/
                bottom.add("Transaction Status: : " + type.getString("transactionStatus"));
                bottom.add("A/C Balance: : " + type.getString("balanceAmount"));
                ArrayList<String> medium = new ArrayList<>();
                medium.add("Balance Enquiry");
                if (serviceType.equalsIgnoreCase("AEPS_BALANCE_ENQ"))
                    customReceiptCastSaleOut(list.get(0).getAgentName(), left, right, bottom, medium, serviceType, type.getString("bankRRN"), MICRO_AEPS_Activity.this, "other");
            } else if (aeps_type.equalsIgnoreCase("aeps2") && serviceType.equalsIgnoreCase("AEPS_CASHOUT")) {
                ArrayList<String> left = new ArrayList<>();
                left.add("Date:" + " " + getCurrentDate() + " " + type.getString("requestTransactionTime"));
                ArrayList<String> right = new ArrayList<>();
                //  right.add("Time(hh:mm:ss:" + " " + type.getString("requestTransactionTime"));
                ArrayList<String> bottom = new ArrayList<>();
                /*if (type.getString("bcName").equals("null") || type.getString("bcName").equals(""))
                    bottom.add("BC Name: " + list.get(0).getAgentName());
                else
                    bottom.add("BC Name: " + type.getString("bcName"));*/
                if (type.getString("agentId").equals("null") || type.getString("agentId").equals(""))
                    bottom.add("Agent Id :" + list.get(0).getMobilno());
                else
                    bottom.add("Agent Id :" + type.getString("agentId"));
                //  bottom.add("BC Location : " + type.getString("BankName"));
                bottom.add("Customer Adhar No : " + "XXXXXX" + adhar);
                //  bottom.add("Customer Name : " + type.getString("RRN"));
               /* if (type.getString("stan").equals("null") || type.getString("stan").equals(""))
                    bottom.add("STAN : " + "");
                else bottom.add("STAN : " + type.getString("stan"));*/
                bottom.add("RRN: : " + type.getString("bankRRN"));
                /*if (type.getString("uidaiAuthCode").equals("null") || type.getString("uidaiAuthCode").equals(""))
                    bottom.add("UIDAI Auth. Code: : " + "");
                else
                    bottom.add("UIDAI Auth. Code: : " + type.getString("uidaiAuthCode"));*/
                bottom.add("Transaction Status: : " + type.getString("transactionStatus"));
                bottom.add("A/C Balance : " + type.getString("balanceAmount"));
                bottom.add("Transaction Amount : " + type.getString("transactionAmount"));
                ArrayList<String> medium = new ArrayList<>();
                medium.add("Cash Withdrawal");
                customReceiptCastSaleOut(list.get(0).getAgentName(), left, right, bottom, medium, serviceType, type.getString("bankRRN"), MICRO_AEPS_Activity.this, "other");
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
        input_userid.setText("");
        input_amount.setText("");
        input_amount.setText("");
        input_amount.setEnabled(true);
        bank_select.setText("Select Bank");
        img_morpho_check.setVisibility(View.GONE);
        img_mantra_check.setVisibility(View.GONE);
        img_startek_check.setVisibility(View.GONE);
        cashid.setChecked(false);
        balanceid.setChecked(false);
        btn_capture.setText("Capture");
        btn_submit_aeps.setVisibility(View.GONE);
        radioflag = false;
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
                if (object.getString("serviceType").equalsIgnoreCase("INITIATE_AEPS_CASHOUT")) {
                    //  JSONArray array = object.getJSONArray("objMposData");
                    requestData = object.getString("requestData"); //encdata
                    requestKey = object.getString("requestKey");
                    headerDatas = object.getString("headerData"); //authdata
                    clientRefID = object.getString("clientRefID");
                    if (requestData != "" && pidData != "" && aeps_type.equals("aeps2") && serviceType.equalsIgnoreCase("AEPS_CASHOUT") && devicetype.equalsIgnoreCase("other")) {
                        new AsyncPostMethod(WebConfig.CASHOUT_URL_NEW, getCashOutDetails2(clientRefID, input_mobile.getText().toString(), requestData, headerDatas, input_amount.getText().toString(), serviceType, requestChannel, reqFor, requestType, input_deviceid.getText().toString(), innno, bankName).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                    } else if (requestData != "" && pidData != "" && aeps_type.equals("aeps2") && serviceType.equalsIgnoreCase("AEPS_BALANCE_ENQ") && devicetype.equalsIgnoreCase("others")) {
                        new AsyncPostMethod(WebConfig.CASHOUT_URL_NEW, getGetBalance(clientRefID, input_mobile.getText().toString(), requestData, headerDatas, input_deviceid.getText().toString(), serviceType, requestChannel, reqFor, requestType, input_deviceid.getText().toString(), innno, bankName).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("CASHOUT_PENDING_TXN_LIST")) {
                    if (object.has("cashoutPendingList")) {
                        insertPendingTransDetails(object.getJSONArray("cashoutPendingList"));
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("AEPS_TRANSACTION_STATUS") || object.getString("serviceType").equalsIgnoreCase("MATM_TRANSACTION_STATUS")) {
                    customReceiptNew("Transaction Status Receipt", object, MICRO_AEPS_Activity.this);
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("85001")) {
                customDialog_Common_device(object.getString("responseMessage"));
            } else if (object.getString("responseCode").equalsIgnoreCase("60217")) {
                customDialog_Common_device(object.getString("responseMessage"));
            } else if (object.getString("responseCode").equalsIgnoreCase("86039")) {
                customDialog_Common_device(object.getString("responseMessage"));
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(this, object.getString("responseMessage"), Toast.LENGTH_LONG).show();
                setBack_click1(this);
            } else if (reqFor.equalsIgnoreCase("MATM")) {
                responseMSg(object);
            } else if (object.getString("responseCode").equalsIgnoreCase("904")) {
                customDialog_Common_device(object.getString("responseMessage"));
            } else if (object.getString("responseCode").equalsIgnoreCase("60067")) {
                /*Toast.makeText(this, object.getString("responseMessage"), Toast.LENGTH_LONG).show();
                setBack_click1(this);*/
                customDialog_List_info(object.getString("respnseMessage"));
            } else {
                if (object.has("respnseMessage"))
                    customDialog_List_info(object.getString("respnseMessage"));
                else
                    customDialog_List_info(object.getString("responseMessage"));
            }
            try { //balanceAmount
                if (object.getBoolean("status") && aeps_type.equalsIgnoreCase("aeps2")) {
                    generateReceipt1(String.valueOf(object), convertString(input_deviceid.getText().toString()));
                } else {
                    customDialog_Common_device(object.getString("respnseMessage"));// respnseMessage
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try { //balanceAmount
                if (object.getString("status").equalsIgnoreCase("1000") && aeps_type.equalsIgnoreCase("aeps2")) {
                    generateReceipt1(String.valueOf(object), convertString(input_deviceid.getText().toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            btn_submit_aeps.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chechStat(String object) {

    }


    // aeps2 balance enquery
    /* "\"requestTransactionTime\":\"30\/08\/2019 11:03:16\", */
    public String convertString(String inputadhar) {
        String lastFourDigits = "";     //substring containing last 4 characters
        if (inputadhar.length() > 6) {
            lastFourDigits = inputadhar.substring(inputadhar.length() - 6);
        }
        Log.e("string conversion", lastFourDigits);
        return lastFourDigits;
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

    // comment for mpos,mantra,astrtek
    @Override
    public void okClicked(String type, Object ob) {

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
                btn_contact.setClickable(false);
                loadIMEI();
                break;
            case R.id.btn_capture:
                btn_submit_aeps.setClickable(false);
                if (!radioflag) {
                    btn_submit_aeps.setClickable(true);
                    Toast.makeText(MICRO_AEPS_Activity.this, "Please select transaction mode", Toast.LENGTH_SHORT).show();
                } else if (flagdevicetype < 1) {
                    btn_submit_aeps.setClickable(true);
                    Toast.makeText(this, "Please select device type", Toast.LENGTH_LONG).show();
                } else if (!ImageUtils.commonAmount(input_mobile.getText().toString())) {
                    input_mobile.setError("Please enter mobile number");
                    input_mobile.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else if (!ImageUtils.commonAmount(input_amount.getText().toString()) && devicetype.equalsIgnoreCase("other")) {
                    input_amount.setError("Please enter amount");
                    input_amount.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else if ((bank_select.getText().toString()).equalsIgnoreCase("Select Bank")) {
                    bank_select.setError("Please select bank");
                    bank_select.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else if ((input_deviceid.getText().toString()).isEmpty()) {
                    input_deviceid.setError("Please enter aadhar no.");
                    input_deviceid.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else if (input_userid.getText().toString().isEmpty()) {
                    input_userid.setError("Please enter name");
                    input_userid.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else {
                    initDevice(flagdevicetype);
                }
                try {
                    String condition = bank_select.getText().toString();
                    bankName = BaseCompactActivity.dbRealm.getBankNames(condition).get(0);
                    innno = BaseCompactActivity.dbRealm.getBankIINNo(condition).get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_submit_aeps:
                btn_submit_aeps.setClickable(false);
                hideKeyboard(MICRO_AEPS_Activity.this);
                if (!radioflag) {
                    btn_submit_aeps.setClickable(true);
                    Toast.makeText(MICRO_AEPS_Activity.this, "Please select transaction mode", Toast.LENGTH_SHORT).show();
                } else if (!ImageUtils.commonAmount(input_mobile.getText().toString())) {
                    input_mobile.setError("Please enter mobile number");
                    input_mobile.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else if (!ImageUtils.commonAmount(input_amount.getText().toString()) && devicetype.equalsIgnoreCase("other")) {
                    input_amount.setError("Please enter amount");
                    input_amount.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else if ((bank_select.getText().toString()).equalsIgnoreCase("Select Bank")) {
                    bank_select.setError("Please select bank");
                    bank_select.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else if (!ImageUtils.commonAmount(input_deviceid.getText().toString())) {
                    input_deviceid.setError("Please enter aadhar no.");
                    input_deviceid.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else if (input_userid.getText().toString().isEmpty()) {
                    input_userid.setError("Please enter name");
                    input_userid.requestFocus();
                    btn_submit_aeps.setClickable(true);
                } else {
                    if (reqFor.equalsIgnoreCase("AEPS") && !input_deviceid.getText().toString().isEmpty() && aeps_type.equals("aeps2")) {
                        Log.e("Device pid=", pidData);
                        btn_submit_aeps.setClickable(true);
                        // Log.e("Device pid=", display);
                        //   new AsyncPostMethod(WebConfig.CASHOUT_URL, getCashOutDetail1(deviceName, input_mobile.getText().toString(), input_amount.getText().toString(), serviceType, requestChannel, reqFor, requestType, input_deviceid.getText().toString()).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                        new AsyncPostMethod(WebConfig.AEPS2_INIT, getCashOutDetail1(deviceName, input_mobile.getText().toString(), input_amount.getText().toString(), input_deviceid.getText().toString(), input_userid.getText().toString(), serviceType, requestChannel, reqFor, requestType, input_deviceid.getText().toString(), innno, bankName).toString(), headerData, MICRO_AEPS_Activity.this, getString(R.string.responseTimeOut), typeput).execute();
                    } else {
                        btn_submit_aeps.setClickable(true);
                        // Toast.makeText(MICRO_AEPS_Activity.this, "Please pair your device through bluetooth", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MICRO_AEPS_Activity.this, "Please pair your device", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.back_click:
                findViewById(R.id.back_click).setClickable(false);
                setBack_click(this);
                finish();
                break;
        }
    }

    public void clickable() {
        findViewById(R.id.back_click).setClickable(true);
        reset.setClickable(true);
        btn_submit_aeps.setClickable(true);
        btn_contact.setClickable(true);
    }

    @Override
    protected void onPause() {
        clickable();
        super.onPause();
    }
}
