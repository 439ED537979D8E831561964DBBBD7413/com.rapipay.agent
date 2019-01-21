package com.rapipay.android.agent.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChangeTask extends AsyncTask<String, String, Bitmap> {
    String url;
    Context context;
    public ChangeTask(String url, Context context) {
        this.url = url;
        this.context = context;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            String s = null;
            if (isNetworkAvailable(context)) {
                Bitmap bitmap = getBitmapFromURL(url);
                return bitmap;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL("https://www.gstatic.com/webp/gallery3/1.sm.png");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}