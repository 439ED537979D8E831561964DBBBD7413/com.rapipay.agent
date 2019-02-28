package com.rapipay.android.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;

import org.json.JSONObject;

import java.util.ArrayList;

public class PayloadBankFragment extends BaseFragment implements RequestHandler, View.OnClickListener {
    private View rv = null;
    EditText ifsc_text,userName,accountNumber;
    String pageType;
    TextView bank_select;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.settlement_layout, container, false);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        String strtext=getArguments().getString("message");
        initialize(rv);
        return rv;
    }
    private void initialize(View rv){
        ifsc_text = (EditText) rv.findViewById(R.id.input_ifsc);
        userName = (EditText) rv.findViewById(R.id.input_username);
        accountNumber = (EditText) rv.findViewById(R.id.input_accountNo);
        bank_select = (TextView) rv.findViewById(R.id.bank_select);
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String condition = "where " + RapipayDB.COLOMN_CREDITBANK + "='Y'";
                ArrayList<String> list_bank = BaseCompactActivity.db.geBankDetails(condition);
                customSpinner(bank_select, "Select Bank", list_bank,ifsc_text);
            }
        });
    }

    @Override
    public void chechStatus(JSONObject object) {

    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void onClick(View v) {

    }
}
