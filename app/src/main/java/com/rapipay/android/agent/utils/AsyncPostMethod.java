package com.rapipay.android.agent.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.main_directory.MainActivity;
import com.rapipay.android.agent.main_directory.PinVerification;

import org.json.JSONObject;

public class AsyncPostMethod extends AsyncTask<String, String, String> {
    String url, xmlData, strHeaderData;
    HttpConnector connector;
    RequestHandler handler;
    CustomProgessDialog dialog;
    Context context;
    String responseData;
    String hitFrom;

    public AsyncPostMethod(String url, String xmlData, String strHeaderData, Context handler,String responseData,String hitFrom) {
        this.url = url;
        this.xmlData = xmlData;
        this.strHeaderData = strHeaderData;
        this.context = handler;
        this.handler = (RequestHandler) handler;
        this.responseData = responseData;
        this.hitFrom = hitFrom;
        customDialog("Please Wait...");
        dialog = new CustomProgessDialog(handler);
    }

    public AsyncPostMethod(String url, String xmlData, String strHeaderData, Context handler,String responseData) {
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
        alertDialog1.dismiss();
        try {
            if (hitFrom!=null && hitFrom.equalsIgnoreCase("AEPS-MATM")) {
                handler.chechStat(hitFrom);
            } else if (s != null) {
                if (s.contains("DOCTYPE")) {
                    handler.chechStat(s);
                } else if (!s.equalsIgnoreCase("false")) {
                    JSONObject object = new JSONObject(s);
                    if (object.has("responseCode")) {
                        if (object.getString("responseCode").equalsIgnoreCase("201")||object.getString("responseCode").equalsIgnoreCase("1032") || object.getString("responseCode").equalsIgnoreCase("86004") || object.getString("responseCode").equalsIgnoreCase("60236") || object.getString("responseCode").equalsIgnoreCase("200") || object.getString("responseCode").equalsIgnoreCase("300") || object.getString("responseCode").equalsIgnoreCase("101") || object.getString("responseCode").equalsIgnoreCase("75077") || object.getString("responseCode").equalsIgnoreCase("75115") || object.getString("responseCode").equalsIgnoreCase("75062") || object.getString("responseCode").equalsIgnoreCase("75061") || object.getString("responseCode").equalsIgnoreCase("75063") || object.getString("responseCode").equalsIgnoreCase("60116") || object.getString("responseCode").equalsIgnoreCase("86001") || object.getString("responseCode").equalsIgnoreCase("86002")) {
                            handler.chechStatus(object);
                            if (object.has("apiCommonResposne")) {
                                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                                if(object1!=null) {
                                    String balance = object1.getString("runningBalance");
                                    for (int i = 0; i < MainActivity.pozoArrayList.size(); i++) {
                                        if (MainActivity.pozoArrayList.get(i).getHeaderID().equalsIgnoreCase("1"))
                                            MainActivity.pozoArrayList.get(i).setHeaderData(balance);
                                    }
                                }
                            }
                        } else if (object.getString("responseCode").equalsIgnoreCase("60147")) {
                            Intent intent = new Intent(context, PinVerification.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        } else {
                            if (object.has("serviceType")) {
                                if (object.getString("serviceType").equalsIgnoreCase("PinVerify"))
                                    handler.chechStat(object.getString("responseMessage"));
                                else if (object.getString("serviceType").equalsIgnoreCase("ValidCredentialService"))
                                    handler.chechStat(object.getString("responseMessage"));
                                else
                                    responseMSg(object);
                            } else
                                responseMSg(object);
                        }
                    } else if (object.has("responsecode")) {
                        if (object.getString("responsecode").equalsIgnoreCase("201")||object.getString("responsecode").equalsIgnoreCase("200") || object.getString("responsecode").equalsIgnoreCase("101") || object.getString("responsecode").equalsIgnoreCase("300") || object.getString("responsecode").equalsIgnoreCase("75077") || object.getString("responsecode").equalsIgnoreCase("75115") || object.getString("responsecode").equalsIgnoreCase("75062") || object.getString("responsecode").equalsIgnoreCase("75061") || object.getString("responsecode").equalsIgnoreCase("60116") || object.getString("responsecode").equalsIgnoreCase("75063") || object.getString("responsecode").equalsIgnoreCase("86001") || object.getString("responsecode").equalsIgnoreCase("86002")) {
                            handler.chechStatus(object);
                            if (object.has("apiCommonResposne")) {
                                JSONObject object1 = object.getJSONObject("apiCommonResposne");
                                if (object1 != null) {
                                    String balance = object1.getString("runningBalance");
                                    for (int i = 0; i < MainActivity.pozoArrayList.size(); i++) {
                                        if (MainActivity.pozoArrayList.get(i).getHeaderID().equalsIgnoreCase("1"))
                                            MainActivity.pozoArrayList.get(i).setHeaderValue(balance);
                                    }
                                }
                            }
                        } else
                            responseMSg(object);
                    } else
                        responseMSg(object);
                } else {
                    customDialog_Common("No Internet Connectivity");
                }

            } else {
                customDialog_Common(responseData);
            }
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

    AlertDialog alertDialog,alertDialog1;

    protected void customDialog_Common(String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.setView(alertLayout);
            alertDialog = dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void customDialog(String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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
            dialog.setView(alertLayout);
            alertDialog1 = dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void responseMSg(JSONObject object) {
        try {
            if (object.has("responseMessage"))
                customDialog_Common(object.getString("responseMessage"));
            else if (object.has("responseMsg"))
                customDialog_Common(object.getString("responseMsg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
