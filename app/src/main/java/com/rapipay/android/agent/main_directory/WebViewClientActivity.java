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
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.RegisterUserFragment;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.WebConfig;


public class WebViewClientActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener, CustomInterface {
    WebView web;
    String mobileNo, parentId, sessionKey, sessionRefNo, nodeAgent, kycType, TYPE, base64image;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> listPath = new ArrayList<>();
    private long size = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        TYPE = getIntent().getStringExtra("type");
        if (TYPE.equalsIgnoreCase("internal"))
            findViewById(R.id.back_click).setVisibility(View.VISIBLE);
        else if (TYPE.equalsIgnoreCase("outside"))
            findViewById(R.id.back_click).setVisibility(View.VISIBLE);
        initialize();
    }

    public void loadIMEIs() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(WebViewClientActivity.this, Manifest.permission.CAMERA)
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(WebViewClientActivity.this,
                Manifest.permission.CAMERA)) {
            alertPerm(getString(R.string.permission_read_phone_state_rationale), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(WebViewClientActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    doPermissionGrantedStuffs();
                }
            });

        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(WebViewClientActivity.this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    private void alertPerm(String msg, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(WebViewClientActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, okListener)
                .setIcon(R.mipmap.ic_launcher_round)
                .show();
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(WebViewClientActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (TYPE.equalsIgnoreCase("internal"))
                selectPhotoCustomer();
            else if (TYPE.equalsIgnoreCase("outside"))
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                finish();
                break;
        }
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("KYC Registration");
        mobileNo = getIntent().getStringExtra("mobileNo");
        parentId = getIntent().getStringExtra("parentId");
        sessionKey = getIntent().getStringExtra("sessionKey");
        sessionRefNo = getIntent().getStringExtra("sessionRefNo");
        nodeAgent = getIntent().getStringExtra("nodeAgent");
        base64image = getIntent().getStringExtra("base64");
        //        base64image+="~"+RegisterUserActivity.byteBase64;
        if (TYPE.equalsIgnoreCase("internal"))
            kycType = "C";
        else
            kycType = "A";
        new AsyncPostMethod(WebConfig.EKYC, getJson_Validate(mobileNo, kycType, parentId, sessionKey, sessionRefNo, nodeAgent).toString(), headerData, WebViewClientActivity.this).execute();
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

        //        web.postUrl(WebConfig.UAT, EncodingUtils.getBytes(getJson_Validate(mobileNo,"A",parentId,sessionKey).toString(), "BASE64"));
    }

    public class myWebClient extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public void onLoadResource(WebView view, String url) {
            if (progressDialog == null) {
                // in standard case YourActivity.this
                progressDialog = new ProgressDialog(WebViewClientActivity.this);
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewClientActivity.this);
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
                            deleteFile();
                            customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object.getString("responseMessage"), WebViewClientActivity.this);
                            //                            customDialog(object.getString("responseMessage"));
                        } else if (object.getString("responseCode").equalsIgnoreCase("60147") || object.getString("responseCode").equalsIgnoreCase("60173")) {
                            deleteFile();
                            if (TYPE.equalsIgnoreCase("internal")) {
                                Intent intent = new Intent(WebViewClientActivity.this, WalletDetailsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else if (TYPE.equalsIgnoreCase("outside")) {
                                Intent intent = new Intent(WebViewClientActivity.this, MainActivity.class);
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

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                Toast.makeText(this, object.getString("responseMessage").toString(), Toast.LENGTH_SHORT).show();
                if (object.getString("serviceType").equalsIgnoreCase("KYC_PROCESS")) {
                    String formPostData = getsession_ValidateKyc(object.getString("token"), object.getString("txnRef"));
                    if (formPostData != null)
                        web.loadDataWithBaseURL("", formPostData, "text/html", "UTF-8", "");
                    //                        web.loadData(formPostData, "text/html", "UTF-8");
                } else if (object.getString("serviceType").equalsIgnoreCase("VALIDATE_KYC_PROCESS")) {

                }
                //                Intent intent = new Intent(WebViewClientActivity.this, PinActivity.class);
                //                intent.putExtra("agentId", input_user.getText().toString());
                //                intent.putExtra("regTxnRefId", object.getString("txnRef"));
                //                intent.putExtra("imeiNo", object.getString("token"));
                //                intent.putExtra("otpRefId", object.getString("otpRefId"));
                //                intent.putExtra("sessionRefNo", object.getString("sessionRefNo"));
                //                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public String getsession_ValidateKyc(String tokenId, String orgTxnRef) {
        tsLong = System.currentTimeMillis() / 1000;
        JSONObject jsonObject = new JSONObject();
        String form = null;
        try {
            jsonObject.put("serviceType", "VALIDATE_KYC_PROCESS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("agentId", parentId);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("tokenId", tokenId);
            jsonObject.put("txnRef", "VKP" + tsLong.toString());
            jsonObject.put("orgTxnRef", orgTxnRef);
            jsonObject.put("nodeAgentId", nodeAgent);
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("kycData", base64image.replaceAll("\n", ""));
            if (TYPE.equalsIgnoreCase("outside")) {
                //                jsonObject.put("nodeAgentId", mobileNo);
                //                jsonObject.put("sessionRefNo", sessionRefNo);
                //                jsonObject.put("kycData", base64image.replaceAll("\n", ""));
                form = "<html>\n" +
                        "\t<body>\n" +
                        "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYC_FORWARD + "\">\n" +
                        "\t\t\t<input name=\"serviceType\" value=\"VALIDATE_KYC_PROCESS\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"requestType\" value=\"EKYC_CHANNEL\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"agentId\" value=\"" + parentId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"typeMobileWeb\" value=\"mobile\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"sessionRefNo\" value=\"" + list.get(0).getAftersessionRefNo() + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"nodeAgentId\" value=\"" + nodeAgent + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"tokenId\" value=\"" + tokenId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"txnRef\" value=\"" + "VKP" + tsLong.toString() + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"orgTxnRef\" value=\"" + orgTxnRef + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"kycData\" value=\"" + base64image + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"kycImage\" value=\"" + RegisterUserFragment.byteBase64 + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"checkSum\" value=\"" + GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()) + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input type=\"submit\"/>\n" +
                        "\t\t</form>\n" +
                        "\t\t<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                        "\t\t\t\t\tdocument.getElementById(\"validatekyc\").submit();\n" +
                        "\t\t</script>\n" +
                        "\t</body>\n" +
                        "</html>";
            } else {
                form = "<html>\n" +
                        "\t<body>\n" +
                        "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYC_FORWARD + "\">\n" +
                        "\t\t\t<input name=\"serviceType\" value=\"VALIDATE_KYC_PROCESS\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"requestType\" value=\"EKYC_CHANNEL\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"agentId\" value=\"" + parentId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"typeMobileWeb\" value=\"mobile\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"sessionRefNo\" value=\"" + list.get(0).getAftersessionRefNo() + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"nodeAgentId\" value=\"" + nodeAgent + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"tokenId\" value=\"" + tokenId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"txnRef\" value=\"" + "VKP" + tsLong.toString() + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"orgTxnRef\" value=\"" + orgTxnRef + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"kycData\" value=\"" + base64image + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"kycImage\" value=\"" + RegisterUserActivity.byteBase64 + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"checkSum\" value=\"" + GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()) + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input type=\"submit\"/>\n" +
                        "\t\t</form>\n" +
                        "\t\t<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                        "\t\t\t\t\tdocument.getElementById(\"validatekyc\").submit();\n" +
                        "\t\t</script>\n" +
                        "\t</body>\n" +
                        "</html>";
            }
            return form;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private void selectPhotoCustomer() {
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

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewClientActivity.this);
        builder.setIcon(R.drawable.camera);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
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

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    contentSelectionIntent.setType("image/*");

                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[1];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), 1);

                    //                } else if (items[item].equals("Choose from Gallery")) {
                    //                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //                    intent.setType("image/*");
                    //                    startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
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

    private void customDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        //Setting message manually and performing action on button click
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void onBackPressed() {
        deleteFile();
        Intent intent = null;
        if (TYPE.equalsIgnoreCase("internal")) {
            intent = new Intent(WebViewClientActivity.this, WalletDetailsActivity.class);
            intent.putExtra("mobileNo", "");
            intent.putExtra("type", "");
        } else if (TYPE.equalsIgnoreCase("outside")) {
            intent = new Intent(WebViewClientActivity.this, MainActivity.class);
            //        } else if (TYPE.equalsIgnoreCase("pending")) {
            //            intent = new Intent(WebViewClientActivity.this, RegisterKYCTab.class);
            //            intent.putExtra("type", "Outside");
            //            intent.putExtra("mobileNo", "");
            //        } else {
            //            intent = new Intent(WebViewClientActivity.this, LoginScreenActivity.class);
        }
        if (intent != null)
            startActivity(intent);
        finish();
    }

    @Override
    public void okClicked(String type, Object ob) {
        Intent intent = null;
        if (TYPE.equalsIgnoreCase("internal")) {
            intent = new Intent(WebViewClientActivity.this, WalletDetailsActivity.class);
            intent.putExtra("mobileNo", mobileNo);
            intent.putExtra("type", "internal");
        } else if (TYPE.equalsIgnoreCase("outside")) {
            intent = new Intent(WebViewClientActivity.this, MainActivity.class);
            //        } else if (TYPE.equalsIgnoreCase("pending")) {
            //            intent = new Intent(WebViewClientActivity.this, RegisterKYCTab.class);
            //            intent.putExtra("type", "Outside");
            //            intent.putExtra("mobileNo", "");
        }
        if (intent != null)
            startActivity(intent);
        finish();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
