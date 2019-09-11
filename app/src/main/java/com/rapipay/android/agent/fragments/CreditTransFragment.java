package com.rapipay.android.agent.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.CreditHistoryPozo;
import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.CreditHistoryAdapter;
import com.rapipay.android.agent.interfaces.ClickListener;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.RecyclerTouchListener;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.grantland.widget.AutofitTextView;

public class CreditTransFragment extends BaseFragment implements RequestHandler, View.OnClickListener, CustomInterface {
    AutofitTextView date1_text, date2_text;
    RecyclerView trans_details;
    ArrayList<CreditHistoryPozo> transactionPozoArrayList;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    View rv;
    protected ArrayList<RapiPayPozo> list;
    TextView heading;
    ImageView btn_fund;
    private int selectedDate, selectedMonth, selectedYear;
    String months = null, dayss = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.credit_fragment_alyout, container, false);
        initialize(rv);
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        else
            dbNull(CreditTransFragment.this);
        return rv;
    }

    private void initialize(View view) {
        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);
        heading = (TextView) view.findViewById(R.id.heading);
        heading.setVisibility(View.GONE);
        date2_text = (AutofitTextView) view.findViewById(R.id.date2);
        date1_text = (AutofitTextView) view.findViewById(R.id.date1);
        date1_text.setOnClickListener(this);
        date2_text.setOnClickListener(this);
        date2_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        date1_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        btn_fund = (ImageView) view.findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        trans_details = (RecyclerView) view.findViewById(R.id.trans_details);
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CreditHistoryPozo pozo = transactionPozoArrayList.get(position);
                if (setClickable(view, isClickable) == false) {
                    isClickable = true;
                } else {
                    isClickable = false;
                    setClickable(view, isClickable);
                    if (pozo.getStatus().equalsIgnoreCase("PENDING"))
                        customDialog_Ben(transactionPozoArrayList.get(position), "Are you sure, you want to De-activate?", "BENLAYOUT", pozo.getStatus(), "Credit History");
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        rv.findViewById(R.id.todate).setOnClickListener(toDateClicked);
        rv.findViewById(R.id.date1).setOnClickListener(toDateClicked);
        rv.findViewById(R.id.fromdate).setOnClickListener(fromDateClicked);
        rv.findViewById(R.id.date2).setOnClickListener(fromDateClicked);
        ImageView toimage = (ImageView) rv.findViewById(R.id.toimage);
        toimage.setOnClickListener(toDateClicked);
        toimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        ImageView fromimage = (ImageView) rv.findViewById(R.id.fromimage);
        fromimage.setOnClickListener(fromDateClicked);
        fromimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
    }

    public boolean isClickable = false;

    public static boolean setClickable(View view, boolean clickable) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setClickable(viewGroup.getChildAt(i), clickable);
                }
            }
            view.setClickable(clickable);
        }
        return clickable;
    }


    AppCompatButton btn_cancel, btn_ok, btn_regenerate;

    private void customDialog_Ben(final CreditHistoryPozo pozo, String msg, final String type, String amount, String title) {
        AutofitTextView btn_p_bank, btn_name, p_transid;
        final EditText newtpinss;
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        //   trans_details.setEnabled(false);
        btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        LinearLayout custom_popup = (LinearLayout) alertLayout.findViewById(R.id.custom_fun);
        custom_popup.setVisibility(View.VISIBLE);
        otpView.setText(msg);
        otpView.setVisibility(View.VISIBLE);
        newtpinss = (EditText) alertLayout.findViewById(R.id.newtpinss1);
        btn_cancel.setText("Cancel");
        btn_cancel.setTextSize(10);
        btn_ok.setText("Deactivate");
        btn_ok.setTextSize(10);
        dialog.setContentView(alertLayout);
        dialog.setContentView(alertLayout);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ok.setClickable(false);
                if (pozo.getStatus().equalsIgnoreCase("PENDING")) {
                    hideKeyboard(getActivity());
                    if (!newtpinss.getText().toString().isEmpty()) {
                        dialog.dismiss();
                        btn_ok.setClickable(true);
                        new AsyncPostMethod(WebConfig.CREDITREQUESTHISTRPT, pending_request(pozo, newtpinss.getText().toString()).toString(), headerData, CreditTransFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                    } else {
                        newtpinss.setError("Please enter remarks");
                        btn_ok.setClickable(true);
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected View.OnClickListener fromDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getActivity());
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

    public void listApiCall() {
        new AsyncPostMethod(WebConfig.CREDITREQUESTHISTRPT, channel_request().toString(), headerData, CreditTransFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fund:
                if (date2_text.getText().toString().isEmpty()) {
                    date2_text.setError("Please enter valid data");
                    date2_text.requestFocus();
                    btn_fund.setClickable(true);
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter valid data");
                    date1_text.requestFocus();
                    btn_fund.setClickable(true);
                } else if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString()))) {
                    //  new AsyncPostMethod(WebConfig.CommonReport, channel_request().toString(), headerData, CreditTransFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                    listApiCall();
                    trans_details.setVisibility(View.VISIBLE);
                } else {
                    trans_details.setVisibility(View.GONE);
                    customDialog_Common("Statement Can Only View For One Month");
                    btn_fund.setClickable(true);
                }
                break;
        }
    }

    View.OnClickListener toDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getActivity());
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
            jsonObject.put("serviceType", "CREDIT_FUND_REQ_REPORT");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("fromDate", date2_text.getText().toString());
            jsonObject.put("toDate", date1_text.getText().toString());
            jsonObject.put("downloadOrView", 'V');
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject pending_request(final CreditHistoryPozo pozo, String remarks) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "REJECT_MY_CREDIT_REQ");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("requestID", pozo.getRequestId());
            jsonObject.put("remarks", remarks);
            jsonObject.put("status", "REJECT");
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
                    if (object.has("creditFundList"))
                        // if (Integer.parseInt(object.getString("creditFundList")) > 0)
                        insertLastTransDetails(object.getJSONArray("creditFundList"));
                } else {
                    listApiCall();
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(getActivity(),object.getString("responseMessage"),Toast.LENGTH_LONG).show();
                setBack_click1(getActivity());
            } else
                responseMSg(object);
            btn_fund.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<CreditHistoryPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new CreditHistoryPozo(object.getString("requestId"), object.getString("amount"), object.getString("bankTxnId"), object.getString("requestType"), object.getString("transactionDate"),
                        object.getString("remark"),
                        object.getString("status"), i));
            }
            if (array.length() == 1) {
                customDialog_Common("No record found");
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

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
