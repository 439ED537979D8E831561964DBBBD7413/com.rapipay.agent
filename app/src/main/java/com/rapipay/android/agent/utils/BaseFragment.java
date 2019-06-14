package com.rapipay.android.agent.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.PMTBenefPozo;
import com.rapipay.android.agent.Model.PermissionPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.Model.SubAgentList;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.CustomSpinnerAdapter;
import com.rapipay.android.agent.adapter.PermissionCheckAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.main_directory.CameraKitActivity;
import com.rapipay.android.agent.main_directory.LoginScreenActivity;
import com.rapipay.android.agent.view.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class BaseFragment extends Fragment {
    protected LocalStorage localStorage;
    protected Dialog dialog, dialognew, dialognew1;
    //    protected AlertDialog alertDialog, newdialog;
    CustomInterface anInterface;
    protected boolean scan = false;
    protected String TYPE, customerType;
    protected ArrayList<RapiPayPozo> list;
    protected String imageBase64 = "";
    protected static final int CAMERA_REQUEST = 1888;
    protected int SELECT_FILE = 1;
    protected EditText newtpin;
    protected TextView ben_amount, con_ifsc;
    protected static final int CONTACT_PICKER_RESULT = 1;
    final protected static int PERMISSIONS_REQUEST_CAMERA_STATE = 1;
    final protected static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    ArrayList<String> left, right, medium, spinner_list;
    ArrayList<HeaderePozo> bottom;
    LinearLayout main_layout;
    protected boolean isNEFT = false;
    protected boolean btnstatus = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void handlercontrol() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                btnstatus = false;
            }
        }, 1000);
    }

    protected void contactRead(Intent data, TextView input_number) {
        if (data != null) {
            Uri contactData = data.getData();
            Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
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
                } else if (phoneNumber.startsWith("0")) {
                    if (phoneNumber.length() == 11) {
                        String str_getMOBILE = phoneNumber.substring(1);
                        input_number.setText(str_getMOBILE);
                    }
                } else if (phoneNumber.length() == 10) {
                    input_number.setText(phoneNumber);
                } else {
                    Toast.makeText(getActivity(), "Please select valid number.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void loadIMEI() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestReadPhoneStatePermission();
        } else {
            doPermissionGrantedStuffs();
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_CONTACTS)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
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
        } else if (requestCode == PERMISSIONS_REQUEST_CAMERA_STATE) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                doCameraPermissionGrantedStuffs();
//            } else {
//                alertPerm(getString(R.string.permissions_not_granted_read_phone_state), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        loadCamera();
//                    }
//                });
//            }
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        }
    }

    protected void deleteTables(String type) {
        localStorage.setActivityState(LocalStorage.ROUTESTATE, "0");
        localStorage.setActivityState(LocalStorage.EMI, "0");
        localStorage.setActivityState(LocalStorage.LOGOUT, "0");
        localStorage.setActivityState(LocalStorage.IMAGEPATH, "0");
        SQLiteDatabase dba = BaseCompactActivity.db.getWritableDatabase();
        dba.execSQL("delete from " + RapipayDB.TABLE_BANK);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYMENT);
        dba.execSQL("delete from " + RapipayDB.TABLE_STATE);
        dba.execSQL("delete from " + RapipayDB.TABLE_OPERATOR);
        dba.execSQL("delete from " + RapipayDB.TABLE_FOOTER);
        dba.execSQL("delete from " + RapipayDB.TABLE_TRANSFERLIST);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYERPAYEE);
        if (!type.equalsIgnoreCase("")) {
            dba.execSQL("delete from " + RapipayDB.TABLE_NAME);
            dba.execSQL("delete from " + RapipayDB.TABLE_MASTER);
            dba.execSQL("delete from " + RapipayDB.TABLE_KYC_PERSONAL);
            dba.execSQL("delete from " + RapipayDB.TABLE_KYC_ADDRESS);
            dba.execSQL("delete from " + RapipayDB.TABLE_KYC_BUISNESS);
            dba.execSQL("delete from " + RapipayDB.TABLE_KYC_VERIFICATION);
            dba.execSQL("delete from " + RapipayDB.TABLE_IMAGES);
        }
    }

    protected void jumpPage() {
        Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        deleteTables("ALL");
    }

    protected void dbNull(CustomInterface customInterface) {
        customDialog_Common("SESSIONEXPIRE", null, null, "Session Expired", null, "Your current session will get expired.", customInterface);
    }

    protected void customDialog_Common(String msg) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.customdialoresponse, null);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setText(this.getResources().getString(R.string.Alert));
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

    protected void customDialog_Common(final String type, JSONObject object, final Object ob, String msg, final String input, String output, final CustomInterface anInterface) {
        this.anInterface = anInterface;
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        final TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        AppCompatButton btn_regenerate = (AppCompatButton) alertLayout.findViewById(R.id.btn_regenerate);
        if (type.equalsIgnoreCase("NETWORKLAYOUT")) {
            btn_cancel.setText("Network User");
            btn_cancel.setTextSize(10);
            btn_ok.setText("Network Setting");
            btn_ok.setTextSize(10);
            dialog_cancel.setVisibility(View.VISIBLE);
            dialog.setContentView(alertLayout);
        }
        if (type.equalsIgnoreCase("ACTIVATELAYOUT")) {
            btn_ok.setText(input);
            btn_ok.setTextSize(10);
            dialog_cancel.setVisibility(View.VISIBLE);
            dialog.setContentView(alertLayout);
        }
        if (type.equalsIgnoreCase("SUBAGENTSERVICE")) {
            SubAgentList pozo = (SubAgentList) ob;
            if (pozo.getStatus().equalsIgnoreCase("N")) {
                btn_cancel.setText("Activate");
                btn_ok.setVisibility(View.GONE);
            } else if (pozo.getStatus().equalsIgnoreCase("Y"))
                btn_cancel.setText("Deactiviate");
            btn_cancel.setTextSize(10);
            btn_ok.setText("Update Service");
            btn_ok.setTextSize(10);
            dialog_cancel.setVisibility(View.VISIBLE);
            btn_regenerate.setText("Cancel");
            btn_regenerate.setVisibility(View.VISIBLE);
            dialog.setContentView(alertLayout);
        }
        try {
            if (type.equalsIgnoreCase("KYCLAYOUT") || type.equalsIgnoreCase("PENDINGREFUND") || type.equalsIgnoreCase("REFUNDTXN") || type.equalsIgnoreCase("SESSIONEXPIRRED") || type.equalsIgnoreCase("PENDINGLAYOUT")) {
                customView(alertLayout, output, dialog);
            } else if (type.equalsIgnoreCase("KYCLAYOUTS") || type.equalsIgnoreCase("KYCLAYOUTSS") || type.equalsIgnoreCase("LOGOUT") || type.equalsIgnoreCase("SESSIONEXPIRE")) {
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, output, dialog);
            } else if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
                alertLayout.findViewById(R.id.custom_service).setVisibility(View.VISIBLE);
                moneyTransgerFees(alertLayout, object, ob, null, output, msg, input);
            }
            if (type.equalsIgnoreCase("ACTIVATELAYOUT")) {
                customView(alertLayout, output, dialog);
            } else if (type.equalsIgnoreCase("CREATEAGENT")) {
                alertLayout.findViewById(R.id.createagen_lay).setVisibility(View.VISIBLE);
                first_name = (TextView) alertLayout.findViewById(R.id.first_name);
                last_name = (TextView) alertLayout.findViewById(R.id.last_name);
                mobile_num = (TextView) alertLayout.findViewById(R.id.mobile_num);
                cree_address = (TextView) alertLayout.findViewById(R.id.cree_address);
                bank_select = (TextView) alertLayout.findViewById(R.id.bank_select);
                pincode = (TextView) alertLayout.findViewById(R.id.pincode);
                bank_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> list_state = BaseCompactActivity.db.getState_Details();
                        customSpinner(bank_select, "Select State*", list_state, "");
                    }
                });
                dialog.setContentView(alertLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    if (type.equalsIgnoreCase("PENDINGREFUND")) {
                        anInterface.okClicked(input, ob);
                        dialog.dismiss();
                    } else if (type.equalsIgnoreCase("KYCLAYOUTS")) {
                        anInterface.okClicked(type, ob);
                        dialog.dismiss();
                    } else if (type.equalsIgnoreCase("TERMCONDITION")) {
                        anInterface.okClicked(type, ob);
                        dialog.dismiss();
                    } else if (type.equalsIgnoreCase("KYCLAYOUTS")) {
                        anInterface.okClicked(type, ob);
                        dialog.dismiss();
                    } else if (type.equalsIgnoreCase("CREATEAGENT")) {
                        if (first_name.getText().toString().isEmpty()) {
                            first_name.setError("Please enter first name");
                            first_name.requestFocus();
                        } else if (last_name.getText().toString().isEmpty()) {
                            last_name.setError("Please enter last name");
                            last_name.requestFocus();
                        } else if (mobile_num.getText().toString().length() != 10) {
                            mobile_num.setError("Please enter mobile number");
                            mobile_num.requestFocus();
                        } else if (cree_address.getText().toString().isEmpty()) {
                            cree_address.setError("Please enter address");
                            cree_address.requestFocus();
                        } else if (pincode.getText().toString().length() != 6) {
                            pincode.setError("Please enter pincode");
                            pincode.requestFocus();
                        } else {
                            anInterface.okClicked(type, ob);
                            dialog.dismiss();
                        }
                    } else {
                        anInterface.okClicked(type, ob);
                        dialog.dismiss();
                    }
                }
                handlercontrol();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    anInterface.cancelClicked(type, ob);
                    dialog.dismiss();
                }
                handlercontrol();
            }
        });
        btn_regenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected void customDialog_CommonNew(final String type, JSONObject object, final Object ob, String msg, final String input, String output, final CustomInterface anInterface) {
        this.anInterface = anInterface;
        dialognew = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        final TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        AppCompatButton btn_regenerate = (AppCompatButton) alertLayout.findViewById(R.id.btn_regenerate);
        ListView list_view_with_checkbox = (ListView) alertLayout.findViewById(R.id.list_view_with_checkbox);
        if (type.equalsIgnoreCase("ACTIVATELAYOUT")) {
            btn_ok.setText(input);
            btn_ok.setTextSize(10);
            dialog_cancel.setVisibility(View.VISIBLE);
            dialognew.setContentView(alertLayout);
        }
        if (type.equalsIgnoreCase("UPDATESERVICE")) {
            btn_cancel.setText("Close");
            btn_cancel.setTextSize(10);
            btn_ok.setText("Update Service Status");
            btn_ok.setTextSize(10);
            dialog_cancel.setVisibility(View.VISIBLE);
            dialognew.setContentView(alertLayout);
        }
        try {
            if (type.equalsIgnoreCase("ACTIVATELAYOUT")) {
                customView(alertLayout, output, dialognew);
            } else if (type.equalsIgnoreCase("UPDATESERVICE")) {
                createAgent(alertLayout, list_view_with_checkbox, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    anInterface.okClicked(type, ob);
                    dialognew.dismiss();
                }
                handlercontrol();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    anInterface.cancelClicked(type, ob);
                    dialognew.dismiss();
                }
                handlercontrol();
            }
        });
        btn_regenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognew.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognew.dismiss();
            }
        });
        dialognew.show();
        Window window = dialognew.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected ArrayList<String> arrayList;
    protected EditText increaselimit;

    protected void customDialog_List(final String type, JSONObject object, final Object ob, String msg, final String input, String output, final CustomInterface anInterface) {
        this.anInterface = anInterface;
        dialognew = new Dialog(getActivity());
        final ArrayList<PermissionPozo> medium = new ArrayList<PermissionPozo>();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.customlistview_layout, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        increaselimit = (EditText) alertLayout.findViewById(R.id.increaselimit);
        text.setText(msg);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        ListView list_view_with_checkbox = (ListView) alertLayout.findViewById(R.id.list_view_with_checkbox);
        final TextView input_text = (TextView) alertLayout.findViewById(R.id.input_text);
        increaselimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()!=0 && s.length()<10) {
                    input_text.setText(EnglishNumberToWords.convert(Integer.parseInt(s.toString())));
                    input_text.setVisibility(View.VISIBLE);
                }else
                    input_text.setVisibility(View.GONE);
            }
        });
//        list_view_with_checkbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
//                // Get user selected item.
//                Object itemObject = adapterView.getAdapter().getItem(itemIndex);
//
//                // Translate the selected item to DTO object.
//                PermissionPozo itemDto = (PermissionPozo) itemObject;
//
//                // Get the checkbox.
//                CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.list_view_item_checkbox);
//
//                // Reverse the checkbox and clicked item check state.
//                if (itemCheckbox.isChecked()) {
//                    medium.get(itemIndex).setChecked(false);
//                    itemCheckbox.setChecked(false);
//                    itemDto.setChecked(false);
//                } else {
//                    medium.get(itemIndex).setChecked(true);
//                    itemCheckbox.setChecked(true);
//                    itemDto.setChecked(true);
//
//                }
//
//                //Toast.makeText(getApplicationContext(), "select item text : " + itemDto.getItemText(), Toast.LENGTH_SHORT).show();
//            }
//        });

        try {
            JSONArray array = object.getJSONArray("serviceList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject.getString("status").equalsIgnoreCase("Y"))
                    medium.add(new PermissionPozo(jsonObject.getString("serviceId"), jsonObject.getString("serviceName"), jsonObject.getString("status"), true));
                else
                    medium.add(new PermissionPozo(jsonObject.getString("serviceId"), jsonObject.getString("serviceName"), jsonObject.getString("status"), false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (type.equalsIgnoreCase("UPDATESERVICE")) {
            btn_cancel.setText("Close");
            btn_cancel.setTextSize(10);
            btn_ok.setText("Update Service Status");
            btn_ok.setTextSize(10);
            dialognew.setContentView(alertLayout);
        }
        try {
            if (type.equalsIgnoreCase("UPDATESERVICE")) {
                createAgent(alertLayout, list_view_with_checkbox, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    arrayList = new ArrayList<>();
                    if (medium != null && medium.size() != 0) {
                        int size = medium.size();
                        for (int i = 0; i < size; i++) {
                            PermissionPozo dto = medium.get(i);
                            arrayList.add(dto.getServiceId());
                        }
                    }
                    if (arrayList.size() != 0)
                        anInterface.okClicked(type, ob);
                    dialognew.dismiss();
                }
                handlercontrol();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    anInterface.cancelClicked(type, ob);
                    dialognew.dismiss();
                }
                handlercontrol();
            }
        });
        dialognew.show();
        Window window = dialognew.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected TextView first_name, last_name, mobile_num, cree_address, bank_select, pincode;

    protected void createAgent(View alertLayout, ListView listView, JSONObject object) throws Exception {
        ArrayList<PermissionPozo> medium = new ArrayList<PermissionPozo>();
        try {
            JSONArray array = object.getJSONArray("serviceList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject.getString("status").equalsIgnoreCase("Y"))
                    medium.add(new PermissionPozo(jsonObject.getString("serviceId"), jsonObject.getString("serviceName"), jsonObject.getString("status"), true));
                else
                    medium.add(new PermissionPozo(jsonObject.getString("serviceId"), jsonObject.getString("serviceName"), jsonObject.getString("status"), false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (medium.size() != 0) {
            listView.setVisibility(View.VISIBLE);
            PermissionCheckAdapter listViewDataAdapter = new PermissionCheckAdapter(getActivity(), medium);
            listViewDataAdapter.notifyDataSetChanged();
            // Set data adapter to list view.
            listView.setAdapter(listViewDataAdapter);
        }

    }

    protected void moneyTransgerFees(View alertLayout, JSONObject object, Object ob, String ifsc_code, String name, String msg, String input) throws Exception {
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
        change.setText(object.getString("sForComm"));
        btn_amount_servide.setText(object.getString("txnAmount"));
        if (ob != null) {
            PMTBenefPozo pozo = (PMTBenefPozo) ob;
            if (!pozo.getAccount_Number().equalsIgnoreCase("null") || !pozo.getAccount_Number().equalsIgnoreCase(""))
                btn_account.setText(pozo.getAccount_Number());
            else
                alertLayout.findViewById(R.id.layout_account).setVisibility(View.GONE);
            if (!pozo.getBank_Details().equalsIgnoreCase("null") || !pozo.getBank_Details().equalsIgnoreCase(""))
                btn_bank.setText(pozo.getBank_Details());
            else
                alertLayout.findViewById(R.id.layout_bank).setVisibility(View.GONE);
        }
        if (input.equalsIgnoreCase("null"))
            alertLayout.findViewById(R.id.senderno).setVisibility(View.GONE);
        else
            btn_sendname.setText(input);
        if (name != null)
            btn_name.setText(name);
        dialog.setContentView(alertLayout);
    }

    protected void customView(View alertLayout, String output, Dialog dialog) throws Exception {
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(output);
        otpView.setVisibility(View.VISIBLE);
        dialog.setContentView(alertLayout);
    }

    protected CustomSpinnerAdapter adapter = null;

    protected void customSpinner(final TextView viewText, final String type, final ArrayList<String> list_spinner, final String typeCheck) {
        spinner_list = list_spinner;
        dialognew = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                if (con_ifsc != null)
                    con_ifsc.setText("");
                viewText.setText(list_spinner.get(position));
                viewText.setError(null);
                if (con_ifsc != null) {
                    String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + viewText.getText().toString() + "'";
                    String ifsccode = BaseCompactActivity.db.geBankIFSC(condition).get(0);
                    if (ifsccode.equalsIgnoreCase("NA") & typeCheck.equalsIgnoreCase("BC")) {
                        con_ifsc.setVisibility(View.VISIBLE);
                        isNEFT = true;
                    } else if (!ifsccode.equalsIgnoreCase("NA") & typeCheck.equalsIgnoreCase("BC")) {
                        con_ifsc.setVisibility(View.VISIBLE);
                        con_ifsc.setText(ifsccode);
                        isNEFT = false;
                    } else if (!ifsccode.equalsIgnoreCase("NA")) {
                        con_ifsc.setVisibility(View.VISIBLE);
                        con_ifsc.setText(BaseCompactActivity.db.geBankIFSC(condition).get(0));
                    }
                }
                dialognew.dismiss();
            }
        });
        dialognew.setCancelable(false);
        dialognew.setContentView(alertLayout);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognew.dismiss();
            }
        });
        dialognew.show();
        Window window = dialognew.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void selectImage() {
        final CharSequence[] items = {"Capture Image", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Image")) {
                    Intent intent = new Intent(getActivity(), CameraKitActivity.class);
                    intent.putExtra("ImageType", "creditRequestImage");
                    intent.putExtra("REQUESTTYPE", CAMERA_REQUEST);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (items[item].equals("Choose from Gallery")) {
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

    protected Bitmap addWaterMark(Bitmap src) {
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
        canvas.drawText(currentDateandTime, w / 4, h - 10, paint);

        return result;
    }

    protected void setPic(Bitmap mCurrentPhotoPath) {
        imageBase64 = getBytesFromBitmap(addWaterMark(mCurrentPhotoPath));
    }

    protected String getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] b = stream.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
        return null;
    }

    protected Bitmap loadImageFromStorage(String name, String path) {
        try {
            File f = new File(path, name);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean printDifference(Date startDate, Date endDate) {
        //milliseconds
        try {
//            Calendar c = Calendar.getInstance();
//            System.out.println("Current time => " + c.getTime());
//
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            Date endDate = mainDate(df.format(c.getTime()));

            long different = endDate.getTime() - startDate.getTime();

            System.out.println("startDate : " + startDate);
            System.out.println("endDate : " + endDate);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;
            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;
            if (elapsedDays >= 0 && elapsedDays <= 31)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Date mainDate(String date) {
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            return date1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        change.setText(object.getString("sForComm"));
        btn_amount_servide.setText(object.getString("txnAmount"));
        if (!pozo.getAccountno().equalsIgnoreCase("null"))
            btn_account.setText(pozo.getAccountno());
        else
            btn_account.setText("NA");
        btn_sendname.setText(input);
        if (msg.equalsIgnoreCase("Confirm Money Transfer?")) {
            String condition = "where " + RapipayDB.COLOMN_IFSC + "='" + pozo.getIfsc() + "'";
            if (BaseCompactActivity.db.geBank(condition).size() != 0)
                btn_bank.setText(BaseCompactActivity.db.geBank(condition).get(0));
            else
                btn_bank.setVisibility(View.GONE);
        } else
            btn_bank.setText(pozo.getBank());
        if (!pozo.getName().equalsIgnoreCase("null"))
            btn_name.setText(pozo.getName());
        else
            btn_name.setText("NA");
        newtpin = (EditText) alertLayout.findViewById(R.id.newtpin);
        if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y"))
            newtpin.setVisibility(View.VISIBLE);
        dialog.setContentView(alertLayout);
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
        change.setText(object.getString("sForComm"));
        btn_amount_servide.setText(object.getString("txnAmount"));
        if (accountNo != null)
            btn_account.setText(accountNo);
        btn_sendname.setText(input);
        if (ifsc_code != null) {
            String condition = "where " + RapipayDB.COLOMN_IFSC + "='" + ifsc_code + "'";
            btn_bank.setText(BaseCompactActivity.db.geBank(condition).get(0));
        }
        if (name != null)
            btn_name.setText(name);
        dialog.setContentView(alertLayout);
    }

    protected void customDialog_Ben(View alertLayout, BeneficiaryDetailsPozo pozo, Dialog dialog, String transType) {
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.btn_name);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.btn_account);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.btn_bank);
        TextView notverified = (TextView) alertLayout.findViewById(R.id.notverified);
        TextView btn_trantype = (TextView) alertLayout.findViewById(R.id.btn_trantype);
        TextView btn_ifsc = (TextView) alertLayout.findViewById(R.id.btn_ifsc);
        final TextView input_text = (TextView) alertLayout.findViewById(R.id.input_texts);
        btn_trantype.setText(transType);
        btn_ifsc.setText(pozo.getIfsc());
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
        if (pozo.getIfsc().equalsIgnoreCase("NOT-VEREFIED"))
            notverified.setVisibility(View.VISIBLE);
        else
            notverified.setVisibility(View.GONE);
        ben_amount = (TextView) alertLayout.findViewById(R.id.input_amount_ben);
        ben_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()!=0 && s.length()<10) {
                    input_text.setText(EnglishNumberToWords.convert(Integer.parseInt(s.toString())));
                    input_text.setVisibility(View.VISIBLE);
                }else
                    input_text.setVisibility(View.GONE);
            }
        });
        dialog.setContentView(alertLayout);
    }

    protected EditText btn_ifsc;

    protected void customAddBeneneft(View alertLayout, String ifsc, String accountNo, String beneName, String beneBank, Dialog dialog, String transType) {
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.neftbtn_name);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.neftbtn_account);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.neftbtn_bank);
        btn_ifsc = (EditText) alertLayout.findViewById(R.id.edit_ifsc);
        btn_ifsc.setText(ifsc);
        btn_account.setText(accountNo);
        btn_bank.setText(beneBank);
        btn_name.setText(beneName);
        dialog.setContentView(alertLayout);
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
        dialog.setContentView(alertLayout);
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
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.receipt_layout_new, null);
        alertLayout.setKeepScreenOn(true);
        ImageView receipt_logo = (ImageView) alertLayout.findViewById(R.id.receipt_logo);
        String condition = "where " + RapipayDB.IMAGE_NAME + "='invoiceLogo.jpg'";
        ArrayList<ImagePozo> imagePozoArrayList = BaseCompactActivity.db.getImageDetails(condition);
        if (imagePozoArrayList.size() != 0) {
            byteConvert(receipt_logo, imagePozoArrayList.get(0).getImagePath());
        }
        main_layout = (LinearLayout) alertLayout.findViewById(R.id.main_layout);
        TextView mediums = (TextView) alertLayout.findViewById(R.id.medium);
        main_layout.setDrawingCacheEnabled(true);
        main_layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        main_layout.layout(0, 0, main_layout.getMeasuredWidth(), main_layout.getMeasuredHeight());
        main_layout.buildDrawingCache(true);

        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);

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
                TextView recycler_text = (TextView) inflate.findViewById(R.id.recycler_text);
                if (k == 1)
                    recycler_text.setTypeface(recycler_text.getTypeface(), Typeface.BOLD);
                recycler_text.setText(left.get(k));
                listLeft.addView(inflate);
            }
        }
        if (right.size() != 0) {
            for (int j = 0; j < right.size(); j++) {
                View inflate = inflater.inflate(R.layout.receipt_list, null);
                TextView recycler_text = (TextView) inflate.findViewById(R.id.recycler_text);
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
        dialog.setContentView(alertLayout);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
                    anInterface.okClicked(type, object);
                    dialog.dismiss();
                }
                handlercontrol();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
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
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Uri apkURI = FileProvider.getUriForFile(
                                getActivity(),
                                getActivity().getApplicationContext()
                                        .getPackageName() + ".provider", f);
                        intent.putExtra(Intent.EXTRA_STREAM, apkURI);
                        intent.setType("image/png");
                        startActivity(Intent.createChooser(intent, "Share image via"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handlercontrol();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected void byteConvert(ImageView imageViews, byte[] decodedString) {
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageViews.setImageBitmap(decodedByte);
        imageViews.setVisibility(View.VISIBLE);
    }

    protected void customReceiptNewTransaction(final String type, final JSONObject object, final CustomInterface anInterface) {
        this.anInterface = anInterface;
        left = new ArrayList<>();
        right = new ArrayList<>();
        medium = new ArrayList<>();
        bottom = new ArrayList<>();
        String medium_value = "";
        int q = 0;
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
                    if (!jsonObject.getString("headerText").equalsIgnoreCase(medium_value)) {
                        medium_value = jsonObject.getString("headerText");
                        q = q + 1;
                    }
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        medium.add(jsonObject.getString("headerText") + " " + "NA");
                    else
                        medium.add(jsonObject.getString("headerText") + " " + jsonObject.getString("headerValue"));
                }
                if (jsonObject.getString("displayType").equalsIgnoreCase("D")) {
                    if (jsonObject.getString("headerValue").equalsIgnoreCase("null"))
                        bottom.add(new HeaderePozo(jsonObject.getString("headerText"), "NA", medium_value));
                    else
                        bottom.add(new HeaderePozo(jsonObject.getString("headerText"), jsonObject.getString("headerValue"), medium_value));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog = new Dialog(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.pmt_receipt_layout, null);
        alertLayout.setKeepScreenOn(true);
        ImageView receipt_logo = (ImageView) alertLayout.findViewById(R.id.receipt_logo);
        String condition = "where " + RapipayDB.IMAGE_NAME + "='invoiceLogo.jpg'";
        ArrayList<ImagePozo> imagePozoArrayList = BaseCompactActivity.db.getImageDetails(condition);
        if (imagePozoArrayList.size() != 0) {
            byteConvert(receipt_logo, imagePozoArrayList.get(0).getImagePath());
        }
        main_layout = (LinearLayout) alertLayout.findViewById(R.id.main_layout);

        main_layout.setDrawingCacheEnabled(true);
        main_layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        main_layout.layout(0, 0, main_layout.getMeasuredWidth(), main_layout.getMeasuredHeight());
        main_layout.buildDrawingCache(true);
        AutofitTextView markdate = (AutofitTextView) alertLayout.findViewById(R.id.markdate);
        TextView signature = (TextView) alertLayout.findViewById(R.id.signature);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);

        text.setText(type);
        LinearLayout listLeft = (LinearLayout) main_layout.findViewById(R.id.listLeft);
        LinearLayout listRight = (LinearLayout) main_layout.findViewById(R.id.listRight);
        LinearLayout listbottom = (LinearLayout) main_layout.findViewById(R.id.listbottom);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        ImageView share = (ImageView) alertLayout.findViewById(R.id.share);
        share.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        medium_value = "";
        if (left.size() != 0) {
            for (int k = 0; k < left.size(); k++) {
                View inflate = inflater.inflate(R.layout.receipt_list, null);
                TextView recycler_text = (TextView) inflate.findViewById(R.id.recycler_text);
                if (k == 1)
                    recycler_text.setTypeface(recycler_text.getTypeface(), Typeface.BOLD);
                recycler_text.setText(left.get(k));
                listLeft.addView(inflate);
            }
        }
        if (right.size() != 0) {
            for (int j = 0; j < right.size(); j++) {
                View inflate = inflater.inflate(R.layout.receipt_list, null);
                TextView recycler_text = (TextView) inflate.findViewById(R.id.recycler_text);
                if (j == 1)
                    recycler_text.setTypeface(recycler_text.getTypeface(), Typeface.BOLD);
                recycler_text.setText(right.get(j));
                listRight.addView(inflate);
            }
        }
//        if (medium.size() == 1)
//            mediums.setText(medium.get(0));
        if (bottom.size() != 0) {
            for (int i = 0; i < bottom.size(); i++) {
                View inflate = inflater.inflate(R.layout.bottom_layout_pmt, null);
                TextView mediums = (TextView) inflate.findViewById(R.id.medium_header);
                if (!bottom.get(i).getHeaderID().equalsIgnoreCase(medium_value)) {
                    mediums.setText(bottom.get(i).getHeaderID());
                    medium_value = bottom.get(i).getHeaderID();
                    mediums.setVisibility(View.VISIBLE);
                }
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
        dialog.setContentView(alertLayout);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                anInterface.okClicked(type, object);
                dialog.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnstatus == false) {
                    btnstatus = true;
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
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Uri apkURI = FileProvider.getUriForFile(
                                getActivity(),
                                getActivity().getApplicationContext()
                                        .getPackageName() + ".provider", f);
                        intent.putExtra(Intent.EXTRA_STREAM, apkURI);
                        intent.setType("image/png");
                        startActivityForResult(Intent.createChooser(intent, "Share image via"), 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handlercontrol();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected boolean date_Different(String fromdate, String toDate, int datedifferent) {
        String fromDate = fromdate;
        String ToDate = toDate;
        String dateBeforeString;
        String dateAfterString;
        if (fromDate.contains("/") == true) {
            dateBeforeString = fromDate.replace("/", "-");
            dateAfterString = ToDate.replace("/", "-");
        } else {
            dateBeforeString = fromDate;
            dateAfterString = ToDate;
        }
        LocalDate dateBefore = null;
        LocalDate dateAfter = null;
        long noOfDaysBetween = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateBefore = LocalDate.parse(dateBeforeString);
            dateAfter = LocalDate.parse(dateAfterString);
            noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
        }
//calculating number of days in between
        if (datedifferent <= noOfDaysBetween)
            return true;
        else
            return false;
    }
}
