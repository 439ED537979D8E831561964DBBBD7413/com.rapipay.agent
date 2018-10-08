package com.rapipay.android.rapipay.main_directory.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.ChannelHistoryPozo;
import com.rapipay.android.rapipay.main_directory.Model.CreditHistoryPozo;
import com.rapipay.android.rapipay.main_directory.Model.RapiPayPozo;
import com.rapipay.android.rapipay.main_directory.adapter.ChannelHistoryAdapter;
import com.rapipay.android.rapipay.main_directory.adapter.CreditHistoryAdapter;
import com.rapipay.android.rapipay.main_directory.interfaces.ClickListener;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.main_directory.ChannelHistoryActivity;
import com.rapipay.android.rapipay.main_directory.main_directory.CreditTabPage;
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

public class CreditTransFragment extends Fragment implements RequestHandler, View.OnClickListener {

    AutofitTextView date_text;
    SimpleDateFormat dateFormatter;
    DatePickerDialog pickerDialog;
    RecyclerView trans_details;
    ArrayList<CreditHistoryPozo> transactionPozoArrayList;
    private String headerData;
    View rv;
    protected ArrayList<RapiPayPozo> list;
    protected SimpleDateFormat format;
    protected Date date;
    TextView heading;
    AppCompatButton btn_fund;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.credit_fragment_alyout, container, false);
        initialize(rv);
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        list = BaseCompactActivity.db.getDetails();
        return rv;
    }

    private void initialize(View view) {
        heading = (TextView) view.findViewById(R.id.heading);
        heading.setVisibility(View.GONE);
        date_text = (AutofitTextView) view.findViewById(R.id.date);
        date_text.setOnClickListener(this);
        btn_fund = (AppCompatButton) view.findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        trans_details = (RecyclerView) view.findViewById(R.id.trans_details);
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CreditHistoryPozo pozo = transactionPozoArrayList.get(position);
//                new AsyncPostMethod(WebConfig.WALLETTRANSFER_URL, receipt_request(pozo).toString(), headerData, getActivity()).execute();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date:
                calender();
                break;
            case R.id.btn_fund:
                if (!date_text.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, channel_request().toString(), headerData, CreditTransFragment.this, getActivity()).execute();
                break;
        }
    }

    private void calender() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
            jsonObject.put("serviceType", "CREDIT_FUND_REQ_REPORT");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("fromDate", date_text.getText().toString());
            jsonObject.put("toDate", date_text.getText().toString());
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
                if (object.getString("serviceType").equalsIgnoreCase("CREDIT_FUND_REQ_REPORT")) {
                    if (object.has("creditFundCount"))
                        if (Integer.parseInt(object.getString("creditFundCount")) > 0)
                            insertLastTransDetails(object.getJSONArray("creditFundList"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<CreditHistoryPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new CreditHistoryPozo(object.getString("requestId") , object.getString("bankName"), object.getString("amount"), object.getString("remark") + " / "+ object.getString("transferId") + " / " + object.getString("depositeDate"), object.getString("status")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<CreditHistoryPozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new CreditHistoryAdapter(getActivity(), trans_details, list));
    }

    @Override
    public void chechStat(String object) {

    }
}
