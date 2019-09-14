package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.ChannelHistoryPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.ChannelListAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import me.grantland.widget.AutofitTextView;

public class ChannelHistoryActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {

    private boolean isLoading;
    ListView trans_details;
    ImageView btn_fund;
    ArrayList<ChannelHistoryPozo> transactionPozoArrayList = new ArrayList<ChannelHistoryPozo>();
    private int first = 1, last = 25;
    ChannelListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_history_layout);
        initialize();
        TYPE = getIntent().getStringExtra("TYPE");
        if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString())))
            new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, ChannelHistoryActivity.this, getString(R.string.responseTimeOut), "TRANSACTIONHISTORY").execute();
    }

    private void initialize() {
        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Transaction History");
        btn_fund = (ImageView) findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        date2_text = (AutofitTextView) findViewById(R.id.date2);
        date1_text = (AutofitTextView) findViewById(R.id.date1);
        if (selectedMonth < 11)
            date2_text.setText(selectedYear + "-" + "0"+selectedMonth + "-" + selectedDate);
        else
            date2_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        if (selectedMonth < 11)
            date1_text.setText(selectedYear + "-" + "0"+selectedMonth + "-" + selectedDate);
        else
            date1_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        trans_details = (ListView) findViewById(R.id.trans_details);
        trans_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trans_details.setEnabled(false);
                ChannelHistoryPozo pozo = transactionPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.WALLETRECEIPTURL, receipt_request(pozo).toString(), headerData, ChannelHistoryActivity.this, getString(R.string.responseTimeOut), "RECEIPTREQUEST").execute();
            }
        });
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
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, ChannelHistoryActivity.this, getString(R.string.responseTimeOut), "TRANSACTIONHISTORY").execute();
                    isLoading = true;
                }
            }
        });
    }

    public void clickable() {
        trans_details.setEnabled(true);
        btn_fund.setClickable(true);
    }

    @Override
    protected void onPause() {
        clickable();
        super.onPause();
    }

    public JSONObject receipt_request(ChannelHistoryPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Get_Txn_Recipt");
            jsonObject.put("requestType", "DMT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("orgTxnRef", pozo.getOrgTxnid());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("routeType", pozo.getTransferType());
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
                btn_fund.setClickable(false);
                if (date2_text.getText().toString().isEmpty()) {
                    date2_text.setError("Please enter mandatory field");
                    Toast.makeText(this, "Please enter mandatory field", Toast.LENGTH_SHORT).show();
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter mandatory field");
                    Toast.makeText(this, "Please enter mandatory field", Toast.LENGTH_SHORT).show();
                } else if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString()))) {
                    trans_details.setVisibility(View.VISIBLE);
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, ChannelHistoryActivity.this, getString(R.string.responseTimeOut), "TRANSACTIONHISTORY").execute();
                } else {
                    customDialog_Common("Statement can only view from one month");
                    trans_details.setVisibility(View.GONE);
                    btn_fund.setClickable(true);
                }
                break;
        }
    }

    public JSONObject channel_request(int fromIndex, int toIndex) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_CHANNEL_TXN_HISTORY");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("fromTxnDate", date2_text.getText().toString());
            jsonObject.put("toTxnDate", date1_text.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            if (TYPE.equalsIgnoreCase("NODE"))
                jsonObject.put("operationFlag", "ALL");
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
                if (object.has("getTxnHistory")) {
                    insertLastTransDetails(object.getJSONArray("getTxnHistory"));
                } else if (object.getString("serviceType").equalsIgnoreCase("Get_Txn_Recipt")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNewTransaction("Transaction Receipt", object, ChannelHistoryActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "Transaction Receipt", "", "Cannot generate receipt now please try later!", ChannelHistoryActivity.this);
                        }
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(this,object.getString("responseMessage"),Toast.LENGTH_LONG).show();
                setBack_click1(this);
            } else {
                responseMSg(object);
                trans_details.setVisibility(View.GONE);
            }
            trans_details.setEnabled(true);
            btn_fund.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 2) {
                dialog.dismiss();
            }
        } else {
            if (dialog != null)
                dialog.dismiss();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        try {
            transactionPozoArrayList.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new ChannelHistoryPozo(object.getString("senderName") + " ( " + object.getString("mobileNo") + " )", object.getString("accountNo") + " ( " + object.getString("bankName") + " )", object.getString("requestAmt"), object.getString("txnStatus"), object.getString("txnDateTime"), object.getString("serviceProviderTXNID"), object.getString("transferType"), object.getString("userTxnId"), object.getString("serviceType"), object.getString("txnDateTime")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<ChannelHistoryPozo> list) {
        if (first == 1) {
            trans_details.setVisibility(View.VISIBLE);
            adapter = new ChannelListAdapter(list, ChannelHistoryActivity.this);
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
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
