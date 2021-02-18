package com.benny.openlauncher.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.MainApplication;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.model.AppNotifyItem;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiresApi(19)
public class NotificationServiceCustom extends NotificationListenerService {
    private static ArrayList<String> listAppDock;
    public static NotificationServiceCustom myService;
    private static long timeGetDock;
    private final IBinder binder = new ServiceBinder();
    private boolean isBound = false;

    public class ServiceBinder extends Binder {
        NotificationServiceCustom getService() {
            return NotificationServiceCustom.this;
        }
    }

    public IBinder onBind(Intent intent) {
        this.isBound = true;
        if ("android.service.notification.NotificationListenerService".equals(intent.getAction())) {
            return super.onBind(intent);
        }
        return this.binder;
    }

    public void onCreate() {
        super.onCreate();
        myService = this;
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startid) {
        myService = this;
        return 1;
    }

    public void onDestroy() {
        myService = null;
        super.onDestroy();
    }

    public boolean isBound() {
        return this.isBound;
    }

    @RequiresApi(19)
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            ChatHeadService.chatHeadService.addNotification(sbn);
        } catch (Exception e) {
            Log.e("error onNotificationPosted: " + e.getMessage());
        }
        myService = this;
        ArrayList<String> listPackage = new ArrayList();
        listPackage.add("com.android.contacts");
        listPackage.add("com.android.mms");
        listPackage.add("com.google.android.gm");
        listPackage.addAll(getListAppDock());
        String packageName = sbn.getPackageName();
        if (packageName.equals("com.android.phone") || packageName.equals("com.android.server.telecom")) {
            packageName = "com.android.contacts";
        }
        if (listPackage.contains(packageName)) {
            try {
                String strTop = sbn.getNotification().extras.getCharSequence(NotificationCompat.EXTRA_TITLE).toString().trim();
                String strBottom = sbn.getNotification().extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString().trim();
                long timeNow = System.currentTimeMillis();
                int number = 1;
                try {
                    int tempNumber;
                    AppNotifyItem appNotifyItem;
                    String strNeed = strTop.split(" ")[0];
                    if (strNeed.startsWith("0") || strNeed.startsWith("+")) {
                        number = 1;
                        if (1 == 1) {
                            try {
                                strNeed = strBottom.split(" ")[0];
                                if (!strNeed.startsWith("0") || strNeed.startsWith("+")) {
                                    number = 1;
                                } else {
                                    tempNumber = Integer.valueOf(strNeed).intValue();
                                    if (tempNumber > 99) {
                                        tempNumber = 1;
                                    }
                                    if (tempNumber > 1) {
                                        number = tempNumber;
                                    }
                                }
                            } catch (Exception e2) {
                                try {
                                    if (timeNow - ((AppNotifyItem) ((MainApplication) getApplicationContext()).getMapNumberNotify().get(packageName)).getTime() < Tool.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                                        return;
                                    }
                                } catch (Exception e3) {
                                }
                            }
                        }
                        if (number > 0) {
                            appNotifyItem = new AppNotifyItem();
                            appNotifyItem.setNumber(number);
                            appNotifyItem.setTime(timeNow);
                            ThreadUpdateNotify.startThread(getApplicationContext());
                            ThreadUpdateNotify.putApp(getApplicationContext(), packageName, appNotifyItem);
                        }
                    }
                    tempNumber = Integer.valueOf(strNeed).intValue();
                    if (tempNumber > 99) {
                        tempNumber = 1;
                    }
                    if (tempNumber > 1) {
                        number = tempNumber;
                    }
                    if (number == 1) {
                        number = strBottom.split(" ")[0].startsWith("0") ? 1 : 1;
                    }
                    if (number > 0) {
                        appNotifyItem = new AppNotifyItem();
                        appNotifyItem.setNumber(number);
                        appNotifyItem.setTime(timeNow);
                        ThreadUpdateNotify.startThread(getApplicationContext());
                        ThreadUpdateNotify.putApp(getApplicationContext(), packageName, appNotifyItem);
                    }
                } catch (Exception e4) {
                }
            } catch (Exception e5) {
                e5.printStackTrace();
            }
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        try {
            ChatHeadService.chatHeadService.removeNotification(sbn);
        } catch (Exception e) {
            Log.e("error onNotificationRemoved: " + e.getMessage());
        }
        myService = this;
        ArrayList<String> listPackage = new ArrayList();
        listPackage.add("com.android.contacts");
        listPackage.add("com.android.mms");
        listPackage.add("com.google.android.gm");
        listPackage.addAll(getListAppDock());
        String packageName = sbn.getPackageName();
        if (packageName.equals("com.android.phone") || packageName.equals("com.android.server.telecom")) {
            packageName = "com.android.contacts";
        }
        if (listPackage.contains(packageName)) {
            HashMap<String, AppNotifyItem> hashMap = ((MainApplication) getApplicationContext()).getMapNumberNotify();
            if (hashMap.containsKey(packageName)) {
                AppNotifyItem temp = (AppNotifyItem) hashMap.get(packageName);
                if (temp != null && System.currentTimeMillis() - temp.getTime() > Tool.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                    AppNotifyItem appNotifyItem = new AppNotifyItem();
                    appNotifyItem.setNumber(0);
                    appNotifyItem.setTime(System.currentTimeMillis());
                    ThreadUpdateNotify.startThread(getApplicationContext());
                    ThreadUpdateNotify.putApp(getApplicationContext(), packageName, appNotifyItem);
                }
            }
        }
    }

    private static ArrayList<String> getListAppDock() {
        try {
            if (listAppDock == null || listAppDock.size() == 0 || System.currentTimeMillis() - timeGetDock > 300000) {
                try {
                    listAppDock = new ArrayList();
                    List<Item> listItem = Home.db.getDock();
                    for (int i = 0; i < listItem.size(); i++) {
                        String pg = ((Item) listItem.get(i)).getPackageName();
                        if (!TextUtils.isEmpty(pg)) {
                            listAppDock.add(pg);
                        }
                    }
                } catch (Exception e) {
                }
                timeGetDock = System.currentTimeMillis();
            }
        } catch (Exception e2) {
        }
        if (listAppDock == null) {
            listAppDock = new ArrayList();
        }
        return listAppDock;
    }

    private static ArrayList<String> getListNotify() {
        ArrayList<String> listApp = new ArrayList();
        try {
            if (myService != null) {
                for (StatusBarNotification sbn : myService.getActiveNotifications()) {
                    String packageName = sbn.getPackageName();
                    if (packageName.equals("com.android.phone") || packageName.equals("com.android.server.telecom")) {
                        packageName = "com.android.contacts";
                    }
                    if (!listApp.contains(packageName)) {
                        listApp.add(packageName);
                    }
                }
            }
        } catch (Exception e) {
        }
        return listApp;
    }

    public static ArrayList<String> getListDockRemove() {
        ArrayList<String> listAppDockTemp = new ArrayList();
        try {
            listAppDockTemp.addAll(getListAppDock());
            ArrayList<String> listAppNotify = getListNotify();
            int i = 0;
            while (i < listAppDockTemp.size()) {
                if (listAppNotify.contains(listAppDockTemp.get(i))) {
                    listAppDockTemp.remove(i);
                    i--;
                }
                i++;
            }
        } catch (Exception e) {
        }
        return listAppDockTemp;
    }
}
