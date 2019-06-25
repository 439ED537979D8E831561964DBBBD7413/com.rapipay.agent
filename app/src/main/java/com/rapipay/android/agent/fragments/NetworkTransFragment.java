package com.rapipay.android.agent.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.NetworkManagePozo;
import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.NetworkAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.main_directory.LoginScreenActivity;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class NetworkTransFragment extends BaseFragment implements RequestHandler, CustomInterface {
    private int first = 1, last = 25;
    private boolean isLoading;
    ListView trans_details;
    LinearLayout last_tran_layout;
    TextView header;
    ArrayList<NetworkTransferPozo> transactionPozoArrayList;
    String clickedId = "0";
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    ArrayList<NetworkManagePozo> logList = new ArrayList<NetworkManagePozo>();
    View rv;
    protected ArrayList<RapiPayPozo> list;
    NetworkAdapter adapter;
    NetworkTransferPozo pozoClick;
    EditText searchfield;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.network_fragment_layout, container, false);
        initialize(rv);
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi()){
            list = BaseCompactActivity.dbRealm.getDetails();
            loadApi();
        } else
            dbNull(NetworkTransFragment.this);
        return rv;
    }

    private void loadApi() {
        logList.add(new NetworkManagePozo(list.get(0).getMobilno(), list.get(0).getMobilno()));
        new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate("GET_MY_NODE_DETAILS", list.get(0).getMobilno(), first, last).toString(), headerData, NetworkTransFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
        else if (type.equalsIgnoreCase("KYCLAYOUT"))
            loadApi();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    private void initialize(View rv) {
        header = (TextView) rv.findViewById(R.id.header);
        header.setVisibility(View.GONE);
        last_tran_layout = (LinearLayout) rv.findViewById(R.id.last_tran_layout);
        trans_details = (ListView) rv.findViewById(R.id.trans_details);
        searchfield = (EditText) rv.findViewById(R.id.searchfield);
        trans_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (btnstatus == false) {
                    btnstatus = true;
                    pozoClick = transactionPozoArrayList.get(position);
                    customDialog_Ben(transactionPozoArrayList.get(position), "Network Transfer", "BENLAYOUT", pozoClick.getConsentStatus(), "Credit To Network");
                }handlercontrol();
            }
        });
        trans_details.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (totalItemCount != 0 && totalItemCount == last && lastInScreen == totalItemCount && !isLoading) {
                    first = last + 1;
                    last += 25;
                    new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate("GET_MY_NODE_DETAILS", list.get(0).getMobilno(), first, last).toString(), headerData, NetworkTransFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                    isLoading = true;
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
                if (s.toString().length() != 0)
                    adapter.filter(s.toString());
                else if (s.toString().length() == 0)
                    adapter.filter("");
            }
        });
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_MY_NODE_DETAILS")) {
                    if (object.has("objAgentNodeList")) {
                        if (Integer.parseInt(object.getString("agentCount")) > 0) {
                            insertLastTransDetails(object.getJSONArray("objAgentNodeList"));
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("C2C_NETWORK_CREDIT")) {
                    customDialog_Ben(null, object.getString("responseMessage"), "NETWORK_CREDIT", null, "Credit Confirmation");
                } else if (object.getString("serviceType").equalsIgnoreCase("C2C_NETWORK_PULL_FUND")) {
                    customDialog_Common("KYCLAYOUT", object, null, "Successfull", null, object.getString("responseMessage"), NetworkTransFragment.this);
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_NODE_HEADER_DATA")) {
                    if (object.has("headerList")) {
                        if (Integer.parseInt(object.getString("listCount")) > 0) {
                            insertLastTransDetailsNode(object.getJSONArray("headerList"));
                        }
                    }
                }
            }else
                responseMSg(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetailsNode(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("headerValue").equalsIgnoreCase("My Balance")) {
                    String data = object.getString("headerData");
                    if (formatss(data) == null || formatss(data).equalsIgnoreCase("0")) {
                        Toast.makeText(getActivity(), "Not Authorized to create New User!.", Toast.LENGTH_SHORT).show();
                    } else
                        customDialogConfirm(pozoClick, "Are you sure you want to Transfer?", "CONFIRMATION", textsss.getText().toString(), "", "Credit Confirmation", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<NetworkTransferPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (!logList.get(logList.size() - 1).getCuurentNo().equalsIgnoreCase(object.getString("mobileNo")))
                    transactionPozoArrayList.add(new NetworkTransferPozo(object.getString("companyName"), object.getString("mobileNo"), object.getString("agentName"), object.getString("agentBalance"), object.getString("agentCategory"), object.getString("consentStatus")));
                else {
                    header.setVisibility(View.GONE);
                    header.setText(object.getString("agentName") + " - " + object.getString("mobileNo") + "\n" + object.getString("agentCategory"));
                    last_tran_layout.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<NetworkTransferPozo> list) {
        if (first == 1) {
            adapter = new NetworkAdapter(list, getActivity());
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
    }

    public JSONObject getNetwork_Validate(String servicetype, String mobileNo, int fromIndex, int toIndex) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentMobile", mobileNo);
            jsonObject.put("fromIndex", String.valueOf(fromIndex));
            jsonObject.put("toIndex", String.valueOf(toIndex));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getReverseTransfer(String mobileno, String remark) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "C2C_NETWORK_PULL_FUND");
            jsonObject.put("requestType", "CRNF_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentPayer", mobileno);
            jsonObject.put("agentPayee", list.get(0).getMobilno());
            jsonObject.put("requestFrom", "B2B");
            if (remark.isEmpty())
                jsonObject.put("remark", "NA");
            else
                jsonObject.put("remark", remark);
            jsonObject.put("txnAmmount", textsss.getText().toString());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    //    AlertDialog alertDialog, alertDialogs;
    TextView textsss;
    EditText newtpinss;
    EditText remarks;

    private void customDialog_BenNEW(final NetworkTransferPozo pozo, String msg, final String type, String amount, String title) {
        AutofitTextView btn_p_bank, btn_name, p_transid;
        dialognew1 = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        AppCompatButton btn_regenerate = (AppCompatButton) alertLayout.findViewById(R.id.btn_regenerate);
        if (type.equalsIgnoreCase("AMOUNTTRANSFER")) {
            alertLayout.findViewById(R.id.custom_popup).setVisibility(View.VISIBLE);
            btn_name = (AutofitTextView) alertLayout.findViewById(R.id.btn_name_popup);
            p_transid = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_transid);
            btn_p_bank = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_bank);
            remarks = (EditText) alertLayout.findViewById(R.id.remarks);
            remarks.setVisibility(View.VISIBLE);
            newtpinss = (EditText) alertLayout.findViewById(R.id.newtpinss);
            if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpinss.getText().toString().isEmpty() || newtpinss.getText().toString().length() != 4))
                newtpinss.setVisibility(View.VISIBLE);
            btn_name.setText("Company Name : " + pozo.getCompanyName());
            p_transid.setText(pozo.getAgentName() + " - " + pozo.getMobileNo());
            btn_p_bank.setText("Current Balance : " + formatss(pozo.getAgentBalance()));
        } else if (type.equalsIgnoreCase("REVERSETRANSFER")) {
            alertLayout.findViewById(R.id.custom_popup).setVisibility(View.VISIBLE);
            btn_name = (AutofitTextView) alertLayout.findViewById(R.id.btn_name_popup);
            p_transid = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_transid);
            btn_p_bank = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_bank);
            remarks = (EditText) alertLayout.findViewById(R.id.remarks);
            remarks.setVisibility(View.VISIBLE);
            btn_name.setText("Company Name : " + pozo.getCompanyName());
            p_transid.setText(pozo.getAgentName() + " - " + pozo.getMobileNo());
            btn_p_bank.setText("Current Balance : " + formatss(pozo.getAgentBalance()));
        } else if (type.equalsIgnoreCase("NETWORK_CREDIT")) {
            btn_cancel.setVisibility(View.GONE);
            TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            otpView.setText(msg);
            otpView.setVisibility(View.VISIBLE);
        }
        textsss = (TextView) alertLayout.findViewById(R.id.input_amount_popup);
        final TextView input_text = (TextView) alertLayout.findViewById(R.id.input_textss);
        textsss.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()!=0 && s.length()<10) {
                    input_text.setText("");
                    input_text.setText(EnglishNumberToWords.convert(Integer.parseInt(s.toString()))+" rupee");
                    input_text.setVisibility(View.VISIBLE);
                }else
                    input_text.setVisibility(View.GONE);
            }
        });
        dialognew1.setContentView(alertLayout);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialognew1.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    if (type.equalsIgnoreCase("AMOUNTTRANSFER")) {
                        hideKeyboard(getActivity());
                        if (!ImageUtils.commonAmount(textsss.getText().toString())) {
                            textsss.setError("Please enter valid data");
                            textsss.requestFocus();
                        } else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpinss.getText().toString().isEmpty() || newtpinss.getText().toString().length() != 4)) {
                            newtpinss.setError("Please enter valid data");
                            newtpinss.requestFocus();
                        } else {
                            dialognew1.dismiss();
                            customDialogConfirm(pozo, "Are you sure you want to Transfer?", "CONFIRMATION", textsss.getText().toString(), newtpinss.getText().toString(), "Credit Confirmation", remarks.getText().toString());
                        }
                    }
                    if (type.equalsIgnoreCase("REVERSETRANSFER")) {
                        hideKeyboard(getActivity());
                        if (!ImageUtils.commonAmount(textsss.getText().toString())) {
                            textsss.setError("Please enter valid data");
                            textsss.requestFocus();
                        } else {
                            dialognew1.dismiss();
                            new AsyncPostMethod(WebConfig.CRNF, getReverseTransfer(pozo.getMobileNo(), remarks.getText().toString()).toString(), headerData, NetworkTransFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                        }
                    } else if (type.equalsIgnoreCase("NETWORK_CREDIT")) {
                        loadApi();
                        dialognew1.dismiss();
                    }
                }
                handlercontrol();
            }
        });
        dialognew1.show();
        btn_regenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognew1.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognew1.dismiss();
            }
        });
        Window window = dialognew1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void customDialog_Ben(final NetworkTransferPozo pozo, String msg, final String type, String amount, String title) {
        AutofitTextView btn_p_bank, btn_name, p_transid;
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        AppCompatButton btn_regenerate = (AppCompatButton) alertLayout.findViewById(R.id.btn_regenerate);
        if (type.equalsIgnoreCase("BENLAYOUT")) {
            if (amount.equalsIgnoreCase("N"))
                btn_cancel.setVisibility(View.GONE);
            btn_cancel.setText("Reverse transfer");
            btn_regenerate.setText("Cancel");
            btn_regenerate.setTextSize(10);
            btn_regenerate.setVisibility(View.VISIBLE);
            btn_cancel.setTextSize(10);
            btn_ok.setText("Fund Transfer");
            btn_ok.setTextSize(10);
            dialog.setContentView(alertLayout);
        } else if (type.equalsIgnoreCase("AMOUNTTRANSFER")) {
            alertLayout.findViewById(R.id.custom_popup).setVisibility(View.VISIBLE);
            btn_name = (AutofitTextView) alertLayout.findViewById(R.id.btn_name_popup);
            p_transid = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_transid);
            btn_p_bank = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_bank);
            remarks = (EditText) alertLayout.findViewById(R.id.remarks);
            remarks.setVisibility(View.VISIBLE);
            newtpinss = (EditText) alertLayout.findViewById(R.id.newtpinss);
            if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpinss.getText().toString().isEmpty() || newtpinss.getText().toString().length() != 4))
                newtpinss.setVisibility(View.VISIBLE);
            btn_name.setText("Company Name : " + pozo.getCompanyName());
            p_transid.setText(pozo.getAgentName() + " - " + pozo.getMobileNo());
            btn_p_bank.setText("Current Balance : " + formatss(pozo.getAgentBalance()));
        } else if (type.equalsIgnoreCase("REVERSETRANSFER")) {
            alertLayout.findViewById(R.id.custom_popup).setVisibility(View.VISIBLE);
            btn_name = (AutofitTextView) alertLayout.findViewById(R.id.btn_name_popup);
            p_transid = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_transid);
            btn_p_bank = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_bank);
            remarks = (EditText) alertLayout.findViewById(R.id.remarks);
            remarks.setVisibility(View.VISIBLE);
            btn_name.setText("Company Name : " + pozo.getCompanyName());
            p_transid.setText(pozo.getAgentName() + " - " + pozo.getMobileNo());
            btn_p_bank.setText("Current Balance : " + formatss(pozo.getAgentBalance()));
        } else if (type.equalsIgnoreCase("NETWORK_CREDIT")) {
            btn_cancel.setVisibility(View.GONE);
            TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            otpView.setText(msg);
            otpView.setVisibility(View.VISIBLE);
        }
        textsss = (TextView) alertLayout.findViewById(R.id.input_amount_popup);
        dialog.setContentView(alertLayout);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    if (type.equalsIgnoreCase("AMOUNTTRANSFER")) {
                        hideKeyboard(getActivity());
                        if (!ImageUtils.commonAmount(textsss.getText().toString())) {
                            textsss.setError("Please enter valid data");
                            textsss.requestFocus();
                        } else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpinss.getText().toString().isEmpty() || newtpinss.getText().toString().length() != 4)) {
                            newtpinss.setError("Please enter valid data");
                            newtpinss.requestFocus();
                        } else {
                            dialog.dismiss();
                            customDialogConfirm(pozo, "Are you sure you want to Transfer?", "CONFIRMATION", textsss.getText().toString(), newtpinss.getText().toString(), "Credit Confirmation", remarks.getText().toString());
                        }
                    }
                    if (type.equalsIgnoreCase("REVERSETRANSFER")) {
                        hideKeyboard(getActivity());
                        if (!ImageUtils.commonAmount(textsss.getText().toString())) {
                            textsss.setError("Please enter valid data");
                            textsss.requestFocus();
                        } else {
                            dialog.dismiss();
                            new AsyncPostMethod(WebConfig.CRNF, getReverseTransfer(pozo.getMobileNo(), remarks.getText().toString()).toString(), headerData, NetworkTransFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                        }
                    } else if (type.equalsIgnoreCase("NETWORK_CREDIT")) {
                        loadApi();
                        dialog.dismiss();
                    } else if (type.equalsIgnoreCase("BENLAYOUT")) {
                        dialog.dismiss();
                        customDialog_BenNEW(pozo, "Network Transfer", "AMOUNTTRANSFER", "", "Credit To Network");
                    }
                }
                handlercontrol();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    if (type.equalsIgnoreCase("BENLAYOUT")) {
                        dialog.dismiss();
                        customDialog_BenNEW(pozo, "Network Transfer", "REVERSETRANSFER", "", "Credit To Network");
                    } else
                        dialog.dismiss();
                }
                handlercontrol();
            }
        });
        dialog.show();
        btn_regenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void customDialogConfirm(final NetworkTransferPozo pozo, String msg,
                                     final String type, final String amount, final String tpin, String title, final String remarks) {
        dialognew = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("CONFIRMATION")) {
            TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            otpView.setText(msg);
            otpView.setVisibility(View.VISIBLE);
        }
        dialognew.setContentView(alertLayout);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialognew.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("CONFIRMATION")) {
                    dialognew.dismiss();
                    new AsyncPostMethod(WebConfig.CRNF, getNetwork_Transfer(pozo.getMobileNo(), amount, tpin, remarks).toString(), headerData, NetworkTransFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognew.dismiss();
            }
        });
        dialognew.show();
        Window window = dialognew.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public JSONObject getNetwork_Transfer(String receiverId, String txnAmount, String newtpin, String remark) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "C2C_NETWORK_CREDIT");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentSenderID", list.get(0).getMobilno());
            jsonObject.put("agentReciverID", receiverId);
            if (remark.isEmpty())
                jsonObject.put("remark", "NA");
            else
                jsonObject.put("remark", remark);
            jsonObject.put("txnAmount", txnAmount);
            if (newtpin.isEmpty())
                jsonObject.put("tPin", "");
            else
                jsonObject.put("tPin", ImageUtils.encodeSHA256(newtpin));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void url() {
        new AsyncPostMethod(WebConfig.COMMONAPI, getDashBoard("GET_NODE_HEADER_DATA").toString(), headerData, getActivity(), getString(R.string.responseTimeOut)).execute();
    }

    public JSONObject getDashBoard(String servicetype) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected String formatss(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void jumpPage() {
        Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        deleteTables("ALL");
    }

    protected void dbNull(CustomInterface customInterface) {
        customDialog_Common("SESSIONEXPIRE", null, null, "Session Expired", null, "Your current session will get expired.", customInterface);
    }
}


