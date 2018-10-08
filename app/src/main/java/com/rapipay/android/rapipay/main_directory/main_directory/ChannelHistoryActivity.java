package com.rapipay.android.rapipay.main_directory.main_directory;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.ChannelHistoryPozo;
import com.rapipay.android.rapipay.main_directory.adapter.ChannelHistoryAdapter;
import com.rapipay.android.rapipay.main_directory.interfaces.ClickListener;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.RecyclerTouchListener;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class ChannelHistoryActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler {

    AutofitTextView date_text,datess;
    SimpleDateFormat dateFormatter;
    DatePickerDialog pickerDialog;
    RecyclerView trans_details;
    AppCompatButton btn_fund;
    ArrayList<ChannelHistoryPozo> transactionPozoArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_history_layout);
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.heading);
        if (balance != null)
            heading.setText("Channel Transfer ( My Balance - " + balance + " )");
        else
            heading.setText("Channel Transfer");
        datess = (AutofitTextView)findViewById(R.id.date);
        btn_fund = (AppCompatButton)findViewById(R.id.btn_fund);
        datess.setOnClickListener(this);
        btn_fund.setOnClickListener(this);
        date_text = (AutofitTextView) findViewById(R.id.date);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(this, trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChannelHistoryPozo pozo = transactionPozoArrayList.get(position);
                new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, receipt_request(pozo).toString(), headerData, ChannelHistoryActivity.this).execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.date:
                calender();
                break;
            case R.id.btn_fund:
                if (!date_text.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, channel_request().toString(), headerData, ChannelHistoryActivity.this).execute();
                break;
        }
    }
    private void calender() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_text.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    public JSONObject channel_request() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_CHANNEL_TXN_HISTORY");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnId", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("fromTxnDate", date_text.getText().toString());
            jsonObject.put("toTxnDate", date_text.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject receipt_request(ChannelHistoryPozo pozo) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Get_Txn_Recipt");
            jsonObject.put("requestType", pozo.getTransferType());
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("txnDate", pozo.getDate_id());
            jsonObject.put("orgTxnRef", pozo.getOrgTxnid());
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
                if (object.has("getTxnHistory")) {
                    insertLastTransDetails(object.getJSONArray("getTxnHistory"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<ChannelHistoryPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                    transactionPozoArrayList.add(new ChannelHistoryPozo(object.getString("senderName") + " ( " + object.getString("mobileNo")+" )", object.getString("accountNo") + " ( "+object.getString("bankName")+" )", object.getString("requestAmt") + " ( "+object.getString("serviceProviderTXNID")+" )", object.getString("userTxnId") + " ( "+object.getString("txnStatus")+" )", object.getString("txnDateTime"), object.getString("userTxnId"), object.getString("transferType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<ChannelHistoryPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new ChannelHistoryAdapter(this, trans_details, list));
    }
    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void chechStat(String object) {

    }

}
