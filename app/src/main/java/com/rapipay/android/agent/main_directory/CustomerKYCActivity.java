package com.rapipay.android.agent.main_directory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.NewKYCPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.AgentKYCFragment;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CustomerKYCActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener {
    JSONObject jsonObject = null;
    protected ArrayList<RapiPayPozo> list;
    protected Long tsLong;
    EditText mobile_no, documentid;
    private LinearLayout kyc_layout_bottom, scan_data;
    AppCompatButton sub_btn;
    Spinner spinner;
    String[] items = new String[]{"Select Document Type", "Aadhar Card", "Voter Id Card", "Driving License", "Passport"};
    String spinner_value = "", TYPE, mobileNo, customerType;
    String type = "MANUAL";
    private ArrayList<NewKYCPozo> newKYCList_Personal = null,newKYCList_Address=null,newKYCList_Buisness=null,newKYCList_Verify=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_kyc_layout);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        TYPE = getIntent().getStringExtra("type");
        mobileNo = getIntent().getStringExtra("mobileNo");
        customerType = getIntent().getStringExtra("customerType");
        initialize();
    }

    public void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Customer KYC");
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        mobile_no.setText(mobileNo);
        mobile_no.setEnabled(false);
        documentid = (EditText) findViewById(R.id.documentid);
        kyc_layout_bottom = (LinearLayout) findViewById(R.id.kyc_layout_bottom);
        scan_data = (LinearLayout) findViewById(R.id.scan_data);
        sub_btn = (AppCompatButton) findViewById(R.id.sub_btn);
        spinner = findViewById(R.id.docs_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerKYCActivity.this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                documentid.setText("");
                if (position != 0)
                    spinner_value = items[position];
                else
                    spinner_value = "";
                if (position == 1) {
                    documentid.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.LengthFilter(12);
                    documentid.setFilters(filterArray);
                } else
                    documentid.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        findViewById(R.id.scan_btn).setOnClickListener(this);
        findViewById(R.id.manual_btn).setOnClickListener(this);
        findViewById(R.id.sub_btn).setOnClickListener(this);
        findViewById(R.id.prsnl_btn).setOnClickListener(this);
        findViewById(R.id.adrs_btn).setOnClickListener(this);
        findViewById(R.id.business_btn).setOnClickListener(this);
        findViewById(R.id.verification_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back_click:
                finish();
                break;
            case R.id.sub_btn:
                if (!ImageUtils.commonNumber(mobile_no.getText().toString(), 10)) {
                    mobile_no.setError("Please enter valid data");
                    mobile_no.requestFocus();
                } else if (spinner_value.isEmpty())
                    Toast.makeText(CustomerKYCActivity.this, "Please Select document type", Toast.LENGTH_SHORT).show();
                else if (documentid.getText().toString().isEmpty()) {
                    documentid.setError("Please enter valid data");
                    documentid.requestFocus();
                } else
                    new AsyncPostMethod(WebConfig.EKYC_FORWARD, request_user(customerType).toString(), headerData, CustomerKYCActivity.this).execute();
                break;
            case R.id.scan_btn:
                type = "SCAN";
                mobile_no.setEnabled(false);
                documentid.setEnabled(false);
                spinner.setEnabled(false);
                spinner.setClickable(false);
                intent = new Intent(CustomerKYCActivity.this, BarcodeActivity.class);
                intent.putExtra("type", TYPE);
                startActivityForResult(intent, 1);
                break;
            case R.id.manual_btn:
                type = "MANUAL";
                mobile_no.setEnabled(false);
                documentid.setEnabled(false);
                spinner.setEnabled(false);
                spinner.setClickable(false);
                jsonObject = null;
                kyc_layout_bottom.setVisibility(View.VISIBLE);
                break;
            case R.id.prsnl_btn:
                intent = new Intent(CustomerKYCActivity.this, KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "personal");
                intent.putExtra("customerType", "C");
                if (newKYCList_Personal != null && newKYCList_Personal.size() != 0) {
                    intent.putExtra("localPersonal", "true");
                    if (newKYCList_Address != null && newKYCList_Address.size() != 0) {
                        intent.putExtra("localAddress", "true");
                        if (newKYCList_Buisness != null && newKYCList_Buisness.size() != 0) {
                            intent.putExtra("localBusiness", "true");
                            if (newKYCList_Verify != null && newKYCList_Verify.size() != 0)
                                intent.putExtra("localVerify", "true");
                            else
                                intent.putExtra("localVerify", "false");
                        }else
                            intent.putExtra("localBusiness", "false");
                    }else
                        intent.putExtra("localAddress", "false");
                }else
                    intent.putExtra("localPersonal", "false");
                intent.putExtra("mobileNo", mobile_no.getText().toString());
                if (jsonObject != null) {
                    intent.putExtra("scandata", jsonObject.toString());
                }
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
                startActivityForResult(intent, 2);
                break;
            case R.id.adrs_btn:
                intent = new Intent(CustomerKYCActivity.this, KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "address");
                intent.putExtra("customerType", "C");
                intent.putExtra("mobileNo", mobile_no.getText().toString());
                if (newKYCList_Address != null && newKYCList_Address.size() != 0) {
                    intent.putExtra("localAddress", "true");
                    if (newKYCList_Buisness != null && newKYCList_Buisness.size() != 0) {
                        intent.putExtra("localBusiness", "true");
                        if (newKYCList_Verify != null && newKYCList_Verify.size() != 0)
                            intent.putExtra("localVerify", "true");
                        else
                            intent.putExtra("localVerify", "false");
                    }else
                        intent.putExtra("localBusiness", "false");
                }else
                    intent.putExtra("localAddress", "false");
                if (jsonObject != null) {
                    intent.putExtra("scandata", jsonObject.toString());
                }
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
                startActivityForResult(intent, 2);
                break;
            case R.id.business_btn:
                intent = new Intent(CustomerKYCActivity.this, KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "buisness");
                intent.putExtra("customerType", "C");
                intent.putExtra("mobileNo", mobile_no.getText().toString());
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
                if (newKYCList_Buisness != null && newKYCList_Buisness.size() != 0) {
                    intent.putExtra("localBusiness", "true");
                    if (newKYCList_Verify != null && newKYCList_Verify.size() != 0)
                        intent.putExtra("localVerify", "true");
                    else
                        intent.putExtra("localVerify", "false");
                }else
                    intent.putExtra("localBusiness", "false");
                startActivityForResult(intent, 2);
                break;
            case R.id.verification_btn:
                intent = new Intent(CustomerKYCActivity.this, KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("customerType", "C");
                intent.putExtra("button", "verification");
                intent.putExtra("mobileNo", mobile_no.getText().toString());
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
                intent.putExtra("localPersonal", "true");
                intent.putExtra("localAddress", "true");
                intent.putExtra("localBusiness", "true");
                if (newKYCList_Verify != null && newKYCList_Verify.size() != 0)
                    intent.putExtra("localVerify", "true");
                else
                    intent.putExtra("localVerify", "false");
                startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeCall();
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public JSONObject request_user(String kycType) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "VALIDATE_KYC_DETAILS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("txnRef", "VKD" + tsLong.toString());
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("mobileNo", mobile_no.getText().toString());
            jsonObject.put("documentType", spinner_value);
            jsonObject.put("documentId", documentid.getText().toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("kycType", kycType);
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("VALIDATE_KYC_DETAILS")) {
                    if (object.getString("responseMessage").equalsIgnoreCase("success")) {
                        String condition = "where " + RapipayDB.MOBILENO + "='" + mobileNo + "'" + " AND " + RapipayDB.DOCUMENTTYPE + "='" + spinner_value + "'" + " AND " + RapipayDB.DOCUMENTID + "='" + documentid.getText().toString() + "'";
                        newKYCList_Personal = db.getKYCDetails_Personal(condition);
                        if (newKYCList_Personal != null && newKYCList_Personal.size() != 0) {
                            findViewById(R.id.prsnl_btn).setBackgroundColor(getResources().getColor(R.color.green));
                            documentid.setText(newKYCList_Personal.get(0).getDOCUMENTID());
                            documentid.setEnabled(false);
                            TextView documentype = (TextView) findViewById(R.id.documentype);
                            documentype.setVisibility(View.VISIBLE);
                            documentype.setEnabled(false);
                            documentype.setText(newKYCList_Personal.get(0).getDOCUMENTTYPE());
                            spinner.setVisibility(View.GONE);
                            sub_btn.setVisibility(View.GONE);
                            scan_data.setVisibility(View.GONE);
                            kyc_layout_bottom.setVisibility(View.VISIBLE);
                            newKYCList_Address = db.getKYCDetails_Address(condition);
                            if (newKYCList_Address.size() != 0) {
                                findViewById(R.id.adrs_btn).setBackgroundColor(getResources().getColor(R.color.green));
                                findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
                                newKYCList_Buisness = db.getKYCDetails_BUISNESS(condition);
                                if (newKYCList_Buisness.size() != 0) {
                                    findViewById(R.id.business_btn).setBackgroundColor(getResources().getColor(R.color.green));
                                    findViewById(R.id.buisness_layout).setVisibility(View.VISIBLE);
                                    newKYCList_Verify = db.getKYCDetails_VERIFY(condition);
                                    if (newKYCList_Verify.size() != 0) {
                                        findViewById(R.id.verification_btn).setBackgroundColor(getResources().getColor(R.color.green));
                                        findViewById(R.id.verification_button).setVisibility(View.VISIBLE);
                                    } else
                                        findViewById(R.id.verification_button).setVisibility(View.VISIBLE);
                                } else
                                    findViewById(R.id.buisness_layout).setVisibility(View.VISIBLE);
                            } else
                                findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
                        } else {
                            scan_data.setVisibility(View.VISIBLE);
                            sub_btn.setVisibility(View.GONE);
                        }
                        hideKeyboard(CustomerKYCActivity.this);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resumeCall(){
        String condition = "where " + RapipayDB.MOBILENO + "='" + mobileNo + "'" + " AND " + RapipayDB.DOCUMENTTYPE + "='" + spinner_value + "'" + " AND " + RapipayDB.DOCUMENTID + "='" + documentid.getText().toString() + "'";
        newKYCList_Personal = db.getKYCDetails_Personal(condition);
        if (newKYCList_Personal != null && newKYCList_Personal.size() != 0) {
            findViewById(R.id.prsnl_btn).setBackgroundColor(getResources().getColor(R.color.green));
            documentid.setText(newKYCList_Personal.get(0).getDOCUMENTID());
            documentid.setEnabled(false);
            TextView documentype = (TextView) findViewById(R.id.documentype);
            documentype.setVisibility(View.VISIBLE);
            documentype.setEnabled(false);
            documentype.setText(newKYCList_Personal.get(0).getDOCUMENTTYPE());
            spinner.setVisibility(View.GONE);
            sub_btn.setVisibility(View.GONE);
            kyc_layout_bottom.setVisibility(View.VISIBLE);
            scan_data.setVisibility(View.GONE);
            newKYCList_Address = db.getKYCDetails_Address(condition);
            if (newKYCList_Address.size() != 0) {
                findViewById(R.id.adrs_btn).setBackgroundColor(getResources().getColor(R.color.green));
                findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
                newKYCList_Buisness = db.getKYCDetails_BUISNESS(condition);
                if (newKYCList_Buisness.size() != 0) {
                    findViewById(R.id.business_btn).setBackgroundColor(getResources().getColor(R.color.green));
                    findViewById(R.id.buisness_layout).setVisibility(View.VISIBLE);
                    newKYCList_Verify = db.getKYCDetails_VERIFY(condition);
                    if (newKYCList_Verify.size() != 0) {
                        findViewById(R.id.verification_btn).setBackgroundColor(getResources().getColor(R.color.green));
                        findViewById(R.id.verification_button).setVisibility(View.VISIBLE);
                    } else
                        findViewById(R.id.verification_button).setVisibility(View.VISIBLE);
                } else
                    findViewById(R.id.buisness_layout).setVisibility(View.VISIBLE);
            } else
                findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void chechStat(String object) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                JSONObject jsonObj = null;
                try {
                    String requiredValue = data.getStringExtra("Key");
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    bitmap_trans.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
//                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//                    byteBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    jsonObj = XML.toJSONObject(requiredValue);
                    jsonObject = jsonObj.getJSONObject("PrintLetterBarcodeData");
                    kyc_layout_bottom.setVisibility(View.VISIBLE);
//                    parseJson(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
