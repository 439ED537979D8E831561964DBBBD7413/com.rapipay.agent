package com.rapipay.android.agent.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;

public class CustomProgessDialog {
    ProgressDialog progressDialog;

    public CustomProgessDialog(Context context){
        progressDialog = new ProgressDialog(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog));
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Please wait..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

    public void hide_progress(){
        progressDialog.dismiss();
    }
}
