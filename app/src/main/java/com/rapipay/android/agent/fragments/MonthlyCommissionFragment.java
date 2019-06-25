package com.rapipay.android.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.rapipay.android.agent.Model.CommissionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.DailyAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MonthlyCommissionFragment extends BaseFragment implements RequestHandler {


    private boolean isLoading;
    ListView trans_details;
    ArrayList<CommissionPozo> transactionPozoArrayList;
    private int first = 1, last = 25;
    private DailyAdapter adapter;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.commission_layout, container, false);
        localStorage = LocalStorage.getInstance(getActivity());
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        initialize(rv);
        loadApi();
        return rv;
    }


    private void loadApi() {
        new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate(first, last).toString(), headerData,MonthlyCommissionFragment.this, getActivity(),getString(R.string.responseTimeOut),"M").execute();
    }

    private void initialize(View rv) {
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
                    new AsyncPostMethod(WebConfig.CommonReport, getNetwork_Validate(first, last).toString(), headerData,MonthlyCommissionFragment.this, getActivity(),getString(R.string.responseTimeOut),"M").execute();
                    isLoading = true;
                }
            }
        });
    }

    public JSONObject getNetwork_Validate(int fromIndex, int toIndex) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "MY_COMMISION_REPORT");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("commisionType", "M");
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("fromIndex", String.valueOf(fromIndex));
            jsonObject.put("toIndex", String.valueOf(toIndex));
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
                if (object.getString("serviceType").equalsIgnoreCase("MY_COMMISION_REPORT")) {
                    if (object.has("myCommisionList")) {
                        if (Integer.parseInt(object.getString("commisionCount")) > 0) {
                            insertLastTransDetails(object.getJSONArray("myCommisionList"));
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
        transactionPozoArrayList = new ArrayList<CommissionPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new CommissionPozo(object.getString("transactionType"), object.getString("transactionAmount")+ " / " + object.getString("crDrAmount") + " "+ object.getString("crDrType"),object.getString("transactionID") + " / "+ object.getString("transactionDate"), object.getString("transactionStatus")+ " / " + object.getString("settleStatus")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<CommissionPozo> list) {
        if (first == 1) {
            adapter = new DailyAdapter(getContext(), list);
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

