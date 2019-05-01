package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.PassbookPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.PassbookAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class PassbookActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler {

    ListView trans_details;
    private boolean isLoading;
    TextView heading;
    ArrayList<PassbookPozo> transactionPozoArrayList;
    private int first = 1, last = 25;
    PassbookAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passbook_layout);
        initialize();
    }

    private void initialize() {
        TYPE = getIntent().getStringExtra("TYPE");
        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH)+1;
        selectedYear = calendar.get(Calendar.YEAR);
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Agent Ledger History");
        date2_text = (AutofitTextView) findViewById(R.id.date2);
        date1_text = (AutofitTextView) findViewById(R.id.date1);
        trans_details = (ListView) findViewById(R.id.trans_details);
        findViewById(R.id.todate).setOnClickListener(toDateClicked);
        findViewById(R.id.date1).setOnClickListener(toDateClicked);
        findViewById(R.id.fromdate).setOnClickListener(fromDateClicked);
        findViewById(R.id.date2).setOnClickListener(fromDateClicked);
        toimage = (ImageView)findViewById(R.id.toimage);
        toimage.setOnClickListener(toDateClicked);
        toimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        fromimage = (ImageView)findViewById(R.id.fromimage);
        fromimage.setOnClickListener(fromDateClicked);
        fromimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        if (TYPE.equalsIgnoreCase("NODE")) {
            date2_text.setText(selectedYear + "-" + selectedMonth + "-" + "01");
            date1_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
            if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString())))
                new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, PassbookActivity.this, getString(R.string.responseTimeOut)).execute();
        }else {
            date2_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
            date1_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        }
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
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, PassbookActivity.this, getString(R.string.responseTimeOut), "TRANSACTIONHISTORY").execute();
                    isLoading = true;
                }
            }
        });
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
                    date2_text.requestFocus();
                }else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter mandatory field");
                    date1_text.requestFocus();
                } else if (printDifference(mainDate(date2_text.getText().toString()),mainDate(date1_text.getText().toString())))
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, PassbookActivity.this,getString(R.string.responseTimeOut)).execute();
                else
                    Toast.makeText(PassbookActivity.this,"Please select correct date",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public JSONObject channel_request(int fromIndex, int toIndex) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_LEADGER_DETAILS");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("fromDate", date2_text.getText().toString());
            jsonObject.put("toDate", date1_text.getText().toString());
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
                if (object.getString("serviceType").equalsIgnoreCase("GET_LEADGER_DETAILS")) {
                    if (object.has("leadgerCount"))
                        if (Integer.parseInt(object.getString("leadgerCount")) > 0)
                            insertLastTransDetails(object.getJSONArray("leadgerList"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<PassbookPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new PassbookPozo(object.getString("txnDate"),object.getString("serviceName"), formatss(object.getString("txnAmount")) + " / " + formatss(object.getString("crDrAmount")) + " " + object.getString("crDrType"), object.getString("txnDate"), formatss(object.getString("openingBalance")) + " / " + formatss(object.getString("closingBalance")), object.getString("transactionStatus")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<PassbookPozo> list) {
        if (first == 1) {
            adapter = new PassbookAdapter(PassbookActivity.this, list);
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        trans_details.setLayoutManager(layoutManager);
//        trans_details.setAdapter(new PassbookAdapter(PassbookActivity.this, list));
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void chechStat(String object) {

    }

    private String formatss(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

