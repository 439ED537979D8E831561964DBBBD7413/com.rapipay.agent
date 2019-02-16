package com.rapipay.android.agent.main_directory;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    ArrayList<HeaderePozo> bannerlist, imagelist;


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
        if (db != null && db.getDetails_Rapi()) {
            loadApi();
//            loadMaster();
        } else
            dbNull(PinVerification.this);
    }

    private void loadApi() {
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getFooterData().toString(), headerData, PinVerification.this, getString(R.string.responseTimeOut)).execute();
    }

    private void loadMaster() {
        new AsyncPostMethod(WebConfig.LOGIN_URL, getMaster().toString(), headerData, PinVerification.this, getString(R.string.responseTimeOut)).execute();
    }

    private void initialize() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        confirmpinView = (EditText) findViewById(R.id.confirmpinView);
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
        indicator.setRadius(5 * density);
        NUM_PAGES = IMAGES.length;
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
        JSONObject jsonObject = new JSONObject();
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String wifiIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "PinVerify");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", ImageUtils.miliSeconds());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("pin", pinResults);
                jsonObject.put("imeiNo", list.get(0).getImei());
                jsonObject.put("deviceName", Build.MANUFACTURER);
                jsonObject.put("sessionRefNo", list.get(0).getSessionRefNo());
                jsonObject.put("osType", "ANDROID");
                jsonObject.put("domainName", BuildConfig.DOMAINNAME);
                jsonObject.put("clientRequestIP", ImageUtils.ipAddress(PinVerification.this));
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
                    } else
                        init(db.getFooterDetail(""));
                } else if (object.getString("serviceType").equalsIgnoreCase("APP_LIVE_STATUS")) {
                    if (object.has("headerList")) {
                        JSONArray array = object.getJSONArray("headerList");
                        versionDetails(array, PinVerification.this);
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("SERVICE_MASTER")) {
                    if (object.has("objServiceMasterList")) {
                        JSONArray array = object.getJSONArray("objServiceMasterList");
                        masterDetails(array);
                    }
                }
            }
            flaf = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void masterDetails(JSONArray array) {
        try {
            SQLiteDatabase dba = db.getWritableDatabase();
            dba.execSQL("delete from " + RapipayDB.TABLE_MASTER);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(RapipayDB.COLOMN_FRONTID, object.getString("frontId"));
                values.put(RapipayDB.COLOMN_SERVICETYPENAME, object.getString("serviceTypeName"));
                values.put(RapipayDB.COLOMN_DISPLAYNAME, object.getString("displayName"));
                values.put(RapipayDB.COLOMN_DISPLAYTYPE, object.getString("displayType"));
                values.put(RapipayDB.COLOMN_ICON, byteConvert(object.getString("icon")));
                values.put(RapipayDB.COLOMN_ORDER, object.getString("order"));
                values.put(RapipayDB.IMAGE_TIME_STAMP, object.getString("timeStamp"));
                dba.insert(RapipayDB.TABLE_MASTER, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_pin:
                customDialog_Common("KYCLAYOUT", null, null, "Forgot Pin", null, "Do you want to reset your pin!.", PinVerification.this);
                break;
        }
    }

    @Override
    public void chechStat(String object) {
        if (object.contains("DOCTYPE"))
            customDialog_Common("TERMCONDITION", null, null, "Term & Condition", "", object, PinVerification.this);
        else
            customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object, PinVerification.this);
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("KYCLAYOUTS"))
            confirmpinView.setText("");
        else if (type.equalsIgnoreCase("KYCLAYOUTSS")) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + "com.rapipay.android.agents"));
            startActivity(webIntent);
        } else if (type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
        else {
            deleteTables("forgot");
            new RouteClass(PinVerification.this, null, "", localStorage, "0");
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    public JSONObject getFooterData() {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_FOOTER_DATA");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
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

    public JSONObject getMaster() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "SERVICE_MASTER");
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("typeMobileWeb", "mobile");
                if (db.getMasterDetail("").size() != 0)
                    jsonObject.put("timeStamp", db.getMasterDetail("").get(0).getTimeStamp());
                else
                    jsonObject.put("timeStamp", format.format(date));
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
        imagelist = new ArrayList<HeaderePozo>();
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
                ContentValues values = new ContentValues();
                String wlimageName = "banner" + i + ".jpg";
                values.put(RapipayDB.COLOMN_OPERATORID, bannerlist.get(i).getHeaderID());
                values.put(RapipayDB.COLOMN_OPERATORDATA, bannerlist.get(i).getHeaderData());
                values.put(RapipayDB.IMAGE_TIME_STAMP, timeStamp);
                values.put(RapipayDB.COLOMN_OPERATORVALUE, wlimageName);
                values.put(RapipayDB.COLOMN_PATH, byteConvert(bannerlist.get(i).getHeaderData()));
                values.put(RapipayDB.COLOMN_OPERATORVALUE, wlimageName);
                dba.insert(RapipayDB.TABLE_FOOTER, null, values);
//                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_FOOTER + "\n" +
//                        "(" + RapipayDB.COLOMN_OPERATORID + "," + RapipayDB.COLOMN_OPERATORVALUE + "," + RapipayDB.COLOMN_OPERATORDATA + "," + RapipayDB.COLOMN_PATH + "," + RapipayDB.IMAGE_TIME_STAMP + ")\n" +
//                        "VALUES \n" +
//                        "( ?, ?, ?,?,?);";
//
//                String imageName = "banner" + i + ".jpg";
//                String path = saveToInternalStorage(base64Convert(bannerlist.get(i).getHeaderData()), imageName);
//                dba.execSQL(insertSQL, new String[]{bannerlist.get(i).getHeaderID(), imageName, bannerlist.get(i).getHeaderData(), path, timeStamp});
            }
        }
//        if (imagelist.size() != 0) {
//            for (int i = 0; i < imagelist.size(); i++) {
//                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_FOOTER + "\n" +
//                        "(" + RapipayDB.COLOMN_OPERATORID + "," + RapipayDB.COLOMN_OPERATORVALUE + "," + RapipayDB.COLOMN_OPERATORDATA + "," + RapipayDB.COLOMN_PATH + "," + RapipayDB.IMAGE_TIME_STAMP + ")\n" +
//                        "VALUES \n" +
//                        "( ?, ?, ?,?,?);";
//
//                String imageName = imagelist.get(i).getHeaderValue() + ".jpg";
//                new ChangeTask(imagelist.get(i).getHeaderData(), PinVerification.this).execute();
//                String path = saveToInternalStorage(base64Convert(imagelist.get(i).getHeaderData()), imageName);
//                dba.execSQL(insertSQL, new String[]{imagelist.get(i).getHeaderID(), imageName, imagelist.get(i).getHeaderData(), path, timeStamp});
//            }
//        }
        init(db.getFooterDetail(""));
    }

    private void initializeTransAdapter(ArrayList<HeaderePozo> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(PinVerification.this, LinearLayoutManager.HORIZONTAL, false);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(new FooterAdapter(PinVerification.this, recycler_view, list));
    }

//    @Override
//    public void checkVersion(ArrayList<VersionPozo> list) {
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getName() != null)
//                if (list.get(i).getName().equalsIgnoreCase("APP_UPDATE_ST")) {
//                    try {
//                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                        String version = pInfo.versionName;
//                        if (("F").equalsIgnoreCase(list.get(i + 1).getValue())) {
//                            customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", PinVerification.this);
//                        } else {
//                            new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate(confirmpinView.getText().toString()).toString(), "", PinVerification.this,getString(R.string.responseTimeOut)).execute();
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//        }
//    }

    @Override
    public void checkVersion(ArrayList<VersionPozo> list) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() != null)
                if (list.get(i).getName().equalsIgnoreCase("PROD")) {
                    stringArrayList.add(list.get(i + 1).getValue());
//                    try {
//                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                        String version = pInfo.versionName;
//                        if (!version.equalsIgnoreCase(list.get(i + 1).getValue())) {
//                            customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", LoginScreenActivity.this);
//                        } else {
//                            new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", LoginScreenActivity.this, getString(R.string.responseTimeOut)).execute();
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
                } else if (list.get(i).getName().equalsIgnoreCase("APP_UPDATE_ST")) {
                    stringArrayList.add(list.get(i + 1).getValue());
//                    try {
//                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                        String version = pInfo.versionName;
//                        if (("F").equalsIgnoreCase(list.get(i + 1).getValue())) {
//                            customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", LoginScreenActivity.this);
//                        } else {
//                            new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate().toString(), "", LoginScreenActivity.this, getString(R.string.responseTimeOut)).execute();
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
                }
        }
        if (stringArrayList.size() != 0) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                if (stringArrayList.get(0).equalsIgnoreCase(version) && stringArrayList.get(1).equalsIgnoreCase("F")) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate(confirmpinView.getText().toString()).toString(), "", PinVerification.this, getString(R.string.responseTimeOut)).execute();
                } else if (!stringArrayList.get(0).equalsIgnoreCase(version) && stringArrayList.get(1).equalsIgnoreCase("F")) {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", PinVerification.this);
                } else if (!stringArrayList.get(0).equalsIgnoreCase(version) && stringArrayList.get(1).equalsIgnoreCase("N")) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate(confirmpinView.getText().toString()).toString(), "", PinVerification.this, getString(R.string.responseTimeOut)).execute();
                } else {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", PinVerification.this);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
