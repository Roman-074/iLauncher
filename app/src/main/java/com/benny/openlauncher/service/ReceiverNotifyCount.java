package com.benny.openlauncher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.benny.openlauncher.core.model.AppNotifyItem;

public class ReceiverNotifyCount extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        int count = intent.getIntExtra("badge_count", 0);
        String packageName = intent.getStringExtra("badge_count_package_name");
        Log.e("XXX", packageName + "    " + count);
        ThreadUpdateNotify.startThread(context);
        AppNotifyItem appNotifyItem = new AppNotifyItem();
        appNotifyItem.setNumber(count);
        appNotifyItem.setTime(System.currentTimeMillis());
        ThreadUpdateNotify.putApp(context, packageName, appNotifyItem);
    }
}
