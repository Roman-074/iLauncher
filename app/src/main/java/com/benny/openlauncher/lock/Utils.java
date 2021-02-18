package com.benny.openlauncher.lock;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Telephony.Sms;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.util.Tool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {
    public static String AsusMSimSmsManager1 = null;
    public static String AsusTelephonyManager1 = null;
    private static final int DAY_MILLIS = 86400000;
    public static String Ext1 = null;
    private static final int HOUR_MILLIS = 3600000;
    public static String HtcTelephonyManager1 = null;
    private static final int MINUTE_MILLIS = 60000;
    private static final int MONTH_MILLIS = -1875767296;
    public static String MSimTelephonyManager1 = null;
    public static String MultiSimSmsManager1 = null;
    public static final int PICK_IMAGE = 12457;
    private static final int SECOND_MILLIS = 1000;
    public static String SubscriptionManager1 = null;
    private static final String TAG = "Check";
    private static final int WEEK_MILLIS = 604800000;
    public static String km1001 = null;
    public static String km501 = null;
    public static String nhamang1 = null;
    public static String simslotcount1 = null;
    public static String tuchoiqc1 = null;

    public static Typeface getTypefaceRobotoThin(Context context) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), Values.ROBOTO_THIN);
        return tf != null ? tf : Typeface.DEFAULT;
    }

    public static Typeface getTypefaceRobotoRegular(Context context) {
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), Values.ROBOTO_REGULAR);
            if (tf != null) {
                return tf;
            }
            return Typeface.DEFAULT;
        } catch (Exception e) {
            return Typeface.DEFAULT;
        }
    }

    public static Typeface getTypefaceRobotoMedium(Context context) {
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), Values.ROBOTO_MEDIUM);
            if (tf != null) {
                return tf;
            }
            return Typeface.DEFAULT;
        } catch (Exception e) {
            return Typeface.DEFAULT;
        }
    }

    public static Typeface getTypefaceRobotoLight(Context context) {
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), Values.ROBOTO_LIGHT);
            if (tf != null) {
                return tf;
            }
            return Typeface.DEFAULT;
        } catch (Exception e) {
            return Typeface.DEFAULT;
        }
    }

    public static boolean decodeNotNull() {
        return (nhamang1 == null || tuchoiqc1 == null || km501 == null || km1001 == null || MultiSimSmsManager1 == null || Ext1 == null || SubscriptionManager1 == null || AsusMSimSmsManager1 == null || AsusTelephonyManager1 == null || MSimTelephonyManager1 == null || HtcTelephonyManager1 == null || simslotcount1 == null) ? false : true;
    }

    public static Cursor getCursorContacts(Context context, String text) {
        StringBuilder stringBuilder = new StringBuilder();
        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, null, "display_name like ? or data1 like ?", new String[]{"%" + text + "%", "%" + text + "%"}, "display_name asc");
        int nameFieldColumnIndex = cursor.getColumnIndex("display_name");
        int numberFieldColumnIndex = cursor.getColumnIndex("datal");
        int id = cursor.getColumnIndex("_id");
        return cursor;
    }

    public static void decode(Context context) {
    }

    @SuppressLint("WrongConstant")
    public static List<RecentTaskInfo> getRecentApp(Context context) {
        if (VERSION.SDK_INT >= 21) {
            return null;
        }
        return ((ActivityManager) context.getSystemService("activity")).getRecentTasks(6, 1);
    }

    @SuppressLint("WrongConstant")
    public static void vibrate(Context context) {
        ((Vibrator) context.getSystemService("vibrator")).vibrate(200);
    }

    @SuppressLint("WrongConstant")
    public static void vibrateTime(Context context, int time) {
        ((Vibrator) context.getSystemService("vibrator")).vibrate((long) time);
    }

    @SuppressLint("WrongConstant")
    public static String getZipcode(Context context) {
        String idCountry = "";
        String zipcode = "";
        return ((TelephonyManager) context.getSystemService("phone")).getSimCountryIso().toUpperCase();
    }

    public static boolean checkTime(int begin_time, int end_time) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("WrongConstant") int timeCurrent = (calendar.get(11) * 60) + calendar.get(12);
        Log.d(TAG, "timeCurrent: " + timeCurrent);
        if (begin_time <= end_time) {
            if (begin_time > timeCurrent || timeCurrent > end_time) {
                return false;
            }
            Log.d(TAG, "timeCurrent: " + timeCurrent);
            return true;
        } else if (begin_time <= timeCurrent && timeCurrent < 1440) {
            return true;
        } else {
            if (timeCurrent <= 0 || timeCurrent > end_time) {
                return false;
            }
            return true;
        }
    }

    public static String longtoStringTime(long time) {
        Date date = new Date(time);
        long now = System.currentTimeMillis();
        if (now < time || time <= 0) {
            return null;
        }
        DateFormat formatter;
        if (now - time < 1471228928) {
            formatter = new SimpleDateFormat(Values.DATE_FORMAT_HH_MM_DD_MM);
        } else {
            formatter = new SimpleDateFormat(Values.DATE_FORMAT_HH_MM_DD_MM_YY);
        }
        return formatter.format(date);
    }

    public static int randomImage(int[] listImage, int position) {
        return listImage[position % listImage.length];
    }

    public static String getEmail(Context context) {
        Account account = getAccount(AccountManager.get(context));
        if (account == null) {
            return null;
        }
        return account.name;
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }

    public static void shareClick(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.TEXT", "http://play.google.com/store/apps/details?id=" + text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    @TargetApi(19)
    public static boolean isDefaultSmsApp(Context context) {
        return context.getPackageName().equals(Sms.getDefaultSmsPackage(context));
    }

    public static void getContacts(Activity activity, int action) {
        Intent pickContactIntent = new Intent("android.intent.action.PICK", Uri.parse("content://contacts"));
        pickContactIntent.setType("vnd.android.cursor.dir/phone_v2");
        activity.startActivityForResult(pickContactIntent, action);
    }

    public static void gotoMarket(Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void gotoMarket(Context context, String url) {
        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
    }

    public static boolean checkHasLink(String s) {
        if (s.contains("http://")) {
            return true;
        }
        return false;
    }

    public static boolean isBrandName(String number) {
        char[] chars = number.toCharArray();
        int i = 0;
        while (i < chars.length) {
            if ((chars[i] >= 'A' && chars[i] <= 'Z') || (chars[i] >= 'a' && chars[i] <= 'z')) {
                return true;
            }
            i++;
        }
        return false;
    }

    public static boolean isHasQCHead(String content) {
        String firtContent = content;
        if (content.length() >= 10) {
            firtContent = content.substring(0, 10);
        }
        int posQ = firtContent.indexOf("Q");
        int posC = firtContent.indexOf("C");
        if (posC <= 0 || posQ <= 0) {
            return false;
        }
        if (posC - (posQ + 2) == 0) {
            char charAtspecial = firtContent.charAt(posQ + 1);
            if (charAtspecial >= 'A' && charAtspecial <= 'Z') {
                return true;
            }
            if (charAtspecial < 'a' || charAtspecial > 'z') {
                return false;
            }
            return true;
        } else if (posC != posQ + 1) {
            return false;
        } else {
            return true;
        }
    }

    public static String getDateInWeek(Context context, int i) {
        return context.getResources().getStringArray(R.array.dayinweek)[i - 1];
    }

    public static String getMonthString(Context context, int i) {
        return context.getResources().getStringArray(R.array.month)[i - 1];
    }

    public static String getMonth(Context context, int i) {
        String[] dayInWeek = context.getResources().getStringArray(R.array.dayinweek);
        switch (i) {
            case 2:
                return dayInWeek[0];
            case 3:
                return dayInWeek[1];
            case 4:
                return dayInWeek[2];
            case 5:
                return dayInWeek[3];
            case 6:
                return dayInWeek[4];
            case 7:
                return dayInWeek[5];
            case 8:
                return dayInWeek[6];
            default:
                return dayInWeek[0];
        }
    }

    @SuppressLint("WrongConstant")
    public static boolean wifiConnected(Context context) {
        if (((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).isConnected()) {
            return true;
        }
        return false;
    }

    public static void getBackgroundEx(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public static void sound(Activity context, final int idSound) {
        @SuppressLint("WrongConstant") AudioManager mAudioManager = (AudioManager) context.getSystemService(Tool.BASE_TYPE_AUDIO);
        context.setVolumeControlStream(3);
        SoundPool soundPool = new SoundPool(20, 3, 0);
        final int soundID = soundPool.load(context, idSound, 1);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (idSound == R.raw.type_keyboard) {
                    soundPool.play(soundID, 0.1f, 0.1f, 1, 0, Tool.DEFAULT_BACKOFF_MULT);
                    return;
                }
                soundPool.play(soundID, 0.5f, 0.5f, 1, 0, Tool.DEFAULT_BACKOFF_MULT);
            }
        });
    }

    public static void shake(Context context, ImageView imageView) {
        imageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake));
    }

    public static void shake(Context context, ImageView imageView1, ImageView imageView2, ImageView imageView3, ImageView imageView4) {
        Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
        imageView1.startAnimation(animShake);
        imageView2.startAnimation(animShake);
        imageView3.startAnimation(animShake);
        imageView4.startAnimation(animShake);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
