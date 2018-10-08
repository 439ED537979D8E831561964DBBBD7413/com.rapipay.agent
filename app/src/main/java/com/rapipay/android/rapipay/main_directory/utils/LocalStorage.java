package com.rapipay.android.rapipay.main_directory.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    public static String ROUTESTATE = "login";

    public static LocalStorage localStorage;
    Context context;
    private LocalStorage(Context context){
        this.context = context;
    }

    public static LocalStorage getInstance(Context context){
        if(localStorage == null){
            return new LocalStorage(context);
        }else {
            return localStorage;
        }
    }

    public void setActivityState(String Key, String value){
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Key,value);
        editor.commit();
    }

    public String getActivityState(String Key){
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences",0);
        return preferences.getString(Key,"0");
    }
}
