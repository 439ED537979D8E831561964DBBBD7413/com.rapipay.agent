package com.rapipay.android.agent.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
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
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Database.RapipayRealmDB;
import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.CreaditPaymentModePozo;
import com.rapipay.android.agent.Model.HandsetRegistration;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.MasterPozo;
import com.rapipay.android.agent.Model.NewKYCPozo;
import com.rapipay.android.agent.Model.NewKycAddress;
import com.rapipay.android.agent.Model.NewKycBusiness;
import com.rapipay.android.agent.Model.NewKycPersion;
import com.rapipay.android.agent.Model.NewKycVerification;
import com.rapipay.android.agent.Model.PMTBenefPozo;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.Model.StatePozo;
import com.rapipay.android.agent.Model.TbNepalPaymentModePozo;
import com.rapipay.android.agent.Model.TbOperatorPozo;
import com.rapipay.android.agent.Model.TbRechargePozo;
import com.rapipay.android.agent.Model.TbTransitionPojo;
import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.Model.microaeps.Microdata;
import com.rapipay.android.agent.Model.microaeps.Microdata1;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.BottomAdapter;
import com.rapipay.android.agent.adapter.CustomSpinnerAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.main_directory.CameraKitActivity;
import com.rapipay.android.agent.main_directory.FOSLoginActivity;
import com.rapipay.android.agent.main_directory.LoginScreenActivity;
import com.rapipay.android.agent.main_directory.MainActivity;
import com.rapipay.android.agent.main_directory.PinVerification;
import com.rapipay.android.agent.view.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import me.grantland.widget.AutofitTextView;

import static com.example.rfplmantra.MantraActivity.display;

public class BaseCompactActivity extends AppCompatActivity {
    protected GoogleApiClient googleApiClient;
    protected final static int REQUEST_CHECK_SETTINGS_GPS = 0x5;
    protected final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    protected String imei;
    protected Location mylocation;
    protected ImageView delete_all;
    protected ArrayList<VersionPozo> versionPozoArrayList;
    protected AutofitTextView date2_text, date1_text;
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected static String balance = null;
    protected static final int CONTACT_PICKER_RESULT = 1;
    protected TextView heading;
    public static RapipayDB db;
    protected BluetoothAdapter btAdapter;
    protected static int REQUEST_BLUETOOTH = 101;
    protected ArrayList<RapiPayPozo> list, listOld;
    protected ArrayList<HandsetRegistration> listHandsetRegistration;
    final protected static int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    final protected static int PERMISSIONS_REQUEST_CAMERA_STATE = 1;
    protected LocalStorage localStorage;
    protected String headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
    protected int selectedDate, selectedMonth, selectedYear;
    protected ImageView reset;
    String months = null, dayss = null;
    protected ImageView toimage, fromimage;
    ArrayList<String> left, right, medium, spinner_list;
    public static ArrayList<HeaderePozo> pozoArrayList = new ArrayList<>();
    ArrayList<HeaderePozo> bottom;
    LinearLayout main_layout;
    protected String mCameraPhotoPath = null;
    protected static final String TAG = MainActivity.class.getSimpleName();
    protected ArrayList<String> listPath = new ArrayList<>();
    protected String TYPE = "";
    protected String transactionIDAEPS;
    protected PowerManager mPowerManager;
    protected PowerManager.WakeLock mWakeLock;
    public static String ENABLE_TPIN = null;
    public static String IS_CRIMAGE_REQUIRED = null;
    public static Realm realm;
    public static RapipayRealmDB dbRealm;
    protected TextView ben_amount, con_ifsc;
    protected boolean isNEFT = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRealm = new RapipayRealmDB();
        db = new RapipayDB(this);
        Realm.init(this);
        getRealmUpdate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        localStorage = LocalStorage.getInstance(this);
        hideKeyboard(this);
        if (dbRealm != null && dbRealm.getDetails_Rapi())
            list = dbRealm.getDetails();
        if (dbRealm != null && dbRealm.getDetails_Rapi_Handset())
            listHandsetRegistration = dbRealm.getDetailsHandset();
    }


    protected boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            //  Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {
            // Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    protected String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
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

    protected void customView(View alertLayout, String output, Dialog dialog) throws Exception {
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(output);
        otpView.setVisibility(View.VISIBLE);
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
                if (s.length() != 0 && s.length() < 10) {
                    input_text.setText("");
                    input_text.setText(EnglishNumberToWords.convert(Integer.parseInt(s.toString())) + " rupee");
                    input_text.setVisibility(View.VISIBLE);
                } else
                    input_text.setVisibility(View.GONE);
            }
        });
        dialog.setContentView(alertLayout);
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
            //String condition = "where " + RapipayDB.COLOMN_IFSC + "='" + pozo.getIfsc() + "'";
            String condition = pozo.getIfsc();
            if (BaseCompactActivity.dbRealm.geBank(condition).size() != 0)
                btn_bank.setText(BaseCompactActivity.dbRealm.geBank(condition).get(0));
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

    private void getRealmUpdate() {
        try {
            RealmConfiguration config = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
        } catch (Exception e) {
            e.printStackTrace();
            realm = Realm.getDefaultInstance();
        }
    }

    public boolean printDifference(Date startDate, Date endDate) {
        try {
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

    protected void loadImageFromStorage(String name, ImageView view, String path) {
        try {
            File f = new File(path, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            view.setImageBitmap(b);
            view.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

    protected String getBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    protected Bitmap base64Convert(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    protected void byteConvert(ImageView imageViews, byte[] decodedString) {
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageViews.setImageBitmap(decodedByte);
        imageViews.setVisibility(View.VISIBLE);
    }

    protected byte[] byteConvert(String encodedImage) {
        return Base64.decode(encodedImage, Base64.DEFAULT);
    }

    protected String saveToInternalStorage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
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

    public JSONObject getJson_Validate(String mobileNo, String kycType, String parentID, String sessionKey, String documentType, String PancardDetails, String sessionRefNo, String nodeAgent) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "KYC_PROCESS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentId", parentID);
            jsonObject.put("mobileNo", mobileNo);
            jsonObject.put("kycType", kycType);
            jsonObject.put("PancardDetails", PancardDetails);
            jsonObject.put("DocumentType", documentType);
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

    //    public void turnOnScreen(){
//        // turn on screen
//        Log.v("ProximityActivity", "ON!");
//        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
//        mWakeLock.acquire();
//    }
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
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

    protected void setBack_click1(Context context) {
        Intent intent = new Intent(this, PinVerification.class);
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

    protected void deletebankTables() {
        final RealmResults<Microdata1> microdata = realm.where(Microdata1.class).findAll();
        //    SQLiteDatabase dba = db.getWritableDatabase();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                microdata.deleteAllFromRealm();
            }
        });
    }

    protected void deleteHandsetRegistrationTables() {
        final RealmResults<HandsetRegistration> handsetRegistrations = realm.where(HandsetRegistration.class).findAll();
        //    SQLiteDatabase dba = db.getWritableDatabase();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                handsetRegistrations.deleteAllFromRealm();
            }
        });
    }

    protected void deleteTables(final String type) {
        localStorage.setActivityState(LocalStorage.ROUTESTATE, "0");
        localStorage.setActivityState(LocalStorage.EMI, "0");
        localStorage.setActivityState(LocalStorage.LOGOUT, "0");
        localStorage.setActivityState(LocalStorage.IMAGEPATH, "0");
        final RealmResults<RapiPayPozo> rapiPayPozoRealmResults = realm.where(RapiPayPozo.class).findAll();
        final RealmResults<MasterPozo> masterPozoRealmResults = realm.where(MasterPozo.class).findAll();
        final RealmResults<ImagePozo> imagePozoRealmResults = realm.where(ImagePozo.class).findAll();
        final RealmResults<PaymentModePozo> paymentModePozoRealmResults = realm.where(PaymentModePozo.class).findAll();
        final RealmResults<StatePozo> statePozoRealmResults = realm.where(StatePozo.class).findAll();
        final RealmResults<TbOperatorPozo> tbOperatorPozoRealmResults = realm.where(TbOperatorPozo.class).findAll();
        final RealmResults<NewKYCPozo> newKYCPozoRealmResults = realm.where(NewKYCPozo.class).findAll();
        final RealmResults<BankDetailsPozo> bankDetailsPozoRealmResults = realm.where(BankDetailsPozo.class).findAll();
        final RealmResults<HeaderePozo> headerePozoRealmResults = realm.where(HeaderePozo.class).findAll();
        final RealmResults<CreaditPaymentModePozo> creaditPaymentModePozos = realm.where(CreaditPaymentModePozo.class).findAll();
        final RealmResults<NewKycAddress> newKycAddresses = realm.where(NewKycAddress.class).findAll();
        final RealmResults<NewKycBusiness> newKycBusinesses = realm.where(NewKycBusiness.class).findAll();
        final RealmResults<NewKycPersion> newKycPersions = realm.where(NewKycPersion.class).findAll();
        final RealmResults<NewKycVerification> newKycVerifications = realm.where(NewKycVerification.class).findAll();
        final RealmResults<TbRechargePozo> tbRechargePozos = realm.where(TbRechargePozo.class).findAll();
        final RealmResults<TbNepalPaymentModePozo> tbNepalPaymentModePozos = realm.where(TbNepalPaymentModePozo.class).findAll();
        final RealmResults<TbTransitionPojo> tbTransitionPojos = realm.where(TbTransitionPojo.class).findAll();
        //    SQLiteDatabase dba = db.getWritableDatabase();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                paymentModePozoRealmResults.deleteAllFromRealm();
                statePozoRealmResults.deleteAllFromRealm();
                tbOperatorPozoRealmResults.deleteAllFromRealm();
                bankDetailsPozoRealmResults.deleteAllFromRealm();
                headerePozoRealmResults.deleteAllFromRealm();
                creaditPaymentModePozos.deleteAllFromRealm();
                newKycAddresses.deleteAllFromRealm();
                newKycBusinesses.deleteAllFromRealm();
                newKycPersions.deleteAllFromRealm();
                newKycVerifications.deleteAllFromRealm();
                tbRechargePozos.deleteAllFromRealm();
                tbNepalPaymentModePozos.deleteAllFromRealm();
                tbTransitionPojos.deleteAllFromRealm();
                if (!type.equalsIgnoreCase("")) {
                    rapiPayPozoRealmResults.deleteAllFromRealm();
                    masterPozoRealmResults.deleteAllFromRealm();
                    imagePozoRealmResults.deleteAllFromRealm();
                    newKYCPozoRealmResults.deleteAllFromRealm();
                }
            }
        });
    }

    protected void jumpPage() {
        Intent intent = new Intent(BaseCompactActivity.this, LoginScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        deleteTables("ALL");
    }

    protected void jumpPageFOS() {
        Intent intent = new Intent(BaseCompactActivity.this, FOSLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        deleteTables("ALL");
    }

    protected void dbNull(CustomInterface customInterface) {
        customDialog_Common("SESSIONEXPIRE", null, null, "Session Expired", null, "Your current session will get expired.", customInterface);
    }

    protected Dialog dialog, dialognew;
    CustomInterface anInterface;

    protected void customDialog_Common(final String type, final JSONObject object, final Object ob, String msg, final String input, String output, final CustomInterface anInterface) {
        this.anInterface = anInterface;
        dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) alertLayout.findViewById(R.id.dialog_cancel);
        text.setText(msg);
        btn_regenerate = (AppCompatButton) alertLayout.findViewById(R.id.btn_regenerate);
        btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        final CheckBox checkBox = (CheckBox) alertLayout.findViewById(R.id.consent_checked);
        if (type.equalsIgnoreCase("NETWORKLAYOUT")) {
            btn_cancel.setText("Network User");
            btn_cancel.setTextSize(10);
            btn_ok.setText("Network Setting");
            btn_ok.setVisibility(View.GONE);
            btn_ok.setTextSize(10);
            btn_regenerate.setText("Details");
            btn_regenerate.setTextSize(10);
            btn_regenerate.setVisibility(View.VISIBLE);
            dialog_cancel.setVisibility(View.VISIBLE);
            dialog.setContentView(alertLayout);
        }
        if (type.equalsIgnoreCase("KYCNEWLAYOUT")) {
            btn_cancel.setTextSize(10);
            btn_ok.setText("Update KYC!");
            btn_ok.setTextSize(10);
            dialog_cancel.setVisibility(View.VISIBLE);
            dialog.setContentView(alertLayout);
        }
        try {
            if (type.equalsIgnoreCase("KYCLAYOUTFOS") || type.equalsIgnoreCase("KYCLAYOUT") || type.equalsIgnoreCase("PENDINGREFUND") || type.equalsIgnoreCase("REFUNDTXN")
                    || type.equalsIgnoreCase("SESSIONEXPIRRED") || type.equalsIgnoreCase("PENDINGLAYOUT")) {
                customView(alertLayout, output);
            } else if (type.equalsIgnoreCase("OTPLAYOUTS")) {
                alertLayout.findViewById(R.id.otp_layout).setVisibility(View.VISIBLE);
                otpView(alertLayout, object);
            } else if (type.equalsIgnoreCase("KYCNEWLAYOUT")) {
                customView(alertLayout, output);
            } else if (type.equalsIgnoreCase("KYCEWLAYOUT")) {
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, output);
            } else if (type.equalsIgnoreCase("KYCLAYOUTS") || type.equalsIgnoreCase("KYCLAYOUTSS") || type.equalsIgnoreCase("LOGOUT") || type.equalsIgnoreCase("SESSIONEXPIRE")) {
                btn_cancel.setVisibility(View.GONE);
                customView(alertLayout, output);
            } else if (type.equalsIgnoreCase("TERMCONDITION")) {
                btn_cancel.setText("Decline");
                btn_cancel.setTextSize(10);
                btn_ok.setText("Accept");
                btn_ok.setTextSize(10);
                alertLayout.findViewById(R.id.accept_term).setVisibility(View.VISIBLE);
                customView_term(alertLayout, output);
            } else if (type.equalsIgnoreCase("CONSENTLAYOUT")) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_ok.setText("Submit");
                alertLayout.findViewById(R.id.consent_term).setVisibility(View.VISIBLE);
                TextView tv_linkosn = (TextView) alertLayout.findViewById(R.id.tv_linkosn);
                tv_linkosn.setText(output);
                tv_linkosn.setVisibility(View.VISIBLE);
                if (input.equalsIgnoreCase("Y")) {
                    checkBox.setChecked(true);
                    checkBox.setEnabled(false);
                    btn_ok.setVisibility(View.GONE);
                }
                dialog.setContentView(alertLayout);
            } else if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
                alertLayout.findViewById(R.id.custom_service).setVisibility(View.VISIBLE);
                moneyTransgerFees(alertLayout, object, ob, null, output, msg, input);
            } else if (type.equalsIgnoreCase("OTPLAYOUT")) {
                alertLayout.findViewById(R.id.otp_layout_pmt).setVisibility(View.VISIBLE);
                otpViewPMT(alertLayout, object);
            } else if (type.equalsIgnoreCase("CREATEAGENT")) {
                alertLayout.findViewById(R.id.createagen_lay).setVisibility(View.VISIBLE);
                createAgent(alertLayout, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  btn_ok.setClickable(false);
                if (type.equalsIgnoreCase("PENDINGREFUND")) {
                    anInterface.okClicked(input, ob);
                    dialog.dismiss();
                } else if (type.equalsIgnoreCase("LOGOUT")) {
                    return_Page();
                    dialog.dismiss();
                } else if (type.equalsIgnoreCase("OTPLAYOUT")) {
                    anInterface.okClicked(type, ob);
                } else if (type.equalsIgnoreCase("OTPLAYOUTS")) {
                    if (otpView.getText().toString().isEmpty()) {
                        otpView.setError("Please enter OTP");
                        otpView.requestFocus();
                    } else if (otpView.getText().toString().length() != 6) {
                        btn_ok.setClickable(true);
                        otpView.setError("Please enter OTP");
                        otpView.requestFocus();
                    } else {
                        anInterface.okClicked(type, object);
                        dialog.dismiss();
                    }
                } else if (type.equalsIgnoreCase("CONSENTLAYOUT")) {
                    if (checkBox.isChecked()) {
                        anInterface.okClicked(type, object);
                        dialog.dismiss();
                    } else
                        Toast.makeText(BaseCompactActivity.this, "Please check then proceed", Toast.LENGTH_SHORT).show();
                } else if (type.equalsIgnoreCase("Fund Transfer Confirmation")) {
                    if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y") && (newtpin.getText().toString().isEmpty() || newtpin.getText().toString().length() != 4)) {
                        newtpin.setError("Please enter TPIN");
                        newtpin.requestFocus();
                        btn_ok.setClickable(true);
                    } else {
                        anInterface.okClicked(type, ob);
//                        dialog.dismiss();
                    }
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
                    } else if (bank_select.getText().toString().isEmpty()) {
                        bank_select.setError("Please enter bank");
                        bank_select.requestFocus();
                    } else if (pincode.getText().toString().length() != 6) {
                        pincode.setError("Please enter pincode");
                        pincode.requestFocus();
                    } else {
                        anInterface.okClicked(type, object);
                        dialog.dismiss();
                    }
                } else {
                    anInterface.okClicked(type, ob);
                    dialog.dismiss();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cancel.setClickable(false);
                if (type.equalsIgnoreCase("TERMCONDITION"))
                    return_Page();
                else
                    anInterface.cancelClicked(type, ob);
                dialog.dismiss();
            }
        });
        btn_regenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_regenerate.setClickable(false);
                if (type.equalsIgnoreCase("NETWORKLAYOUT"))
                    anInterface.okClicked("Details", ob);
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


    protected void return_Page() {
        Intent intent = new Intent(BaseCompactActivity.this, PinVerification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected String getDataBase64(String data) {
        try {
            return Base64.encodeToString(data.getBytes("utf-8"), Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }

    protected EditText newtpin;
    protected String radio_Clicked = "";

    protected void serviceFee(View alertLayout, JSONObject object, BeneficiaryDetailsPozo pozo, String msg, String input, String type) throws Exception {
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
        RadioGroup radioGroup = (RadioGroup) alertLayout.findViewById(R.id.myRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.prepaid) {
                    radio_Clicked = "OTP";
                } else if (checkedId == R.id.postpaid) {
                    radio_Clicked = "IPIN";
                }
            }
        });
        btn_amount_servide.setText(object.getString("txnAmount"));
        if (!pozo.getAccountno().equalsIgnoreCase("null"))
            btn_account.setText(pozo.getAccountno());
        else
            btn_account.setText("NA");
        btn_sendname.setText(input);
        if (msg.equalsIgnoreCase("Confirm Money Transfer?")) {
            String condition = "where " + RapipayDB.COLOMN_IFSC + "='" + pozo.getIfsc() + "'";
            if (dbRealm.geBank(condition).size() != 0)
                btn_bank.setText(dbRealm.geBank(condition).get(0));
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
            btn_bank.setText(dbRealm.geBank(condition).get(0));
        }
        if (name != null)
            btn_name.setText(name);
        dialog.setContentView(alertLayout);
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
        newtpin = (EditText) alertLayout.findViewById(R.id.newtpin);
        if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y"))
            newtpin.setVisibility(View.VISIBLE);
        dialog.setContentView(alertLayout);
    }

    protected void customDialog_Ben(View alertLayout, BeneficiaryDetailsPozo pozo) {
        TextView btn_name = (TextView) alertLayout.findViewById(R.id.btn_name);
        TextView btn_account = (TextView) alertLayout.findViewById(R.id.btn_account);
        TextView btn_bank = (TextView) alertLayout.findViewById(R.id.btn_bank);
        TextView notverified = (TextView) alertLayout.findViewById(R.id.notverified);
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

    protected TextView otpView;

    protected void otpView(View alertLayout, JSONObject object) throws Exception {
        otpView = (TextView) alertLayout.findViewById(R.id.input_otp);
        if (!radio_Clicked.isEmpty())
            otpView.setHint("Enter " + radio_Clicked);
        else
            otpView.setHint("Enter OTP");
        otpView.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(6);
        otpView.setFilters(filterArray);
        dialog.setContentView(alertLayout);
    }

    protected void otpViewPMT(View alertLayout, JSONObject object) throws Exception {
        otpView = (TextView) alertLayout.findViewById(R.id.input_otppmt);
        dialog.setContentView(alertLayout);
    }

    protected TextView first_name, last_name, mobile_num, cree_address, bank_select, pincode;

    protected void createAgent(View alertLayout, JSONObject object) throws Exception {
        first_name = (TextView) alertLayout.findViewById(R.id.first_name);
        last_name = (TextView) alertLayout.findViewById(R.id.last_name);
        mobile_num = (TextView) alertLayout.findViewById(R.id.mobile_num);
        cree_address = (TextView) alertLayout.findViewById(R.id.cree_address);
        bank_select = (TextView) alertLayout.findViewById(R.id.bank_select);
        pincode = (TextView) alertLayout.findViewById(R.id.pincode);
        bank_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list_state1 = new ArrayList<>();
                ArrayList<StatePozo> list_state = dbRealm.getState_Details();
                for (int i = 0; i < list_state.size(); i++) {
                    list_state1.add(list_state.get(i).getHeaderValue());
                }
                customSpinner(bank_select, "Select State*", list_state1);
            }
        });
        dialog.setContentView(alertLayout);
    }

    protected void customView(View alertLayout, String output) throws Exception {
        TextView otpView = (TextView) alertLayout.findViewById(R.id.dialog_msg);
        otpView.setText(output);
        otpView.setVisibility(View.VISIBLE);
        dialog.setContentView(alertLayout);
    }

    protected void customView_term(View alertLayout, String output) throws Exception {
        TextView otpView = (TextView) alertLayout.findViewById(R.id.tv_linkon);
        otpView.setText(Html.fromHtml(output));
        otpView.setVisibility(View.VISIBLE);
        dialog.setContentView(alertLayout);
    }

    protected void customView_Consent(View alertLayout, String output, String input) throws Exception {
        TextView otpView = (TextView) alertLayout.findViewById(R.id.tv_linkon);
        CheckBox checkBox = (CheckBox) alertLayout.findViewById(R.id.consent_checked);
        otpView.setText(Html.fromHtml(output));
        otpView.setVisibility(View.VISIBLE);
        if (input.equalsIgnoreCase("Y"))
            checkBox.setChecked(true);
        dialog.setContentView(alertLayout);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Calendar calendar = Calendar.getInstance();
            selectedDate = calendar.get(Calendar.DAY_OF_MONTH);
            selectedMonth = calendar.get(Calendar.MONTH);
            selectedYear = calendar.get(Calendar.YEAR);
            return new DatePickerDialog(this,
                    myDateListener, selectedYear, selectedMonth, selectedDate);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        if (String.valueOf(month + 1).length() == 1)
            months = "0" + String.valueOf(month);
        else
            months = String.valueOf(month);
        if (String.valueOf(day).length() == 1)
            dayss = "0" + String.valueOf(day);
        else
            dayss = String.valueOf(day);
        if (selectedDate >= day && selectedMonth + 1 >= month && selectedYear >= year) {
            date1_text.setText(year + "-" + months + "-" + dayss);
            date1_text.setError(null);
        } else if (selectedYear > year) {
            date1_text.setText(year + "-" + months + "-" + dayss);
            date1_text.setError(null);
        } else {
            date1_text.setError("Please select correct date");
        }
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
    protected View.OnClickListener checkDateClicked = new View.OnClickListener() {
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
                    if (dayOfMonth <= selectedDate && (month + 1) <= (selectedMonth + 1) && year == selectedYear) {
                        date1_text.setText(year + "-" + months + "-" + dayss);
                        dialog.dismiss();
                    } else if (year < selectedYear) {
                        date1_text.setText(year + "-" + months + "-" + dayss);
                        dialog.dismiss();
                    } else {
                        date1_text.setText("");
                        Toast.makeText(BaseCompactActivity.this, "Future date selection are not allowed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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
        if (data != null) {
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
    }

    public static int intidevice = 0;

    public void loadIMEI() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestReadPhoneStatePermission();
        } else {
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
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
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doCameraPermissionGrantedStuffs();
            } else {
                alertPerm(getString(R.string.permissions_not_granted_read_phone_state), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadCamera();
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
        ArrayList<HeaderePozo> medium = new ArrayList<HeaderePozo>();
        try {
            JSONArray array = object.getJSONArray("objMposData");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject.getString("displayFlag").equalsIgnoreCase("D"))
                    medium.add(new HeaderePozo(jsonObject.getString("headerValue"), jsonObject.getString("headerData"), jsonObject.getString("headerId"), jsonObject.getString("displayFlag")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.receipt_layout, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
        text.setText(type);
        ListView listbottom = (ListView) alertLayout.findViewById(R.id.listbottom);
        AppCompatButton btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        AppCompatButton btn_cancel = (AppCompatButton) alertLayout.findViewById(R.id.btn_cancel);
        if (medium.size() != 0)
            listbottom.setAdapter(new BottomAdapter(medium, this));
        dialog.setCancelable(false);
        dialog.setContentView(alertLayout);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anInterface.okClicked(type, object);
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anInterface.cancelClicked(type, object);
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    CustomSpinnerAdapter adapter = null;

    protected void customSpinner(final TextView viewText, final String type, final ArrayList<String> list_spinner) {
        spinner_list = list_spinner;
        dialognew = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_spinner_layout, null);
        alertLayout.setKeepScreenOn(true);
        TextView text = (TextView) alertLayout.findViewById(R.id.spinner_title);
        final EditText search = (EditText) alertLayout.findViewById(R.id.input_search);
        text.setText(type);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    protected void customSpinner(final TextView viewText, final String type, final ArrayList<String> list_spinner, final String typeCheck) {
        spinner_list = list_spinner;
        dialognew = new Dialog(this);
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
                if (con_ifsc != null)
                    con_ifsc.setText("");
                viewText.setText(list_spinner.get(position));
                viewText.setError(null);
                if (con_ifsc != null) {
                    // String condition = "where " + RapipayDB.COLOMN__BANK_NAME + "='" + viewText.getText().toString() + "'";
                    String condition = viewText.getText().toString();
                    String ifsccode = BaseCompactActivity.dbRealm.geBankIFSC(condition).get(0);
                    if (ifsccode.equalsIgnoreCase("NA") & typeCheck.equalsIgnoreCase("BC")) {
                        con_ifsc.setVisibility(View.VISIBLE);
                        isNEFT = true;
                    } else if (!ifsccode.equalsIgnoreCase("NA") & typeCheck.equalsIgnoreCase("BC")) {
                        con_ifsc.setVisibility(View.VISIBLE);
                        con_ifsc.setText(ifsccode);
                        isNEFT = false;
                    } else if (!ifsccode.equalsIgnoreCase("NA")) {
                        con_ifsc.setVisibility(View.VISIBLE);
                        con_ifsc.setText(BaseCompactActivity.dbRealm.geBankIFSC(condition).get(0));
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


    protected void customReceiptCastSaleOut(final String type, ArrayList<String> left, ArrayList<String> right, ArrayList<String> bottom, ArrayList<String> medium, String amount, String name, final CustomInterface anInterface, String ss) {
        this.anInterface = anInterface;
        dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.cashout_receipt, null);
        alertLayout.setKeepScreenOn(true);
        ImageView receipt_logo = (ImageView) alertLayout.findViewById(R.id.receipt_logo);
        LinearLayout ln_for_below = (LinearLayout) alertLayout.findViewById(R.id.ln_for_below);
        RelativeLayout amount_layout = (RelativeLayout) alertLayout.findViewById(R.id.amount_layout);
        TextView amounts = (TextView) alertLayout.findViewById(R.id.amount);
        //  String condition = "where " + RapipayDB.IMAGE_NAME + "='invoiceLogo.jpg'";
        if (!ss.equalsIgnoreCase("other") || !ss.equalsIgnoreCase("others")) {
            ln_for_below.setVisibility(View.GONE);
            amount_layout.setVisibility(View.GONE);
            amounts.setVisibility(View.GONE);
            String condition = "loginLogo.jpg";
            ArrayList<ImagePozo> imagePozoArrayList = dbRealm.getImageDetails(condition);
            if (imagePozoArrayList.size() != 0) {
                byteConvert(receipt_logo, imagePozoArrayList.get(0).getImagePath());
            }
        }
        if (ss.equalsIgnoreCase("other")) {
            amount = "Cash Withdrawal";
        } else if (ss.equalsIgnoreCase("others")) {
            amount = "Balance Enquiry";
        }
        main_layout = (LinearLayout) alertLayout.findViewById(R.id.main_layout);
        main_layout.setDrawingCacheEnabled(true);
        main_layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        main_layout.layout(0, 0, main_layout.getMeasuredWidth(), main_layout.getMeasuredHeight());
        main_layout.buildDrawingCache(true);
        LinearLayout mediums = (LinearLayout) main_layout.findViewById(R.id.medium);
        TextView text = (TextView) alertLayout.findViewById(R.id.agent_name);
        text.setText(type);
        TextView custom_name = (TextView) alertLayout.findViewById(R.id.custom_name);
        amounts.setText("Rs  " + amount);
        custom_name.setText(name);
        LinearLayout listLeft = (LinearLayout) main_layout.findViewById(R.id.listLeft);
        LinearLayout listRight = (LinearLayout) main_layout.findViewById(R.id.listRight);
        LinearLayout listbottom = (LinearLayout) main_layout.findViewById(R.id.listbottom);
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        share = (ImageView) alertLayout.findViewById(R.id.share);
        share.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        if (left.size() != 0) {
            for (int k = 0; k < left.size(); k++) {
                View inflate = inflater.inflate(R.layout.pos_receipt, null);
                AutofitTextView recycler_text = (AutofitTextView) inflate.findViewById(R.id.recycler_text);
                recycler_text.setText(left.get(k));
                listLeft.addView(inflate);
            }
        }
        if (right.size() != 0) {
            for (int j = 0; j < right.size(); j++) {
                View inflate = inflater.inflate(R.layout.pos_receipt, null);
                AutofitTextView recycler_text = (AutofitTextView) inflate.findViewById(R.id.recycler_text);
                recycler_text.setText(right.get(j));
                listRight.addView(inflate);
            }
        }
        if (medium.size() != 0) {
            for (int j = 0; j < medium.size(); j++) {
                View inflate = inflater.inflate(R.layout.pos_mid_receipt, null);
                AutofitTextView recycler_text = (AutofitTextView) inflate.findViewById(R.id.recycler_text);
                recycler_text.setText(medium.get(j));
                mediums.addView(inflate);
            }
        }
        if (bottom.size() != 0) {
            for (int j = 0; j < bottom.size(); j++) {
                View inflate = inflater.inflate(R.layout.pos_receipt, null);
                AutofitTextView recycler_text = (AutofitTextView) inflate.findViewById(R.id.recycler_text);
                recycler_text.setText(bottom.get(j));
                listbottom.addView(inflate);
            }
        }
        dialog.setCancelable(false);
        dialog.setContentView(alertLayout);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setBack_click(getApplicationContext());
                btn_ok.setClickable(false);
                anInterface.okClicked(type, null);
                dialog.dismiss();
                /*Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(dashboard);*/
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share.setClickable(false);
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
                            BaseCompactActivity.this,
                            BaseCompactActivity.this.getApplicationContext()
                                    .getPackageName() + ".provider", f);
                    intent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    intent.setType("image/png");
                    startActivityForResult(Intent.createChooser(intent, "Share image via"), 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.receipt_layout_new, null);
        alertLayout.setKeepScreenOn(true);
        ImageView receipt_logo = (ImageView) alertLayout.findViewById(R.id.receipt_logo);
        //  String condition = "where " + RapipayDB.IMAGE_NAME + "='invoiceLogo.jpg'";
        String condition = "invoiceLogo.jpg";
        ArrayList<ImagePozo> imagePozoArrayList = dbRealm.getImageDetails(condition);
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
                anInterface.okClicked(type, object);
                dialog.dismiss();
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
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Uri apkURI = FileProvider.getUriForFile(
                            BaseCompactActivity.this,
                            BaseCompactActivity.this.getApplicationContext()
                                    .getPackageName() + ".provider", f);
                    intent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    intent.setType("image/png");
                    startActivityForResult(Intent.createChooser(intent, "Share image via"), 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void clickable() {
        try {
            btn_ok.setClickable(true);
            btn_cancel.setClickable(true);
            btn_regenerate.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    AppCompatButton btn_ok, btn_cancel, btn_regenerate;
    ImageView share;

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
        dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.pmt_receipt_layout, null);
        alertLayout.setKeepScreenOn(true);
        ImageView receipt_logo = (ImageView) alertLayout.findViewById(R.id.receipt_logo);
        // String condition = "where " + RapipayDB.IMAGE_NAME + "='invoiceLogo.jpg'";
        String condition = "invoiceLogo.jpg";
        ArrayList<ImagePozo> imagePozoArrayList = dbRealm.getImageDetails(condition);
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
        btn_ok = (AppCompatButton) alertLayout.findViewById(R.id.btn_ok);
        share = (ImageView) alertLayout.findViewById(R.id.share);
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
                btn_ok.setClickable(false);
                anInterface.okClicked(type, object);
                dialog.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share.setClickable(false);
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
                            BaseCompactActivity.this,
                            BaseCompactActivity.this.getApplicationContext()
                                    .getPackageName() + ".provider", f);
                    intent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    intent.setType("image/png");
                    startActivityForResult(Intent.createChooser(intent, "Share image via"), 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (localStorage.getActivityState(LocalStorage.LOGOUT).equalsIgnoreCase("LOGOUT")) {
            objTimer.cancel();
            count = 15 * 60 * 5500;
            objTimer.start();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (localStorage.getActivityState(LocalStorage.LOGOUT).equalsIgnoreCase("LOGOUT")) {
            objTimer.cancel();
            count = 15 * 60 * 5500;
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
                if (BaseCompactActivity.ENABLE_TPIN != null && BaseCompactActivity.ENABLE_TPIN.equalsIgnoreCase("Y")) {
                    objTimer.cancel();
                    count = 30 * 60 * 7500;
                    objTimer.start();
                } else if (localStorage.getActivityState(LocalStorage.LOGOUT).equalsIgnoreCase("LOGOUT")) {
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
    protected void onStop() {
        super.onStop();
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
        }

        public void onFinish() {
        }
    };

    protected void customDialogLog(final String type, String msg, String output) {
        Intent intent = new Intent(BaseCompactActivity.this, PinVerification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        localStorage.setActivityState(LocalStorage.LOGOUT, "0");
        objTimer.cancel();
    }

    public JSONObject version(String emi) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "APP_LIVE_STATUS");
            jsonObject.put("requestType", "handset_CHannel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("settingName", "Android");
            jsonObject.put("imeiNo", emi);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(emi, jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected void loadVersion(String emi) {
        new AsyncPostMethod(WebConfig.LOGIN_URL, version(emi).toString(), headerData, BaseCompactActivity.this, getString(R.string.responseTimeOut)).execute();
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

    public void loadCamera() {
        if (ActivityCompat.checkSelfPermission(BaseCompactActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            doCameraPermissionGrantedStuffs();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(BaseCompactActivity.this,
                Manifest.permission.CAMERA)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(BaseCompactActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA_STATE);
                    doPermissionGrantedStuffs();
                }
            });
        } else {
            ActivityCompat.requestPermissions(BaseCompactActivity.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA_STATE);
        }
    }

    private void doCameraPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(BaseCompactActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (TYPE.equalsIgnoreCase("internal")) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    selectPhotoCustomer();
                } else {
                    selectPhotoAgent();
                }
            } else if (TYPE.equalsIgnoreCase("outside") || TYPE.equalsIgnoreCase("pending"))
                selectPhotoAgent();
        }
    }

    protected void selectPhotoCustomer() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                Log.e(TAG, "Unable to create Image File", ex);
            }
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        startActivityForResult(Intent.createChooser(takePictureIntent, "Select images"), 1);
    }

    protected void selectPhotoAgent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                Log.e(TAG, "Unable to create Image File", ex);
            }
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent, intent};
        } else {
            intentArray = new Intent[2];
        }
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), 1);
    }

    protected File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    protected void deleteFile() {
        if (listPath.size() != 0)
            for (int i = 0; i < listPath.size(); i++) {
                File fdelete = new File(listPath.get(i));
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + listPath.get(i));
                    } else {
                        System.out.println("file not Deleted :" + listPath.get(i));
                    }
                }
            }
    }

    protected Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public boolean printDifference(Date startDate) {
        try {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = mainDate(df.format(c.getTime()));

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
            if (elapsedDays / 365 >= 18)
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

    protected JSONObject getCashOutDetailsold(String mobile, String txnAmmount, String serviceType, String requestChannel, String reqFor, String requestType, String blueToothAddress) {
        JSONObject jsonObject = new JSONObject();
        try {
            transactionIDAEPS = ImageUtils.miliSeconds();
            jsonObject.put("serviceType", serviceType);
            jsonObject.put("requestChannel", requestChannel);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", transactionIDAEPS);
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("customerMobile", mobile);
            jsonObject.put("senderName", "RapiPay");
            jsonObject.put("txnAmount", txnAmmount);
            if (reqFor.equalsIgnoreCase("AEPS")) {
                jsonObject.put("bluetoothAddress", blueToothAddress);
            } else
                jsonObject.put("bluetoothAddress", blueToothAddress.replaceAll(":", ""));
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("langitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("requestType", requestType);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected JSONObject getCashOutDetails2(String mobile, String txnAmmount, String serviceType, String requestChannel, String reqFor, String requestType, String blueToothAddress) {
        JSONObject jsonObject = new JSONObject();
        try {
            transactionIDAEPS = ImageUtils.miliSeconds();
            jsonObject.put("serviceType", serviceType);
            jsonObject.put("requestChannel", requestChannel);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", transactionIDAEPS);
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("customerMobile", mobile);
            jsonObject.put("senderName", "RapiPay");
            jsonObject.put("txnAmount", txnAmmount);
            if (reqFor.equalsIgnoreCase("AEPS")) {
                jsonObject.put("bluetoothAddress", blueToothAddress);
            } else
                jsonObject.put("bluetoothAddress", blueToothAddress.replaceAll(":", ""));
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("langitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("requestType", requestType);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    protected JSONObject getCashOutDetails(String mobile, String txnAmmount, String serviceType, String requestChannel, String reqFor, String requestType, String blueToothAddress) {
        JSONObject jsonObject = new JSONObject();
        try { //https://uat.rapipay.com/MposService/AEPSCashout
            transactionIDAEPS = ImageUtils.miliSeconds();
            jsonObject.put("serviceType", serviceType);
            jsonObject.put("requestChannel", requestChannel);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", transactionIDAEPS);
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("customerMobile", mobile);
            jsonObject.put("senderName", "RapiPay");
            jsonObject.put("txnAmount", txnAmmount);
            if (reqFor.equals("MATM"))
                jsonObject.put("aepsType", "aeps");
            else
                jsonObject.put("aepsType", "aeps1");
            if (reqFor.equalsIgnoreCase("AEPS")) {
                jsonObject.put("bluetoothAddress", blueToothAddress);
            } else
                jsonObject.put("bluetoothAddress", blueToothAddress.replaceAll(":", ""));
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("langitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("requestType", requestType);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

  /*  {"serviceType":"INITIATE_AEPS_CASHOUT",
            "aepsType":"AEPS2",
            "reqFor":"AEPS",
            "latitude":"",
            "bankName":"",
            "txnIP":"",
            "deviceName":"",
            "transactionID":"",
            "senderName":"",
            "initiatedFor":"AEPS_BALANCE_ENQ",
            "deviceSrno":"",
            "requestChannel":"AEPS_CHANNEL",
            "langitude":"0",
            "requestType":"AEPS-CASHOUT",
            "sessionRefNo":"",
            "aadharNo":"",
            "bankIinNo":"",
            "agentMobile":"",
            "responseUrl":"",
            "nodeAgentId":"",
            "clientIp":"",
            "checkSum":"",
            "customerMobile":"",
            "typeMobileWeb":"mobile",
            "txnAmount":}
*/

    /* protected JSONObject getCashOutDetail1(String mobile, String txnAmmount, String serviceType, String requestChannel, String reqFor, String requestType, String blueToothAddress) {
         JSONObject jsonObject = new JSONObject();
         try { //https://uat.rapipay.com/MposService/AEPSCashout
             transactionIDAEPS = ImageUtils.miliSeconds();
             jsonObject.put("serviceType", serviceType);
             jsonObject.put("requestChannel", requestChannel);
             jsonObject.put("typeMobileWeb", "mobile");
             jsonObject.put("transactionID", transactionIDAEPS);
             jsonObject.put("agentMobile", list.get(0).getMobilno());
             jsonObject.put("customerMobile", mobile);
             jsonObject.put("senderName", "RapiPay");
             jsonObject.put("txnAmount", txnAmmount);
             jsonObject.put("aepsType", "aeps2");
             jsonObject.put("deviceIMEI", localStorage.getActivityState(LocalStorage.EMI));
             if (reqFor.equalsIgnoreCase("AEPS")) {
                 jsonObject.put("bluetoothAddress", blueToothAddress);
             } else
                 jsonObject.put("bluetoothAddress", blueToothAddress.replaceAll(":", ""));
             jsonObject.put("reqFor", reqFor);
             jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
             jsonObject.put("langitude", String.valueOf(mylocation.getLongitude()));
             jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
             jsonObject.put("requestType", requestType);
             jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
         } catch (Exception e) {
             e.printStackTrace();
         }

         return jsonObject;
     }
 */
    protected JSONObject getCashOutDetail1(String deviceName, String mobile, String txnAmmount, String adharno, String username, String serviceType, String requestChannel, String reqFor, String requestType, String blueToothAddress, String innno, String bankName) {
        JSONObject jsonObject = new JSONObject();
        String initiatedfor = "";
        try { //https://uat.rapipay.com/MposService/AEPSCashout
            transactionIDAEPS = ImageUtils.miliSeconds();
            jsonObject.put("serviceType", "INITIATE_AEPS_CASHOUT");
            jsonObject.put("aepsType", "AEPS2");
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("bankName", bankName);
            jsonObject.put("txnIP", ImageUtils.ipAddress(getApplicationContext()));
            jsonObject.put("deviceName", deviceName);
            jsonObject.put("transactionID", transactionIDAEPS);
            jsonObject.put("senderName", username);
            jsonObject.put("initiatedFor", serviceType);
            jsonObject.put("deviceSrno", "NA");
            jsonObject.put("requestChannel", requestChannel);
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("requestType", "AEPS-CASHOUT");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("aadharNo", adharno);
            jsonObject.put("bankIinNo", innno);
            jsonObject.put("agentMobile", list.get(0).getMobilno());
            jsonObject.put("responseUrl", "");
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            //  jsonObject.put("clientIp",localStorage.getActivityState(LocalStorage.EMI));
            jsonObject.put("customerMobile", mobile);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("txnAmount", txnAmmount);
            jsonObject.put("langitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected JSONObject getCashOutDetails2(String clientRefID, String mobile, String requestData, String headerData, String txnAmmount, String serviceType, String requestChannel, String reqFor, String requestType, String blueToothAddress, String innno, String bankName) {
        JSONObject jsonObject = new JSONObject();
        try { //https://uat.rapipay.com/MposService/AEPSCashout
            transactionIDAEPS = ImageUtils.miliSeconds();
            jsonObject.put("serviceType", "AEPS_CASH_WITHDRAW");
            jsonObject.put("mobileNumber", mobile);
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            jsonObject.put("adhaarNumber", blueToothAddress);
            jsonObject.put("transactionID", transactionIDAEPS);
            jsonObject.put("paymentType", "B");
            jsonObject.put("requestRemarks", "mobile aeps");
            jsonObject.put("merchantPin", "b027291b0af8cde6ae6e30bf6056204b");
            jsonObject.put("deviceIMEI", localStorage.getActivityState(LocalStorage.EMI));
            jsonObject.put("merchantTransactionId", transactionIDAEPS);
            jsonObject.put("aepsType", "aeps2");
            jsonObject.put("bankName", bankName);
            if (display != null)
                jsonObject.put("bioData", base64(display));
            // jsonObject.put("encData", enccriptData(display));
            jsonObject.put("longitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("timestamp", gettime());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("subMerchantId", "rapipaym");
            jsonObject.put("requestType", "AEPS_CHANNEL");//nationalBankIdentificationNumber
            jsonObject.put("nationalBankIdentificationNumber", innno); // https://fingpayap.tapits.in/fingpay/getBankDetailsMasterData
            jsonObject.put("merchantUserName", list.get(0).getAgentName());
            jsonObject.put("languageCode", "en");
            jsonObject.put("indicatorforUID", "0");
            jsonObject.put("transactionType", "CW");
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("iCount", "0");
            jsonObject.put("pCount", "0");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("authentication", headerData);
            jsonObject.put("transactionAmount", txnAmmount);
            jsonObject.put("encData", requestData);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String gettime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    protected JSONObject getGetBalance(String clientRefID, String mobile, String requestData, String headerData, String txnAmmount, String serviceType, String requestChannel, String reqFor, String requestType, String blueToothAddress, String innno, String bankName) {
        JSONObject jsonObject = new JSONObject();
        try {
            transactionIDAEPS = ImageUtils.miliSeconds();
            jsonObject.put("serviceType", "AEPS_GET_BALANCE");
            jsonObject.put("mobileNumber", mobile);
            jsonObject.put("reqFor", reqFor);
            jsonObject.put("latitude", String.valueOf(mylocation.getLatitude()));
            if (txnAmmount == null) {
                txnAmmount = "0";
            }
            jsonObject.put("adhaarNumber", txnAmmount);
            jsonObject.put("transactionID", transactionIDAEPS);
            jsonObject.put("paymentType", "B");
            jsonObject.put("requestRemarks", "mobile aeps");
            jsonObject.put("merchantPin", "b027291b0af8cde6ae6e30bf6056204b");
            jsonObject.put("deviceIMEI", localStorage.getActivityState(LocalStorage.EMI));
            jsonObject.put("merchantTransactionId", transactionIDAEPS);
            jsonObject.put("bankName", bankName);
            jsonObject.put("aepsType", "aeps2");
            if (display != null)
                jsonObject.put("bioData", base64(display));
            // jsonObject.put("encData", enccriptData(display));
            jsonObject.put("longitude", String.valueOf(mylocation.getLongitude()));
            jsonObject.put("timestamp", gettime());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("subMerchantId", "rapipaym");
            jsonObject.put("requestType", "AEPS_CHANNEL");
            jsonObject.put("nationalBankIdentificationNumber", innno); // https://fingpayap.tapits.in/fingpay/getBankDetailsMasterData
            jsonObject.put("merchantUserName", list.get(0).getAgentName());
            jsonObject.put("languageCode", "en");
            jsonObject.put("indicatorforUID", "0");
            jsonObject.put("transactionType", "BE");
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("iCount", "0");
            jsonObject.put("pCount", "0");
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("authentication", headerData);
            jsonObject.put("encData", requestData);
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String base64(String display) {
        byte[] data = new byte[0];
        try {
            data = display.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

  /*  @RequiresApi(api = Build.VERSION_CODES.O)
    public String Hello(String display)
    {
        byte[] message;
        try
        {
            PublicKey publicKey = readPublicKey("public.der");
            PrivateKey privateKey = readPrivateKey("private.der");
            message = display.getBytes("UTF8");
            byte[] secret = encrypt(publicKey, message);
           // byte[] recovered_message = decrypt(privateKey, secret);
           // System.out.println(new String(recovered_message, "UTF8"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return message;
    }

    public static String PUBLIC_KEY = "2e8c1753-a1ce-46f4-8dc1-fa6e35a0e2b0";

    *//*static String enccriptData(String txt)
    {
        String encoded = "";
        byte[] encrypted = null;
        try {
            byte[] publicBytes = Base64.decode(PUBLIC_KEY, Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING"); //or try with "RSA"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encrypted = cipher.doFinal(txt.getBytes());
            encoded = Base64.encodeToString(encrypted, Base64.DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }*//*

    @RequiresApi(api = Build.VERSION_CODES.O)
    public byte[] readFileBytes(String filename) throws IOException
    {
        Path path = Paths.get(filename);
        return Files.readAllBytes(path);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PublicKey readPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(readFileBytes(filename));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicSpec);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PrivateKey readPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(readFileBytes(filename));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public byte[] encrypt(PublicKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }*/


    protected void selectImage(final int id1, final int id2, final String imageType) {
        final CharSequence[] items = {"Capture Image", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseCompactActivity.this);
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Image")) {
                    Intent intent = new Intent(BaseCompactActivity.this, CameraKitActivity.class);
                    intent.putExtra("ImageType", imageType);
                    intent.putExtra("REQUESTTYPE", id1);
                    startActivityForResult(intent, id1);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), id2);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        clickable();
        builder.show();
    }

    protected void responseMSg(JSONObject object) {
        try {
            if (object.has("responseMessage"))
                customDialog_Common(object.getString("responseMessage"));
            else if (object.has("responseMsg"))
                customDialog_Common(object.getString("responseMsg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void customDialog_Common(String msg) {
        try {
            final Dialog dialog = new Dialog(BaseCompactActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setText(getResources().getString(R.string.Alert));
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

    protected void customDialog_Common_device(String msg) {
        try {
            final Dialog dialog = new Dialog(BaseCompactActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setText(getResources().getString(R.string.Alert));
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
                        Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(dashboard);
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

    protected void customDialog_Matm_device(String msg) {
        try {
            final Dialog dialog = new Dialog(BaseCompactActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setText(getResources().getString(R.string.Alert));
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

    protected void customDialog_List_info(String msg) {
        try {
            final Dialog dialog = new Dialog(BaseCompactActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertLayout = inflater.inflate(R.layout.custom_layout_common, null);
            TextView text = (TextView) alertLayout.findViewById(R.id.dialog_title);
            text.setText(getResources().getString(R.string.Alert));
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

    protected void deleteTables() {
        SQLiteDatabase dba = BaseCompactActivity.db.getWritableDatabase();
        dba.execSQL("delete from " + RapipayDB.TABLE_NAME);
        dba.execSQL("delete from " + RapipayDB.TABLE_BANK);
        dba.execSQL("delete from " + RapipayDB.TABLE_STATE);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYMENT);
        dba.execSQL("delete from " + RapipayDB.TABLE_OPERATOR);
        dba.execSQL("delete from " + RapipayDB.TABLE_TRANSFERLIST);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYERPAYEE);
        dba.execSQL("delete from " + RapipayDB.TABLE_NEPAL_PAYMENTMOODE);
        dba.execSQL("delete from " + RapipayDB.TABLE_NEPAL_BANK);
        dba.execSQL("delete from " + RapipayDB.TABLE_KYC_PERSONAL);
        dba.execSQL("delete from " + RapipayDB.TABLE_KYC_ADDRESS);
        dba.execSQL("delete from " + RapipayDB.TABLE_KYC_BUISNESS);
        dba.execSQL("delete from " + RapipayDB.TABLE_KYC_VERIFICATION);
        dba.execSQL("delete from " + RapipayDB.TABLE_IMAGES);
    }
}
