package com.rapipay.android.agent.fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChangeMobileFragment extends BaseFragment implements RequestHandler, View.OnClickListener {
    protected ArrayList<RapiPayPozo> list;
    EditText newmobileno, oldotp, newotp;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    LinearLayout old_number, new_number;
    AppCompatButton sub_btn, sub_btn_oldotp, sub_btn_newotp;
    String otpRefId = null, orgTxnId = null;
    protected LocalStorage localStorage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.change_number_layout, container, false);
        localStorage = LocalStorage.getInstance(getActivity());
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        initialize(rv);
        return rv;
    }

    private void initialize(View view) {
        newmobileno = (EditText) view.findViewById(R.id.newmobileno);
        oldotp = (EditText) view.findViewById(R.id.oldotp);
        newotp = (EditText) view.findViewById(R.id.newotp);
        old_number = (LinearLayout) view.findViewById(R.id.old_number);
        new_number = (LinearLayout) view.findViewById(R.id.new_number);
        sub_btn = (AppCompatButton) view.findViewById(R.id.sub_btn);
        sub_btn.setOnClickListener(this);
        sub_btn_oldotp = (AppCompatButton) view.findViewById(R.id.sub_btn_oldotp);
        sub_btn_oldotp.setOnClickListener(this);
        sub_btn_newotp = (AppCompatButton) view.findViewById(R.id.sub_btn_newotp);
        sub_btn_newotp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.sub_btn:
                if (btnstatus == false) {
                    btnstatus = true;
                    if (!ImageUtils.commonNumber(newmobileno.getText().toString(), 10)) {
                        newmobileno.setError("Please enter valid data");
                        newmobileno.requestFocus();
                    } else
                        new AsyncPostMethod(WebConfig.LOGIN_URL, request_user().toString(), headerData, ChangeMobileFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                }
                handlercontrol();
                break;
            case R.id.sub_btn_oldotp:
                if (btnstatus == false) {
                    btnstatus = true;
                    if (!ImageUtils.commonNumber(oldotp.getText().toString(), 6)) {
                        oldotp.setError("Please enter valid data");
                        oldotp.requestFocus();
                    } else
                        new AsyncPostMethod(WebConfig.LOGIN_URL, oldRequest_user().toString(), headerData, ChangeMobileFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                }
                handlercontrol();
                break;
            case R.id.sub_btn_newotp:
                if (btnstatus == false) {
                    btnstatus = true;
                    if (!ImageUtils.commonNumber(newotp.getText().toString(), 6)) {
                        newotp.setError("Please enter valid data");
                        newotp.requestFocus();
                    } else
                        new AsyncPostMethod(WebConfig.LOGIN_URL, newRequest_user().toString(), headerData, ChangeMobileFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                }
                handlercontrol();
                break;
        }
    }

    public JSONObject request_user() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "CHANGE_MOBILE_NO");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("requestType", "HANDSET_CHANNEL");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("newMobileNo", newmobileno.getText().toString());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject oldRequest_user() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "CHANGE_VERIFY_OTP");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("requestType", "HANDSET_CHANNEL");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("otp", oldotp.getText().toString());
            jsonObject.put("otpRefId", otpRefId);
            jsonObject.put("orgTxnRef", orgTxnId);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject newRequest_user() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "VERIFY_CHNAGE_MOBILE_OTP");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("requestType", "HANDSET_CHANNEL");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("newMobileNo", newmobileno.getText().toString());
            jsonObject.put("otp", newotp.getText().toString());
            jsonObject.put("otpRefId", otpRefId);
            jsonObject.put("orgTxnRef", orgTxnId);
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
                if (object.getString("serviceType").equalsIgnoreCase("CHANGE_MOBILE_NO")) {
                    newmobileno.setEnabled(false);
                    sub_btn.setVisibility(View.GONE);
                    old_number.setVisibility(View.VISIBLE);
                    if (object.has("otpRefId"))
                        otpRefId = object.getString("otpRefId");
                    if (object.has("transactionId"))
                        orgTxnId = object.getString("transactionId");
                } else if (object.getString("serviceType").equalsIgnoreCase("CHANGE_VERIFY_OTP")) {
                    newmobileno.setEnabled(false);
                    old_number.setVisibility(View.GONE);
                    new_number.setVisibility(View.VISIBLE);
                    if (object.has("otpRefId"))
                        otpRefId = object.getString("otpRefId");
                    if (object.has("transactionId"))
                        orgTxnId = object.getString("transactionId");
                } else if (object.getString("serviceType").equalsIgnoreCase("VERIFY_CHNAGE_MOBILE_OTP")) {
                    newmobileno.setText("");
                    newmobileno.setEnabled(true);
                    oldotp.setText("");
                    newotp.setText("");
                    new_number.setVisibility(View.GONE);
                    old_number.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), object.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    new RouteClass(getActivity(), null, "", localStorage, "0");
                }
            }else
                responseMSg(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chechStat(String object) {

    }
}
