package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MPOSRegistration extends BaseCompactActivity implements RequestHandler, View.OnClickListener, CustomInterface {

    WebView web;
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> listPath = new ArrayList<>();
    private long size = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        mpos_service();
    }

    public String getmpos_Validate() {
        String form = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_FORM_DATA");
            jsonObject.put("mobileNo", list.get(0).getMobilno());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("responseUrl", "");
            jsonObject.put("trnasactionId", "GFD" + tsLong.toString());
            form = "<html>\n" +
                    "\t<body>\n" +
                    "\t\t<form name=\"mposRegister\" id=\"mposRegister\" method=\"POST\" action=\"http://192.168.1.106:8082/KYC_RAPIPAY_APP/EnrollmentFormService" + "" + "\">\n" +
                    "\t\t\t<input name=\"serviceType\" value=\"GET_FORM_DATA\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"mobileNo\" value=\"" + list.get(0).getMobilno() + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"agentId\" value=\"" + list.get(0).getMobilno() + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"typeMobileWeb\" value=\"mobile\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"requestType\" value=\"EKYC_CHANNEL\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"nodeAgentId\" value=\"" + list.get(0).getMobilno() + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"sessionRefNo\" value=\"" + list.get(0).getAftersessionRefNo() + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"responseUrl\" value=\"" + "" + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"trnasactionId\" value=\"" + "GFD" + tsLong.toString() + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input name=\"checkSum\" value=\"" + GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()) + "\" type=\"hidden\"/>\n" +
                    "\t\t\t<input type=\"submit\"/>\n" +
                    "\t\t</form>\n" +
                    "\t\t<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                    "\t\t\t\t\tdocument.getElementById(\"mposRegister\").submit();\n" +
                    "\t\t</script>\n" +
                    "\t</body>\n" +
                    "</html>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return form;
    }


    public void mpos_service() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("MPOS Registration");
        web = (WebView) findViewById(R.id.webview01);
        WebSettings webSettings = web.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);

        //        webSettings.setJavaScriptEnabled(true);
        //        webSettings.setUseWideViewPort(true);
        //        webSettings.setLoadWithOverviewMode(true);
        //        webSettings.setAllowFileAccess(true);
        //        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        web.setWebViewClient(new myWebClient());
        web.setWebChromeClient(new PQChromeClient());
        if (Build.VERSION.SDK_INT >= 19)
            web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        String formPostData = getmpos_Validate();
        if (formPostData != null)
            web.loadDataWithBaseURL("", formPostData, "text/html", "UTF-8", "");
    }
    public class PQChromeClient extends WebChromeClient {

        // For Android 5.0+
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePath;
            loadIMEIs();
            return true;
        }
    }
    private void selectPhotoAgent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Unable to create Image File", ex);
            }
            // Continue only if the File was successfully created
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
    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_FORM_DATA")) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
        }
    }

    public class myWebClient extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public void onLoadResource(WebView view, String url) {
            if (progressDialog == null) {
                // in standard case YourActivity.this
                progressDialog = new ProgressDialog(MPOSRegistration.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            try {
                if (progressDialog.isShowing()) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    if (cookieManager == null) {
                    }
                    String rawCookieHeader = null;
                    URL parsedURL = new URL(url);
                    rawCookieHeader = cookieManager.getCookie(url);
                    Log.e("COOKIES", rawCookieHeader);
                    progressDialog.dismiss();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MPOSRegistration.this);
            builder.setMessage(R.string.notification_error_ssl_cert_invalid);
            builder.setCancelable(false);
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            String response = inputStreamAsString(url);
            if (response != null) {
                try {
                    if (response.equalsIgnoreCase("User Cancel The Request")||response.equalsIgnoreCase("Data Inserted Successfully!")) {
                        setBack_click(MPOSRegistration.this);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;

        }
    }

    public String inputStreamAsString(String url) {
        String data = null;
        try {
            Map<String, String> map = getQueryMap(url);
            if (map != null) {
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    System.out.println("Name=" + key);
                    System.out.println("Value=" + map.get(key).replaceAll("%20", " "));
                    byte[] value = Base64.decode(map.get(key).getBytes("UTF-8"), Base64.DEFAULT);
                    data = map.get(key).replaceAll("%20", " ");
                }
                return data;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getQueryMap(String query) {
        if (query.contains("?")) {
            String[] params = query.split("\\?");
            String[] name;
            Map<String, String> map = new HashMap<String, String>();
            if (params.length != 0) {
                name = params[1].split("=");
                if (name.length != 0)
                    map.put(name[0], name[1]);
            }
            return map;
        } else {
            finish();
            return null;
        }
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("KYCLAYOUTS")) {
            setBack_click(MPOSRegistration.this);
            finish();
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
    public void loadIMEIs() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(MPOSRegistration.this, Manifest.permission.CAMERA)
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(MPOSRegistration.this,
                Manifest.permission.CAMERA)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MPOSRegistration.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(MPOSRegistration.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    private void alertPerm(String msg, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MPOSRegistration.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(MPOSRegistration.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            if (TYPE.equalsIgnoreCase("internal")) {
//                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                    // Do something for lollipop and above versions
//                    selectPhotoCustomer();
//                } else {
//                    // do something for phones running an SDK before lollipop
//                    selectPhotoAgent();
//                }
//
//            } else if (TYPE.equalsIgnoreCase("outside") || TYPE.equalsIgnoreCase("pending"))
                selectPhotoAgent();
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
                        loadIMEIs();
                    }
                });

            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mUploadMessage == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (resultCode == 0) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
            return;
        }
        try {
            String file_path = mCameraPhotoPath.replace("file:", "");
            listPath.add(file_path);
            File file = new File(file_path);
            size = file.length();

        } catch (Exception e) {
            Log.e("Error!", "Error while opening image file" + e.getLocalizedMessage());
        }

        if (data != null || mCameraPhotoPath != null) {
            Integer count = 1;
            ClipData images = null;
            try {
                images = data.getClipData();
            } catch (Exception e) {
                Log.e("Error!", e.getLocalizedMessage());
            }

            if (images == null && data != null && data.getDataString() != null) {
                count = data.getDataString().length();
            } else if (images != null) {
                count = images.getItemCount();
            }
            Uri[] results = new Uri[count];
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (size != 0) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else if (data.getClipData() == null) {
                    results = new Uri[]{Uri.parse(data.getDataString())};
                } else {

                    for (int i = 0; i < images.getItemCount(); i++) {
                        results[i] = images.getItemAt(i).getUri();
                    }
                }
            }

            mUploadMessage.onReceiveValue(results);
            mUploadMessage = null;
        }
    }

}