package com.benny.openlauncher;

import android.content.Context;
import android.graphics.Bitmap;

import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.MainApplication;

/**
 * Created by Phí Văn Tuấn on 26/7/2018.
 */

public class App extends MainApplication {
    public static Bitmap blur;
    private static App instance;

    public static App get() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        //OneSignal.startInit(this).autoPromptLocation(false).inFocusDisplaying(OSInFocusDisplayOption.Notification).unsubscribeWhenNotificationsAreDisabled(true).filterOtherGCMReceivers(true).init();
    }

    public void onTerminate() {
        super.onTerminate();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    public boolean putEvents(String content) {
        Log.i("log event facebook: " + content);
        return true;
    }

}
