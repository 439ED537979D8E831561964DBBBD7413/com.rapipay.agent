package com.rapipay.android.agent.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.PMTBenefPozo;
import com.rapipay.android.agent.Model.SettlementPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.SettlementPayLoadAdapter;
import com.rapipay.android.agent.adapter.SettlementPayloadDeleteAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.WalletRequestHandler;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WalletAsyncMethod;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class SettlementBankFragment extends BaseFragment implements WalletRequestHandler, View.OnClickListener, CustomInterface {
    private View rv = null;
    EditText ifsc_text, userName, accountNumber;
    String pageType, ifsc_code, filePath = "";
    TextView bank_select, account_verified;
    AutofitTextView image;
    AppCompatButton verifyAccount;
    AppCompatButton btn_addBank;
    static String transactionID;
    RecyclerView beneficiary_details, trans_details;
    LinearLayout beneficiary_layout, last_tran_layout;
    ArrayList<SettlementPozo> activeAccountList, deleteAccountList;
    CardView activeaccounts, deleteaccounts;
    boolean accountAdded = false;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.settlement_layout, container, false);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        pageType = getArguments().getString("message");
        initialize(rv);
        loadUrl();
        return rv;
    }

    private void loadUrl() {
        new WalletAsyncMethod(WebConfig.CRNF, getAgentBankDetails().toString(), headerData, SettlementBankFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "GETBANKLIST").execute();
    }

    private void initialize(View rv) {
        ifsc_text = (EditText) rv.findViewById(R.id.input_ifsc);
        userName = (EditText) rv.findViewById(R.id.input_username);
        accountNumber = (EditText) rv.findViewById(R.id.input_accountNo);
        bank_select = (TextView) rv.findViewById(R.id.bank_select);
        verifyAccount = (AppCompatButton) rv.findViewById(R.id.btn_ok);
        verifyAccount.setOnClickListener(this);
        btn_addBank = (AppCompatButton) rv.findViewById(R.id.btn_addBank);
        btn_addBank.setOnClickListener(this);
        image = (AutofitTextView) rv.findViewById(R.id.images);
        image.setOnClickListener(this);
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_bank = BaseCompactActivity.db.geBankDetails("");
                customSpinner(bank_select, "Select Bank", list_bank, ifsc_text);
            }
        });
        account_verified = (TextView) rv.findViewById(R.id.account_verified);
        beneficiary_layout = (LinearLayout) rv.findViewById(R.id.beneficiary_layout);
        beneficiary_details = (RecyclerView) rv.findViewById(R.id.beneficiary_details);
        last_tran_layout = (LinearLayout) rv.findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) rv.findViewById(R.id.trans_details);
        activeaccounts = (CardView) rv.findViewById(R.id.activeaccounts);
        deleteaccounts = (CardView) rv.findViewById(R.id.deleteaccounts);
    }

    public JSONObject verify_Account() {
        JSONObject jsonObject = new JSONObject();
        try {
            transactionID = ImageUtils.miliSeconds();
            String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + bank_select.getText().toString() + "'";
            ifsc_code = BaseCompactActivity.db.geBankIFSC(condition).get(0);
            jsonObject.put("serviceType", "Verify_Account");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("senderName", userName.getText().toString());
            jsonObject.put("IFSC", ifsc_code);
            jsonObject.put("accountNo", accountNumber.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("mobileNumber", list.get(0).getMobilno());
            jsonObject.put("txnAmmount", "1");
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject addAgentBankDetails() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "ADD_AGENT_BANK_DETAILS");
            jsonObject.put("requestType", "CRNF_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("agentName", userName.getText().toString());
            jsonObject.put("agentBankName", bank_select.getText().toString());
            jsonObject.put("agentBankIFSC", ifsc_text.getText().toString());
            jsonObject.put("bankAccountNO", accountNumber.getText().toString());
            jsonObject.put("confirmAccountNo", accountNumber.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentAccountType", pageType);
            jsonObject.put("txnIP", ImageUtils.ipAddress(getActivity()));
            jsonObject.put("verificationTxnId", transactionID);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            jsonObject.put("cancelChequeImg", imageBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getAgentBankDetails() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_AGENT_BANK_DETAILS");
            jsonObject.put("requestType", "CRNF_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("txnIP", ImageUtils.ipAddress(getActivity()));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object, String hitFrom) {
        try {
            if (object.has("apiCommonResposne")) {
                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                String balance = object1.getString("runningBalance");
//                heading.setText("BC Fund Transfer (Balance : Rs." + format(balance) + ")");
            }
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_SERVICE_FEE")) {
                    PMTBenefPozo pozo = new PMTBenefPozo(accountNumber.getText().toString(), bank_select.getText().toString());
                    customDialog_Common("Fund Transfer Confirmation", object, pozo, "Are you Sure?", "null", userName.getText().toString(), SettlementBankFragment.this);
                } else if (object.getString("serviceType").equalsIgnoreCase("Verify_Account")) {
                    account_verified.setText("Account verified Successfully. Kindly upload Cancel Cheque Image And add settlement Account");
                    account_verified.setVisibility(View.VISIBLE);
                    rv.findViewById(R.id.addbanklayout).setVisibility(View.VISIBLE);
                    rv.findViewById(R.id.btn_ok).setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                } else if (object.getString("serviceType").equalsIgnoreCase("ADD_AGENT_BANK_DETAILS")) {
                    if (hitFrom.equalsIgnoreCase("ADDBANKLIST")) {
                        customDialog_Common("KYCLAYOUTS", null, null, "Bank Details Added", "", object.getString("responseMessage"), SettlementBankFragment.this);
                        clearData();
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_AGENT_BANK_DETAILS")) {
                    if (hitFrom.equalsIgnoreCase("GETBANKLIST")) {
                        if (object.has("objAgentBankDetailsList"))
                            insertLastTransDetails(object.getJSONArray("objAgentBankDetailsList"));
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("DEACTIVATE_AGENT_BANK_DETAILS")) {
                    accountAdded = false;
                    customDialog_Common("KYCLAYOUTS", null, null, "Account Deactivation", "", object.getString("responseMessage"), SettlementBankFragment.this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        accountAdded = false;
        activeAccountList = new ArrayList<>();
        deleteAccountList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (pageType.equalsIgnoreCase(object.getString("accountType")))
                    if (object.getString("accountStatus").equalsIgnoreCase("0") || object.getString("accountStatus").equalsIgnoreCase("1"))
                        activeAccountList.add(new SettlementPozo(object.getString("agentID"), object.getString("agentName"), object.getString("agentBankName"), object.getString("agentBankIFSC"), object.getString("agentAccountNO"), object.getString("accountStatus"), object.getString("accountType"), object.getString("remark"), object.getString("agentAddress"), object.getString("agentBankId")));
                    else
                        deleteAccountList.add(new SettlementPozo(object.getString("agentID"), object.getString("agentName"), object.getString("agentBankName"), object.getString("agentBankIFSC"), object.getString("agentAccountNO"), object.getString("accountStatus"), object.getString("accountType"), object.getString("remark"), object.getString("agentAddress"), object.getString("agentBankId")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activeAccountList.size() != 0) {
            accountAdded = true;
            last_tran_layout.setVisibility(View.VISIBLE);
            activeaccounts.setVisibility(View.VISIBLE);
            initializeTransAdapter(activeAccountList);
        }else {
            last_tran_layout.setVisibility(View.GONE);
            activeaccounts.setVisibility(View.GONE);
        }

        if (deleteAccountList.size() != 0) {
            beneficiary_layout.setVisibility(View.VISIBLE);
            deleteaccounts.setVisibility(View.VISIBLE);
            initializeDeleteAdapter(deleteAccountList);
        }else {
            beneficiary_layout.setVisibility(View.GONE);
            deleteaccounts.setVisibility(View.GONE);
        }
    }

    private void initializeTransAdapter(ArrayList<SettlementPozo> listDelete) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new SettlementPayLoadAdapter(getActivity(), trans_details, listDelete, SettlementBankFragment.this));
    }

    private void initializeDeleteAdapter(ArrayList<SettlementPozo> listDelete) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        beneficiary_details.setLayoutManager(layoutManager);
        beneficiary_details.setAdapter(new SettlementPayloadDeleteAdapter(getActivity(), beneficiary_details, listDelete));
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
            jsonObject.put("reqFor", "BC1");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object, String hitFrom) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                hideKeyboard(getActivity());
                if (userName.getText().toString().isEmpty()) {
                    userName.setError("Please enter mandatory field");
                    userName.requestFocus();
                } else if (bank_select.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select.setError("Please enter mandatory field");
                    bank_select.requestFocus();
                } else if (!ImageUtils.commonAccount(accountNumber.getText().toString(), 5, 30)) {
                    accountNumber.setError("Please enter valid account number.");
                    accountNumber.requestFocus();
                } else {
                    if (pageType.equalsIgnoreCase("S")) {
                        if (!accountAdded)
                            new WalletAsyncMethod(WebConfig.BCRemittanceApp, service_fee("1", "Money_Transfer_Bene").toString(), headerData, SettlementBankFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "SERVICEFEE").execute();
                        else {
                            customDialog_Common("SESSIONEXPIRRED", null, null, "Error", "", "Request For add Settlement Bank Account is Already Pending.", SettlementBankFragment.this);
                        }
                    } else if (pageType.equalsIgnoreCase("P")) {
                        new WalletAsyncMethod(WebConfig.BCRemittanceApp, service_fee("1", "Money_Transfer_Bene").toString(), headerData, SettlementBankFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "SERVICEFEE").execute();
                    }
                }
                break;
            case R.id.btn_addBank:
                hideKeyboard(getActivity());
                if (userName.getText().toString().isEmpty()) {
                    userName.setError("Please enter mandatory field");
                    userName.requestFocus();
                } else if (bank_select.getText().toString().equalsIgnoreCase("Select Bank")) {
                    bank_select.setError("Please enter mandatory field");
                    bank_select.requestFocus();
                } else if (!ImageUtils.commonAccount(accountNumber.getText().toString(), 5, 30)) {
                    accountNumber.setError("Please enter valid account number.");
                    accountNumber.requestFocus();
                } else if (pageType.equalsIgnoreCase("S") && image.getText().toString().isEmpty()) {
                    image.setError("Please Select Image");
                    image.requestFocus();
                } else {
                    new WalletAsyncMethod(WebConfig.CRNF, addAgentBankDetails().toString(), headerData, SettlementBankFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "ADDBANKLIST").execute();
                }
                break;
            case R.id.images:
                selectImage();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageBase64 = "";
        image.setText("");
        if (requestCode == CAMERA_REQUEST) {
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
            if (data != null) {
                Uri uri = data.getData();
                Bitmap thumbnail = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageBase64 = getBytesFromBitmap(addWaterMark(thumbnail));
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                String[] splits = filePath.split("\\/");
                int len = splits.length;
                image.setText(splits[len - 1]);
                image.setError(null);
            }
        }
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("KYCLAYOUTS")) {
            new WalletAsyncMethod(WebConfig.CRNF, getAgentBankDetails().toString(), headerData, SettlementBankFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "GETBANKLIST").execute();
        } else if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
            new WalletAsyncMethod(WebConfig.BCRemittanceApp, verify_Account().toString(), headerData, SettlementBankFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "VERIFYACCOUNT").execute();
        } else if (type.equalsIgnoreCase("DELETEACCOUNT")) {
            new WalletAsyncMethod(WebConfig.CRNF, deactivateAgentBankDetails((SettlementPozo) ob).toString(), headerData, SettlementBankFragment.this, getActivity(), getString(R.string.responseTimeOutTrans), "DEACTIVATEBANKACCOUNT").execute();
        } else if (type.equalsIgnoreCase("SESSIONEXPIRRED")) {
            clearData();
            accountAdded = false;
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    public JSONObject deactivateAgentBankDetails(SettlementPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "DEACTIVATE_AGENT_BANK_DETAILS");
            jsonObject.put("requestType", "CRNF_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("txnIP", ImageUtils.ipAddress(getActivity()));
            jsonObject.put("agentBankId", pozo.getAgentBankId());
            jsonObject.put("agentAccountType", pageType);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void clearData() {
        userName.setText("");
        bank_select.setText("");
        ifsc_text.setText("");
        accountNumber.setText("");
        ifsc_text.setEnabled(true);
        account_verified.setVisibility(View.GONE);
        rv.findViewById(R.id.addbanklayout).setVisibility(View.GONE);
        rv.findViewById(R.id.btn_ok).setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);
        image.setText("Upload Image");
        bank_select.setText("Select Bank");
    }
}
