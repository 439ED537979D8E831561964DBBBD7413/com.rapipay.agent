package com.rapipay.android.agent.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.grantland.widget.AutofitTextView;

public class KYCFormActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener {

    public static String formData = null;
    String type, button, mobileNo, persons, scandata = null, documentType = null, documentID = null, customerType;
    ImageView back_click;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_personal_detail);
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
        select_state = (TextView) findViewById(R.id.state_name);
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
                    if (!newKYCList_Personal.get(0).getPASSPORT_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Personal.get(0).getIMAGE_NAME(), (ImageView) findViewById(R.id.passportphotoimage), newKYCList_Personal.get(0).getPASSPORT_PHOTO());
                        findViewById(R.id.passportphoto).setVisibility(View.GONE);
                        if (!kycMapImage.has("passportPhoto"))
                            kycMapImage.put("passportPhoto", getBase64(getBitmap((ImageView) findViewById(R.id.passportphotoimage))));
                    }
                    input_name.setEnabled(false);
                    input_email.setEnabled(false);
                    input_comp.setEnabled(false);
                    date1_text.setEnabled(false);
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
                    if (!newKYCList_Address.get(0).getDOCUMENTFRONT_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Address.get(0).getDOCUMENTFRONT_IMAGENAME(), (ImageView) findViewById(R.id.documentfrontimage), newKYCList_Address.get(0).getDOCUMENTFRONT_PHOTO());
                        findViewById(R.id.documentfront).setVisibility(View.GONE);
                        if (!kycMapImage.has("uploadFront"))
                            kycMapImage.put("uploadFront", getBase64(getBitmap((ImageView) findViewById(R.id.documentfrontimage))));
                    }
                    if (!newKYCList_Address.get(0).getDOCUMENTBACK_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Address.get(0).getDOCUMENTBACK_IMAGENAME(), (ImageView) findViewById(R.id.documentbackimage), newKYCList_Address.get(0).getDOCUMENTBACK_PHOTO());
                        findViewById(R.id.documentback).setVisibility(View.GONE);
                        if (!kycMapImage.has("uploadBack"))
                            kycMapImage.put("uploadBack", getBase64(getBitmap((ImageView) findViewById(R.id.documentbackimage))));
                    }
                    input_address.setEnabled(false);
                    city_name.setEnabled(false);
                    select_state.setEnabled(false);
                    pin_code.setEnabled(false);
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
                    if (!newKYCList_Verify.get(0).getSELF_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Verify.get(0).getSELF_PHOTO_IMAGENAME(), (ImageView) findViewById(R.id.selfphoto), newKYCList_Verify.get(0).getSELF_PHOTO());
                        findViewById(R.id.self_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("selfPhoto"))
                            kycMapImage.put("selfPhoto", getBase64(getBitmap((ImageView) findViewById(R.id.selfphoto))));
                    }
                    if (!newKYCList_Verify.get(0).getSIGN_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Verify.get(0).getSIGN_PHOTO_IMAGENAME(), (ImageView) findViewById(R.id.signphoto), newKYCList_Verify.get(0).getSIGN_PHOTO());
                        findViewById(R.id.sign_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("signPhoto"))
                            kycMapImage.put("signPhoto", getBase64(getBitmap((ImageView) findViewById(R.id.signphoto))));
                    }
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
                    if (!newKYCList_Business.get(0).getPAN_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Business.get(0).getPAN_PHOTO_IMAGENAME(), (ImageView) findViewById(R.id.panphoto), newKYCList_Business.get(0).getPAN_PHOTO());
                        findViewById(R.id.pan_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("pancardImg"))
                            kycMapImage.put("pancardImg", getBase64(getBitmap((ImageView) findViewById(R.id.panphoto))));
                    }
                    if (!newKYCList_Business.get(0).getSHOP_PHOTO().equalsIgnoreCase("")) {
                        loadImageFromStorage(newKYCList_Business.get(0).getSHOP_PHOTO_IMAGENAME(), (ImageView) findViewById(R.id.shopphoto), newKYCList_Business.get(0).getSHOP_PHOTO());
                        findViewById(R.id.shop_photo).setVisibility(View.GONE);
                        if (!kycMapImage.has("shopPhoto"))
                            kycMapImage.put("shopPhoto", getBase64(getBitmap((ImageView) findViewById(R.id.shopphoto))));
                    }
                    pan_no.setEnabled(false);
                    gsin_no.setEnabled(false);
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
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
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
                self_photo.setError("Please enter valid data");
                self_photo.requestFocus();
                return false;
            } else if (signphoto.getDrawable() == null) {
                TextView sign_photo = (TextView) findViewById(R.id.sign_photo);
                sign_photo.setError("Please enter valid data");
                sign_photo.requestFocus();
                return false;
            } else
                return true;
        } else if (persons.equalsIgnoreCase("internal")) {
            if (selfphoto.getDrawable() == null) {
                TextView self_photo = (TextView) findViewById(R.id.self_photo);
                self_photo.setError("Please enter valid data");
                self_photo.requestFocus();
                return false;
            } else
                return true;
        } else
            return false;
    }

    private Boolean personalValidation() {
        if (!ImageUtils.commonNumber(input_number.getText().toString(), 10)) {
            input_number.setError("Please enter valid data");
            input_number.requestFocus();
            return false;
        } else if (!ImageUtils.commonRegex(input_name.getText().toString(), 150, ". ")) {
            input_name.setError("Please enter valid data");
            input_name.requestFocus();
            return false;
        } else if (date1_text.getText().toString().isEmpty()) {
            date1_text.setError("Please enter valid data");
            date1_text.requestFocus();
            return false;
        } else if (passportphotoimage.getDrawable() == null) {
            TextView self_photo = (TextView) findViewById(R.id.passportphoto);
            self_photo.setError("Please enter valid data");
            self_photo.requestFocus();
            return false;
        } else if (persons.equalsIgnoreCase("outside")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString()).matches()) {
                input_email.setError("Please enter valid data");
                input_email.requestFocus();
                return false;
            } else if (!ImageUtils.commonRegex(input_comp.getText().toString(), 150, "0-9 .&")) {
                input_comp.setError("Please enter valid data");
                input_comp.requestFocus();
                return false;
            } else
                return true;

        } else
            return true;
    }

    private Boolean addressValidation() {
        if (input_address.getText().toString().isEmpty()) {
            input_address.setError("Please enter valid data");
            input_address.requestFocus();
            return false;
        } else if (city_name.getText().toString().isEmpty()) {
            city_name.setError("Please enter valid data");
            city_name.requestFocus();
            return false;
        } else if (select_state.getText().toString().isEmpty()) {
            select_state.setError("Please enter valid data");
            select_state.requestFocus();
            return false;
        } else if (pin_code.getText().toString().isEmpty()) {
            pin_code.setError("Please enter valid data");
            pin_code.requestFocus();
            return false;
        } else if (document_id.getText().toString().isEmpty()) {
            document_id.setError("Please enter valid data");
            document_id.requestFocus();
            return false;
        } else if (documentfrontimage.getDrawable() == null) {
            TextView documentfront = (TextView) findViewById(R.id.documentfront);
            documentfront.setError("Please enter valid data");
            documentfront.requestFocus();
            return false;
        } else if (documentbackimage.getDrawable() == null) {
            TextView documentfront = (TextView) findViewById(R.id.documentback);
            documentfront.setError("Please enter valid data");
            documentfront.requestFocus();
            return false;
        } else
            return true;
    }

    private Boolean buisnessValidation() {
        if (persons.equalsIgnoreCase("outside")) {
            if (!pan_no.getText().toString().matches("^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$")) {
                pan_no.setError("Please enter valid data");
                pan_no.requestFocus();
                return false;
            } else if (panphoto.getDrawable() == null) {
                TextView pan_photo = (TextView) findViewById(R.id.pan_photo);
                pan_photo.setError("Please enter valid data");
                pan_photo.requestFocus();
                return false;
            } else if (shopPhoto.getDrawable() == null) {
                TextView shop_photo = (TextView) findViewById(R.id.shop_photo);
                shop_photo.setError("Please enter valid data");
                shop_photo.requestFocus();
                return false;
            } else
                return true;
        } else if (persons.equalsIgnoreCase("internal")) {
            if (pan_no.getText().length() != 0) {
                if (!pan_no.getText().toString().matches("^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$")) {
                    pan_no.setError("Please enter valid data");
                    pan_no.requestFocus();
                    return false;
                } else if (pan_no.getText().toString().matches("^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$")) {
                    if (panphoto.getDrawable() == null) {
                        TextView pan_photo = (TextView) findViewById(R.id.pan_photo);
                        pan_photo.setError("Please enter valid data");
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
                view.setImageBitmap(thumbnail);
                view.setVisibility(View.VISIBLE);
                kycMapImage.put(imageType, getBase64(thumbnail));
                textView.setError(null);
            } catch (Exception e) {
            }
        }
    }

    private String getBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
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
        String path = "";
        try {
            String insertSQL = "INSERT INTO " + RapipayDB.TABLE_KYC_PERSONAL + "\n" +
                    "(" + RapipayDB.MOBILENO + "," + RapipayDB.USER_NAME + "," + RapipayDB.DOB + "," + RapipayDB.EMAILID + "," + RapipayDB.COMPANY_NAME + "," + RapipayDB.IMAGE_NAME + ","
                    + RapipayDB.PASSPORT_PHOTO + "," + RapipayDB.PERSONAL_CLICKED + "," + RapipayDB.DOCUMENTTYPE + "," + RapipayDB.DOCUMENTID + ")\n" +
                    "VALUES \n" +
                    "( ?, ?, ?,?,?, ?, ?,?,?,?);";

            String imageName = "passportPhoto_" + mapValue.getString("mobileNo") + ".jpg";
            if (mapPhoto.has("passportPhoto"))
                path = saveToInternalStorage(base64Convert(mapPhoto.getString("passportPhoto")), imageName);
            dba.execSQL(insertSQL, new String[]{mapValue.getString("mobileNo"), name, mapValue.getString("dob"), mapValue.getString("email"), mapValue.getString("companyName"), imageName, path, "true", documentType, documentID});
            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertAddress(JSONObject mapValue, JSONObject mapPhoto, String documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
        try {
            String insertSQL = "INSERT INTO " + RapipayDB.TABLE_KYC_ADDRESS + "\n" +
                    "(" + RapipayDB.MOBILENO + "," + RapipayDB.ADDRESS + "," + RapipayDB.CITY + "," + RapipayDB.STATE + "," + RapipayDB.PINCODE + "," + RapipayDB.DOCUMENTFRONT_IMAGENAME + "," + RapipayDB.DOCUMENTBACK_IMAGENAME + ","
                    + RapipayDB.DOCUMENTFRONT_PHOTO + "," + RapipayDB.DOCUMENTBACK_PHOTO + "," + RapipayDB.ADDRESS_CLICKED + "," + RapipayDB.DOCUMENTTYPE + "," + RapipayDB.DOCUMENTID + ")\n" +
                    "VALUES \n" +
                    "(?,?,?,?,?,?,?,?,?,?,?,?);";

            String frontimageName = "frontPhoto_" + mobileNo + ".jpg";
            String backimageName = "backPhoto_" + mobileNo + ".jpg";
            String pathFront = saveToInternalStorage(base64Convert(mapPhoto.getString("uploadFront")), frontimageName);
            String pathBack = saveToInternalStorage(base64Convert(mapPhoto.getString("uploadBack")), backimageName);
            dba.execSQL(insertSQL, new String[]{mobileNo, mapValue.getString("address"), mapValue.getString("city"), mapValue.getString("state"), mapValue.getString("pinCode"), frontimageName, backimageName, pathFront, pathBack, "true", documentType, documentID});
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
        String pathPan = "", pathShop = "";
        try {
            String insertSQL = "INSERT INTO " + RapipayDB.TABLE_KYC_BUISNESS + "\n" +
                    "(" + RapipayDB.MOBILENO + "," + RapipayDB.PANNUMBER + "," + RapipayDB.GSTINNUMBER + "," + RapipayDB.PAN_PHOTO_IMAGENAME + "," + RapipayDB.SHOP_PHOTO_IMAGENAME + ","
                    + RapipayDB.PAN_PHOTO + "," + RapipayDB.SHOP_PHOTO + "," + RapipayDB.BUISNESS_CLICKED + "," + RapipayDB.DOCUMENTTYPE + "," + RapipayDB.DOCUMENTID + ")\n" +
                    "VALUES \n" +
                    "(?,?,?,?,?,?,?,?,?,?);";

            String panimageName = "panPhoto_" + mobileNo + ".jpg";
            String shopimageName = "shopPhoto_" + mobileNo + ".jpg";
            if (mapPhoto.has("pancardImg"))
                pathPan = saveToInternalStorage(base64Convert(mapPhoto.getString("pancardImg")), panimageName);
            if (mapPhoto.has("shopPhoto"))
                pathShop = saveToInternalStorage(base64Convert(mapPhoto.getString("shopPhoto")), shopimageName);
            dba.execSQL(insertSQL, new String[]{mobileNo, mapValue.getString("panNo"), mapValue.getString("gstIN"), panimageName, shopimageName, pathPan, pathShop, "true", documentType, documentID});
            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertVerify(JSONObject mapValue, JSONObject mapPhoto, String documentID, String documentType, String customerType) {
        customProgessDialog = new CustomProgessDialog(KYCFormActivity.this);
        SQLiteDatabase dba = db.getWritableDatabase();
        String pathSelf = "", pathSign = "";
        try {
            String insertSQL = "INSERT INTO " + RapipayDB.TABLE_KYC_VERIFICATION + "\n" +
                    "(" + RapipayDB.MOBILENO + "," + RapipayDB.SELF_PHOTO_IMAGENAME + "," + RapipayDB.SIGN_PHOTO_IMAGENAME + "," + RapipayDB.SELF_PHOTO + "," + RapipayDB.SIGN_PHOTO +
                    "," + RapipayDB.VERIFY_CLICKED + "," + RapipayDB.DOCUMENTTYPE + "," + RapipayDB.DOCUMENTID + ")\n" +
                    "VALUES \n" +
                    "(?,?,?,?,?,?,?,?);";

            String selfimageName = "selfPhoto_" + mobileNo + ".jpg";
            String signimageName = "signPhoto_" + mobileNo + ".jpg";
            if (mapPhoto.has("selfPhoto"))
                pathSelf = saveToInternalStorage(base64Convert(mapPhoto.getString("selfPhoto")), selfimageName);
            if (mapPhoto.has("signPhoto"))
                pathSign = saveToInternalStorage(base64Convert(mapPhoto.getString("signPhoto")), signimageName);
            dba.execSQL(insertSQL, new String[]{mobileNo, selfimageName, signimageName, pathSelf, pathSign, "true", documentType, documentID});
            customProgessDialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
