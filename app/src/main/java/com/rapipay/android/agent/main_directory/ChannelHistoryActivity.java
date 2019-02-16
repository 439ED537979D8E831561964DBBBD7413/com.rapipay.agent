package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
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
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Transaction History");
        btn_fund = (ImageView) findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        date2_text = (AutofitTextView) findViewById(R.id.date2);
        date1_text = (AutofitTextView) findViewById(R.id.date1);
        trans_details = (ListView) findViewById(R.id.trans_details);
        trans_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChannelHistoryPozo pozo = transactionPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.WALLETRECEIPTURL, receipt_request(pozo).toString(), headerData, ChannelHistoryActivity.this,getString(R.string.responseTimeOut)).execute();
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
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, ChannelHistoryActivity.this,getString(R.string.responseTimeOut)).execute();
                    isLoading = true;
                }
            }
        });
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
                if (date2_text.getText().toString().isEmpty()) {
                    date2_text.setError("Please enter mandatory field");
                    Toast.makeText(this, "Please enter mandatory field", Toast.LENGTH_SHORT).show();
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter mandatory field");
                    Toast.makeText(this, "Please enter mandatory field", Toast.LENGTH_SHORT).show();
                } else if (!date1_text.getText().toString().isEmpty() && !date2_text.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, ChannelHistoryActivity.this,getString(R.string.responseTimeOut)).execute();
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
                            customDialog_Common("KYCLAYOUTS",null,null,"Transaction Receipt","","Cannot generate receipt now please try later!", ChannelHistoryActivity.this);
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new ChannelHistoryPozo(object.getString("senderName") + " ( " + object.getString("mobileNo") + " )", object.getString("accountNo") + " ( " + object.getString("bankName") + " )", object.getString("requestAmt"), object.getString("txnStatus"), object.getString("txnDateTime"), object.getString("serviceProviderTXNID"), object.getString("transferType"), object.getString("userTxnId"), object.getString("serviceType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<ChannelHistoryPozo> list) {
        if (first == 1) {
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
