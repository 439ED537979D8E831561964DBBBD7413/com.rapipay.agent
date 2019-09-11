package com.rapipay.android.agent.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;

import com.rapipay.android.agent.R;

public class CustomProgessDialog {
    ProgressDialog progressDialog;

    public CustomProgessDialog(Context context){
        progressDialog = new ProgressDialog(new ContextThemeWrapper(context, R.style.MyAlertDialogStyle));
        progressDialog.setMessage("Please wait..."); // Setting Message
      //  progressDialog.setTitle("Please wait..."); // Setting Title
      //  progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

    public void hide_progress(){
        progressDialog.dismiss();
    }
}