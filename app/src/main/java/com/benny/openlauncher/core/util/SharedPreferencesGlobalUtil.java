package com.benny.openlauncher.core.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

public class SharedPreferencesGlobalUtil {
    private static String encode(String str) throws Exception {
        return Base64.encodeToString(str.getBytes("UTF-8"), 0);
    }

    private static String decode(String base64) throws Exception {
        return new String(Base64.decode(base64, 0), "UTF-8");
    }

    public static String getValue(Context context, String key) {
        String value = context.getSharedPreferences("basesystem_sys", 0).getString(key, null);
        if (value == null) {
            return value;
        }
        try {
            return decode(value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setValue(Context context, String key, String value) {
        Editor editor = context.getSharedPreferences("basesystem_sys", 0).edit();
        if (value != null) {
            try {
                editor.putString(key, encode(value));
            } catch (Exception e) {
                editor.remove(key);
            }
        } else {
            editor.remove(key);
        }
        editor.commit();
    }
}
