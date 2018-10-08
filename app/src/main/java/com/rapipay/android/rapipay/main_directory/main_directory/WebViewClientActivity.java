package com.rapipay.android.rapipay.main_directory.main_directory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Base64DataException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.CustomWebViewClient;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class WebViewClientActivity extends BaseCompactActivity implements RequestHandler {
    WebView web;
    String mobileNo, parentId, sessionKey, sessionRefNo, nodeAgent, kycType;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    private long size = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        initialize();
    }

    private void initialize() {
        ImageView hamburger = (ImageView) findViewById(R.id.back_click);
        hamburger.setVisibility(View.GONE);
        mobileNo = getIntent().getStringExtra("mobileNo");
        parentId = getIntent().getStringExtra("parentId");
        sessionKey = getIntent().getStringExtra("sessionKey");
        sessionRefNo = getIntent().getStringExtra("sessionRefNo");
        nodeAgent = getIntent().getStringExtra("nodeAgent");
        if (nodeAgent.equalsIgnoreCase(""))
            kycType = "A";
        else
            kycType = "C";
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
                    progressDialog.dismiss();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
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
                            customDialog(object.getString("responseMessage"));
                        } else if (object.getString("responseCode").equalsIgnoreCase("60147") || object.getString("responseCode").equalsIgnoreCase("60173")) {
                            Intent intent = new Intent(WebViewClientActivity.this, LoginScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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
            Set<String> keys = map.keySet();
            for (String key : keys) {
                System.out.println("Name=" + key);
                System.out.println("Value=" + map.get(key));
                byte[] value = Base64.decode(map.get(key).getBytes("UTF-8"), Base64.DEFAULT);
                data = new String(value, StandardCharsets.UTF_8);
            }
            return data;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getQueryMap(String query) {
        String[] params = query.split("\\?");
        String[] name;
        Map<String, String> map = new HashMap<String, String>();
        if (params.length != 0) {
            name = params[1].split("=");
            if (name.length != 0)
                map.put(name[0], name[1]);
        }
        return map;
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

    public String getsession_ValidateKyc(String tokenId, String orgTxnRef) {
        JSONObject jsonObject = new JSONObject();
        String form = null;
        try {
            jsonObject.put("serviceType", "VALIDATE_KYC_PROCESS");
            jsonObject.put("requestType", "EKYC_CHANNEL");
            jsonObject.put("agentId", parentId);
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("tokenId", tokenId);
            jsonObject.put("txnRef", format.format(date));
            jsonObject.put("orgTxnRef", orgTxnRef);
            if (nodeAgent.equalsIgnoreCase("")) {
                jsonObject.put("nodeAgentId", mobileNo);
                jsonObject.put("sessionRefNo", sessionRefNo);

                form = "<html>\n" +
                        "\t<body>\n" +
                        "\t\t<form name=\"validatekyc\" id=\"validatekyc\" method=\"POST\" action=\"" + WebConfig.EKYC_FORWARD + "\">\n" +
                        "\t\t\t<input name=\"serviceType\" value=\"VALIDATE_KYC_PROCESS\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"requestType\" value=\"EKYC_CHANNEL\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"agentId\" value=\"" + parentId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"typeMobileWeb\" value=\"mobile\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"sessionRefNo\" value=\"" + sessionRefNo + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"nodeAgentId\" value=\"" + mobileNo + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"tokenId\" value=\"" + tokenId + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"txnRef\" value=\"" + format.format(date) + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"orgTxnRef\" value=\"" + orgTxnRef + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"checkSum\" value=\"" + GenerateChecksum.checkSum(sessionKey, jsonObject.toString()) + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input type=\"submit\"/>\n" +
                        "\t\t</form>\n" +
                        "\t\t<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                        "\t\t\t\t\tdocument.getElementById(\"validatekyc\").submit();\n" +
                        "\t\t</script>\n" +
                        "\t</body>\n" +
                        "</html>";
            } else {
                jsonObject.put("nodeAgentId", nodeAgent);
                jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());

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
                        "\t\t\t<input name=\"txnRef\" value=\"" + format.format(date) + "\" type=\"hidden\"/>\n" +
                        "\t\t\t<input name=\"orgTxnRef\" value=\"" + orgTxnRef + "\" type=\"hidden\"/>\n" +
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
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePath;
            Log.e("FileCooserParams => ", filePath.toString());


            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

            return true;

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
        try {
            String file_path = mCameraPhotoPath.replace("file:", "");
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
}
