package com.rapipay.android.agent.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.main_directory.PinVerification;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;
import com.rapipay.android.agent.view.PinEntryEditText;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChangePinFragment extends BaseFragment implements RequestHandler, View.OnClickListener {

    View rv;
    protected ArrayList<RapiPayPozo> list;
    AppCompatButton btn_login;
    PinEntryEditText pinView, otppinView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.changepin_layout, container, false);
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        initialize(rv);
        return rv;
    }

    private void initialize(View view) {
        btn_login = (AppCompatButton) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        pinView = (PinEntryEditText) view.findViewById(R.id.pinView);
        otppinView = (PinEntryEditText) view.findViewById(R.id.otppinView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                btn_login.setClickable(false);
                if (otppinView.length() < 6)
                    otppinView.setError("Please enter 6 digin pin");
                else if (pinView.length() < 6)
                    pinView.setError("Please enter 6 digin pin");
                else if (otppinView.getText().toString().equalsIgnoreCase(pinView.getText().toString()))
                    pinView.setError("Old and Current pin cannot be same");
                else
                    new AsyncPostMethod(WebConfig.UAT, getJson_Validate().toString(), "", ChangePinFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
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
                jsonObject.put("serviceType", "ChangePin");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", ImageUtils.miliSeconds());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("oldPin", otppinView.getText().toString());
                jsonObject.put("newPin", pinView.getText().toString());
                jsonObject.put("imeiNo", list.get(0).getImei());
                jsonObject.put("deviceName", Build.MANUFACTURER);
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("osType", "ANDROID");
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
                if (object.getString("serviceType").equalsIgnoreCase("ChangePin")) {
                    customDialog_Ben("Pin Change", object.getString("responseMessage"));
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(getActivity(), object.getString("responseMessage"), Toast.LENGTH_LONG).show();
                setBack_click1(getActivity());
            } else
                responseMSg(object);
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
        otpView.setText(msg);
        otpView.setVisibility(View.VISIBLE);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialog.setView(alertLayout);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PinVerification.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
