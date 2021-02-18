package com.benny.openlauncher.lock;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.benny.openlauncher.base.utils.Log;


public class MyService extends Service {
    public static boolean MyService_IS_RUNNING = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.d("onCreate lock screen");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(ScreenOn.newInstance(), intentFilter);
    }

    public void onDestroy() {
        Log.d("onDestroy lock screen");
        super.onDestroy();
        MyService_IS_RUNNING = false;
        unregisterReceiver(ScreenOn.newInstance());
    }
}
