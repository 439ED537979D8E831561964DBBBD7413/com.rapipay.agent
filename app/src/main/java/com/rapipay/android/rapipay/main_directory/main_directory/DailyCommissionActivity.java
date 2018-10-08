package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.CommissionPozo;
import com.rapipay.android.rapipay.main_directory.adapter.DailyAdapter;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DailyCommissionActivity extends BaseCompactActivity implements RequestHandler,View.OnClickListener {

    String type;
    TextView header;
    RecyclerView trans_details;
    ArrayList<CommissionPozo> transactionPozoArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_transfer_layout);
        type = getIntent().getStringExtra("TYPE");
        initialize();
        loadApi();
    }

    private void loadApi() {
        new AsyncPostMethod(WebConfig.PASSBOOK_URL, getNetwork_Validate().toString(), headerData, DailyCommissionActivity.this).execute();
    }

    private void initialize() {
        header = (TextView) findViewById(R.id.header);
        header.setVisibility(View.VISIBLE);
        if (type.equalsIgnoreCase("D"))
            header.setText("Daily Commission");
        else if (type.equalsIgnoreCase("M"))
            header.setText("Monthly Commission");
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    public JSONObject getNetwork_Validate() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "MY_COMMISION_REPORT");
            jsonObject.put("requestType", "RPT_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
            jsonObject.put("commisionType", type);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
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
                if (object.getString("serviceType").equalsIgnoreCase("MY_COMMISION_REPORT")) {
                    if (object.has("myCommisionList")) {
                        if (Integer.parseInt(object.getString("commisionCount")) > 0) {
                            insertLastTransDetails(object.getJSONArray("myCommisionList"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<CommissionPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new CommissionPozo(object.getString("transactionType"), object.getString("transactionAmount")+ " / " + object.getString("crDrAmount") + " "+ object.getString("crDrType"),object.getString("transactionID") + " / "+ object.getString("transactionDate"), object.getString("transactionStatus")+ " / " + object.getString("settleStatus")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<CommissionPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new DailyAdapter(this, trans_details, list));
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
    public void chechStat(String object) {

    }
}
