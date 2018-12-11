package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.app.ProgressDialog;
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
import com.rapipay.android.agent.utils.BaseCompactActivity;

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

public class WebViewVerify extends BaseCompactActivity implements CustomInterface, View.OnClickListener {
    WebView web;
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> listPath = new ArrayList<>();
    private long size = 0;
    private String TYPE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("KYC Registration");
        TYPE = getIntent().getStringExtra("persons");
//        String formData = getIntent().getStringExtra("formData");
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
        if (Build.VERSION.SDK_INT >= 19) {
            web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        if (KYCFormActivity.formData != null)
            web.loadDataWithBaseURL("", KYCFormActivity.formData, "text/html", "UTF-8", "");
        //        web.postUrl(WebConfig.UAT, EncodingUtils.getBytes(getJson_Validate(mobileNo,"A",parentId,sessionKey).toString(), "BASE64"));
    }

    public void loadIMEIs() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(WebViewVerify.this, Manifest.permission.CAMERA)
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(WebViewVerify.this,
                Manifest.permission.CAMERA)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(WebViewVerify.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(WebViewVerify.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    private void alertPerm(String msg, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(WebViewVerify.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(WebViewVerify.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (TYPE.equalsIgnoreCase("internal")) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    // Do something for lollipop and above versions
                    selectPhotoCustomer();
                } else {
                    // do something for phones running an SDK before lollipop
                    selectPhotoAgent();
                }

            } else if (TYPE.equalsIgnoreCase("outside") || TYPE.equalsIgnoreCase("pending"))
                selectPhotoAgent();
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

    private void selectPhotoCustomer() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
        startActivityForResult(Intent.createChooser(takePictureIntent, "Select images"), 1);
        //        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        //        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //        contentSelectionIntent.setType("image/*");
        //
        //        Intent[] intentArray;
        //        if (takePictureIntent != null) {
        //            intentArray = new Intent[]{takePictureIntent};
        //        } else {
        //            intentArray = new Intent[1];
        //        }
        //
        //        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        //        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        //        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        //        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        //        startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), 1);
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

    public class myWebClient extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public void onLoadResource(WebView view, String url) {
            if (progressDialog == null) {
                // in standard case YourActivity.this
                progressDialog = new ProgressDialog(WebViewVerify.this);
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewVerify.this);
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
                    JSONObject object = new JSONObject(response);
                    if (object.has("responseCode"))
                        if (object.getString("responseCode").equalsIgnoreCase("60187")) {
                            customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object.getString("responseMessage"), WebViewVerify.this);
                            //                            customDialog(object.getString("responseMessage"));
                        } else if (object.getString("responseCode").equalsIgnoreCase("60147") || object.getString("responseCode").equalsIgnoreCase("60173")) {
                            deleteFile();
                            if (TYPE.equalsIgnoreCase("internal")) {
                                if (RegisterUserActivity.scan_check == 2) {
                                    Intent intent = new Intent(WebViewVerify.this, WalletDetailsActivity.class);
                                    intent.putExtra("mobileNo", "");
                                    intent.putExtra("type", "");
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(WebViewVerify.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    //                                intent.putExtra("type", "Outside");
                                    //                                intent.putExtra("mobileNo", "");
                                    startActivity(intent);
                                }
                            } else if (TYPE.equalsIgnoreCase("outside")) {
                                Intent intent = new Intent(WebViewVerify.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //                                intent.putExtra("type", "Outside");
                                //                                intent.putExtra("mobileNo", "");
                                startActivity(intent);
                                //                            } else {
                                //                                Intent intent = new Intent(WebViewClientActivity.this, LoginScreenActivity.class);
                                //                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //                                startActivity(intent);
                            }
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
                    System.out.println("Value=" + map.get(key));
                    byte[] value = Base64.decode(map.get(key).getBytes("UTF-8"), Base64.DEFAULT);
                    data = new String(value, StandardCharsets.UTF_8);
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

    public class PQChromeClient extends WebChromeClient {

        // For Android 5.0+
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePath;
            loadIMEIs();
            //            selectImage();
            //            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //                // Create the File where the photo should go
            //                File photoFile = null;
            //                try {
            //                    photoFile = createImageFile();
            //                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            //                } catch (IOException ex) {
            //                    // Error occurred while creating the File
            //                    Log.e(TAG, "Unable to create Image File", ex);
            //                }
            //
            //                // Continue only if the File was successfully created
            //                if (photoFile != null) {
            //                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
            //                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            //                } else {
            //                    takePictureIntent = null;
            //                }
            //            }
            //
            //            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            //            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            //            contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            //            contentSelectionIntent.setType("image/*");
            //
            //            Intent[] intentArray;
            //            if (takePictureIntent != null) {
            //                intentArray = new Intent[]{takePictureIntent, intent};
            //            } else {
            //                intentArray = new Intent[2];
            //            }
            //
            //            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            //            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            //            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            //            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            //            startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), 1);

            return true;

        }
    }

    private void deleteFile() {
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

    @Override
    public void okClicked(String type, Object ob) {

    }

    @Override
    public void cancelClicked(String type, Object ob) {

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
}
