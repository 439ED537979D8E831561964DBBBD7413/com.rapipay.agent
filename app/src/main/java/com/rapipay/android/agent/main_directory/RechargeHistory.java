package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.RechargePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.RechargeHistoryAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;

public class RechargeHistory extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {

    RecyclerView trans_details;
    TextView heading;
    Spinner select_state;
    ArrayList<String> list_state;
    private String payee;
    ArrayList<RechargePozo> transactionPozoArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passbook_layout);
        initialize();
    }


    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    private void initialize() {
        select_state = (Spinner) findViewById(R.id.select_state);
        select_state.setVisibility(View.VISIBLE);
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Recharge History");
        date2_text = (AutofitTextView) findViewById(R.id.date2);
        date1_text = (AutofitTextView) findViewById(R.id.date1);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
        findViewById(R.id.todate).setOnClickListener(toDateClicked);
        findViewById(R.id.date1).setOnClickListener(toDateClicked);
        findViewById(R.id.fromdate).setOnClickListener(fromDateClicked);
        findViewById(R.id.date2).setOnClickListener(fromDateClicked);
        toimage = (ImageView) findViewById(R.id.toimage);
        toimage.setOnClickListener(toDateClicked);
        toimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        fromimage = (ImageView) findViewById(R.id.fromimage);
        fromimage.setOnClickListener(fromDateClicked);
        fromimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        String condition = "Select distinct(" + RapipayDB.COLOMN_OPERATORVALUE + ") " + "FROM " + RapipayDB.TABLE_OPERATOR + " Group by " + RapipayDB.COLOMN_OPERATORVALUE;
        list_state = BaseCompactActivity.db.getOperatorProvider(condition);
        if (list_state.size() != 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RechargeHistory.this,
                    android.R.layout.simple_spinner_item, list_state);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_state.setAdapter(dataAdapter);
        }
        select_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    payee = list_state.get(position);
                else
                    payee = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(this, trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                RechargePozo pozo = transactionPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, receipt_request(pozo).toString(), headerData, RechargeHistory.this).execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public JSONObject receipt_request(RechargePozo pozo) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Get_Txn_Recipt");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", "GTRt" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("orgTxnRef", pozo.getTransactionID());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("routeType", "BBPS");
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_fund:
                if (payee.isEmpty())
                    Toast.makeText(this, "Please select recharge type", Toast.LENGTH_SHORT).show();
                else if (date2_text.getText().toString().isEmpty()) {
                    date2_text.setError("Please enter mandatory field");
                    date2_text.requestFocus();
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter mandatory field");
                    date1_text.requestFocus();
                } else
                    new AsyncPostMethod(WebConfig.RECHARGE_URL, channel_request(0, 5).toString(), headerData, RechargeHistory.this).execute();
                break;
        }
    }

    public JSONObject channel_request(int fromIndex, int toIndex) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "RECHARGE_HISTORY");
            jsonObject.put("requestType", "UBP_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "RH" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("fromDate", date2_text.getText().toString());
            jsonObject.put("toDate", date1_text.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("fromIndex", String.valueOf(fromIndex));
            jsonObject.put("toIndex", String.valueOf(toIndex));
            jsonObject.put("rechargeType", payee);
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
                if (object.getString("serviceType").equalsIgnoreCase("RECHARGE_HISTORY")) {
                    if (object.has("rechargeHistoryList")) {
                        insertLastTransDetails(object.getJSONArray("rechargeHistoryList"));
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("Get_Txn_Recipt")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNew("Transaction Receipt", object, RechargeHistory.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "Transaction Receipt", "", "Cannot generate receipt now please try later!", RechargeHistory.this);
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<RechargePozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new RechargePozo(object.getString("operatorName"), object.getString("rechargeType"), object.getString("mobileNo"), object.getString("txnAmount"), object.getString("transactionID"), object.getString("txnStatus")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<RechargePozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(RechargeHistory.this, LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new RechargeHistoryAdapter(RechargeHistory.this, trans_details, list));
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void okClicked(String type, Object ob) {

    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
