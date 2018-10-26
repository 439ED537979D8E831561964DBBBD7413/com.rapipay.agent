package com.rapipay.android.agent.main_directory;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.utils.LocalStorage;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.FooterAdapter;
import com.rapipay.android.agent.adapter.SlidingImage_Adapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.RouteClass;
import com.rapipay.android.agent.utils.WebConfig;


public class PinVerification extends BaseCompactActivity implements RequestHandler, View.OnClickListener, CustomInterface, VersionListener {
    private ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    EditText confirmpinView;
    TextView toolbar_title;
    ArrayList<HeaderePozo> bannerlist;


    boolean flaf = false;
    private static final Integer[] IMAGES = {R.drawable.banner1, R.drawable.banner1, R.drawable.banner1, R.drawable.banner1};
    private ArrayList<Integer> ImagesArray;
    RecyclerView recycler_view;
    String TAG = "XML";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinverification_layout);
        localStorage.setActivityState(LocalStorage.LOGOUT, "0");
        initialize();
//        init();
//        loadVersion();
        loadApi();
    }

    private void loadApi() {
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getFooterData().toString(), headerData, PinVerification.this).execute();
    }

    private void initialize() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        confirmpinView = (EditText) findViewById(R.id.confirmpinView);
//        PinViewSettings pinViewSettings = new PinViewSettings.Builder()
//                .withMaskPassword(true)
//                .withDeleteOnClick(false)
//                .withKeyboardMandatory(false)
//                .withSplit("-")
//                .withNumberPinBoxes(6)
//                .withNativePinBox(false)
//                .build();
//        confirmpinView.setSettings(pinViewSettings);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbar_title.setText("Hello, " + list.get(0).getAgentName());
        toolbar_title.setTextColor(getResources().getColor(R.color.white));
        confirmpinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6 && !flaf) {
                    flaf = true;
                    loadVersion(localStorage.getActivityState(LocalStorage.EMI));
                    flaf = false;
                }
            }
        });
//        confirmpinView.setOnCompleteListener(new PinView.OnCompleteListener() {
//            @Override
//            public void onComplete(boolean completed, final String pinResults) {
//                //Do what you want
//                if (completed) {
//                    hideKeyboard(PinVerification.this);
//                    new AsyncPostMethod(WebConfig.UAT, getJson_Validate(pinResults).toString(), "", PinVerification.this).execute();
//                }
//            }
//        });
    }

    private void init(ArrayList<HeaderePozo> bannerlist) {
        ImagesArray = new ArrayList<Integer>();
        for (int i = 0; i < IMAGES.length; i++) {
            ImagesArray.add(IMAGES[i]);
        }
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(PinVerification.this, bannerlist));
        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES = IMAGES.length;

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    public JSONObject getJson_Validate(String pinResults) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "PinVerify");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", "PVF" + tsLong.toString());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("pin", pinResults);
                jsonObject.put("imeiNo", list.get(0).getImei());
                jsonObject.put("deviceName", Build.MANUFACTURER);
                jsonObject.put("sessionRefNo", list.get(0).getSessionRefNo());
                jsonObject.put("osType", "ANDROID");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("PinVerify")) {
                    SQLiteDatabase dba = db.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(RapipayDB.COLOMN_PINSESSION, object.getString("session"));
                    contentValues.put(RapipayDB.COLOMN_AFTERSESSIONREFNO, object.getString("sessionRefNo"));
                    String whereClause = "apikey=?";
                    String whereArgs[] = {list.get(0).getApikey()};
                    dba.update(RapipayDB.TABLE_NAME, contentValues, whereClause, whereArgs);
                    localStorage.setActivityState(LocalStorage.ROUTESTATE, "UPDATE");
                    localStorage.setActivityState(LocalStorage.LOGOUT, "LOGOUT");
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_FOOTER_DATA")) {
                    if (object.getString("imageDownloadFlag").equalsIgnoreCase("Y")) {
                        if (object.has("footerImgList")) {
                            JSONArray array = object.getJSONArray("footerImgList");
                            insertFooterDetails(array, db, object.getString("timeStamp"));
                        }
                    }else
                        init(db.getFooterDetail("banner"));
                } else if (object.getString("serviceType").equalsIgnoreCase("APP_LIVE_STATUS")) {
                    if (object.has("headerList")) {
                        JSONArray array = object.getJSONArray("headerList");
                        versionDetails(array, PinVerification.this);
                    }
                }
            }
            flaf = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_pin:
                customDialog_Common("KYCLAYOUT", null, null, "Forgot Pin", null, "Do you want to reset your pin!.", PinVerification.this);
//                confirmDialog("Do you want to reset your pin!.");
                break;
        }
    }

//    private void confirmDialog(String msg) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.app_name);
//        //Setting message manually and performing action on button click
//        builder.setMessage(msg)
//                .setCancelable(false)
//                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        deleteTables("forgot");
//                        new RouteClass(PinVerification.this, null, "", localStorage, "0");
//                        dialog.dismiss();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

    @Override
    public void chechStat(String object) {
        customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object, PinVerification.this);
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("KYCLAYOUTS"))
            confirmpinView.setText("");
        else if (type.equalsIgnoreCase("KYCLAYOUTSS")) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(webIntent);
        } else {
            deleteTables("forgot");
            new RouteClass(PinVerification.this, null, "", localStorage, "0");
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    public JSONObject getFooterData() {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_FOOTER_DATA");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", "GFD" + tsLong.toString());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                if (db.getFooterDetail("banner").size() != 0)
                    jsonObject.put("timeStamp", db.getFooterDetail("banner").get(0).getTimeStamp());
                else
                    jsonObject.put("timeStamp", "");
                jsonObject.put("sessionRefNo", list.get(0).getSessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private void insertFooterDetails(JSONArray array, RapipayDB db, String timeStamp) {
        bannerlist = new ArrayList<HeaderePozo>();
        SQLiteDatabase dba = db.getWritableDatabase();
        if (db.getDetailsFooter())
            dba.execSQL("delete from " + RapipayDB.TABLE_FOOTER);
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("headerValue").equalsIgnoreCase("banner")) {
                    bannerlist.add(new HeaderePozo(object.getString("headerValue"), object.getString("headerData"), object.getString("headerId")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bannerlist.size() != 0) {
            for (int i = 0; i < bannerlist.size(); i++) {
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_FOOTER + "\n" +
                        "(" + RapipayDB.COLOMN_OPERATORID + "," + RapipayDB.COLOMN_OPERATORVALUE + "," + RapipayDB.COLOMN_OPERATORDATA + "," + RapipayDB.COLOMN_PATH + "," + RapipayDB.IMAGE_TIME_STAMP + ")\n" +
                        "VALUES \n" +
                        "( ?, ?, ?,?,?);";

                String imageName = "banner" + i + ".jpg";
                String path = saveToInternalStorage(base64Convert(bannerlist.get(i).getHeaderData()), imageName);
                dba.execSQL(insertSQL, new String[]{bannerlist.get(i).getHeaderID(), imageName, bannerlist.get(i).getHeaderData(), path, timeStamp});
            }
        }
        init(db.getFooterDetail(""));
//        initializeTransAdapter(db.getFooterDetail(""));
    }


//    private String saveToInternalStorage(Bitmap bitmapImage, String name) {
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath = new File(directory, name);
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return directory.getAbsolutePath();
//    }

    private Bitmap base64Convert(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private void initializeTransAdapter(ArrayList<HeaderePozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(PinVerification.this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(new FooterAdapter(PinVerification.this, recycler_view, list));
    }

    @Override
    public void checkVersion(ArrayList<VersionPozo> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() != null)
                if (list.get(i).getName().equalsIgnoreCase("PROD")) {
                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        String version = pInfo.versionName;
                        if (!version.equalsIgnoreCase(list.get(i + 1).getValue())) {
                            customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", PinVerification.this);
                        } else {
                            new AsyncPostMethod(WebConfig.UAT, getJson_Validate(confirmpinView.getText().toString()).toString(), "", PinVerification.this).execute();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
