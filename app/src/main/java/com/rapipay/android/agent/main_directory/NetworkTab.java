package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.NetworkHistoryFragment;
import com.rapipay.android.agent.fragments.NetworkTransFragment;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkTab extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    TabLayout tabLayout;
    NetworkTransFragment fragment_credit;
    NetworkHistoryFragment transFragment;
    TextView consent;
    String version = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_tablayout);
        setUpGClient();
        initialize();
    }

    private void initialize() {
        tabLayout = (TabLayout) findViewById(R.id.bottomNavigation);
        consent = (TextView) findViewById(R.id.consent);
        consent.setVisibility(View.VISIBLE);
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void setupTabLayout() {
        fragment_credit = new NetworkTransFragment();
        transFragment = new NetworkHistoryFragment();
        tabLayout.addTab(tabLayout.newTab().setText("Credit to Network"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Network Credit History"));
    }

    private void bindWidgetsWithAnEvent() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragment_credit);
                break;
            case 1:
                replaceFragment(transFragment);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.consent:
                consent.setClickable(false);
                new AsyncPostMethod(WebConfig.CRNF, getConsentDetails().toString(), headerData, NetworkTab.this, getString(R.string.responseTimeOut)).execute();
                break;
        }
    }

    public void clickable(){
        consent.setClickable(true);
    }


    @Override
    protected void onPause() {
        clickable();
        super.onPause();
    }

    public JSONObject getConsentDetails() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_REVERSAL_CONSENT_DETAILS");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("parentID", MainActivity.Parent_Mobile);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getConsentOTPDetails(JSONObject object) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "AUTO_REVERSAL_CONSENT_INITIATE");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("parentID", MainActivity.Parent_Mobile);
            jsonObject.put("clientIP", ImageUtils.ipAddress(NetworkTab.this));
            jsonObject.put("geoLogitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("geoLatitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("tbcFileVersion", object.getString("version"));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getVerifyOTPDetails(JSONObject object) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "AUTO_REVERSAL_CONSENT_VERIFY");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("parentID", MainActivity.Parent_Mobile);
            jsonObject.put("clientIP", ImageUtils.ipAddress(NetworkTab.this));
            jsonObject.put("geoLogitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("geoLatitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("tbcFileVersion", version);
            jsonObject.put("otp", otpView.getText().toString());
            jsonObject.put("otpRefId", object.getString("otpRefId"));
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
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_REVERSAL_CONSENT_DETAILS")) {
                    version = object.getString("version");
                    customDialog_Common("CONSENTLAYOUT", object, null, "Consent Details", object.getString("status"), object.getString("consentDetails"), NetworkTab.this);
                } else if (object.getString("serviceType").equalsIgnoreCase("AUTO_REVERSAL_CONSENT_INITIATE")) {
                    customDialog_Common("OTPLAYOUTS", object, null, "Enter OTP", null, null, NetworkTab.this);
                } else if (object.getString("serviceType").equalsIgnoreCase("AUTO_REVERSAL_CONSENT_VERIFY")) {
                    customDialog_Common("KYCLAYOUT", object, null, "Successfull", null, object.getString("responseMessage"), NetworkTab.this);
                }
            }
            consent.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("CONSENTLAYOUT")) {
            new AsyncPostMethod(WebConfig.CRNF, getConsentOTPDetails((JSONObject) ob).toString(), headerData, NetworkTab.this, getString(R.string.responseTimeOut)).execute();
        } else if (type.equalsIgnoreCase("OTPLAYOUTS")) {
            new AsyncPostMethod(WebConfig.CRNF, getVerifyOTPDetails((JSONObject) ob).toString(), headerData, NetworkTab.this, getString(R.string.responseTimeOut)).execute();
        }

    }

    @Override
    public void cancelClicked(String type, Object ob) {

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
                int permissionLocation = ContextCompat.checkSelfPermission(NetworkTab.this,
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
                                            .checkSelfPermission(NetworkTab.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                        Log.e("present location:",mylocation+"");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(NetworkTab.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(NetworkTab.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(NetworkTab.this,
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

}

