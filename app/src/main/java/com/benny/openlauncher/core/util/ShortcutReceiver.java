package com.benny.openlauncher.core.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.Log;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.Definitions.ItemPosition;
import com.benny.openlauncher.core.widget.CellContainer;

public class ShortcutReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            Item item;
            String name = intent.getExtras().getString("android.intent.extra.shortcut.NAME");
            Intent newIntent = (Intent) intent.getExtras().get("android.intent.extra.shortcut.INTENT");
            Drawable shortcutIconDrawable = null;
            BitmapDrawable bitmapDrawable;
            try {
                Parcelable iconResourceParcelable = intent.getExtras().getParcelable("android.intent.extra.shortcut.ICON_RESOURCE");
                if (iconResourceParcelable != null && (iconResourceParcelable instanceof ShortcutIconResource)) {
                    ShortcutIconResource iconResource = (ShortcutIconResource) iconResourceParcelable;
                    Resources resources = context.getPackageManager().getResourcesForApplication(iconResource.packageName);
                    if (resources != null) {
                        shortcutIconDrawable = resources.getDrawable(resources.getIdentifier(iconResource.resourceName, null, null));
                    }
                }
                if (shortcutIconDrawable == null) {
                    bitmapDrawable = new BitmapDrawable(context.getResources(), (Bitmap) intent.getExtras().getParcelable("android.intent.extra.shortcut.ICON"));
                }
            } catch (Exception e) {
                if (null == null) {
                    bitmapDrawable = new BitmapDrawable(context.getResources(), (Bitmap) intent.getExtras().getParcelable("android.intent.extra.shortcut.ICON"));
                }
            } catch (Throwable th) {
                Throwable th2 = th;
                if (null == null) {
                    bitmapDrawable = new BitmapDrawable(context.getResources(), (Bitmap) intent.getExtras().getParcelable("android.intent.extra.shortcut.ICON"));
                }
            }
            App app = Setup.appLoader().createApp(newIntent);
            if (app != null) {
                item = Item.newAppItem(app);
            } else {
                item = Item.newShortcutItem(newIntent, shortcutIconDrawable, name);
            }
            Point preferredPos = ((CellContainer) Home.launcher.desktop.pages.get(Home.launcher.desktop.getCurrentItem())).findFreeSpace();
            if (preferredPos == null) {
                Tool.toast(Home.launcher, (int) R.string.toast_not_enough_space);
                return;
            }
            item.setX(preferredPos.x);
            item.setY(preferredPos.y);
            Log.e("x",item.getX()+"  y  "+item.getY());
            Home.db.saveItem(item, Home.launcher.desktop.getCurrentItem(), ItemPosition.Desktop);
            boolean added = Home.launcher.desktop.addItemToPage(item, Home.launcher.desktop.getCurrentItem());
            Setup.logger().log(this, 4, null, "Shortcut installed - %s => Intent: %s (Item type: %s; x = %d, y = %d, added = %b)", name, newIntent, item.getType(), Integer.valueOf(item.getX()), Integer.valueOf(item.getY()), Boolean.valueOf(added));
        }
    }
}
