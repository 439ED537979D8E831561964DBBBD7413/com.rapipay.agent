package com.rapipay.android.agent.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChangePassword extends Fragment implements RequestHandler, View.OnClickListener {

    View rv;
    protected ArrayList<RapiPayPozo> list;
    protected Long tsLong;
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
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", ChangePassword.this, getActivity()).execute();
                break;
        }
    }

    public JSONObject getJson_Validate() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "CHANGE_PASSWORD");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", "CP" + tsLong.toString());
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
                deleteTables("forgot");
                new RouteClass(getActivity(), null, "", localStorage, "0");
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
