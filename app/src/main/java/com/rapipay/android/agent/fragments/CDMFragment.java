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
import android.widget.Toast;

import com.rapipay.android.agent.Model.CDMPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.CdmAdapter;
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
import java.util.Locale;

public class CDMFragment extends BaseFragment implements RequestHandler {
    private boolean isLoading;
    ListView trans_details;
    ArrayList<CDMPozo> transactionPozoArrayList;
    private int first = 1, last = 25;
    CdmAdapter adapter;
    EditText heading;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.cdm_axix_alyout, container, false);
        localStorage = LocalStorage.getInstance(getActivity());
        if (BaseCompactActivity.dbRealm != null && BaseCompactActivity.dbRealm.getDetails_Rapi())
            list = BaseCompactActivity.dbRealm.getDetails();
        initialize(rv);
        url();
        return rv;
    }

    private void initialize(View view) {
        heading = (EditText) view.findViewById(R.id.heading);
        trans_details = (ListView) view.findViewById(R.id.trans_details);
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
                    new AsyncPostMethod(WebConfig.CommonReport, getCDMDetails(first, last).toString(), headerData, CDMFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                    isLoading = true;
                }
            }
        });
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
    }

    private void url() {
        new AsyncPostMethod(WebConfig.CommonReport, getCDMDetails(first, last).toString(), headerData, CDMFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
    }

    public JSONObject getCDMDetails(int first, int last) {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_CDM_LOCATION");
                jsonObject.put("requestType", "REPORT_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("fromIndex", String.valueOf(first));
                jsonObject.put("toIndex", String.valueOf(last));
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Blank Value", Toast.LENGTH_SHORT).show();
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
                if (object.getString("serviceType").equalsIgnoreCase("GET_CDM_LOCATION")) {
                    if (object.has("cdmLocationList")) {
                        insertLastTransDetails(object.getJSONArray("cdmLocationList"));
                    }

                }
            } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                Toast.makeText(getActivity(), object.getString("responseCode"), Toast.LENGTH_LONG).show();
                setBack_click1(getActivity());
            } else
                responseMSg(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new CDMPozo(object.getString("branchName"), object.getString("state"), object.getString("city"), object.getString("address"), object.getString("locateAt"), object.getString("pinCode")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    private void initializeTransAdapter(ArrayList<CDMPozo> list) {
        if (first == 1) {
            adapter = new CdmAdapter(list, getActivity());
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
    }


}

