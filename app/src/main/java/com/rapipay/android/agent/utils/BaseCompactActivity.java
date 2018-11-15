package com.rapipay.android.agent.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BottomAdapter;
import com.rapipay.android.agent.adapter.CustomSpinnerAdapter;
import com.rapipay.android.agent.adapter.ReceiptAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.main_directory.MainActivity;
import com.rapipay.android.agent.main_directory.PinVerification;

public class BaseCompactActivity extends AppCompatActivity {
    protected String imei;
    protected ArrayList<VersionPozo> versionPozoArrayList;
    protected AutofitTextView date2_text, date1_text;
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected Long tsLong;
    protected static String balance = null;
    protected static final int CONTACT_PICKER_RESULT = 1;
    protected TextView heading;
    public static RapipayDB db;
    protected ArrayList<RapiPayPozo> list;
    final protected static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    protected LocalStorage localStorage;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    private int selectedDate, selectedMonth, selectedYear;
    protected ImageView reset;
    String months = null, dayss = null;
    protected ImageView toimage, fromimage;
    ArrayList<String> left, right, medium, spinner_list;
    ArrayList<HeaderePozo> bottom;
    LinearLayout main_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        align_text_center();
        tsLong = System.currentTimeMillis() / 1000;
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        localStorage = LocalStorage.getInstance(this);
        hideKeyboard(this);
        if (db != null && db.getDetails_Rapi())
            list = db.getDetails();
    }

//    private void align_text_center() {
//        ActionBar ab = getSupportActionBar();
//        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.splash_screen));
//        TextView tv = new TextView(getApplicationContext());
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
//                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
//        tv.setLayoutParams(lp);
//        tv.setText(ab.getTitle());
//        tv.setTextSize(24.0f);
//        tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//        tv.setGravity(Gravity.CENTER);
//        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        ab.setCustomView(tv);
//    }

    protected String saveToInternalStorage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public JSONObject getJson_Validate(String mobileNo, String kycType, String parentID, String sessionKey, String sessionRefNo, String nodeAgent) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "KYC_PROCESS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", "KYCP" + tsLong.toString());
            jsonObject.put("agentId", parentID);
            jsonObject.put("mobileNo", mobileNo);
            jsonObject.put("kycType", kycType);
            jsonObject.put("responseUrl", WebConfig.RESPONSE_URL);
            if (nodeAgent.equalsIgnoreCase("")) {
                jsonObject.put("nodeAgentId", mobileNo);
                jsonObject.put("sessionRefNo", sessionRefNo);
                jsonObject.put("checkSum", GenerateChecksum.checkSum(sessionKey, jsonObject.toString()));
            } else {
                jsonObject.put("nodeAgentId", nodeAgent);
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void setBack_click(Context context) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    protected String format(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void deleteTables(String type) {
        SQLiteDatabase dba = db.getWritableDatabase();
        dba.execSQL("delete from " + RapipayDB.TABLE_BANK);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYMENT);
        dba.execSQL("delete from " + RapipayDB.TABLE_STATE);
        dba.execSQL("delete from " + RapipayDB.TABLE_OPERATOR);
        dba.execSQL("delete from " + RapipayDB.TABLE_FOOTER);
        dba.execSQL("delete from " + RapipayDB.TABLE_TRANSFERLIST);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYERPAYEE);
        if (!type.equalsIgnoreCase(""))
            dba.execSQL("delete from " + RapipayDB.TABLE_NAME);
    }

    protected AlertDialog.Builder dialog;
    protected AlertDialog alertDialog, newdialog;
    CustomInterface anInterface;

    protected void customDialog_Common(final String type, JSONObject object, final Object ob, String msg, final String input, String output, final CustomInterface anInterface) {
        this.anInterface = anInterface;
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (type.equalsIgnoreCase("NETWORKLAYOUT")) {
            btn_cancel.setText("Network User");
            btn_cancel.setTextSize(10);
            btn_ok.setText("Network Setting");
            btn_ok.setTextSize(10);
            dialog_cancel.setVisibility(View.VISIBLE);
            dialog.setView(alertLayout);
        }
        try {
            if (type.equalsIgnoreCase("KYCLAYOUT") || type.equalsIgnoreCase("PENDINGREFUND") || type.equalsIgnoreCase("REFUNDTXN") || type.equalsIgnoreCase("SESSIONEXPIRRED") || type.equalsIgnoreCase("PENDINGLAYOUT")) {
                customView(alertLayout, output);
            } else if (type.equalsIgnoreCase("KYCLAYOUTS") || type.equalsIgnoreCase("KYCLAYOUTSS") || type.equalsIgnoreCase("LOGOUT")) {
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, output);
            } else if (type.equalsIgnoreCase("TERMCONDITION")) {
                btn_cancel.setText("Decline");
                btn_cancel.setTextSize(10);
                btn_ok.setText("Accept");
                btn_ok.setTextSize(10);
                alertLayout.findViewById(R.id.accept_term).setVisibility(View.VISIBLE);
                customView_term(alertLayout, output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("PENDINGREFUND"))
                    anInterface.okClicked(input, ob);
                else if (type.equalsIgnoreCase("KYCLAYOUTS"))
                    anInterface.okClicked(type, ob);
                else if (type.equalsIgnoreCase("LOGOUT")) {
                    return_Page();
                } else if (type.equalsIgnoreCase("TERMCONDITION")) {
                    anInterface.okClicked(type, ob);
                } else
                    anInterface.okClicked(type, ob);
                alertDialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("TERMCONDITION"))
                    return_Page();
                else
                    anInterface.cancelClicked(type, ob);
                alertDialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }

    protected void return_Page() {
        Intent intent = new Intent(BaseCompactActivity.this, PinVerification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void serviceFee(View alertLayout, JSONObject object, BeneficiaryDetailsPozo pozo, String msg, String input) throws Exception {
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.btn_name_service);
        TextView btn_servicefee = (TextView) alertLayout.findViewById(R.id.btn_servicefee);
        btn_servicefee.setText(String.valueOf(Math.round(Double.parseDouble(object.getString("chargeServiceFee")) * 100.0) / 100.0));
        TextView btn_igst = (TextView) alertLayout.findViewById(R.id.btn_igst);
        btn_igst.setText(String.valueOf(Math.round(Double.parseDouble(object.getString("igst")) * 100.0) / 100.0));
        TextView btn_cgst = (TextView) alertLayout.findViewById(R.id.btn_cgst);
        btn_cgst.setText(String.valueOf(Math.round(Double.parseDouble(object.getString("cgst")) * 100.0) / 100.0));
        TextView btn_sgst = (TextView) alertLayout.findViewById(R.id.btn_sgst);
        btn_sgst.setText(String.valueOf(Math.round(Double.parseDouble(object.getString("sgst")) * 100.0) / 100.0));
        TextView btn_sendname = (TextView) alertLayout.findViewById(R.id.btn_sendname);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.btn_account_service);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.btn_bank_service);
        TextView btn_amount_servide = (TextView) alertLayout.findViewById(R.id.btn_amount_servide);
        TextView change = (TextView) alertLayout.findViewById(R.id.change);
//        if(object.getString("sForComm").equalsIgnoreCase("1"))
        change.setText(object.getString("sForComm"));
//        else if(object.getString("").equalsIgnoreCase("1"))
//            change.setText("Service Fee");
//        if (ben_amount == null)
//            btn_amount_servide.setText(object.getString("txnAmount"));
//        else
        btn_amount_servide.setText(object.getString("txnAmount"));
        if (!pozo.getAccountno().equalsIgnoreCase("null"))
            btn_account.setText(pozo.getAccountno());
        else
            btn_account.setText("NA");
        btn_sendname.setText(input);
        if (msg.equalsIgnoreCase("Confirm Money Transfer?")) {
            String condition = "where " + RapipayDB.COLOMN_IFSC + "='" + pozo.getIfsc() + "'";
            btn_bank.setText(db.geBank(condition).get(0));
        } else
            btn_bank.setText(pozo.getBank());
        if (!pozo.getName().equalsIgnoreCase("null"))
            btn_name.setText(pozo.getName());
        else
            btn_name.setText("NA");
        dialog.setView(alertLayout);
    }

    protected void moneyTransgerFee(View alertLayout, JSONObject object, String accountNo, String ifsc_code, String name, String msg, String input) throws Exception {
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.btn_name_service);
        TextView btn_servicefee = (TextView) alertLayout.findViewById(R.id.btn_servicefee);
        btn_servicefee.setText(String.valueOf(Math.round(Double.parseDouble(object.getString("chargeServiceFee")) * 100.0) / 100.0));
        TextView btn_igst = (TextView) alertLayout.findViewById(R.id.btn_igst);
        btn_igst.setText(String.valueOf(Math.round(Double.parseDouble(object.getString("igst")) * 100.0) / 100.0));
        TextView btn_cgst = (TextView) alertLayout.findViewById(R.id.btn_cgst);
        btn_cgst.setText(String.valueOf(Math.round(Double.parseDouble(object.getString("cgst")) * 100.0) / 100.0));
        TextView btn_sgst = (TextView) alertLayout.findViewById(R.id.btn_sgst);
        btn_sgst.setText(String.valueOf(Math.round(Double.parseDouble(object.getString("sgst")) * 100.0) / 100.0));
        TextView btn_sendname = (TextView) alertLayout.findViewById(R.id.btn_sendname);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.btn_account_service);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.btn_bank_service);
        TextView btn_amount_servide = (TextView) alertLayout.findViewById(R.id.btn_amount_servide);
        TextView change = (TextView) alertLayout.findViewById(R.id.change);
//        if(object.getString("sForComm").equalsIgnoreCase("1"))
        change.setText(object.getString("sForComm"));
//        else if(object.getString("").equalsIgnoreCase("1"))
//            change.setText("Service Fee");
//        if (ben_amount == null)
//            btn_amount_servide.setText(object.getString("txnAmount"));
//        else
        btn_amount_servide.setText(object.getString("txnAmount"));
//        if (!pozo.getAccountno().equalsIgnoreCase("null"))
//            btn_account.setText(pozo.getAccountno());
//        else
        btn_account.setText(accountNo);
        btn_sendname.setText(input);
//        if (msg.equalsIgnoreCase("Sure you want to Transfer?")) {
        String condition = "where " + RapipayDB.COLOMN_IFSC + "='" + ifsc_code + "'";
        btn_bank.setText(db.geBank(condition).get(0));
//        } else
//            btn_bank.setText(pozo.getBank());
//        if (!pozo.getName().equalsIgnoreCase("null"))
        btn_name.setText(name);
//        else
//            btn_name.setText("NA");
        dialog.setView(alertLayout);
    }

    protected TextView ben_amount;

    protected void customDialog_Ben(View alertLayout, BeneficiaryDetailsPozo pozo) {
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.btn_name);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.btn_account);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.btn_bank);
        if (!pozo.getAccountno().equalsIgnoreCase("null"))
            btn_account.setText(pozo.getAccountno());
        else
            btn_account.setText("NA");
        if (!pozo.getBank().equalsIgnoreCase("null"))
            btn_bank.setText(pozo.getBank());
        else
            btn_bank.setText("NA");
        if (!pozo.getName().equalsIgnoreCase("null"))
            btn_name.setText(pozo.getName());
        else
            btn_name.setText("NA");
        ben_amount = (TextView) alertLayout.findViewById(R.id.input_amount_ben);
        dialog.setView(alertLayout);
    }

    protected void verifyTransferFee(View alertLayout, JSONObject object) throws Exception {
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.btn_name_verify);
        TextView btn_servicefee = (TextView) alertLayout.findViewById(R.id.bt_verify);
        btn_servicefee.setText(object.getString("subResponseMsg"));
        TextView btn_sendname = (TextView) alertLayout.findViewById(R.id.btn_sendname_verify);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.btn_account_verify);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.btn_bank_verify);
        TextView btn_amount_servide = (TextView) alertLayout.findViewById(R.id.btn_amount_verify);
        btn_amount_servide.setText(object.getString("bankRRNNumber"));
        btn_account.setText(object.getString("txnAmount"));
        btn_sendname.setText(object.getString("bankName"));
        btn_bank.setText(object.getString("bankAccountName"));
        btn_name.setText(object.getString("accountNo"));
        dialog.setView(alertLayout);
    }

    protected TextView otpView;

    protected void otpView(View alertLayout, JSONObject object) throws Exception {
        otpView = (TextView) alertLayout.findViewById(R.id.input_otp);
        dialog.setView(alertLayout);
    }

    protected void customView(View alertLayout, String output) throws Exception {
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(output);
        otpView.setVisibility(View.VISIBLE);
        dialog.setView(alertLayout);
    }

    protected void customView_term(View alertLayout, String output) throws Exception {
        TextView otpView = (TextView) alertLayout.findViewById(R.id.tv_linkon);
//        otpView.setText(Html.fromHtml(output, new Html.ImageGetter() {
//            @Override
//            public Drawable getDrawable(String source) {
//                byte[] data = Base64.decode(source, Base64.DEFAULT);
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                return new BitmapDrawable(getResources(), bitmap);
//            }
//        }, null));
        otpView.setText(Html.fromHtml(output));
        otpView.setVisibility(View.VISIBLE);
        dialog.setView(alertLayout);
    }

    protected View.OnClickListener toDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(BaseCompactActivity.this);
            dialog.setContentView(R.layout.datepickerview);
            dialog.setTitle("");

            DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
            selectedMonth = calendar.get(Calendar.MONTH);
            selectedYear = calendar.get(Calendar.YEAR);
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                    if (String.valueOf(month + 1).length() == 1)
                        months = "0" + String.valueOf(month + 1);
                    else
                        months = String.valueOf(month + 1);
                    if (String.valueOf(dayOfMonth).length() == 1)
                        dayss = "0" + String.valueOf(dayOfMonth);
                    else
                        dayss = String.valueOf(dayOfMonth);
                    if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                        date1_text.setText(year + "-" + months + "-" + dayss);
                        dialog.dismiss();
                    } else {

                        if (selectedDate != dayOfMonth) {
                            date1_text.setText(year + "-" + months + "-" + dayss);
                            dialog.dismiss();
                        } else {
                            if (selectedMonth != month) {
                                date1_text.setText(year + "-" + months + "-" + dayss);
                                dialog.dismiss();
                            }
                        }
                    }
                    date1_text.setError(null);
                    selectedDate = dayOfMonth;
                    selectedMonth = (month);
                    selectedYear = year;
                }
            });
            dialog.show();
        }
    };
    protected View.OnClickListener fromDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(BaseCompactActivity.this);
            dialog.setContentView(R.layout.datepickerview);
            dialog.setTitle("");

            DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
            selectedMonth = calendar.get(Calendar.MONTH);
            selectedYear = calendar.get(Calendar.YEAR);
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                    if (String.valueOf(month + 1).length() == 1)
                        months = "0" + String.valueOf(month + 1);
                    else
                        months = String.valueOf(month + 1);
                    if (String.valueOf(dayOfMonth).length() == 1)
                        dayss = "0" + String.valueOf(dayOfMonth);
                    else
                        dayss = String.valueOf(dayOfMonth);
                    if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
                        date2_text.setText(year + "-" + months + "-" + dayss);
                        dialog.dismiss();
                    } else {

                        if (selectedDate != dayOfMonth) {
                            date2_text.setText(year + "-" + months + "-" + dayss);
                            dialog.dismiss();
                        } else {
                            if (selectedMonth != month) {
                                date2_text.setText(year + "-" + months + "-" + dayss);
                                dialog.dismiss();
                            }
                        }
                    }
                    date2_text.setError(null);
                    selectedDate = dayOfMonth;
                    selectedMonth = (month);
                    selectedYear = year;
                }
            });
            dialog.show();
        }
    };

    protected void contactRead(Intent data, TextView input_number) {
        Uri contactData = data.getData();
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (phoneNumber.contains(" "))
                phoneNumber = phoneNumber.replaceAll(" ", "");
            if (phoneNumber.startsWith("+")) {
                if (phoneNumber.length() == 13) {
                    String str_getMOBILE = phoneNumber.substring(3);
                    input_number.setText(str_getMOBILE);
                } else if (phoneNumber.length() == 11) {
                    String str_getMOBILE = phoneNumber.substring(1);
                    input_number.setText(str_getMOBILE);
                } else if (phoneNumber.length() == 10) {
                    input_number.setText(phoneNumber);
                }
//
            } else if (phoneNumber.startsWith("0")) {
                if (phoneNumber.length() == 11) {
                    String str_getMOBILE = phoneNumber.substring(1);
                    input_number.setText(str_getMOBILE);
                }
            } else if (phoneNumber.length() == 10) {
                input_number.setText(phoneNumber);
            } else {
                Toast.makeText(this, "Please select valid number.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
//            checkAndRequestPermissions();
            requestReadPhoneStatePermission();
        } else {

            // READ_PHONE_STATE permission is already been granted.
            doPermissionGrantedStuffs();
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(BaseCompactActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                doPermissionGrantedStuffs();

            } else {
                alertPerm(getString(R.string.permissions_not_granted_read_phone_state), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadIMEI();
                    }
                });

            }
        }
    }

    private void alertPerm(String msg, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        }
    }

    protected void customReceipt(final String type, final JSONObject object, final CustomInterface anInterface) {
        this.anInterface = anInterface;
        left = new ArrayList<>();
        right = new ArrayList<>();
        medium = new ArrayList<>();
        bottom = new ArrayList<>();
        try {
            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject.getString("displayType").equalsIgnoreCase("L")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        left.add(jsonObject.getString("headerText") + " " + "NA");
                    else
                        left.add(jsonObject.getString("headerText") + " " + jsonObject.getString("headerValue"));
                }
                if (jsonObject.getString("displayType").equalsIgnoreCase("R")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        right.add(jsonObject.getString("headerText") + " " + "NA");
                    else
                        right.add(jsonObject.getString("headerText") + " " + jsonObject.getString("headerValue"));
                }
                if (jsonObject.getString("displayType").equalsIgnoreCase("M")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        medium.add(jsonObject.getString("headerText") + " " + "NA");
                    else
                        medium.add(jsonObject.getString("headerText") + " " + jsonObject.getString("headerValue"));
                }
                if (jsonObject.getString("displayType").equalsIgnoreCase("D")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        bottom.add(new HeaderePozo(jsonObject.getString("headerText"), "NA"));
                    else
                        bottom.add(new HeaderePozo(jsonObject.getString("headerText"), jsonObject.getString("headerValue")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.receipt_layout, null);

        main_layout = (LinearLayout) alertLayout.findViewById(R.id.main_layout);

        main_layout.setDrawingCacheEnabled(true);
        main_layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        main_layout.layout(0, 0, main_layout.getMeasuredWidth(), main_layout.getMeasuredHeight());
        main_layout.buildDrawingCache(true);

        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView mediums = (TextView) main_layout.findViewById(R.id.medium);
        text.setText(type);
        ListView listLeft = (ListView) main_layout.findViewById(R.id.listLeft);
        ListView listRight = (ListView) main_layout.findViewById(R.id.listRight);
        ListView listbottom = (ListView) main_layout.findViewById(R.id.listbottom);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        ImageView share = (ImageView) alertLayout.findViewById(R.id.share);
        share.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        if (left.size() != 0)
            listLeft.setAdapter(new ReceiptAdapter(left, this));
        if (right.size() != 0)
            listRight.setAdapter(new ReceiptAdapter(right, this));
        if (medium.size() == 1)
            mediums.setText(medium.get(0));
        if (bottom.size() != 0)
            listbottom.setAdapter(new BottomAdapter(bottom, this));
        dialog.setCancelable(false);
        dialog.setView(alertLayout);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anInterface.okClicked(type, object);
                alertDialog.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap b = Bitmap.createBitmap(main_layout.getDrawingCache());
                main_layout.setDrawingCacheEnabled(false);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "v2i.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.flush();
                    fo.close();
                    f.setReadable(true, false);
                    final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    intent.setType("image/png");
                    startActivity(Intent.createChooser(intent, "Share image via"));
                    alertDialog.dismiss();
                } catch (Exception e) {
                }
            }
        });
        alertDialog = dialog.show();
    }

    CustomSpinnerAdapter adapter = null;

    protected void customSpinner(final TextView viewText, final String type, final ArrayList<String> list_spinner) {
        spinner_list = list_spinner;
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_spinner_layout, null);
        TextView text = (TextView) alertLayout.findViewById(R.id.spinner_title);
        final EditText search = (EditText) alertLayout.findViewById(R.id.input_search);
        text.setText(type);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });
        ListView listLeft = (ListView) alertLayout.findViewById(R.id.list_view);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        if (spinner_list.size() != 0) {
            adapter = new CustomSpinnerAdapter(spinner_list, this);
            listLeft.setAdapter(adapter);
        }
        listLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewText.setText(list_spinner.get(position));
                viewText.setError(null);
                alertDialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setView(alertLayout);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }

    protected void customReceiptNew(final String type, final JSONObject object, final CustomInterface anInterface) {
        this.anInterface = anInterface;
        left = new ArrayList<>();
        right = new ArrayList<>();
        medium = new ArrayList<>();
        bottom = new ArrayList<>();
        try {
            JSONArray array = object.getJSONArray("getTxnReceiptDataList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject.getString("displayType").equalsIgnoreCase("L")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        left.add(jsonObject.getString("headerText") + " " + "NA");
                    else
                        left.add(jsonObject.getString("headerText") + " " + jsonObject.getString("headerValue"));
                }
                if (jsonObject.getString("displayType").equalsIgnoreCase("R")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        right.add(jsonObject.getString("headerText") + " " + "NA");
                    else
                        right.add(jsonObject.getString("headerText") + " " + jsonObject.getString("headerValue"));
                }
                if (jsonObject.getString("displayType").equalsIgnoreCase("M")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        medium.add(jsonObject.getString("headerText") + " " + "NA");
                    else
                        medium.add(jsonObject.getString("headerText") + " " + jsonObject.getString("headerValue"));
                }
                if (jsonObject.getString("displayType").equalsIgnoreCase("D")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        bottom.add(new HeaderePozo(jsonObject.getString("headerText"), "NA"));
                    else
                        bottom.add(new HeaderePozo(jsonObject.getString("headerText"), jsonObject.getString("headerValue")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.receipt_layout_new, null);

        main_layout = (LinearLayout) alertLayout.findViewById(R.id.main_layout);

        main_layout.setDrawingCacheEnabled(true);
        main_layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        main_layout.layout(0, 0, main_layout.getMeasuredWidth(), main_layout.getMeasuredHeight());
        main_layout.buildDrawingCache(true);

        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView mediums = (TextView) main_layout.findViewById(R.id.medium);
        text.setText(type);
        LinearLayout listLeft = (LinearLayout) main_layout.findViewById(R.id.listLeft);
        LinearLayout listRight = (LinearLayout) main_layout.findViewById(R.id.listRight);
        LinearLayout listbottom = (LinearLayout) main_layout.findViewById(R.id.listbottom);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        ImageView share = (ImageView) alertLayout.findViewById(R.id.share);
        share.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        if (left.size() != 0) {
            for (int k = 0; k < left.size(); k++) {
                View inflate = inflater.inflate(R.layout.receipt_list, null);
                AutofitTextView recycler_text = (AutofitTextView) inflate.findViewById(R.id.recycler_text);
                if (k == 1)
                    recycler_text.setTypeface(recycler_text.getTypeface(), Typeface.BOLD);
                recycler_text.setText(left.get(k));
                listLeft.addView(inflate);
            }
        }
        if (right.size() != 0) {
            for (int j = 0; j < right.size(); j++) {
                View inflate = inflater.inflate(R.layout.receipt_list, null);
                AutofitTextView recycler_text = (AutofitTextView) inflate.findViewById(R.id.recycler_text);
                if (j == 1)
                    recycler_text.setTypeface(recycler_text.getTypeface(), Typeface.BOLD);
                recycler_text.setText(right.get(j));
                listRight.addView(inflate);
            }
        }
        if (medium.size() == 1)
            mediums.setText(medium.get(0));
        if (bottom.size() != 0) {
            for (int i = 0; i < bottom.size(); i++) {
                View inflate = inflater.inflate(R.layout.bottom_layout, null);
                AutofitTextView btn_name = (AutofitTextView) inflate.findViewById(R.id.btn_name);
                TextView btn_p_bank = (TextView) inflate.findViewById(R.id.btn_p_bank);
                LinearLayout top = (LinearLayout) inflate.findViewById(R.id.top);
                if (i % 2 == 0)
                    top.setBackgroundColor(getResources().getColor(R.color.colorbackground));
                else
                    top.setBackgroundColor(getResources().getColor(R.color.white));
                if (bottom.get(i).getHeaderValue().equalsIgnoreCase("Txn. ID/RRN/STATUS")) {
                    top.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    btn_name.setTextColor(getResources().getColor(R.color.white));
                    btn_p_bank.setTextColor(getResources().getColor(R.color.white));
                } else {
                    btn_name.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    btn_p_bank.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                btn_name.setText(bottom.get(i).getHeaderData());
                btn_p_bank.setText(bottom.get(i).getHeaderValue());
                listbottom.addView(inflate);
            }
        }
        dialog.setCancelable(false);
        dialog.setView(alertLayout);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anInterface.okClicked(type, object);
                alertDialog.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap b = Bitmap.createBitmap(main_layout.getDrawingCache());
                main_layout.setDrawingCacheEnabled(false);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "v2i.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.flush();
                    fo.close();
                    f.setReadable(true, false);
                    final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Uri apkURI = FileProvider.getUriForFile(
                            BaseCompactActivity.this,
                            BaseCompactActivity.this.getApplicationContext()
                                    .getPackageName() + ".provider", f);
                    intent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    intent.setType("image/png");
                    startActivity(Intent.createChooser(intent, "Share image via"));
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog = dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (localStorage.getActivityState(LocalStorage.LOGOUT).equalsIgnoreCase("LOGOUT")) {
            objTimer.cancel();
            count = 15 * 60 * 3500;
            objTimer.start();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (localStorage.getActivityState(LocalStorage.LOGOUT).equalsIgnoreCase("LOGOUT")) {
            objTimer.cancel();
            count = 15 * 60 * 3500;
            objTimer.start();
        }
    }

    boolean isRegister = false;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            isRegister = true;
            if (action.equalsIgnoreCase("android.intent.action.SCREEN_OFF")) {
                if (localStorage.getActivityState(LocalStorage.LOGOUT).equalsIgnoreCase("LOGOUT")) {
                    customDialogLog("LOGOUT", "Session Expired", "Your Session got expired");
                    Toast.makeText(BaseCompactActivity.this, "Your Session got expired", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mIntentReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRegister) {
            unregisterReceiver(mIntentReceiver);
            isRegister = false;
        }
    }

    static int count = 15 * 60 * 3500;
    int interval = 3000;
    CountDownTimer objTimer = new CountDownTimer(count, interval) {

        public void onTick(long millisUntilFinished) {
            count = count - interval;
            Log.e("TAG_INTERVAL", "" + count);
            if (count == 0)
                if (localStorage.getActivityState(LocalStorage.LOGOUT).equalsIgnoreCase("LOGOUT")) {
                    customDialogLog("LOGOUT", "Session Expired", "Your Session got expired");
                    Toast.makeText(BaseCompactActivity.this, "Your Session got expired", Toast.LENGTH_SHORT).show();
                }
            //Some code
        }

        public void onFinish() {
//            String className = BaseCompactActivity.this.getLocalClassName();
//            if (!(className.equalsIgnoreCase("main_directory.SpashScreenActivity") ||  className.equalsIgnoreCase("main_directory.LoginScreenActivity") || className.equalsIgnoreCase("main_directory.PinActivity") ||  className.equalsIgnoreCase("main_directory.PinVerification"))) {
//            if (localStorage.getActivityState(LocalStorage.LOGOUT).equalsIgnoreCase("LOGOUT")) {
//                customDialogLog("LOGOUT", "Session Expired", "Your Session got expired");
//                Toast.makeText(BaseCompactActivity.this, "Your Session got expired", Toast.LENGTH_SHORT).show();
//            }

            //Logout
        }
    };

    protected void customDialogLog(final String type, String msg, String output) {
//        dialog = new AlertDialog.Builder(BaseCompactActivity.this);
//        LayoutInflater inflater = getLayoutInflater();
//        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
//        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
//        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
//        text.setText(msg);
//        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
//        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
//        try {
//            if (type.equalsIgnoreCase("LOGOUT")) {
//                btn_cancel.setVisibility(View.GONE);
//                customView(alertLayout, output);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        dialog.setCancelable(false);
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (type.equalsIgnoreCase("LOGOUT")) {
        Intent intent = new Intent(BaseCompactActivity.this, PinVerification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        localStorage.setActivityState(LocalStorage.LOGOUT, "0");
        objTimer.cancel();
//                }
//                alertDialog.dismiss();
//            }
//        });
//        dialog_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//        alertDialog = dialog.show();
    }

    public JSONObject version(String emi) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "APP_LIVE_STATUS");
            jsonObject.put("requestType", "handset_CHannel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "ALS" + tsLong.toString());
            jsonObject.put("settingName", "Android");
            jsonObject.put("imeiNo", emi);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(emi, jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected void loadVersion(String emi) {
        new AsyncPostMethod(WebConfig.UAT, version(emi).toString(), headerData, BaseCompactActivity.this).execute();
    }

    protected void versionDetails(JSONArray array, VersionListener listener) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String xml = object.getString("headerValue");
                parseXml(xml, listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void parseXml(String xml, VersionListener listener) {
        versionPozoArrayList = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml)); // pass input whatever xml you have
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                VersionPozo pozo = new VersionPozo();
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    pozo.setName(xpp.getName());
                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                    pozo.setValue(xpp.getText());
                }
//                if (pozo.getName()!=null && pozo.getValue()!=null)
                versionPozoArrayList.add(pozo);
                eventType = xpp.next();
            }
            if (versionPozoArrayList.size() != 0)
                listener.checkVersion(versionPozoArrayList);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
