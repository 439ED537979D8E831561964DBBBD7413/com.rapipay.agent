package com.rapipay.android.agent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rapipay.android.agent.Model.PendingKYCPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.PendingKYCAdapter;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.main_directory.WebViewVerify;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PendingKyc extends BaseFragment implements RequestHandler {

    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    protected ArrayList<RapiPayPozo> list;
    ListView trans_details;
    PendingKYCAdapter adapter;
    private int first = 1, last = 25;
    private boolean isLoading;
    ArrayList<PendingKYCPozo> transactionPozoArrayList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.pending_kyc_layout, container, false);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        TYPE = getActivity().getIntent().getStringExtra("type");
        customerType = getActivity().getIntent().getStringExtra("customerType");
        initialize(rv);
        loadApi();
        return rv;
    }

    private void initialize(View view) {
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
                    new AsyncPostMethod(WebConfig.EKYC, request_user().toString(), headerData, PendingKyc.this, getActivity(),getString(R.string.responseTimeOut)).execute();
                    isLoading = true;
                }
            }
        });
        trans_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PendingKYCPozo pendingKYCPozo = transactionPozoArrayList.get(position);
                if (pendingKYCPozo.getIsKycSubmitted().equalsIgnoreCase("N") && pendingKYCPozo.getStatusAction().equalsIgnoreCase("DENIED")) {
                    try {
                        String formData = getsession_ValidateKyc(customerType, pendingKYCPozo);
                        Intent intent = new Intent(getActivity(), WebViewVerify.class);
                        intent.putExtra("persons", "pending");
                        intent.putExtra("mobileNo", pendingKYCPozo.getMobileNo());
                        intent.putExtra("formData", formData);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String getsession_ValidateKyc(String kycType, PendingKYCPozo pendingKYCPozo) {
        JSONObject kycMapData = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        String form = null;
        try {
            kycMapData.put("mobileNo", pendingKYCPozo.getMobileNo());
            jsonObject.put("serviceType", "KYC_PROCESS");
            jsonObject.put("reKYC", "");
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("kycType", kycType);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("isreKYC", "Y");
            jsonObject.put("isAuto", "1");
            jsonObject.put("isEditable", "Y");
            jsonObject.put("txnRef",ImageUtils.miliSeconds());
            jsonObject.put("listdata", kycMapData.toString());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            form = "<html>\n" +
                    "\t<body>\n" +
                    "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYCFORWARD + "\">\n" +
                    "\t\t\t<input name=\"requestedData\" value=\"" + getDataBase64(jsonObject.toString()) + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input type=\"submit\"/>\n" +
                    "\t\t</form>\n" +
                    "\t\t<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                    "\t\t\t\t\tdocument.getElementById(\"validatekyc\").submit();\n" +
                    "\t\t</script>\n" +
                    "\t</body>\n" +
                    "</html>";
            return form;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadApi() {
        new AsyncPostMethod(WebConfig.EKYC, request_user().toString(), headerData, PendingKyc.this, getActivity(),getString(R.string.responseTimeOut)).execute();
    }

    private String getDataBase64(String data) {
        try {
            return Base64.encodeToString(data.getBytes("utf-8"), Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }

    public JSONObject request_user() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "PENDING_AGENT_KYC");
            jsonObject.put("requestType", "EKYC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
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
                try {
                    if (object.has("agentKycPendingList")) {
                        insertLastTransDetails(object.getJSONArray("agentKycPendingList"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        transactionPozoArrayList = new ArrayList<PendingKYCPozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transactionPozoArrayList.add(new PendingKYCPozo(object.getString("mobileNo"), object.getString("fullName"), object.getString("emailId"), object.getString("companyName"), object.getString("fullAddress"), object.getString("stateName"), object.getString("creationDate"), object.getString("statusAction"), object.getString("remarks"), object.getString("isKycSubmitted")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionPozoArrayList.size() != 0)
            initializeTransAdapter(transactionPozoArrayList);
    }

    @Override
    public void chechStat(String object) {

    }

    private void initializeTransAdapter(ArrayList<PendingKYCPozo> list) {
        if (first == 1) {
            adapter = new PendingKYCAdapter(list, getActivity());
            trans_details.setAdapter(adapter);
        } else {
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
    }
}
