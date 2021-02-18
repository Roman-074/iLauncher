package com.benny.openlauncher.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.activity.SettingsActivity;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.core.util.Tool;

import java.util.List;

public class LauncherAction {
    public static ActionDisplayItem[] actionDisplayItems = new ActionDisplayItem[]{new ActionDisplayItem(Action.SetWallpaper, Home.resources.getString(R.string.minibar_1), R.drawable.ic_photo_black_24dp), new ActionDisplayItem(Action.LockScreen, Home.resources.getString(R.string.minibar_2), R.drawable.ic_lock_black_24dp), new ActionDisplayItem(Action.ClearRam, Home.resources.getString(R.string.minibar_3), R.drawable.ic_donut_large_black_24dp), new ActionDisplayItem(Action.DeviceSettings, Home.resources.getString(R.string.minibar_4), R.drawable.ic_settings_applications_black_24dp), new ActionDisplayItem(Action.LauncherSettings, Home.resources.getString(R.string.minibar_5), R.drawable.ic_settings_launcher_black_24dp)};
    private static boolean clearingRam = false;

    public enum Action {
        SetWallpaper,
        LockScreen,
        ClearRam,
        DeviceSettings,
        LauncherSettings,
        VolumeDialog,
        LaunchApp
    }

    public static class ActionDisplayItem {
        public String description;
        public int icon;
        public Action label;

        public ActionDisplayItem(Action label, String description, int icon) {
            this.label = label;
            this.description = description;
            this.icon = icon;
        }
    }

    public static class ActionItem {
        public Action action;
        public Intent extraData;

        public ActionItem(Action action, Intent extraData) {
            this.action = action;
            this.extraData = extraData;
        }
    }

    public enum Theme {
        Dark,
        Light
    }

    public static void RunAction(@Nullable ActionItem actionItem, Context context) {
        if (actionItem != null) {
            switch (actionItem.action) {
                case LaunchApp:
                    Tool.startApp(context, actionItem.extraData);
                    return;
                default:
                    RunAction(actionItem.action, context);
                    return;
            }
        }
    }

    @SuppressLint("WrongConstant")
    public static void RunAction(Action action, final Context context) {
        switch (action) {
            case SetWallpaper:
                context.startActivity(Intent.createChooser(new Intent("android.intent.action.SET_WALLPAPER"), context.getString(R.string.wallpaper_pick)));
                return;
            case LockScreen:
                try {
                    ((DevicePolicyManager) context.getSystemService("device_policy")).lockNow();
                    return;
                } catch (Exception e) {
                    Tool.toast(context, context.getString(R.string.toast_device_admin_required));
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings"));
                    context.startActivity(intent);
                    return;
                }
            case ClearRam:
                if (!clearingRam) {
                    MemoryInfo mi = new MemoryInfo();
                    final ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
                    activityManager.getMemoryInfo(mi);
                    final long pre = mi.availMem / PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED;
                    new AsyncTask<Void, Void, Void>() {
                        protected void onPreExecute() {
                            LauncherAction.clearingRam = true;
                        }

                        protected Void doInBackground(Void[] p1) {
                            List<RunningAppProcessInfo> runningAppProcessInfo = activityManager.getRunningAppProcesses();
                            for (int i = 0; i < runningAppProcessInfo.size(); i++) {
                                activityManager.killBackgroundProcesses(((RunningAppProcessInfo) runningAppProcessInfo.get(i)).pkgList[0]);
                            }
                            System.runFinalization();
                            Runtime.getRuntime().gc();
                            System.gc();
                            return null;
                        }

                        protected void onPostExecute(Void result) {
                            LauncherAction.clearingRam = false;
                            MemoryInfo mi = new MemoryInfo();
                            activityManager.getMemoryInfo(mi);
                            if ((mi.availMem / PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) - pre > 10) {
                                Tool.toast(context, context.getResources().getString(R.string.toast_free_ram, new Object[]{Long.valueOf(0), Long.valueOf(0 - pre)}));
                            } else {
                                Tool.toast(context, context.getResources().getString(R.string.toast_free_all_ram, new Object[]{Long.valueOf(0)}));
                            }
                            super.onPostExecute(result);
                        }
                    }.execute(new Void[0]);
                    return;
                }
                return;
            case DeviceSettings:
                context.startActivity(new Intent("android.settings.SETTINGS"));
                return;
            case LauncherSettings:
                context.startActivity(new Intent(context, SettingsActivity.class));
                try {
                    ((BaseApplication) context.getApplicationContext()).putEvents(BaseApplication.EVENTS_NAME_CLICK_ICON_SETTINGS);
                    return;
                } catch (Exception e2) {
                    return;
                }
            case VolumeDialog:
                AudioManager audioManager;
                if (VERSION.SDK_INT >= 23) {
                    try {
                        audioManager = (AudioManager) context.getSystemService(Tool.BASE_TYPE_AUDIO);
                        audioManager.setStreamVolume(2, audioManager.getStreamVolume(2), 1);
                        return;
                    } catch (Exception e3) {
                        if (!((NotificationManager) context.getSystemService(Tool.TABLE_NAME)).isNotificationPolicyAccessGranted()) {
                            context.startActivity(new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"));
                            return;
                        }
                        return;
                    }
                }
                audioManager = (AudioManager) context.getSystemService(Tool.BASE_TYPE_AUDIO);
                audioManager.setStreamVolume(2, audioManager.getStreamVolume(2), 1);
                return;
            default:
                return;
        }
    }

    public static ActionDisplayItem getActionItemFromString(String string) {
        for (ActionDisplayItem item : actionDisplayItems) {
            if (item.label.toString().equals(string)) {
                return item;
            }
        }
        return null;
    }

    public static ActionItem getActionItem(int position) {
        return new ActionItem(Action.values()[position], null);
    }

    public static int getActionItemIndex(ActionItem item) {
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < Action.values().length; i++) {
            if (item.action == Action.values()[i]) {
                return i;
            }
        }
        return -1;
    }
}
