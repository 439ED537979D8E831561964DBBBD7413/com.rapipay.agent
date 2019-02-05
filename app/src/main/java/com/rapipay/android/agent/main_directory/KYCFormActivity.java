package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseArray;
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
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.NewKYCPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.AgentKYCFragment;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.CustomProgessDialog;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class KYCFormActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private Uri imageUri;
    public static String formData = null;
    String type, button, mobileNo, persons, scandata = null, documentType = null, documentID = null, customerType;
    ImageView back_click, delete_all;
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
    private boolean isSubmitClicked = false;

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
            input_email.setHint("Email ID (Optional)");
            input_comp.setHint("Company Name (Optional)");
            pan_no.setHint("Pan Number (Optional)");
            findViewById(R.id.passportphoto).setVisibility(View.GONE);
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
        date1_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });
        delete_all = (ImageView) findViewById(R.id.delete_all);
        date1_text.setHint("Date Of Birth");
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
                            kycMapImage.put("passportPhoto", getBase64(addWaterMark(getBitmap((ImageView) findViewById(R.id.passportphotoimage)))));
                    }
                    if (newKYCList_Personal.get(0).getSCANIMAGEPATH() != null) {
                        if (!kycMapImage.has("kycImage"))
                            kycMapImage.put("kycImage", getBase64(addWaterMark(loadImageFromStorage(newKYCList_Personal.get(0).getSCANIMAGENAME(), newKYCList_Personal.get(0).getSCANIMAGEPATH()))));
                    }
                    input_name.setEnabled(false);
                    if (input_email.getText().toString().length() != 0)
                        input_email.setEnabled(false);
                    if (input_comp.getText().toString().length() != 0)
                        input_comp.setEnabled(false);
                    type = newKYCList_Personal.get(0).getSCANTYPE();
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
                            kycMapImage.put("uploadFront", getBase64(addWaterMark(getBitmap((ImageView) findViewById(R.id.documentfrontimage)))));
                    }
                    if (newKYCList_Address.get(0).getDOCUMENTBACK_PHOTO() != null && !newKYCList_Address.get(0).getDOCUMENTBACK_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Address.get(0).getDOCUMENTBACK_IMAGENAME(), (ImageView) findViewById(R.id.documentbackimage), newKYCList_Address.get(0).getDOCUMENTBACK_PHOTO());
                        findViewById(R.id.documentback).setVisibility(View.GONE);
                        if (!kycMapImage.has("uploadBack"))
                            kycMapImage.put("uploadBack", getBase64(addWaterMark(getBitmap((ImageView) findViewById(R.id.documentbackimage)))));
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
                            kycMapImage.put("selfPhoto", getBase64(addWaterMark(getBitmap((ImageView) findViewById(R.id.selfphoto)))));
                    }
                    if (newKYCList_Verify.get(0).getSIGN_PHOTO() != null && !newKYCList_Verify.get(0).getSIGN_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Verify.get(0).getSIGN_PHOTO_IMAGENAME(), (ImageView) findViewById(R.id.signphoto), newKYCList_Verify.get(0).getSIGN_PHOTO());
                        findViewById(R.id.sign_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("signPhoto"))
                            kycMapImage.put("signPhoto", getBase64(addWaterMark(getBitmap((ImageView) findViewById(R.id.signphoto)))));
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
                            kycMapImage.put("pancardImg", getBase64(addWaterMark(getBitmap((ImageView) findViewById(R.id.panphoto)))));
                    }
                    if (newKYCList_Business.get(0).getSHOP_PHOTO() != null && !newKYCList_Business.get(0).getSHOP_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Business.get(0).getSHOP_PHOTO_IMAGENAME(), (ImageView) findViewById(R.id.shopphoto), newKYCList_Business.get(0).getSHOP_PHOTO());
                        findViewById(R.id.shop_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("shopPhoto"))
                            kycMapImage.put("shopPhoto", getBase64(addWaterMark(getBitmap((ImageView) findViewById(R.id.shopphoto)))));
                    }
                    if (pan_no.getText().toString().length() != 0)
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

    private void clearVerify() {
        AgentKYCFragment.bitmap_trans = null;
        AgentKYCFragment.byteBase64 = "";
        CustomerKYCActivity.bitmap_trans = null;
        CustomerKYCActivity.byteBase64 = "";
        db.deleteRow(mobileNo, "");
        finish();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.delete_all:
                    db.deleteRow(mobileNo, "verify");
                    clearVerify();
                    break;
                case R.id.back_click:
                    finish();
                    break;
                case R.id.documentfront:
                    selectImage(DOCUMENT_FRONT1, DOCUMENT_FRONT2, "uploadFront");
                    break;
                case R.id.passportphoto:
                    selectImage(passportphoto1, passportphoto2, "passportPhoto");
                    break;
                case R.id.documentback:
                    selectImage(DOCUMENT_BACK1, DOCUMENT_BACK2, "uploadBack");
                    break;
                case R.id.pan_photo:
                    if (persons.equalsIgnoreCase("internal") || persons.equalsIgnoreCase("outside"))
                        if (!pan_no.getText().toString().isEmpty())
                            selectImage(PANPIC1, PANPIC2, "pancardImg");
                        else
                            Toast.makeText(KYCFormActivity.this, "Please enter pancard number first.", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.cust_pan_photo:
                    selectImage(CUSTPANPIC1, CUSTPANPIC2, "custpanphoto");
                    break;
                case R.id.shop_photo:
                    selectImage(SHOPPIC1, SHOPPIC2, "shopPhoto");
                    break;
                case R.id.self_photo:
                    selectImage(selfphoto1, selfphoto2, "selfPhoto");
                    break;
                case R.id.sign_photo:
                    selectImage(signphoto1, signphoto2, "signPhoto");
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
                        if (type.equalsIgnoreCase("SCAN")) {
                            if (customerType.equalsIgnoreCase("C"))
                                kycMapImage.put("kycImage", getBase64(CustomerKYCActivity.bitmap_trans, 100));
                            else if (customerType.equalsIgnoreCase("A"))
                                kycMapImage.put("kycImage", getBase64(AgentKYCFragment.bitmap_trans, 100));
                        }
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
        if (!isSubmitClicked) {
            new AsyncPostMethod(WebConfig.ProcessKYC, getKycMapImage().toString(), headerData, KYCFormActivity.this, getString(R.string.responseTimeOut)).execute();
            isSubmitClicked = true;
        }
    }

    public String getsession_ValidateKyc(String kycType, String tokenId) {
        JSONObject jsonObject = new JSONObject();
        String byteBase64 = "", passportPhoto = "", signPhoto = "", shopPhoto = "", pancardImg = "", kycImage = "";
//        if (customerType.equalsIgnoreCase("C"))
//            byteBase64 = CustomerKYCActivity.byteBase64;
//        else if (customerType.equalsIgnoreCase("A"))
//            byteBase64 = AgentKYCFragment.byteBase64;
        String form = null;
        try {
//            if (kycMapImage.has("kycImage"))
//                kycImage = kycMapImage.get("kycImage").toString();
//            if (kycMapImage.has("pancardImg"))
//                pancardImg = kycMapImage.get("pancardImg").toString();
//            if (kycMapImage.has("passportPhoto"))
//                passportPhoto = kycMapImage.get("passportPhoto").toString();
//            if (kycMapImage.has("signPhoto"))
//                signPhoto = kycMapImage.get("signPhoto").toString();
//            if (kycMapImage.has("shopPhoto"))
//                shopPhoto = kycMapImage.get("shopPhoto").toString();
            jsonObject.put("serviceType", "KYC_PROCESS");
            jsonObject.put("reKYC", "");
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("kycType", kycType);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("langitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("isreKYC", "N");
            jsonObject.put("tokenId", tokenId);
            jsonObject.put("isMobileKYC", "Y");
            if (documentType.equalsIgnoreCase("Aadhar Card") && customerType.equalsIgnoreCase("C") && type.equalsIgnoreCase("SCAN"))
                jsonObject.put("isAuto", "2");
            else
                jsonObject.put("isAuto", "1");
            jsonObject.put("isEditable", "N");
            jsonObject.put("listdata", kycMapData.toString());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            form = "<html>\n" +
                    "\t<body>\n" +
                    "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYCFORWARD + "\">\n" +
                    "\t\t\t<input name=\"requestedData\" value=\"" + getDataBase64(jsonObject.toString()) + "\" type=\"hidden\"/>\n" +
//                    "\t\t\t<input name=\"passportPhoto\" value=\"" + passportPhoto + "\" type=\"hidden\"/>\n" +
//                    "\t\t\t<input name=\"signPhoto\" value=\"" + signPhoto + "\" type=\"hidden\"/>\n" +
//                    "\t\t\t<input name=\"uploadFront\" value=\"" + kycMapImage.get("uploadFront").toString() + "\" type=\"hidden\"/>\n" +
//                    "\t\t\t<input name=\"uploadBack\" value=\"" + kycMapImage.get("uploadBack").toString() + "\" type=\"hidden\"/>\n" +
//                    "\t\t\t<input name=\"pancardImg\" value=\"" + pancardImg + "\" type=\"hidden\"/>\n" +
//                    "\t\t\t<input name=\"shopPhoto\" value=\"" + shopPhoto + "\" type=\"hidden\"/>\n" +
//                    "\t\t\t<input name=\"selfPhoto\" value=\"" + kycMapImage.get("selfPhoto").toString() + "\" type=\"hidden\"/>\n" +
//                    "\t\t\t<input name=\"kycImage\" value=\"" + kycImage + "\" type=\"hidden\"/>\n" +
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

    private JSONObject getKycMapImage() {
        CustomProgessDialog dialog = new CustomProgessDialog(KYCFormActivity.this);
        JSONObject jsonObject = new JSONObject();
        String passportPhoto = "", signPhoto = "", shopPhoto = "", pancardImg = "", kycImage = "";
        try {
            if (kycMapImage.has("kycImage"))
                kycImage = kycMapImage.get("kycImage").toString();
            if (kycMapImage.has("pancardImg"))
                pancardImg = kycMapImage.get("pancardImg").toString();
            if (kycMapImage.has("passportPhoto"))
                passportPhoto = kycMapImage.get("passportPhoto").toString();
            if (kycMapImage.has("signPhoto"))
                signPhoto = kycMapImage.get("signPhoto").toString();
            if (kycMapImage.has("shopPhoto"))
                shopPhoto = kycMapImage.get("shopPhoto").toString();
            jsonObject.put("serviceType", "VALIDATE_KYC_DETAILS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("mobileNo", mobileNo);
            jsonObject.put("documentType", documentType);
            jsonObject.put("documentId", documentID);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("kycType", customerType);
            jsonObject.put("isKYCImageAvailable", "Y");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("passportPhoto", passportPhoto);
            jsonObject.put("signPhoto", signPhoto);
            jsonObject.put("uploadFront", kycMapImage.get("uploadFront").toString());
            jsonObject.put("uploadBack", kycMapImage.get("uploadBack").toString());
            jsonObject.put("pancardImg", pancardImg);
            jsonObject.put("shopPhoto", shopPhoto);
            jsonObject.put("selfPhoto", kycMapImage.get("selfPhoto").toString());
            jsonObject.put("kycImage", kycImage);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.hide_progress();
        return jsonObject;
    }

    private Bitmap addWaterMark(Bitmap src) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = "RAPIPAY" + sdf.format(new Date());
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE); // Text Color
        paint.setTextSize(10);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
        Bitmap waterMark = BitmapFactory.decodeResource(getResources(), R.drawable.rapipay);
        canvas.drawText(currentDateandTime, w / 4, h - 10, paint);
        return result;
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
            Toast.makeText(KYCFormActivity.this, "Please select valid date of birth", Toast.LENGTH_SHORT).show();
            date1_text.setError("Please enter valid date");
            date1_text.requestFocus();
            return false;
        } else if (customerType.equalsIgnoreCase("C") && date1_text.getText().toString().isEmpty()) {
            date1_text.setError("Please enter valid date");
            date1_text.requestFocus();
            return false;
        } else if (customerType.equalsIgnoreCase("A")) {
            if (!printDifference(mainDate(date1_text.getText().toString()))) {
                Toast.makeText(KYCFormActivity.this, "Please select valid date of birth", Toast.LENGTH_SHORT).show();
                date1_text.setError("Please enter valid date");
                date1_text.requestFocus();
                return false;
            } else
                return true;
        } else if (persons.equalsIgnoreCase("outside")) {
            if (passportphotoimage.getDrawable() == null) {
                TextView self_photo = (TextView) findViewById(R.id.passportphoto);
                self_photo.setError("Please upload valid passport size image");
                self_photo.requestFocus();
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString()).matches()) {
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
        if (!ImageUtils.commonAddress(input_address.getText().toString())) {
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
        } else if (pin_code.getText().toString().isEmpty() || pin_code.getText().toString().length() != 6) {
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
            } else if (gsin_no.getText().toString().length() != 0) {
                if (!gsin_no.getText().toString().matches("^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[0-9]{1}[a-zA-Z]{1}[a-z0-9A-Z]{1}$")) {
                    gsin_no.setError("Please upload valid data");
                    gsin_no.requestFocus();
                    return false;
                }
            } else
                return true;
        } else if (persons.equalsIgnoreCase("internal")) {
            if (pan_no.getText().toString().length() != 0) {
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

    private void selectImage(final int id1, final int id2, final String imageType) {
        final CharSequence[] items = {"Capture Image", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(KYCFormActivity.this);
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Image")) {
                    Intent intent = new Intent(KYCFormActivity.this, CameraKitActivity.class);
                    intent.putExtra("ImageType", imageType);
                    intent.putExtra("REQUESTTYPE", id1);
                    startActivityForResult(intent, id1);
//                    String filename = System.currentTimeMillis() + ".jpg";
//
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, filename);
//                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//                    Intent intent = new Intent();
//                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    startActivityForResult(intent, id1);
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

    private void setImage(ImageView view, int id_1, int id_2, Intent data, String imageTypes, TextView textView) {
        if (id_1 == 1) {
            String path = data.getStringExtra("ImagePath");
            String imageType = data.getStringExtra("ImageType");
            Bitmap bitmap = loadImageFromStorage(imageType, path);
            inspect(bitmap, documentType, documentID, imageType, textView, view);
        } else if (id_2 == 1) {
            if (data != null) {
                InputStream is = null;
                Bitmap bitmap = null;
                try {
                    is = getContentResolver().openInputStream(data.getData());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inSampleSize = 2;
                    options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
                    bitmap = BitmapFactory.decodeStream(is, null, options);
                    inspect(bitmap, documentType, documentID, imageTypes, textView, view);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateImage(Bitmap thumbnail, ImageView view, TextView textView, String imageType) {
        try {
            Bitmap bitmap = getResizedBitmap(thumbnail, 1500);
            int fileSizeInBytes = byteSizeOf(bitmap);
            long fileSizeInKB = fileSizeInBytes / 1024;
            long fileSizeInMB = fileSizeInKB / 1024;
            if (fileSizeInMB > 10)
                Toast.makeText(KYCFormActivity.this, "Length Should be less than 10 MB", Toast.LENGTH_SHORT).show();
            else if (fileSizeInKB > 500 && fileSizeInMB < 10) {
                view.setImageBitmap(bitmap);
                view.setVisibility(View.VISIBLE);
                textView.setError(null);
                kycMapImage.put(imageType, getBase64(addWaterMark(bitmap), 100));
            } else {
                view.setImageBitmap(bitmap);
                view.setVisibility(View.VISIBLE);
                textView.setError(null);
                kycMapImage.put(imageType, getBase64(addWaterMark(bitmap), 100));
            }
        } catch (Exception e) {
        }
    }

    private void inspect(Bitmap bitmap, String documentType, String documentID, String imageType, TextView textView, ImageView view) {
//        InputStream is = null;
//        Bitmap bitmap = null;
        try {
//            is = getContentResolver().openInputStream(uri);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            options.inSampleSize = 2;
//            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
//            bitmap = BitmapFactory.decodeStream(is, null, options);
            String condition = "where " + RapipayDB.MOBILENO + "='" + mobileNo + "'" + " AND " + RapipayDB.DOCUMENTTYPE + "='" + documentType + "'" + " AND " + RapipayDB.DOCUMENTID + "='" + documentID + "'";
            ArrayList<NewKYCPozo> newKYCList_Personal = db.getKYCDetails_Personal(condition);
            if (newKYCList_Personal.size() != 0)
                type = newKYCList_Personal.get(0).getSCANTYPE();
            if (type.equalsIgnoreCase("SCAN")) {
                if (imageType.equalsIgnoreCase("uploadFront")) {
                    if (inspectFromBitmap(bitmap, documentType, documentID, imageType)) {
                        updateImage(bitmap, view, textView, imageType);
                    } else
                        Toast.makeText(KYCFormActivity.this, "Please upload clear and same document image as mentioned before, or Do change camera rotation", Toast.LENGTH_SHORT).show();
                } else if (imageType.equalsIgnoreCase("pancardImg") && (persons.equalsIgnoreCase("outside") || persons.equalsIgnoreCase("internal"))) {
                    if (pan_no.getText().toString().length() != 0) {
                        if (inspectFromBitmap(bitmap, documentType, documentID, imageType)) {
                            updateImage(bitmap, view, textView, imageType);
                        } else
                            Toast.makeText(KYCFormActivity.this, "Please upload clear and same document image as mentioned before, or Do change camera rotation", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KYCFormActivity.this, "Please enter pancard number first.", Toast.LENGTH_SHORT).show();
                    }
                } else
                    updateImage(bitmap, view, textView, imageType);
            } else
                updateImage(bitmap, view, textView, imageType);
        } catch (Exception e) {
            Log.w(TAG, "Failed to find the file: " + e);
        }

    }

    private boolean inspectFromBitmap(Bitmap bitmap, String documentType, String documentID, String imageType) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new android.app.AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return false;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            StringBuilder detectedText = new StringBuilder();
            if (imageType.equalsIgnoreCase("pancardImg")) {
                for (TextBlock textBlock : textBlocks) {
                    if (textBlock != null && textBlock.getValue() != null) {
                        detectedText.append(textBlock.getValue());
                        detectedText.append("\n");
                    }
                }
                String textSplit[] = detectedText.toString().split("\n");
                for (int i = 0; i < textSplit.length; i++) {
                    if (imageType.equalsIgnoreCase("pancardImg") && (persons.equalsIgnoreCase("outside") || persons.equalsIgnoreCase("internal"))) {
                        if (textSplit[i].matches("^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$"))
                            if (pan_no.getText().toString().equalsIgnoreCase(textSplit[i].replace(" ", "")))
                                return true;
                            else
                                return false;
                    }
                }

            } else {
                for (TextBlock textBlock : textBlocks) {
                    if (textBlock != null && textBlock.getValue() != null) {
                        detectedText.append(textBlock.getValue());
                        detectedText.append("@");
                    }
                }
                String textSplit[] = detectedText.toString().split("@");
                for (int i = 0; i < textSplit.length; i++) {
                    if (documentType.equalsIgnoreCase("Aadhar Card")) {
                        if (textSplit[i].matches("^[0-9]{4}+\\s+[0-9]{4}+\\s+[0-9]{4}$") || textSplit[i].matches(documentID))
                            if (documentID.equalsIgnoreCase(textSplit[i].replace(" ", "")))
                                return true;
                            else
                                return false;
                    } else if (documentType.equalsIgnoreCase("Voter Id Card")) {
                        if (textSplit[i].matches("^[A-Z]{3}[0-9]{7}$") || textSplit[i].matches("^[A-Z\\/]{3}[0-9\\/]{3}[0-9\\/]{4}[0-9]{6}$") || textSplit[i].matches(documentID))
                            if (documentID.equalsIgnoreCase(textSplit[i].replace(" ", "")))
                                return true;
                            else
                                return false;
                    } else if (documentType.equalsIgnoreCase("Passport")) {
                        if (textSplit[i].matches(documentID))
                            if (documentID.equalsIgnoreCase(textSplit[i].replace(" ", "")))
                                return true;
                            else
                                return false;
                    }
                }
            }
        } finally {
            textRecognizer.release();
        }
        return false;
    }

    private String getBase64(Bitmap bitmap, int ratio) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, ratio, byteArrayOutputStream);
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
                if (object.getString("dob").contains("/") || object.getString("dob").contains("-")) {
                    date1_text.setText(dataconvert(object.getString("dob")));
                    date1_text.setEnabled(false);
                    date1_text.setOnClickListener(null);
                }
            }
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
        SimpleDateFormat format = null;
        if (formData.matches("^[0-9-]{5}[0-9]{2}[0-9-]{3}$"))
            format = new SimpleDateFormat("yyyy-MM-dd");
        else if (formData.matches("^[0-9-]{3}[0-9]{2}[0-9-]{5}$"))
            format = new SimpleDateFormat("dd-MM-yyyy");
        if (formData.matches("^[0-9/]{5}[0-9]{2}[0-9/]{3}$"))
            format = new SimpleDateFormat("yyyy/MM/dd");
        else if (formData.matches("^[0-9/]{3}[0-9]{2}[0-9/]{5}$"))
            format = new SimpleDateFormat("dd/MM/yyyy");
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
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("VALIDATE_KYC_DETAILS")) {
                    isSubmitClicked = false;
                    formData = "";
                    formData = getsession_ValidateKyc(customerType, object.getString("tokenId"));
                    Intent intent = new Intent(KYCFormActivity.this, WebViewVerify.class);
                    intent.putExtra("persons", persons);
                    intent.putExtra("mobileNo", mobileNo);
                    intent.putExtra("documentType", documentType);
                    intent.putExtra("documentID", documentID);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertPersonal(JSONObject mapValue, JSONObject mapPhoto, String name, String
            documentID, String documentType, String customerType) {
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
            values.put(RapipayDB.SCANTYPE, type);
            if (mapPhoto.has("passportPhoto"))
                values.put(RapipayDB.PASSPORT_PHOTO, byteConvert(mapPhoto.getString("passportPhoto")));
            if (type.equalsIgnoreCase("SCAN")) {
                String scanImageName = "scanPhoto_" + mobileNo + ".jpg";
                String pathScan = saveToInternalStorage(base64Convert(mapPhoto.getString("kycImage")), scanImageName);
                values.put(RapipayDB.SCANIMAGE, scanImageName);
                values.put(RapipayDB.SCANIMAGEPATH, pathScan);
            }
            dba.insert(RapipayDB.TABLE_KYC_PERSONAL, null, values);
            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertAddress(JSONObject mapValue, JSONObject mapPhoto, String
            documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
        try {
            String frontimageName = "frontPhoto_" + mobileNo + ".jpg";
            String backimageName = "backPhoto_" + mobileNo + ".jpg";
            String pathBack = saveToInternalStorage(base64Convert(mapPhoto.getString("uploadBack")), backimageName);
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
            values.put(RapipayDB.DOCUMENTBACK_PHOTO, pathBack);
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

    private void insertBuisness(JSONObject mapValue, JSONObject mapPhoto, String
            documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
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
            if (mapPhoto.has("shopPhoto"))
                values.put(RapipayDB.SHOP_PHOTO, saveToInternalStorage(base64Convert(mapPhoto.getString("shopPhoto")), shopimageName));
            dba.insert(RapipayDB.TABLE_KYC_BUISNESS, null, values);
            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertVerify(JSONObject mapValue, JSONObject mapPhoto, String
            documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
        String signImage = "";
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
            if (mapPhoto.has("signPhoto"))
                values.put(RapipayDB.SIGN_PHOTO, saveToInternalStorage(base64Convert(mapPhoto.getString("signPhoto")), signimageName));
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

    protected void getMyLocation() {
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
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(KYCFormActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(KYCFormActivity.this,
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
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(KYCFormActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    protected synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
}
