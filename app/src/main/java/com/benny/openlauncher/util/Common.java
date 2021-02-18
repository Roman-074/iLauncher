package com.benny.openlauncher.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.benny.openlauncher.R;

import java.util.ArrayList;

/**
 * Created by Phí Văn Tuấn on 26/7/2018.
 */

public class Common {
    public static final String EXIT_JSON = "exit_json";
    public static final String SPLASH_JSON = "splash_json";
    public static final String TOKEN = "token";
    public static String accountLink;


    public static Boolean CheckNet(Context context) {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        boolean z = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return Boolean.valueOf(z);
    }

    public static String getPrefString(Context context, String key) {
        if (context != null) {
            return context.getSharedPreferences(context.getString(R.string.app_name), 0).getString(key, "");
        }
        return "";
    }

    public static void setPrefString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.app_name), 0).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean getPrefBoolean(Context context, String key) {
        if (context != null) {
            return context.getSharedPreferences(context.getString(R.string.app_name), 0).getBoolean(key, false);
        }
        return false;
    }

    public static void setPrefBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.app_name), 0).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}
