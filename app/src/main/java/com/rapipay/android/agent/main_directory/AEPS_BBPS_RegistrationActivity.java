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
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.WebConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AEPS_BBPS_RegistrationActivity extends BaseCompactActivity implements CustomInterface, View.OnClickListener {
    WebView web;
    private ValueCallback<Uri[]> mUploadMessage;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private long size = 0;
    String typeput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        typeput = getIntent().getStringExtra("typeput");
        initialize();
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        if (typeput.equalsIgnoreCase("AEPS"))
            heading.setText("AEPS Registration");
        else if (typeput.equalsIgnoreCase("BBPS"))
            heading.setText("BBPS Registration");
        web = (WebView) findViewById(R.id.webview01);
        TYPE = getIntent().getStringExtra("persons");
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
        web.loadDataWithBaseURL("", getregistrationDetails(), "text/html", "UTF-8", "");
    }

    public String getregistrationDetails() {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                if (typeput.equalsIgnoreCase("AEPS")) {
                    jsonObject.put("serviceType", "AEPS_REGISTRATION");
                    jsonObject.put("requestType", "AEPS_CHANNEL");
                } else if (typeput.equalsIgnoreCase("BBPS")) {
                    jsonObject.put("serviceType", "BBPS_REGISTRATION");
                    jsonObject.put("requestType", "BBPS_CHANNEL");
                } else if (typeput.equalsIgnoreCase("MATM")) {
                    jsonObject.put("serviceType", "MATM_REGISTRATION");
                    jsonObject.put("requestType", "MATM_CHANNEL");
                }
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
                jsonObject.put("responseUrl", "");
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));
                String form = "<html>\n" +
                        "\t<body>\n" +
                        "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.AEPSReg + "\">\n" +
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
        }
        return null;
    }

    public class myWebClient extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public void onLoadResource(WebView view, String url) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(AEPS_BBPS_RegistrationActivity.this);
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(AEPS_BBPS_RegistrationActivity.this);
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
            if (!url.contains("offlineaadhaar")) {
                String response = inputStreamAsString(url);
                if (response != null) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("responseCode"))
                            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                                customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object.getString("responseMessage"), AEPS_BBPS_RegistrationActivity.this);
                            } else if (object.getString("responseCode").equalsIgnoreCase("75161")) {
                                customDialog_Common("KYCLAYOUT", null, null, getResources().getString(R.string.Alert), null, object.getString("responseMessage"), AEPS_BBPS_RegistrationActivity.this);
                            } else
                                customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object.getString("responseMessage"), AEPS_BBPS_RegistrationActivity.this);
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

    public String getsession_ValidateKyc(String kycType) {
        JSONObject kycMapData = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        String form = null;
        try {
            kycMapData.put("mobileNo", list.get(0).getMobilno());
            jsonObject.put("serviceType", "KYC_PROCESS");
            jsonObject.put("reKYC", "Y");
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("agentId", list.get(0).getMobilno());
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("kycType", kycType);
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("isreKYC", "N");
            jsonObject.put("isAuto", "1");
            jsonObject.put("isEditable", "Y");
            jsonObject.put("listdata", kycMapData.toString());
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
            setBack_click(this);
            finish();
        } else if (type.equalsIgnoreCase("KYCLAYOUT")) {
            String formData = getsession_ValidateKyc("A");
            Intent intent = new Intent(AEPS_BBPS_RegistrationActivity.this, WebViewVerify.class);
            intent.putExtra("persons", "pending");
            intent.putExtra("mobileNo", list.get(0).getMobilno());
            intent.putExtra("formData", formData);
            intent.putExtra("documentType", "");
            intent.putExtra("documentID", "");
            startActivity(intent);
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
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setBack_click(this);
        finish();
    }
}

