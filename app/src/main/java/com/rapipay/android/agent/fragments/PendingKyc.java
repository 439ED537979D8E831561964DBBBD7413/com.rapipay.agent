package com.rapipay.android.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;

import java.util.ArrayList;

public class PendingKyc extends Fragment implements RequestHandler {

    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    protected Long tsLong;
    protected ArrayList<RapiPayPozo> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rv = (View) inflater.inflate(R.layout.register_kyc_frag, container, false);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        loadApi();
        return rv;
    }

    private void loadApi(){
        new AsyncPostMethod(WebConfig.UAT, request_user().toString(), headerData,PendingKyc.this, getActivity()).execute();
    }
    public JSONObject request_user() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "PENDING_AGENT_KYC");
            jsonObject.put("requestType", "EKYC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRefId", "B2BT" + tsLong.toString());
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

    }

    @Override
    public void chechStat(String object) {

    }
}
