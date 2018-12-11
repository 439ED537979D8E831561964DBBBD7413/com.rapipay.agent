package com.rapipay.android.agent.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.main_directory.BarcodeActivity;
import com.rapipay.android.agent.main_directory.KYCFormActivity;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AgentKYCFragment extends Fragment implements RequestHandler, View.OnClickListener {
    JSONObject jsonObject = null;
    protected ArrayList<RapiPayPozo> list;
    protected Long tsLong;
    EditText mobile_no, documentid;
    private LinearLayout kyc_layout_bottom, scan_data;
    AppCompatButton sub_btn;
    Spinner spinner;
    String[] items = new String[]{"Select Document Type", "Aadhar Card", "Voter Id Card", "Driving License", "Passport"};
    String spinner_value = "", TYPE, customerType;
    String type = "";
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.activity_kyc_new, container, false);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        TYPE = getActivity().getIntent().getStringExtra("type");
        customerType = getActivity().getIntent().getStringExtra("customerType");
        initialize(rv);
        return rv;
    }

    public void initialize(View rv) {
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
                }else
                    documentid.setInputType(InputType.TYPE_CLASS_TEXT);
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
        if (!TYPE.equalsIgnoreCase("outside"))
            rv.findViewById(R.id.buisness_layout).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.sub_btn:
                if (!ImageUtils.commonNumber(mobile_no.getText().toString(), 10)) {
                    mobile_no.setError("Please enter valid data");
                    mobile_no.requestFocus();
                } else if (spinner_value.isEmpty())
                    Toast.makeText(getActivity(), "Please Select document type", Toast.LENGTH_SHORT).show();
                else if (documentid.getText().toString().isEmpty()) {
                    documentid.setError("Please enter valid data");
                    documentid.requestFocus();
                } else
                    new AsyncPostMethod("http://192.168.1.105:8080/KYC_RAPIPAY_APP/EKYCProcess", request_user(customerType).toString(), headerData, AgentKYCFragment.this, getActivity()).execute();
                break;
            case R.id.scan_btn:
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
                type = "MANUAL";
                mobile_no.setEnabled(false);
                documentid.setEnabled(false);
                spinner.setEnabled(false);
                spinner.setClickable(false);
                jsonObject = null;
                kyc_layout_bottom.setVisibility(View.VISIBLE);
                break;
            case R.id.prsnl_btn:
                intent = new Intent(getActivity(), KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "personal");
                intent.putExtra("customerType", "A");
                intent.putExtra("mobileNo", mobile_no.getText().toString());
                if (jsonObject != null) {
                    intent.putExtra("scandata", jsonObject.toString());
                }
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
                startActivityForResult(intent, 2);
                break;
            case R.id.adrs_btn:
                intent = new Intent(getActivity(), KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "address");
                intent.putExtra("customerType", "A");
                if (jsonObject != null) {
                    intent.putExtra("scandata", jsonObject.toString());
                }
                intent.putExtra("documentType", spinner_value);
                intent.putExtra("documentID", documentid.getText().toString());
                startActivityForResult(intent, 2);
                break;
            case R.id.business_btn:
                intent = new Intent(getActivity(), KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("button", "buisness");
                intent.putExtra("customerType", "A");
                startActivityForResult(intent, 2);
                break;
            case R.id.verification_btn:
                intent = new Intent(getActivity(), KYCFormActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("persons", TYPE);
                intent.putExtra("customerType", "A");
                intent.putExtra("button", "verification");
                startActivityForResult(intent, 2);
                break;
        }
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
                        scan_data.setVisibility(View.VISIBLE);
                        sub_btn.setVisibility(View.GONE);
                        hideKeyboard(getActivity());
                    }
                }
            }
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

//    private void parseJson(JSONObject object) {
//        try {
//            scan_check = 2;
//            if (object.has("name")) {
//                input_name.setText(object.getString("name"));
//                input_name.setEnabled(false);
//            }
//            if (object.has("house") && object.has("street") && object.has("lm") && object.has("vtc") && object.has("dist")) {
//                String add = object.getString("house") + ", " + object.getString("street") + ", " + object.getString("lm") + ", " + object.getString("vtc").replaceAll("^\\s+", "") + ", " + object.getString("dist");
//                input_address.setText(add);
//                input_address.setEnabled(false);
//            } else if (object.has("street") && object.has("lm") && object.has("vtc") && object.has("dist")) {
//                String add = object.getString("street") + ", " + object.getString("lm") + ", " + object.getString("vtc").replaceAll("^\\s+", "") + ", " + object.getString("dist");
//                input_address.setText(add.replace("null,", ""));
//                input_address.setEnabled(false);
//            } else if (object.has("house") && object.has("street") && object.has("lm") && object.has("loc") && object.has("vtc") && object.has("dist")) {
//                String add = object.getString("house") + ", " + object.getString("street") + ", " + object.getString("lm") + ", " + object.getString("loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "") + ", " + object.getString("dist");
//                input_address.setText(add);
//                input_address.setEnabled(false);
//            } else if (object.has("house") && object.has("street") && object.has("loc") && object.has("vtc") && object.has("dist")) {
//                String add = object.getString("house") + ", " + object.getString("street") + ", " + object.getString("loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "") + ", " + object.getString("dist");
//                input_address.setText(add);
//                input_address.setEnabled(false);
//            } else if (object.has("_house") && object.has("_street") && object.has("_lm") && object.has("_vtc") && object.has("_dist")) {
//                String add = object.getString("_house") + ", " + object.getString("_street") + ", " + object.getString("_lm") + ", " + object.getString("_vtc").replaceAll("^\\s+", "") + ", " + object.getString("_dist");
//                input_address.setText(add);
//                input_address.setEnabled(false);
//            } else if (object.has("_loc") && object.has("vtc") && object.has("dist")) {
//                String add = object.getString("_loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "") + ", " + object.getString("dist");
//                input_address.setText(add);
//                input_address.setEnabled(false);
//            } else if (object.has("loc") && object.has("vtc") && object.has("dist")) {
//                String add = object.getString("loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "") + ", " + object.getString("dist");
//                input_address.setText(add);
//                input_address.setEnabled(false);
//            } else if (object.has("_loc") && object.has("_vtc") && object.has("_dist")) {
//                String add = object.getString("_loc") + ", " + object.getString("_vtc").replaceAll("^\\s+", "") + ", " + object.getString("_dist");
//                input_address.setText(add);
//                input_address.setEnabled(false);
//            } else if (object.has("_lm") && object.has("_loc") && object.has("_vtc") && object.has("_dist")) {
//                String add = object.getString("_lm") + ", " + object.getString("_loc") + ", " + object.getString("vtc").replaceAll("^\\s+", "") + ", " + object.getString("dist");
//                input_address.setText(add);
//                input_address.setEnabled(false);
//            }
//            if (object.has("state"))
//                select_state.setText(object.getString("state"));
//            if (object.has("_state"))
//                select_state.setText(object.getString("_state"));
//            if (input_name.getText().toString().isEmpty() || input_address.getText().toString().isEmpty() || select_state.getText().toString().isEmpty())
//                Toast.makeText(getActivity(), "Please fill entry manually", Toast.LENGTH_SHORT).show();
//            select_state.setEnabled(false);
////            reset.setVisibility(View.VISIBLE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
