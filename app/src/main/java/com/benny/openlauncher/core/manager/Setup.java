package com.benny.openlauncher.core.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.AppDeleteListener;
import com.benny.openlauncher.core.interfaces.AppUpdateListener;
import com.benny.openlauncher.core.interfaces.DialogListener.OnAddAppDrawerItemListener;
import com.benny.openlauncher.core.interfaces.DialogListener.OnEditDialogListener;
import com.benny.openlauncher.core.interfaces.SettingsManager;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.BaseIconProvider;
import com.benny.openlauncher.core.util.Definitions.ItemPosition;
import com.benny.openlauncher.core.util.Definitions.ItemState;
import com.benny.openlauncher.core.viewutil.DesktopGestureListener.DesktopGestureCallback;
import com.benny.openlauncher.core.viewutil.ItemGestureListener.ItemGestureCallback;
import java.util.List;

public abstract class Setup<A extends App> {
    private static Setup setup = null;

    public interface ImageLoader<A extends App> {
        BaseIconProvider createIconProvider(int i);

        BaseIconProvider createIconProvider(Drawable drawable);

        BaseIconProvider createIconProvider(String str);
    }

    public interface EventHandler {
        void showDeletePackageDialog(Context context, DragEvent dragEvent);

        void showEditDialog(Context context, Item item, OnEditDialogListener onEditDialogListener);

        void showLauncherSettings(Context context);

        void showPickAction(Context context, OnAddAppDrawerItemListener onAddAppDrawerItemListener);
    }

    public interface Logger {
        void log(Object obj, int i, String str, String str2, Object... objArr);
    }

    public interface AppLoader<A extends App> {
        void addDeleteListener(AppDeleteListener<A> appDeleteListener);

        void addUpdateListener(AppUpdateListener<A> appUpdateListener);

        A createApp(Intent intent);

        A findItemApp(Item item);

        List<A> getAllApps(Context context);

        void loadItems();

        void notifyRemoveListeners(List<A> list);

        void notifyUpdateListeners(List<A> list);

        void onAppUpdated(Context context, Intent intent);

        void removeDeleteListener(AppDeleteListener<A> appDeleteListener);

        void removeUpdateListener(AppUpdateListener<A> appUpdateListener);
    }

    public interface DataManager {
        void deleteItem(Item item, boolean z);

        List<List<Item>> getDesktop();

        List<Item> getDock();

        List<Item> getGroup();

        int getId(Intent intent);

        int getIdGroup(String str);

        Item getItem(int i);

        Item getItem(Intent intent);

        int getMaxPage();

        int getPage(int i);

        boolean isDesktop(int i);

        boolean isShowDesktop(int i);

        void saveItem(Item item);

        void saveItem(Item item, int i, ItemPosition itemPosition);

        boolean saveItem(Item item, int i, ItemPosition itemPosition, boolean z);

        void updateSate(Item item, ItemState itemState);
    }

    public abstract Context getAppContext();

    public abstract AppLoader<A> getAppLoader();

    public abstract SettingsManager getAppSettings();

    public abstract DataManager getDataManager();

    public abstract DesktopGestureCallback getDesktopGestureCallback();

    public abstract EventHandler getEventHandler();

    public abstract ImageLoader<A> getImageLoader();

    public abstract ItemGestureCallback getItemGestureCallback();

    public abstract Logger getLogger();

    public static boolean wasInitialised() {
        return setup != null;
    }

    public static void init(Setup setup2) {
        setup = setup2;
    }

    public static Setup get() {
        return setup;
    }

    public static Context appContext() {
        return get().getAppContext();
    }

    public static SettingsManager appSettings() {
        if (get() == null) {
            return null;
        }
        return get().getAppSettings();
    }

    public static DesktopGestureCallback desktopGestureCallback() {
        return get().getDesktopGestureCallback();
    }

    public static ItemGestureCallback itemGestureCallback() {
        return get().getItemGestureCallback();
    }

    public static <IL extends ImageLoader<A>, A extends App> IL imageLoader() {
        return (IL) get().getImageLoader();
    }

    public static DataManager dataManager() {
        return get().getDataManager();
    }

    public static <AL extends AppLoader<A>, A extends App> AL appLoader() {
        return (AL) get().getAppLoader();
    }

    public static EventHandler eventHandler() {
        return get().getEventHandler();
    }

    public static Logger logger() {
        return get().getLogger();
    }
}
