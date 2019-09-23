package com.rapipay.android.agent.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.rapipay.android.agent.Model.NewKYCPozo;
import com.rapipay.android.agent.Model.NewKycAddress;
import com.rapipay.android.agent.Model.NewKycBusiness;
import com.rapipay.android.agent.Model.NewKycPersion;
import com.rapipay.android.agent.Model.NewKycVerification;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.main_directory.BarcodeActivity;
import com.rapipay.android.agent.main_directory.KYCFormActivity;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AgentKYCFragment extends BaseFragment implements RequestHandler, View.OnClickListener {
    JSONObject jsonObject = null;
    protected ArrayList<RapiPayPozo> list;
    public static Bitmap bitmap_trans = null;
    public static String byteBase64;
    EditText mobile_no, documentid;
    private LinearLayout kyc_layout_bottom, scan_data;
    AppCompatButton sub_btn;
    Spinner spinner;
    String[] items = new String[]{"Select Document Type", "Aadhar Card", "Voter Id Card", "Driving License", "Passport"};
    String spinner_value = "";
    String type = "MANUAL";
    private ArrayList<NewKycPersion> newKYCList_Personal = null;
    private ArrayList<NewKycAddress> newKYCList_Address = null;
    private ArrayList<NewKycBusiness> newKYCList_Buisness = null;
    private ArrayList<NewKycVerification> newKYCList_Verify = null;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    private View rv = null;
    ArrayList<String> stcondition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.activity_kyc_new, container, false);
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        TYPE = getActivity().getIntent().getStringExtra("type");
        customerType = getActivity().getIntent().getStringExtra("customerType");
        initialize(rv);
        return rv;
    }

    public void initialize(View rv) {
        bitmap_trans = null;
        byteBase64 = "";
        mobile_no = (EditText) rv.findViewById(R.id.mobile_no);
        documentid = (EditText) rv.findViewById(R.id.documentid);
        kyc_layout_bottom = (LinearLayout) rv.findViewById(R.id.kyc_layout_bottom);
        scan_data = (LinearLayout) rv.findViewById(R.id.scan_data);
        sub_btn = (AppCompatButton) rv.findViewById(R.id.sub_btn);
        spinner = rv.findViewById(R.id.docs_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
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
        rv.findViewById(R.id.scan_btn).setOnClickListener(this);
        rv.findViewById(R.id.manual_btn).setOnClickListener(this);
        rv.findViewById(R.id.sub_btn).setOnClickListener(this);
        rv.findViewById(R.id.prsnl_btn).setOnClickListener(this);
        rv.findViewById(R.id.adrs_btn).setOnClickListener(this);
        rv.findViewById(R.id.business_btn).setOnClickListener(this);
        rv.findViewById(R.id.verification_btn).setOnClickListener(this);

    }

    public void clikable() {
        sub_btn.setClickable(true);
        rv.findViewById(R.id.scan_btn).setClickable(true);
        rv.findViewById(R.id.manual_btn).setClickable(true);
        rv.findViewById(R.id.prsnl_btn).setClickable(true);
        rv.findViewById(R.id.adrs_btn).setClickable(true);
        rv.findViewById(R.id.business_btn).setClickable(true);
        rv.findViewById(R.id.verification_btn).setClickable(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        clikable();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.sub_btn:
                sub_btn.setClickable(false);
                if (!ImageUtils.commonNumber(mobile_no.getText().toString(), 10)) {
                    mobile_no.setError("Please enter valid data");
                    mobile_no.requestFocus();
                    sub_btn.setClickable(true);
                } else if (spinner_value.isEmpty())
                    Toast.makeText(getActivity(), "Please Select document type", Toast.LENGTH_SHORT).show();
                else if (spinner_value.equalsIgnoreCase("Aadhar Card") && (documentid.getText().toString().isEmpty() || documentid.getText().toString().length() != 12)) {
                    documentid.setError("Please enter valid data");
                    documentid.requestFocus();
                    sub_btn.setClickable(true);
                } else if (spinner_value.equalsIgnoreCase("Aadhar Card") && !ImageUtils.validateVerhoeff(documentid.getText().toString())) {
                    documentid.setError("Please enter valid Aadhar Number");
                    documentid.requestFocus();
                    sub_btn.setClickable(true);
                } else if (spinner_value.equalsIgnoreCase("Voter Id Card") && (documentid.getText().toString().isEmpty() || documentid.getText().toString().length() != 10)) {
                    documentid.setError("Please enter valid data");
                    documentid.requestFocus();
                    sub_btn.setClickable(true);
                } else if (spinner_value.equalsIgnoreCase("Passport") && !documentid.getText().toString().matches("^[A-Z]{1}-[0-9]{7}$")) {
                    documentid.setError("Please enter valid data");
                    documentid.requestFocus();
                    sub_btn.setClickable(true);
                } else if (documentid.getText().toString().isEmpty()) {
                    documentid.setError("Please enter valid data");
                    documentid.requestFocus();
                    sub_btn.setClickable(true);
                } else {
                    new AsyncPostMethod(WebConfig.EKYC, request_user(customerType).toString(), headerData, AgentKYCFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                    sub_btn.setClickable(false);
                }
                break;
            case R.id.scan_btn:
                rv.findViewById(R.id.scan_btn).setClickable(false);
                type = "SCAN";
                mobile_no.setEnabled(false);
                documentid.setEnabled(false);
                spinner.setEnabled(false);
                spinner.setClickable(false);
                intent = new Intent(getActivity(), BarcodeActivity.class);
                intent.putExtra("type", TYPE);
                startActivityForResult(intent, 1);
                break;
            case R.id.manual_btn:
                rv.findViewById(R.id.manual_btn).setClickable(false);
                type = "MANUAL";
                mobile_no.setEnabled(false);
                documentid.setEnabled(false);
                spinner.setEnabled(false);
                spinner.setClickable(false);
                scan_data.setVisibility(View.GONE);
                jsonObject = null;
                kyc_layout_bottom.setVisibility(View.VISIBLE);
                break;
            case R.id.prsnl_btn:
                rv.findViewById(R.id.prsnl_btn).setClickable(false);
                scan = false;
                intent = new Intent(getActivity(), KYCFormActivity.class);
                rv.findViewById(R.id.manual_btn).setClickable(true);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "personal");
                intent.putExtra("customerType", customerType);
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
                        } else
                            intent.putExtra("localBusiness", "false");
                    } else
                        intent.putExtra("localAddress", "false");
                } else
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
                rv.findViewById(R.id.adrs_btn).setClickable(false);
                intent = new Intent(getActivity(), KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "address");
                intent.putExtra("customerType", customerType);
                intent.putExtra("mobileNo", mobile_no.getText().toString());
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
                        } else
                            intent.putExtra("localBusiness", "false");
                    } else
                        intent.putExtra("localAddress", "false");
                } else
                    intent.putExtra("localPersonal", "false");
                if (jsonObject != null) {
                    intent.putExtra("scandata", jsonObject.toString());
                }
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
                startActivityForResult(intent, 2);
                break;
            case R.id.business_btn:
                rv.findViewById(R.id.business_btn).setClickable(false);
                intent = new Intent(getActivity(), KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "buisness");
                intent.putExtra("customerType", customerType);
                intent.putExtra("mobileNo", mobile_no.getText().toString());
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
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
                        } else
                            intent.putExtra("localBusiness", "false");
                    } else
                        intent.putExtra("localAddress", "false");
                } else
                    intent.putExtra("localPersonal", "false");
                startActivityForResult(intent, 2);
                break;
            case R.id.verification_btn:
                rv.findViewById(R.id.verification_btn).setClickable(false);
                intent = new Intent(getActivity(), KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("customerType", customerType);
                intent.putExtra("button", "verification");
                intent.putExtra("mobileNo", mobile_no.getText().toString());
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
                intent.putExtra("localPersonal", "true");
                intent.putExtra("localAddress", "true");
                intent.putExtra("localBusiness", "true");
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
                        } else
                            intent.putExtra("localBusiness", "false");
                    } else
                        intent.putExtra("localAddress", "false");
                } else
                    intent.putExtra("localPersonal", "false");
                startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!scan) {
            resumeCall();
        }
    }

    private void resumeCall() {
        //  String condition = "where " + RapipayRealmDB.MOBILENO + "='" + mobile_no.getText().toString() + "'" + " AND " + RapipayRealmDB.DOCUMENTTYPE + "='" + spinner_value + "'" + " AND " + RapipayRealmDB.DOCUMENTID + "='" + documentid.getText().toString() + "'";
        stcondition = new ArrayList<>();
        stcondition.add(mobile_no.getText().toString());
        stcondition.add(spinner_value);
        stcondition.add(documentid.getText().toString());
        newKYCList_Personal = BaseCompactActivity.dbRealm.getKYCDetails_Personal(stcondition);
        if (newKYCList_Personal != null && newKYCList_Personal.size() != 0) {
            rv.findViewById(R.id.prsnl_btn).setBackgroundColor(getResources().getColor(R.color.green));
            documentid.setText(newKYCList_Personal.get(0).getDOCUMENTID());
            documentid.setEnabled(false);
            TextView documentype = (TextView) rv.findViewById(R.id.documentype);
            documentype.setVisibility(View.VISIBLE);
            documentype.setEnabled(false);
            documentype.setText(newKYCList_Personal.get(0).getDOCUMENTTYPE());
            spinner.setVisibility(View.GONE);
            sub_btn.setVisibility(View.GONE);
            scan_data.setVisibility(View.GONE);
            mobile_no.setEnabled(false);
            kyc_layout_bottom.setVisibility(View.VISIBLE);
            newKYCList_Address = BaseCompactActivity.dbRealm.getKYCDetails_Address(stcondition);
            if (newKYCList_Address.size() != 0) {
                rv.findViewById(R.id.adrs_btn).setBackgroundColor(getResources().getColor(R.color.green));
                rv.findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
                newKYCList_Buisness = BaseCompactActivity.dbRealm.getKYCDetails_BUISNESS(stcondition);
                if (newKYCList_Buisness.size() != 0) {
                    rv.findViewById(R.id.business_btn).setBackgroundColor(getResources().getColor(R.color.green));
                    rv.findViewById(R.id.buisness_layout).setVisibility(View.VISIBLE);
                    newKYCList_Verify = BaseCompactActivity.dbRealm.getKYCDetails_VERIFY(stcondition);
                    if (newKYCList_Verify.size() != 0) {
                        rv.findViewById(R.id.verification_btn).setBackgroundColor(getResources().getColor(R.color.green));
                        rv.findViewById(R.id.verification_button).setVisibility(View.VISIBLE);
                    } else
                        rv.findViewById(R.id.verification_button).setVisibility(View.VISIBLE);
                } else
                    rv.findViewById(R.id.buisness_layout).setVisibility(View.VISIBLE);
            } else
                rv.findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
        } else {
            showcon();
        }
    }

    public void showcon() {
        scan_data.setVisibility(View.GONE);
        sub_btn.setVisibility(View.VISIBLE);
        kyc_layout_bottom.setVisibility(View.GONE);
        rv.findViewById(R.id.adrs_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        rv.findViewById(R.id.business_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        rv.findViewById(R.id.verification_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        rv.findViewById(R.id.prsnl_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        rv.findViewById(R.id.address_layout).setVisibility(View.GONE);
        rv.findViewById(R.id.buisness_layout).setVisibility(View.GONE);
        rv.findViewById(R.id.verification_button).setVisibility(View.GONE);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public JSONObject request_user(String kycType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "VALIDATE_KYC_DETAILS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
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
                        //  String condition = "where " + RapipayRealmDB.MOBILENO + "='" + mobile_no.getText().toString() + "'" + " AND " + RapipayRealmDB.DOCUMENTTYPE + "='" + spinner_value + "'" + " AND " + RapipayRealmDB.DOCUMENTID + "='" + documentid.getText().toString() + "'";
                        stcondition = new ArrayList<>();
                        stcondition.add(mobile_no.getText().toString());
                        stcondition.add(spinner_value);
                        stcondition.add(documentid.getText().toString());
                        newKYCList_Personal = BaseCompactActivity.dbRealm.getKYCDetails_Personal(stcondition);
                        if (newKYCList_Personal != null && newKYCList_Personal.size() != 0) {
                            rv.findViewById(R.id.prsnl_btn).setBackgroundColor(getResources().getColor(R.color.green));
                            documentid.setText(newKYCList_Personal.get(0).getDOCUMENTID());
                            documentid.setEnabled(false);
                            TextView documentype = (TextView) rv.findViewById(R.id.documentype);
                            documentype.setVisibility(View.VISIBLE);
                            documentype.setEnabled(false);
                            mobile_no.setEnabled(false);
                            documentype.setText(newKYCList_Personal.get(0).getDOCUMENTTYPE());
                            spinner.setVisibility(View.GONE);
                            sub_btn.setVisibility(View.GONE);
                            scan_data.setVisibility(View.GONE);
                            kyc_layout_bottom.setVisibility(View.VISIBLE);
                            newKYCList_Address = BaseCompactActivity.dbRealm.getKYCDetails_Address(stcondition);
                            if (newKYCList_Address.size() != 0) {
                                rv.findViewById(R.id.adrs_btn).setBackgroundColor(getResources().getColor(R.color.green));
                                rv.findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
                                newKYCList_Buisness = BaseCompactActivity.dbRealm.getKYCDetails_BUISNESS(stcondition);
                                if (newKYCList_Buisness.size() != 0) {
                                    rv.findViewById(R.id.business_btn).setBackgroundColor(getResources().getColor(R.color.green));
                                    rv.findViewById(R.id.buisness_layout).setVisibility(View.VISIBLE);
                                    newKYCList_Verify = BaseCompactActivity.dbRealm.getKYCDetails_VERIFY(stcondition);
                                    if (newKYCList_Verify.size() != 0) {
                                        rv.findViewById(R.id.verification_btn).setBackgroundColor(getResources().getColor(R.color.green));
                                        rv.findViewById(R.id.verification_button).setVisibility(View.VISIBLE);
                                    } else
                                        rv.findViewById(R.id.verification_button).setVisibility(View.VISIBLE);
                                } else
                                    rv.findViewById(R.id.buisness_layout).setVisibility(View.VISIBLE);
                            } else
                                rv.findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
                        } else {
                            scan_data.setVisibility(View.VISIBLE);
                            if (spinner_value.equalsIgnoreCase("Aadhar Card"))
                                rv.findViewById(R.id.scan_btn).setVisibility(View.VISIBLE);
                            sub_btn.setVisibility(View.GONE);
                            kyc_layout_bottom.setVisibility(View.GONE);
                            rv.findViewById(R.id.adrs_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            rv.findViewById(R.id.business_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            rv.findViewById(R.id.verification_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            rv.findViewById(R.id.prsnl_btn).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            rv.findViewById(R.id.address_layout).setVisibility(View.GONE);
                            rv.findViewById(R.id.buisness_layout).setVisibility(View.GONE);
                            rv.findViewById(R.id.verification_button).setVisibility(View.GONE);
                            documentid.setEnabled(false);
                            spinner.setEnabled(false);
                            spinner.setClickable(false);
                        }
                        hideKeyboard(getActivity());
                    }
                }
            }else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(getActivity(),object.getString("responseMessage"),Toast.LENGTH_LONG).show();
                setBack_click1(getActivity());
            }  else {
                responseMSg(object);
            }
            sub_btn.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
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
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap_trans.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    jsonObj = XML.toJSONObject(requiredValue);
                    jsonObject = jsonObj.getJSONObject("PrintLetterBarcodeData");
                    scan_data.setVisibility(View.GONE);
                    kyc_layout_bottom.setVisibility(View.VISIBLE);
                    scan = true;
//                    parseJson(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
