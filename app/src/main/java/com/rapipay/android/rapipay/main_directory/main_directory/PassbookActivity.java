package com.rapipay.android.rapipay.main_directory.main_directory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.ChannelHistoryPozo;
import com.rapipay.android.rapipay.main_directory.Model.PassbookPozo;
import com.rapipay.android.rapipay.main_directory.adapter.ChannelHistoryAdapter;
import com.rapipay.android.rapipay.main_directory.adapter.PassbookAdapter;
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

public class PassbookActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler {
    AutofitTextView date2_text, date1_text;
    SimpleDateFormat dateFormatter;
    DatePickerDialog pickerDialog;
    RecyclerView trans_details;
    TextView heading;
    ArrayList<PassbookPozo> transactionPozoArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passbook_layout);
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.heading);
        heading.setText("Ledger History");
        date2_text = (AutofitTextView) findViewById(R.id.date2);
        date1_text = (AutofitTextView) findViewById(R.id.date1);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.date1:
                calender(date1_text);
                break;
            case R.id.date2:
                calender(date2_text);
                break;
            case R.id.btn_fund:
                if (!date1_text.getText().toString().isEmpty() && !date2_text.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.PASSBOOK_URL, channel_request().toString(), headerData, PassbookActivity.this).execute();
                break;
        }
    }

    private void calender(final TextView textview) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                textview.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    public JSONObject channel_request() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_LEADGER_DETAILS");
            jsonObject.put("requestType", "RPT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
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
                transactionPozoArrayList.add(new PassbookPozo(object.getString("serviceName"), object.getString("txnAmount")+ " / " + object.getString("crDrAmount") + " "+ object.getString("crDrType"),object.getString("txnDate"), object.getString("openingBalance")+ " / " + object.getString("closingBalance"), object.getString("transactionStatus")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<PassbookPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new PassbookAdapter(this, trans_details, list));
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

