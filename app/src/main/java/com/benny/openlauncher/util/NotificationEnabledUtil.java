package com.benny.openlauncher.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;

import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.service.NotificationServiceCustom;


public class NotificationEnabledUtil {
    public static boolean isServicePermission(Context context) {
        try {
            String notificationListenerString = Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
            if (notificationListenerString == null || !notificationListenerString.contains(context.getPackageName())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void startService(Context context) {
        String notificationListenerString = Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (notificationListenerString != null && notificationListenerString.contains(context.getPackageName()) && !BaseUtils.isMyServiceRunning(context, NotificationServiceCustom.class.getName())) {
            Intent intent = new Intent(context.getApplicationContext(), NotificationServiceCustom.class);
            intent.setAction("android.settings.NOTIFICATION_LISTENER_SETTINGS");
            context.getApplicationContext().startService(intent);
        }
    }

    @SuppressLint("WrongConstant")
    public static void requestPermission(Context context) {
        try {
            Intent requestIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            requestIntent.addFlags(268435456);
            context.getApplicationContext().startActivity(requestIntent);
        } catch (Exception e) {
        }
    }
}
