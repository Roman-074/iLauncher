package com.benny.openlauncher.ox11;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.util.Tool;


public class OverlayService extends Service {
    static View view;
    static WindowManager windowManager;
    final double hParam = 0.24d;
    final double wParam = 2.0d;

    @SuppressLint("WrongConstant")
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService("window");
        drawPortait();
    }

    public void drawPortait() {
        int overlayType;
        if (VERSION.SDK_INT < 26) {
            overlayType = 2006;
        } else {
            overlayType = 2038;
        }
        view = View.inflate(getApplicationContext(), R.layout.ip_ox11, null);
        LayoutParams params = new LayoutParams((int) (((float) getWidthScreen()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT), -2, overlayType, 296, -3);
        params.gravity = 49;
        windowManager.addView(view, params);
    }

    public void drawLandscape() {
        int overlayType;
        if (VERSION.SDK_INT < 26) {
            overlayType = 2006;
        } else {
            overlayType = 2038;
        }
        view = View.inflate(getApplicationContext(), R.layout.ip_ox11_l, null);
        LayoutParams params = new LayoutParams((int) (0.24d * ((double) getYPPI())), (int) (2.0d * ((double) getXPPI())), overlayType, 8, -3);
        params.gravity = GravityCompat.START;
        windowManager.addView(view, params);
    }

    public static void removeView() {
        if (view != null && windowManager != null) {
            windowManager.removeView(view);
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            removeView();
            drawLandscape();
        } else if (newConfig.orientation == 1) {
            removeView();
            drawPortait();
        }
    }

    public float getXPPI() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        float xdpi = metrics.xdpi;
        if (xdpi < 320.0f) {
            return 320.0f;
        }
        return xdpi;
    }

    public float getYPPI() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        float ydpi = metrics.ydpi;
        if (ydpi < 320.0f) {
            return 320.0f;
        }
        return ydpi;
    }

    private int getWidthScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
