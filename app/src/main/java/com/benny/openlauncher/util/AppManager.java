package com.benny.openlauncher.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build.VERSION;
import android.provider.ContactsContract.Contacts;
import android.provider.Telephony.Sms;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.base.dao.BaseConfig;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.interfaces.AppDeleteListener;
import com.benny.openlauncher.core.interfaces.AppUpdateListener;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.manager.Setup.AppLoader;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.BaseIconProvider;
import com.benny.openlauncher.core.util.Definitions;
import com.benny.openlauncher.core.util.Definitions.ItemPosition;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.util.more.LiveWallPaper;
import com.benny.openlauncher.core.util.more.iRingTone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class AppManager implements AppLoader<AppManager.App> {
    private static AppManager ref;
    private List<App> apps = new ArrayList();
    private Context context;
    public final List<AppDeleteListener<App>> deleteListeners = new ArrayList();
    private PackageManager packageManager;
    public boolean recreateAfterGettingApps;
    private AsyncTask task;
    public final List<AppUpdateListener<App>> updateListeners = new ArrayList();

    public static class App implements com.benny.openlauncher.core.interfaces.App {
        public String className = "";
        private Context context;
        public BaseIconProvider iconProvider;
        public ResolveInfo info;
        public String label = "";
        public String packageName = "";
        private PackageManager pm;

        public App(Context context) {
            this.context = context;
        }

        public App(Context context, ResolveInfo info, PackageManager pm) {
            this.info = info;
            this.pm = pm;
            this.context = context;
        }

        public App(Context context, PackageManager pm, BaseConfig.shortcut_dynamic shortcut_dynamic) {
            this.pm = pm;
            this.context = context;
            this.label = shortcut_dynamic.getName_shotcut();
            this.iconProvider = Setup.imageLoader().createIconProvider(shortcut_dynamic.getIconDrawable());
            this.packageName = shortcut_dynamic.getPackage_name();
            this.className = shortcut_dynamic.getPackage_name();
        }

        public boolean equals(Object o) {
            if (o instanceof App) {
                return this.packageName.equals(((App) o).packageName);
            }
            return false;
        }

        public String getLabel() {
            if (this.label.equals("")) {
                if (getPackageName().equals(this.context.getPackageName())) {
                    this.label = this.context.getString(R.string.app_name_tini);
                } else {
                    this.label = this.info.loadLabel(this.pm).toString();
                }
            }
            return this.label;
        }

        public String getPackageName() {
            if (this.packageName.equals("")) {
                this.packageName = this.info.activityInfo.packageName;
            }
            return this.packageName;
        }

        public String getClassName() {
            if (this.className.equals("")) {
                if (getPackageName().equals(this.context.getPackageName())) {
                    this.className = "com.benny.openlauncher.activity.SettingsActivity";
                } else {
                    this.className = this.info.activityInfo.name;
                }
            }
            return this.className;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public BaseIconProvider getIconProvider() {
//            if (this.iconProvider == null) {
            switch (Definitions.packageNameDefaultApps.indexOf(getPackageName())) {
                case 0:
                    switch (Definitions.packageNameDefaultContacts.indexOf(getClassName())) {
                        case 0:
                            this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_phonecall);
                            break;
                        case 1:
                            this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_contacts);
                            break;
                        default:
                            this.iconProvider = Setup.imageLoader().createIconProvider(this.info.loadIcon(this.pm));
                            break;
                    }
                    break;
                case 1:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_sms);
                    break;
                case 2:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_web);
                    break;
                case 3:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_settings);
                    break;
                case 4:
                    switch (Definitions.packageNameDefaultContacts.indexOf(getClassName())) {
                        case 0:
                            this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_phonecall);
                            break;
                        case 1:
                            this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_contacts);
                            break;
                        default:
                            this.iconProvider = Setup.imageLoader().createIconProvider(this.info.loadIcon(this.pm));
                            break;
                    }
                    break;
                case 5:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_calendar);
                    break;
                case 6:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_camera);
                    break;
                case 7:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_clock);
                    break;
                case 8:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_maps);
                    break;
                case 9:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_store);
                    break;
                case 10:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_calculator);
                    break;
                case 11:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_gallery);
                    break;
                case 12:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_filemanager);
                    break;
                case 13:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_music_player);
                    break;
                case 14:
                    this.iconProvider = Setup.imageLoader().createIconProvider((int) R.drawable.ic_ios_record_audio);
                    break;
                default:
                    this.iconProvider = Setup.imageLoader().createIconProvider(this.info.loadIcon(this.pm));
                    break;
            }
//            }
            return this.iconProvider;

        }
    }

    public static abstract class AppUpdatedListener implements AppUpdateListener<App> {
        private String listenerID = UUID.randomUUID().toString();

        public boolean equals(Object obj) {
            return (obj instanceof AppUpdatedListener) && ((AppUpdatedListener) obj).listenerID.equals(this.listenerID);
        }
    }

    private class AsyncGetApps extends AsyncTask {
        private List<App> tempApps;

        private AsyncGetApps() {
        }

        protected void onPreExecute() {
            this.tempApps = new ArrayList(AppManager.this.apps);
            super.onPreExecute();
        }

        protected void onCancelled() {
            this.tempApps = null;
            super.onCancelled();
        }


        private void addPackageNameDefault() {
            if (Definitions.packageNameDefaultApps.size() == 0) {
                List queryIntentActivities = AppManager.this.packageManager.queryIntentActivities(new Intent("android.intent.action.DIAL", null), 0);
                if (queryIntentActivities.size() > 0) {

                    Definitions.packageNameDefaultContacts.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.name);
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);


                } else {
                    Definitions.packageNameDefaultApps.add("");
                    Definitions.packageNameDefaultContacts.add("");
                }
                Definitions.packageNameDefaultApps.add(AppManager.getDefaultSmsAppPackageName(AppManager.this.context));
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com"));
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                intent = new Intent("android.settings.SETTINGS");
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                intent = new Intent("android.intent.action.VIEW", Contacts.CONTENT_URI);
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {

                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                    Definitions.packageNameDefaultContacts.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.name);


                } else {
                    Definitions.packageNameDefaultApps.add("");
                    Definitions.packageNameDefaultContacts.add("");
                }
                intent = new Intent("android.intent.action.EDIT");
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("title", "Some title");
                intent.putExtra("description", "Some description");
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                intent = new Intent("android.intent.action.SET_ALARM");
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                Definitions.packageNameDefaultApps.add("com.google.android.apps.maps");
                Definitions.packageNameDefaultApps.add("com.android.vending");
                intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.APP_CALCULATOR");
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                intent = new Intent("android.intent.action.VIEW", Uri.parse("content://media/internal/images/media"));
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("file/*");
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                intent = new Intent("android.media.action.MEDIA_PLAY_FROM_SEARCH");
                intent.putExtra("android.intent.extra.focus", "vnd.android.cursor.item/*");
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() <= 0) {
                    Definitions.packageNameDefaultApps.add("");
                } else if (((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName.contains("com.google.android.youtube")) {
                    Definitions.packageNameDefaultApps.add("");
                } else {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                }
                intent = new Intent("android.provider.MediaStore.RECORD_SOUND");
                queryIntentActivities.clear();
                queryIntentActivities.addAll(AppManager.this.packageManager.queryIntentActivities(intent, 0));
                if (queryIntentActivities.size() > 0) {
                    Definitions.packageNameDefaultApps.add(((ResolveInfo) queryIntentActivities.get(0)).activityInfo.packageName);
                } else {
                    Definitions.packageNameDefaultApps.add("");
                }
                Definitions.packageNameDefaultApps.add("com.google.android.youtube");
                Definitions.packageNameDefaultApps.add("com.google.android.gm");
            }
        }

        protected Object doInBackground(Object[] p1) {
            AppManager.this.apps.clear();
            try {
                addPackageNameDefault();
            } catch (Exception e) {
            }
            try {
                Intent intent = new Intent("android.intent.action.MAIN", null);
                intent.addCategory("android.intent.category.LAUNCHER");
                List<ResolveInfo> activitiesInfo = AppManager.this.packageManager.queryIntentActivities(intent, 0);
                Collections.sort(activitiesInfo, new DisplayNameComparator(AppManager.this.packageManager));
                try {
                    BaseApplication application = (BaseApplication) AppManager.this.context.getApplicationContext();
                    if (!application.isPurchase()) {

                        Iterator it = application.getBaseConfig().getShortcut_dynamic().iterator();
                        while (it.hasNext()) {
                            BaseConfig.shortcut_dynamic shortcut_dynamic = (BaseConfig.shortcut_dynamic) it.next();
                            if (!BaseUtils.isInstalled(AppManager.this.context, shortcut_dynamic.getPackage_name())) {
                                AppManager.this.apps.add(0, new App(AppManager.this.context, AppManager.this.packageManager, shortcut_dynamic));
                            }
                        }
                    }
                } catch (Exception e2) {
                }
                int tempPositionAdd = 0;
                for (ResolveInfo info : activitiesInfo) {
                    App app = new App(AppManager.this.context, info, AppManager.this.packageManager);
                    if (((String) Definitions.packageNameDefaultApps.get(10)).equals("") && app.getPackageName().toLowerCase().contains("calculator")) {
                        Definitions.packageNameDefaultApps.set(10, app.getPackageName());
                    }
                    if (Definitions.packageNameDefaultApps.contains(info.activityInfo.packageName)) {
                        AppManager.this.apps.add(tempPositionAdd, app);
                        tempPositionAdd++;
                    } else {
                        AppManager.this.apps.add(app);
                    }
                }
               if (!isInstall(context.getResources().getString(R.string.iRingtone))){
                    apps.add(0,new iRingTone(context));
               }else {
                   apps.set(0,new iRingTone(context));
               }
               if (!isInstall(context.getResources().getString(R.string.livewallpaper))){
                   apps.add(1,new LiveWallPaper(context));
               }else {
                   apps.set(1,new LiveWallPaper(context));
               }

                if (Setup.appSettings().isFirstAddDock()) {
                    for (int i = 0; i < Setup.appSettings().getDockSize(); i++) {
                        for (com.benny.openlauncher.core.interfaces.App app2 : AppManager.this.apps) {
                            if (app2.getPackageName().equals(Definitions.packageNameDefaultApps.get(i))) {
                                if (i != 0) {
                                    Setup.dataManager().saveItem(Item.newAppItem(app2), 0, ItemPosition.Dock, false);
                                    break;
                                } else if (Definitions.packageNameDefaultContacts.size()>0){
                                    if (Definitions.packageNameDefaultContacts.get(0).equals(app2.getClassName())) {
                                        Setup.dataManager().saveItem(Item.newAppItem(app2), 0, ItemPosition.Dock, false);
                                        break;
                                    }else if (Definitions.packageNameDefaultContacts.get(0).equals(context.getResources().getString(R.string.class_google))&&app2.getClassName().equals(context.getResources().getString(R.string.class_google2))){
                                        Setup.dataManager().saveItem(Item.newAppItem(app2), 0, ItemPosition.Dock, false);
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }
                Setup.appSettings().setFirstAddDock();
            } catch (Exception e3) {
            }
            return null;
        }

        protected void onPostExecute(Object result) {
            AppManager.this.notifyUpdateListeners(AppManager.this.apps);
            List<App> removed = Tool.getRemovedApps(this.tempApps, AppManager.this.apps);
            if (removed.size() > 0) {
                AppManager.this.notifyRemoveListeners(removed);
            }
            if (AppManager.this.recreateAfterGettingApps) {
                AppManager.this.recreateAfterGettingApps = false;
                if (AppManager.this.context instanceof Home) {
                    ((Home) AppManager.this.context).recreate();
                }
            }
            for (int i = 0; i < AppManager.this.apps.size(); i++) {
                Log.i("jj", "onPostExecute: " + ((App) AppManager.this.apps.get(i)).getPackageName());
            }
            super.onPostExecute(result);
        }
        private boolean isInstall(String package_name){
            for (int i=0;i<apps.size();i++){
                if (apps.get(i).getPackageName().equals(package_name)){
                    return true;
                }
            }
            return false;
        }
    }

    public AppManager(Context c) {
        this.context = c;
        this.packageManager = c.getPackageManager();
    }

    public static AppManager getInstance(Context context) {
        if (ref != null) {
            return ref;
        }
        AppManager appManager = new AppManager(context);
        ref = appManager;
        return appManager;
    }

    @Nullable
    public static String getDefaultSmsAppPackageName(@NonNull Context context) {
        try {
            if (VERSION.SDK_INT >= 19) {
                return Sms.getDefaultSmsPackage(context);
            }
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW").addCategory("android.intent.category.DEFAULT").setType("vnd.android-dir/mms-sms"), 0);
            if (resolveInfos == null || resolveInfos.isEmpty()) {
                return "";
            }
            return ((ResolveInfo) resolveInfos.get(0)).activityInfo.packageName;
        } catch (Exception e) {
            Log.e("HuyAnh", "error get default packageName sms: " + e.getMessage());
            return "";
        }
    }

    public Context getContext() {
        return this.context;
    }

    public PackageManager getPackageManager() {
        return this.packageManager;
    }

    public App findApp(Intent intent) {
        if (intent == null || intent.getComponent() == null) {
            return null;
        }
        String packageName = intent.getComponent().getPackageName();
        String className = intent.getComponent().getClassName();
        for (App app : this.apps) {
            if (app.getClassName().equals(className) && app.getPackageName().equals(packageName)) {
                return app;
            }

        }
        return null;
    }

    public List<App> getApps() {
        return this.apps;
    }

    public List<App> getNonFilteredApps() {
        return this.apps;
    }

    public void clearListener() {
        this.updateListeners.clear();
        this.deleteListeners.clear();
    }

    public void init() {
        Log.d("HuyAnh", "init AppManager");
        getAllApps();
    }

    private void getAllApps() {
        if (this.task == null || this.task.getStatus() == Status.FINISHED) {
            this.task = new AsyncGetApps().execute(new Object[0]);
        } else if (this.task.getStatus() == Status.RUNNING) {
            this.task.cancel(false);
            this.task = new AsyncGetApps().execute(new Object[0]);
        }
    }

    public void onReceive(Context p1, Intent p2) {
        getAllApps();
    }

    public void loadItems() {
        getAllApps();
    }

    public List<App> getAllApps(Context context) {
        return getApps();
    }

    public App findItemApp(Item item) {
        if (item == null) {
            return null;
        }
        return findApp(item.getIntent());
    }

    public App createApp(Intent intent) {
        try {
            App app = new App(getContext(), this.packageManager.resolveActivity(intent, 0), this.packageManager);
            if (this.apps == null || this.apps.contains(app)) {
                return app;
            }
            this.apps.add(app);
            return app;
        } catch (Exception e) {
            return null;
        }
    }

    public void onAppUpdated(Context p1, Intent p2) {
        onReceive(p1, p2);
    }

    public void notifyUpdateListeners(List<App> apps) {
        Iterator<AppUpdateListener<App>> iter = this.updateListeners.iterator();
        while (iter.hasNext()) {
            if (((AppUpdateListener) iter.next()).onAppUpdated(apps)) {
                iter.remove();
            }
        }
    }

    public void notifyRemoveListeners(List<App> apps) {
        Iterator<AppDeleteListener<App>> iter = this.deleteListeners.iterator();
        while (iter.hasNext()) {
            if (((AppDeleteListener) iter.next()).onAppDeleted(apps)) {
                iter.remove();
            }
        }
    }

    public void addUpdateListener(AppUpdateListener updateListener) {
        this.updateListeners.add(updateListener);
    }

    public void removeUpdateListener(AppUpdateListener updateListener) {
        this.updateListeners.remove(updateListener);
    }

    public void addDeleteListener(AppDeleteListener deleteListener) {
        this.deleteListeners.add(deleteListener);
    }

    public void removeDeleteListener(AppDeleteListener deleteListener) {
        this.deleteListeners.remove(deleteListener);
    }
}
