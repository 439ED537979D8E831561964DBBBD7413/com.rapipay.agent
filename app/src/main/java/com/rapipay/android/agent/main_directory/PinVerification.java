package com.rapipay.android.agent.main_directory;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Database.RapipayRealmDB;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.MasterPozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.SlidingImage_Adapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;
import com.rapipay.android.agent.utils.WebConfig;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import io.realm.Realm;
import io.realm.RealmResults;

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
        // sysnch data from sqlite to Realm database if the existing user
        if(!checkInternetConenction()){
            customDialog_List_info("Please check your network connectivity");
        }
        if (listHandsetRegistration == null) {
            jumpPage();
        }
        /*if (db != null && db.getDetails_Rapi()) {
            listOld = db.getDetails();
            if (dbRealm != null && dbRealm.getDetails_Rapi())
                list = dbRealm.getDetails();
        }*/
        initialize();
        if (dbRealm != null && dbRealm.getDetails_Rapi()) {
            deleteTables();
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
        TextView verionName = findViewById(R.id.btn_sing_in);
        try {
            verionName.setText("Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
   /*
    2019-08-23 12:54:38.654 23099-23099/com.rapipay.android.agents E/json: {"serviceType":"PinVerify","requestType":"handset_CHannel","typeMobileWeb":"mobile","txnRefId":"133854120823625","agentId":"9168360492",
    "nodeAgentId":"9168360492","pin":"111111","imeiNo":"356477080688252","deviceName":"motorola","sessionRefNo":"6JAJ8XRFPZ","osType":"ANDROID","domainName":"agent.rapipay.com","clientRequestIP":"172.16.50.246",
    "checkSum":"319ABCC1DB3FCE5075C3258AD732F27E779F52F29EAAE6C3EA5AB8408E228FA8AA0553AAE8553188B4DC36D6C25100830A2CDEE6D461887BC2E167504FCFBDC8"}

*/
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
                jsonObject.put("sessionRefNo", listHandsetRegistration.get(0).getSessionRefNo());
                jsonObject.put("osType", "ANDROID");
                jsonObject.put("domainName", BuildConfig.DOMAINNAME);
                jsonObject.put("clientRequestIP", ImageUtils.ipAddress(PinVerification.this));
                jsonObject.put("checkSum", GenerateChecksum.checkSum(listHandsetRegistration.get(0).getSessionKey(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e("json",jsonObject+"");
        return jsonObject;
    }

    @Override
    public void chechStatus(final JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("PinVerify")) {
                    String whereArgs1 = list.get(0).getApikey();

                    final RapiPayPozo pinVeriPojo1 = realm.where(RapiPayPozo.class).equalTo("apikey", whereArgs1).findFirst();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            try {
                                pinVeriPojo1.setSession(object.getString("session"));
                                pinVeriPojo1.setSessionRefNo(object.getString("sessionRefNo"));
                                pinVeriPojo1.setAftersessionRefNo(object.getString("sessionRefNo"));
                                pinVeriPojo1.setPinsession(object.getString("session"));
                                realm.copyToRealmOrUpdate(pinVeriPojo1);
                                localStorage.setActivityState(LocalStorage.LOGOUT, "LOGOUT");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_FOOTER_DATA")) {
                    if (object.getString("imageDownloadFlag").equalsIgnoreCase("Y")) {
                        if (object.has("footerImgList")) {
                            JSONArray array = object.getJSONArray("footerImgList");
                            insertFooterDetails(array, dbRealm, object.getString("timeStamp"));
                        }
                    } else
                        init(dbRealm.getFooterDetail(""));
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
            } else {
                confirmpinView.setText("");
                responseMSg(object);
            }
            flaf = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void masterDetails(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                final JSONObject object = array.getJSONObject(i);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            MasterPozo masterPozo = new MasterPozo();
                            masterPozo.setFrontId(object.getString("frontId"));
                            masterPozo.setServiceTypeName(object.getString("serviceTypeName"));
                            masterPozo.setDisplayName(object.getString("displayName"));
                            masterPozo.setDisplayType(object.getString("displayType"));
                            masterPozo.setIcon(byteConvert(object.getString("icon")));
                            masterPozo.setOrder(object.getString("order"));
                            masterPozo.setTimeStamp(object.getString("timeStamp"));
                            realm.copyToRealm(masterPozo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
            case R.id.switchuser:
                customDialog_Common("KYCLAYOUT", null, null, "Switch User", null, "Do you want to switch user!.", PinVerification.this);
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
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
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
                if (dbRealm.getFooterDetail("banner").size() != 0)
                    jsonObject.put("timeStamp", dbRealm.getFooterDetail("banner").get(0).getTimeStamp());
                else
                    jsonObject.put("timeStamp", "");
                jsonObject.put("sessionRefNo", listHandsetRegistration.get(0).getSessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(listHandsetRegistration.get(0).getSessionKey(), jsonObject.toString()));
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
                if (dbRealm.getMasterDetail("").size() != 0)
                    jsonObject.put("timeStamp", dbRealm.getMasterDetail("").get(0).getTimeStamp());
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

    private void insertFooterDetails(JSONArray array, RapipayRealmDB db, final String timeStamp) {
        bannerlist = new ArrayList<HeaderePozo>();
        imagelist = new ArrayList<HeaderePozo>();
        if (dbRealm.getDetailsFooter())
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<HeaderePozo> headerePozoRealmResults = realm.where(HeaderePozo.class).findAll();
                    headerePozoRealmResults.deleteAllFromRealm();
                }
            });
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
            if (bannerlist.size() != 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (int i = 0; i < bannerlist.size(); i++) {
                            HeaderePozo headerePozo = new HeaderePozo();
                            String wlimageName = "banner" + i + ".jpg";
                            headerePozo.setHeaderID(bannerlist.get(i).getHeaderID());
                            headerePozo.setHeaderData(bannerlist.get(i).getHeaderData());
                            headerePozo.setTimeStamp(timeStamp);
                            headerePozo.setHeaderValue(wlimageName);
                            headerePozo.setPath(bannerlist.get(i).getHeaderData());
                            headerePozo.setHeaderValue(wlimageName);
                            realm.copyToRealm(headerePozo);
                        }
                    }
                });
            }
        }
        init(dbRealm.getFooterDetail(""));
    }

    @Override
    public void checkVersion(ArrayList<VersionPozo> list) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() != null)
                if (list.get(i).getName().equalsIgnoreCase("PROD")) {
                    stringArrayList.add(list.get(i + 1).getValue());
                } else if (list.get(i).getName().equalsIgnoreCase("APP_UPDATE_ST")) {
                    stringArrayList.add(list.get(i + 1).getValue());
                }
        }
        if (stringArrayList.size() != 0) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                if (Double.valueOf(version) >= Double.valueOf(stringArrayList.get(0)) && (stringArrayList.get(1).equalsIgnoreCase("F") || stringArrayList.get(1).equalsIgnoreCase("N"))) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate(confirmpinView.getText().toString()).toString(), "", PinVerification.this, getString(R.string.responseTimeOut)).execute();
                } else if (Double.valueOf(version) < Double.valueOf(stringArrayList.get(0)) && stringArrayList.get(1).equalsIgnoreCase("F")) {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", PinVerification.this);
                } else if (Double.valueOf(stringArrayList.get(0)) != Double.valueOf(version) && stringArrayList.get(1).equalsIgnoreCase("N")) {
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
