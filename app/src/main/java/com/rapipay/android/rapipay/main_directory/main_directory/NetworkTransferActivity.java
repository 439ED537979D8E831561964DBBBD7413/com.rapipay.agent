package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.rapipay.main_directory.Model.NetworkManagePozo;
import com.rapipay.android.rapipay.main_directory.Model.NetworkTransferPozo;
import com.rapipay.android.rapipay.main_directory.adapter.NetworkTransferAdapter;
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
import java.util.Date;

import me.grantland.widget.AutofitTextView;

public class NetworkTransferActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener {

    RecyclerView trans_details;
    LinearLayout last_tran_layout;
    TextView header;
    ArrayList<NetworkTransferPozo> transactionPozoArrayList;
    String clickedId, mobileNo = "";
    ImageView back_click;
    ArrayList<NetworkManagePozo> logList = new ArrayList<NetworkManagePozo>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_transfer_layout);
        initialize();
        clickedId = getIntent().getStringExtra("CLICKED");
        loadApi();
    }

    private void loadApi() {
        logList.add(new NetworkManagePozo(list.get(0).getMobilno(), list.get(0).getMobilno()));
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Validate("GET_MY_NODE_DETAILS", list.get(0).getMobilno()).toString(), headerData, NetworkTransferActivity.this).execute();
    }

    private void initialize() {
        header = (TextView) findViewById(R.id.header);
        back_click = (ImageView) findViewById(R.id.back_clicked);
        back_click.setOnClickListener(this);
        back_click.setColorFilter(getResources().getColor(R.color.white));
        last_tran_layout = (LinearLayout) findViewById(R.id.last_tran_layout);
        trans_details = (RecyclerView) findViewById(R.id.trans_details);
        trans_details.addOnItemTouchListener(new RecyclerTouchListener(this, trans_details, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (clickedId.equalsIgnoreCase("0"))
                    customDialog_Ben(transactionPozoArrayList.get(position));
                else if (clickedId.equalsIgnoreCase("2")) {
                    NetworkTransferPozo pozo = transactionPozoArrayList.get(position);
                    if (!pozo.getAgentCategory().equalsIgnoreCase("Retailer")) {
                        logList.add(new NetworkManagePozo(pozo.getMobileNo(), pozo.getMobileNo()));
                        mobileNo = pozo.getMobileNo();
                        back_click.setVisibility(View.VISIBLE);
                        customPopUp(pozo);
                    }else
                        reDirect_Activity(pozo);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trans_details.setLayoutManager(layoutManager);
        trans_details.setAdapter(new NetworkTransferAdapter(this, trans_details, list));
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

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    private void customDialog_Ben(final NetworkTransferPozo pozo) {
        AutofitTextView btn_p_bank, btn_name, p_transid;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
                    hideKeyboard(NetworkTransferActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Transfer(pozo.getMobileNo(), text.getText().toString()).toString(), headerData, NetworkTransferActivity.this).execute();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void customPopUp(final NetworkTransferPozo pozo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setCancelable(false)
                .setNegativeButton("Network Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reDirect_Activity(pozo);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Network User", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Validate("GET_MY_NODE_DETAILS", pozo.getMobileNo()).toString(), headerData, NetworkTransferActivity.this).execute();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void reDirect_Activity(NetworkTransferPozo pozo){
        Intent intent = new Intent(NetworkTransferActivity.this,UserSettingDetails.class);
        intent.putExtra("OBJECT", pozo);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
            case R.id.back_clicked:
                if (logList.size() != 0) {
                    new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getNetwork_Validate("GET_MY_NODE_DETAILS", logList.get(logList.size() - 2).getBackMaintain()).toString(), headerData, NetworkTransferActivity.this).execute();
                    logList.remove(logList.size() - 1);
                    if (logList.size() == 1)
                        back_click.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void chechStat(String object) {

    }
}

