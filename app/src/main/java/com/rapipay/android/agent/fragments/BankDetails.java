package com.rapipay.android.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Model.BankDetailPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BankDetailAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BankDetails extends BaseFragment implements RequestHandler {

    ArrayList<BankDetailPozo> detailPozoArrayList;
    RecyclerView recycler_view;
    TextView note1, note2, heading;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.bankdetail_layout, container, false);
        localStorage = LocalStorage.getInstance(getActivity());
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        initialize(rv);
        url();
        return rv;
    }

    private void initialize(View view) {
        heading = (TextView) view.findViewById(R.id.toolbar_title);
        heading.setText("Bank Details for Deposit");
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        note1 = (TextView) view.findViewById(R.id.note1);
        note2 = (TextView) view.findViewById(R.id.note2);
    }

    private void url() {
        new AsyncPostMethod(WebConfig.LOGIN_URL, getWLDetails().toString(), headerData, BankDetails.this, getActivity(), getString(R.string.responseTimeOut)).execute();
    }


    public JSONObject getWLDetails() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "WL_DOMAIN_DETAILS");
                jsonObject.put("requestType", "HANDSET_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("timeStamp", format.format(date));
                jsonObject.put("appType", BuildConfig.USERTYPE);
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Blank Value", Toast.LENGTH_SHORT).show();
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
                if (object.getString("serviceType").equalsIgnoreCase("WL_DOMAIN_DETAILS")) {
                    byte[] data = Base64.decode(object.getString("smsVirtualCode"), Base64.DEFAULT);
                    String text = new String(data, "UTF-8");
                    parseBankDetails(new JSONObject(text));

                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(getActivity(), object.getString("responseCode"), Toast.LENGTH_LONG).show();
                setBack_click1(getActivity());
            } else {
                responseMSg(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseBankDetails(JSONObject objects) {
        detailPozoArrayList = new ArrayList<>();
        try {
            if (objects.has("NOTE1"))
                note1.setText(" Note1 : " + objects.getString("NOTE1"));
            if (objects.has("NOTE2"))
                note2.setText(" Note2 : " + objects.getString("NOTE2"));
            JSONArray array = objects.getJSONArray("BANK_DETAILS");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("ECOL").equalsIgnoreCase("Y"))
                    detailPozoArrayList.add(new BankDetailPozo(object.getString("BANK"), object.getString("NAME"), object.getString("AC NO.") + list.get(0).getMobilno(), object.getString("BRANCH"), object.getString("IFSC"), object.getString("DEPOSIT"), object.getString("ECOL")));
                else
                    detailPozoArrayList.add(new BankDetailPozo(object.getString("BANK"), object.getString("NAME"), object.getString("AC NO."), object.getString("BRANCH"), object.getString("IFSC"), object.getString("DEPOSIT"), object.getString("ECOL")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (detailPozoArrayList.size() != 0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recycler_view.setLayoutManager(layoutManager);
            recycler_view.setAdapter(new BankDetailAdapter(getActivity(), detailPozoArrayList));
        }

    }

}
