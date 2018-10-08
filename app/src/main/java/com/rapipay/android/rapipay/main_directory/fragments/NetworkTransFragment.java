package com.rapipay.android.rapipay.main_directory.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.NetworkManagePozo;
import com.rapipay.android.rapipay.main_directory.Model.NetworkTransferPozo;
import com.rapipay.android.rapipay.main_directory.Model.RapiPayPozo;
import com.rapipay.android.rapipay.main_directory.adapter.NetworkTransferAdapter;
import com.rapipay.android.rapipay.main_directory.interfaces.ClickListener;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.main_directory.NetworkTransferActivity;
import com.rapipay.android.rapipay.main_directory.main_directory.UserSettingDetails;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.RecyclerTouchListener;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.grantland.widget.AutofitTextView;

public class NetworkTransFragment extends Fragment implements RequestHandler {

    protected SimpleDateFormat format;
    protected Date date;
    RecyclerView trans_details;
    LinearLayout last_tran_layout;
    TextView header;
    ArrayList<NetworkTransferPozo> transactionPozoArrayList;
    String clickedId="0",headerData;
    ArrayList<NetworkManagePozo> logList = new ArrayList<NetworkManagePozo>();
    View rv;
    protected ArrayList<RapiPayPozo> list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.network_fragment_layout, container, false);
        initialize(rv);
//        clickedId = getActivity().getIntent().getStringExtra("CLICKED");
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        list = BaseCompactActivity.db.getDetails();
        loadApi();
        return rv;
    }
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.network_transfer_layout);
//        initialize();
//        clickedId = getIntent().getStringExtra("CLICKED");
//        loadApi();
//    }

    private void loadApi() {
        logList.add(new NetworkManagePozo(list.get(0).getMobilno(), list.get(0).getMobilno()));
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Validate("GET_MY_NODE_DETAILS", list.get(0).getMobilno()).toString(), headerData, NetworkTransFragment.this, getActivity()).execute();
    }

    private void initialize(View rv) {
        header = (TextView) rv.findViewById(R.id.header);
        last_tran_layout = (LinearLayout) rv.findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) rv.findViewById(R.id.trans_details);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
//        trans_details.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), trans_details, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                if (clickedId.equalsIgnoreCase("0"))
//                    customDialog_Ben(transactionPozoArrayList.get(position));
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
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
                    customDialog(object.getString("responseMessage"));
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
                    header.setVisibility(View.VISIBLE);
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
        NetworkTransferAdapter adapter = new NetworkTransferAdapter(getActivity(), trans_details, list);
        trans_details.setAdapter(adapter);
    }

    public JSONObject getNetwork_Validate(String servicetype, String mobileNo) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentMobile", mobileNo);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private void customDialog_Ben(final NetworkTransferPozo pozo) {
        AutofitTextView btn_p_bank, btn_name, p_transid;
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_popup_network, null);
        btn_name = (AutofitTextView) alertLayout.findViewById(R.id.btn_name);
        p_transid = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_transid);
        btn_p_bank = (AutofitTextView) alertLayout.findViewById(R.id.btn_p_bank);
        btn_name.setText("( " + pozo.getCompanyName() + " )");
        p_transid.setText(pozo.getAgentName() + " - " + pozo.getMobileNo());
        btn_p_bank.setText(pozo.getAgentBalance());
        final TextView text = (TextView) alertLayout.findViewById(R.id.input_amount_ben);
        dialog.setView(alertLayout);
        dialog.setTitle(getResources().getString(R.string.app_name));

        dialog.setCancelable(true);
        dialog.setPositiveButton("Network Transfer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!text.getText().toString().isEmpty()) {
                    hideKeyboard(getActivity());
                    confirmDialog("Sure you want to Transfer?",text,pozo);
                    dialog.dismiss();
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public JSONObject getNetwork_Transfer(String receiverId, String txnAmount) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "C2C_NETWORK_CREDIT");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
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

    private void customDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadApi();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void confirmDialog(String msg,final TextView text,final NetworkTransferPozo pozo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Transfer(pozo.getMobileNo(), text.getText().toString()).toString(), headerData, NetworkTransFragment.this, getActivity()).execute();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
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
}


