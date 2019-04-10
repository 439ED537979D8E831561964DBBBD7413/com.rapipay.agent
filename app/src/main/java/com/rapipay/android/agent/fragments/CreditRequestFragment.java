package com.rapipay.android.agent.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.PaymentAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.BaseFragment;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import me.grantland.widget.AutofitTextView;

public class CreditRequestFragment extends BaseFragment implements RequestHandler, View.OnClickListener, CustomInterface {
    final private static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    Spinner select_mode;
    TextView bank_select;
    ArrayList<PaymentModePozo> list_payment;
    TextView input_account, input_remark, input_transid, input_amount, input_code;
    AutofitTextView date1_text, image;
    private static final int CAMERA_REQUEST = 1888;
    private int SELECT_FILE = 1;
    private String filePath = "", paymode = "";
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    View rv;
    protected ArrayList<RapiPayPozo> list;
    AppCompatButton btn_fund;
    private int selectedDate, selectedMonth, selectedYear;
    String months = null, dayss = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.credit_request_layout, container, false);
        initialize(rv);
        if (BaseCompactActivity.db != null && BaseCompactActivity.db.getDetails_Rapi())
            list = BaseCompactActivity.db.getDetails();
        else
            dbNull(CreditRequestFragment.this);
        return rv;
    }

    private void initialize(View view) {
        image = (AutofitTextView) view.findViewById(R.id.images);
        image.setOnClickListener(this);
        date1_text = (AutofitTextView) view.findViewById(R.id.date);
        date1_text.setHint(getResources().getString(R.string.p_dater));
        date1_text.setOnClickListener(this);
        bank_select = (TextView) view.findViewById(R.id.bank_select);
        select_mode = (Spinner) view.findViewById(R.id.select_mode);
        input_remark = (TextView) view.findViewById(R.id.input_remark);
        input_transid = (TextView) view.findViewById(R.id.input_transid);
        input_amount = (TextView) view.findViewById(R.id.input_amount);
        input_code = (TextView) view.findViewById(R.id.input_code);
        input_account = (TextView) view.findViewById(R.id.input_account);
        btn_fund = (AppCompatButton) view.findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String condition = "where " + RapipayDB.COLOMN_CREDITBANK + "='Y'";
                ArrayList<String> list_bank = BaseCompactActivity.db.geBankDetails(condition);
                customSpinner(bank_select, "Select Bank", list_bank, null);
            }
        });
        list_payment = BaseCompactActivity.db.getPaymenttDetails();
        if (list_payment.size() != 0)
            select_mode.setAdapter(new PaymentAdapter(getActivity(), list_payment));
        select_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    paymode = list_payment.get(position).getPaymentMode();
                else
                    paymode = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rv.findViewById(R.id.todate).setOnClickListener(toDateClicked);
        rv.findViewById(R.id.date).setOnClickListener(toDateClicked);
        ImageView toimage = (ImageView) rv.findViewById(R.id.toimage);
        toimage.setOnClickListener(toDateClicked);
        toimage.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("CREDIT_FUND_REQUEST")) {
                    customDialog_Ben(object.getString("responseMessage"), "CREDIT FUND REQUEST");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("SESSIONEXPIRE")) {
            jumpPage();
            clear();
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    AlertDialog alertDialog;

    private void customDialog_Ben(String msg, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.GONE);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(msg);
        otpView.setVisibility(View.VISIBLE);
        TextView texttitle = (TextView) alertLayout.findViewById(R.id.dialog_title);
        texttitle.setText(title);
        dialog.setView(alertLayout);
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                alertDialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.show();
    }

    private void clear() {
        input_transid.setText("");
        input_amount.setText("");
        input_code.setText("");
        input_account.setText("");
        input_remark.setText("");
        date1_text.setText("");
        image.setText("");
        imageBase64 = "";
        bank_select.setText("Select Bank");
        filePath = "";
        paymode = "";
        image.setError(null);
        date1_text.setError(null);
        select_mode.setAdapter(new PaymentAdapter(getActivity(), list_payment));
    }

    public void loadIMEI() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestReadPhoneStatePermission();
        } else {
            doPermissionGrantedStuffs();
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        new AlertDialog.Builder(getActivity())
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.images:
                loadIMEI();
                break;
            case R.id.btn_fund:
                if (!ImageUtils.commonRegex(input_account.getText().toString(), 150, " ")) {
                    input_account.setError("Please enter valid data");
                    input_account.requestFocus();
                } else if (!ImageUtils.commonRegex(input_code.getText().toString(), 20, "0-9")) {
                    input_code.setError("Please enter valid data");
                    input_code.requestFocus();
                } else if (!ImageUtils.commonAmount(input_amount.getText().toString())) {
                    input_amount.setError("Please enter valid data");
                    input_amount.requestFocus();
                } else if (!ImageUtils.commonRegex(input_transid.getText().toString(), 30, "0-9")) {
                    input_transid.setError("Please enter valid data");
                    input_transid.requestFocus();
                } else if (bank_select.getText().toString().equalsIgnoreCase("Select Bank"))
                    bank_select.setError("Please enter valid data");
                else if (paymode.isEmpty())
                    Toast.makeText(getActivity(), "Please select payment mode.", Toast.LENGTH_SHORT).show();
//                else if (BaseCompactActivity.IS_CRIMAGE_REQUIRED == null && image.getText().toString().isEmpty()) {
//                    image.setError("Please Select Image");
//                    image.requestFocus();
//                }
                else if (BaseCompactActivity.IS_CRIMAGE_REQUIRED != null && BaseCompactActivity.IS_CRIMAGE_REQUIRED.equalsIgnoreCase("Y") && image.getText().toString().isEmpty()) {
                    image.setError("Please Select Image");
                    image.requestFocus();
                } else if (date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please select date");
                    date1_text.requestFocus();
                } else if (!paymode.isEmpty() && BaseCompactActivity.IS_CRIMAGE_REQUIRED == null)
                    new AsyncPostMethod(WebConfig.CRNF, credit_request().toString(), headerData, CreditRequestFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                else if (!paymode.isEmpty() && BaseCompactActivity.IS_CRIMAGE_REQUIRED != null && BaseCompactActivity.IS_CRIMAGE_REQUIRED.equalsIgnoreCase("Y") && !imageBase64.isEmpty())
                    new AsyncPostMethod(WebConfig.CRNF, credit_request().toString(), headerData, CreditRequestFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                else if (!paymode.isEmpty() && BaseCompactActivity.IS_CRIMAGE_REQUIRED != null && BaseCompactActivity.IS_CRIMAGE_REQUIRED.equalsIgnoreCase("N"))
                    new AsyncPostMethod(WebConfig.CRNF, credit_request().toString(), headerData, CreditRequestFragment.this, getActivity(), getString(R.string.responseTimeOut)).execute();
                else
                    Toast.makeText(getActivity(), "Please select mandatory fields", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    View.OnClickListener toDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getActivity());
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
                        date1_text.setText(dayss + "/" + months + "/" + year);
                        dialog.dismiss();
                    } else {

                        if (selectedDate != dayOfMonth) {
                            date1_text.setText(dayss + "/" + months + "/" + year);
                            dialog.dismiss();
                        } else {
                            if (selectedMonth != month) {
                                date1_text.setText(dayss + "/" + months + "/" + year);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageBase64 = "";
        image.setText("");
        if (data != null) {
            if (requestCode == CAMERA_REQUEST) {
                try {
                    String path = data.getStringExtra("ImagePath");
                    String imageType = data.getStringExtra("ImageType");
                    Bitmap bitmap = loadImageFromStorage(imageType, path);
                    setPic(bitmap);
                    image.setText(imageType + ".jpg");
                    image.setError(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                Bitmap thumbnail = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                imageBase64 = getBytesFromBitmap(addWaterMark(thumbnail));
                String[] splits = filePath.split("\\/");
                int len = splits.length;
                image.setText(splits[len - 1]);
                image.setError(null);
            }
        }
    }


    public JSONObject credit_request() {
        JSONObject jsonObject = new JSONObject();
        try {
            String remark = "";
            if (input_remark.getText().toString().isEmpty())
                remark = "";
            else if (!ImageUtils.commonRegex(input_remark.getText().toString(), 250, "0-9 ?/,._-"))
                remark = input_remark.getText().toString();
            jsonObject.put("serviceType", "CREDIT_FUND_REQUEST");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("parentID", list.get(0).getMobilno());
            if (!imageBase64.isEmpty())
                jsonObject.put("imgByteCode", imageBase64);
            else
                jsonObject.put("imgByteCode", "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");
            jsonObject.put("amount", input_amount.getText().toString());
            jsonObject.put("depositeDate", date1_text.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("bankName", bank_select.getText().toString());
            jsonObject.put("branchName", input_account.getText().toString());
            jsonObject.put("branchCode", input_code.getText().toString());
            jsonObject.put("banktransactionID", input_transid.getText().toString());
            jsonObject.put("remark", remark);
            jsonObject.put("payMode", paymode);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] b = stream.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
        return null;
    }

    @Override
    public void chechStat(String object) {

    }

}
