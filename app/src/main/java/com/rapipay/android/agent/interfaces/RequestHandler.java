package com.rapipay.android.agent.interfaces;

import org.json.JSONObject;

public interface RequestHandler {
    void chechStatus(JSONObject object);
    void chechStat(String object);
}
