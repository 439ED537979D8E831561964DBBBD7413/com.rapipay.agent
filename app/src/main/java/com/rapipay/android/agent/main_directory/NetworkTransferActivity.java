package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Model.NetworkManagePozo;
import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.NetworkTransferAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NetworkTransferActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener, CustomInterface {

    private int first = 1, last = 25;
    private boolean isLoading;
    ListView trans_details;
    LinearLayout last_tran_layout;
    TextView header;
    ArrayList<NetworkTransferPozo> transactionPozoArrayList;
    String clickedId, mobileNo = "";
    ImageView back_click;
    ArrayList<NetworkManagePozo> logList = new ArrayList<NetworkManagePozo>();
    private NetworkTransferAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_transfer_layout);
        initialize();
        clickedId = getIntent().getStringExtra("CLICKED");
        if (dbRealm != null && dbRealm.getDetails_Rapi())
            loadApi();
        else
            dbNull(NetworkTransferActivity.this);
    }

    private void loadApi() {
        logList.add(new NetworkManagePozo(list.get(0).getMobilno(), list.get(0).getMobilno()));
        new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate("GET_MY_NODE_DETAILS", list.get(0).getMobilno(), first, last).toString(), headerData, NetworkTransferActivity.this, getString(R.string.responseTimeOut), "GETNODEDETAILS").execute();
    }

    @Override
    protected void onPause() {
        trans_details.setEnabled(true);
        super.onPause();
    }

    private void initialize() {
        header = (TextView) findViewById(R.id.header);
        back_click = (ImageView) findViewById(R.id.back_clicked);
        back_click.setOnClickListener(this);
        back_click.setColorFilter(getResources().getColor(R.color.white));
        last_tran_layout = (LinearLayout) findViewById(R.id.last_tran_layout);
        trans_details = (ListView) findViewById(R.id.trans_details);
        trans_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (clickedId.equalsIgnoreCase("2")) {
                    NetworkTransferPozo pozo = transactionPozoArrayList.get(position);
                    if (!pozo.getAgentCategory().equalsIgnoreCase("Retailer")) {
                        logList.add(new NetworkManagePozo(pozo.getMobileNo(), pozo.getMobileNo()));
                        mobileNo = pozo.getMobileNo();
                        trans_details.setEnabled(false);
                        customDialog_Common("NETWORKLAYOUT", null, pozo, "Network Option", null, null, NetworkTransferActivity.this);
                    } else
                        reDirect_Activity(pozo);
                }
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
                    new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate("GET_MY_NODE_DETAILS", list.get(0).getMobilno(), first, last).toString(), headerData, NetworkTransferActivity.this, getString(R.string.responseTimeOut), "GETNODEDETAILS").execute();
                    isLoading = true;
                }
            }
        });
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
                    customDialog_Common("KYCLAYOUT", null, null, "Network Detail", null, object.getString("responseMessage"), NetworkTransferActivity.this);
                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(this,object.getString("responseMessage"),Toast.LENGTH_LONG).show();
                setBack_click1(this);
            } else {
                responseMSg(object);
            }
            trans_details.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        if (array.length() != 1)
            transactionPozoArrayList = new ArrayList<NetworkTransferPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (!logList.get(logList.size() - 1).getCuurentNo().equalsIgnoreCase(object.getString("mobileNo")))
                    transactionPozoArrayList.add(new NetworkTransferPozo(object.getString("companyName"), object.getString("mobileNo"), object.getString("agentName"), object.getString("agentBalance"), object.getString("agentCategory")));
                else {
                    if (array.length() != 1) {
                        header.setVisibility(View.VISIBLE);
                        header.setText(object.getString("agentName") + " - " + object.getString("mobileNo") + "\n" + object.getString("agentCategory"));
                        last_tran_layout.setVisibility(View.VISIBLE);
                    }

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
            adapter = new NetworkTransferAdapter(NetworkTransferActivity.this, list);
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
    }

    public JSONObject getNetwork_Validate(String servicetype, String mobileNo, int fromIndex, int toIndex) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
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

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    private void serviceActivity(NetworkTransferPozo pozo) {
        Intent intent = new Intent(NetworkTransferActivity.this, GetAllServicesActivity.class);
        intent.putExtra("OBJECT", pozo);
        startActivity(intent);
    }

    private void reDirect_Activity(NetworkTransferPozo pozo) {
        Intent intent = new Intent(NetworkTransferActivity.this, UserSettingDetails.class);
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
                v.findViewById(R.id.back_clicked).setClickable(false);
                if (logList.size() != 0) {
                    new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate("GET_MY_NODE_DETAILS", logList.get(logList.size() - 2).getBackMaintain(), first, last).toString(), headerData, NetworkTransferActivity.this, getString(R.string.responseTimeOut), "GETNODEDETAILS").execute();
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

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("Details"))
            reDirect_Activity((NetworkTransferPozo) ob);
        else if (type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
//        else if (type.equalsIgnoreCase("DETAILS"))
//            serviceActivity((NetworkTransferPozo) ob);
    }

    @Override
    public void cancelClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("NETWORKLAYOUT")) {
            NetworkTransferPozo pozo = (NetworkTransferPozo) ob;
            back_click.setVisibility(View.VISIBLE);
            new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate("GET_MY_NODE_DETAILS", pozo.getMobileNo(), first, last).toString(), headerData, NetworkTransferActivity.this, getString(R.string.responseTimeOut), "GETNODEDETAILS").execute();
        }
    }
}

