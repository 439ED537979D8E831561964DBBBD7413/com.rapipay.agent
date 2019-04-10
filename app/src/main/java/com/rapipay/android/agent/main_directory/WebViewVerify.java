package com.rapipay.android.agent.main_directory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebViewVerify extends BaseCompactActivity implements CustomInterface, View.OnClickListener {
    WebView web;
    private ValueCallback<Uri[]> mUploadMessage;
    private String mobileNo, formData;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private long size = 0;
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
        mobileNo = getIntent().getStringExtra("mobileNo");
        web = (WebView) findViewById(R.id.webview01);
        WebSettings webSettings = web.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        web.setWebViewClient(new myWebClient());
        web.setWebChromeClient(new PQChromeClient());
        if (Build.VERSION.SDK_INT >= 19) {
            web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        if (TYPE.equalsIgnoreCase("pending")) {
            formData = getIntent().getStringExtra("formData");
            if (formData != null)
                web.loadDataWithBaseURL("", formData, "text/html", "UTF-8", "");
        }else {
            if (KYCFormActivity.formData != null)
                web.loadDataWithBaseURL("", KYCFormActivity.formData, "text/html", "UTF-8", "");
        }
    }

    public class myWebClient extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public void onLoadResource(WebView view, String url) {
            if (progressDialog == null) {
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
            if (!url.contains("offlineaadhaar")) {
                String response = inputStreamAsString(url);
                if (response != null) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("responseCode"))
                            if (object.getString("responseCode").equalsIgnoreCase("60187")||object.getString("responseCode").equalsIgnoreCase("200")) {
                                customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object.getString("responseMessage"), WebViewVerify.this);
                                //                            customDialog(object.getString("responseMessage"));
                            } else if (object.getString("responseCode").equalsIgnoreCase("60147") || object.getString("responseCode").equalsIgnoreCase("60173")) {
                                deleteFile();
                                if (TYPE.equalsIgnoreCase("internal")) {
                                    Intent intent = new Intent(WebViewVerify.this, WalletDetailsActivity.class);
                                    intent.putExtra("mobileNo", "");
                                    intent.putExtra("type", "");
                                    startActivity(intent);
                                } else if (TYPE.equalsIgnoreCase("outside")) {
                                    Intent intent = new Intent(WebViewVerify.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                finish();
                            }else {
                                customDialog_Common("KYCLAYOUTSS", null, null, getResources().getString(R.string.Alert), null, object.getString("responseMessage"), WebViewVerify.this);
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
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
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePath;
            loadCamera();
            return true;
        }
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
            if (resultCode == Activity.RESULT_OK) {
                if (size != 0) {
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
    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("KYCLAYOUTS")) {
            db.deleteRow(mobileNo, "");
            setBack_click(this);
            finish();
        }else if (type.equalsIgnoreCase("KYCLAYOUTSS")) {
            setBack_click(this);
            finish();
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                setBack_click(this);
                Intent intent = new Intent();
                setResult(2,intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(2,intent);
        finish();
    }
}
