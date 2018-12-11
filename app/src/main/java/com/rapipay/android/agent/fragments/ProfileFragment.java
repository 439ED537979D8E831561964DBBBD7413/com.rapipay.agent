package com.rapipay.android.agent.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.ProfileAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileFragment extends Fragment implements RequestHandler, View.OnClickListener {

    View rv;
    protected ArrayList<RapiPayPozo> list;
    protected Long tsLong;
    protected LocalStorage localStorage;
    LinearLayout listView;
    ArrayList<HeaderePozo> pozoArrayList;
    ProfileAdapter adapter;
    AppCompatButton btn_login;
    HashMap<String, String> headerePozoArrayList;
    private int selectedDate, selectedMonth, selectedYear;
    String months = null, dayss = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.profile_view, container, false);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        localStorage = LocalStorage.getInstance(getActivity());
        initialize(rv);
        loadApi();
        return rv;
    }

    private void loadApi() {
        new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", ProfileFragment.this, getActivity()).execute();
    }

    public JSONObject getJson_Validate() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_USER_PROFILE_DETAILS");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", "CP" + tsLong.toString());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private void initialize(View view) {
        btn_login = (AppCompatButton) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        listView = (LinearLayout) view.findViewById(R.id.listbottom);
        headerePozoArrayList = new HashMap<String, String>();
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_USER_PROFILE_DETAILS")) {
                    JSONArray array = object.getJSONArray("listUserProfileData");
                    insertLastTransDetails(array);
                } else if (object.getString("serviceType").equalsIgnoreCase("UPDATE_USER_PROFILE_DETAILS")) {
                    customDialog_Ben(object.getString("responseMessage"), "Profile Updated Successfully");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    AlertDialog alertDialog;

    private void customDialog_Ben(String msg, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.GONE);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(msg);
        otpView.setVisibility(View.VISIBLE);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialog.setView(alertLayout);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadApi();
                alertDialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }

    @Override
    public void chechStat(String object) {

    }

    int i = 0;
    EditText textEdit = null;

    private void insertLastTransDetails(JSONArray array) {
        listView.removeAllViews();
        pozoArrayList = new ArrayList<HeaderePozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String value = object.getString("headervalue");
                if (!value.equalsIgnoreCase("null"))
                    pozoArrayList.add(new HeaderePozo(object.getString("headervalue"), object.getString("headreText"), object.getString("iseditable")));
                else
                    pozoArrayList.add(new HeaderePozo("", object.getString("headreText"), object.getString("iseditable")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pozoArrayList.size() != 0) {
            LayoutInflater inflater = getLayoutInflater();
            for (i = 0; i < pozoArrayList.size(); i++) {
                View view = inflater.inflate(R.layout.profile_layout, null);
                TextView headervalue = (TextView) view.findViewById(R.id.headervalue);
                final TextView headerdata = (TextView) view.findViewById(R.id.headerdata);
                ImageView headeredit = (ImageView) view.findViewById(R.id.headeredit);
                final EditText headerdataedit = (EditText) view.findViewById(R.id.headerdataedit);
                headervalue.setText(pozoArrayList.get(i).getHeaderData());
                headerdata.setText(pozoArrayList.get(i).getHeaderValue());
                headerdataedit.setText(pozoArrayList.get(i).getHeaderValue());
                headerdataedit.setTag(pozoArrayList.get(i).getHeaderData());
                if (pozoArrayList.get(i).getHeaderID().equalsIgnoreCase("Y")) {
                    headeredit.setVisibility(View.VISIBLE);
                    headerePozoArrayList.put(pozoArrayList.get(i).getHeaderData(), pozoArrayList.get(i).getHeaderValue());
                } else
                    headeredit.setVisibility(View.GONE);
//                if (pozoArrayList.get(i).getHeaderData().equalsIgnoreCase("Date of birth"))
//                    headerdataedit.setOnClickListener(toDateClicked);
                headeredit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        headerdataedit.setVisibility(View.VISIBLE);
                        headerdata.setVisibility(View.GONE);
                    }
                });

                headerdataedit.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        textEdit = (EditText) v;
                        String tag = (String) textEdit.getTag();
                        if (tag.equalsIgnoreCase("Date of birth"))
                            textEdit.setOnClickListener(toDateClicked);
                        return false;
                    }
                });
                headerdataedit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String tag = (String) textEdit.getTag();
                        headerePozoArrayList.put(tag, s.toString());
//                headerePozoArrayList.get(position).setHeaderValue(s.toString());
                    }
                });
                listView.addView(view);
            }
//            adapter = new ProfileAdapter(pozoArrayList, getActivity());
//            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                HashMap<String, String> list = headerePozoArrayList;
                new AsyncPostMethod(WebConfig.LOGIN_URL, updateJson_Validate(list).toString(), "", ProfileFragment.this, getActivity()).execute();
                break;
        }
    }

    public JSONObject updateJson_Validate(HashMap<String, String> listMap) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "UPDATE_USER_PROFILE_DETAILS");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", "GUPD" + tsLong.toString());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                for (String key : listMap.keySet()) {
                    jsonObject.put(key, listMap.get(key));
                }
//                if (listMap.containsKey("State"))
//                    jsonObject.put("statename", listMap.get("State"));
//                if (listMap.containsKey("Email ID"))
//                    jsonObject.put("emailId", listMap.get("Email ID"));
//                if (listMap.containsKey("Date Of birth"))
//                    jsonObject.put("dateOfBirth", listMap.get("Date Of birth"));
//                if (listMap.containsKey("GSTIN"))
//                    jsonObject.put("gstin", listMap.get("GSTIN"));
//                if (listMap.containsKey("PAN No"))
//                    jsonObject.put("panCardNo", listMap.get("PAN No"));
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
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
                        textEdit.setText(dayss + "/" + months + "/" + year);
                        dialog.dismiss();
                    } else {

                        if (selectedDate != dayOfMonth) {
                            textEdit.setText(dayss + "/" + months + "/" + year);
                            dialog.dismiss();
                        } else {
                            if (selectedMonth != month) {
                                textEdit.setText(dayss + "/" + months + "/" + year);
                                dialog.dismiss();
                            }
                        }
                    }
                    textEdit.setError(null);
                    selectedDate = dayOfMonth;
                    selectedMonth = (month);
                    selectedYear = year;
                }
            });
            dialog.show();
        }
    };
}
