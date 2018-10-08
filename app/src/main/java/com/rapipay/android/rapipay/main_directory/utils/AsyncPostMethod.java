package com.rapipay.android.rapipay.main_directory.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;

import org.json.JSONObject;


public class AsyncPostMethod extends AsyncTask<String, String, String> {
    String url, xmlData, strHeaderData;
    HttpConnector connector;
    RequestHandler handler;
    CustomProgessDialog dialog;
    Context context;

    public AsyncPostMethod(String url, String xmlData, String strHeaderData, Context handler) {
        this.url = url;
        this.xmlData = xmlData;
        this.strHeaderData = strHeaderData;
        this.context = handler;
        this.handler = (RequestHandler) handler;
        dialog = new CustomProgessDialog(handler);
        this.context = context;
    }

    public AsyncPostMethod(String url, String xmlData, String strHeaderData, RequestHandler handler, Context context) {
        this.url = url;
        this.xmlData = xmlData;
        this.strHeaderData = strHeaderData;
        this.handler = handler;
        this.context = context;
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
            if (isNetworkAvailable(context))
                s = connector.postData(url, xmlData, strHeaderData);
            else {
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
        try {
            if (s != null) {
                if (!s.equalsIgnoreCase("false")) {
                    JSONObject object = new JSONObject(s);
                    if (object.has("responseCode")) {
                        if (object.getString("responseCode").equalsIgnoreCase("200") || object.getString("responseCode").equalsIgnoreCase("75059") || object.getString("responseCode").equalsIgnoreCase("300") || object.getString("responseCode").equalsIgnoreCase("101") || object.getString("responseCode").equalsIgnoreCase("75077") || object.getString("responseCode").equalsIgnoreCase("75115") || object.getString("responseCode").equalsIgnoreCase("75062") || object.getString("responseCode").equalsIgnoreCase("75061") || object.getString("responseCode").equalsIgnoreCase("75063"))
                            handler.chechStatus(object);
                        else
                            customDialog(object.getString("responseMessage"));
                    } else if (object.has("responsecode")) {
                        if (object.getString("responsecode").equalsIgnoreCase("200") || object.getString("responsecode").equalsIgnoreCase("75059") || object.getString("responsecode").equalsIgnoreCase("101") || object.getString("responsecode").equalsIgnoreCase("300") || object.getString("responsecode").equalsIgnoreCase("75077") || object.getString("responseCode").equalsIgnoreCase("75061") || object.getString("responseCode").equalsIgnoreCase("75063"))
                            handler.chechStatus(object);
                        else
                            customDialog(object.getString("responseMessage"));
                    } else
                        customDialog(object.getString("responseMessage"));
                } else {
                    customDialog("No Internet Connectivity");
//                    Toast.makeText(context,"No Internet Connectivity",Toast.LENGTH_SHORT).show();
                }
            } else {
                customDialog("Connection TimeOut, Please Try Again!");
            }
            dialog.hide_progress();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null)
                dialog.hide_progress();
        }
    }

    private void customDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) handler);
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
