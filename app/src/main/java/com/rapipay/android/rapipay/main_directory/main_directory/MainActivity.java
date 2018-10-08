package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Database.RapipayDB;
import com.rapipay.android.rapipay.main_directory.Model.BankDetailsPozo;
import com.rapipay.android.rapipay.main_directory.Model.HeaderePozo;
import com.rapipay.android.rapipay.main_directory.Model.RapiPayPozo;
import com.rapipay.android.rapipay.main_directory.fragments.DashBoardFragments;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.MasterClass;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseCompactActivity
        implements NavigationView.OnNavigationItemSelectedListener, RequestHandler {
    String regId = "";
    public static String _val = "";
    private static String _type = "";
    static Boolean _internet;
    public static ImageView ivHeaderPhoto;
    TextView footettext;
    String headerData;
    private static final int CAMERA_REQUEST = 1888;
    private int SELECT_FILE = 1;
    private static String filePath;
    private static final String TAG = "Contacts";

    DrawerLayout drawer;
    TabLayout tabLayout;
    boolean doubleBackToExitPressedOnce = false;

    private static LinearLayout linearlay_verification, linearlay_newverification;
    Context _context;
    DashBoardFragments fragment;
    public static ArrayList<HeaderePozo> pozoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = db.getDetails();
//        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        initialization();
        loadUrl();
//        ivHeaderPhoto =(ImageView) headerLayout.findViewById(R.id.profile_image);
//        ivHeaderPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImage();
//            }
//        });
    }


    private void loadUrl() {
        headerData = (WebConfig.BASIC_USERID + ":" + WebConfig.BASIC_PASSWORD);
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getDashBoard("GET_NODE_HEADER_DATA").toString(), headerData, MainActivity.this).execute();
    }

    public JSONObject getDashBoard(String servicetype) {
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_Channel");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "header"+format.format(date));
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initialization() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tabLayout = (TabLayout) findViewById(R.id.bottomNavigation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    private void setupTabLayout() {
        fragment = new DashBoardFragments();
        fragment = new DashBoardFragments();

        tabLayout.addTab(tabLayout.newTab().setText("Home"), true);
        tabLayout.addTab(tabLayout.newTab().setText("TWO"));
    }

    private void bindWidgetsWithAnEvent() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragment);
                break;
            case 1:
                replaceFragment(fragment);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                date = new Date();
                File destination = new File(Environment.getExternalStorageDirectory(),
                        format.format(date) + ".jpg");
                filePath = destination.toString();
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                ivHeaderPhoto.setImageBitmap(thumbnail);
                //SaveInDB(thumbnail);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
//                ivHeaderPhoto.setImageBitmap(bm);
                // SaveInDB(bm);
                // filePath =selectedImagePath;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
//        fragment = new Change_Password();
//        if (id == R.id.nav_cpsw) {
//            fragment = new Change_Password();
//        }
//        if (id == R.id.nav_Cpin) {
//            fragment = new Change_Pin();
//        }

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_MASTER_DATA")) {
                    if (new MasterClass().getMasterData(object, db))
                        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge().toString(), headerData, MainActivity.this).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_NODE_HEADER_DATA")) {
                    if (object.has("headerList")) {
                        if (Integer.parseInt(object.getString("listCount")) > 0) {
                            insertLastTransDetails(object.getJSONArray("headerList"));
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("UPDATE_DOWNLAOD_DATA_STATUS")) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLastTransDetails(JSONArray array) {
        pozoArrayList = new ArrayList<HeaderePozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (!object.getString("headerValue").equalsIgnoreCase("DOWNLOAD_MASTER_DATA"))
                    pozoArrayList.add(new HeaderePozo(object.getString("headerValue"), object.getString("headerData"), object.getString("headerId")));
                else {
                    if (object.getString("headerData").equalsIgnoreCase("Y")) {
                        SQLiteDatabase dba = db.getWritableDatabase();
                        dba.execSQL("delete from " + RapipayDB.TABLE_BANK);
                        dba.execSQL("delete from " + RapipayDB.TABLE_PAYMENT);
                        dba.execSQL("delete from " + RapipayDB.TABLE_STATE);
                        dba.execSQL("delete from " + RapipayDB.TABLE_OPERATOR);
                        callMasterDetails();
                    }
                }
            }
            for (int j = 0; j < pozoArrayList.size(); j++) {
                if (pozoArrayList.get(j).getHeaderValue().equalsIgnoreCase("My Balance")) {
                    balance = format(pozoArrayList.get(j).getHeaderData());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void callMasterDetails() {
        ArrayList<BankDetailsPozo> list = db.geBanktDetails("");
        if (list.size() == 0) {
            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getMaster_Validate().toString(), headerData, MainActivity.this).execute();
        }
    }

    public JSONObject getMaster_Validate() {
        ArrayList<RapiPayPozo> list = db.getDetails();
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_MASTER_DATA");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", "master"+format.format(date));
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

    public JSONObject acknowledge() {
        ArrayList<RapiPayPozo> list = db.getDetails();
        format = new SimpleDateFormat("ddMMyyyyHHmmss");
        date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "UPDATE_DOWNLAOD_DATA_STATUS");
                jsonObject.put("requestType", "BC_Channel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", "status"+format.format(date));
                jsonObject.put("DataDownloadFlag", "Y");
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

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private String format(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

