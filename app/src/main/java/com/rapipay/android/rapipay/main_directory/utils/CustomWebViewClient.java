package com.rapipay.android.rapipay.main_directory.utils;

import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;

public class CustomWebViewClient extends WebViewClient {

    public interface NavigatingListener
    {
        boolean onNavigating(WebResourceRequest request);
    }

    private NavigatingListener listener;

    public CustomWebViewClient(NavigatingListener navListener) {
        listener = navListener;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest (WebView view, WebResourceRequest request) {
        try {
            if(listener.onNavigating(request))
                return new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream("*** REDIRECTING ***".getBytes(StandardCharsets.UTF_8.name())));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //Allow Self signed SSL websites
    @Override
    public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }
}