package com.rapipay.android.rapipay.main_directory.fragments;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Database.RapipayDB;
import com.rapipay.android.rapipay.main_directory.Model.PaymentModePozo;
import com.rapipay.android.rapipay.main_directory.Model.RapiPayPozo;
import com.rapipay.android.rapipay.main_directory.adapter.PaymentAdapter;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

import static android.app.Activity.RESULT_OK;

public class CreditRequestFragment extends Fragment implements RequestHandler, View.OnClickListener {

    Spinner bank_select, select_mode;
    ArrayList<String> list_bank;
    ArrayList<PaymentModePozo> list_payment;
    DatePickerDialog pickerDialog;
    TextView input_account, input_remark, input_transid, input_amount, input_code;
    AutofitTextView date_text, image;
    SimpleDateFormat dateFormatter;
    private static final int CAMERA_REQUEST = 1888;
    private int SELECT_FILE = 1;
    private String filePath = "", bankName = "", paymode = "", imageBase64 = "", headerData;
    View rv;
    protected ArrayList<RapiPayPozo> list;
    protected SimpleDateFormat format;
    protected Date date;
    AppCompatButton btn_fund;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rv = (View) inflater.inflate(R.layout.credit_request_layout, container, false);
        initialize(rv);
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        list = BaseCompactActivity.db.getDetails();
        return rv;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.credit_request_layout);
//        headerData = (WebConfig.BASIC_FUNDID + ":" + WebConfig.BASIC_PASSWORD);
//        initialize();
//        list = db.getDetails();
//    }

    private void initialize(View view) {
        image = (AutofitTextView) view.findViewById(R.id.images);
        image.setOnClickListener(this);
        date_text = (AutofitTextView) view.findViewById(R.id.date);
        date_text.setOnClickListener(this);
        bank_select = (Spinner) view.findViewById(R.id.bank_select);
        select_mode = (Spinner) view.findViewById(R.id.select_mode);
        input_remark = (TextView) view.findViewById(R.id.input_remark);
        input_transid = (TextView) view.findViewById(R.id.input_transid);
        input_amount = (TextView) view.findViewById(R.id.input_amount);
        input_code = (TextView) view.findViewById(R.id.input_code);
        input_account = (TextView) view.findViewById(R.id.input_account);
        btn_fund = (AppCompatButton) view.findViewById(R.id.btn_fund);
        btn_fund.setOnClickListener(this);
        String condition = "where " + RapipayDB.COLOMN_CREDITBANK + "='Y'";
        list_bank = BaseCompactActivity.db.geBankDetails(condition);
        list_payment = BaseCompactActivity.db.getPaymenttDetails();
        if (list_bank.size() != 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, list_bank);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bank_select.setAdapter(dataAdapter);
        }
//            bank_select.setAdapter(new SpinnerAdapters(getApplicationContext(), list_bank));
        if (list_payment.size() != 0)
            select_mode.setAdapter(new PaymentAdapter(getActivity(), list_payment));
        bank_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bankName = list_bank.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        select_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paymode = list_payment.get(position).getPaymentMode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("CREDIT_FUND_REQUEST")) {
                    customDialog(object.getString("responseMessage"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void customDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clear();
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void clear() {
        input_transid.setText("");
        input_amount.setText("");
        input_code.setText("");
        input_account.setText("");
        input_remark.setText("");
        date_text.setText("");
        image.setText("");
        imageBase64 = "";
        bankName = "";
        filePath = "";
        paymode = "";
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date:
                calender();
                break;
            case R.id.images:
                selectImage();
                break;
            case R.id.btn_fund:
                if (!bankName.isEmpty() && !paymode.isEmpty() && !filePath.isEmpty() && !imageBase64.isEmpty() && !input_account.getText().toString().isEmpty() && !input_amount.getText().toString().isEmpty() && !input_code.getText().toString().isEmpty() && !input_transid.getText().toString().isEmpty() && !date_text.getText().toString().isEmpty())
                    new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, credit_request().toString(), headerData, CreditRequestFragment.this, getActivity()).execute();
                break;
        }
    }

    private void calender() {
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date_text.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imageBase64 = getBytesFromBitmap(thumbnail);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                File destination = new File(Environment.getExternalStorageDirectory(),
                        formatter.format(now) + ".jpg");
                filePath = destination.toString();
                String[] splits = filePath.split("\\/");
                image.setText(splits[4]);
            } else if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                Bitmap thumbnail=null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                }catch (Exception e){
                    e.printStackTrace();
                }
                imageBase64 = getBytesFromBitmap(thumbnail);
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                String[] splits = filePath.split("\\/");
                int len = splits.length;
                image.setText(splits[len-1]);
            }
        }
    }

    public JSONObject credit_request() {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "CREDIT_FUND_REQUEST");
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("parentID", list.get(0).getMobilno());
            jsonObject.put("imgByteCode", imageBase64);
            jsonObject.put("amount", input_amount.getText().toString());
            jsonObject.put("depositeDate", date_text.getText().toString());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("bankName", bankName);
            jsonObject.put("branchName", input_account.getText().toString());
            jsonObject.put("branchCode", input_code.getText().toString());
            jsonObject.put("banktransactionID", input_transid.getText().toString());
            jsonObject.put("remark", input_remark.getText().toString());
            jsonObject.put("payMode", paymode);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getBytesFromBitmap(Bitmap bitmap) {
        String imageEncoded;
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] b = stream.toByteArray();
            imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            return imageEncoded;
        }
        return null;
    }

    @Override
    public void chechStat(String object) {

    }
}
