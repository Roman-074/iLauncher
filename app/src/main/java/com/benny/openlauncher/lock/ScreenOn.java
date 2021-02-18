package com.benny.openlauncher.lock;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import com.benny.openlauncher.core.manager.Setup;

public class ScreenOn extends BroadcastReceiver {
    public static final String TAG = "ScreenOn";
    public static volatile ScreenOn screenOn;
    private Intent intent1;

    public static ScreenOn newInstance() {
        if (screenOn == null) {
            screenOn = new ScreenOn();
        }
        return screenOn;
    }

    @SuppressLint("WrongConstant")
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.intent.action.SCREEN_OFF") || intent.getAction().equalsIgnoreCase("android.intent.action.SCREEN_ON")) {
            Log.w("HuyAnh", "BroadcastReceiver on/off screen onReceive: " + intent.getAction());
            if (!Setup.appSettings().isLockScreenEnable()) {
                Log.d("HuyAnh", "4");
            } else if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Values.ENABLE_PASSCODE, false)) {
                if (LockHasPasscode.lockHasPasscode == null) {
                    Log.d("HuyAnh", "LockHasPasscode");
                    this.intent1 = new Intent(context, LockHasPasscode.class);
                    this.intent1.setFlags(268435456);
                    context.startActivity(this.intent1);
                    return;
                }
                LockHasPasscode.lockHasPasscode.updateTime();
            } else if (LockNoPasscode.lockNoPasscode == null) {
                Log.d("HuyAnh", LockNoPasscode.TAG);
                this.intent1 = new Intent(context, LockNoPasscode.class);
                this.intent1.setFlags(268435456);
                context.startActivity(this.intent1);
            } else {
                LockNoPasscode.lockNoPasscode.updateTime();
            }
        }
    }
}
