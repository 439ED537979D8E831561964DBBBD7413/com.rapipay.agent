package com.rapipay.android.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class EnableDiablePinFragment extends BaseFragment implements View.OnClickListener, RequestHandler {

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
        confirmpin = (EditText) rv.findViewById(R.id.input_password);
        confirmpin.setVisibility(View.GONE);
        enterpin.setHint("Enter TPIN");
        btn_login = (AppCompatButton) rv.findViewById(R.id.btn_login);
        if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y"))
            btn_login.setText("Disable TPIN");
        else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("N"))
            btn_login.setText("Enable TPIN");
        else
            btn_login.setText("Enable TPIN");
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
                } else
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", EnableDiablePinFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                break;
        }
    }

    @Override
    public void onPause() {
        btn_login.setClickable(true);
        super.onPause();
    }

    public JSONObject getJson_Validate() {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "Txn_PIN_ENABLE");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", ImageUtils.miliSeconds());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("txnPin", ImageUtils.encodeSHA256(enterpin.getText().toString()));
                jsonObject.put("txnIP", ImageUtils.ipAddress(getActivity()));
                jsonObject.put("resetStatus", "Y");
                if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y"))
                    jsonObject.put("txnPinEnable", "N");
                else if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("N"))
                    jsonObject.put("txnPinEnable", "Y");
                else
                    jsonObject.put("txnPinEnable", "Y");
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
                if (object.getString("serviceType").equalsIgnoreCase("Txn_PIN_ENABLE")) {
                    customDialog_Ben("Alert", object.getString("responseMessage"));
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("75120")) {
                if (object.getString("serviceType").equalsIgnoreCase("Txn_PIN_ENABLE")) {
                    customDialog_Ben("Alert", object.getString("responseMessage"));
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(getActivity(), object.getString("responseCode"), Toast.LENGTH_LONG).show();
                setBack_click1(getActivity());
            } else
                responseMSg(object);
            btn_login.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    AlertDialog alertDialog;

    private void customDialog_Ben(String msg, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.GONE);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(title);
        otpView.setVisibility(View.VISIBLE);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(msg);
        dialog.setView(alertLayout);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterpin.setText("");
                enterpin.setHint("New TPIN");
                alertDialog.dismiss();
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
}
