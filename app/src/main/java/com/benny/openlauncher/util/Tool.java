package com.benny.openlauncher.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import android.text.format.Formatter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.MainApplication;
import com.benny.openlauncher.util.AppManager.App;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class Tool extends com.benny.openlauncher.core.util.Tool {
    private Tool() {
    }
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    public static String[] split(String string, String delim) {
        String s;
        ArrayList<String> list = new ArrayList();
        char[] charArr = string.toCharArray();
        char[] delimArr = delim.toCharArray();
        int counter = 0;
        int i = 0;
        while (i < charArr.length) {
            int k = 0;
            int j = 0;
            while (j < delimArr.length && charArr[i + j] == delimArr[j]) {
                k++;
                j++;
            }
            if (k == delimArr.length) {
                s = "";
                while (counter < i) {
                    s = s + charArr[counter];
                    counter++;
                }
                i += k;
                counter = i;
                list.add(s);
            }
            i++;
        }
        s = "";
        if (counter < charArr.length) {
            while (counter < charArr.length) {
                s = s + charArr[counter];
                counter++;
            }
            list.add(s);
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static long getContactIDFromNumber(Context context, String contactNumber) {
        String UriContactNumber = Uri.encode(contactNumber);
        long phoneContactID = (long) new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, UriContactNumber), new String[]{"display_name", "_id"}, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getLong(contactLookupCursor.getColumnIndexOrThrow("_id"));
        }
        contactLookupCursor.close();
        return phoneContactID;
    }

    public static int factorColorBrightness(int color, int brightnessFactorPercent) {
        float f = 255.0f;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = (float) (((double) hsv[2]) * (((double) brightnessFactorPercent) / 100.0d));
        if (hsv[2] <= 255.0f) {
            f = hsv[2];
        }
        hsv[2] = f;
        return Color.HSVToColor(hsv);
    }

    public static Integer fetchThumbnailId(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"photo_id"}, null, null, "display_name ASC");
        Integer thumbnailId = null;
        try {
            if (cursor.moveToFirst()) {
                thumbnailId = Integer.valueOf(cursor.getInt(cursor.getColumnIndex("photo_id")));
            }
            cursor.close();
            return thumbnailId;
        } catch (Throwable th) {
            cursor.close();
            return null;
        }
    }

    public static Bitmap fetchThumbnail(Context context, String phoneNumber) {
        com.benny.openlauncher.core.util.Tool.print((Object) phoneNumber);
        Integer thumbnailId = fetchThumbnailId(context, phoneNumber);
        if (thumbnailId == null) {
            return null;
        }
        Uri uri = ContentUris.withAppendedId(Data.CONTENT_URI, (long) thumbnailId.intValue());
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"data15"}, null, null, null);
        Bitmap thumbnail = null;
        try {
            if (cursor.moveToFirst()) {
                byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                }
            }
            cursor.close();
            return thumbnail;
        } catch (Throwable th) {
            cursor.close();
            return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */

    public static Bitmap openPhoto(Context context2, String str2) {
        com.benny.openlauncher.core.util.Tool.print((Object) str2);
        Bitmap str=null;
        Uri withAppendedPath = Uri.withAppendedPath(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, getContactIDFromNumber(context2, str2)), Tool.PARAMETER_SHARE_DIALOG_CONTENT_PHOTO);
        Cursor context = context2.getContentResolver().query(withAppendedPath, new String[]{"data15"}, null, null, null);
        if (context == null) {
            return null;
        }
        try {
            if (context.moveToFirst()) {
                byte[] str1 = context.getBlob(0);
                if (str != null) {
                    str = BitmapFactory.decodeStream(new ByteArrayInputStream(str1));
                    context.close();
                    return str;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            context.close();
        }
        context.close();
        return null;
    }


    @SuppressLint("WrongConstant")
    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 1);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    public static void startApp(Context context, App app) {
        try {
            if (BaseUtils.isInstalled(context, app.getPackageName())) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setFlags(268435456);
                intent.setClassName(app.getPackageName(), app.getClassName());
                context.startActivity(intent);
                if (app.getPackageName().equals(context.getPackageName())) {
                    try {
                        ((BaseApplication) context.getApplicationContext()).putEvents(BaseApplication.EVENTS_NAME_CLICK_ICON_SETTINGS);
                    } catch (Exception e) {
                    }
                }
            } else {
                if (Home.launcher != null) {
                    Home.launcher.resetFirstShowPopup();
                }
                BaseUtils.gotoStore(context, app.getPackageName());
            }
            Home.consumeNextResume = true;
            ((MainApplication) context.getApplicationContext()).dbHelper.addRecent(app.getPackageName());
        } catch (Exception e2) {
            com.benny.openlauncher.core.util.Tool.toast(context, (int) R.string.toast_app_uninstalled);
        }
    }

    public static void startApp(Context context, Intent intent) {
        try {
            if (BaseUtils.isInstalled(context, intent.getComponent().getPackageName())) {
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(intent.getComponent().getPackageName());
                    if (launchIntent != null) {
                        context.startActivity(launchIntent);
                    }
                }
                if (intent.getComponent().getPackageName().equals(context.getPackageName())) {
                    try {
                        ((BaseApplication) context.getApplicationContext()).putEvents(BaseApplication.EVENTS_NAME_CLICK_ICON_SETTINGS);
                    } catch (Exception e2) {
                    }
                }
            } else {
                if (Home.launcher != null) {
                    Home.launcher.resetFirstShowPopup();
                }
                BaseUtils.gotoStore(context, intent.getComponent().getPackageName());
            }
            Home.consumeNextResume = true;
            ((MainApplication) context.getApplicationContext()).dbHelper.addRecent(intent.getComponent().getPackageName());
        } catch (Exception e3) {
            com.benny.openlauncher.core.util.Tool.toast(context, (int) R.string.toast_app_uninstalled);
        }
    }

    @SuppressLint("WrongConstant")
    public static Intent getStartAppIntent(App app) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(268435456);
        intent.setClassName(app.getPackageName(), app.getClassName());
        return intent;
    }

    public static OnTouchListener getBtnColorMaskController() {
        return new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        v.animate().scaleY(1.1f).scaleX(1.1f).setDuration(50);
                        ((TextView) v).setTextColor(Color.rgb(Callback.DEFAULT_DRAG_ANIMATION_DURATION, Callback.DEFAULT_DRAG_ANIMATION_DURATION, Callback.DEFAULT_DRAG_ANIMATION_DURATION));
                        break;
                    case 1:
                        v.animate().scaleY(Tool.DEFAULT_BACKOFF_MULT).scaleX(Tool.DEFAULT_BACKOFF_MULT).setDuration(50);
                        ((TextView) v).setTextColor(-1);
                        break;
                }
                return false;
            }
        };
    }

    public static void writeToFile(String name, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name, 0));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
        }
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                sb.append(line).append("\n");
            } else {
                reader.close();
                return sb.toString();
            }
        }
    }

    public static String getStringFromFile(String name, Context context) {
        try {
            FileInputStream fin = context.openFileInput(name);
            String ret = convertStreamToString(fin);
            fin.close();
            return ret;
        } catch (Exception e) {
            return null;
        }
    }

    public static String wrapColorTag(String str, @ColorInt int color) {
        return "<font color='" + String.format("#%06X", new Object[]{Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK & color)}) + "'>" + str + "</font>";
    }

    @SuppressLint({"DefaultLocale", "WrongConstant"})
    public static String getRAM_Info(Context context) {
        ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(new MemoryInfo());
        MemoryInfo memInfo = new MemoryInfo();
        return String.format("<big><big><b>%s</b></big></big><br\\>%s / %s", new Object[]{context.getString(R.string.memory), Formatter.formatFileSize(context, memInfo.availMem), Formatter.formatFileSize(context, memInfo.totalMem)});
    }

    @SuppressLint({"DefaultLocale"})
    public static String getStorage_Info(Context context) {
        File externalFilesDir = Environment.getExternalStorageDirectory();
        if (externalFilesDir == null) {
            return "?";
        }
        StatFs stat = new StatFs(externalFilesDir.getPath());
        long blockSize = (long) stat.getBlockSize();
        String str = "<big><big><b>%s</b></big></big><br\\>%s / %s";
        Object[] objArr = new Object[3];
        objArr[0] = context.getString(R.string.storage);
        objArr[1] = Formatter.formatFileSize(context, ((long) stat.getAvailableBlocks()) * blockSize);
        objArr[2] = Formatter.formatFileSize(context, (VERSION.SDK_INT >= 18 ? stat.getBlockCountLong() : (long) stat.getBlockCount()) * blockSize);
        return String.format(str, objArr);
    }

    @DrawableRes
    public static int getOL_LauncherIcon() {
        return R.drawable.ic_launcher;
    }

    public static void copy(Context context, String stringIn, String stringOut) {
        try {
            new File(stringOut).delete();
            new File(stringOut).delete();
            new File(stringOut).delete();
            com.benny.openlauncher.core.util.Tool.print((Object) "deleted");
            FileInputStream in = new FileInputStream(stringIn);
            FileOutputStream out = new FileOutputStream(stringOut);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = in.read(buffer);
                if (read != -1) {
                    out.write(buffer, 0, read);
                } else {
                    in.close();
                    out.flush();
                    out.close();
                    com.benny.openlauncher.core.util.Tool.print((Object) "copied");
                    return;
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.dialog__backup_app_settings__error, Toast.LENGTH_SHORT).show();
        }
    }
}
