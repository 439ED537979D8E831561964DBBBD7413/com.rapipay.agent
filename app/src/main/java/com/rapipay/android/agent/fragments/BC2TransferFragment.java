package com.rapipay.android.agent.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.LastTransactionPozo;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.StatePozo;
import com.rapipay.android.agent.Model.bc2addresspojo.AddressResponse;
import com.rapipay.android.agent.Model.bc2addresspojo.PostOffice;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BCBeneficiaryAdapter;
import com.rapipay.android.agent.adapter.LastTransAdapter;
import com.rapipay.android.agent.adapter.PaymentAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.WalletRequestHandler;
import com.rapipay.android.agent.main_directory.MainActivity;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.CustomProgessDialog;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WalletAsyncMethod;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.MovableFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import me.grantland.widget.AutofitTextView;

import static android.app.Activity.RESULT_OK;
import static com.rapipay.android.agent.utils.BaseCompactActivity.dbRealm;


public class BC2TransferFragment extends BaseFragment implements View.OnClickListener, WalletRequestHandler, CustomInterface {
    EditText input_amount, input_account, input_name, senderpincode, input_name1, input_mobile, input_otp, searchfield;
    EditText incomesource, gender, documentid, documentype, city, district, address_name, sendercomname, state_update;
    AppCompatButton btn_otpsubmit, btn_fund, btn_verify;
    LinearLayout sender_layout, sender_layout1, otp_layout, fundlayout, beneficiary_layout, last_tran_layout, ln_bc1, ln_bc2;
    String otpRefId, fund_transferId, ifsc_code, reqFor;
    TextView bank_select, text_ben, bank_select_bene, select_state, detail_expend;
    ImageView btn_sender, img_bc1_check, img_bc2_check;
    RecyclerView beneficiary_details, trans_details;
    ArrayList<BeneficiaryDetailsPozo> beneficiaryDetailsPozoslist;
    ArrayList<LastTransactionPozo> transactionPozoArrayList;
    BeneficiaryDetailsPozo pozo;
    String amount = "", mobileNo;
    TextView bene_number, bene_name, newtin;
    float limit;
    RelativeLayout addbc2details;
    TextView limit_title;
    BCBeneficiaryAdapter adapter;
    boolean receipt_clicked = false;
    // String headerData;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 1000;
    MovableFloatingActionButton fab;
    boolean isFabClick = false;
    private TextInputLayout state_update_top, gender_layout, document_layout;
    boolean isFabClick1 = false;
    Spinner bank_district, bank_city, account_type, gender_spinner, spinner_docType;
    ArrayList<PaymentModePozo> list_gender = null;
    ArrayAdapter<String> adapter_doc;
    String headerData;
    String selectGender = "", docType = "", imgType = "";
    AutofitTextView date_text;
    String[] items = new String[]{"Select Document Type", "Aadhaar Card", "Voter ID", "Driving License", "Passport", "Rasan Card", "Pan Card", "Indian Citizenship"};
    View rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rv = inflater.inflate(R.layout.fragment_bc2_transfer3, container, false);
        localStorage = LocalStorage.getInstance(getActivity());
        heading = (TextView) getActivity().findViewById(R.id.toolbar_title);
        if (balance != null)
            heading.setText("BC2 Fund Transfer (Balance : Rs." + balance + ")");
        else
            heading.setText("BC2 Fund Transfer");
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        if (dbRealm != null && dbRealm.getDetails_Rapi())
            list = dbRealm.getDetails();
        initialize(rv);
        return rv;
    }

    private void clear() {
        input_amount.setText("");
        input_account.setText("");
        input_otp.setText("");
        bank_select.setText("Select Bank");
    }

    private void initialize(View rv) {
        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);
        beforeyear = selectedYear;
        fab = (MovableFloatingActionButton) rv.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        rv.findViewById(R.id.btn_submit).setOnClickListener(this);
        rv.findViewById(R.id.btn_submit).setClickable(true);
        text_ben = (TextView) rv.findViewById(R.id.text_ben);
        limit_title = (TextView) rv.findViewById(R.id.limit);
        input_amount = (EditText) rv.findViewById(R.id.input_amount);
        input_account = (EditText) rv.findViewById(R.id.input_account);
        input_name = (EditText) rv.findViewById(R.id.input_name);
        senderpincode = (EditText) rv.findViewById(R.id.senderpincode);
        input_name1 = (EditText) rv.findViewById(R.id.input_name1);
        detail_expend = (TextView) rv.findViewById(R.id.detail_expend);
        input_mobile = (EditText) rv.findViewById(R.id.input_mobile);
        input_otp = (EditText) rv.findViewById(R.id.input_otp);
        btn_fund = (AppCompatButton) rv.findViewById(R.id.btn_fund);
        state_update = (EditText) rv.findViewById(R.id.state_update);
        state_update_top = (TextInputLayout) rv.findViewById(R.id.state_update_top);
        incomesource = (EditText) rv.findViewById(R.id.incomesource);
        documentid = (EditText) rv.findViewById(R.id.documentid);
        addbc2details = (RelativeLayout) rv.findViewById(R.id.addbc2details);
        input_mobile = (EditText) rv.findViewById(R.id.input_mobile);
        documentype = (EditText) rv.findViewById(R.id.documentype);
        gender = (EditText) rv.findViewById(R.id.gender);
        gender_layout = (TextInputLayout) rv.findViewById(R.id.gender_layout);
        gender_layout.setVisibility(View.GONE);
        document_layout = (TextInputLayout) rv.findViewById(R.id.document_layout);
        document_layout.setVisibility(View.GONE);
        gender_spinner = (Spinner) rv.findViewById(R.id.gender_spinner);
        date_text = (AutofitTextView) rv.findViewById(R.id.date);
        date_text.setHint("Date Of Birth*");
        date_text.setOnClickListener(toDateClicked);
        city = (EditText) rv.findViewById(R.id.city);
        district = (EditText) rv.findViewById(R.id.district);
        address_name = (EditText) rv.findViewById(R.id.address_name);
        sendercomname = (EditText) rv.findViewById(R.id.sendercomname);
        select_state = (TextView) rv.findViewById(R.id.select_state);
        select_state.setClickable(true);
        list_gender = new ArrayList<PaymentModePozo>();
        list_gender.add(new PaymentModePozo("0", "Select Gender"));
        list_gender.add(new PaymentModePozo("0", "MALE"));
        list_gender.add(new PaymentModePozo("0", "FEMALE"));
        list_gender.add(new PaymentModePozo("0", "OTHERS"));
        if (list_gender.size() != 0)
            gender_spinner.setAdapter(new PaymentAdapter(getActivity(), list_gender));
        btn_fund.setOnClickListener(this);
        btn_verify = (AppCompatButton) rv.findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
        btn_sender = (ImageView) rv.findViewById(R.id.btn_sender);
        btn_sender.setOnClickListener(this);
        getActivity().findViewById(R.id.reset).setOnClickListener(this);
        getActivity().findViewById(R.id.reset).setVisibility(View.GONE);
        btn_sender.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        sender_layout = (LinearLayout) rv.findViewById(R.id.sender_layout);
        sender_layout1 = (LinearLayout) rv.findViewById(R.id.sender_layout1);
        otp_layout = (LinearLayout) rv.findViewById(R.id.otp_layout);
        btn_otpsubmit = (AppCompatButton) rv.findViewById(R.id.btn_otpsubmit);
        btn_otpsubmit.setOnClickListener(this);
        fundlayout = (LinearLayout) rv.findViewById(R.id.fundlayout);
        searchfield = (EditText) rv.findViewById(R.id.searchfield);
        beneficiary_layout = (LinearLayout) rv.findViewById(R.id.beneficiary_layout);
        beneficiary_details = (RecyclerView) rv.findViewById(R.id.beneficiary_details);
        last_tran_layout = (LinearLayout) rv.findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) rv.findViewById(R.id.trans_details);
        bank_select = (TextView) rv.findViewById(R.id.bank_select);
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_bank = new ArrayList<>();
                ArrayList<BankDetailsPozo> list_bank1 = dbRealm.geBankDetails("");
                for (int i = 0; i < list_bank1.size(); i++) {
                    list_bank.add(list_bank1.get(i).getBankName());
                }
                customSpinner(bank_select, "Select Bank", list_bank, null);
            }
        });
        beneficiary_details.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), beneficiary_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (listitem_click) {
                    if (beneficiaryDetailsPozoslist.size() != 0) {
                        pozo = beneficiaryDetailsPozoslist.get(position);
                        if (pozo.getIsNEFT().equalsIgnoreCase("N") && pozo.getIsIMPS().equalsIgnoreCase("Y")) {
                            customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", "IMPS");
                            listitem_click = false;
                        } else if (pozo.getIsNEFT().equalsIgnoreCase("Y") && pozo.getIsIMPS().equalsIgnoreCase("N")) {
                            customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", "NEFT");
                            listitem_click = false;
                        } else {
                            customDialog_Common("BENLAYOUT", null, pozo, "Fund Transfer Type", null);
                            listitem_click = false;
                        }
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (listitem_click) {
                    listitem_click = false;
                    if (beneficiaryDetailsPozoslist.size() != 0) {
                        pozo = beneficiaryDetailsPozoslist.get(position);
                        customDialog_Common("Beneficiary Details", null, pozo, "Sure you want to Delete Beneficiary?", "");
                    }
                }
            }
        }));
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                mLastClickTime = now;
                if (transactionPozoArrayList.size() != 0 && !receipt_clicked) {
                    receipt_clicked = true;
                    LastTransactionPozo pozo = transactionPozoArrayList.get(position);
                    new WalletAsyncMethod(WebConfig.WALLETRECEIPTURL, receipt_request(pozo).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                }
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
                //  if (flagdevicetype > 0) {
                if (s.length() == 10) {
                    clear();
                    new WalletAsyncMethod(WebConfig.BC2RemittanceApp, getSender_Validate().toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                } else {
                    reset();
                    fab.setVisibility(View.GONE);
                    getActivity().findViewById(R.id.btn_submit).setVisibility(View.GONE);
                    sender_layout1.setVisibility(View.GONE);
                    detail_expend.setVisibility(View.GONE);
                }
            }
        });
        searchfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s.toString().length() != -1)
                        adapter.filter(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        senderpincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    String condition = senderpincode.getText().toString();
                    initAddressApi(condition);
                } else {
                    notselectpinApi();
                }
            }
        });

        select_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_state1 = new ArrayList<>();
                ArrayList<StatePozo> list_state = BaseCompactActivity.dbRealm.getState_Details();
                for (int i = 0; i < list_state.size(); i++) {
                    list_state1.add(list_state.get(i).getHeaderData());
                }
                customSpinner(select_state, "Select State", list_state1);
            }
        });

        spinner_docType = rv.findViewById(R.id.docs_type);
        adapter_doc = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter_doc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_docType.setAdapter(adapter_doc);
        spinner_docType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                documentid.setText("");
                docType = items[position];
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

    boolean listitem_click = true;

    public JSONObject delete_Benef(BeneficiaryDetailsPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "DELETE_BENEFICIARY");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("beneficiaryId", pozo.getBeneficiaryId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("reqFor", "BC2");
            jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void notselectpinApi() {
        city.setText("");
        district.setText("");
        state_update.setText("");
        flagstate = 0;
        select_state.setClickable(true);
        state_update_top.setVisibility(View.GONE);
        select_state.setVisibility(View.VISIBLE);
    }

    public JSONObject receipt_request(LastTransactionPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Get_Txn_Recipt");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("orgTxnRef", pozo.getRefundTxnId());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("routeType", pozo.getTransferType());
            jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getJson_Validate() {
        JSONObject jsonObject = new JSONObject();
        if (!input_name.getText().toString().isEmpty() && !bank_select.getText().toString().isEmpty() && !bank_select.getText().toString().equalsIgnoreCase("Select Bank")) {
            try {
                // String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
                String condition = bank_select.getText().toString();
                ifsc_code = dbRealm.geBankIFSC(condition).get(0);
                jsonObject.put("serviceType", "Money_Transfer");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("txnAmmount", input_amount.getText().toString());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("senderName", input_name.getText().toString().trim());
                jsonObject.put("IFSC", ifsc_code);
                jsonObject.put("accountNo", input_account.getText().toString());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Enter Text", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject getSender_Validate() {
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
                jsonObject.put("reqFor", "BC2");
                jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject addBeneAccount(String accountNo, String ifsc_code, String bankAccountName, String CDFlag, String transfer_type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "ADD_BENEFICIARY_DETAILS");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", input_mobile.getText().toString());
            jsonObject.put("accountNo", accountNo);
            jsonObject.put("confirmAccountNo", accountNo);
            jsonObject.put("senderMobile", input_mobile.getText().toString());
            jsonObject.put("ifscCode", ifsc_code);
            jsonObject.put("accountName", bankAccountName);
            jsonObject.put("transferType", transfer_type);
            jsonObject.put("CDFlag", CDFlag);
            jsonObject.put("reqFor", "BC2");
            jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject addSender() {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10 && !input_name.getText().toString().isEmpty()) {
            try {
                jsonObject.put("serviceType", "ADD_SENDER_DETAILS");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("mobileNumber", input_mobile.getText().toString());
                jsonObject.put("senderName", input_name.getText().toString().trim());
                jsonObject.put("reqFor", "BC2");
                jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Enter Text", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    /*  {"serviceType":"Verify_Mobile","prevServiceType":"ADD_SENDER_DETAILS","otp":"213195","otprefID":"602900020298","fundTransferId":"19090706435415068",
              "transactionID":"19090706440849973","senderMobile":"9222222222","nodeAgentId":"1000000014","requestType":"BC_CHANNEL","typeMobileWeb":"WEB","reqFor":"BC2","txnIP":"172.16.50.73","sessionRefNo":"NZ794ZM3HG"}
  */

    public JSONObject add_OtpDetails(String otpRefId, String fund_transferId) {
        JSONObject jsonObject = new JSONObject();
        if (!input_mobile.getText().toString().isEmpty() && input_mobile.getText().toString().length() == 10) {
            if (input_otp.getText().toString().length() == 6 && !input_otp.getText().toString().isEmpty()) {
                try {
                    jsonObject.put("serviceType", "Verify_Mobile");
                    jsonObject.put("prevServiceType", "ADD_SENDER_DETAILS");
                    jsonObject.put("requestType", "BC_CHANNEL");
                    jsonObject.put("typeMobileWeb", "mobile");
                    jsonObject.put("transactionID", ImageUtils.miliSeconds());
                    jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                    jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                    jsonObject.put("senderMobile", input_mobile.getText().toString());
                    jsonObject.put("fundTransferId", fund_transferId);
                    jsonObject.put("otp", input_otp.getText().toString());
                    jsonObject.put("otprefID", otpRefId);
                    jsonObject.put("reqFor", "BC2");
                    jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
                    jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Enter Text", Toast.LENGTH_SHORT).show();
            }
        }
        return jsonObject;
    }

    protected void customDialog(String msg) {
        try {
            dialogs = new Dialog(getActivity());
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            alertLayout.setKeepScreenOn(true);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setVisibility(View.GONE);
            TextView dialog_msg = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            dialog_msg.setText(msg);
            dialog_msg.setVisibility(View.VISIBLE);
            alertLayout.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
            btn_ok.setVisibility(View.GONE);
            dialogs.setContentView(alertLayout);
            dialogs.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PostOffice postOffice;
    AddressResponse addressResponse;
    ArrayList<PostOffice> postOfficeArrayList;
    // CustomProgessDialog dialog;
    Dialog dialogs;
    int flagstate = 0;

    private void initAddressApi(final String condition) {
        String url = "https://api.postalpincode.in/pincode/" + condition;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        dialogs = new Dialog(getActivity());
        final CustomProgessDialog dialog = new CustomProgessDialog(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            postOffice = new PostOffice();
                            dialogs.dismiss();
                            dialog.hide_progress();
                            postOfficeArrayList = new ArrayList<>();
                            final Gson gson = new Gson();
                            JSONArray rootresponse = new JSONArray(response);
                            final JSONObject jsonObject = rootresponse.getJSONObject(0);
                            BaseCompactActivity.realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    try {
                                        addressResponse = gson.fromJson(jsonObject.toString(), AddressResponse.class);
                                        Log.e("jsonObject=", String.valueOf(addressResponse));
                                        for (int i = 0; i < addressResponse.getPostOffice().size(); i++) {
                                            postOfficeArrayList.add(addressResponse.getPostOffice().get(i));
                                        }
                                        for (int i = 0; i < postOfficeArrayList.size(); i++) {
                                            postOffice.setPincode(postOfficeArrayList.get(i).getPincode());
                                            postOffice.setDistrict(postOfficeArrayList.get(i).getDistrict());
                                            postOffice.setState(postOfficeArrayList.get(i).getState());
                                            postOffice.setBlock(postOfficeArrayList.get(i).getBlock());
                                        }
                                        realm.copyToRealm(postOffice);
                                        if (dbRealm.getBc2City(condition) != null) {
                                            city.setText(dbRealm.getBc2City(condition));
                                            district.setText(dbRealm.getBc2Dist(condition));
                                            state_update.setText(dbRealm.getBc2State(condition));
                                            state_update.setEnabled(false);
                                            flagstate = 1;
                                            state_update_top.setVisibility(View.VISIBLE);
                                            select_state.setVisibility(View.GONE);
                                            district.setClickable(false);
                                            city.setClickable(false);
                                        }
                                        Log.e("response= postoffice=", postOffice.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            dialog.hide_progress();
                            e.printStackTrace();
                            notselectpinApi();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //post_des.setText("That didn't work!");
                dialog.hide_progress();
                Log.e("response url", error + "");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    @Override
    public void chechStatus(JSONObject object, String hitfrom) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (!isFabClick1) {
                    sender_layout1.setVisibility(View.VISIBLE);
                    isFabClick1 = true;
                    detail_expend.setVisibility(View.GONE);
                } else if (isFabClick1) {
                    sender_layout1.setVisibility(View.GONE);
                    isFabClick1 = false;
                    detail_expend.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.back_click:
                setBack_click(getActivity());
                getActivity().finish();
                break;
            case R.id.btn_search:
                v.findViewById(R.id.btn_search).setClickable(false);
                hideKeyboard(getActivity());
                loadIMEI();
                break;
            case R.id.btn_fund:
                v.findViewById(R.id.btn_fund).setClickable(false);
                hideKeyboard(getActivity());
                addBeneDetails("FUNDTRANSFER", "Add Beneficiary Detail");
                break;
            case R.id.btn_sender:
                hideKeyboard(getActivity());
                if (!input_name.getText().toString().isEmpty())
                    new WalletAsyncMethod(WebConfig.BC2RemittanceApp, addSender().toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                else
                    Toast.makeText(getActivity(), "Please enter correct text", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_otpsubmit:
                if (!otpRefId.isEmpty() && !fund_transferId.isEmpty() && !input_otp.getText().toString().isEmpty()) {
                    hideKeyboard(getActivity());
                    new WalletAsyncMethod(WebConfig.BC2RemittanceApp, add_OtpDetails(otpRefId, fund_transferId).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                } else
                    Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                break;
            case R.id.reset:
                v.findViewById(R.id.reset).setClickable(false);
                reset();
                input_mobile.setText("");
                input_mobile.setEnabled(true);
                break;
            case R.id.delete_all:
                v.findViewById(R.id.delete_all).setClickable(false);
                addBeneDetails("FUNDTRANSFER", "Add Beneficiary Detail");
                break;
            /*case R.id.select_state:
             *//*ArrayList<String> list_state1 = new ArrayList<>();
                ArrayList<StatePozo> list_state = dbRealm.getState_Details();
                for (int i = 0; i < list_state.size(); i++) {
                    list_state1.add(list_state.get(i).getHeaderData());
                }
                customSpinner(select_state, "Select State", list_state1);*//*

                break;*/

            case R.id.btn_submit:
                if (input_name1.getText().toString().isEmpty()) {
                    input_name1.setError("Please enter valid name");
                    input_name1.requestFocus();
                } else if (selectGender.equalsIgnoreCase("Select Gender")) {
                    Toast.makeText(getActivity(), "Please Select Gender", Toast.LENGTH_SHORT).show();
                } else if (Math.abs(years - beforeyear) < 18) {
                    // Log.e("years "+years,"beforeyear " +beforeyear+" ageyear difference"+Math.abs(years-beforeyear));
                    Toast.makeText(getActivity(), "Age must be greater than 17 years", Toast.LENGTH_SHORT).show();
                } else if (senderpincode.getText().toString().isEmpty()) {
                    senderpincode.setError("Please enter valid pincode");
                    senderpincode.requestFocus();
                } else if (district.getText().toString().isEmpty()) {
                    district.setError("Please enter district");
                    district.requestFocus();
                } else if (city.getText().toString().isEmpty()) {
                    city.setError("Please enter city");
                    city.requestFocus();
                } else if (address_name.getText().toString().isEmpty()) {
                    address_name.setError("Please enter valid address");
                    address_name.requestFocus();
                } else if (docType.equalsIgnoreCase("Select Document Type")) {
                    Toast.makeText(getActivity(), "Please Select document type", Toast.LENGTH_SHORT).show();
                } else if (date_text.getText().toString().isEmpty()) {
                    date_text.setError("Please enter valid date");
                    date_text.requestFocus();
                } else if (documentid.getText().toString().isEmpty()) {
                    documentid.setError("Please enter valid document id");
                    documentid.requestFocus();
                } else //if (flagstate == 0) {
                    if ((select_state.getText().toString().isEmpty() || select_state.getText().toString().equalsIgnoreCase("Select State")) && flagstate == 0) {
                        select_state.setError("Please enter valid state");
                        select_state.requestFocus();
                        //  }
                    } else {
                        if (flagstate == 0)
                            new WalletAsyncMethod(WebConfig.BC2RemittanceApp, addSenderDetails(select_state.getText().toString()).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                            //   new AsyncPostMethod(WebConfig.FUNDTRANSFER_URL, getJson_Validate().toString(), headerData, FundTransferActivity.this, getString(R.string.responseTimeOutTrans)).execute();
                        else
                            new WalletAsyncMethod(WebConfig.BC2RemittanceApp, addSenderDetails(state_update.getText().toString()).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                    }
                break;
        }
    }

    private JSONObject addSenderDetails(String state) {
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
                jsonObject.put("reqFor", "BC2");
                jsonObject.put("senderName", input_name1.getText().toString());
                jsonObject.put("senderGender", selectGender);
                jsonObject.put("senderDoB", date_text.getText().toString());
                jsonObject.put("senderAddress", address_name.getText().toString());
                jsonObject.put("docType", docType);
                jsonObject.put("docID", documentid.getText().toString());
                jsonObject.put("senderCity", city.getText().toString());
                jsonObject.put("senderPostalPin", senderpincode.getText().toString());
                jsonObject.put("senderDistrict", district.getText().toString());
                jsonObject.put("senderState", state);
                jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public void clickable() {
        try {
            beneficiary_details.setClickable(true);
            getActivity().findViewById(R.id.trans_details).setClickable(true);
            getActivity().findViewById(R.id.btn_search).setClickable(true);
            // getActivity().findViewById(R.id.delete_all).setClickable(true);
            getActivity().findViewById(R.id.reset).setClickable(true);
            getActivity().findViewById(R.id.btn_otpsubmit).setClickable(true);
            getActivity().findViewById(R.id.btn_sender).setClickable(true);
            getActivity().findViewById(R.id.btn_fund).setClickable(true);
            btn_cancel.setClickable(true);
            btn_ok.setClickable(true);
            btn_regenerate.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        clickable();
        super.onPause();
    }

    private void addBeneDetails(final String type, String msg) {
        final Dialog dialognew = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        btn_regenerate = (AppCompatButton) alertLayout.findViewById(R.id.btn_regenerate);
        try {
            if (type.equalsIgnoreCase("FUNDTRANSFER")) {
                dialog_cancel.setVisibility(View.VISIBLE);
                btn_ok.setVisibility(View.GONE);
                btn_cancel.setTextSize(11);
                btn_regenerate.setTextSize(11);
                btn_cancel.setText("Add Beneficiary");
                btn_regenerate.setText(getResources().getString(R.string.btnverify));
                btn_regenerate.setVisibility(View.VISIBLE);
                alertLayout.findViewById(R.id.bc_add_bene_details).setVisibility(View.VISIBLE);
                bene_number = (TextView) alertLayout.findViewById(R.id.bc_bene_number);
                bene_name = (TextView) alertLayout.findViewById(R.id.bc_bene_name);
                con_ifsc = (TextView) alertLayout.findViewById(R.id.bc_con_bene_num);
                bank_select_bene = (TextView) alertLayout.findViewById(R.id.bank_select_bene);
                bank_select_bene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> list_bank = new ArrayList<>();
                        ArrayList<BankDetailsPozo> list_bank1 = dbRealm.geBankDetails("");
                        for (int i = 0; i < list_bank1.size(); i++) {
                            list_bank.add(list_bank1.get(i).getBankName());
                        }
                        customSpinner(bank_select_bene, "Select Bank", list_bank, "BC");
                    }
                });
                newtin = (EditText) alertLayout.findViewById(R.id.newpin);
                dialognew.setContentView(alertLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialognew.setCancelable(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cancel.setClickable(false);
                getActivity().findViewById(R.id.btn_fund).setClickable(true);
                if (bank_select_bene.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select_bene.setError("Please enter mandatory field");
                    bank_select_bene.requestFocus();
                    btn_cancel.setClickable(true);
                } else if (!ImageUtils.commonAccount(bene_number.getText().toString(), 8, 30)) {
                    bene_number.setError("Please enter valid beneficiary account number");
                    bene_number.requestFocus();
                    btn_cancel.setClickable(true);
                } else if (bene_name.getText().toString().isEmpty()) {
                    bene_name.setError("Please enter valid data");
                    bene_name.requestFocus();
                    btn_cancel.setClickable(true);
                } else if (isNEFT && !con_ifsc.getText().toString().matches("^[a-zA-Z0-9]{1,50}$")) {
                    con_ifsc.setError("Please enter valid IFSC number");
                    con_ifsc.requestFocus();
                    btn_cancel.setClickable(true);
                } else {
                    dialognew.dismiss();
                    if (isNEFT && con_ifsc.getText().toString().matches("^[a-zA-Z0-9]{1,50}$"))
                        new WalletAsyncMethod(WebConfig.BC2RemittanceApp, addBeneAccount(bene_number.getText().toString(), con_ifsc.getText().toString(), bene_name.getText().toString(), "D", "NEFT").toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                    else if (!isNEFT) {
                        String condition = bank_select_bene.getText().toString();
                        ifsc_code = dbRealm.geBankIFSC(condition).get(0);
                        new WalletAsyncMethod(WebConfig.BC2RemittanceApp, addBeneAccount(bene_number.getText().toString(), con_ifsc.getText().toString(), bene_name.getText().toString(), "D", "IMPS").toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                    }
                }
            }
        });
        btn_regenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_regenerate.setClickable(false);
                getActivity().findViewById(R.id.btn_fund).setClickable(true);
                if (bank_select_bene.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select_bene.setError("Please enter mandatory field");
                    bank_select_bene.requestFocus();
                    btn_regenerate.setClickable(true);
                } else if (!ImageUtils.commonAccount(bene_number.getText().toString(), 5, 30)) {
                    bene_number.setError("Please enter valid beneficiary account number");
                    bene_number.requestFocus();
                    btn_regenerate.setClickable(true);
                } else if (isNEFT && !con_ifsc.getText().toString().matches("^[a-zA-Z0-9]{1,50}$")) {
                    con_ifsc.setError("Please enter valid IFSC number");
                    con_ifsc.requestFocus();
                    btn_regenerate.setClickable(true);
                } else {
                    dialognew.dismiss();
                    if (isNEFT && con_ifsc.getText().toString().matches("^[a-zA-Z0-9]{1,50}$"))
                        new WalletAsyncMethod(WebConfig.BCRemittanceApp, verify_Account(con_ifsc.getText().toString(), bene_number.getText().toString()).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                    else if (!isNEFT) {
                        //  String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select_bene.getText().toString() + "'";
                        String condition = bank_select_bene.getText().toString();
                        ifsc_code = dbRealm.geBankIFSC(condition).get(0);
//                    if (newtin.getText().toString().isEmpty())
                        new WalletAsyncMethod(WebConfig.BCRemittanceApp, verify_Account(ifsc_code, bene_number.getText().toString()).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                    }
                }
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognew.dismiss();
                getActivity().findViewById(R.id.btn_fund).setClickable(true);
            }
        });
        dialognew.show();
        Window window = dialognew.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void reset() {
        otp_layout.setVisibility(View.GONE);
        sender_layout.setVisibility(View.GONE);
        fundlayout.setVisibility(View.GONE);
        btn_sender.setVisibility(View.GONE);
        beneficiary_layout.setVisibility(View.GONE);
        last_tran_layout.setVisibility(View.GONE);
        bank_select.setText("Select Bank");
        input_name.setEnabled(true);
        gender_layout.setEnabled(true);
        gender_spinner.setEnabled(true);
        senderpincode.setEnabled(true);
        select_state.setEnabled(true);
        state_update_top.setEnabled(true);
        state_update.setEnabled(true);
        address_name.setEnabled(true);
        district.setEnabled(true);
        city.setEnabled(true);
        documentype.setEnabled(true);
        spinner_docType.setEnabled(true);
        documentid.setEnabled(true);
        getActivity().findViewById(R.id.btn_submit).setEnabled(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                reset();
                contactRead(data, input_mobile);
            }
        }
    }

    public JSONObject verify_Account(String ifsc, String accountno) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Verify_Account");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("senderName", input_name.getText().toString().trim());
            jsonObject.put("IFSC", ifsc);
            jsonObject.put("accountNo", accountno);
            jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", input_mobile.getText().toString());
            jsonObject.put("txnAmmount", "1");
            jsonObject.put("reqFor", "BC2");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject service_fee(String txnAmmount, String subType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_SERVICE_FEE");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("subType", subType);
            jsonObject.put("txnAmmount", txnAmmount);
            jsonObject.put("reqFor", "BC2");
            jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    private void insertBenfDetails(JSONArray array) {
        final Dialog dialogs = new Dialog(getActivity());
        final CustomProgessDialog dialog = new CustomProgessDialog(getActivity());
        beneficiaryDetailsPozoslist = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("bc_BENE_ID").isEmpty() || !object.getString("bc_BENE_ID").equalsIgnoreCase("null"))
                    beneficiaryDetailsPozoslist.add(new BeneficiaryDetailsPozo(object.getString("bank_ACCOUNT_NAME"), object.getString("account_NUMBER"), object.getString("account_IFSC"), object.getString("bank_Name"), object.getString("bc_BENE_ID"), object.getString("isVerified"), object.getString("isIMPS"), object.getString("isNEFT")));
                if (array.length() > 0) {
                    dialogs.dismiss();
                    dialog.hide_progress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (beneficiaryDetailsPozoslist.size() != 0)
            initializeBenAdapter(beneficiaryDetailsPozoslist);
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new LastTransactionPozo(object.getString("accountNo"), object.getString("txnAmount"), object.getString("refundTxnId"), object.getString("bankName"), object.getString("serviceProviderTXNID"), object.getString("transferType"), object.getString("txnRequestDate")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeBenAdapter(ArrayList<BeneficiaryDetailsPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        beneficiary_details.setLayoutManager(layoutManager);
        adapter = new BCBeneficiaryAdapter(getActivity(), list);
        beneficiary_details.setAdapter(adapter);
    }

    private void initializeTransAdapter(ArrayList<LastTransactionPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new LastTransAdapter(getActivity(), trans_details, list));
    }

    // change for bc2
    public JSONObject getMoney_Validate(String amount, JSONObject object, String beneficiaryId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "DMT_BC_AC_TRANSFER");
            // jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("transactionID", object.getString("bcTransactionID"));
            jsonObject.put("mobileNumber", input_mobile.getText().toString());
            jsonObject.put("senderName", input_name1.getText().toString().trim());
            jsonObject.put("txnAmmount", amount);
            jsonObject.put("beneficiaryId", beneficiaryId);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("reqFor", "BC2");
            jsonObject.put("transferType", transfer_type);
            jsonObject.put("txnIP", shieldsquare_IP2Hex(ImageUtils.ipAddress(getActivity())));
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            //  jsonObject.put("IFSC", pozo.getIfsc());
            if (newtpin.getText().toString().isEmpty())
                jsonObject.put("tPin", "");
            else
                jsonObject.put("tPin", ImageUtils.encodeSHA256(newtpin.getText().toString()));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String shieldsquare_IP2Hex(String reqIpAddr) {
        String hex = "";
        String[] part = reqIpAddr.split("[\\.,]");
        if (part.length < 4) {
            return "00000000";
        }
        for (int i = 0; i < 4; i++) {
            int decimal = Integer.parseInt(part[i]);
            if (decimal < 16) // Append a 0 to maintian 2 digits for every
            // number
            {
                hex += "0" + String.format("%01X", decimal);
            } else {
                hex += String.format("%01X", decimal);
            }
        }
        return hex;
    }//{"userAccessId":"10883423","responseMessage":"Insufficient balance in user account.","responseCode":"900"}

    @Override
    public void chechStat(String s, String hitfrom) {
        try {
            if (s != null) {
                JSONObject object = new JSONObject(s);
                if (object.has("apiCommonResposne") && !object.getString("apiCommonResposne").equalsIgnoreCase("null")) {
                    JSONObject object1 = object.getJSONObject("apiCommonResposne");
                    String balance = object1.getString("runningBalance");
                }
                if (object.getString("responseCode").equalsIgnoreCase("1032")) {
                    if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                        sender_layout.setVisibility(View.VISIBLE);
                        btn_sender.setVisibility(View.VISIBLE);
                        clear();
                        input_name.setText("");
                        getActivity().findViewById(R.id.warning).setVisibility(View.VISIBLE);
                    }
                } else if (object.getString("responseCode").equalsIgnoreCase("200")) {
                    // when new no and after successfully add the we get otp
                    if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS") && object.has("otpRefId")) {
                        addbc2details.setClickable(false);
                        btn_sender.setVisibility(View.GONE);
                        otp_layout.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);
                        detail_expend.setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.sender_layout1).setVisibility(View.GONE);
                        clear();
                        otpRefId = object.getString("otpRefId");
                        // when existing no. and after successfully add then otp must not show
                        if (otpRefId.isEmpty() || otpRefId == "null") {
                            otp_layout.setVisibility(View.GONE);
                            new WalletAsyncMethod(WebConfig.BC2RemittanceApp, getSender_Validate().toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                        }
                        if (object.has("reqFor"))
                            reqFor = object.getString("reqFor");
                        fund_transferId = object.getString("transactionId");
                    } else if (object.getString("serviceType").equalsIgnoreCase("ADD_SENDER_DETAILS")) {
                        new WalletAsyncMethod(WebConfig.BC2RemittanceApp, getSender_Validate().toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                    } else if (object.getString("serviceType").equalsIgnoreCase("DELETE_BENEFICIARY")) {
                        if (object.has("responseMsg"))
                            customDialog_Common("KYCLAYOUTLAY", object, null, "Payee Detail", object.getString("responseMsg"));
                        else
                            customDialog_Common("KYCLAYOUTLAY", object, null, "Payee Detail", object.getString("responseMessage"));
                    } else if (object.getString("serviceType").equalsIgnoreCase("GET_SERVICE_FEE")) {
                        customDialog_Common("Fund Transfer Confirmation", object, pozo, "Sure you want to Transfer?", input_mobile.getText().toString());
                    } else if (object.getString("serviceType").equalsIgnoreCase("Verify_Account")) {
                        customDialog_Common("Account Verify Details", object, pozo, "VerifyLayout", object.getString("senderName").trim());
                    } else if (object.getString("serviceType").equalsIgnoreCase("DMT_BC_AC_TRANSFER")) {
                        dialog.dismiss();
                        customDialog_Common("Fund Transfer Details", object, pozo, "VerifyLayout", input_name.getText().toString().trim());
                    } else if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) { // sender already registered
                        sender_layout.setVisibility(View.GONE);
                        getActivity().findViewById(R.id.reset).setVisibility(View.VISIBLE);
                        try {
                            JSONObject jsonObject = object.getJSONObject("senderDetails");
                            input_name.setText(jsonObject.getString("sender_Name"));
                            input_name.setClickable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (object.has("senderDetails")) {
                            //input_name.setText(object.getString("sender_Name"));
                            spinner_docType.setVisibility(View.GONE);
                            select_state.setVisibility(View.GONE);
                            enterSenderDetails(object.getJSONObject("senderDetails"));
                            hideKeyboard(getActivity());
                            otp_layout.setVisibility(View.GONE);
                            btn_sender.setVisibility(View.GONE);
                            sender_layout.setVisibility(View.VISIBLE);
                            fundlayout.setVisibility(View.VISIBLE);
                            if (object.has("remainingLimit") && !object.getString("remainingLimit").equalsIgnoreCase("null")) {
                                String split_limit[] = object.getString("remainingLimit").split("~");
                                for (int i = 0; i < split_limit.length; i++) {
                                    limit = limit + Float.valueOf(split_limit[i]);
                                }
                                limit_title.setText("Available Limit : Rs " + object.getString("remainingLimit").replace("~", "+"));
                                limit_title.setVisibility(View.VISIBLE);
                            }

                            text_ben.setText("Beneficiary Details (Tap to Fund Transfer & Long press to delete)");
                            if (object.has("beneListDetail")) {
                                if (Integer.parseInt(object.getString("beneCount")) > 0) {
                                    beneficiary_layout.setVisibility(View.VISIBLE);
                                    insertBenfDetails(object.getJSONArray("beneListDetail"));
                                }
                            }
                        }
                    } else if (object.getString("serviceType").equalsIgnoreCase("Money_Transfer")) {
                        if (object.has("getTxnReceiptDataList")) {
                            try {
                                JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                                customReceiptNew("Money Transfer", object, BC2TransferFragment.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                                customDialog_Common("Cannot generate receipt now please try later!", object, pozo, "VerifyLayout", input_name.getText().toString().trim());
                            }
                        }
                    } else if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                        hideKeyboard(getActivity());
                        otp_layout.setVisibility(View.GONE);
                        btn_sender.setVisibility(View.GONE);
                        sender_layout.setVisibility(View.VISIBLE);
                        fundlayout.setVisibility(View.VISIBLE);
                        clear();
                        input_name.setText(object.getString("sender_Name"));
                        getActivity().findViewById(R.id.warning).setVisibility(View.GONE);
//                    reset.setVisibility(View.VISIBLE);
                        input_name.setEnabled(false);
                        if (object.has("remainingLimit") && !object.getString("remainingLimit").equalsIgnoreCase("null")) {
                            String split_limit[] = object.getString("remainingLimit").split("~");
                            for (int i = 0; i < split_limit.length; i++) {
                                limit = limit + Float.valueOf(split_limit[i]);
                            }
                            limit_title.setText("Available Limit : Rs " + object.getString("remainingLimit").replace("~", "+"));
                            limit_title.setVisibility(View.VISIBLE);
                        }
                        text_ben.setText("Beneficiary Details (Tap to Fund Transfer & Long press to delete)");
                        if (object.has("beneListDetail")) {
                            if (Integer.parseInt(object.getString("beneCount")) > 0) {
                                beneficiary_layout.setVisibility(View.VISIBLE);
                                insertBenfDetails(object.getJSONArray("beneListDetail"));
                            }
                        }
                    } else if (object.getString("serviceType").equalsIgnoreCase("Get_Txn_Recipt")) {
                        if (object.has("getTxnReceiptDataList")) {
                            try {
                                JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                                customReceiptNew("Transaction Receipt", object, BC2TransferFragment.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                                customDialog_Common("Cannot generate receipt now please try later!", object, pozo, "VerifyLayout", input_name.getText().toString().trim());
                            }
                        }
                    } else if (object.getString("serviceType").equalsIgnoreCase("ADD_BENEFICIARY_DETAILS")) {
                        customDialog_Common("KYCLAYOUTS", null, null, null, null, object.getString("responseMessage"), BC2TransferFragment.this);
                    }
                } else if (object.getString("responseCode").equalsIgnoreCase("102")) {
                    customDialog_Common(object.getString("responseMessage"));
                } else if (object.getString("responseCode").equalsIgnoreCase("75235")) {
                    customDialog_Common(object.getString("responseMessage"));
                } else if (object.has("serviceType")) {
                    if (object.getString("serviceType").equalsIgnoreCase("SENDER_COMPLETE_DETAILS")) {
                        if (object.getString("responseCode").equalsIgnoreCase("86036")) {
                            sender_layout.setVisibility(View.GONE);
                            detail_expend.setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.reset).setVisibility(View.VISIBLE);
                            if (object.has("senderDetails")) {
                                // JSONArray array = object.getJSONArray("senderDetails");
                                //JSONObject jsonObject = object.getJSONObject("senderDetails");
                                //  enterSenderDetails(object.getJSONObject("senderDetails"));
                                select_state.setVisibility(View.GONE);
                                enterNewSenderDetails(object.getJSONObject("senderDetails"));
                                getActivity().findViewById(R.id.btn_submit).setVisibility(View.VISIBLE);
                                // delete_all.setVisibility(View.VISIBLE);
                            }
                        } else if (object.getString("responseCode").equalsIgnoreCase("86002")) { // when sender partially registered
                            sender_layout.setVisibility(View.VISIBLE);
                            input_name.setVisibility(View.VISIBLE);
                            input_name.setText(object.getJSONObject("senderDetails").getString("sender_Name"));
                            input_name1.setText(object.getJSONObject("senderDetails").getString("sender_Name"));
                            fab.setVisibility(View.VISIBLE);
                            input_name.setEnabled(false);
                            input_name1.setEnabled(false);
                            getActivity().findViewById(R.id.btn_submit).setVisibility(View.VISIBLE);
                        } else if (object.getString("responseCode").equalsIgnoreCase("75152")) { // when sender fresh register
                            fab.setVisibility(View.VISIBLE);
                            detail_expend.setVisibility(View.VISIBLE);
                            input_name.setEnabled(true);
                            input_name1.setEnabled(true);
                            input_name.setText("");
                            input_name1.setText("");
                            getActivity().findViewById(R.id.btn_submit).setVisibility(View.VISIBLE);
                        } else if (object.getString("responseCode").equalsIgnoreCase("60217")) {
                            if (object.has("responseMsg"))
                                customDialog_Common(object.getString("responseMsg"));
                            else
                                customDialog_Common(object.getString("responseMessage"));
                        }
                    }
                } else if (object.getString("responseCode").equalsIgnoreCase("102")) {
                    dialog.dismiss();
                    customDialog_Common(object.getString("responseMessage"));
                } else if (object.getString("responseCode").equalsIgnoreCase("101")) {
                    if (object.has("responseMsg"))
                        customDialog_Common("Money Transfer", null, null, "KYCLAYOUT", object.getString("responseMsg"));
                    else
                        customDialog_Common("Money Transfer", null, null, "KYCLAYOUT", object.getString("responseMessage"));
                } else if (object.getString("responseCode").equalsIgnoreCase("60067")) {
                    dialog.dismiss();
                    customDialog_Common(object.getString("responseMessage"));
                } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                    Toast.makeText(getActivity(), object.getString("responseMessage"), Toast.LENGTH_LONG).show();
                    setBack_click1(getActivity());
                } else if (object.getString("responseCode").equalsIgnoreCase("60003")) {
                    customDialog_Common(object.getString("responseMessage"));
                    btn_otpsubmit.setClickable(true);
                } else if (object.getString("responseCode").equalsIgnoreCase("401")) {
                    dialog.dismiss();
                    customDialog_Common(object.getString("responseMessage"));
                } else {
                    if (object.has("responseMessage")) {
                        customDialog_Common(object.getString("responseMessage"));
                    } else if (object.has("respnseMessage")) {
                        customDialog_Common(object.getString("respnseMessage"));
                    } else if (object.has("responseMsg"))
                        customDialog_Common(object.getString("responseMsg"));
                }
            } else {
                if (MainActivity.FN_TIME_OUT != null)
                    customDialog_Common(MainActivity.FN_TIME_OUT);
                else
                    customDialog_Common("Network Timeout! Kindly check your Transaction Ledger/History before initiating any financial transaction.");
            }
            beneficiary_details.setEnabled(true);
        } catch (Exception e) { //Attempt to invoke virtual method 'void android.widget.ImageView.setVisibility(int)' on a null object reference
            e.printStackTrace();
        }
    }

    private int selectedDate, selectedMonth, selectedYear;
    String months = null, dayss = null;
    int years = 0, beforeyear = 0;
    View.OnClickListener toDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.datepickerview);
            dialog.setTitle("");
            DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
            selectedMonth = calendar.get(Calendar.MONTH);
            selectedYear = calendar.get(Calendar.YEAR);
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    years = year;
                    Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                    if (String.valueOf(month + 1).length() == 1)
                        months = "0" + String.valueOf(month + 1);
                    else
                        months = String.valueOf(month + 1);
                    if (String.valueOf(dayOfMonth).length() == 1)
                        dayss = "0" + String.valueOf(dayOfMonth);
                    else
                        dayss = String.valueOf(dayOfMonth);
                    if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                        date_text.setText(year + "-" + months + "-" + dayss);
                        dialog.dismiss();
                    } else {

                        if (selectedDate != dayOfMonth) {
                            date_text.setText(year + "-" + months + "-" + dayss);
                            dialog.dismiss();
                        } else {
                            if (selectedMonth != month) {
                                date_text.setText(year + "-" + months + "-" + dayss);
                                dialog.dismiss();
                            }
                        }
                    }
                    selectedDate = dayOfMonth;
                    selectedMonth = (month);
                    selectedYear = year;
                }
            });
            dialog.show();
        }
    };

    private void enterSenderDetails(JSONObject object) {
        try {
            input_name1.setText(object.getString("sender_Name"));
            gender.setText(object.getString("sd_Gender"));
            senderpincode.setText(object.getString("sender_Postal_Pin"));
            address_name.setText(object.getString("sd_Address"));
            date_text.setText(object.getString("sd_Dob"));
            district.setText(object.getString("sd_District"));
            city.setText(object.getString("sd_City"));
            documentype.setText(object.getString("sd_Id_Type"));
            documentid.setText(object.getString("sd_Id_Number"));
            state_update.setText(object.getString("sender_State_Name"));
            input_name1.setEnabled(false);
            gender.setEnabled(false);
            senderpincode.setEnabled(false);
            address_name.setEnabled(false);
            date_text.setEnabled(false);
            district.setEnabled(false);
            city.setEnabled(false);
            documentype.setEnabled(false);
            documentid.setEnabled(false);
            state_update.setEnabled(false);
            select_state.setVisibility(View.GONE);
            state_update_top.setVisibility(View.VISIBLE);
            //   select_state.setVisibility(View.GONE);
            // image.setVisibility(View.GONE);
            getActivity().findViewById(R.id.btn_submit).setVisibility(View.GONE);
            amount = "";
            fab.setVisibility(View.VISIBLE);
            hideKeyboard(getActivity());
            input_mobile.setEnabled(true);
            input_name.setEnabled(false);
            gender_layout.setVisibility(View.VISIBLE);
            gender_spinner.setVisibility(View.GONE);
            document_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterNewSenderDetails(JSONObject object) {
        try {
            if (object.getString("sender_Name").equalsIgnoreCase(null))
                input_name.setText("");
            else
                input_name.setText(object.getString("sender_Name"));
            if (object.getString("sd_Gender").equalsIgnoreCase("null")) {
                gender.setText("");
                gender.setVisibility(View.GONE);
                gender_spinner.setVisibility(View.VISIBLE);
            } else {
                gender.setText(object.getString("sd_Gender"));
                gender.setVisibility(View.VISIBLE);
                gender_spinner.setVisibility(View.GONE);
            }
            if (object.getString("sd_Nationality").equalsIgnoreCase("null"))
                senderpincode.setText("");
            else senderpincode.setText(object.getString("sd_Nationality"));
            if (object.getString("sd_Address") == "null")
                address_name.setText("");
            else address_name.setText(object.getString("sd_Address"));
            if (object.getString("sd_Dob") == "null")
                date_text.setText("");
            else date_text.setText(object.getString("sd_Dob"));
            if (object.getString("sd_District") == "null")
                district.setText("");
            else district.setText(object.getString("sd_District"));
            if (object.getString("sd_City") == "null")
                city.setText("");
            else city.setText(object.getString("sd_City"));
            if (object.getString("sd_Id_Type") == "null") {
                documentype.setText("");
                document_layout.setVisibility(View.GONE);
            } else documentype.setText(object.getString("sd_Id_Type"));
            if (object.getString("sd_Id_Number") == "null")
                documentid.setText("");
            else documentid.setText(object.getString("sd_Id_Number"));
            if (object.getString("sender_State_Name") == "null") {
                state_update.setText("");
                state_update_top.setVisibility(View.GONE);
            } else {
                state_update.setText(object.getString("sender_State_Name"));
                state_update_top.setVisibility(View.VISIBLE);
            }
            getActivity().findViewById(R.id.btn_submit).setVisibility(View.GONE);
            amount = "";
            fab.setVisibility(View.VISIBLE);
            hideKeyboard(getActivity());
            input_mobile.setEnabled(false);
            gender_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void customDialog_Common(final String msg) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setText(this.getResources().getString(R.string.Alert));
            TextView dialog_msg = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            dialog_msg.setText(msg);
            dialog_msg.setVisibility(View.VISIBLE);
            alertLayout.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
            dialog.setCancelable(false);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.setContentView(alertLayout);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String transfer_type;

    protected void customFundTransfer(final String type, final JSONObject object, Object ob, String msg, final String input) {
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        text.setText(type);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("Fund Transfer")) {
            alertLayout.findViewById(R.id.ben_layout).setVisibility(View.VISIBLE);
            customDialog_Ben(alertLayout, (BeneficiaryDetailsPozo) ob, dialog, input);
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ok.setClickable(false);
                hideKeyboard(getActivity());
                if (type.equalsIgnoreCase("Fund Transfer")) {
                    if (!ImageUtils.commonAmount(ben_amount.getText().toString())) {
                        ben_amount.setError("Please enter valid amount.");
                        ben_amount.requestFocus();
                        btn_ok.setClickable(true);
                    } else if (Integer.parseInt(ben_amount.getText().toString()) > 25000) {
                        ben_amount.setError("Maximum transfer amount would be 25000.");
                        ben_amount.requestFocus();
                        btn_ok.setClickable(true);
                    } else if (Integer.parseInt(ben_amount.getText().toString()) > limit) {
                        ben_amount.setError("Maximum transfer amount would be " + limit + ".");
                        ben_amount.requestFocus();
                        btn_ok.setClickable(true);
                    } else {
                        transfer_type = input;
                        new WalletAsyncMethod(WebConfig.BC2RemittanceApp, service_fee(ben_amount.getText().toString(), "DMT_BC_AC_TRANSFER").toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                        dialog.dismiss();
                    }
                }
                listitem_click = true;
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cancel.setClickable(false);
                bank_select.setText("Select Bank");
                input_account.setText("");
                input_amount.setText("");
                dialog.dismiss();
                listitem_click = true;
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    AppCompatButton btn_cancel, btn_ok, btn_regenerate;

    protected void customDialog_Common(final String type, final JSONObject object, Object ob, String msg, String input) {
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        text.setText(msg);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("BENLAYOUT")) {
            btn_cancel.setText("NEFT");
            btn_cancel.setTextSize(10);
            btn_ok.setText("IMPS");
            btn_ok.setTextSize(10);
            if (pozo.getIsIMPS().equalsIgnoreCase("Y") && pozo.getIsNEFT().equalsIgnoreCase("Y")) {
                btn_ok.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.VISIBLE);
            } else if (!pozo.getIsNEFT().equalsIgnoreCase("Y")) {
                btn_cancel.setVisibility(View.GONE);
                btn_ok.setVisibility(View.VISIBLE);
            } else if (!pozo.getIsIMPS().equalsIgnoreCase("Y")) {
                btn_ok.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.VISIBLE);
            }
            dialog_cancel.setVisibility(View.VISIBLE);
            dialog.setContentView(alertLayout);
        }
        try {
            if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
                alertLayout.findViewById(R.id.custom_service).setVisibility(View.VISIBLE);
                if (!object.getString("subType").equalsIgnoreCase("Money_Transfer"))
                    serviceFee(alertLayout, object, (BeneficiaryDetailsPozo) ob, msg, input);
                else {
                    //   String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
                    String condition = bank_select.getText().toString();
                    ifsc_code = dbRealm.geBankIFSC(condition).get(0);
                    moneyTransgerFee(alertLayout, object, input_account.getText().toString(), ifsc_code, input_name.getText().toString().trim(), msg, input);
                }
            } else if (type.equalsIgnoreCase("Fund Transfer")) {
                alertLayout.findViewById(R.id.ben_layout).setVisibility(View.VISIBLE);
                customDialog_Ben(alertLayout, (BeneficiaryDetailsPozo) ob, dialog, "");
            } else if (type.equalsIgnoreCase("Beneficiary Details")) {
                btn_cancel.setVisibility(View.VISIBLE);
                customView(alertLayout, msg, dialog);
            } else if (msg.equalsIgnoreCase("VerifyLayout")) {
                if (type.equalsIgnoreCase("Fund Transfer Details"))
                    customReceiptNew(type, object, BC2TransferFragment.this);
                else {
                    btn_ok.setText("Add Beneficiary");
                    alertLayout.findViewById(R.id.verifytransferlayout).setVisibility(View.VISIBLE);
                    verifyTransferFee(alertLayout, object);
                }
            } else if (type.equalsIgnoreCase("KYCLAYOUTLAY")) {
                text.setText(msg);
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, input, dialog);
            } else if (msg.equalsIgnoreCase("KYCLAYOUT")) {
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, input, dialog);
            } else if (type.equalsIgnoreCase("Money Transfer"))
                btn_cancel.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                listitem_click = true;
                if (type.equalsIgnoreCase("BENLAYOUT")) {
                    customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", btn_ok.getText().toString());
                    dialog.dismiss();
                } else if (type.equalsIgnoreCase("Fund Transfer Confirmation"))
                    try {
                        if (!object.getString("subType").equalsIgnoreCase("Money_Transfer")) {
                            if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && newtpin.getText().toString().length() == 4) {
                                //need to change for bc2
                                new WalletAsyncMethod(WebConfig.BC2DMTBC2Service, getMoney_Validate(ben_amount.getText().toString(), object, pozo.getBeneficiaryId()).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();

                            } else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpin.getText().toString().isEmpty() || newtpin.getText().toString().length() != 4)) {
                                newtpin.setError("Please enter TPIN");
                                newtpin.requestFocus();
                            } else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("N")) {
                                //need to change for bc2
                                new WalletAsyncMethod(WebConfig.BC2DMTBC2Service, getMoney_Validate(ben_amount.getText().toString(), object, pozo.getBeneficiaryId()).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                            }
                        } else {
                            new WalletAsyncMethod(WebConfig.FUNDTRANSFER_URL, getJson_Validate().toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else if (type.equalsIgnoreCase("Fund Transfer")) {
                    if (!ImageUtils.commonAmount(ben_amount.getText().toString())) {
                        ben_amount.setError("Please enter valid amount.");
                        ben_amount.requestFocus();
                    } else if (Integer.parseInt(ben_amount.getText().toString()) > 25000) {
                        ben_amount.setError("Maximum transfer amount would be 25000.");
                        ben_amount.requestFocus();
                    } else if (Integer.parseInt(ben_amount.getText().toString()) > limit) {
                        ben_amount.setError("Maximum transfer amount would be " + limit + ".");
                        ben_amount.requestFocus();
                    } else {
                        new WalletAsyncMethod(WebConfig.BC2RemittanceApp, service_fee(ben_amount.getText().toString(), "Money_Transfer_Bene").toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                        dialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("KYCLAYOUTLAY")) {
                    new WalletAsyncMethod(WebConfig.BC2RemittanceApp, getSender_Validate().toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                    dialog.dismiss();
                } else if (type.equalsIgnoreCase("Beneficiary Details")) {
                    new WalletAsyncMethod(WebConfig.BC2RemittanceApp, delete_Benef((BeneficiaryDetailsPozo) pozo).toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
                    dialog.dismiss();
                } else if (type.equalsIgnoreCase("Money Transfer") || type.equalsIgnoreCase("Account Verify Details") || type.equalsIgnoreCase("Cannot generate receipt now please try later!")) {
                    input_account.setText("");
                    input_amount.setText("");
                    bank_select.setText("Select Bank");
                    dialog.dismiss();
                    if (type.equalsIgnoreCase("Account Verify Details")) {
                        try {
                            new WalletAsyncMethod(WebConfig.BC2RemittanceApp, addBeneAccount(object.getString("accountNo"), object.getString("ifscCode"), object.getString("bankAccountName"), "C", "IMPS").toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
//                            alertDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cancel.setClickable(false);
                listitem_click = true;
                if (type.equalsIgnoreCase("BENLAYOUT")) {
                    customFundTransfer("Fund Transfer", null, pozo, "Sure you want to Transfer?", btn_cancel.getText().toString());
                }
                bank_select.setText("Select Bank");
                input_account.setText("");
                input_amount.setText("");
                dialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listitem_click = true;
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("Money Transfer") || type.equalsIgnoreCase("Transaction Receipt")) {
            clear();
            receipt_clicked = false;
        } else if (type.equalsIgnoreCase("KYCLAYOUTS"))
            new WalletAsyncMethod(WebConfig.BC2RemittanceApp, getSender_Validate().toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
        else if (type.equalsIgnoreCase("Fund Transfer Details")) {
//            localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
            new WalletAsyncMethod(WebConfig.BC2RemittanceApp, getSender_Validate().toString(), headerData, BC2TransferFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "BCTRANSFER").execute();
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}

