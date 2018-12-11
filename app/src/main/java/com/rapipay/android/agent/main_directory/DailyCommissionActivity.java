package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.rapipay.android.agent.Model.CommissionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.DailyAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.WebConfig;

public class DailyCommissionActivity extends BaseCompactActivity implements RequestHandler,View.OnClickListener {

    String type;
    TextView header;
    private boolean isLoading;
    ListView trans_details;
    ArrayList<CommissionPozo> transactionPozoArrayList;
    private int first = 1, last = 25;
    private DailyAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_transfer_layout);
        type = getIntent().getStringExtra("TYPE");
        initialize();
        loadApi();
    }

    private void loadApi() {
        new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate(first, last).toString(), headerData, DailyCommissionActivity.this).execute();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        header = (TextView) findViewById(R.id.header);
        header.setVisibility(View.GONE);
        if (type.equalsIgnoreCase("D"))
            heading.setText("Daily Commission");
        else if (type.equalsIgnoreCase("M"))
            heading.setText("Monthly Commission");
        trans_details = (ListView) findViewById(R.id.trans_details);
        trans_details.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (totalItemCount != 0 && totalItemCount == last && lastInScreen == totalItemCount && !isLoading) {
                    first = last + 1;
                    last += 25;
                    new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate(first, last).toString(), headerData, DailyCommissionActivity.this).execute();
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    public JSONObject getNetwork_Validate(int fromIndex, int toIndex) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "MY_COMMISION_REPORT");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "MCR"+tsLong.toString());
            jsonObject.put("commisionType", type);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("fromIndex", String.valueOf(fromIndex));
            jsonObject.put("toIndex", String.valueOf(toIndex));
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
        if (first == 1) {
            adapter = new DailyAdapter(this, list);
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        trans_details.setLayoutManager(layoutManager);
//        trans_details.setAdapter(new DailyAdapter(this, trans_details, list));
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
