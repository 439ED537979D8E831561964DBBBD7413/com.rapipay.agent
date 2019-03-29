package com.rapipay.android.agent.interfaces;

import org.json.JSONObject;

public interface WalletRequestHandler {
    void chechStatus(JSONObject object,String hitFrom);
    void chechStat(String object,String hitFrom);
}

