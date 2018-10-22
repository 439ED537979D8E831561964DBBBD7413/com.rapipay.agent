package com.rapipay.android.agent.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

import com.rapipay.android.agent.Model.NetworkManagePozo;
import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.NetworkAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.main_directory.MainActivity;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.WebConfig;

public class NetworkTransFragment extends Fragment implements RequestHandler {

    private int first = 1, last = 25;
    private boolean isLoading;
    protected SimpleDateFormat format;
    protected Date date;
    ListView trans_details;
    LinearLayout last_tran_layout;
    TextView header;
    ArrayList<NetworkTransferPozo> transactionPozoArrayList;
    String clickedId = "0", headerData;
    ArrayList<NetworkManagePozo> logList = new ArrayList<NetworkManagePozo>();
    View rv;
    protected ArrayList<RapiPayPozo> list;
    NetworkAdapter adapter;
    protected Long tsLong;
    NetworkTransferPozo pozoClick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.network_fragment_layout, container, false);
        initialize(rv);
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        list = BaseCompactActivity.db.getDetails();
        loadApi();
        return rv;
    }

    private void loadApi() {
        logList.add(new NetworkManagePozo(list.get(0).getMobilno(), list.get(0).getMobilno()));
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Validate("GET_MY_NODE_DETAILS", list.get(0).getMobilno(), first, last).toString(), headerData, NetworkTransFragment.this, getActivity()).execute();
    }

    private void initialize(View rv) {
        header = (TextView) rv.findViewById(R.id.header);
        header.setVisibility(View.GONE);
        last_tran_layout = (LinearLayout) rv.findViewById(R.id.last_tran_layout);
        trans_details = (ListView) rv.findViewById(R.id.trans_details);
        trans_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pozoClick = transactionPozoArrayList.get(position);
                if (clickedId.equalsIgnoreCase("0"))
                    customDialog_Ben(transactionPozoArrayList.get(position), "Network Transfer", "AMOUNTTRANSFER", "", "Credit To Network");
            }
        });
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
                    new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Validate("GET_MY_NODE_DETAILS", list.get(0).getMobilno(), first, last).toString(), headerData, NetworkTransFragment.this, getActivity()).execute();
                    isLoading = true;
                }
            }
        });


//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        trans_details.setLayoutManager(layoutManager);
//        trans_details.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), trans_details, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                if (clickedId.equalsIgnoreCase("0"))
//                    customDialog_Ben(transactionPozoArrayList.get(position),"Network Transfer","AMOUNTTRANSFER","","Credit To Network");
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
//        trans_details.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_MY_NODE_DETAILS")) {
                    if (object.has("objAgentNodeList")) {
                        if (Integer.parseInt(object.getString("agentCount")) > 0) {
                            insertLastTransDetails(object.getJSONArray("objAgentNodeList"));
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("C2C_NETWORK_CREDIT")) {
                    customDialog_Ben(null, object.getString("responseMessage"), "NETWORK_CREDIT", null, "Credit Confirmation");
//                    customDialog(object.getString("responseMessage"));
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_NODE_HEADER_DATA")) {
                    if (object.has("headerList")) {
                        if (Integer.parseInt(object.getString("listCount")) > 0) {
                            insertLastTransDetailsNode(object.getJSONArray("headerList"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetailsNode(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("headerValue").equalsIgnoreCase("My Balance")) {
                    String data = object.getString("headerData");
                    if (formatss(data) == null || formatss(data).equalsIgnoreCase("0")) {
                        Toast.makeText(getActivity(),"Not Authorized to create New User!.", Toast.LENGTH_SHORT).show();
                    } else
                        customDialogConfirm(pozoClick, "Are you sure you want to Transfer?", "CONFIRMATION", textsss.getText().toString(), "Credit Confirmation");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<NetworkTransferPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (!logList.get(logList.size() - 1).getCuurentNo().equalsIgnoreCase(object.getString("mobileNo")))
                    transactionPozoArrayList.add(new NetworkTransferPozo(object.getString("companyName"), object.getString("mobileNo"), object.getString("agentName"), object.getString("agentBalance"), object.getString("agentCategory")));
                else {
                    header.setVisibility(View.GONE);
                    header.setText(object.getString("agentName") + " - " + object.getString("mobileNo") + "\n" + object.getString("agentCategory"));
                    last_tran_layout.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<NetworkTransferPozo> list) {
        if (first == 1) {
            adapter = new NetworkAdapter(list, getActivity());
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
//        Toast.makeText(getActivity(),"TAG",Toast.LENGTH_SHORT).show();
    }

    public JSONObject getNetwork_Validate(String servicetype, String mobileNo, int fromIndex, int toIndex) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "GMND" + format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentMobile", mobileNo);
            jsonObject.put("fromIndex", String.valueOf(fromIndex));
            jsonObject.put("toIndex", String.valueOf(toIndex));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    AlertDialog alertDialog, alertDialogs;
    TextView textsss;
    private void customDialog_Ben(final NetworkTransferPozo pozo, String msg, final String type, String amount, String title) {
        AutofitTextView btn_p_bank, btn_name, p_transid;
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("AMOUNTTRANSFER")) {
            alertLayout.findViewById(R.id.custom_popup).setVisibility(View.VISIBLE);
            btn_name = (AutofitTextView) alertLayout.findViewById(R.id.btn_name_popup);
            p_transid = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_transid);
            btn_p_bank = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_bank);
            btn_name.setText("Company Name : " + pozo.getCompanyName());
            p_transid.setText(pozo.getAgentName() + " - " + pozo.getMobileNo());
            btn_p_bank.setText("Current Balance : " + formatss(pozo.getAgentBalance()));
        } else if (type.equalsIgnoreCase("NETWORK_CREDIT")) {
            btn_cancel.setVisibility(View.GONE);
            TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            otpView.setText(msg);
            otpView.setVisibility(View.VISIBLE);
        }
        textsss = (TextView) alertLayout.findViewById(R.id.input_amount_popup);
        dialog.setView(alertLayout);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("AMOUNTTRANSFER")) {
                    hideKeyboard(getActivity());
                    if (!ImageUtils.commonAmount(textsss.getText().toString())) {
                        textsss.setError("Please enter valid data");
                        textsss.requestFocus();
                    } else {
//                        for (int i = 0; i < MainActivity.pozoArrayList.size(); i++) {
//                            if (MainActivity.pozoArrayList.get(i).getHeaderID().equalsIgnoreCase("1"))
//                                if (MainActivity.pozoArrayList.get(i).getHeaderData().equalsIgnoreCase("null") || formatss(MainActivity.pozoArrayList.get(i).getHeaderData()).equalsIgnoreCase("0")) {
//                                    url();
//                                } else {
                                    customDialogConfirm(pozo, "Are you sure you want to Transfer?", "CONFIRMATION", textsss.getText().toString(), "Credit Confirmation");
//                                }
//                        }
                        alertDialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("NETWORK_CREDIT")) {
                    loadApi();
                    alertDialog.dismiss();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
//        dialog.setPositiveButton("Network Transfer", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (!text.getText().toString().isEmpty()) {
//                    hideKeyboard(getActivity());
//                    confirmDialog("Sure you want to Transfer?",text,pozo);
//                    dialog.dismiss();
//                }
//            }
//        });
//        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        alertDialog = dialog.show();
    }

    private void customDialogConfirm(final NetworkTransferPozo pozo, String msg, final String type, final String amount, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("CONFIRMATION")) {
            TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            otpView.setText(msg);
            otpView.setVisibility(View.VISIBLE);
        }
        dialog.setView(alertLayout);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("CONFIRMATION")) {
                    alertDialogs.dismiss();
                    new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Transfer(pozo.getMobileNo(), amount).toString(), headerData, NetworkTransFragment.this, getActivity()).execute();
                }
                alertDialogs.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogs.dismiss();
            }
        });
        alertDialogs = dialog.show();
    }

    public JSONObject getNetwork_Transfer(String receiverId, String txnAmount) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "C2C_NETWORK_CREDIT");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "CNC" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentSenderID", list.get(0).getMobilno());
            jsonObject.put("agentReciverID", receiverId);
            jsonObject.put("txnAmount", txnAmount);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

//    private void customDialog(String msg) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.app_name);
//        //Setting message manually and performing action on button click
//        builder.setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        loadApi();
//                        dialog.dismiss();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

    //    private void confirmDialog(String msg,final TextView text,final NetworkTransferPozo pozo) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.app_name);
//        //Setting message manually and performing action on button click
//        builder.setMessage(msg)
//                .setCancelable(false)
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Transfer(pozo.getMobileNo(), text.getText().toString()).toString(), headerData, NetworkTransFragment.this, getActivity()).execute();
//                        dialog.dismiss();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
    private void url() {
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getDashBoard("GET_NODE_HEADER_DATA").toString(), headerData, getActivity()).execute();
    }

    public JSONObject getDashBoard(String servicetype) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "GNHD" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {

    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected String formatss(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


