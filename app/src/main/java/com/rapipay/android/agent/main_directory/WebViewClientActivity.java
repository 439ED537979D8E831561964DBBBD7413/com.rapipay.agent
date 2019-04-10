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
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.RegisterUserFragment;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.AsyncPostMethod;
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

public class WebViewClientActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener, CustomInterface {
    WebView web;
    String mobileNo, parentId,DocumentType, PancardDetails, sessionKey, sessionRefNo, nodeAgent, kycType,base64image;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private ValueCallback<Uri[]> mUploadMessage;
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
        new AsyncPostMethod(WebConfig.EKYC, getJson_Validate(mobileNo, kycType, parentId,PancardDetails,DocumentType, sessionKey, sessionRefNo, nodeAgent).toString(), headerData, WebViewClientActivity.this,getString(R.string.responseTimeOut)).execute();
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
                                if (RegisterUserActivity.scan_check == 2) {
                                    Intent intent = new Intent(WebViewClientActivity.this, WalletDetailsActivity.class);
                                    intent.putExtra("mobileNo", "");
                                    intent.putExtra("type", "");
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(WebViewClientActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } else if (TYPE.equalsIgnoreCase("outside")) {
                                Intent intent = new Intent(WebViewClientActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
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
                } else if (object.getString("serviceType").equalsIgnoreCase("VALIDATE_KYC_PROCESS")) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getsession_ValidateKyc(String tokenId, String orgTxnRef) {
        JSONObject jsonObject = new JSONObject();
        String form = null;
        try {
            jsonObject.put("serviceType", "VALIDATE_KYC_PROCESS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("agentId", parentId);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("tokenId", tokenId);
            jsonObject.put("txnRef", ImageUtils.miliSeconds());
            jsonObject.put("orgTxnRef", orgTxnRef);
            jsonObject.put("nodeAgentId", nodeAgent);
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("kycData", base64image.replaceAll("\n", ""));
            if (TYPE.equalsIgnoreCase("outside") || TYPE.equalsIgnoreCase("pending")) {
                form = "<html>\n" +
                        "\t<body>\n" +
                        "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYCFORWARD + "\">\n" +
                        "\t\t\t<input name=\"serviceType\" value=\"VALIDATE_KYC_PROCESS\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"requestType\" value=\"EKYC_CHANNEL\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"agentId\" value=\"" + parentId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"typeMobileWeb\" value=\"mobile\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"sessionRefNo\" value=\"" + list.get(0).getAftersessionRefNo() + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"nodeAgentId\" value=\"" + nodeAgent + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"tokenId\" value=\"" + tokenId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"txnRef\" value=\"" + ImageUtils.miliSeconds() + "\" type=\"hidden\"/>\n" +
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
                        "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYCFORWARD + "\">\n" +
                        "\t\t\t<input name=\"serviceType\" value=\"VALIDATE_KYC_PROCESS\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"requestType\" value=\"EKYC_CHANNEL\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"agentId\" value=\"" + parentId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"typeMobileWeb\" value=\"mobile\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"sessionRefNo\" value=\"" + list.get(0).getAftersessionRefNo() + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"nodeAgentId\" value=\"" + nodeAgent + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"tokenId\" value=\"" + tokenId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"txnRef\" value=\"" + ImageUtils.miliSeconds() + "\" type=\"hidden\"/>\n" +
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
            loadCamera();
            return true;

        }
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

    @Override
    public void chechStat(String object) {

    }

    @Override
    public void onBackPressed() {
        deleteFile();
        Intent intent = null;
        if (TYPE.equalsIgnoreCase("internal")) {
            if (RegisterUserActivity.scan_check == 2) {
                intent = new Intent(WebViewClientActivity.this, WalletDetailsActivity.class);
                intent.putExtra("mobileNo", "");
                intent.putExtra("type", "");
                startActivity(intent);
            } else {
                intent = new Intent(WebViewClientActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else if (TYPE.equalsIgnoreCase("outside")) {
            intent = new Intent(WebViewClientActivity.this, MainActivity.class);
        }
        if (intent != null)
            startActivity(intent);
        finish();
    }

    @Override
    public void okClicked(String type, Object ob) {
        Intent intent = null;
        if (TYPE.equalsIgnoreCase("internal")) {
            if (RegisterUserActivity.scan_check == 2) {
                intent = new Intent(WebViewClientActivity.this, WalletDetailsActivity.class);
                intent.putExtra("mobileNo", mobileNo);
                intent.putExtra("type", "internal");
            } else {
                intent = new Intent(WebViewClientActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        } else if (TYPE.equalsIgnoreCase("outside")) {
            intent = new Intent(WebViewClientActivity.this, MainActivity.class);
        }
        if (intent != null)
            startActivity(intent);
        finish();
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }
}
