package com.rapipay.android.agent.main_directory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.finopaytech.finosdk.activity.MainTransactionActivity;
import com.finopaytech.finosdk.encryption.AES_BC;
import com.finopaytech.finosdk.helpers.Utils;
import com.finopaytech.finosdk.models.ErrorSingletone;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;


public class Fino_AEPS_BBPS_Activity extends Activity implements View.OnClickListener{
    CheckBox cbMicroATM, cbAEPS;
    RadioGroup rgTransactionType;
    RadioButton rbCashWithdrawal;
    RadioButton rbLts;
    RadioButton rbBalanceEnquiry;
    String strTransType = "";
    Button btnProceed;
    EditText etAmount;
    private boolean doubleBackToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_app_act);
        initView();
        rgTransactionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rb_lts:

                        strTransType = "Last Transaction Status";
                        etAmount.setHint("ClientRefID");
                        etAmount.setText("");
                        etAmount.setClickable(true);
                        etAmount.setEnabled(true);
                        etAmount.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;

                    case R.id.rb_cw:
                        etAmount.setClickable(true);
                        etAmount.setHint("Amount");
                        strTransType = "Cash Withdrawal";
                        etAmount.setText("");
                        etAmount.setEnabled(true);
                        etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;

                    case R.id.rb_be:
                        strTransType = "Balance Enquiry";
                        etAmount.setText("0");
                        etAmount.setClickable(false);
                        etAmount.setEnabled(false);
                        break;
                }
            }
        });

        cbMicroATM.setOnClickListener(this);
        cbAEPS.setOnClickListener(this);
        btnProceed.setOnClickListener(this);
    }

    private void initView()
    {
        cbMicroATM = findViewById(R.id.cb_micro_atm);
        cbAEPS = findViewById(R.id.cb_aeps);
        rgTransactionType = findViewById(R.id.rg_trans_type);
        rbCashWithdrawal = findViewById(R.id.rb_cw);
        rbLts = findViewById(R.id.rb_lts);
        rbBalanceEnquiry = findViewById(R.id.rb_be);
        btnProceed = findViewById(R.id.btn_proceed);
        etAmount = findViewById(R.id.et_amount);
        cbMicroATM.setChecked(true);
    }

    private void resetData(int id) {
        rbLts.setChecked(true);
        rbCashWithdrawal.setChecked(false);
        rbBalanceEnquiry.setChecked(false);
        cbMicroATM.setChecked(true);
        etAmount.setFocusable(true);
        etAmount.setHint("ClientRefID");
        strTransType = "Last Transaction Status";
        etAmount.setText("");
        if (id == R.id.cb_micro_atm)
        {
            cbAEPS.setChecked(false);
        } else if (id == R.id.cb_aeps)
        {
            cbMicroATM.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_proceed:
                if (validate())
                {
                    Intent intent = new Intent(this, MainTransactionActivity.class);
                    intent.putExtra("RequestData", getEncryptedRequest());
                    intent.putExtra("HeaderData", getEncryptedHeader());
                    intent.putExtra("ReturnTime", 5);// Application return time in second
                    startActivityForResult(intent, 1);
                    resetData(cbMicroATM.getId());
                }
                break;

            case R.id.cb_micro_atm:
                resetData(cbMicroATM.getId());
                break;

            case R.id.cb_aeps:
                resetData(cbAEPS.getId());
                break;
        }

    }

    private boolean validate() {
        if ((!cbMicroATM.isChecked()) && (!cbAEPS.isChecked()))
        {
            showOneBtnDialog(this, "Info", "Please select Transaction!", false);
            return false;
        } else if ((!rbLts.isChecked()) && (!rbCashWithdrawal.isChecked()) && (!rbBalanceEnquiry.isChecked())) {
            showOneBtnDialog(this, "Info", "Please select Transaction Type!", false);
            return false;
        } else if (etAmount.getText().toString().equals(""))
        {
            String msg = "";
            if(rbCashWithdrawal.isChecked())
            {
                msg = "Amount";
            }
            else if(rbLts.isChecked())
            {
                msg = "ClientRefID";
            }
            showOneBtnDialog(this, "Info", "Please enter "+msg+" !", false);
            return false;
        }
        return true;
    }

    private void showOneBtnDialog(final Context mContext, String title, String msg, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                hideKeyboard(mContext);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.show();
    }

    private void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public String getServiceID() {
        String clientRefID = "";
        if (cbMicroATM.isChecked())
        {
            if (rbLts.isChecked())
                clientRefID = Constants.SERVICE_MICRO_TS;
            if (rbCashWithdrawal.isChecked())
                clientRefID = Constants.SERVICE_MICRO_CW;
            if (rbBalanceEnquiry.isChecked())
                clientRefID = Constants.SERVICE_MICRO_BE;
        } else if (cbAEPS.isChecked())
        {
            if (rbLts.isChecked())
                clientRefID = Constants.SERVICE_AEPS_TS;
            if (rbCashWithdrawal.isChecked())
                clientRefID = Constants.SERVICE_AEPS_CW;
            if (rbBalanceEnquiry.isChecked())
                clientRefID = Constants.SERVICE_AEPS_BE;
        }
        return clientRefID;
    }

    public String getEncryptedRequest()
    {
        String strRequestData = "";
        JSONObject jsonRequestDataObj = new JSONObject(); // inner object request
        try {
            jsonRequestDataObj.put("MerchantId", Constants.MERCHANT_ID);
            jsonRequestDataObj.put("SERVICEID", getServiceID());
            jsonRequestDataObj.put("RETURNURL", Constants.RETURN_URL);
            jsonRequestDataObj.put("Version", Constants.VERSION);

            if(getServiceID().equals(Constants.SERVICE_AEPS_TS) || getServiceID().equals(Constants.SERVICE_MICRO_TS))
            {
                jsonRequestDataObj.put("Amount", "0");
                jsonRequestDataObj.put("ClientRefID", etAmount.getText().toString());
            }
            else
            {
                jsonRequestDataObj.put("Amount", etAmount.getText().toString());
                jsonRequestDataObj.put("ClientRefID", Utils.generateRefID(Constants.MERCHANT_ID));
            }
            strRequestData = Utils.replaceNewLine(AES_BC.getInstance().encryptEncode(jsonRequestDataObj.toString(), Constants.CLIENT_REQUEST_ENCRYPTION_KEY));
        } catch (Exception e) {
        }
        return strRequestData;
    }

    private static String getEncryptedHeader()
    {
        String strHeader = "";
        JSONObject header = new JSONObject();
        try {
            header.put("AuthKey", Constants.AUTHKEY);
            header.put("ClientId", Constants.CLIENTID);
            strHeader = Utils.replaceNewLine(AES_BC.getInstance().encryptEncode(header.toString(), Constants.CLIENT_HEADER_ENCRYPTION_KEY));
        } catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return strHeader;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null & resultCode == RESULT_OK && requestCode == 1)
        {
            String response;
            if (data.hasExtra("ClientResponse")) {
                response = data.getStringExtra("ClientResponse");
                String strDecryptResponse = AES_BC.getInstance().decryptDecode(Utils.replaceNewLine(response), Constants.CLIENT_REQUEST_ENCRYPTION_KEY);
                Utils.showOneBtnDialog(this, getString(com.finopaytech.finosdk.R.string.STR_INFO), strDecryptResponse, false);
            } else if (data.hasExtra("ErrorDtls")) {
                response = data.getStringExtra("ErrorDtls");
                String errorMsg="",errorDtlsMsg="";
                if (!response.equalsIgnoreCase("")) {
                    try {
                        String[] error_dtls = response.split("\\|");
                        if(error_dtls.length>0)
                        {
                            errorMsg = error_dtls[0];
                            Utils.showOneBtnDialog(this, getString(com.finopaytech.finosdk.R.string.STR_INFO), "Error Message : " + errorMsg , false);
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException exp)
                    { }
                }
            }
            ErrorSingletone.getFreshInstance();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (doubleBackToExit) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExit = true;
        Toast.makeText(this, getString(R.string.msg_double_click_to_exit) + " " + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExit = false;
            }
        }, 2000);
    }
}
