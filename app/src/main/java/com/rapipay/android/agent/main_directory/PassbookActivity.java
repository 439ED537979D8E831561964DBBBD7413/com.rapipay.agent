package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.Model.ChannelHistoryPozo;
import com.rapipay.android.agent.Model.PassbookPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.PassbookAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.WebConfig;

public class PassbookActivity extends BaseCompactActivity implements View.OnClickListener, RequestHandler {

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
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("Agent Ledger History");
        date2_text = (AutofitTextView) findViewById(R.id.date2);
        date1_text = (AutofitTextView) findViewById(R.id.date1);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
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
    }
//    String months,dayss;
//    private void picDate() {
//        months=null;dayss=null;
//        findViewById(R.id.date1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // custom dialog
//                final Dialog dialog = new Dialog(PassbookActivity.this);
//                dialog.setContentView(R.layout.datepickerview);
//                dialog.setTitle("");
//
//                DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
//                selectedMonth = calendar.get(Calendar.MONTH);
//                selectedYear = calendar.get(Calendar.YEAR);
//                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
//
//                    @Override
//                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
//                        Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
//                        if(String.valueOf(month+1).length()==1)
//                            months = "0"+ String.valueOf(month+1);
//                        else
//                            months = String.valueOf(month+1);
//                        if(String.valueOf(dayOfMonth).length()==1)
//                            dayss = "0"+String.valueOf(dayOfMonth);
//                        else
//                            dayss = String.valueOf(dayOfMonth);
//                        if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
//                            date1_text.setText(year + "-" + months + "-" + dayss);
//                            dialog.dismiss();
//                        } else {
//
//                            if (selectedDate != dayOfMonth) {
//                                date1_text.setText(year + "-" + months + "-" + dayss);
//                                dialog.dismiss();
//                            } else {
//                                if (selectedMonth != month) {
//                                    date1_text.setText(year + "-" + months + "-" + dayss);
//                                    dialog.dismiss();
//                                }
//                            }
//                        }
//                        selectedDate = dayOfMonth;
//                        selectedMonth = (month);
//                        selectedYear = year;
//                    }
//                });
//                dialog.show();
//            }
//        });
//    }
//
//    private void picDate1() {
//        months=null;dayss=null;
//        findViewById(R.id.date2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // custom dialog
//                final Dialog dialog = new Dialog(PassbookActivity.this);
//                dialog.setContentView(R.layout.datepickerview);
//                dialog.setTitle("");
//
//                DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
//                selectedMonth = calendar.get(Calendar.MONTH);
//                selectedYear = calendar.get(Calendar.YEAR);
//                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
//
//                    @Override
//                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
//                        Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
//                        if(String.valueOf(month+1).length()==1)
//                            months = "0"+ String.valueOf(month+1);
//                        else
//                            months = String.valueOf(month+1);
//                        if(String.valueOf(dayOfMonth).length()==1)
//                            dayss = "0"+String.valueOf(dayOfMonth);
//                        else
//                            dayss = String.valueOf(dayOfMonth);
//                        if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
//                            date2_text.setText(year + "-" + months + "-" + dayss);
//                            dialog.dismiss();
//                        } else {
//
//                            if (selectedDate != dayOfMonth) {
//                                date2_text.setText(year + "-" + months + "-" + dayss);
//                                dialog.dismiss();
//                            } else {
//                                if (selectedMonth != month) {
//                                    date2_text.setText(year + "-" + months + "-" + dayss);
//                                    dialog.dismiss();
//                                }
//                            }
//                        }
//                        selectedDate = dayOfMonth;
//                        selectedMonth = (month);
//                        selectedYear = year;
//                    }
//                });
//                dialog.show();
//            }
//        });
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
                    date2_text.requestFocus();
                }else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter mandatory field");
                    date1_text.requestFocus();
                }else if (!date1_text.getText().toString().isEmpty() && !date2_text.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(0,5).toString(), headerData, PassbookActivity.this).execute();
                break;
        }
    }

//    private void calender(final TextView textview) {
//        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        Calendar newCalendar = Calendar.getInstance();
//        pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//                textview.setText(dateFormatter.format(newDate.getTime()));
//            }
//        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//        pickerDialog.show();
//    }

    public JSONObject channel_request(int fromIndex, int toIndex) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_LEADGER_DETAILS");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "GLD" + tsLong.toString());
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

    public JSONObject receipt_request(ChannelHistoryPozo pozo) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "Get_Txn_Recipt");
            jsonObject.put("requestType", pozo.getTransferType());
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", "GTR" + tsLong.toString());
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
                transactionPozoArrayList.add(new PassbookPozo(object.getString("serviceName"), formatss(object.getString("txnAmount")) + " / " + formatss(object.getString("crDrAmount")) + " " + object.getString("crDrType"), object.getString("txnDate"), formatss(object.getString("openingBalance")) + " / " + formatss(object.getString("closingBalance")), object.getString("transactionStatus")));
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

