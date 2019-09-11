package com.rapipay.android.agent.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.rapipay.android.agent.Model.NetworkHistoryPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.Model.TbOperatorPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.NetworkHistoryAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import me.grantland.widget.AutofitTextView;

public class NetworkHistoryFragment extends BaseFragment implements RequestHandler, View.OnClickListener, CustomInterface {
    View rv;
    private int first = 1, last = 25;
    private boolean isLoading;
    AutofitTextView date2_text, date1_text;
    ListView trans_details;
    protected ArrayList<RapiPayPozo> list;
    private String payee;
    Spinner select_state;
    ArrayList<String> list_state;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    ArrayList<NetworkHistoryPozo> transactionPozoArrayList;
    private int selectedDate, selectedMonth, selectedYear;
    String months = null, dayss = null;
    NetworkHistoryAdapter adapter;
    ArrayList<TbOperatorPozo> list_state1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.network_history_layout, container, false);
        initialize(rv);
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        else
            dbNull(NetworkHistoryFragment.this);
        return rv;
    }

    private void initialize(View view) {
        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);
        date2_text = (AutofitTextView) view.findViewById(R.id.date2);
        date2_text.setOnClickListener(this);
        date1_text = (AutofitTextView) view.findViewById(R.id.date1);
        date1_text.setOnClickListener(this);
        date2_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        date1_text.setText(selectedYear + "-" + selectedMonth + "-" + selectedDate);
        view.findViewById(R.id.btn_fund).setOnClickListener(this);
        trans_details = (ListView) view.findViewById(R.id.trans_details);
        select_state = (Spinner) view.findViewById(R.id.select_state);
        list_state = new ArrayList<>();
        list_state1 = BaseCompactActivity.dbRealm.getPayee_Details();
        for (int i = 0; i < list_state1.size(); i++) {
            list_state.add(list_state1.get(i).getOperatorsValue());
        }
        if (list_state.size() != 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, list_state);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_state.setAdapter(dataAdapter);
        }
        select_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    payee = list_state.get(position);
                else
                    payee = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rv.findViewById(R.id.todate).setOnClickListener(toDateClicked);
        rv.findViewById(R.id.date1).setOnClickListener(toDateClicked);
        ImageView toimage = (ImageView) rv.findViewById(R.id.toimage);
        toimage.setOnClickListener(toDateClicked);
        toimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        rv.findViewById(R.id.fromdate).setOnClickListener(fromDateClicked);
        rv.findViewById(R.id.date2).setOnClickListener(fromDateClicked);
        ImageView fromimage = (ImageView) rv.findViewById(R.id.fromimage);
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
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, NetworkHistoryFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fund:
                rv.findViewById(R.id.btn_fund).setClickable(false);
                if (date2_text.getText().toString().isEmpty()) {
                    date2_text.setError("Please enter valid data");
                    date2_text.requestFocus();
                    rv.findViewById(R.id.btn_fund).setClickable(true);
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter valid data");
                    date1_text.requestFocus();
                    rv.findViewById(R.id.btn_fund).setClickable(true);
                } else if (printDifference(mainDate(date2_text.getText().toString()), mainDate(date1_text.getText().toString())))
                    new AsyncPostMethod(WebConfig.CommonReport, channel_request(first, last).toString(), headerData, NetworkHistoryFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                else {
                    customDialog_Common("Statement Can Only View For One Month");
                    rv.findViewById(R.id.btn_fund).setClickable(true);
                    if(adapter!=null) {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                    }
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

    View.OnClickListener fromDateClicked = new View.OnClickListener() {
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

    public JSONObject channel_request(int fromIndex, int toIndex) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "C2C_CREDIT_HISTORY_REPORT");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("fromDate", date2_text.getText().toString());
            jsonObject.put("toDate", date1_text.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("roleType", "Payee");
            jsonObject.put("userMobile", list.get(0).getMobilno());
            jsonObject.put("fromIndex", String.valueOf(fromIndex));
            jsonObject.put("toIndex", String.valueOf(toIndex));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.has("objC2CCreditRPTList")) {
                    if (object.isNull("objC2CCreditRPTList") && object.getJSONArray("objC2CCreditRPTList").length() == 0)
                        customDialog_Common("No Record Found");
                    else
                        insertLastTransDetails(object.getJSONArray("objC2CCreditRPTList"));
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(getActivity(),object.getString("responseMessage"),Toast.LENGTH_LONG).show();
                setBack_click1(getActivity());
            } else
                responseMSg(object);
            rv.findViewById(R.id.btn_fund).setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<NetworkHistoryPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new NetworkHistoryPozo(object.getString("requestID"), object.getString("debitAmount"), object.getString("creditAmount"), object.getString("createdOn"), object.getString("sysRemarks"), object.getString("requestType")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<NetworkHistoryPozo> list) {
        if (first == 1) {
            adapter = new NetworkHistoryAdapter(list, getActivity());
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
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
