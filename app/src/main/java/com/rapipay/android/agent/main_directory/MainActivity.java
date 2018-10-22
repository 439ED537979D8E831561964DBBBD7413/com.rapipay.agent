package com.rapipay.android.agent.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.ChangePassword;
import com.rapipay.android.agent.fragments.ChangePinFragment;
import com.rapipay.android.agent.fragments.DashBoardFragments;
import com.rapipay.android.agent.fragments.ProfileFragment;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.MasterClass;
import com.rapipay.android.agent.utils.WebConfig;

public class MainActivity extends BaseCompactActivity
        implements NavigationView.OnNavigationItemSelectedListener, RequestHandler, CustomInterface, View.OnClickListener {
    String regId = "";
    public static String _val = "";
    private static String _type = "";
    static Boolean _internet;
    public static ImageView ivHeaderPhoto;
    TextView footettext;
    NavigationView navigationView;
    private static final int CAMERA_REQUEST = 1888;
    private int SELECT_FILE = 1;
    private static String filePath;
    private static final String TAG = "Contacts";

    DrawerLayout drawer;
    String data, term = null;

    public static ArrayList<HeaderePozo> pozoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = db.getDetails();
        initialization();
        loadUrl();
    }

    private void url() {
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getDashBoard("GET_NODE_HEADER_DATA").toString(), headerData, MainActivity.this).execute();
    }

    private void loadUrl() {
        if (localStorage.getActivityState(LocalStorage.ROUTESTATE).equalsIgnoreCase("UPDATE")) {
            url();
        } else
            itemSelection(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                url();
                break;
        }
    }

    public JSONObject getDashBoard(String servicetype) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", servicetype);
            jsonObject.put("requestType", "BC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", "GNHD" + tsLong.toString());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initialization() {
        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        reset.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
//        reset.setVisibility(View.VISIBLE);
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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ivHeaderPhoto = (ImageView) headerLayout.findViewById(R.id.imageView);
        if (!localStorage.getActivityState(LocalStorage.IMAGEPATH).equalsIgnoreCase("0"))
            loadImageFromStorage("image.jpg", ivHeaderPhoto, localStorage.getActivityState(LocalStorage.IMAGEPATH));
        TextView name_agent = (TextView) headerLayout.findViewById(R.id.name_agent);
        name_agent.setText(list.get(0).getAgentName());
        ivHeaderPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void loadImageFromStorage(String name, ImageView view, String path) {

        try {
            File f = new File(path, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            view.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

//    private void setupTabLayout() {
//        fragment = new DashBoardFragments();
//        fragment = new DashBoardFragments();
//
//        tabLayout.addTab(tabLayout.newTab().setText("Home"), true);
//        tabLayout.addTab(tabLayout.newTab().setText("TWO"));
//    }
//
//    private void bindWidgetsWithAnEvent() {
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                setCurrentTabFragment(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });
//    }

//    private void setCurrentTabFragment(int tabPosition) {
//        switch (tabPosition) {
//            case 0:
//                replaceFragment(fragment);
//                break;
//            case 1:
//                replaceFragment(fragment);
//                break;
//        }
//    }
//
//    public void replaceFragment(Fragment fragment) {
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.frame_container, fragment);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        ft.commit();
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date date = new Date();
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
                ivHeaderPhoto.setImageBitmap(thumbnail);
                String imageName = "image" + ".jpg";
                String path = saveToInternalStorage(thumbnail, imageName);
                localStorage.setActivityState(LocalStorage.IMAGEPATH, path);
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
                final int REQUIRED_SIZE = 150;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                ivHeaderPhoto.setImageBitmap(bm);
                String imageName = "image" + ".jpg";
                String path = saveToInternalStorage(bm, imageName);
                localStorage.setActivityState(LocalStorage.IMAGEPATH, path);
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
        if (id == R.id.nav_home) {
            reset.setVisibility(View.VISIBLE);
            fragment = new DashBoardFragments();
        } else if (id == R.id.nav_Cpin) {
            reset.setVisibility(View.GONE);
            fragment = new ChangePinFragment();
        } else if (id == R.id.nav_cpsw) {
            reset.setVisibility(View.GONE);
            fragment = new ChangePassword();
        } else if (id == R.id.profile) {
            reset.setVisibility(View.GONE);
            fragment = new ProfileFragment();
        } else if (fragment == null)
            Toast.makeText(MainActivity.this, "Under Process", Toast.LENGTH_SHORT).show();
        if (fragment != null)
            fragmentReplace(fragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void fragmentReplace(Fragment fragment) {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void itemSelection(int id) {
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
//        switch (id) {
//            case 0:
//                DashBoardFragments fragment = new DashBoardFragments();
//                fragmentReplace(fragment);
//                break;
//        }
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_MASTER_DATA")) {
                    if (new MasterClass().getMasterData(object, db))
                        if (term.equalsIgnoreCase("N"))
                            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge(data, term).toString(), headerData, MainActivity.this).execute();
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_NODE_HEADER_DATA")) {
                    if (object.has("headerList")) {
                        localStorage.setActivityState(LocalStorage.ROUTESTATE, "0");
                        if (Integer.parseInt(object.getString("listCount")) > 0) {
                            insertLastTransDetails(object.getJSONArray("headerList"));
                        }
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("UPDATE_DOWNLAOD_DATA_STATUS")) {
                    loadUrl();
                }
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
                if (!object.getString("headerValue").equalsIgnoreCase("DOWNLOAD_MASTER_DATA") && !object.getString("headerValue").equalsIgnoreCase("TnC") && !object.getString("headerValue").equalsIgnoreCase("TCLINK") && !object.getString("headerValue").equalsIgnoreCase("Parent Mobile"))
                    pozoArrayList.add(new HeaderePozo(object.getString("headerValue"), object.getString("headerData"), object.getString("headerId")));
                else {
                    if (object.getString("headerValue").equalsIgnoreCase("TnC")) {
                        term = object.getString("headerData");
                        if (object.getString("headerData").equalsIgnoreCase("Y")) {
                            JSONObject object1 = array.getJSONObject(i + 1);
                            if (object1.getString("headerValue").equalsIgnoreCase("TCLINK")) {
                                new AsyncPostMethod(object1.getString("headerData"), "", "", MainActivity.this).execute();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        itemSelection(0);
//        setse
//        bindWidgetsWithAnEvent();
//        setupTabLayout();
    }

    private void callMasterDetails() {
        ArrayList<BankDetailsPozo> list = db.geBanktDetails("");
        if (list.size() == 0) {
            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getMaster_Validate().toString(), headerData, MainActivity.this).execute();
        }
    }

    public JSONObject getMaster_Validate() {
        ArrayList<RapiPayPozo> list = db.getDetails();
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_MASTER_DATA");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", "GMD" + tsLong.toString());
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
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "UPDATE_DOWNLAOD_DATA_STATUS");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", "UDDS" + tsLong.toString());
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        customDialog_Common("KYCLAYOUT", null, null, "Rapipay", null, "Are you sure you want to exit ?", MainActivity.this);
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            finish();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
    }

    @Override
    public void okClicked(String type, Object ob) {
        super.onBackPressed();
        if (type.equalsIgnoreCase("TERMCONDITION")) {
            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge(data, term).toString(), headerData, MainActivity.this).execute();
        } else
            finish();
    }

    @Override
    public void cancelClicked(String type, Object ob) {
        alertDialog.dismiss();
    }

    private void selectImage() {
        final CharSequence[] items = {"Capture Image", "Choose from Gallery", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Image")) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

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
}

