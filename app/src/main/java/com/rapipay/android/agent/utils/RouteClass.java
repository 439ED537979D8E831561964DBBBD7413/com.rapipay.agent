package com.rapipay.android.agent.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.main_directory.LoginScreenActivity;
import com.rapipay.android.agent.main_directory.PinActivity;
import com.rapipay.android.agent.main_directory.PinVerification;
import com.rapipay.android.agent.main_directory.WebViewClientActivity;

public class RouteClass {
    Intent intent = null;
    public RouteClass(Context context, JSONObject object, String mobileNo, LocalStorage localStorage, String type) {
        define_Route(context, object, mobileNo, localStorage,type);
    }

    private void define_Route(final Context context, JSONObject object, String mobileNo, LocalStorage localStorage, String type) {
        try {
            ArrayList<RapiPayPozo> list = BaseCompactActivity.db.getDetails();
            if (list.size() == 0) {
                if (type!=null && type.equalsIgnoreCase("PINENTERED")) {
                    intent = new Intent(context, PinActivity.class);
                    intent.putExtra("agentId", mobileNo);
                    intent.putExtra("regTxnRefId", object.getString("txnRefId"));
                    intent.putExtra("imeiNo", object.getString("imeiNo"));
                    intent.putExtra("otpRefId", object.getString("otpRefId"));
                    intent.putExtra("sessionRefNo", object.getString("sessionRefNo"));
                    intent.putExtra("sessionKey", object.getString("sessionKey"));
                }else if (type!=null && type.equalsIgnoreCase("KYCENTERED")) {
                    intent = new Intent(context, WebViewClientActivity.class);
                    intent.putExtra("mobileNo", mobileNo);
                    intent.putExtra("parentId", object.getString("parentID"));
                    intent.putExtra("sessionKey", object.getString("sessionKey"));
                    intent.putExtra("sessionRefNo", object.getString("sessionRefNo"));
                    intent.putExtra("nodeAgent", "");
                    intent.putExtra("type", "");
//                    if(object.has("kycData")){
//                        JSONArray array = object.getJSONArray("kycData");
//                        JSONObject jsonObject = array.getJSONObject(0);
//                        byte[] bytes = jsonObject.toString().getBytes("utf-8");
//                        String imageEncoded = Base64.encodeToString(bytes, Base64.DEFAULT);
//                        intent.putExtra("base64", imageEncoded);
//                    }
                }else if (localStorage.getActivityState(LocalStorage.ROUTESTATE).equalsIgnoreCase("0")) {
                    intent = new Intent(context, LoginScreenActivity.class);
                }
            } else if (list.size() != 0) {
                if (!list.get(0).getSession().isEmpty()) {
                    intent = new Intent(context, PinVerification.class);
                }
            }
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }
}
