package com.benny.openlauncher.core.model;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.LabelProvider;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.BaseIconProvider;
import com.benny.openlauncher.core.util.Tool;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Item implements LabelProvider, Parcelable {
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    public int actionValue;
    public BaseIconProvider iconProvider;
    private int idValue;
    public Intent intent;
    public List<Item> items;
    private String name;
    public int spanX;
    public int spanY;
    public Type type;
    public int widgetValue;
    public int x;
    public int y;

    public enum Type {
        APP,
        SHORTCUT,
        GROUP,
        ACTION,
        WIDGET
    }

    public Item() {
        this.idValue = 0;
        this.name = "";
        this.iconProvider = null;
        this.x = -1;
        this.y = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.idValue = new Random().nextInt();
    }

    public Item(Parcel parcel) {
        BaseIconProvider baseIconProvider = null;
        this.idValue = 0;
        this.name = "";
        this.iconProvider = null;
        this.x = -1;
        this.y = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.idValue = parcel.readInt();
        this.type = Type.valueOf(parcel.readString());
        this.name = parcel.readString();
        this.x = parcel.readInt();
        this.y = parcel.readInt();
        switch (this.type) {
            case APP:
            case SHORTCUT:
                this.intent = Tool.getIntentFromString(parcel.readString());
                break;
            case GROUP:
                List<String> labels = new ArrayList();
                parcel.readStringList(labels);
                this.items = new ArrayList();
                for (String s : labels) {
                    List list = this.items;
                    Home home = Home.launcher;
                    list.add(Home.db.getItem(Integer.parseInt(s)));
                }
                break;
            case ACTION:
                this.actionValue = parcel.readInt();
                break;
            case WIDGET:
                this.widgetValue = parcel.readInt();
                this.spanX = parcel.readInt();
                this.spanY = parcel.readInt();
                break;
        }
        try {
            if (Home.launcher == null || !new File(Home.launcher.getFilesDir() + "/icons/" + Integer.toString(this.idValue) + ".png").exists()) {
                switch (this.type) {
                    case APP:
                    case SHORTCUT:
                        App app = Setup.appLoader().findItemApp(this);
                        if (app != null) {
                            baseIconProvider = app.getIconProvider();
                        }
                        this.iconProvider = baseIconProvider;
                        return;
                    default:
                        return;
                }
            }
            this.iconProvider = Setup.imageLoader().createIconProvider(Tool.getIcon(Home.launcher, Integer.toString(this.idValue)));
        } catch (Exception e) {
        }
    }

    public boolean equals(Object object) {
        return object != null && this.idValue == ((Item) object).idValue;
    }

    public static Item newAppItem(App app) {
        Item item = Setup.dataManager().getItem(toIntent(app));
        if (item == null) {
            item = new Item();
            item.type = Type.APP;
            item.name = app.getLabel();
            item.iconProvider = app.getIconProvider();
            item.intent = toIntent(app);
            return item;
        }
        try {
            if (Home.launcher == null || !new File(Home.launcher.getFilesDir() + "/icons/" + Integer.toString(item.getId().intValue()) + ".png").exists()) {
                return item;
            }
            item.iconProvider = Setup.imageLoader().createIconProvider(Tool.getIcon(Home.launcher, Integer.toString(item.getId().intValue())));
            return item;
        } catch (Exception e) {
            return item;
        }
    }

    public static Item newShortcutItem(Intent intent, Drawable icon, String name) {
        Item item = new Item();
        item.type = Type.SHORTCUT;
        item.name = name;
        item.iconProvider = Setup.imageLoader().createIconProvider(icon);
        item.spanX = 1;
        item.spanY = 1;
        item.intent = intent;
        return item;
    }

    public static Item newGroupItem() {
        Item item = new Item();
        item.type = Type.GROUP;
        item.name = "Unnamed " + new Random().nextInt();
        item.spanX = 1;
        item.spanY = 1;
        item.items = new ArrayList();
        return item;
    }

    public static Item newActionItem(int action) {
        Item item = new Item();
        item.type = Type.ACTION;
        item.spanX = 1;
        item.spanY = 1;
        item.actionValue = action;
        return item;
    }

    public static Item newWidgetItem(int widgetValue) {
        Item item = new Item();
        item.type = Type.WIDGET;
        item.widgetValue = widgetValue;
        item.spanX = 1;
        item.spanY = 1;
        return item;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.idValue);
        out.writeString(this.type.toString());
        out.writeString(this.name);
        out.writeInt(this.x);
        out.writeInt(this.y);
        switch (this.type) {
            case APP:
            case SHORTCUT:
                out.writeString(Tool.getIntentAsString(this.intent));
                return;
            case GROUP:
                List<String> labels = new ArrayList();
                for (Item i : this.items) {
                    labels.add(Integer.toString(i.idValue));
                }
                out.writeStringList(labels);
                return;
            case ACTION:
                out.writeInt(this.actionValue);
                return;
            case WIDGET:
                out.writeInt(this.widgetValue);
                out.writeInt(this.spanX);
                out.writeInt(this.spanY);
                return;
            default:
                return;
        }
    }

    public void reset() {
        this.idValue = new Random().nextInt();
    }

    public Integer getId() {
        return Integer.valueOf(this.idValue);
    }

    public void setItemId(int id) {
        this.idValue = id;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public String getLabel() {
        return this.name;
    }

    public void setLabel(String label) {
        this.name = label;
        int idDb = Setup.dataManager().getIdGroup(this.name);
        if (idDb != 1) {
            setItemId(idDb);
        }
    }

    public Type getType() {
        return this.type;
    }

    public List<Item> getGroupItems() {
        return this.items;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getSpanX() {
        return this.spanX;
    }

    public int getSpanY() {
        return this.spanY;
    }

    public void setSpanX(int x) {
        this.spanX = x;
    }

    public void setSpanY(int y) {
        this.spanY = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @SuppressLint("WrongConstant")
    private static Intent toIntent(App app) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(268435456);
        intent.setClassName(app.getPackageName(), app.getClassName());
        return intent;
    }

    public BaseIconProvider getIconProvider() {
        return this.iconProvider;
    }

    public void setIconProvider(BaseIconProvider iconProvider) {
        this.iconProvider = iconProvider;
    }

    public void changeIconProvider(Drawable drawable) {
        this.iconProvider = Setup.imageLoader().createIconProvider(drawable);
    }

    public String getPackageName() {
        return getIntent().getComponent().getPackageName();
    }

    public String getClassName() {
        return getIntent().getComponent().getClassName();
    }
}
