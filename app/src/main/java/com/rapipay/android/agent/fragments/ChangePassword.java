package com.rapipay.android.agent.fragments;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

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

public class ChangePassword extends BaseFragment implements RequestHandler, View.OnClickListener {

    View rv;
    protected ArrayList<RapiPayPozo> list;
    AppCompatButton btn_login;
    protected LocalStorage localStorage;
    TextInputEditText pinView, otppinView, confirmpinView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.changepassword_layout, container, false);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        localStorage = LocalStorage.getInstance(getActivity());
        initialize(rv);
        return rv;
    }

    private void initialize(View view) {
        btn_login = (AppCompatButton) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        pinView = (TextInputEditText) view.findViewById(R.id.pinView);
        otppinView = (TextInputEditText) view.findViewById(R.id.otppinView);
        confirmpinView = (TextInputEditText) view.findViewById(R.id.confirmpinView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (otppinView.getText().toString().isEmpty()) {
                    otppinView.setError("Please enter current password");
                    otppinView.requestFocus();
                } else if (pinView.getText().toString().isEmpty()) {
                    pinView.setError("Please enter new password");
                    pinView.requestFocus();
                } else if (confirmpinView.getText().toString().isEmpty()) {
                    confirmpinView.setError("Please confirm new password");
                    confirmpinView.requestFocus();
                } else if (!pinView.getText().toString().equalsIgnoreCase(confirmpinView.getText().toString()))
                    pinView.setError("New and Confirm password cannot be different");
                else
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", ChangePassword.this, getActivity(),getString(R.string.responseTimeOut)).execute();
                break;
        }
    }

    public JSONObject getJson_Validate() {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "CHANGE_PASSWORD");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", ImageUtils.miliSeconds());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("oldPassword", otppinView.getText().toString());
                jsonObject.put("newPassword", pinView.getText().toString());
                jsonObject.put("confirmPassword", confirmpinView.getText().toString());
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
                if (object.getString("serviceType").equalsIgnoreCase("CHANGE_PASSWORD")) {
                    customDialog_Ben("Password Change", object.getString("responseMessage"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void customDialog_Ben(String msg, String title) {
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.GONE);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(msg);
        otpView.setVisibility(View.VISIBLE);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialog.setContentView(alertLayout);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTables("forgot");
                new RouteClass(getActivity(), null, "", localStorage, "0");
                dialog.dismiss();
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
    protected void deleteTables(String type) {
        SQLiteDatabase dba = BaseCompactActivity.db.getWritableDatabase();
        dba.execSQL("delete from " + RapipayDB.TABLE_BANK);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYMENT);
        dba.execSQL("delete from " + RapipayDB.TABLE_STATE);
        dba.execSQL("delete from " + RapipayDB.TABLE_OPERATOR);
        dba.execSQL("delete from " + RapipayDB.TABLE_FOOTER);
        dba.execSQL("delete from " + RapipayDB.TABLE_TRANSFERLIST);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYERPAYEE);
        if (!type.equalsIgnoreCase(""))
            dba.execSQL("delete from " + RapipayDB.TABLE_NAME);
    }
}
