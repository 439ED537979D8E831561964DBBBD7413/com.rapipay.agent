package com.rapipay.android.agent.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.Model.NetworkHistoryPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.NetworkAdapter;
import com.rapipay.android.agent.adapter.NetworkHistoryAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.WebConfig;

public class NetworkHistoryFragment extends Fragment implements RequestHandler, View.OnClickListener {

    Long tsLong;
    View rv;
    private int first = 1, last = 25;
    private boolean isLoading;
    AutofitTextView date2_text, date1_text;
    ListView trans_details;
    protected ArrayList<RapiPayPozo> list;
    private String headerData, payee;
    Spinner select_state;
    ArrayList<String> list_state;
    ArrayList<NetworkHistoryPozo> transactionPozoArrayList;
    private int selectedDate, selectedMonth, selectedYear;
    String months = null, dayss = null;
    NetworkHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.network_history_layout, container, false);
        initialize(rv);
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        list = BaseCompactActivity.db.getDetails();
        return rv;
    }

    private void initialize(View view) {
        date2_text = (AutofitTextView) view.findViewById(R.id.date2);
        date2_text.setOnClickListener(this);
        date1_text = (AutofitTextView) view.findViewById(R.id.date1);
        date1_text.setOnClickListener(this);
        view.findViewById(R.id.btn_fund).setOnClickListener(this);
        trans_details = (ListView) view.findViewById(R.id.trans_details);
        select_state = (Spinner) view.findViewById(R.id.select_state);
        list_state = BaseCompactActivity.db.getPayee_Details();
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
                    payee="";
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
                    new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, channel_request(first,last).toString(), headerData, NetworkHistoryFragment.this, getActivity()).execute();
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fund:
                if (payee.isEmpty())
                    Toast.makeText(getActivity(), "Please select type", Toast.LENGTH_SHORT).show();
                else if (date2_text.getText().toString().isEmpty()) {
                    date2_text.setError("Please enter valid data");
                    date2_text.requestFocus();
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please enter valid data");
                    date1_text.requestFocus();
                } else
                    new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, channel_request(first,last).toString(), headerData, NetworkHistoryFragment.this, getActivity()).execute();
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

    //    private void picDate() {
//        months=null;dayss=null;
//        rv.findViewById(R.id.todate).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // custom dialog
//                final Dialog dialog = new Dialog(getActivity());
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
//        rv.findViewById(R.id.fromdate).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // custom dialog
//                final Dialog dialog = new Dialog(getActivity());
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
//    private void calender(final TextView textview) {
//        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        Calendar newCalendar = Calendar.getInstance();
//        pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
            jsonObject.put("serviceType", "C2C_CREDIT_HISTORY_REPORT");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "CCHR" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("fromDate", date2_text.getText().toString());
            jsonObject.put("toDate", date1_text.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("roleType", payee);
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
                    insertLastTransDetails(object.getJSONArray("objC2CCreditRPTList"));
                }
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
                transactionPozoArrayList.add(new NetworkHistoryPozo(object.getString("agentID"), object.getString("requestAmount"), object.getString("creditID"), object.getString("createdOn")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<NetworkHistoryPozo> list) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        trans_details.setLayoutManager(layoutManager);
//        trans_details.setAdapter(new NetworkHistoryAdapter(getActivity(), trans_details, list));
        if (first == 1) {
            adapter = new NetworkHistoryAdapter(list,getActivity());
            trans_details.setAdapter(adapter);
        }else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
    }
}
