package com.benny.openlauncher.service;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.core.MainApplication;
import com.benny.openlauncher.core.model.AppNotifyItem;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.widget.AppItemView;
import com.benny.openlauncher.core.widget.CellContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThreadUpdateNotify extends Thread {
    private static HashMap<String, AppNotifyItem> hashMap;
    public static ThreadUpdateNotify threadUpdateNotify;
    private static long timeNote;
    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ArrayList<AppItemView> listUpdate = new ArrayList();

    public static void startThread(Context context) {
        if (threadUpdateNotify == null) {
            if (hashMap == null) {
                hashMap = ((MainApplication) context.getApplicationContext()).getMapNumberNotify();
            }
            threadUpdateNotify = new ThreadUpdateNotify(context);
            threadUpdateNotify.start();
        }
        timeNote = System.currentTimeMillis();
    }

    public static synchronized void putApp(Context context, String packageName, AppNotifyItem appNotifyItem) {
        synchronized (ThreadUpdateNotify.class) {
            synchronized (ThreadUpdateNotify.class) {
                if (hashMap == null) {
                    hashMap = ((MainApplication) context.getApplicationContext()).getMapNumberNotify();
                }
                hashMap.put(packageName, appNotifyItem);
                timeNote = System.currentTimeMillis();
            }
        }
    }

    public ThreadUpdateNotify(Context context) {
        threadUpdateNotify = this;
        this.context = context;
    }

    public void run() {
        int i;
        try {
            timeNote = System.currentTimeMillis();
            while (System.currentTimeMillis() - timeNote < 800) {
                Thread.sleep(200);
                if (Math.abs(System.currentTimeMillis() - timeNote) > 300000) {
                    hashMap = null;
                    threadUpdateNotify = null;
                    return;
                }
            }
            if (VERSION.SDK_INT >= 19 && NotificationServiceCustom.myService != null) {
                ArrayList<String> listAppDockRemove = NotificationServiceCustom.getListDockRemove();
                for (i = 0; i < listAppDockRemove.size(); i++) {
                    if (hashMap.containsKey(listAppDockRemove.get(i))) {
                        hashMap.remove(listAppDockRemove.get(i));
                    }
                }
            }
            List<CellContainer> pages = Home.launcher.desktop.pages;
            for (i = 0; i < pages.size(); i++) {
                List<View> listViewFullItem = ((CellContainer) pages.get(i)).getAllCells();
                listViewFullItem.addAll(Home.launcher.dock.getAllCells());
                if (listViewFullItem.size() > 0) {
                    for (int k = 0; k < listViewFullItem.size(); k++) {
                        try {
                            AppItemView appItemView = (AppItemView) listViewFullItem.get(k);
                            if (appItemView.getApp() != null) {
                                if (hashMap.containsKey(appItemView.getApp().getPackageName())) {
                                    this.listUpdate.add(appItemView);
                                }
                            } else {
                                List<Item> listItem = appItemView.getItemGroup().getGroupItems();
                                for (int m = 0; m < listItem.size(); m++) {
                                    try {
                                        String packageName = ((Item) listItem.get(m)).getPackageName();
                                        if (!TextUtils.isEmpty(packageName) && hashMap.containsKey(packageName)) {
                                            this.listUpdate.add(appItemView);
                                            break;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    continue;
                }
            }
            Log.e("XXX", this.listUpdate.size() + "    -----");
            this.handler.post(new Runnable() {
                public void run() {
                    for (int i = 0; i < ThreadUpdateNotify.this.listUpdate.size(); i++) {
                        try {
                            AppItemView appItemView = (AppItemView) ThreadUpdateNotify.this.listUpdate.get(i);
                            appItemView.reset();
                            appItemView.load();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e22) {
            e22.printStackTrace();
        }
        try {
            long timeNow = System.currentTimeMillis();
            ArrayList<String> listKey = new ArrayList(hashMap.keySet());
            i = 0;
            while (i < listKey.size()) {
                try {
                    AppNotifyItem appNotifyItem = (AppNotifyItem) hashMap.get(listKey.get(i));
                    if (appNotifyItem.getNumber() == 0 || Math.abs(timeNow - appNotifyItem.getTime()) > 86400000) {
                        hashMap.remove(listKey.get(i));
                        i++;
                    } else {
                        i++;
                    }
                } catch (Exception e3) {
                    hashMap.remove(listKey.get(i));
                }
            }
        } catch (Exception e222) {
            e222.printStackTrace();
            ((MainApplication) this.context.getApplicationContext()).getMapNumberNotify().clear();
        }
        ((MainApplication) this.context.getApplicationContext()).saveMapNumberNotify();
        hashMap = null;
        threadUpdateNotify = null;
    }
}
