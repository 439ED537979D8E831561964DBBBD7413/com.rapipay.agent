package com.rapipay.android.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.NetworkTransferAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class FOSTransFrag extends BaseFragment implements RequestHandler {

    String nodeAgentID, sessionKey, sessionRefNo, AgentID;
    EditText heading;
    private boolean isLoading;
    int first = 1, last = 25;
    ListView trans_details;
    ArrayList<NetworkTransferPozo> transactionPozoArrayList;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    NetworkTransferAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.fos_layout_transfer, container, false);
        initialize(rv);
        loadUrl();
        return rv;
    }

    void loadUrl() {
        new AsyncPostMethod(WebConfig.CommonReport, getTransgerRequest(first, last).toString(), headerData, FOSTransFrag.this, getActivity(), getString(R.string.responseTimeOut)).execute();
    }

    JSONObject getTransgerRequest(int first, int last) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_MY_NODE_DETAILS");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", nodeAgentID);
            jsonObject.put("sessionRefNo", sessionRefNo);
            jsonObject.put("agentMobile", AgentID);
            jsonObject.put("fromIndex", first);
            jsonObject.put("toIndex", last);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(sessionKey, jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initialize(View rv) {
        nodeAgentID = getArguments().getString("nodeAgentID");
        AgentID = getArguments().getString("AgentID");
        sessionRefNo = getArguments().getString("sessionRefNo");
        sessionKey = getArguments().getString("sessionKey");
        heading = (EditText) rv.findViewById(R.id.headingsearch);
        heading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });
        trans_details = (ListView) rv.findViewById(R.id.trans_details);
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
                    new AsyncPostMethod(WebConfig.CommonReport, getTransgerRequest(first, last).toString(), headerData, FOSTransFrag.this, getActivity(), getString(R.string.responseTimeOut)).execute();
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
                }
            }else
                responseMSg(object);
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
                transactionPozoArrayList.add(new NetworkTransferPozo(object.getString("companyName"), object.getString("mobileNo"), object.getString("agentName"), object.getString("agentBalance"), object.getString("agentCategory")));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<NetworkTransferPozo> list) {
        if (first == 1) {
            adapter = new NetworkTransferAdapter(getActivity(), list);
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
    }

    @Override
    public void chechStat(String object) {

    }
}
