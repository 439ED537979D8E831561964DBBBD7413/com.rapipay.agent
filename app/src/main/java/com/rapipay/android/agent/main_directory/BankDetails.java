package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Model.BankDetailPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BankDetailAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BankDetails extends BaseCompactActivity implements RequestHandler, View.OnClickListener {

    ArrayList<BankDetailPozo> detailPozoArrayList;
    RecyclerView recycler_view;
    TextView note1, note2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bankdetail_layout);
        initialize();
        url();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Bank Details for Deposit");
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        note1 = (TextView) findViewById(R.id.note1);
        note2 = (TextView) findViewById(R.id.note2);
    }

    private void url() {
        new AsyncPostMethod(WebConfig.LOGIN_URL, getWLDetails().toString(), headerData, BankDetails.this, getString(R.string.responseTimeOut)).execute();
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
            Toast.makeText(this, "Blank Value", Toast.LENGTH_SHORT).show();
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
                    detailPozoArrayList.add(new BankDetailPozo(object.getString("BANK"), object.getString("NAME"), object.getString("AC NO.")+list.get(0).getMobilno(), object.getString("BRANCH"), object.getString("IFSC"), object.getString("DEPOSIT"), object.getString("ECOL")));
                else
                    detailPozoArrayList.add(new BankDetailPozo(object.getString("BANK"), object.getString("NAME"), object.getString("AC NO."), object.getString("BRANCH"), object.getString("IFSC"), object.getString("DEPOSIT"), object.getString("ECOL")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (detailPozoArrayList.size() != 0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(BankDetails.this, LinearLayoutManager.VERTICAL, false);
            recycler_view.setLayoutManager(layoutManager);
            recycler_view.setAdapter(new BankDetailAdapter(BankDetails.this, detailPozoArrayList));
        }

    }

}
