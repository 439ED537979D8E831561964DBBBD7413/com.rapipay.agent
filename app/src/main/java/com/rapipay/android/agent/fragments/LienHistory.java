package com.rapipay.android.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rapipay.android.agent.Model.ChannelHistoryPozo;
import com.rapipay.android.agent.Model.LienHistoryPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.ChannelListAdapter;
import com.rapipay.android.agent.adapter.LienAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.WalletRequestHandler;
import com.rapipay.android.agent.main_directory.ChannelHistoryActivity;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WalletAsyncMethod;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LienHistory extends BaseFragment implements WalletRequestHandler, View.OnClickListener, CustomInterface {

    View rv;
    ListView trans_details;
    ArrayList<LienHistoryPozo> lienHistoryArrayList;
    LienAdapter lienAdapter;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.lein_history_layout, container, false);
        initialize(rv);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        else
            dbNull(LienHistory.this);
        loadUrl();
        return rv;
    }

    private void initialize(View rv) {
        trans_details = (ListView) rv.findViewById(R.id.trans_details);
    }

    private void loadUrl() {
        new WalletAsyncMethod(WebConfig.CommonReport, lienHistory().toString(), headerData, LienHistory.this, getActivity(), getString(R.string.responseTimeOut), "TRANSACTIONHISTORY").execute();
    }

    public JSONObject lienHistory() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_AGENT_LIEN_DETAILS");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("txnIP", ImageUtils.ipAddress(getActivity()));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object, String hitFrom) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_AGENT_LIEN_DETAILS")) {
                    if (object.has("agentLienList") && object.getInt("count") > 0) {
                        insertLastTransDetails(object.getJSONArray("agentLienList"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        lienHistoryArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                lienHistoryArrayList.add(new LienHistoryPozo(object.getString("agentMobile"), object.getString("lienAmt"), object.getString("lienReason"), object.getString("approvalStatus"), object.getString("createdOn"), object.getString("createdBy"), object.getString("lienRemovalStatus"), object.getString("requestID")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lienHistoryArrayList.size() != 0)
            initializeTransAdapter(lienHistoryArrayList);
    }

    private void initializeTransAdapter(ArrayList<LienHistoryPozo> list) {
        lienAdapter = new LienAdapter(list, getActivity());
        trans_details.setAdapter(lienAdapter);
    }

    @Override
    public void chechStat(String object, String hitFrom) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void okClicked(String type, Object ob) {

    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
