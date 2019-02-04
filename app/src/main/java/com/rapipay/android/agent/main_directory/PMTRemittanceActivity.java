package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.NepalCityPozo;
import com.rapipay.android.agent.Model.NepalDistrictPozo;
import com.rapipay.android.agent.Model.PMTBenefPozo;
import com.rapipay.android.agent.Model.PMTTransactionHistory;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.NepalCityAdapter;
import com.rapipay.android.agent.adapter.NepalDistrictAdapter;
import com.rapipay.android.agent.adapter.PMTBenefAdapter;
import com.rapipay.android.agent.adapter.PMTTransAdapter;
import com.rapipay.android.agent.adapter.PaymentAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.MovableFloatingActionButton;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.grantland.widget.AutofitTextView;

public class PMTRemittanceActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {
    EditText incomesource, gender, documentid, input_mobile, documentype, city, district, address_name, sendernation, sendercomname, input_name, state_update;
    ImageView btn_search;
    LinearLayout sender_layout, beneficiary_layout, last_tran_layout;
    AutofitTextView image;
    TextView select_state;
    RecyclerView beneficiary_details, trans_details;
    ArrayList<PMTBenefPozo> beneficiaryDetailsPozoslist;
    ArrayList<PMTTransactionHistory> pmtTransactionHistoryArrayList;
    PMTBenefPozo pozo;
    PMTTransactionHistory pmtPozo;
    boolean otp_flag = false, bene_update = false;
    String otpRefId;
    private String filePath = "", imageBase64 = "";
    private static final int CAMERA_REQUEST = 1888, SELECT_PDF_DIALOG = 2999;
    private int SELECT_FILE = 1889;
    private TextInputLayout state_update_top, gender_layout, document_layout;

    Spinner bank_district, bank_city, account_type, gender_spinner, spinner_docType;
    private ArrayList<NepalDistrictPozo> nepalDistrictPozoArrayList = null;
    private ArrayList<NepalCityPozo> nepalCityPozoArrayList = null;
    protected String paymode = "", nepalBank = "", nepalDistrict = "", branchID = "", accountType = "";
    TextView input_amountss, bene_relation, bene_number, bene_name, bene_city, bene_address, accountno, confirmAccountNo;
    AppCompatButton add_bank_details;
    NepalCityAdapter nepalCityAdapter = null;
    private Uri imageUri;
    String amount = "", selectGender = "", docType = "", imgType = "";
    MovableFloatingActionButton fab;
    boolean isFabClick = false;
    String[] items = new String[]{"Select Document Type", "Aadhar Card", "Voter Id Card", "Driving License", "Passport"};
    ArrayAdapter<String> adapter_doc;
    ArrayList<PaymentModePozo> list_gender = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pmt_resistance_layout);
        initialize();
    }

    private void initialize() {
        fab = (MovableFloatingActionButton) findViewById(R.id.fab);
        reset = (ImageView) findViewById(R.id.reset);
        delete_all = (ImageView) findViewById(R.id.delete_all);
        delete_all.setImageDrawable(getResources().getDrawable(R.drawable.delete_all));
        reset.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        heading = (TextView) findViewById(R.id.toolbar_title);
        if (balance != null)
            heading.setText("INDO NEPAL (Balance : Rs." + balance + ")");
        else
            heading.setText("INDO NEPAL");
        state_update = (EditText) findViewById(R.id.state_update);
        state_update_top = (TextInputLayout) findViewById(R.id.state_update_top);
        incomesource = (EditText) findViewById(R.id.incomesource);
        documentid = (EditText) findViewById(R.id.documentid);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        documentype = (EditText) findViewById(R.id.documentype);
        gender = (EditText) findViewById(R.id.gender);
        gender_layout = (TextInputLayout) findViewById(R.id.gender_layout);
        gender_layout.setVisibility(View.GONE);
        document_layout = (TextInputLayout) findViewById(R.id.document_layout);
        document_layout.setVisibility(View.GONE);
        gender_spinner = (Spinner) findViewById(R.id.gender_spinner);
        list_gender = new ArrayList<PaymentModePozo>();
        list_gender.add(new PaymentModePozo("0", "Select Gender"));
        list_gender.add(new PaymentModePozo("0", "MALE"));
        list_gender.add(new PaymentModePozo("0", "FEMALE"));
        list_gender.add(new PaymentModePozo("0", "OTHERS"));
        if (list_gender.size() != 0)
            gender_spinner.setAdapter(new PaymentAdapter(PMTRemittanceActivity.this, list_gender));
        btn_search = (ImageView) findViewById(R.id.btn_search);
        city = (EditText) findViewById(R.id.city);
        district = (EditText) findViewById(R.id.district);
        address_name = (EditText) findViewById(R.id.address_name);
        sendernation = (EditText) findViewById(R.id.sendernation);
        sendercomname = (EditText) findViewById(R.id.sendercomname);
        input_name = (EditText) findViewById(R.id.input_name);
        select_state = (TextView) findViewById(R.id.select_state);
        image = (AutofitTextView) findViewById(R.id.images);
        image.setOnClickListener(this);
        date1_text = (AutofitTextView) findViewById(R.id.date);
        date1_text.setHint("Date Of Birth");
        date1_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });
        sender_layout = (LinearLayout) findViewById(R.id.sender_layout);
        beneficiary_layout = (LinearLayout) findViewById(R.id.beneficiary_layout);
        last_tran_layout = (LinearLayout) findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
        beneficiary_details = (RecyclerView) findViewById(R.id.beneficiary_details);
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(this, trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                pmtPozo = pmtTransactionHistoryArrayList.get(position);
                customDialog_Doc("PMTDOC", pmtPozo, "Upload Document to verify Transaction");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        beneficiary_details.addOnItemTouchListener(new RecyclerTouchListener(this, beneficiary_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                pozo = beneficiaryDetailsPozoslist.get(position);
                customDialog_Common("FUNDTRANSFER", pozo, "Select Payment Mode");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        input_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10)
                    new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getSender_Validate().toString(), headerData, PMTRemittanceActivity.this).execute();
                else
                    reset();
            }
        });
        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    selectGender = list_gender.get(position).getPaymentMode();
                else
                    selectGender = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_docType = findViewById(R.id.docs_type);
        adapter_doc = new ArrayAdapter<String>(PMTRemittanceActivity.this,
                android.R.layout.simple_spinner_item, items);
        adapter_doc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_docType.setAdapter(adapter_doc);
        spinner_docType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                documentid.setText("");
                if (position != 0)
                    docType = items[position];
                else
                    docType = "";
                if (position == 1) {
                    documentid.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.LengthFilter(12);
                    documentid.setFilters(filterArray);
                } else if (position == 2) {
                    documentid.setInputType(InputType.TYPE_CLASS_TEXT);
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.LengthFilter(10);
                    documentid.setFilters(filterArray);
                } else {
                    documentid.setInputType(InputType.TYPE_CLASS_TEXT);
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.LengthFilter(20);
                    documentid.setFilters(filterArray);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void reset() {
        clear();
        sender_layout.setVisibility(View.GONE);
        beneficiary_layout.setVisibility(View.GONE);
        last_tran_layout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
    }

    public JSONObject getServiceFee(String amount, String mode) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "GET_SERVICE_FEE");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("agentID", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("subType", "Money_Transfer_pmt");
                jsonObject.put("txnAmmount", amount);
                jsonObject.put("paymentMode", mode);
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject addBene(String number, String name, String city, String address, String relation, String beneType, String branchID, String accountNo, String confirm, String beneID, String accountType) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "ADD_BENEFICIARY_DETAILS");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("agentID", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("subType", "Money_Transfer_pmt");
                jsonObject.put("senderMobile", input_mobile.getText().toString());
                jsonObject.put("receiverMobile", number);
                jsonObject.put("reciverName", name);
                jsonObject.put("receiverAddress", address);
                jsonObject.put("receiverCity", city);
                jsonObject.put("relationWithSender", relation);
                jsonObject.put("sendCountry", "INDIA");
                jsonObject.put("payoutCountry", "NEPAL");
                jsonObject.put("branchId", branchID);
                jsonObject.put("accountNo", accountNo);
                jsonObject.put("confirmAccountNo", confirm);
                jsonObject.put("accountType", accountType);
                jsonObject.put("beneId", beneID);
                jsonObject.put("isInsertUpdateFlag", beneType);
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject getMoneyTransferPMT(String amount, String mode, PMTBenefPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "MONEY_TRANSFER_PMT");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("transferAmt", amount);
                jsonObject.put("paymentMode", mode);
                jsonObject.put("beneficiaryId", pozo.getPmt_Bene_Id());
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject getOtpPMT(String mode) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "VERIFY_SENDER_OTP");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("senderMobile", input_mobile.getText().toString());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("otpRefID", otpRefId);
                jsonObject.put("otp", mode);
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    private JSONObject getSender_Validate() {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "SENDER_COMPLETE_DETAILS");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    private JSONObject getNepalDistrict(String bankCode) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "GET_BANK_DISTRICT");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("agentMobile", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("bankCode", bankCode);
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    private JSONObject getNepalCity(String bankCode, String districtCode) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "GET_BRANCH_CITY");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("agentMobile", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("bankCode", bankCode);
                jsonObject.put("districCode", districtCode);
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    private JSONObject addSenderDetails() {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "ADD_SENDER_DETAILS");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("senderName", input_name.getText().toString());
                jsonObject.put("senderGender", selectGender);
                jsonObject.put("senderDoB", date1_text.getText().toString());
                jsonObject.put("senderAddress", address_name.getText().toString());
                jsonObject.put("employer", sendercomname.getText().toString());
                jsonObject.put("senderNationality", sendernation.getText().toString());
                jsonObject.put("senderIDType", docType);
                jsonObject.put("senderIDNumber", documentid.getText().toString());
                jsonObject.put("senderCity", city.getText().toString());
                jsonObject.put("senderDistrict", district.getText().toString());
                jsonObject.put("senderState", select_state.getText().toString());
                jsonObject.put("incomeSource", incomesource.getText().toString());
                jsonObject.put("senderPhone", input_mobile.getText().toString());
                jsonObject.put("consumedLimit", "");
                jsonObject.put("imgType", imgType);
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
                jsonObject.put("senderImgDoc", imageBase64);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    private JSONObject docUpload(String userTxnId) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            try {
                jsonObject.put("serviceType", "UPLOAD_SIGNED_TXN_RECEIPT");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("userTxnId", userTxnId);
                jsonObject.put("reqFor", "BC3");
                jsonObject.put("documentType", "other");
                jsonObject.put("imgType", imgType);
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
                jsonObject.put("base64Img", imageBase64);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            reset.setVisibility(View.VISIBLE);
            if (object.has("apiCommonResposne")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                if (object1 != null) {
                    String balance = object1.getString("runningBalance");
                    heading.setText("INDO NEPAL (Balance : Rs." + format(balance) + ")");
                }
            }
            if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                if (object.getString("responseCode").equalsIgnoreCase("86001") || object.getString("responseCode").equalsIgnoreCase("86004")) {
                    sender_layout.setVisibility(View.VISIBLE);
                    otp_flag = true;
                } else if (object.getString("responseCode").equalsIgnoreCase("86002")) {
                    sender_layout.setVisibility(View.VISIBLE);
                } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    sender_layout.setVisibility(View.GONE);
                    if (object.has("senderDetails")) {
                        JSONArray array = object.getJSONArray("senderDetails");
                        JSONObject jsonObject = array.getJSONObject(0);
                        enterSenderDetails(jsonObject);
                        delete_all.setVisibility(View.VISIBLE);
                    }
                    if (object.has("beneListDetail")) {
                        if (Integer.parseInt(object.getString("beneCount")) > 0) {
                            beneficiary_layout.setVisibility(View.VISIBLE);
                            insertBenfDetails(object.getJSONArray("beneListDetail"));
                        }
                    }
                    if (object.has("getTxnHistory")) {
                        if (Integer.parseInt(object.getString("historyCount")) > 0) {
                            last_tran_layout.setVisibility(View.VISIBLE);
                            insertTransDetails(object.getJSONArray("getTxnHistory"));
                        }
                    }
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("GET_SERVICE_FEE")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    customDialog_Common("Fund Transfer Confirmation", object, pozo, "Sure you want to Transfer?", input_mobile.getText().toString(), input_name.getText().toString(), PMTRemittanceActivity.this);
                    bene_update = false;
                    amount = "";
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("MONEY_TRANSFER_PMT")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    if (object.has("getTxnReceiptDataList"))
                        customReceiptNewTransaction("INDO-NEPAL BILL PAYMENT", object, PMTRemittanceActivity.this);
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    otpRefId = object.getString("otpRefId");
                    if (otp_flag)
                        customDialog_Common("OTPLAYOUT", null, null, "Add Sender Details", null, null, PMTRemittanceActivity.this);
                    else {
                        new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getSender_Validate().toString(), headerData, PMTRemittanceActivity.this).execute();
                        delete_all.setVisibility(View.VISIBLE);
                        sender_layout.setVisibility(View.GONE);
                    }
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("VERIFY_SENDER_OTP")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    alertDialog.dismiss();
                    new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getSender_Validate().toString(), headerData, PMTRemittanceActivity.this).execute();
                    sender_layout.setVisibility(View.GONE);
                    delete_all.setVisibility(View.VISIBLE);
                } else if (object.getString("responseCode").equalsIgnoreCase("60236")) {
                    otpView.setText("");
                    otpView.setError("Please enter correct otp");
                    otpView.requestFocus();
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("ADD_BENEFICIARY_DETAILS")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    branchID = "";
                    nepalBank = "";
                    nepalDistrict = "";
                    if (bene_update)
                        new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getServiceFee(amount, paymode).toString(), headerData, PMTRemittanceActivity.this).execute();
                    else
                        customDialog_Common("KYCLAYOUTS", null, null, null, null, object.getString("responseMessage"), PMTRemittanceActivity.this);
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("GET_BANK_DISTRICT")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    if (object.has("pmtBankResDtoList")) {
                        JSONArray array = object.getJSONArray("pmtBankResDtoList");
                        getDistrict(array);
                    }
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("GET_BRANCH_CITY")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    if (object.has("pmtBankResDtoList")) {
                        JSONArray array = object.getJSONArray("pmtBankResDtoList");
                        getCity(array);
                    }
                }
            } else if (object.getString("serviceType").equalsIgnoreCase("UPLOAD_SIGNED_TXN_RECEIPT")) {
                if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    customDialog_Common("KYCLAYOUTS", null, null, "INDO-NEPAL Remittance", "", object.getString("responseMessage"), PMTRemittanceActivity.this);
                }
            }
            hideKeyboard(PMTRemittanceActivity.this);
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }

    private void getDistrict(JSONArray array) {
        nepalDistrictPozoArrayList = new ArrayList<>();
        nepalDistrictPozoArrayList.add(new NepalDistrictPozo("Select District", "", "0", ""));
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                nepalDistrictPozoArrayList.add(new NepalDistrictPozo(jsonObject.getString("districtName"), jsonObject.getString("districCode"), jsonObject.getString("bankCode"), jsonObject.getString("bankName")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nepalDistrictPozoArrayList.size() != 0)
            bank_district.setAdapter(new NepalDistrictAdapter(PMTRemittanceActivity.this, nepalDistrictPozoArrayList));
    }

    private void getCity(JSONArray array) {
        nepalCityPozoArrayList = new ArrayList<>();
        nepalCityPozoArrayList.add(new NepalCityPozo("", "Select City", "", "0", ""));
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                nepalCityPozoArrayList.add(new NepalCityPozo(jsonObject.getString("branchName"), jsonObject.getString("branchAddress"), jsonObject.getString("branchCode"), jsonObject.getString("branchId"), jsonObject.getString("branchCity")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nepalCityPozoArrayList.size() != 0) {
            nepalCityAdapter = new NepalCityAdapter(PMTRemittanceActivity.this, nepalCityPozoArrayList);
            bank_city.setAdapter(nepalCityAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (!isFabClick) {
                    sender_layout.setVisibility(View.VISIBLE);
                    isFabClick = true;
                } else if (isFabClick) {
                    sender_layout.setVisibility(View.GONE);
                    isFabClick = false;
                }
                break;
            case R.id.reset:
                reset();
                input_mobile.setText("");
                break;
            case R.id.delete_all:
                addBeneDetails("FUNDTRANSFER", "Add Beneficiary Detail");
                break;
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_search:
                hideKeyboard(PMTRemittanceActivity.this);
                loadIMEI();
                break;
            case R.id.images:
                loadCamera();
                break;
            case R.id.select_state:
                ArrayList<String> list_state = db.getState_Details();
                customSpinner((TextView) findViewById(R.id.select_state), "Select State", list_state);
                break;
            case R.id.btn_submit:
                if (input_name.getText().toString().isEmpty()) {
                    input_name.setError("Please enter valid name");
                    input_name.requestFocus();
                } else if (sendercomname.getText().toString().isEmpty()) {
                    sendercomname.setError("Please enter valid company name");
                    sendercomname.requestFocus();
                } else if (sendernation.getText().toString().isEmpty()) {
                    sendernation.setError("Please enter valid nationality");
                    sendernation.requestFocus();
                } else if (address_name.getText().toString().isEmpty()) {
                    address_name.setError("Please enter valid address");
                    address_name.requestFocus();
                } else if (district.getText().toString().isEmpty()) {
                    district.setError("Please enter valid district");
                    district.requestFocus();
                } else if (city.getText().toString().isEmpty()) {
                    city.setError("Please enter valid city");
                    city.requestFocus();
                } else if (docType.equalsIgnoreCase("")) {
                    Toast.makeText(PMTRemittanceActivity.this, "Please Select document type", Toast.LENGTH_SHORT).show();
                } else if (documentid.getText().toString().isEmpty()) {
                    documentid.setError("Please enter valid document id");
                    documentid.requestFocus();
                } else if (select_state.getText().toString().equalsIgnoreCase("Select State")) {
                    select_state.setError("Please enter valid state");
                    select_state.requestFocus();
                } else if (incomesource.getText().toString().isEmpty()) {
                    incomesource.setError("Please enter valid income source");
                    incomesource.requestFocus();
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter valid date");
                    date1_text.requestFocus();
                } else if (image.getText().toString().isEmpty() || imageBase64 == null || imgType.equalsIgnoreCase("")) {
                    image.setError("Please Select document");
                    image.requestFocus();
                } else if (selectGender.equalsIgnoreCase("")) {
                    Toast.makeText(PMTRemittanceActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                } else {
                    new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, addSenderDetails().toString(), headerData, PMTRemittanceActivity.this).execute();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    private void insertBenfDetails(JSONArray array) {
        beneficiaryDetailsPozoslist = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("pmt_Bene_Id").isEmpty() || !object.getString("pmt_Bene_Id").equalsIgnoreCase("null"))
                    beneficiaryDetailsPozoslist.add(new PMTBenefPozo(object.getString("receiver_Mobile"), object.getString("receiver_Details"), object.getString("relation_With_Sender"), object.getString("account_Number"), object.getString("pmt_Bene_Id"), object.getString("bank_Details")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (beneficiaryDetailsPozoslist.size() != 0)
            initializeBenAdapter(beneficiaryDetailsPozoslist);
    }

    private void insertTransDetails(JSONArray array) {
        pmtTransactionHistoryArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                pmtTransactionHistoryArrayList.add(new PMTTransactionHistory(object.getString("txnDateTime"), object.getString("senderName"), object.getString("mobileNo"), object.getString("bankName"), object.getString("beneficiaryName"), object.getString("accountNo"), object.getString("requestAmt"), object.getString("serviceProviderTXNID"), object.getString("userTxnId"), object.getString("txnStatus"), object.getString("transferType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pmtTransactionHistoryArrayList.size() != 0)
            initializeTransAdapter(pmtTransactionHistoryArrayList);
    }

    private void initializeTransAdapter(ArrayList<PMTTransactionHistory> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new PMTTransAdapter(this, list));
    }

    private void initializeBenAdapter(ArrayList<PMTBenefPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        beneficiary_details.setLayoutManager(layoutManager);
        beneficiary_details.setAdapter(new PMTBenefAdapter(this, list));
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
            new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getMoneyTransferPMT(input_amountss.getText().toString(), paymode, pozo).toString(), headerData, PMTRemittanceActivity.this).execute();
        } else if (type.equalsIgnoreCase("OTPLAYOUT")) {
            if (!otpView.getText().toString().isEmpty()) {
                new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getOtpPMT(otpView.getText().toString()).toString(), headerData, PMTRemittanceActivity.this).execute();
                otp_flag = false;
            }
        } else if (type.equalsIgnoreCase("KYCLAYOUTS") || type.equalsIgnoreCase("INDO-NEPAL BILL PAYMENT")) {
            new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getSender_Validate().toString(), headerData, PMTRemittanceActivity.this).execute();
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("OTPLAYOUT")) {
            otp_flag = false;
            input_mobile.setText("");
            clear();
        }
    }

    protected void customDialog_Doc(final String type, final PMTTransactionHistory ob, String msg) {
        imageBase64 = null;
        imgType = "";
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        try {
            if (type.equalsIgnoreCase("PMTDOC")) {
                alertLayout.findViewById(R.id.pmt_doc).setVisibility(View.VISIBLE);
                image = (AutofitTextView) alertLayout.findViewById(R.id.images_doc);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadCamera();
                    }
                });
                btn_ok.setText("Upload Document");
                dialog.setView(alertLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image.getText().toString().isEmpty() || imageBase64 == null || imgType.equalsIgnoreCase("")) {
                    image.setError("Please Select document");
                    image.requestFocus();
                } else {
                    new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, docUpload(ob.getUserTxnId()).toString(), headerData, PMTRemittanceActivity.this).execute();
                    newdialog.dismiss();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newdialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        newdialog = dialog.show();
    }

    protected void customDialog_Common(final String type, final PMTBenefPozo ob, String msg) {
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        try {
            if (type.equalsIgnoreCase("FUNDTRANSFER")) {
                alertLayout.findViewById(R.id.fundtransfer).setVisibility(View.VISIBLE);
                Spinner bank_selectss = (Spinner) alertLayout.findViewById(R.id.bank_selectss);
                input_amountss = (TextView) alertLayout.findViewById(R.id.input_amountss);
                accountno = (TextView) alertLayout.findViewById(R.id.accountnos);
                confirmAccountNo = (TextView) alertLayout.findViewById(R.id.confirmaccountnos);
                final ArrayList<PaymentModePozo> list_payment = db.getPaymentModeNepal();
                if (list_payment.size() != 0)
                    bank_selectss.setAdapter(new PaymentAdapter(PMTRemittanceActivity.this, list_payment));
                bank_selectss.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            paymode = list_payment.get(position).getTypeID();
                            if (paymode.equalsIgnoreCase("PMTAD") && (ob.getAccount_Number().equalsIgnoreCase("null") || ob.getAccount_Number().equalsIgnoreCase("")) && (ob.getBank_Details().equalsIgnoreCase("null") || ob.getBank_Details().equalsIgnoreCase("")))
                                alertLayout.findViewById(R.id.accountlayout).setVisibility(View.VISIBLE);
                            else
                                alertLayout.findViewById(R.id.accountlayout).setVisibility(View.GONE);
                        } else
                            paymode = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                Spinner bank_name = (Spinner) alertLayout.findViewById(R.id.bank_names);
                bank_district = (Spinner) alertLayout.findViewById(R.id.bank_districts);
                bank_city = (Spinner) alertLayout.findViewById(R.id.bank_citys);
                account_type = (Spinner) alertLayout.findViewById(R.id.account_types);
                final ArrayList<PaymentModePozo> list_bank = db.getBankNepal();
                if (list_bank.size() != 0)
                    bank_name.setAdapter(new PaymentAdapter(PMTRemittanceActivity.this, list_bank));
                final ArrayList<PaymentModePozo> list_account_type = new ArrayList<>();
                list_account_type.add(new PaymentModePozo("0", "Select Account Type"));
                list_account_type.add(new PaymentModePozo("1", "SAVING"));
                list_account_type.add(new PaymentModePozo("2", "CURRENT"));
                if (list_account_type.size() != 0)
                    account_type.setAdapter(new PaymentAdapter(PMTRemittanceActivity.this, list_account_type));
                bank_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            nepalBank = list_bank.get(position).getTypeID();
                            new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getNepalDistrict(nepalBank).toString(), headerData, PMTRemittanceActivity.this).execute();
                            if (nepalCityPozoArrayList != null || nepalCityAdapter != null) {
                                nepalCityPozoArrayList.clear();
                                nepalCityAdapter.notifyDataSetChanged();
                            }
                        } else
                            nepalBank = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                account_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            accountType = list_account_type.get(position).getPaymentMode();
                        } else
                            accountType = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                bank_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            nepalDistrict = nepalDistrictPozoArrayList.get(position).getDistricCode();
                            new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getNepalCity(nepalBank, nepalDistrict).toString(), headerData, PMTRemittanceActivity.this).execute();
                        } else
                            nepalDistrict = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                bank_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            branchID = nepalCityPozoArrayList.get(position).getBranchId();
                        } else
                            branchID = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                dialog.setView(alertLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymode.equalsIgnoreCase("")) {
                    Toast.makeText(PMTRemittanceActivity.this, "Please select payment mode", Toast.LENGTH_SHORT).show();
                } else if (input_amountss.getText().toString().isEmpty()) {
                    input_amountss.setError("Please enter valid data");
                    input_amountss.requestFocus();
                } else if (!paymode.equalsIgnoreCase("PMTAD")) {
                    new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getServiceFee(input_amountss.getText().toString(), paymode).toString(), headerData, PMTRemittanceActivity.this).execute();
                    alertDialog.dismiss();
                } else if (paymode.equalsIgnoreCase("PMTAD") && !(ob.getAccount_Number().equalsIgnoreCase("null") || ob.getAccount_Number().equalsIgnoreCase("")) && !(ob.getBank_Details().equalsIgnoreCase("null") || ob.getBank_Details().equalsIgnoreCase(""))) {
                    new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getServiceFee(input_amountss.getText().toString(), paymode).toString(), headerData, PMTRemittanceActivity.this).execute();
                    alertDialog.dismiss();
                } else {
                    if (nepalBank.equalsIgnoreCase(""))
                        Toast.makeText(PMTRemittanceActivity.this, "Please select bank", Toast.LENGTH_SHORT).show();
                    else if (nepalDistrict.equalsIgnoreCase(""))
                        Toast.makeText(PMTRemittanceActivity.this, "Please select district", Toast.LENGTH_SHORT).show();
                    else if (branchID.equalsIgnoreCase(""))
                        Toast.makeText(PMTRemittanceActivity.this, "Please select city", Toast.LENGTH_SHORT).show();
                    else if (!ImageUtils.commonAccount(accountno.getText().toString(), 8, 30)) {
                        accountno.setError("Please enter account number");
                        accountno.requestFocus();
                    } else if (!ImageUtils.commonAccount(confirmAccountNo.getText().toString(), 8, 30)) {
                        confirmAccountNo.setError("Please confirm account number");
                        confirmAccountNo.requestFocus();
                    } else if (accountType.equalsIgnoreCase(""))
                        Toast.makeText(PMTRemittanceActivity.this, "Please Select Account Type", Toast.LENGTH_SHORT).show();
                    else if (accountno.getText().toString().equalsIgnoreCase(confirmAccountNo.getText().toString())) {
                        String split[] = ob.getReceiver_Details().split(",");
                        new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, addBene(ob.getReceiver_Mobile(), split[0], split[1], split[2], ob.getRelation_With_Sender(), "U", branchID, accountno.getText().toString(), confirmAccountNo.getText().toString(), ob.getPmt_Bene_Id(), accountType).toString(), headerData, PMTRemittanceActivity.this).execute();
                        bene_update = true;
                        amount = input_amountss.getText().toString();
                        alertDialog.dismiss();
                    } else
                        Toast.makeText(PMTRemittanceActivity.this, "Account number not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }

    protected void addBeneDetails(final String type, String msg) {
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        try {
            if (type.equalsIgnoreCase("FUNDTRANSFER")) {
                alertLayout.findViewById(R.id.add_bene_details).setVisibility(View.VISIBLE);
                bene_number = (TextView) alertLayout.findViewById(R.id.bene_number);
                bene_name = (TextView) alertLayout.findViewById(R.id.bene_name);
                bene_city = (TextView) alertLayout.findViewById(R.id.bene_city);
                bene_address = (TextView) alertLayout.findViewById(R.id.bene_address);
                bene_relation = (TextView) alertLayout.findViewById(R.id.bene_relation);
                accountno = (TextView) alertLayout.findViewById(R.id.accountno);
                confirmAccountNo = (TextView) alertLayout.findViewById(R.id.confirmaccountno);
                add_bank_details = (AppCompatButton) alertLayout.findViewById(R.id.add_bank_details);
                add_bank_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add_bank_details.setVisibility(View.GONE);
                        alertLayout.findViewById(R.id.accountlayouted).setVisibility(View.VISIBLE);
                    }
                });
                Spinner bank_name = (Spinner) alertLayout.findViewById(R.id.bank_name);
                bank_district = (Spinner) alertLayout.findViewById(R.id.bank_district);
                bank_city = (Spinner) alertLayout.findViewById(R.id.bank_city);
                account_type = (Spinner) alertLayout.findViewById(R.id.account_type);
                final ArrayList<PaymentModePozo> list_payment = db.getBankNepal();
                if (list_payment.size() != 0)
                    bank_name.setAdapter(new PaymentAdapter(PMTRemittanceActivity.this, list_payment));
                final ArrayList<PaymentModePozo> list_account_type = new ArrayList<>();
                list_account_type.add(new PaymentModePozo("0", "Select Account Type"));
                list_account_type.add(new PaymentModePozo("1", "SAVING"));
                list_account_type.add(new PaymentModePozo("2", "CURRENT"));
                if (list_account_type.size() != 0)
                    account_type.setAdapter(new PaymentAdapter(PMTRemittanceActivity.this, list_account_type));
                bank_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            nepalBank = list_payment.get(position).getTypeID();
                            new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getNepalDistrict(nepalBank).toString(), headerData, PMTRemittanceActivity.this).execute();
                            if (nepalCityPozoArrayList != null || nepalCityAdapter != null) {
                                nepalCityPozoArrayList.clear();
                                nepalCityAdapter.notifyDataSetChanged();
                            }
                        } else
                            nepalBank = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                account_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            accountType = list_account_type.get(position).getPaymentMode();
                        } else
                            accountType = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                bank_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            nepalDistrict = nepalDistrictPozoArrayList.get(position).getDistricCode();
                            new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, getNepalCity(nepalBank, nepalDistrict).toString(), headerData, PMTRemittanceActivity.this).execute();
                        } else
                            nepalDistrict = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                bank_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            branchID = nepalCityPozoArrayList.get(position).getBranchId();
                        } else
                            branchID = "";
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                dialog.setView(alertLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bene_number.getText().toString().matches("^[2-9]{1}[0-9]{4,9}$")) {
                    bene_number.setError("Please enter valid data");
                    bene_number.requestFocus();
                } else if (bene_name.getText().toString().isEmpty()) {
                    bene_name.setError("Please enter valid data");
                    bene_name.requestFocus();
                } else if (bene_city.getText().toString().isEmpty()) {
                    bene_city.setError("Please enter valid data");
                    bene_city.requestFocus();
                } else if (bene_address.getText().toString().isEmpty()) {
                    bene_address.setError("Please enter valid data");
                    bene_address.requestFocus();
                } else if (bene_relation.getText().toString().isEmpty()) {
                    bene_relation.setError("Please enter valid data");
                    bene_relation.requestFocus();
                } else if (!nepalBank.equalsIgnoreCase("") || !nepalDistrict.equalsIgnoreCase("") || !branchID.equalsIgnoreCase("") || !accountno.getText().toString().isEmpty() || !confirmAccountNo.getText().toString().isEmpty()) {
                    if (nepalBank.equalsIgnoreCase(""))
                        Toast.makeText(PMTRemittanceActivity.this, "Please select bank", Toast.LENGTH_SHORT).show();
                    else if (nepalDistrict.equalsIgnoreCase(""))
                        Toast.makeText(PMTRemittanceActivity.this, "Please select district", Toast.LENGTH_SHORT).show();
                    else if (branchID.equalsIgnoreCase(""))
                        Toast.makeText(PMTRemittanceActivity.this, "Please select city", Toast.LENGTH_SHORT).show();
                    else if (!ImageUtils.commonAccount(accountno.getText().toString(), 8, 30)) {
                        accountno.setError("Please enter account number");
                        accountno.requestFocus();
                    } else if (!ImageUtils.commonAccount(confirmAccountNo.getText().toString(), 8, 30)) {
                        confirmAccountNo.setError("Please confirm account number");
                        confirmAccountNo.requestFocus();
                    } else if (accountType.equalsIgnoreCase(""))
                        Toast.makeText(PMTRemittanceActivity.this, "Please Select Account Type", Toast.LENGTH_SHORT).show();
                    else if (accountno.getText().toString().equalsIgnoreCase(confirmAccountNo.getText().toString())) {
                        new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, addBene(bene_number.getText().toString(), bene_name.getText().toString(), bene_city.getText().toString(), bene_address.getText().toString(), bene_relation.getText().toString(), "I", branchID, accountno.getText().toString(), confirmAccountNo.getText().toString(), "", accountType).toString(), headerData, PMTRemittanceActivity.this).execute();
                        alertDialog.dismiss();
                    } else
                        Toast.makeText(PMTRemittanceActivity.this, "Account number not match", Toast.LENGTH_SHORT).show();
                } else {
                    new AsyncPostMethod(WebConfig.PMTSERVICE_DETAILS, addBene(bene_number.getText().toString(), bene_name.getText().toString(), bene_city.getText().toString(), bene_address.getText().toString(), bene_relation.getText().toString(), "I", branchID, "", "", "", "").toString(), headerData, PMTRemittanceActivity.this).execute();
                    alertDialog.dismiss();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }

    private void enterSenderDetails(JSONObject object) {
        try {
            input_name.setText(object.getString("sender_Name"));
            gender.setText(object.getString("sd_Gender"));
            sendercomname.setText(object.getString("sd_Employer"));
            sendernation.setText(object.getString("sd_Nationality"));
            address_name.setText(object.getString("sd_Address"));
            date1_text.setText(object.getString("sd_Dob"));
            district.setText(object.getString("sd_District"));
            city.setText(object.getString("sd_City"));
            documentype.setText(object.getString("sd_Id_Type"));
            documentid.setText(object.getString("sd_Id_Number"));
            incomesource.setText(object.getString("sd_Income_Source"));
            state_update.setText(object.getString("sender_State_Name"));
            input_name.setEnabled(false);
            gender.setEnabled(false);
            sendercomname.setEnabled(false);
            sendernation.setEnabled(false);
            address_name.setEnabled(false);
            date1_text.setEnabled(false);
            district.setEnabled(false);
            city.setEnabled(false);
            documentype.setEnabled(false);
            documentid.setEnabled(false);
            incomesource.setEnabled(false);
            image.setEnabled(false);
            state_update.setEnabled(false);
            state_update_top.setVisibility(View.VISIBLE);
            select_state.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            findViewById(R.id.btn_submit).setVisibility(View.GONE);
            branchID = "";
            nepalBank = "";
            nepalDistrict = "";
            amount = "";
            fab.setVisibility(View.VISIBLE);
            hideKeyboard(PMTRemittanceActivity.this);
            input_mobile.setEnabled(false);
            gender_layout.setVisibility(View.VISIBLE);
            gender_spinner.setVisibility(View.GONE);
            spinner_docType.setVisibility(View.GONE);
            document_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        input_name.setText("");
        gender.setText("");
        sendercomname.setText("");
        sendernation.setText("");
        address_name.setText("");
        date1_text.setText("");
        date1_text.setHint("Date Of Birth");
        district.setText("");
        city.setText("");
        documentype.setText("");
        documentid.setText("");
        incomesource.setText("");
        image.setText("");
        input_name.setText("");
        input_name.setEnabled(true);
        gender.setEnabled(true);
        sendercomname.setEnabled(true);
        sendernation.setEnabled(true);
        address_name.setEnabled(true);
        date1_text.setEnabled(true);
        district.setEnabled(true);
        city.setEnabled(true);
        documentype.setEnabled(true);
        documentid.setEnabled(true);
        incomesource.setEnabled(true);
        image.setEnabled(true);
        state_update.setEnabled(true);
        state_update_top.setVisibility(View.GONE);
        select_state.setText("");
        select_state.setHint("Select State");
        select_state.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_submit).setVisibility(View.VISIBLE);
        branchID = "";
        nepalBank = "";
        nepalDistrict = "";
        amount = "";
        input_mobile.setEnabled(true);
        gender_layout.setVisibility(View.GONE);
        gender_spinner.setVisibility(View.VISIBLE);
        spinner_docType.setVisibility(View.VISIBLE);
        document_layout.setVisibility(View.GONE);
        spinner_docType.setAdapter(adapter_doc);
        if (list_gender.size() != 0)
            gender_spinner.setAdapter(new PaymentAdapter(PMTRemittanceActivity.this, list_gender));
    }

    private void selectImage() {
        final CharSequence[] items = {"Capture Image", "Choose from Gallery", "Select PDF", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PMTRemittanceActivity.this);
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Image")) {
                    Intent intent = new Intent(PMTRemittanceActivity.this, CameraKitActivity.class);
                    intent.putExtra("ImageType", "pmtImage");
                    intent.putExtra("REQUESTTYPE", CAMERA_REQUEST);
                    startActivityForResult(intent, CAMERA_REQUEST);
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
//                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//                    imageUri = getContentResolver().insert(
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Select PDF")) {
                    Intent intent4 = new Intent(PMTRemittanceActivity.this, NormalFilePickActivity.class);
                    intent4.putExtra(Constant.MAX_NUMBER, 1);
                    intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
                    startActivityForResult(intent4, SELECT_PDF_DIALOG);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private Bitmap addWaterMark(Bitmap src) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
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

    private void setPic(Bitmap mCurrentPhotoPath) {
//        int targetW = image.getWidth();
//        int targetH = image.getHeight();
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageBase64 = getBytesFromBitmap(addWaterMark(mCurrentPhotoPath));
        imgType = "jpg";
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageBase64 = "";
        image.setText("");
        if (resultCode == CAMERA_REQUEST) {
            try {
                String path = data.getStringExtra("ImagePath");
                String imageType = data.getStringExtra("ImageType");
                Bitmap bitmap = loadImageFromStorage(imageType, path);
                setPic(bitmap);
                image.setText(imageType + ".jpg");
                image.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == SELECT_FILE) {
            Uri uri = data.getData();
            Bitmap thumbnail = null;
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 2;
                options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
                thumbnail = BitmapFactory.decodeStream(is, null, options);
                imageBase64 = getBytesFromBitmap(addWaterMark(thumbnail));
                imgType = "jpg";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Uri selectedImageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};
            CursorLoader cursorLoader = new CursorLoader(PMTRemittanceActivity.this, selectedImageUri, projection, null, null,
                    null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(column_index);
            String[] splits = filePath.split("\\/");
            int len = splits.length;
            image.setText(splits[len - 1]);
            image.setError(null);
        } else if (requestCode == SELECT_PDF_DIALOG) {
            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
            imageBase64 = getStringFile(list.get(0).getPath());
            image.setText(list.get(0).getName());
            imgType = "pdf";
        } else if (requestCode == CONTACT_PICKER_RESULT) {
            reset();
            contactRead(data, input_mobile);
        }
    }

    public String getStringFile(String f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(f);

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] b = stream.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
        return null;
    }

    public void loadCamera() {
        if (ActivityCompat.checkSelfPermission(PMTRemittanceActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestReadPhoneStatePermission();
        } else {
            doPermissionGrantedStuffs();
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PMTRemittanceActivity.this,
                Manifest.permission.CAMERA)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(PMTRemittanceActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            ActivityCompat.requestPermissions(PMTRemittanceActivity.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doPermissionGrantedStuffs();
            } else {
                alertPerm(getString(R.string.permissions_not_granted_read_phone_state), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadIMEI();
                    }
                });
            }
        }
    }

    private void alertPerm(String msg, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PMTRemittanceActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(PMTRemittanceActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }
    }

}
