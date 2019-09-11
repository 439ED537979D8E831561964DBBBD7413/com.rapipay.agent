package com.rapipay.android.agent.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;

public class GenerateTPINFragment extends BaseFragment implements View.OnClickListener, RequestHandler {

    View rv;
    EditText enterpin, confirmpin;
    AppCompatButton btn_login;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.tpin_layout, container, false);
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        initialize(rv);
        return rv;
    }

    private void initialize(View rv) {
        enterpin = (EditText) rv.findViewById(R.id.input_user);
        enterpin.setHint("Enter New TPIN");
        confirmpin = (EditText) rv.findViewById(R.id.input_password);
        confirmpin.setHint("Confirm New TPIN");
        btn_login = (AppCompatButton) rv.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                btn_login.setClickable(false);
                if (enterpin.length() < 4) {
                    enterpin.setError("Please enter 4 digin pin");
                    enterpin.requestFocus();
                    btn_login.setClickable(true);
                } else if (confirmpin.length() < 4) {
                    confirmpin.setError("Please enter 4 digin pin");
                    confirmpin.requestFocus();
                    btn_login.setClickable(true);
                } else if (!enterpin.getText().toString().equalsIgnoreCase(confirmpin.getText().toString())) {
                    confirmpin.setError("TPIN Not matched");
                    confirmpin.requestFocus();
                    btn_login.setClickable(true);
                } else
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", GenerateTPINFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                break;
        }
    }

    public JSONObject getJson_Validate() {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "Insert_Txn_Pin");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", ImageUtils.miliSeconds());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("confirmTxnPin", ImageUtils.encodeSHA256(confirmpin.getText().toString()));
                jsonObject.put("txnIP", ImageUtils.ipAddress(getActivity()));
                jsonObject.put("resetStatus", "Y");
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public JSONObject getVerifyOTP(String otp, String otpRefId) {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "VERIFY_TXN_PIN_OTP");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", ImageUtils.miliSeconds());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("otp", otp);
                jsonObject.put("otpRefId", otpRefId);
                jsonObject.put("txnIP", ImageUtils.ipAddress(getActivity()));
                jsonObject.put("resetStatus", "Y");
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("Insert_Txn_Pin")) {
                    customDialog_Ben("OTPLAYOUT", object.getString("otpRefId"), "Enter OTP");
                } else if (object.getString("serviceType").equalsIgnoreCase("VERIFY_TXN_PIN_OTP")) {
                    dialog.dismiss();
                    customDialog_Ben("", object.getString("responseMessage"), "Alert");
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(getActivity(),object.getString("responseMessage"),Toast.LENGTH_LONG).show();
                setBack_click1(getActivity());
            } else
                responseMSg(object);
            btn_login.setClickable(true);
            btn_ok.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickable(){
        try {
            btn_ok.setClickable(true);
            btn_login.setClickable(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        clickable();
        super.onPause();
    }

    AppCompatButton btn_ok;
    private void customDialog_Ben(final String type, final String msg, String title) {
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
      //  btn_cancel.setVisibility(View.GONE);
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        final TextView otpView = (TextView) alertLayout.findViewById(R.id.input_otp);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        if (type.equalsIgnoreCase("OTPLAYOUT")) {
            alertLayout.findViewById(R.id.otp_layout).setVisibility(View.VISIBLE);
            otpView.setInputType(InputType.TYPE_CLASS_NUMBER);
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(12);
            otpView.setFilters(filterArray);
        } else {
            TextView dialog_msg = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            dialog_msg.setText(msg);
            dialog_msg.setVisibility(View.VISIBLE);
        }
        dialog.setContentView(alertLayout);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ok.setClickable(false);
                if (type.equalsIgnoreCase("OTPLAYOUT")) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getVerifyOTP(otpView.getText().toString(), msg).toString(), "", GenerateTPINFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                } else {
                    enterpin.setText("");
                    confirmpin.setText("");
                    enterpin.setHint("Enter New TPIN");
                    confirmpin.setHint("Confirm New TPIN");
                    dialog.dismiss();
                    btn_ok.setClickable(true);
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
