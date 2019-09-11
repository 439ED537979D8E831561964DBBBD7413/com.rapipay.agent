package com.rapipay.android.agent.main_directory;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.NetworkHistoryPozo;
import com.rapipay.android.agent.Model.NetworkTransHistPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.NetworkHistoryAdapter;
import com.rapipay.android.agent.adapter.NetworkTranHistAdapter;
import com.rapipay.android.agent.fragments.NetworkHistoryFragment;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import me.grantland.widget.AutofitTextView;

public class NetworkTransHistory extends BaseCompactActivity implements RequestHandler, View.OnClickListener, CustomInterface {

    AutofitTextView date1_text, date2_text;
    ListView trans_details;
    ArrayList<NetworkHistoryPozo> transactionPozoArrayList;
    TextView heading;
    ImageView btn_fund;
    private int selectedDate, selectedMonth, selectedYear;
    String months = null, dayss = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.networkhislayout);
        initialize();
    }

    private void initialize() {
        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Network Transfer History");
        findViewById(R.id.heading).setVisibility(View.GONE);
        date2_text = (AutofitTextView) findViewById(R.id.date2);
        date1_text = (AutofitTextView) findViewById(R.id.date1);
        date1_text.setOnClickListener(this);
        date2_text.setOnClickListener(this);
        date2_text.setText(selectedYear + "-" + selectedMonth + "-" + "01");
        date1_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        btn_fund = (ImageView) findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        trans_details = (ListView) findViewById(R.id.trans_details);
        trans_details.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        findViewById(R.id.todate).setOnClickListener(toDateClicked);
        findViewById(R.id.date1).setOnClickListener(toDateClicked);
        findViewById(R.id.fromdate).setOnClickListener(fromDateClicked);
        findViewById(R.id.date2).setOnClickListener(fromDateClicked);
        ImageView toimage = (ImageView) findViewById(R.id.toimage);
        toimage.setOnClickListener(toDateClicked);
        toimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        ImageView fromimage = (ImageView) findViewById(R.id.fromimage);
        fromimage.setOnClickListener(fromDateClicked);
        fromimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString())))
            new AsyncPostMethod(WebConfig.CommonReport, channel_request().toString(), headerData, NetworkTransHistory.this, getString(R.string.responseTimeOut)).execute();
    }

    protected View.OnClickListener fromDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(NetworkTransHistory.this);
            dialog.setContentView(R.layout.datepickerview);
            dialog.setTitle("");

            DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
            selectedMonth = calendar.get(Calendar.MONTH);
            selectedYear = calendar.get(Calendar.YEAR);
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                    if (String.valueOf(month + 1).length() == 1)
                        months = "0" + String.valueOf(month + 1);
                    else
                        months = String.valueOf(month + 1);
                    if (String.valueOf(dayOfMonth).length() == 1)
                        dayss = "0" + String.valueOf(dayOfMonth);
                    else
                        dayss = String.valueOf(dayOfMonth);
                    if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                        date2_text.setText(year + "-" + months + "-" + dayss);
                        dialog.dismiss();
                    } else {

                        if (selectedDate != dayOfMonth) {
                            date2_text.setText(year + "-" + months + "-" + dayss);
                            dialog.dismiss();
                        } else {
                            if (selectedMonth != month) {
                                date2_text.setText(year + "-" + months + "-" + dayss);
                                dialog.dismiss();
                            }
                        }
                    }
                    selectedDate = dayOfMonth;
                    selectedMonth = (month);
                    selectedYear = year;
                }
            });
            dialog.show();
        }
    };

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
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
                    date2_text.setError("Please enter valid data");
                    date2_text.requestFocus();
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter valid data");
                    date1_text.requestFocus();
                } else if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString()))) {
                    trans_details.setVisibility(View.VISIBLE);
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request().toString(), headerData, NetworkTransHistory.this, getString(R.string.responseTimeOut)).execute();
                } else {
                    customDialog_Common("Statement can only view from one month");
                    trans_details.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        btn_fund.setClickable(true);
        super.onPause();
    }

    View.OnClickListener toDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(NetworkTransHistory.this);
            dialog.setContentView(R.layout.datepickerview);
            dialog.setTitle("");
            DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
            selectedMonth = calendar.get(Calendar.MONTH);
            selectedYear = calendar.get(Calendar.YEAR);
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                    if (String.valueOf(month + 1).length() == 1)
                        months = "0" + String.valueOf(month + 1);
                    else
                        months = String.valueOf(month + 1);
                    if (String.valueOf(dayOfMonth).length() == 1)
                        dayss = "0" + String.valueOf(dayOfMonth);
                    else
                        dayss = String.valueOf(dayOfMonth);
                    if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                        date1_text.setText(year + "-" + months + "-" + dayss);
                        dialog.dismiss();
                    } else {

                        if (selectedDate != dayOfMonth) {
                            date1_text.setText(year + "-" + months + "-" + dayss);
                            dialog.dismiss();
                        } else {
                            if (selectedMonth != month) {
                                date1_text.setText(year + "-" + months + "-" + dayss);
                                dialog.dismiss();
                            }
                        }
                    }
                    selectedDate = dayOfMonth;
                    selectedMonth = (month);
                    selectedYear = year;
                }
            });
            dialog.show();
        }
    };

    public JSONObject channel_request() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "NETWORK_TRANSFER_HISTORY");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("fromTxnDate", date2_text.getText().toString());
            jsonObject.put("toTxnDate", date1_text.getText().toString());
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
                if (object.getString("serviceType").equalsIgnoreCase("NETWORK_TRANSFER_HISTORY")) {
                    if (object.has("netTransferHistList"))
                        if (object.isNull("netTransferHistList") && object.getJSONArray("netTransferHistList").length() == 0)
                            customDialog_Common("No Record Found");
                        else {
                            insertLastTransDetails(object.getJSONArray("netTransferHistList"));
                            trans_details.setVisibility(View.VISIBLE);
                        }

                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(this,object.getString("responseMessage"),Toast.LENGTH_LONG).show();
                setBack_click1(this);
            } else {
                responseMSg(object);
                trans_details.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<NetworkHistoryPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new NetworkHistoryPozo(object.getString("requestID"), object.getString("requestAmount"), object.getString("crDrAmount"), object.getString("createdOn"), object.getString("sysRemarks"), object.getString("requestType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<NetworkHistoryPozo> list) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(NetworkTransHistory.this, LinearLayoutManager.VERTICAL, false);
//        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new NetworkHistoryAdapter(list, NetworkTransHistory.this));
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
