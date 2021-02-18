package com.benny.openlauncher.viewutil;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.InputCallback;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.base.dao.BaseConfig;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.IconLabelItem;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.model.Item.Type;
import com.benny.openlauncher.core.util.DragDropHandler;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.util.AppManager;
import com.benny.openlauncher.util.AppManager.App;
import com.benny.openlauncher.util.DatabaseHelper;
import com.benny.openlauncher.util.LauncherAction;
import com.benny.openlauncher.util.LauncherAction.Action;
import com.benny.openlauncher.util.LauncherAction.ActionItem;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.qiujuer.genius.blur.StackBlur;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DialogHelper {

    public interface onItemEditListener {
        void itemLabel(String str);
    }

    public interface OnAppSelectedListener {
        void onAppSelected(App app);
    }

    public interface OnActionSelectedListener {
        void onActionSelected(ActionItem actionItem);
    }

    public static void editItemDialog(String title, Item item, Context c, final onItemEditListener listener) {
        if (item.getType() == Type.APP) {
            try {
                createDialogEdit(title, c, listener, item);
            } catch (Exception e) {
            }
        } else if (item.getType() == Type.GROUP) {
            new Builder(c).title((CharSequence) title).positiveText((int) R.string.ok).negativeText((int) R.string.cancel).input(null, item.getLabel(), new InputCallback() {
                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    listener.itemLabel(input.toString());
                }
            }).show();
        }
    }

    private static void createDialogEdit(String title, final Context context, final onItemEditListener listener, final Item item) {
        final MaterialDialog dialog = new Builder(context).title((CharSequence) title).customView((int) R.layout.view_dialog_edit_constant_icon, true).positiveText((int) R.string.ok).onPositive(new SingleButtonCallback() {
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                try {
                    listener.itemLabel(((EditText) dialog.getCustomView().findViewById(R.id.dialog_edit_icon_et)).getText().toString());
                } catch (Exception e) {
                }
            }
        }).negativeText((int) R.string.cancel).show();
        View alertLayout = dialog.getCustomView();
        if (alertLayout != null) {
            ((EditText) alertLayout.findViewById(R.id.dialog_edit_icon_et)).setText(item.getLabel());
            if (item.getIconProvider() != null) {
                ImageView ivIcon = (ImageView) alertLayout.findViewById(R.id.dialog_edit_icon_iv);
                ivIcon.setImageDrawable(item.getIconProvider().getDrawableSynchronously((int) context.getResources().getDimension(R.dimen.dialog_edit_icon_iv_size)));
                ivIcon.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (context instanceof Home) {
                            dialog.cancel();
                            ((Home) context).showSelectImgDialog(item);
                        }
                    }
                });
                alertLayout.findViewById(R.id.dialog_edit_icon_tvChange).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (context instanceof Home) {
                            dialog.cancel();
                            ((Home) context).showSelectImgDialog(item);
                        }
                    }
                });
            }
        }
    }

    public static void alertDialog(Context context, String title, String msg) {
        new Builder(context).title((CharSequence) title).content((CharSequence) msg).negativeText((int) R.string.cancel).positiveText((int) R.string.ok).show();
    }

    public static void alertDialog(Context context, String title, String msg, SingleButtonCallback onPositive) {
        new Builder(context).title((CharSequence) title).onPositive(onPositive).content((CharSequence) msg).negativeText((int) R.string.cancel).positiveText((int) R.string.ok).show();
    }

    public static void addActionItemDialog(Context context, ListCallback callback) {
        new Builder(context).title((int) R.string.desktop_action).items((int) R.array.entries__desktop_actions).itemsCallback(callback).show();
    }

    public static void selectAppDialog(Context context, final OnAppSelectedListener onAppSelectedListener) {
        Builder builder = new Builder(context);
        FastItemAdapter<IconLabelItem> fastItemAdapter = new FastItemAdapter();
        builder.title((int) R.string.select_app).adapter(fastItemAdapter, new LinearLayoutManager(context, 1, false)).negativeText((int) R.string.cancel);
        final MaterialDialog dialog = builder.build();
        List<IconLabelItem> items = new ArrayList();
        final List<App> apps = AppManager.getInstance(context).getApps();
        int size = Tool.dp2px(18, context);
        int sizePad = Tool.dp2px(8, context);
        for (int i = 0; i < apps.size(); i++) {
            items.add(new IconLabelItem(context, ((App) apps.get(i)).getIconProvider(), ((App) apps.get(i)).label, size).withIconGravity(GravityCompat.START).withDrawablePadding(context, sizePad));
        }
        fastItemAdapter.set(items);
        fastItemAdapter.withOnClickListener(new com.mikepenz.fastadapter.listeners.OnClickListener<IconLabelItem>() {
            public boolean onClick(View v, IAdapter<IconLabelItem> iAdapter, IconLabelItem item, int position) {
                if (onAppSelectedListener != null) {
                    onAppSelectedListener.onAppSelected((App) apps.get(position));
                }
                dialog.dismiss();
                return true;
            }
        });
        dialog.show();
    }

    public static void selectActionDialog(final Context context, int titleId, ActionItem selected, final int id, final OnActionSelectedListener onActionSelectedListener) {
        new Builder(context).title(context.getString(titleId)).negativeText((int) R.string.cancel).items((int) R.array.entries__gestures).itemsCallbackSingleChoice(LauncherAction.getActionItemIndex(selected) + 1, new ListCallbackSingleChoice() {
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                com.benny.openlauncher.core.activity.Home home;
                if (which > 0) {
                    ActionItem item = LauncherAction.getActionItem(which - 1);
                    if (item != null && item.action == Action.LaunchApp) {
                        final ActionItem finalItem = item;
                        DialogHelper.selectAppDialog(context, new OnAppSelectedListener() {
                            public void onAppSelected(App app) {
                                finalItem.extraData = com.benny.openlauncher.util.Tool.getStartAppIntent(app);
                                onActionSelectedListener.onActionSelected(finalItem);
                                com.benny.openlauncher.core.activity.Home home = Home.launcher;
                                ((DatabaseHelper) com.benny.openlauncher.core.activity.Home.db).setGesture(id, finalItem);
                            }
                        });
                    } else if (onActionSelectedListener != null) {
                        onActionSelectedListener.onActionSelected(item);
                        home = Home.launcher;
                        ((DatabaseHelper) com.benny.openlauncher.core.activity.Home.db).setGesture(id, item);
                    }
                } else {
                    home = Home.launcher;
                    ((DatabaseHelper) com.benny.openlauncher.core.activity.Home.db).deleteGesture(id);
                }
                return true;
            }
        }).show();
    }

    public static void setWallpaperDialog(final Context context) {
        new Builder(context).title((int) R.string.wallpaper).iconRes(R.drawable.ic_photo_black_24dp).items((int) R.array.entries__wallpaper_options).itemsCallback(new ListCallback() {
            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                switch (position) {
                    case 0:
                        context.startActivity(Intent.createChooser(new Intent("android.intent.action.SET_WALLPAPER"), context.getString(R.string.wallpaper_pick)));
                        return;
                    case 1:
                        try {
                            WallpaperManager.getInstance(context).setBitmap(StackBlur.blur(Tool.drawableToBitmap(context.getWallpaper()), 10, false));
                            return;
                        } catch (Exception e) {
                            Tool.toast(context, context.getString(R.string.wallpaper_unable_to_blur));
                            return;
                        }
                    default:
                        return;
                }
            }
        }).show();
    }

    public static void deletePackageDialog(Context context, DragEvent dragEvent) {
        Item item = (Item) DragDropHandler.getDraggedObject(dragEvent);
        if (item.type == Type.APP) {
            try {
                Log.v("deletePackageDialog: " + item.intent.getComponent().getPackageName());
                if (BaseUtils.isInstalled(context, item.intent.getComponent().getPackageName())) {
                    context.startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + item.intent.getComponent().getPackageName())));
                    return;
                }
                try {
                    com.benny.openlauncher.App appliation = (com.benny.openlauncher.App) context.getApplicationContext();
                    int i = 0;
                    while (i < appliation.getBaseConfig().getShortcut_dynamic().size()) {
                        BaseConfig.shortcut_dynamic shortcut_dynamic = (BaseConfig.shortcut_dynamic) appliation.getBaseConfig().getShortcut_dynamic().get(i);
                        if (shortcut_dynamic.getPackage_name().contains(item.intent.getComponent().getPackageName())) {
                            appliation.getBaseConfig().getShortcut_dynamic().remove(shortcut_dynamic);
                        } else {
                            i++;
                        }
                    }
                    Setup.appLoader().onAppUpdated(context, item.intent);
                } catch (Exception e) {
                    Log.e("error remove shortcut: " + e.getMessage());
                }
            } catch (Exception e2) {
                Log.e("error deletePackageDialog: " + item.intent.getComponent().getPackageName());
            }
        }
    }

    public static void backupDialog(final Context context) {
        new Builder(context).title((int) R.string.pref_title__backup).positiveText((int) R.string.cancel).items((int) R.array.entries__backup_options).itemsCallback(new ListCallback() {
            public void onSelection(MaterialDialog dialog, View itemView, int item, CharSequence text) {
                File directory;
                PackageManager m = context.getPackageManager();
                String s = context.getPackageName();
                if (context.getResources().getStringArray(R.array.entries__backup_options)[item].equals(context.getResources().getString(R.string.dialog__backup_app_settings__backup))) {
                    directory = new File(Environment.getExternalStorageDirectory() + "/OpenLauncher/");
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    try {
                        s = m.getPackageInfo(s, 0).applicationInfo.dataDir;
                        com.benny.openlauncher.util.Tool.copy(context, s + "/databases/home.db", directory + "/home.db");
                        com.benny.openlauncher.util.Tool.copy(context, s + "/shared_prefs/app.xml", directory + "/app.xml");
                        Toast.makeText(context, R.string.dialog__backup_app_settings__success, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.dialog__backup_app_settings__error, Toast.LENGTH_SHORT).show();
                    }
                }
                if (context.getResources().getStringArray(R.array.entries__backup_options)[item].equals(context.getResources().getString(R.string.dialog__backup_app_settings__restore))) {
                    directory = new File(Environment.getExternalStorageDirectory() + "/OpenLauncher/");
                    try {
                        s = m.getPackageInfo(s, 0).applicationInfo.dataDir;
                        com.benny.openlauncher.util.Tool.copy(context, directory + "/home.db", s + "/databases/home.db");
                        com.benny.openlauncher.util.Tool.copy(context, directory + "/app.xml", s + "/shared_prefs/app.xml");
                        Toast.makeText(context, R.string.dialog__backup_app_settings__success, Toast.LENGTH_SHORT).show();
                    } catch (Exception e2) {
                        Toast.makeText(context, R.string.dialog__backup_app_settings__error, Toast.LENGTH_SHORT).show();
                    }
                    System.exit(1);
                }
            }
        }).show();
    }
}
