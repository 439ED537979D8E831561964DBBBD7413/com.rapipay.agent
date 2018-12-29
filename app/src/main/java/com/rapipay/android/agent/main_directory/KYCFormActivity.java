package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
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
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.NewKYCPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.CustomProgessDialog;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class KYCFormActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static String formData = null;
    String type, button, mobileNo, persons, scandata = null, documentType = null, documentID = null, customerType;
    ImageView back_click,delete_all;
    TextView input_name, input_number, input_address, input_email, input_comp, select_state, gsin_no, pan_no, city_name, pin_code, document_id;
    ImageView documentfrontimage, documentbackimage, panphoto, selfphoto, shopPhoto, signphoto, passportphotoimage;
    AppCompatButton documentback, documentfront;
    private static final int DOCUMENT_FRONT1 = 1888;
    private static final int DOCUMENT_FRONT2 = 1889;
    private static final int DOCUMENT_BACK1 = 1890;
    private static final int DOCUMENT_BACK2 = 1891;
    private static final int PANPIC1 = 1892;
    private static final int PANPIC2 = 1893;
    private static final int SHOPPIC1 = 1894;
    private static final int SHOPPIC2 = 1895;
    private static final int CUSTPANPIC1 = 1896;
    private static final int CUSTPANPIC2 = 1897;
    private static final int selfphoto1 = 1898;
    private static final int selfphoto2 = 1899;
    private static final int signphoto1 = 1990;
    private static final int signphoto2 = 1991;
    private static final int passportphoto1 = 1992;
    private static final int passportphoto2 = 1993;
    private JSONObject kycMapData = new JSONObject();
    private JSONObject kycMapImage = new JSONObject();
    private CustomProgessDialog customProgessDialog;
    private String clicked = "";
    private Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x5;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_personal_detail);
        setUpGClient();
        initialize();
        loadCamera();
    }

    private void initialize() {
        TYPE = "NOTHING";
        customerType = getIntent().getStringExtra("customerType");
        type = getIntent().getStringExtra("type");
        mobileNo = getIntent().getStringExtra("mobileNo");
        documentType = getIntent().getStringExtra("documentType");
        persons = getIntent().getStringExtra("persons");
        documentID = getIntent().getStringExtra("documentID");
        button = getIntent().getStringExtra("button");
        input_number = (TextView) findViewById(R.id.mobile_no);
        document_id = (TextView) findViewById(R.id.document_id);
        document_id.setText(documentID);
        document_id.setEnabled(false);
        input_number.setText(mobileNo);
        input_number.setEnabled(false);
        input_email = (TextView) findViewById(R.id.email_id);
        input_comp = (TextView) findViewById(R.id.company_name);
        input_name = (TextView) findViewById(R.id.name);
        input_address = (TextView) findViewById(R.id.address_name);
        city_name = (TextView) findViewById(R.id.city_name);
        select_state = (TextView) findViewById(R.id.select_state);
        select_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_state = db.getState_Details();
                customSpinner(select_state, "Select State", list_state);
            }
        });
        pan_no = (TextView) findViewById(R.id.pan_no);
        gsin_no = (TextView) findViewById(R.id.gsin_no);
        date1_text = (AutofitTextView) findViewById(R.id.date);
        pin_code = (TextView) findViewById(R.id.pin_code);
        documentfrontimage = (ImageView) findViewById(R.id.documentfrontimage);
        documentbackimage = (ImageView) findViewById(R.id.documentbackimage);
        panphoto = (ImageView) findViewById(R.id.panphoto);
        selfphoto = (ImageView) findViewById(R.id.selfphoto);
        shopPhoto = (ImageView) findViewById(R.id.shopphoto);
        signphoto = (ImageView) findViewById(R.id.signphoto);
        passportphotoimage = (ImageView) findViewById(R.id.passportphotoimage);
        if (type.equalsIgnoreCase("SCAN")) {
            if (button.equalsIgnoreCase("personal")) {
                scandata = getIntent().getStringExtra("scandata");
                findViewById(R.id.personal_layout).setVisibility(View.VISIBLE);
                parsePersonalJson(scandata);
            }
            if (button.equalsIgnoreCase("address")) {
                scandata = getIntent().getStringExtra("scandata");
                findViewById(R.id.address_details).setVisibility(View.VISIBLE);
                parseAddressJson(scandata);
            }
        } else if (type.equalsIgnoreCase("MANUAL")) {
            if (button.equalsIgnoreCase("personal")) {
                findViewById(R.id.personal_layout).setVisibility(View.VISIBLE);
            }
            if (button.equalsIgnoreCase("address")) {
                findViewById(R.id.address_details).setVisibility(View.VISIBLE);
            }
        }
        if (button.equalsIgnoreCase("buisness"))
            findViewById(R.id.business_detail).setVisibility(View.VISIBLE);
        if (button.equalsIgnoreCase("verification"))
            findViewById(R.id.verification_layout).setVisibility(View.VISIBLE);

        if (persons.equalsIgnoreCase("internal")) {
            findViewById(R.id.sign_photo).setVisibility(View.GONE);
            findViewById(R.id.gsin_no).setVisibility(View.GONE);
            findViewById(R.id.shop_photo).setVisibility(View.GONE);
        }
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("KYC FORM");
        back_click = (ImageView) findViewById(R.id.back_click);
        back_click.setImageDrawable(getResources().getDrawable(R.drawable.back_icon));
        documentfront = (AppCompatButton) findViewById(R.id.documentfront);
        documentfront.setText("Upload " + documentType + " front");
        documentback = (AppCompatButton) findViewById(R.id.documentback);
        documentback.setText("Upload " + documentType + " back");
        date1_text.setOnClickListener(toDateClicked);
        delete_all = (ImageView)findViewById(R.id.delete_all);
        fillPersonal();
        fillAddress();
        fillBusiness();
        fillVerify();
    }

    private void fillPersonal() {
        if (button.equalsIgnoreCase("personal")) {
            getPersonalDetails();
        }
    }

    private void getPersonalDetails() {
        try {
            if (getIntent().getStringExtra("localPersonal").equalsIgnoreCase("true") || clicked.equalsIgnoreCase("business")) {
                String condition = "where " + RapipayDB.MOBILENO + "='" + mobileNo + "'" + " AND " + RapipayDB.DOCUMENTTYPE + "='" + documentType + "'" + " AND " + RapipayDB.DOCUMENTID + "='" + documentID + "'";
                ArrayList<NewKYCPozo> newKYCList_Personal = db.getKYCDetails_Personal(condition);
                if (newKYCList_Personal.size() != 0) {
                    input_name.setText(newKYCList_Personal.get(0).getUSER_NAME());
                    input_email.setText(newKYCList_Personal.get(0).getEMAILID());
                    input_comp.setText(newKYCList_Personal.get(0).getCOMPANY_NAME());
                    date1_text.setText(newKYCList_Personal.get(0).getDOB());
                    if (newKYCList_Personal.get(0).getPASSPORT_PHOTO() != null) {
                        byteConvert((ImageView) findViewById(R.id.passportphotoimage), newKYCList_Personal.get(0).getPASSPORT_PHOTO());
                        findViewById(R.id.passportphoto).setVisibility(View.GONE);
                        if (!kycMapImage.has("passportPhoto"))
                            kycMapImage.put("passportPhoto", getBase64(getBitmap((ImageView) findViewById(R.id.passportphotoimage))));
                    }
                    input_name.setEnabled(false);
                    input_email.setEnabled(false);
                    input_comp.setEnabled(false);
                    date1_text.setEnabled(false);
                    delete_all.setVisibility(View.VISIBLE);
                    mapPersonalData(newKYCList_Personal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillAddress() {
        if (button.equalsIgnoreCase("address")) {
            getAddressDetails();
        }
    }

    private void getAddressDetails() {
        try {
            if (getIntent().getStringExtra("localAddress").equalsIgnoreCase("true") || clicked.equalsIgnoreCase("business")) {
                String condition = "where " + RapipayDB.MOBILENO + "='" + mobileNo + "'" + " AND " + RapipayDB.DOCUMENTTYPE + "='" + documentType + "'" + " AND " + RapipayDB.DOCUMENTID + "='" + documentID + "'";
                ArrayList<NewKYCPozo> newKYCList_Address = db.getKYCDetails_Address(condition);
                if (newKYCList_Address.size() != 0) {
                    input_address.setText(newKYCList_Address.get(0).getADDRESS());
                    city_name.setText(newKYCList_Address.get(0).getCITY());
                    select_state.setText(newKYCList_Address.get(0).getSTATE());
                    pin_code.setText(newKYCList_Address.get(0).getPINCODE());
                    if (newKYCList_Address.get(0).getDOCUMENTFRONT_PHOTO() != null) {
                        byteConvert((ImageView) findViewById(R.id.documentfrontimage), newKYCList_Address.get(0).getDOCUMENTFRONT_PHOTO());
                        findViewById(R.id.documentfront).setVisibility(View.GONE);
                        if (!kycMapImage.has("uploadFront"))
                            kycMapImage.put("uploadFront", getBase64(getBitmap((ImageView) findViewById(R.id.documentfrontimage))));
                    }
                    if (newKYCList_Address.get(0).getDOCUMENTBACK_PHOTO() != null) {
                        byteConvert((ImageView) findViewById(R.id.documentbackimage), newKYCList_Address.get(0).getDOCUMENTBACK_PHOTO());
                        findViewById(R.id.documentback).setVisibility(View.GONE);
                        if (!kycMapImage.has("uploadBack"))
                            kycMapImage.put("uploadBack", getBase64(getBitmap((ImageView) findViewById(R.id.documentbackimage))));
                    }
                    input_address.setEnabled(false);
                    city_name.setEnabled(false);
                    select_state.setEnabled(false);
                    pin_code.setEnabled(false);
                    delete_all.setVisibility(View.VISIBLE);
                    mapAddressData(newKYCList_Address);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mapAddressData(ArrayList<NewKYCPozo> newKYCPozoArrayList) {
        try {
            if (!kycMapData.has("address"))
                kycMapData.put("address", newKYCPozoArrayList.get(0).getADDRESS());
            if (!kycMapData.has("city"))
                kycMapData.put("city", newKYCPozoArrayList.get(0).getCITY());
            if (!kycMapData.has("state"))
                kycMapData.put("state", newKYCPozoArrayList.get(0).getSTATE());
            if (!kycMapData.has("pinCode"))
                kycMapData.put("pinCode", newKYCPozoArrayList.get(0).getPINCODE());
            if (!kycMapData.has("documentId"))
                kycMapData.put("documentId", newKYCPozoArrayList.get(0).getDOCUMENTID());
            if (!kycMapData.has("documentType"))
                kycMapData.put("documentType", newKYCPozoArrayList.get(0).getDOCUMENTTYPE());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillBusiness() {
        if (button.equalsIgnoreCase("buisness")) {
            getBusinessDetails();
        }
    }

    private void fillVerify() {
        if (button.equalsIgnoreCase("verification")) {
            getVerifyDetails();
        }
    }

    private void getVerifyDetails() {
        try {
            if (getIntent().getStringExtra("localVerify").equalsIgnoreCase("true")) {
                String condition = "where " + RapipayDB.MOBILENO + "='" + mobileNo + "'" + " AND " + RapipayDB.DOCUMENTTYPE + "='" + documentType + "'" + " AND " + RapipayDB.DOCUMENTID + "='" + documentID + "'";
                ArrayList<NewKYCPozo> newKYCList_Verify = db.getKYCDetails_VERIFY(condition);
                if (newKYCList_Verify.size() != 0) {
                    if (newKYCList_Verify.get(0).getSELF_PHOTO() != null) {
                        byteConvert((ImageView) findViewById(R.id.selfphoto), newKYCList_Verify.get(0).getSELF_PHOTO());
                        findViewById(R.id.self_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("selfPhoto"))
                            kycMapImage.put("selfPhoto", getBase64(getBitmap((ImageView) findViewById(R.id.selfphoto))));
                    }
                    if (newKYCList_Verify.get(0).getSIGN_PHOTO() != null) {
                        byteConvert((ImageView) findViewById(R.id.signphoto), newKYCList_Verify.get(0).getSIGN_PHOTO());
                        findViewById(R.id.sign_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("signPhoto"))
                            kycMapImage.put("signPhoto", getBase64(getBitmap((ImageView) findViewById(R.id.signphoto))));
                    }
                    delete_all.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBusinessDetails() {
        try {
            if (getIntent().getStringExtra("localBusiness").equalsIgnoreCase("true") || clicked.equalsIgnoreCase("business")) {
                String condition = "where " + RapipayDB.MOBILENO + "='" + mobileNo + "'" + " AND " + RapipayDB.DOCUMENTTYPE + "='" + documentType + "'" + " AND " + RapipayDB.DOCUMENTID + "='" + documentID + "'";
                ArrayList<NewKYCPozo> newKYCList_Business = db.getKYCDetails_BUISNESS(condition);
                if (newKYCList_Business.size() != 0) {
                    pan_no.setText(newKYCList_Business.get(0).getPANNUMBER());
                    gsin_no.setText(newKYCList_Business.get(0).getGSTINNUMBER());
                    if (newKYCList_Business.get(0).getPAN_PHOTO() != null) {
                        byteConvert((ImageView) findViewById(R.id.panphoto), newKYCList_Business.get(0).getPAN_PHOTO());
                        findViewById(R.id.pan_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("pancardImg"))
                            kycMapImage.put("pancardImg", getBase64(getBitmap((ImageView) findViewById(R.id.panphoto))));
                    }
                    if (newKYCList_Business.get(0).getSHOP_PHOTO() != null) {
                        byteConvert((ImageView) findViewById(R.id.shopphoto), newKYCList_Business.get(0).getSHOP_PHOTO());
                        findViewById(R.id.shop_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("shopPhoto"))
                            kycMapImage.put("shopPhoto", getBase64(getBitmap((ImageView) findViewById(R.id.shopphoto))));
                    }
                    pan_no.setEnabled(false);
                    gsin_no.setEnabled(false);
                    delete_all.setVisibility(View.VISIBLE);
                    mapBusinessData(newKYCList_Business);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mapBusinessData(ArrayList<NewKYCPozo> newKYCPozoArrayList) {
        try {
            if (!kycMapData.has("panNo"))
                kycMapData.put("panNo", newKYCPozoArrayList.get(0).getPANNUMBER());
            if (!kycMapData.has("gstIN"))
                kycMapData.put("gstIN", newKYCPozoArrayList.get(0).getGSTINNUMBER());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearVerify(){
        db.deleteRow(mobileNo,"");
        finish();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.delete_all:
                    db.deleteRow(mobileNo,"verify");
                    clearVerify();
                    break;
                case R.id.back_click:
                    finish();
                    break;
                case R.id.documentfront:
                    selectImage(DOCUMENT_FRONT1, DOCUMENT_FRONT2);
                    break;
                case R.id.passportphoto:
                    selectImage(passportphoto1, passportphoto2);
                    break;
                case R.id.documentback:
                    selectImage(DOCUMENT_BACK1, DOCUMENT_BACK2);
                    break;
                case R.id.pan_photo:
                    selectImage(PANPIC1, PANPIC2);
                    break;
                case R.id.cust_pan_photo:
                    selectImage(CUSTPANPIC1, CUSTPANPIC2);
                    break;
                case R.id.shop_photo:
                    selectImage(SHOPPIC1, SHOPPIC2);
                    break;
                case R.id.self_photo:
                    selectImage(selfphoto1, selfphoto2);
                    break;
                case R.id.sign_photo:
                    selectImage(signphoto1, signphoto2);
                    break;
                case R.id.sub_btn_present:
                    if (personalValidation()) {
                        kycMapData.put("mobileNo", input_number.getText().toString());
                        kycMapData.put("dob", date1_text.getText().toString());
                        kycMapData.put("email", input_email.getText().toString());
                        kycMapData.put("companyName", input_comp.getText().toString());
                        String[] token = input_name.getText().toString().split(" ");
                        kycMapData.put("firstName", token[0]);
                        int count = token.length;
                        if (count > 2) {
                            kycMapData.put("middleName", token[1]);
                            StringBuilder builder = new StringBuilder();
                            for (int i = 2; i < count; i++) {
                                builder.append(token[i] + " ");
                            }
                            kycMapData.put("lastName", builder.toString());
                        } else if (count >= 2)
                            kycMapData.put("lastName", token[1]);
                        findViewById(R.id.personal_layout).setVisibility(View.GONE);
                        findViewById(R.id.address_details).setVisibility(View.VISIBLE);
                        if (getIntent().hasExtra("localAddress"))
                            getAddressDetails();
                        parseAddressJson(scandata);
                        if (getIntent().getStringExtra("localPersonal").equalsIgnoreCase("false"))
                            insertPersonal(kycMapData, kycMapImage, input_name.getText().toString(), documentID, documentType, customerType);
                        clicked = "personal";
                    }
                    break;
                case R.id.sub_btn_address:
                    if (addressValidation()) {
                        kycMapData.put("address", input_address.getText().toString());
                        kycMapData.put("city", city_name.getText().toString());
                        kycMapData.put("state", select_state.getText().toString());
                        kycMapData.put("pinCode", pin_code.getText().toString());
                        kycMapData.put("documentId", document_id.getText().toString());
                        kycMapData.put("documentType", documentType);
                        findViewById(R.id.personal_layout).setVisibility(View.GONE);
                        findViewById(R.id.address_details).setVisibility(View.GONE);
                        findViewById(R.id.business_detail).setVisibility(View.VISIBLE);
                        if (getIntent().hasExtra("localAddress")) {
                            if (getIntent().getStringExtra("localAddress").equalsIgnoreCase("false"))
                                insertAddress(kycMapData, kycMapImage, documentID, documentType, customerType);
                        } else if (clicked.equalsIgnoreCase("personal")) {
                            insertAddress(kycMapData, kycMapImage, documentID, documentType, customerType);
                            clicked = "address";
                        }
                        if (getIntent().hasExtra("localBusiness"))
                            getBusinessDetails();
                    }
                    break;
                case R.id.sub_btn_buisness:
                    if (buisnessValidation()) {
                        kycMapData.put("panNo", pan_no.getText().toString());
                        kycMapData.put("gstIN", gsin_no.getText().toString());
                        findViewById(R.id.personal_layout).setVisibility(View.GONE);
                        findViewById(R.id.address_details).setVisibility(View.GONE);
                        findViewById(R.id.business_detail).setVisibility(View.GONE);
                        findViewById(R.id.verification_layout).setVisibility(View.VISIBLE);
                        if (getIntent().hasExtra("localBusiness")) {
                            if (getIntent().getStringExtra("localBusiness").equalsIgnoreCase("false"))
                                insertBuisness(kycMapData, kycMapImage, documentID, documentType, customerType);
                        } else if (clicked.equalsIgnoreCase("address")) {
                            insertBuisness(kycMapData, kycMapImage, documentID, documentType, customerType);
                            clicked = "business";
                        }
                        if (getIntent().hasExtra("localVerify"))
                            getVerifyDetails();
                    }
                    break;
                case R.id.sub_btn_verify:
                    if (getIntent().hasExtra("localPersonal"))
                        getPersonalDetails();
                    if (getIntent().hasExtra("localAddress"))
                        getAddressDetails();
                    if (getIntent().hasExtra("localBusiness"))
                        getBusinessDetails();
                    if (personalValidation())
                        if (addressValidation())
                            if (buisnessValidation())
                                if (oursValidation()) {
                                    if (getIntent().hasExtra("localVerify")) {
                                        if (getIntent().getStringExtra("localVerify").equalsIgnoreCase("false"))
                                            insertVerify(kycMapData, kycMapImage, documentID, documentType, customerType);
                                    } else if (clicked.equalsIgnoreCase("business")) {
                                        insertVerify(kycMapData, kycMapImage, documentID, documentType, customerType);
                                        clicked = "verify";
                                    }
                                    reDirectWebView();
                                }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mapPersonalData(ArrayList<NewKYCPozo> newKYCPozoArrayList) {
        try {
            if (!kycMapData.has("mobileNo"))
                kycMapData.put("mobileNo", newKYCPozoArrayList.get(0).getMOBILENO());
            if (!kycMapData.has("dob"))
                kycMapData.put("dob", newKYCPozoArrayList.get(0).getDOB());
            if (!kycMapData.has("email"))
                kycMapData.put("email", newKYCPozoArrayList.get(0).getEMAILID());
            if (!kycMapData.has("companyName"))
                kycMapData.put("companyName", newKYCPozoArrayList.get(0).getCOMPANY_NAME());
            String[] token = newKYCPozoArrayList.get(0).getUSER_NAME().split(" ");
            if (!kycMapData.has("firstName"))
                kycMapData.put("firstName", token[0]);
            int count = token.length;
            if (count > 2) {
                if (!kycMapData.has("middleName"))
                    kycMapData.put("middleName", token[1]);
                StringBuilder builder = new StringBuilder();
                for (int i = 2; i < count; i++) {
                    builder.append(token[i] + " ");
                }
                if (!kycMapData.has("lastName"))
                    kycMapData.put("lastName", builder.toString());
            } else if (count >= 2) {
                if (!kycMapData.has("lastName"))
                    kycMapData.put("lastName", token[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reDirectWebView() {
        formData = getsession_ValidateKyc(customerType);
        Intent intent = new Intent(KYCFormActivity.this, WebViewVerify.class);
        intent.putExtra("persons", persons);
        intent.putExtra("mobileNo", mobileNo);
        intent.putExtra("documentType", documentType);
        intent.putExtra("documentID", documentID);
        startActivity(intent);
    }

    public String getsession_ValidateKyc(String kycType) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        String form = null;
        try {
            jsonObject.put("serviceType", "KYC_PROCESS");
            jsonObject.put("reKYC", "");
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("txnRef", "VKP" + tsLong.toString());
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("kycType", kycType);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("langitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("listdata", kycMapData.toString());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            form = "<html>\n" +
                    "\t<body>\n" +
                    "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYC_FORWARD_POST + "\">\n" +
                    "\t\t\t<input name=\"requestedData\" value=\"" + getDataBase64(jsonObject.toString()) + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"documentsListData\" value=\"" + getDataBase64(kycMapImage.toString()) + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input type=\"submit\"/>\n" +
                    "\t\t</form>\n" +
                    "\t\t<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                    "\t\t\t\t\tdocument.getElementById(\"validatekyc\").submit();\n" +
                    "\t\t</script>\n" +
                    "\t</body>\n" +
                    "</html>";
            return form;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDataBase64(String data) {
        try {
            return Base64.encodeToString(data.getBytes("utf-8"), Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }

    private Boolean oursValidation() {
        if (persons.equalsIgnoreCase("outside")) {
            if (selfphoto.getDrawable() == null) {
                TextView self_photo = (TextView) findViewById(R.id.self_photo);
                self_photo.setError("Please upload valid live image");
                self_photo.requestFocus();
                return false;
            } else if (signphoto.getDrawable() == null) {
                TextView sign_photo = (TextView) findViewById(R.id.sign_photo);
                sign_photo.setError("Please upload valid agent sign image");
                sign_photo.requestFocus();
                return false;
            } else
                return true;
        } else if (persons.equalsIgnoreCase("internal")) {
            if (selfphoto.getDrawable() == null) {
                TextView self_photo = (TextView) findViewById(R.id.self_photo);
                self_photo.setError("Please upload valid live image");
                self_photo.requestFocus();
                return false;
            } else
                return true;
        } else
            return false;
    }

    private Boolean personalValidation() {
        if (!ImageUtils.commonNumber(input_number.getText().toString(), 10)) {
            input_number.setError("Please enter valid mobile number");
            input_number.requestFocus();
            return false;
        } else if (!ImageUtils.commonRegex(input_name.getText().toString(), 150, ". ")) {
            input_name.setError("Please enter valid name");
            input_name.requestFocus();
            return false;
        } else if (date1_text.getText().toString().isEmpty()) {
            date1_text.setError("Please enter valid date");
            date1_text.requestFocus();
            return false;
        } else if (passportphotoimage.getDrawable() == null) {
            TextView self_photo = (TextView) findViewById(R.id.passportphoto);
            self_photo.setError("Please upload valid passport size image");
            self_photo.requestFocus();
            return false;
        } else if (persons.equalsIgnoreCase("outside")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString()).matches()) {
                input_email.setError("Please enter valid email address");
                input_email.requestFocus();
                return false;
            } else if (!ImageUtils.commonRegex(input_comp.getText().toString(), 150, "0-9 .&")) {
                input_comp.setError("Please enter valid company name");
                input_comp.requestFocus();
                return false;
            } else
                return true;

        } else
            return true;
    }

    private Boolean addressValidation() {
        if (!ImageUtils.commonAddress(city_name.getText().toString())) {
            input_address.setError("Please enter valid address");
            input_address.requestFocus();
            return false;
        } else if (!ImageUtils.commonRegex(city_name.getText().toString(), 150, ". ")) {
            city_name.setError("Please enter valid city");
            city_name.requestFocus();
            return false;
        } else if (select_state.getText().toString().equalsIgnoreCase("Select State")) {
            select_state.setError("Please enter valid state");
            select_state.requestFocus();
            return false;
        } else if (pin_code.getText().toString().isEmpty()|| pin_code.getText().toString().length()!=6) {
            pin_code.setError("Please enter valid pincode");
            pin_code.requestFocus();
            return false;
        } else if (document_id.getText().toString().isEmpty()) {
            document_id.setError("Please enter valid DocumentID");
            document_id.requestFocus();
            return false;
        } else if (documentfrontimage.getDrawable() == null) {
            TextView documentfront = (TextView) findViewById(R.id.documentfront);
            documentfront.setError("Please upload valid document front side image");
            documentfront.requestFocus();
            return false;
        } else if (documentbackimage.getDrawable() == null) {
            TextView documentfront = (TextView) findViewById(R.id.documentback);
            documentfront.setError("Please upload valid document back side image");
            documentfront.requestFocus();
            return false;
        } else
            return true;
    }

    private Boolean buisnessValidation() {
        if (persons.equalsIgnoreCase("outside")) {
            if (!pan_no.getText().toString().matches("^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$")) {
                pan_no.setError("Please enter valid pancard number");
                pan_no.requestFocus();
                return false;
            } else if (panphoto.getDrawable() == null) {
                TextView pan_photo = (TextView) findViewById(R.id.pan_photo);
                pan_photo.setError("Please upload valid pancard image");
                pan_photo.requestFocus();
                return false;
            } else if (shopPhoto.getDrawable() == null) {
                TextView shop_photo = (TextView) findViewById(R.id.shop_photo);
                shop_photo.setError("Please upload valid data");
                shop_photo.requestFocus();
                return false;
            } else
                return true;
        } else if (persons.equalsIgnoreCase("internal")) {
            if (pan_no.getText().length() != 0) {
                if (!pan_no.getText().toString().matches("^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$")) {
                    pan_no.setError("Please upload valid pancard number");
                    pan_no.requestFocus();
                    return false;
                } else if (pan_no.getText().toString().matches("^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$")) {
                    if (panphoto.getDrawable() == null) {
                        TextView pan_photo = (TextView) findViewById(R.id.pan_photo);
                        pan_photo.setError("Please upload valid pancard image");
                        pan_photo.requestFocus();
                        return false;
                    } else
                        return true;
                }
            } else
                return true;
        }
        return true;
    }

    private void selectImage(final int id1, final int id2) {
        final CharSequence[] items = {"Capture Image", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(KYCFormActivity.this);
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Image")) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, id1);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), id2);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == passportphoto1 || requestCode == passportphoto2) {
                if (requestCode == passportphoto1)
                    setImage((ImageView) findViewById(R.id.passportphotoimage), 1, 0, data, "passportPhoto", (TextView) findViewById(R.id.passportphoto));
                else if (requestCode == passportphoto2)
                    setImage((ImageView) findViewById(R.id.passportphotoimage), 0, 1, data, "passportPhoto", (TextView) findViewById(R.id.passportphoto));
            } else if (requestCode == DOCUMENT_FRONT1 || requestCode == DOCUMENT_FRONT2) {
                if (requestCode == DOCUMENT_FRONT1)
                    setImage((ImageView) findViewById(R.id.documentfrontimage), 1, 0, data, "uploadFront", (TextView) findViewById(R.id.documentfront));
                else if (requestCode == DOCUMENT_FRONT2)
                    setImage((ImageView) findViewById(R.id.documentfrontimage), 0, 1, data, "uploadFront", (TextView) findViewById(R.id.documentfront));
            } else if (requestCode == DOCUMENT_BACK1 || requestCode == DOCUMENT_BACK2) {
                if (requestCode == DOCUMENT_BACK1)
                    setImage((ImageView) findViewById(R.id.documentbackimage), 1, 0, data, "uploadBack", (TextView) findViewById(R.id.documentback));
                else if (requestCode == DOCUMENT_BACK2)
                    setImage((ImageView) findViewById(R.id.documentbackimage), 0, 1, data, "uploadBack", (TextView) findViewById(R.id.documentback));
            } else if (requestCode == PANPIC1 || requestCode == PANPIC2) {
                if (requestCode == PANPIC1)
                    setImage((ImageView) findViewById(R.id.panphoto), 1, 0, data, "pancardImg", (TextView) findViewById(R.id.pan_photo));
                else if (requestCode == PANPIC2)
                    setImage((ImageView) findViewById(R.id.panphoto), 0, 1, data, "pancardImg", (TextView) findViewById(R.id.pan_photo));
            } else if (requestCode == SHOPPIC1 || requestCode == SHOPPIC2) {
                if (requestCode == SHOPPIC1)
                    setImage((ImageView) findViewById(R.id.shopphoto), 1, 0, data, "shopPhoto", (TextView) findViewById(R.id.shop_photo));
                else if (requestCode == SHOPPIC2)
                    setImage((ImageView) findViewById(R.id.shopphoto), 0, 1, data, "shopPhoto", (TextView) findViewById(R.id.shop_photo));
            } else if (requestCode == CUSTPANPIC1 || requestCode == CUSTPANPIC2) {
                if (requestCode == CUSTPANPIC1)
                    setImage((ImageView) findViewById(R.id.cust_panphoto), 1, 0, data, "custpanphoto", (TextView) findViewById(R.id.cust_pan_photo));
                else if (requestCode == CUSTPANPIC2)
                    setImage((ImageView) findViewById(R.id.cust_panphoto), 0, 1, data, "custpanphoto", (TextView) findViewById(R.id.cust_pan_photo));
            } else if (requestCode == selfphoto1 || requestCode == selfphoto2) {
                if (requestCode == selfphoto1)
                    setImage((ImageView) findViewById(R.id.selfphoto), 1, 0, data, "selfPhoto", (TextView) findViewById(R.id.self_photo));
                else if (requestCode == selfphoto2)
                    setImage((ImageView) findViewById(R.id.selfphoto), 0, 1, data, "selfPhoto", (TextView) findViewById(R.id.self_photo));
            } else if (requestCode == signphoto1 || requestCode == signphoto2) {
                if (requestCode == signphoto1)
                    setImage((ImageView) findViewById(R.id.signphoto), 1, 0, data, "signPhoto", (TextView) findViewById(R.id.sign_photo));
                else if (requestCode == signphoto2)
                    setImage((ImageView) findViewById(R.id.signphoto), 0, 1, data, "signPhoto", (TextView) findViewById(R.id.sign_photo));
            }
        }
    }

    private void setImage(ImageView view, int id_1, int id_2, Intent data, String imageType, TextView textView) {
        Bitmap thumbnail = null;
        if (id_1 == 1)
            thumbnail = (Bitmap) data.getExtras().get("data");
        else if (id_2 == 1) {
            Uri uri = data.getData();
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (thumbnail != null) {
            try {
                int fileSizeInBytes = byteSizeOf(thumbnail);
                long fileSizeInKB = fileSizeInBytes / 1024;
                // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                long fileSizeInMB = fileSizeInKB / 1024;
                if (fileSizeInMB > 2)
                    Toast.makeText(KYCFormActivity.this, "Length Should be less than 2 MB", Toast.LENGTH_SHORT).show();
                else if (fileSizeInKB > 500 && fileSizeInMB < 2) {
                    kycMapImage.put(imageType, getBase64(thumbnail, 50));
                    view.setImageBitmap(thumbnail);
                    view.setVisibility(View.VISIBLE);
                    textView.setError(null);
                } else {
                    view.setImageBitmap(thumbnail);
                    view.setVisibility(View.VISIBLE);
                    textView.setError(null);
                    kycMapImage.put(imageType, getBase64(thumbnail, 100));
                }
            } catch (Exception e) {
            }
        }
    }

    private String getBase64(Bitmap bitmap, int ratio) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, ratio, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private String getBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void parsePersonalJson(String scandata) {
        try {
            JSONObject object = new JSONObject(scandata);
            if (object.has("name")) {
                input_name.setText(object.getString("name"));
                input_name.setEnabled(false);
            }
            if (object.has("dob")) {
                date1_text.setText(dataconvert(object.getString("dob")));
                date1_text.setEnabled(false);
                date1_text.setOnClickListener(null);
            }
//            reset.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    private String dataconvert(String formData) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(formData);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseAddressJson(String scandata) {
        try {
            JSONObject object = new JSONObject(scandata);
            if (object.has("house") && object.has("street") && object.has("lm") && object.has("vtc")) {
                String add = object.getString("house") + ", " + object.getString("street") + ", " + object.getString("lm") + ", " + object.getString("vtc").replaceAll("^\\s+", "");
                input_address.setText(add);
                input_address.setEnabled(false);
            } else if (object.has("street") && object.has("lm") && object.has("vtc")) {
                String add = object.getString("street") + ", " + object.getString("lm") + ", " + object.getString("vtc").replaceAll("^\\s+", "");
                input_address.setText(add.replace("null,", ""));
                input_address.setEnabled(false);
            } else if (object.has("house") && object.has("street") && object.has("lm") && object.has("loc") && object.has("vtc")) {
                String add = object.getString("house") + ", " + object.getString("street") + ", " + object.getString("lm") + ", " + object.getString("loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "");
                input_address.setText(add);
                input_address.setEnabled(false);
            } else if (object.has("house") && object.has("street") && object.has("loc") && object.has("vtc")) {
                String add = object.getString("house") + ", " + object.getString("street") + ", " + object.getString("loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "");
                input_address.setText(add);
                input_address.setEnabled(false);
            } else if (object.has("_house") && object.has("_street") && object.has("_lm") && object.has("_vtc")) {
                String add = object.getString("_house") + ", " + object.getString("_street") + ", " + object.getString("_lm") + ", " + object.getString("_vtc").replaceAll("^\\s+", "");
                input_address.setText(add);
                input_address.setEnabled(false);
            } else if (object.has("_loc") && object.has("vtc")) {
                String add = object.getString("_loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "");
                input_address.setText(add);
                input_address.setEnabled(false);
            } else if (object.has("loc") && object.has("vtc")) {
                String add = object.getString("loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "");
                input_address.setText(add);
                input_address.setEnabled(false);
            } else if (object.has("_loc") && object.has("_vtc")) {
                String add = object.getString("_loc") + ", " + object.getString("_vtc").replaceAll("^\\s+", "");
                input_address.setText(add);
                input_address.setEnabled(false);
            } else if (object.has("_lm") && object.has("_loc") && object.has("_vtc")) {
                String add = object.getString("_lm") + ", " + object.getString("_loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "");
                input_address.setText(add);
                input_address.setEnabled(false);
            }
            if (object.has("pc"))
                pin_code.setText(object.getString("pc"));
            else if (object.has("_pc"))
                pin_code.setText(object.getString("_pc"));
            if (object.has("_dist"))
                city_name.setText(object.getString("_dist"));
            else if (object.has("dist"))
                city_name.setText(object.getString("dist"));
            if (object.has("state"))
                select_state.setText(object.getString("state"));
            if (object.has("_state"))
                select_state.setText(object.getString("_state"));
            select_state.setEnabled(false);
            city_name.setEnabled(false);
            pin_code.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void chechStatus(JSONObject object) {

    }

    private void insertPersonal(JSONObject mapValue, JSONObject mapPhoto, String name, String documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            String imageName = "passportPhoto_" + mapValue.getString("mobileNo") + ".jpg";
            values.put(RapipayDB.MOBILENO, mapValue.getString("mobileNo"));
            values.put(RapipayDB.USER_NAME, name);
            values.put(RapipayDB.DOB, mapValue.getString("dob"));
            values.put(RapipayDB.EMAILID, mapValue.getString("email"));
            values.put(RapipayDB.COMPANY_NAME, mapValue.getString("companyName"));
            values.put(RapipayDB.IMAGE_NAME, imageName);
            values.put(RapipayDB.PERSONAL_CLICKED, "true");
            values.put(RapipayDB.DOCUMENTTYPE, documentType);
            values.put(RapipayDB.DOCUMENTID, documentID);
            if (mapPhoto.has("passportPhoto"))
                values.put(RapipayDB.PASSPORT_PHOTO, byteConvert(mapPhoto.getString("passportPhoto")));
            else
                values.put(RapipayDB.PASSPORT_PHOTO, "");
            dba.insert(RapipayDB.TABLE_KYC_PERSONAL, null, values);
            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertAddress(JSONObject mapValue, JSONObject mapPhoto, String documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
        try {

            String frontimageName = "frontPhoto_" + mobileNo + ".jpg";
            String backimageName = "backPhoto_" + mobileNo + ".jpg";

            ContentValues values = new ContentValues();
            values.put(RapipayDB.MOBILENO, mobileNo);
            values.put(RapipayDB.ADDRESS, mapValue.getString("address"));
            values.put(RapipayDB.CITY, mapValue.getString("city"));
            values.put(RapipayDB.STATE, mapValue.getString("state"));
            values.put(RapipayDB.PINCODE, mapValue.getString("pinCode"));
            values.put(RapipayDB.DOCUMENTFRONT_IMAGENAME, frontimageName);
            values.put(RapipayDB.DOCUMENTBACK_IMAGENAME, backimageName);
            values.put(RapipayDB.ADDRESS_CLICKED, "true");
            values.put(RapipayDB.DOCUMENTTYPE, documentType);
            values.put(RapipayDB.DOCUMENTID, documentID);
            values.put(RapipayDB.DOCUMENTFRONT_PHOTO, byteConvert(mapPhoto.getString("uploadFront")));
            values.put(RapipayDB.DOCUMENTBACK_PHOTO, byteConvert(mapPhoto.getString("uploadBack")));
            dba.insert(RapipayDB.TABLE_KYC_ADDRESS, null, values);

            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmap(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        return drawable.getBitmap();
    }

    private void insertBuisness(JSONObject mapValue, JSONObject mapPhoto, String documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
//        String pathPan = "", pathShop = "";
        try {
            String panimageName = "panPhoto_" + mobileNo + ".jpg";
            String shopimageName = "shopPhoto_" + mobileNo + ".jpg";

            ContentValues values = new ContentValues();
            values.put(RapipayDB.MOBILENO, mobileNo);
            values.put(RapipayDB.PANNUMBER, mapValue.getString("panNo"));
            values.put(RapipayDB.GSTINNUMBER, mapValue.getString("gstIN"));
            values.put(RapipayDB.PAN_PHOTO_IMAGENAME, panimageName);
            values.put(RapipayDB.SHOP_PHOTO_IMAGENAME, shopimageName);
            values.put(RapipayDB.BUISNESS_CLICKED, "true");
            values.put(RapipayDB.DOCUMENTTYPE, documentType);
            values.put(RapipayDB.DOCUMENTID, documentID);
            if (mapPhoto.has("pancardImg"))
                values.put(RapipayDB.PAN_PHOTO, byteConvert(mapPhoto.getString("pancardImg")));
            else
                values.put(RapipayDB.PAN_PHOTO, "");
            if (mapPhoto.has("shopPhoto"))
                values.put(RapipayDB.SHOP_PHOTO, byteConvert(mapPhoto.getString("shopPhoto")));
            else
                values.put(RapipayDB.SHOP_PHOTO, "");
            dba.insert(RapipayDB.TABLE_KYC_BUISNESS, null, values);

            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertVerify(JSONObject mapValue, JSONObject mapPhoto, String documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
//        String pathSelf = "", pathSign = "";
        try {

            String selfimageName = "selfPhoto_" + mobileNo + ".jpg";
            String signimageName = "signPhoto_" + mobileNo + ".jpg";
            ContentValues values = new ContentValues();
            values.put(RapipayDB.MOBILENO, mobileNo);
            values.put(RapipayDB.SELF_PHOTO_IMAGENAME, selfimageName);
            values.put(RapipayDB.SIGN_PHOTO_IMAGENAME, signimageName);
            values.put(RapipayDB.VERIFY_CLICKED, "true");
            values.put(RapipayDB.DOCUMENTTYPE, documentType);
            values.put(RapipayDB.DOCUMENTID, documentID);
            if (mapPhoto.has("selfPhoto"))
                values.put(RapipayDB.SELF_PHOTO, byteConvert(mapPhoto.getString("selfPhoto")));
            else
                values.put(RapipayDB.SELF_PHOTO, "");
            if (mapPhoto.has("signPhoto"))
                values.put(RapipayDB.SIGN_PHOTO, byteConvert(mapPhoto.getString("signPhoto")));
            else
                values.put(RapipayDB.SIGN_PHOTO, "");
            dba.insert(RapipayDB.TABLE_KYC_VERIFICATION, null, values);

            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
//            bluetooth();
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
                int permissionLocation = ContextCompat.checkSelfPermission(KYCFormActivity.this,
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
                                            .checkSelfPermission(KYCFormActivity.this,
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
                                        status.startResolutionForResult(KYCFormActivity.this,
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

    protected void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(KYCFormActivity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(KYCFormActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
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

}
