package com.rapipay.android.agent.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Database.RapipayDB;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.CustomSpinnerAdapter;
import com.rapipay.android.agent.adapter.PaymentAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import static android.app.Activity.RESULT_OK;

public class CreditRequestFragment extends Fragment implements RequestHandler, View.OnClickListener, CustomInterface {
    final private static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    Spinner select_mode;
    TextView bank_select;
    ArrayList<PaymentModePozo> list_payment;
    DatePickerDialog pickerDialog;
    TextView input_account, input_remark, input_transid, input_amount, input_code;
    AutofitTextView date1_text, image;
    SimpleDateFormat dateFormatter;
    private static final int CAMERA_REQUEST = 1888;
    private int SELECT_FILE = 1;
    private String filePath = "", paymode = "", imageBase64 = "", headerData;
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
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        list = BaseCompactActivity.db.getDetails();
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
                customSpinner(bank_select, "Select Bank", list_bank);
            }
        });
        list_payment = BaseCompactActivity.db.getPaymenttDetails();
//        if (list_bank.size() != 0) {
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
//                    android.R.layout.simple_spinner_item, list_bank);
//            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            bank_select.setAdapter(dataAdapter);
//        }
        if (list_payment.size() != 0)
            select_mode.setAdapter(new PaymentAdapter(getActivity(), list_payment));
//        bank_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0)
//                    bankName = list_bank.get(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        select_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    paymode = list_payment.get(position).getPaymentMode();
                else
                    paymode="";
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
//                    customDialog(object.getString("responseMessage"));
                    customDialog_Ben(object.getString("responseMessage"), "CREDIT FUND REQUEST");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okClicked(String type, Object ob) {
        clear();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    AlertDialog alertDialog;

    private void customDialog_Ben(String msg, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
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

//    private void customDialog(String msg) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.app_name);
//        //Setting message manually and performing action on button click
//        builder.setMessage(msg)
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        clear();
//                        dialog.dismiss();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

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
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
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
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

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
                else if(paymode.isEmpty())
                    Toast.makeText(getActivity(), "Please select payment mode.", Toast.LENGTH_SHORT).show();
                else if(image.getText().toString().isEmpty()) {
                    image.setError("Please Select Image");
                    image.requestFocus();
                }else if(date1_text.getText().toString().isEmpty()) {
                    date1_text.setError("Please select date");
                    date1_text.requestFocus();
                }else if (!paymode.isEmpty() && !filePath.isEmpty() && !imageBase64.isEmpty())
                    new AsyncPostMethod(WebConfig.CRNF, credit_request().toString(), headerData, CreditRequestFragment.this, getActivity()).execute();
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
//    private void picDate() {
//        months=null;dayss=null;
//        rv.findViewById(R.id.date).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // custom dialog
//                final Dialog dialog = new Dialog(getActivity());
//                dialog.setContentView(R.layout.datepickerview);
//                dialog.setTitle("");
//
//                DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
//                selectedMonth = calendar.get(Calendar.MONTH);
//                selectedYear = calendar.get(Calendar.YEAR);
//                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
//
//                    @Override
//                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
//                        Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
//                        if(String.valueOf(month+1).length()==1)
//                            months = "0"+ String.valueOf(month+1);
//                        else
//                            months = String.valueOf(month+1);
//                        if(String.valueOf(dayOfMonth).length()==1)
//                            dayss = "0"+String.valueOf(dayOfMonth);
//                        else
//                            dayss = String.valueOf(dayOfMonth);
//                        if (selectedDate == dayOfMonth && selectedMonth == month && selectedYear == year) {
//                            date_text.setText(dayss + "/" + months + "/" + year);
//                            dialog.dismiss();
//                        } else {
//
//                            if (selectedDate != dayOfMonth) {
//                                date_text.setText(dayss + "/" + months + "/" + year);
//                                dialog.dismiss();
//                            } else {
//                                if (selectedMonth != month) {
//                                    date_text.setText(dayss + "/" + months + "/" + year);
//                                    dialog.dismiss();
//                                }
//                            }
//                        }
//                        selectedDate = dayOfMonth;
//                        selectedMonth = (month);
//                        selectedYear = year;
//                    }
//                });
//                dialog.show();
//            }
//        });
//    }
//
//    private void calender() {
//        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
//        Calendar newCalendar = Calendar.getInstance();
//        pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//                date_text.setText(dateFormatter.format(newDate.getTime()));
//            }
//        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//        pickerDialog.show();
//    }


    private void selectImage() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        contentSelectionIntent.setType("image/*");
//        Intent[] intentArray;
//        if (takePictureIntent != null) {
//            intentArray = new Intent[]{takePictureIntent, intent};
//        } else {
//            intentArray = new Intent[2];
//        }
//        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//
//        startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), SELECT_FILE);

        final CharSequence[] items = {"Capture Image", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Image")) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

//                } else if (items[item].equals("Choose from Gallery")) {
//                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    intent.setType("image/*");
//                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private Bitmap addWaterMark(Bitmap src) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE); // Text Color
        paint.setTextSize(10);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
        Bitmap waterMark = BitmapFactory.decodeResource(getResources(), R.drawable.rapipay);
//        canvas.drawBitmap(waterMark, 0, 0, paint);
        canvas.drawText(currentDateandTime, w/4, h-10, paint);

        return result;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imageBase64 = getBytesFromBitmap(addWaterMark(thumbnail));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                File destination = new File(Environment.getExternalStorageDirectory(),
                        formatter.format(now) + ".jpg");
                filePath = destination.toString();
                String[] splits = filePath.split("\\/");
                image.setText(splits[4]);
                image.setError(null);
            } else if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                Bitmap thumbnail = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageBase64 = getBytesFromBitmap(addWaterMark(thumbnail));
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
                image.setText(splits[len - 1]);
                image.setError(null);
            }
        }
    }

    public JSONObject credit_request() {
        Long tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            String remark="";
            if (input_remark.getText().toString().isEmpty())
                remark = "";
            else if (!ImageUtils.commonRegex(input_remark.getText().toString(), 250, "0-9 ?/,._-"))
                remark = input_remark.getText().toString();
            jsonObject.put("serviceType", "CREDIT_FUND_REQUEST");
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "CFR" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("parentID", list.get(0).getMobilno());
            jsonObject.put("imgByteCode", imageBase64);
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

    CustomSpinnerAdapter adapter = null;
    ArrayList<String> spinner_list;
    AlertDialog.Builder dialog;

    protected void customSpinner(final TextView viewText, final String type, final ArrayList<String> list_spinner) {

        spinner_list = list_spinner;
        dialog = new AlertDialog.Builder(getActivity());
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
            adapter = new CustomSpinnerAdapter(spinner_list, getActivity());
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

}
