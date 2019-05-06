package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.DeviceDetailsPozo;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.MenuModel;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.ExpandableListAdapter;
import com.rapipay.android.agent.fragments.ChangeMobileFragment;
import com.rapipay.android.agent.fragments.ChangePassword;
import com.rapipay.android.agent.fragments.ChangePinFragment;
import com.rapipay.android.agent.fragments.DashBoardFragments;
import com.rapipay.android.agent.fragments.LienHistory;
import com.rapipay.android.agent.fragments.ProfileFragment;
import com.rapipay.android.agent.fragments.SettlementBankFragment;
import com.rapipay.android.agent.fragments.TpinTab;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.kotlin_classs.SettlementTab;
import com.rapipay.android.agent.kotlin_classs.SubAgentFrag;
import com.rapipay.android.agent.kotlin_classs.TransactionReports;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.MasterClass;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseCompactActivity
        implements RequestHandler, CustomInterface, View.OnClickListener {

    public static ImageView ivHeaderPhoto;
    NavigationView navigationView;
    private static String filePath;
    DrawerLayout drawer;
    String data, term = null;
    TextView tv, bankde, bal;
    ImageView back_click;
    private static final int dpPhoto1 = 2001;
    private static final int dpPhoto2 = 2002;
    public static String bankdetails = null;
    public static String Parent_Mobile = null;
    public static String regBankDetails = null;
    public static ArrayList<HeaderePozo> pozoArrayList;
    public static boolean relailerDetails = false;
    public static ArrayList<DeviceDetailsPozo> deviceDetailsPozoArrayList;
    boolean isUrl = false;
    boolean isclicked = false;


    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();
        if (db != null && db.getDetails_Rapi()) {
            String condition = "where " + RapipayDB.IMAGE_NAME + "='invoiceLogo.jpg'";
            ArrayList<ImagePozo> imagePozoArrayList = db.getImageDetails(condition);
            if (imagePozoArrayList.size() != 0) {
                byteConvert(back_click, imagePozoArrayList.get(0).getImagePath());
            } else {
                if (BuildConfig.APPTYPE == 1 || BuildConfig.APPTYPE == 3)
                    back_click.setImageDrawable(getResources().getDrawable(R.drawable.new_test));
                if (BuildConfig.APPTYPE == 2)
                    back_click.setImageDrawable(getResources().getDrawable(R.drawable.rapipay_parter));
            }
            loadUrl();
            loadBalance();
            loadMasterData();
        } else {
            dbNull(MainActivity.this);
        }
    }

    private void loadMasterData() {
        new AsyncPostMethod(WebConfig.CASHOUT_URL, getDeviceDetails().toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut), "NODEHEADERDATA").execute();
    }

    public JSONObject getDeviceDetails() {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_MASTER_DEVICE_DETAILS");
                jsonObject.put("requestChannel", "MPOS_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("agentMobile", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "User Data is null", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    private void url() {
        new AsyncPostMethod(WebConfig.COMMONAPI, getDashBoard("GET_NODE_HEADER_DATA").toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut), "NODEHEADERDATA").execute();
        localStorage.setActivityState(LocalStorage.ROUTESTATE, "0");
    }

    private void loadUrl() {
        url();
    }

    private void loadBalance() {
        new AsyncPostMethod(WebConfig.CRNF, getBalance().toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut), "NODEHEADERDATA").execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                loadBalance();
                tv.setText("");
                tv.setVisibility(View.GONE);
                break;
            case R.id.bankde:
                Intent intent = new Intent(MainActivity.this, BankDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.back_click:
                openfragment(0);
                break;
        }
    }

    public JSONObject getDashBoard(String servicetype) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getBalance() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_AGENT_BALANCE");
            jsonObject.put("requestType", "CRNF_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("agentID", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initialization() {
        expandableListView = findViewById(R.id.expandableListView);
        prepareMenuData();
        populateExpandableList();
        bal = (TextView) findViewById(R.id.bal);
        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        bankde = (TextView) findViewById(R.id.bankde);
        bankde.setVisibility(View.GONE);
        bankde.setText("Credit Banks");
        back_click = (ImageView) findViewById(R.id.back_click);
        tv = (TextView) this.findViewById(R.id.mywidget);
        tv.setSelected(true);
        reset.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.hamburger);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView verionName = navigationView.findViewById(R.id.btn_sing_in);
        TextView contactus = navigationView.findViewById(R.id.contactus);
        if (BuildConfig.APPTYPE == 1 || BuildConfig.APPTYPE == 3)
            contactus.setVisibility(View.VISIBLE);
        try {
            verionName.setText("Version - " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ivHeaderPhoto = (ImageView) headerLayout.findViewById(R.id.imageView);
        if (!localStorage.getActivityState(LocalStorage.IMAGEPATH).equalsIgnoreCase("0"))
            loadImageFromStorage("image.jpg", ivHeaderPhoto, localStorage.getActivityState(LocalStorage.IMAGEPATH));
        TextView name_agent = (TextView) headerLayout.findViewById(R.id.name_agent);
        name_agent.setText(list.get(0).getAgentName());
        ivHeaderPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(dpPhoto1, dpPhoto2, "DPPHOTO");
            }
        });
    }

    private void prepareMenuData() {

        MenuModel menuModel = new MenuModel("Home", true, false, "https://www.journaldev.com/9333/android-webview-example-tutorial"); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Profile", true, false, "https://www.journaldev.com/9333/android-webview-example-tutorial"); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel("Manage FOS", true, false, "https://www.journaldev.com/9333/android-webview-example-tutorial"); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Credit Banks", true, false, "https://www.journaldev.com/9333/android-webview-example-tutorial"); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }
        menuModel = new MenuModel("Bank Settlement", true, false, "https://www.journaldev.com/9333/android-webview-example-tutorial"); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel("Settings", true, true, ""); //Menu of Java Tutorials
        headerList.add(menuModel);
        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel("Change Password", false, false, "https://www.journaldev.com/7153/core-java-tutorial");
        childModelsList.add(childModel);

        childModel = new MenuModel("Change TPIN", false, false, "https://www.journaldev.com/19187/java-fileinputstream");
        childModelsList.add(childModel);

        childModel = new MenuModel("Change Login Pin", false, false, "https://www.journaldev.com/19115/java-filereader");
        childModelsList.add(childModel);
        childModel = new MenuModel("Change Mobile", false, false, "https://www.journaldev.com/19115/java-filereader");
        childModelsList.add(childModel);


        if (menuModel.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel, childModelsList);
        }

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("Accounts Details", true, true, ""); //Menu of Python Tutorials
        headerList.add(menuModel);
        childModel = new MenuModel("Bank Settlement Account", false, false, "https://www.journaldev.com/19243/python-ast-abstract-syntax-tree");
        childModelsList.add(childModel);

        childModel = new MenuModel("Whitelist Bank Account(Auto Credit)", false, false, "https://www.journaldev.com/19226/python-fractions");
        childModelsList.add(childModel);
        childModel = new MenuModel("Lien History", false, false, "https://www.journaldev.com/19226/python-fractions");
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("Transaction Reports", true, true, ""); //Menu of Python Tutorials
        headerList.add(menuModel);
        childModel = new MenuModel("BC Fund Transfer History", false, false, "https://www.journaldev.com/19243/python-ast-abstract-syntax-tree");
        childModelsList.add(childModel);

        childModel = new MenuModel("Wallet Fund Transfer History", false, false, "https://www.journaldev.com/19226/python-fractions");
        childModelsList.add(childModel);
        childModel = new MenuModel("Indo-Nepal Fund Transfer History", false, false, "https://www.journaldev.com/19226/python-fractions");
        childModelsList.add(childModel);
        childModel = new MenuModel("MPOS Transaction History", false, false, "https://www.journaldev.com/19226/python-fractions");
        childModelsList.add(childModel);
        childModel = new MenuModel("AEPS/ MATM Transaction History", false, false, "https://www.journaldev.com/19226/python-fractions");
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }
    }

    private void populateExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) {
                    if (!headerList.get(groupPosition).hasChildren) {
                        openfragment(groupPosition);
//                        WebView webView = findViewById(R.id.webView);
//                        webView.loadUrl(headerList.get(groupPosition).url);
                        closeDrawer();
                    }
                }

                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);
                    if (model.url.length() > 0) {
                        int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                        parent.setItemChecked(index, true);
                        openChildfragment(groupPosition, childPosition);
//                        WebView webView = findViewById(R.id.webView);
//                        webView.loadUrl(model.url);
                        closeDrawer();
                    }
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == dpPhoto1) {
                try {
                    String path = data.getStringExtra("ImagePath");
                    String imageType = data.getStringExtra("ImageType");
                    Bitmap bitmap = loadImageFromStorage(imageType, path);
                    ivHeaderPhoto.setImageBitmap(bitmap);
                    String imageName = "image" + ".jpg";
                    String paths = saveToInternalStorage(bitmap, imageName);
                    localStorage.setActivityState(LocalStorage.IMAGEPATH, paths);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == dpPhoto2) {
                Uri uri = data.getData();
                Bitmap thumbnail = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(MainActivity.this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                ivHeaderPhoto.setImageBitmap(thumbnail);
                String imageName = "image" + ".jpg";
                String path = saveToInternalStorage(thumbnail, imageName);
                localStorage.setActivityState(LocalStorage.IMAGEPATH, path);
            } else if (requestCode == 2) {
                url();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void fragmentReplace(Fragment fragment) {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openChildfragment(int head, int child) {
        Fragment fragment = null;
        if (head == 5 && child == 2) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            isclicked = false;
            fragment = new ChangePinFragment();
        } else if (head == 5 && child == 0) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            isclicked = false;
            fragment = new ChangePassword();
        } else if (head == 5 && child == 3) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            isclicked = false;
            fragment = new ChangeMobileFragment();
        } else if (head == 6 && child == 0) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("message", "S");
            isclicked = false;
            fragment = new SettlementBankFragment();
            fragment.setArguments(bundle);
        } else if (head == 6 && child == 1) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("message", "P");
            isclicked = false;
            fragment = new SettlementBankFragment();
            fragment.setArguments(bundle);
        } else if (head == 6 && child == 2) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            isclicked = false;
            fragment = new LienHistory();
        } else if (head == 5 && child == 1) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            isclicked = false;
            fragment = new TpinTab();
        } else if (head == 7 && child == 0) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("reqFor", "BCS");
            isclicked = false;
            fragment = new TransactionReports();
            fragment.setArguments(bundle);
        } else if (head == 7 && child == 1) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("reqFor", "WLT");
            isclicked = false;
            fragment = new TransactionReports();
            fragment.setArguments(bundle);
        } else if (head == 7 && child == 2) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("reqFor", "PMT");
            isclicked = false;
            fragment = new TransactionReports();
            fragment.setArguments(bundle);
        } else if (head == 7 && child == 3) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("reqFor", "MPOS");
            isclicked = false;
            fragment = new TransactionReports();
            fragment.setArguments(bundle);
        } else if (head == 7 && child == 4) {
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("reqFor", "AEPSMATM");
            isclicked = false;
            fragment = new TransactionReports();
            fragment.setArguments(bundle);
        } else if (fragment == null)
            Toast.makeText(MainActivity.this, "Under Process", Toast.LENGTH_SHORT).show();
        if (fragment != null)
            fragmentReplace(fragment);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void openfragment(int id) {
        Fragment fragment = null;
        if (id == 0) {
            isclicked = true;
            bankde.setVisibility(View.GONE);
            reset.setVisibility(View.VISIBLE);
            if (isUrl) {
                isUrl = false;
                loadUrl();
            }
            fragment = new DashBoardFragments();
        } else if (id == 1) {
            isclicked = false;
            isUrl = true;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            fragment = new ProfileFragment();
        } else if (id == 2) {
            isUrl = true;
            isclicked = false;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            fragment = new SubAgentFrag();
        } else if (id == 3) {
            isUrl = true;
            isclicked = false;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            fragment = new com.rapipay.android.agent.kotlin_classs.BankDetails();
        } else if (id == 4) {
            isUrl = true;
            isclicked = false;
            reset.setVisibility(View.GONE);
            bankde.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("reqFor", "STLMNT");
            fragment = new SettlementTab();
            fragment.setArguments(bundle);
        } else if (fragment == null)
            Toast.makeText(MainActivity.this, "Under Process", Toast.LENGTH_SHORT).show();
        if (fragment != null)
            fragmentReplace(fragment);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_MASTER_DATA")) {
                    if (new MasterClass().getMasterData(object, db))
                        if (term.equalsIgnoreCase("N"))
                            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge(data, term).toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut), "DOWMLOADDATA").execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_NODE_HEADER_DATA")) {
                    if (object.has("headerList")) {
                        localStorage.setActivityState(LocalStorage.ROUTESTATE, "0");
                        if (Integer.parseInt(object.getString("listCount")) > 0) {
                            insertLastTransDetails(object.getJSONArray("headerList"));
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("UPDATE_DOWNLAOD_DATA_STATUS")) {
                    loadUrl();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_AGENT_BALANCE")) {
                    bal.setText("Bal:-" + format(object.getString("agentBalance")));
                    bal.setVisibility(View.VISIBLE);
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_MASTER_DEVICE_DETAILS")) {
                    if (object.has("deviceList")) {
                        deviceDetails(object.getJSONArray("deviceList"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deviceDetails(JSONArray array) {
        deviceDetailsPozoArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                deviceDetailsPozoArrayList.add(new DeviceDetailsPozo(object.getString("deviceID"), object.getString("deviceType"), object.getString("bluetoothID")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        pozoArrayList = new ArrayList<HeaderePozo>();
        pozoArrayList.clear();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("headerData").equalsIgnoreCase("Retailer") && object.getString("headerValue").equalsIgnoreCase("CATEGORY"))
                    relailerDetails = true;
                if (object.getString("headerValue").equalsIgnoreCase("Parent Mobile"))
                    Parent_Mobile = object.getString("headerData");
                if (object.getString("displayFlag").equalsIgnoreCase("D"))
                    pozoArrayList.add(new HeaderePozo(object.getString("headerValue"), object.getString("headerData"), object.getString("headerId"), object.getString("displayFlag")));
                else if (object.getString("headerValue").equalsIgnoreCase("Notice")) {
                    tv.setText(object.getString("headerData"));
                    tv.setVisibility(View.VISIBLE);
                } else if (object.getString("headerValue").equalsIgnoreCase("BANK_LIVE_ST")) {
                    bankdetails = object.getString("headerData").replace(",", "\n");
//                    customDialog_Common("KYCLAYOUTS", null, null, "Banl Update", "", object.getString("headerData").replace(",","\n"), MainActivity.this);
                } else if (object.getString("headerValue").equalsIgnoreCase("MPAB_FLAG")) {
//                    regBankDetails = object.getString("headerData");
//                    customDialog_Common("KYCLAYOUTS", null, null, "Banl Update", "", object.getString("headerData").replace(",","\n"), MainActivity.this);
                } else if (object.getString("headerValue").equalsIgnoreCase("ENABLE_TPIN")) {
                    ENABLE_TPIN = object.getString("headerData");
//                    customDialog_Common("KYCLAYOUTS", null, null, "Banl Update", "", object.getString("headerData").replace(",","\n"), MainActivity.this);
                } else if (object.getString("headerValue").equalsIgnoreCase("IS_KYC_COMPLETED") && object.getString("headerData").equalsIgnoreCase("N")) {
                    customDialog_Common("KYCNEWLAYOUT", null, null, "Warning", null, "Your KYC is not updated, Kindly Proceed to update complete KYC.", MainActivity.this);
//                    customDialog_Common("KYCLAYOUTS", null, null, "Banl Update", "", object.getString("headerData").replace(",","\n"), MainActivity.this);
                } else if (object.getString("headerValue").equalsIgnoreCase("IS_CRIMAGE_REQUIRED")) {
                    IS_CRIMAGE_REQUIRED = object.getString("headerData");
//                    customDialog_Common("KYCLAYOUTS", null, null, "Banl Update", "", object.getString("headerData").replace(",","\n"), MainActivity.this);
                } else {
                    if (object.getString("headerValue").equalsIgnoreCase("TnC")) {
                        term = object.getString("headerData");
                        if (object.getString("headerData").equalsIgnoreCase("Y")) {
                            JSONObject object1 = array.getJSONObject(i + 1);
                            if (object1.getString("headerValue").equalsIgnoreCase("TCLINK")) {
                                new AsyncPostMethod(object1.getString("headerData"), "", "", MainActivity.this, getString(R.string.responseTimeOut)).execute();
                            }
                        }
                    }
                    if (object.getString("headerValue").equalsIgnoreCase("DOWNLOAD_MASTER_DATA")) {
                        data = object.getString("headerData");
                        if (object.getString("headerData").equalsIgnoreCase("Y")) {
                            deleteTables("");
                            callMasterDetails();
                        }
                    }
                }
            }
            for (int j = 0; j < pozoArrayList.size(); j++) {
                if (pozoArrayList.get(j).getHeaderValue().equalsIgnoreCase("My Balance")) {
                    balance = format(pozoArrayList.get(j).getHeaderData());
                }
            }
        } catch (Exception e)

        {
            e.printStackTrace();
        }

        openfragment(0);
    }

    private void callMasterDetails() {
        ArrayList<BankDetailsPozo> list = db.geBanktDetails("");
        if (list.size() == 0) {
            new AsyncPostMethod(WebConfig.CommonReport, getMaster_Validate().toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut), "GETMASTERDATA").execute();
        }
    }

    public JSONObject getMaster_Validate() {
        ArrayList<RapiPayPozo> list = db.getDetails();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_MASTER_DATA");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Blank Value", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    public JSONObject acknowledge(String data, String term) {
        ArrayList<RapiPayPozo> list = db.getDetails();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "UPDATE_DOWNLAOD_DATA_STATUS");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("DataDownloadFlag", data + "1" + term);
                jsonObject.put("agentMobile", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Blank Value", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    @Override
    public void chechStat(String object) {
        customDialog_Common("TERMCONDITION", null, null, "Term & Condition", "", object, MainActivity.this);
    }

    @Override
    public void onBackPressed() {
        if (!isclicked ) {
            isclicked = true;
            bankde.setVisibility(View.GONE);
            reset.setVisibility(View.VISIBLE);
            if (isUrl) {
                isUrl = false;
                loadUrl();
            }
            Fragment fragment = new DashBoardFragments();
            fragmentReplace(fragment);

        } else if (isclicked) {
            customDialog_Common("KYCLAYOUT", null, null, "Rapipay", null, "Are you sure you want to exit ?", MainActivity.this);
        }
    }

    private void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void okClicked(String type, Object ob) {
        super.onBackPressed();
        if (type.equalsIgnoreCase("TERMCONDITION")) {
            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge(data, term).toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut), "DOWMLOADDATA").execute();
        } else if (type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
        else if (type.equalsIgnoreCase("KYCLAYOUTS")) {
        } else if (type.equalsIgnoreCase("KYCNEWLAYOUT")) {
            String formData = getUserKyc();
            Intent intent = new Intent(MainActivity.this, WebViewVerify.class);
            intent.putExtra("persons", "pending");
            intent.putExtra("mobileNo", list.get(0).getMobilno());
            intent.putExtra("formData", formData);
            startActivityForResult(intent, 2);
        } else
            finish();
    }

    @Override
    public void cancelClicked(String type, Object ob) {
        dialog.dismiss();
    }

    public String getUserKyc() {
        JSONObject jsonObject = new JSONObject();
        String form = null;
        try {
            jsonObject.put("serviceType", "UPDATE_KYC_DETAILS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("responseUrl", "");
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("mobileNo", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
            form = "<html>\n" +
                    "\t<body>\n" +
                    "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYCFORWARD + "\">\n" +
                    "\t\t\t<input name=\"requestedData\" value=\"" + getDataBase64(jsonObject.toString()) + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input type=\"submit\"/>\n" +
                    "\t\t</form>\n" +
                    "\t\t<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                    "\t\t\t\t\tdocument.getElementById(\"validatekyc\").submit();\n" +
                    "\t\t</script>\n" +
                    "\t</body>\n" +
                    "</html>";
            return form;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

