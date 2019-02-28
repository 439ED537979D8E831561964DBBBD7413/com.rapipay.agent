package com.rapipay.android.agent.main_directory;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.ChangeMobileFragment;
import com.rapipay.android.agent.fragments.ChangePassword;
import com.rapipay.android.agent.fragments.ChangePinFragment;
import com.rapipay.android.agent.fragments.DashBoardFragments;
import com.rapipay.android.agent.fragments.LienHistory;
import com.rapipay.android.agent.fragments.PayloadBankFragment;
import com.rapipay.android.agent.fragments.ProfileFragment;
import com.rapipay.android.agent.fragments.SettlementBankFragment;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.MasterClass;
import com.rapipay.android.agent.utils.WebConfig;

public class MainActivity extends BaseCompactActivity
        implements NavigationView.OnNavigationItemSelectedListener, RequestHandler, CustomInterface, View.OnClickListener {

    public static ImageView ivHeaderPhoto;
    NavigationView navigationView;
    private static final int CAMERA_REQUEST = 1888;
    private int SELECT_FILE = 1;
    private static String filePath;
    private static final String TAG = "Contacts";

    DrawerLayout drawer;
    String data, term = null;
    TextView tv;
    ImageView back_click;
    private static final int dpPhoto1 = 2001;
    private static final int dpPhoto2 = 2002;

    public static ArrayList<HeaderePozo> pozoArrayList;

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
                    back_click.setImageDrawable(getResources().getDrawable(R.drawable.rapipay_agent));
                if (BuildConfig.APPTYPE == 2)
                    back_click.setImageDrawable(getResources().getDrawable(R.drawable.rapipay_parter));
            }
            loadUrl();
        } else {
            dbNull(MainActivity.this);
        }
    }

    private void url() {
        new AsyncPostMethod(WebConfig.COMMONAPI, getDashBoard("GET_NODE_HEADER_DATA").toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut),"NODEHEADERDATA").execute();
        localStorage.setActivityState(LocalStorage.ROUTESTATE, "0");
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
                tv.setText("");
                tv.setVisibility(View.GONE);
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

    private void initialization() {
        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
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
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView verionName = navigationView.findViewById(R.id.btn_sing_in);
        try {
            verionName.setText("Version - "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        }catch (PackageManager.NameNotFoundException e){
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
                selectImage(dpPhoto1,dpPhoto2,"DPPHOTO");
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
            if(data!=null) {
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
                String[] splits = filePath.split("\\/");
                ivHeaderPhoto.setImageBitmap(thumbnail);
                String imageName = "image" + ".jpg";
                String path = saveToInternalStorage(thumbnail, imageName);
                localStorage.setActivityState(LocalStorage.IMAGEPATH, path);
            }
        }
    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CAMERA_REQUEST) {
//            String path = data.getStringExtra("ImagePath");
//            String imageType = data.getStringExtra("ImageType");
//            Bitmap bitmap = loadImageFromStorage(imageType, path);
//            ivHeaderPhoto.setImageBitmap(bitmap);
//            String imageName = "image" + ".jpg";
//            String paths = saveToInternalStorage(bitmap, imageName);
//            localStorage.setActivityState(LocalStorage.IMAGEPATH, paths);
//            //SaveInDB(thumbnail);
//        } else if (requestCode == SELECT_FILE) {
//            if(data!=null) {
//                Uri selectedImageUri = data.getData();
//                String[] projection = {MediaStore.MediaColumns.DATA};
//                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
//                        null);
//                Cursor cursor = cursorLoader.loadInBackground();
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                cursor.moveToFirst();
//                String selectedImagePath = cursor.getString(column_index);
//                Bitmap bm;
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(selectedImagePath, options);
//                final int REQUIRED_SIZE = 150;
//                int scale = 1;
//                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
//                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
//                    scale *= 2;
//                options.inSampleSize = scale;
//                options.inJustDecodeBounds = false;
//                bm = BitmapFactory.decodeFile(selectedImagePath, options);
//                ivHeaderPhoto.setImageBitmap(bm);
//                String imageName = "image" + ".jpg";
//                String path = saveToInternalStorage(bm, imageName);
//                localStorage.setActivityState(LocalStorage.IMAGEPATH, path);
//            }
//        }
//    }


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
        int id = item.getItemId();
        Fragment fragment = null;
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
        } else if (id == R.id.nav_Cmobile) {
            reset.setVisibility(View.GONE);
            fragment = new ChangeMobileFragment();
        }else if (id == R.id.settle_Cmobile) {
            reset.setVisibility(View.GONE);
            Bundle bundle=new Bundle();
            bundle.putString("message", "S");
            fragment = new SettlementBankFragment();
            fragment.setArguments(bundle);
        }else if (id == R.id.payload_Cmobile) {
            reset.setVisibility(View.GONE);
            Bundle bundle=new Bundle();
            bundle.putString("message", "P");
            fragment = new SettlementBankFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.lien_Cmobile) {
            reset.setVisibility(View.GONE);
            fragment = new LienHistory();
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
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_MASTER_DATA")) {
                    if (new MasterClass().getMasterData(object, db))
                        if (term.equalsIgnoreCase("N"))
                            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge(data, term).toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut),"DOWMLOADDATA").execute();
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
                if (object.getString("displayFlag").equalsIgnoreCase("D"))
                    pozoArrayList.add(new HeaderePozo(object.getString("headerValue"), object.getString("headerData"), object.getString("headerId"), object.getString("displayFlag")));
                else if (object.getString("headerValue").equalsIgnoreCase("Notice")) {
                    tv.setText(object.getString("headerData"));
                    tv.setVisibility(View.VISIBLE);
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

        itemSelection(0);
    }

    private void callMasterDetails() {
        ArrayList<BankDetailsPozo> list = db.geBanktDetails("");
        if (list.size() == 0) {
            new AsyncPostMethod(WebConfig.CommonReport, getMaster_Validate().toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut),"GETMASTERDATA").execute();
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        customDialog_Common("KYCLAYOUT", null, null, "Rapipay", null, "Are you sure you want to exit ?", MainActivity.this);
    }

    @Override
    public void okClicked(String type, Object ob) {
        super.onBackPressed();
        if (type.equalsIgnoreCase("TERMCONDITION")) {
            new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, acknowledge(data, term).toString(), headerData, MainActivity.this, getString(R.string.responseTimeOut),"DOWMLOADDATA").execute();
        } else if (type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
        else
            finish();
    }

    @Override
    public void cancelClicked(String type, Object ob) {
        alertDialog.dismiss();
    }

}

