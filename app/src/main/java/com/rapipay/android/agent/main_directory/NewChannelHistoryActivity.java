package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.LoadSummaryPozo;
import com.rapipay.android.agent.Model.NewTransactionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.TransactionHistAdapter;
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

public class NewChannelHistoryActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler, CustomInterface {

    private boolean isLoading;
    ListView trans_details;
    ImageView btn_fund;
    ArrayList<LoadSummaryPozo> transactionPozoArrayList;
    private int first = 1, last = 25;
    TransactionHistAdapter adapter;
    EditText search_data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_history_layout);
        initialize();
        TYPE = getIntent().getStringExtra("TYPE");
        if (TYPE.equalsIgnoreCase("NODE"))
            if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString())))
                new AsyncPostMethod(WebConfig.CommonReport, channel_request().toString(), headerData, NewChannelHistoryActivity.this, getString(R.string.responseTimeOut), "TRANSACTIONHISTORY").execute();
    }

    private void initialize() {
        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Load Summary");
        btn_fund = (ImageView) findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        date2_text = (AutofitTextView) findViewById(R.id.date2);
        date1_text = (AutofitTextView) findViewById(R.id.date1);
        date2_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        date1_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        trans_details = (ListView) findViewById(R.id.trans_details);
        search_data = (EditText) findViewById(R.id.search_data);
        search_data.setVisibility(View.VISIBLE);
        search_data.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0)
                    adapter.filter(s.toString());

            }
        });
//        trans_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                NewTransactionPozo pozo = transactionPozoArrayList.get(position);
//                new AsyncPostMethod(WebConfig.WALLETRECEIPTURL, receipt_request(pozo).toString(), headerData, NewChannelHistoryActivity.this, getString(R.string.responseTimeOut), "RECEIPTREQUEST").execute();
//            }
//        });
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
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request().toString(), headerData, NewChannelHistoryActivity.this, getString(R.string.responseTimeOut), "TRANSACTIONHISTORY").execute();
                    isLoading = true;
                }
            }
        });
    }
//
//    public JSONObject receipt_request(NewTransactionPozo pozo) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("serviceType", "Get_Txn_Recipt");
//            jsonObject.put("requestType", "DMT_CHANNEL");
//            jsonObject.put("typeMobileWeb", "mobile");
//            jsonObject.put("txnRef", ImageUtils.miliSeconds());
//            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
//            jsonObject.put("agentId", list.get(0).getMobilno());
//            jsonObject.put("orgTxnRef", pozo.getOrgTxnid());
//            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
//            jsonObject.put("routeType", pozo.getTransferType());
//            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.btn_fund:
                if (date2_text.getText().toString().isEmpty()) {
                    date2_text.setError("Please enter mandatory field");
                    Toast.makeText(this, "Please enter mandatory field", Toast.LENGTH_SHORT).show();
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter mandatory field");
                    Toast.makeText(this, "Please enter mandatory field", Toast.LENGTH_SHORT).show();
                } else if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString())))
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request().toString(), headerData, NewChannelHistoryActivity.this, getString(R.string.responseTimeOut), "TRANSACTIONHISTORY").execute();
                else
                    Toast.makeText(NewChannelHistoryActivity.this, "Please select correct date", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public JSONObject channel_request() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_LOAD_SUMMARY");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("fromDate", date2_text.getText().toString());
            jsonObject.put("toDate", date1_text.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
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
                if (object.getString("serviceType").equalsIgnoreCase("GET_LOAD_SUMMARY")) {
                    if (object.has("getLoadSummaryList")) {
                        insertLastTransDetails(object.getJSONArray("getLoadSummaryList"));
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("Get_Txn_Recipt")) {
                    if (object.has("getTxnReceiptDataList"))
                        try {
                            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
                            customReceiptNewTransaction("Transaction Receipt", object, NewChannelHistoryActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            customDialog_Common("KYCLAYOUTS", null, null, "Transaction Receipt", "", "Cannot generate receipt now please try later!", NewChannelHistoryActivity.this);
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<LoadSummaryPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new LoadSummaryPozo(object.getString("srNo"), object.getString("serviceType"), object.getString("debitAmount"), object.getString("creditAmount")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<LoadSummaryPozo> list) {
        if (first == 1) {
            adapter = new TransactionHistAdapter(list, NewChannelHistoryActivity.this);
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

