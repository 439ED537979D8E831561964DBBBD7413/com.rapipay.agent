package com.rapipay.android.agent.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;

import org.json.JSONObject;

public class AsyncPostMethod extends AsyncTask<String, String, String> {
    String url, xmlData, strHeaderData;
    HttpConnector connector;
    RequestHandler handler;
    CustomProgessDialog dialog;
    Context context;
    String responseData;
    String hitFrom;
    Dialog dialogs;

    public AsyncPostMethod(String url, String xmlData, String strHeaderData, Context handler, String responseData, String hitFrom) {
        this.url = url;
        this.xmlData = xmlData;
        this.strHeaderData = strHeaderData;
        this.context = handler;
        this.handler = (RequestHandler) handler;
        this.responseData = responseData;
        this.hitFrom = hitFrom;
        customDialog("Please Wait...");
        dialog = new CustomProgessDialog(handler);
    } //http://172.16.50.210:8080/RapiPayAPIHub/views/termCondition.jsp

    public AsyncPostMethod(String url, String xmlData, String strHeaderData, Context handler, String responseData) {
        this.url = url;
        this.xmlData = xmlData;
        this.strHeaderData = strHeaderData;
        this.context = handler;
        this.handler = (RequestHandler) handler;
        this.responseData = responseData;
        customDialog("Please Wait...");
        dialog = new CustomProgessDialog(handler);
    }

    public AsyncPostMethod(String url, String xmlData, String strHeaderData, RequestHandler handler, Context context, String responseData) {
        this.url = url;
        this.xmlData = xmlData;
        this.strHeaderData = strHeaderData;
        this.handler = handler;
        this.context = context;
        this.responseData = responseData;
        customDialog("Please Wait...");
        dialog = new CustomProgessDialog(context);
    }

    public AsyncPostMethod(String url, String xmlData, String strHeaderData, RequestHandler handler, Context context, String responseData, String hitFrom) {
        this.url = url;
        this.xmlData = xmlData;
        this.strHeaderData = strHeaderData;
        this.handler = handler;
        this.context = context;
        this.responseData = responseData;
        this.hitFrom = hitFrom;
        customDialog("Please Wait...");
        dialog = new CustomProgessDialog(context);
    }

    @Override
    protected void onPreExecute() {
        connector = HttpConnector.getInstance();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String s = null;
            if (isNetworkAvailable(context)) {
                if (url.contains("crm.rapipay.com"))
                    connector.crmsetServerCert(context);
                else
                    connector.setServerCert(context);
                s = connector.postData(url, xmlData, strHeaderData);
            } else {
                s = "false";
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialogs.dismiss();
        try {
            if (hitFrom != null && hitFrom.equalsIgnoreCase("AEPS-MATM")) {
                handler.chechStat(hitFrom);
            } else if (hitFrom != null && hitFrom.equalsIgnoreCase("ACTIVATIONSERVICE")) {
                handler.chechStat(s);
            } else if (s != null) {
                if (s.contains("DOCTYPE")) {
                    handler.chechStat(s);
                } else if (!s.equalsIgnoreCase("false")) {
                    JSONObject object = new JSONObject(s);
                    handler.chechStatus(object);
                }
            } else
                customDialog_Common("Transaction Timeout...");
            dialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null)
                dialog.hide_progress();
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void customDialog_Common(String msg) {
        try {
            final Dialog dialog = new Dialog(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setText(context.getResources().getString(R.string.Alert));
            TextView dialog_msg = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            dialog_msg.setText(msg);
            dialog_msg.setVisibility(View.VISIBLE);
            alertLayout.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
            dialog.setCancelable(false);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.setContentView(alertLayout);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void customDialog(String msg) {
        try {
            dialogs = new Dialog(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            alertLayout.setKeepScreenOn(true);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setVisibility(View.GONE);
            TextView dialog_msg = (TextView) alertLayout.findViewById(R.id.dialog_msg);
            dialog_msg.setText(msg);
            dialog_msg.setVisibility(View.VISIBLE);
            alertLayout.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
            btn_ok.setVisibility(View.GONE);
            dialogs.setContentView(alertLayout);
            dialogs.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
