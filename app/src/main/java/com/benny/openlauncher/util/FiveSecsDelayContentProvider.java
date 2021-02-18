package com.benny.openlauncher.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benny.openlauncher.activity.AutoFinishActivity;
import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.util.Tool;

public class FiveSecsDelayContentProvider extends ContentProvider {
    private static final String PATH_RESET_5SEC_DELAY = "reset5secs";

    public boolean onCreate() {
        return true;
    }

    @Nullable
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Nullable
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Tool.print((Object) "dfshgjsdfdfrghid");
        checkCallingPackage();
        if (!PATH_RESET_5SEC_DELAY.equals(uri.getLastPathSegment())) {
            return 0;
        }
        AutoFinishActivity.start(getContext());
        return 1;
    }

    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Unsupported");
    }

    private void checkCallingPackage() throws SecurityException {
        String callingPkg = null;
        if (VERSION.SDK_INT >= 19) {
            callingPkg = getCallingPackage();
            android.util.Log.e("caaling",callingPkg);
        }
        if (!"org.kustom.wallpaper".equals(callingPkg) && !"org.kustom.widget".equals(callingPkg)) {
            throw new SecurityException("Unauthorized");
        }
    }
}
