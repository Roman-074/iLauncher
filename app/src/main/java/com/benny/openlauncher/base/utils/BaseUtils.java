package com.benny.openlauncher.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.Signature;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.TypedValue;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.utils.BaseConstant;
import com.benny.openlauncher.base.utils.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class BaseUtils {
    @SuppressLint("WrongConstant")
    public static boolean isMyServiceRunning(Context context, String serviceName) {
        try {
            for (RunningServiceInfo service : ((ActivityManager) context.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName.equals(service.service.getClassName()) && context.getApplicationContext().getPackageName().equals(service.service.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static String getCountry(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(BaseConstant.KEY_COUNTRY_REQUEST, "VN");
    }

    public static void setCountry(Context context, String country) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.getString(BaseConstant.KEY_COUNTRY_REQUEST, "").equals("")) {
            Editor editor = pref.edit();
            editor.putString(BaseConstant.KEY_COUNTRY_REQUEST, country);
            editor.apply();
        }
    }

    @SuppressLint("WrongConstant")
    public static void setDateInstall(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.getString(BaseConstant.KEY_INSTALL_DATE, null) == null) {
            Editor editor = pref.edit();
            Calendar c = Calendar.getInstance();
            editor.putString(BaseConstant.KEY_INSTALL_DATE, c.get(1) + "-" + standardNumber(c.get(2) + 1) + "-" + standardNumber(c.get(5)));
            editor.apply();
        }
    }

    private static String standardNumber(int number) {
        if (number <= 9) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    public static String getDateInstall(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String date = pref.getString(BaseConstant.KEY_INSTALL_DATE, "");
        if (!date.equals("")) {
            return date;
        }
        setDateInstall(context);
        return pref.getString(BaseConstant.KEY_INSTALL_DATE, "");
    }

    public static String getDeviceID(Context context) {
        return Secure.getString(context.getContentResolver(), "android_id");
    }

    @SuppressLint("WrongConstant")
    public static String getNetwork(Context context) {
        try {
            return ((TelephonyManager) context.getSystemService("phone")).getNetworkOperatorName();
        } catch (Exception e) {
            return "N/A";
        }
    }

    @SuppressLint("WrongConstant")
    public static boolean isInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    public static String printKeyHash(Activity r13) {
        try {
            for (Signature signature : r13.getPackageManager().getPackageInfo(r13.getPackageName(), 64).signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("hashkey: " + new String(Base64.encode(md.digest(), 0)));
            }
        } catch (Exception e) {
            Log.e("error get facebook hash key: " + e.toString());
        }
        return "";
    }

    public static void gotoUrl(Context context, String url) {
        Intent i = new Intent("android.intent.action.VIEW");
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    @SuppressLint("WrongConstant")
    public static void shareText(Context context, String text, String title, String choosen_app) {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("text/plain");
        share.addFlags(524288);
        share.putExtra("android.intent.extra.SUBJECT", title);
        share.putExtra("android.intent.extra.TEXT", text);
        context.startActivity(Intent.createChooser(share, choosen_app));
    }

    public static String convertTimeToString1(Context context, int second) {
        if (second <= 60) {
            return second + " " + context.getString(R.string.time_date_s) + " " + context.getString(R.string.time_date_ago);
        }
        int timeM = second / 60;
        if (timeM <= 60) {
            return timeM + " " + context.getString(R.string.time_date_m) + " " + context.getString(R.string.time_date_ago);
        }
        int timeH = timeM / 60;
        if (timeH <= 24) {
            return timeH + " " + context.getString(R.string.time_date_h) + " " + context.getString(R.string.time_date_ago);
        }
        int timeD = timeH / 24;
        if (timeD <= 7) {
            return timeD + " " + context.getString(R.string.time_date_d) + " " + context.getString(R.string.time_date_ago);
        }
        int timeW = timeD / 7;
        if (timeW < 5) {
            return timeW + " " + context.getString(R.string.time_date_w) + " " + context.getString(R.string.time_date_ago);
        }
        return context.getString(R.string.time_date_sw) + " " + context.getString(R.string.time_date_ago);
    }

    public static String readFileFromAsset(Context context, String fileName) {
        try {
            InputStream stream = context.getAssets().open(fileName);
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            stream.close();
            Log.d("doc file asset: " + fileName);
            return new String(buffer);
        } catch (IOException e) {
            Log.e("error read file asset: " + fileName + " msg: " + e.getMessage());
            return "";
        }
    }

    public static String readTxtFile(File file) {
        try {
            InputStream input = new FileInputStream(file);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();
            Log.d("doc file " + file.getName());
            return new String(buffer);
        } catch (Exception e) {
            Log.e("error read file: " + file.getName() + " msg: " + e.getMessage());
            return "";
        }
    }

    public static boolean writeTxtFile(File file, String fileContents) {
        try {
            FileWriter out = new FileWriter(file);
            out.write(fileContents);
            out.close();
            Log.d("write file " + file.getName());
            return true;
        } catch (IOException e) {
            Log.e("error write file: " + file.getName() + " msg: " + e.getMessage());
            return false;
        }
    }

    public static int genpx(Context context, int dp) {
        int pxTemp = (int) TypedValue.applyDimension(1, (float) dp, context.getResources().getDisplayMetrics());
        return pxTemp != 0 ? pxTemp : dp * 4;
    }

    public static float genpx(Context context, float dp) {
        float pxTemp = TypedValue.applyDimension(1, dp, context.getResources().getDisplayMetrics());
        return pxTemp != 0.0f ? pxTemp : dp * 4.0f;
    }

    public static final String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (byte b : messageDigest) {
                String h = Integer.toHexString(b & 255);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static void gotoStore(Context context) {
        String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void gotoStore(Context context, String packageName) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}
