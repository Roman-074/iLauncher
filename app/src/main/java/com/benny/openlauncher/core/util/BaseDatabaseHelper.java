package com.benny.openlauncher.core.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.manager.Setup.DataManager;
import com.benny.openlauncher.core.manager.Setup.Logger;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.model.Item.Type;
import com.benny.openlauncher.core.util.Definitions.ItemPosition;
import com.benny.openlauncher.core.util.Definitions.ItemState;
import java.util.ArrayList;
import java.util.List;

public class BaseDatabaseHelper extends SQLiteOpenHelper implements DataManager {
    protected static final String COLUMN_DATA = "data";
    protected static final String COLUMN_DESKTOP = "desktop";
    protected static final String COLUMN_LABEL = "label";
    protected static final String COLUMN_PAGE = "page";
    protected static final String COLUMN_STATE = "state";
    protected static final String COLUMN_TIME = "time";
    protected static final String COLUMN_TYPE = "type";
    protected static final String COLUMN_X_POS = "x";
    protected static final String COLUMN_Y_POS = "y";
    protected static final String DATABASE_HOME = "home_new.db";
    protected static final String SQL_CREATE_GESTURE = "CREATE TABLE gesture (time INTEGER PRIMARY KEY,type VARCHAR,data VARCHAR)";
    protected static final String SQL_CREATE_HOME = "CREATE TABLE home (time INTEGER PRIMARY KEY,type VARCHAR,label VARCHAR,x INTEGER,y INTEGER,data VARCHAR,page INTEGER,desktop INTEGER,state INTEGER)";
    protected static final String SQL_DELETE = "DROP TABLE IF EXISTS ";
    protected static final String SQL_QUERY = "SELECT * FROM ";
    protected static final String TABLE_GESTURE = "gesture";
    protected static final String TABLE_HOME = "home";
    protected Context context;
    protected SQLiteDatabase db = getWritableDatabase();

    public BaseDatabaseHelper(Context c) {
        super(c, DATABASE_HOME, null, 1);
        this.context = c;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HOME);
        db.execSQL(SQL_CREATE_GESTURE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS home");
        db.execSQL("DROP TABLE IF EXISTS gesture");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createItem(Item item, int page, ItemPosition itemPosition) {
        ContentValues itemValues = new ContentValues();
        itemValues.put(COLUMN_TIME, item.getId());
        itemValues.put(COLUMN_TYPE, item.type.toString());
        itemValues.put(COLUMN_LABEL, item.getLabel());
        itemValues.put(COLUMN_X_POS, Integer.valueOf(item.x));
        itemValues.put(COLUMN_Y_POS, Integer.valueOf(item.y));
        String concat = "";
        switch (item.type) {
            case APP:
                try {
                    Tool.saveIcon(this.context, Tool.drawableToBitmap(item.getIconProvider().getDrawableSynchronously(-1)), Integer.toString(item.getId().intValue()));
                } catch (Exception e) {
                }
                itemValues.put(COLUMN_DATA, Tool.getIntentAsString(item.intent));
                break;
            case GROUP:
                for (Item tmp : item.items) {
                    if (tmp != null) {
                        concat = concat + tmp.getId() + Definitions.INT_SEP;
                    }
                }
                itemValues.put(COLUMN_DATA, concat);
                break;
            case ACTION:
                itemValues.put(COLUMN_DATA, Integer.valueOf(item.actionValue));
                break;
            case WIDGET:
                itemValues.put(COLUMN_DATA, Integer.toString(item.widgetValue) + Definitions.INT_SEP + Integer.toString(item.spanX) + Definitions.INT_SEP + Integer.toString(item.spanY));
                break;
        }
        itemValues.put(COLUMN_PAGE, Integer.valueOf(page));
        itemValues.put(COLUMN_DESKTOP, Integer.valueOf(itemPosition.ordinal()));
        itemValues.put(COLUMN_STATE, Integer.valueOf(1));
        this.db.insert(TABLE_HOME, null, itemValues);
    }

    public void saveItem(Item item) {
        updateItem(item);
    }

    public void saveItem(Item item, int page, ItemPosition itemPosition) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE time = " + item.getId(), null);
        if (cursor.getCount() == 0) {
            createItem(item, page, itemPosition);
        } else if (cursor.getCount() == 1) {
            updateItem(item, page, itemPosition);
        }
    }

    public boolean saveItem(Item item, int page, ItemPosition itemPosition, boolean update) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE label = \"" + item.getLabel() + "\"", null);
        if (cursor.getCount() != 0) {
            return false;
        }
        cursor.close();
        for (int tempPosition = 0; tempPosition < Setup.appSettings().getDockSize(); tempPosition++) {
            if (this.db.rawQuery("SELECT * FROM home WHERE x = " + tempPosition + " AND " + COLUMN_DESKTOP + " = 0 ", null).getCount() == 0) {
                item.setX(tempPosition);
                item.setY(0);
                createItem(item, page, itemPosition);
                return true;
            }
        }
        return false;
    }

    public void updateSate(Item item, ItemState state) {
        updateItem(item, state);
    }

    public void deleteItem(Item item, boolean deleteSubItems) {
        Log.i("HuyAnh", "delete " + item.getLabel());
        if (deleteSubItems && item.getType() == Type.GROUP) {
            for (Item i : item.getGroupItems()) {
                deleteItem(i, deleteSubItems);
            }
        }
        this.db.delete(TABLE_HOME, "time = ?", new String[]{String.valueOf(item.getId())});
    }

    public List<List<Item>> getDesktop() {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home", null);
        List<List<Item>> desktop = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                int page = Integer.parseInt(cursor.getString(6));
                int desktopVar = Integer.parseInt(cursor.getString(7));
                int stateVar = Integer.parseInt(cursor.getString(8));
                while (page >= desktop.size()) {
                    desktop.add(new ArrayList());
                }
                if (desktopVar == 1 && stateVar == 1) {
                    ((List) desktop.get(page)).add(getSelectionItem(cursor));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return desktop;
    }

    public List<Item> getDock() {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home", null);
        List<Item> dock = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                int desktopVar = Integer.parseInt(cursor.getString(7));
                int stateVar = Integer.parseInt(cursor.getString(8));
                if (desktopVar == 0 && stateVar == 1) {
                    dock.add(getSelectionItem(cursor));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Tool.print("database : dock size is ", Integer.valueOf(dock.size()));
        return dock;
    }

    public List<Item> getGroup() {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home", null);
        List<Item> groups = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(1).equals("GROUP")) {
                        groups.add(getSelectionItem(cursor));
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return groups;
    }

    public int getMaxPage() {
        Cursor cursor = this.db.rawQuery("select max(page) from home", null);
        if (cursor == null || !cursor.moveToFirst()) {
            return 0;
        }
        int maxPage = cursor.getInt(0);
        cursor.close();
        return maxPage;
    }

    public Item getItem(int id) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE time = " + id, null);
        Item item = null;
        try {
            if (cursor.moveToFirst()) {
                item = getSelectionItem(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return item;
    }

    public Item getItem(Intent intent) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE data = \"" + Tool.getIntentAsString(intent) + "\"", null);
        Item item = null;
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            item = getSelectionItem(cursor);
        }
        cursor.close();
        return item;
    }

    public int getId(Intent intent) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE data = \"" + Tool.getIntentAsString(intent) + "\"", null);
        if (cursor == null || !cursor.moveToFirst()) {
            return 1;
        }
        int id = cursor.getInt(0);
        Log.i("HuyAnh", "id get db: " + id);
        cursor.close();
        return id;
    }

    public boolean isShowDesktop(int id) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE time = " + id, null);
        if (cursor == null) {
            return true;
        }
        boolean isDesktop = true;
        if (cursor.moveToFirst() && cursor.getInt(7) == 0) {
            isDesktop = false;
        }
        cursor.close();
        return isDesktop;
    }

    public boolean isDesktop(int id) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE time = " + id, null);
        if (cursor == null) {
            return true;
        }
        boolean isDesktop = true;
        if (cursor.moveToFirst() && cursor.getInt(7) == 0) {
            isDesktop = false;
        }
        cursor.close();
        return isDesktop;
    }

    public int getPage(int id) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE time = " + id, null);
        if (cursor == null) {
            return -1;
        }
        int page = -1;
        if (cursor.moveToFirst()) {
            page = cursor.getInt(6);
        }
        cursor.close();
        return page;
    }

    public int getIdGroup(String label) {
        try {
            Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE type = \"GROUP\" AND label = \"" + label + "\"", null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                cursor.close();
                return id;
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "getIdGroup: " + e.getMessage());
        }
        return 1;
    }

    public void setDesktop(List<List<Item>> desktop) {
        int pageCounter = 0;
        for (List<Item> page : desktop) {
            for (Item item : page) {
                Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE time = " + item.getId(), null);
                if (cursor.getCount() == 0) {
                    createItem(item, pageCounter, ItemPosition.Desktop);
                } else if (cursor.getCount() == 1) {
                    updateItem(item, pageCounter, ItemPosition.Desktop);
                }
            }
            pageCounter++;
        }
    }

    public void setDock(List<Item> dock) {
        for (Item item : dock) {
            Cursor cursorItem = this.db.rawQuery("SELECT * FROM home WHERE time = " + item.getId(), null);
            if (cursorItem.getCount() == 0) {
                createItem(item, 0, ItemPosition.Dock);
            } else if (cursorItem.getCount() == 1) {
                updateItem(item, 0, ItemPosition.Dock);
            }
        }
    }

    public void setItem(Item item, int page, ItemPosition itemPosition) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE time = " + item.getId(), null);
        if (cursor.getCount() == 0) {
            createItem(item, page, itemPosition);
        } else if (cursor.getCount() == 1) {
            updateItem(item, page, itemPosition);
        }
    }

    public void updateItem(Item item) {
        try {
            int intValue;
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_LABEL, item.getLabel());
            itemValues.put(COLUMN_X_POS, Integer.valueOf(item.x));
            itemValues.put(COLUMN_Y_POS, Integer.valueOf(item.y));
            Logger logger = Setup.logger();
            String str = "updateItem: %s (ID: %d)";
            Object[] objArr = new Object[2];
            objArr[0] = item != null ? item.getLabel() : "NULL";
            if (item != null) {
                intValue = item.getId().intValue();
            } else {
                intValue = -1;
            }
            objArr[1] = Integer.valueOf(intValue);
            logger.log(this, 4, null, str, objArr);
            Log.v("HuyAnh", "updateItem: " + (item != null ? item.getLabel() : "NULL") + " (ID: " + (item != null ? item.getId().intValue() : -1) + ")");
            String concat = "";
            switch (item.type) {
                case APP:
                    if (Setup.appSettings().enableImageCaching()) {
                        Tool.saveIcon(this.context, Tool.drawableToBitmap(item.getIconProvider().getDrawableSynchronously(-1)), Integer.toString(item.getId().intValue()));
                    }
                    itemValues.put(COLUMN_DATA, Tool.getIntentAsString(item.intent));
                    break;
                case GROUP:
                    for (Item tmp : item.items) {
                        if (tmp != null) {
                            concat = concat + tmp.getId() + Definitions.INT_SEP;
                        }
                    }
                    itemValues.put(COLUMN_DATA, concat);
                    break;
                case ACTION:
                    itemValues.put(COLUMN_DATA, Integer.valueOf(item.actionValue));
                    break;
                case WIDGET:
                    itemValues.put(COLUMN_DATA, Integer.toString(item.widgetValue) + Definitions.INT_SEP + Integer.toString(item.spanX) + Definitions.INT_SEP + Integer.toString(item.spanY));
                    break;
            }
            Cursor cursor = this.db.rawQuery("SELECT * FROM home WHERE time = " + item.getId(), null);
            if (cursor.getCount() == 0) {
                itemValues.put(COLUMN_TYPE, item.type.toString());
                itemValues.put(COLUMN_PAGE, Integer.valueOf(Home.launcher.desktop.getCurrentItem()));
                itemValues.put(COLUMN_DESKTOP, Integer.valueOf(1));
                itemValues.put(COLUMN_STATE, Integer.valueOf(1));
                itemValues.put(COLUMN_TIME, item.getId());
                this.db.insert(TABLE_HOME, null, itemValues);
            } else if (cursor.getCount() == 1) {
                this.db.update(TABLE_HOME, itemValues, "time = " + item.getId(), null);
            }
        } catch (Exception e) {
        }
    }

    public void updateItem(Item item, ItemState state) {
        ContentValues itemValues = new ContentValues();
        Logger logger = Setup.logger();
        String str = "updateItem (state): %s (ID: %d)";
        Object[] objArr = new Object[2];
        objArr[0] = item != null ? item.getLabel() : "NULL";
        objArr[1] = Integer.valueOf(item != null ? item.getId().intValue() : -1);
        logger.log(this, 4, null, str, objArr);
        itemValues.put(COLUMN_STATE, Integer.valueOf(state.ordinal()));
        this.db.update(TABLE_HOME, itemValues, "time = " + item.getId(), null);
    }

    public void updateItem(Item item, int page, ItemPosition itemPosition) {
        Logger logger = Setup.logger();
        String str = "updateItem (delete + create): %s (ID: %d)";
        Object[] objArr = new Object[2];
        objArr[0] = item != null ? item.getLabel() : "NULL";
        objArr[1] = Integer.valueOf(item != null ? item.getId().intValue() : -1);
        logger.log(this, 4, null, str, objArr);
        deleteItem(item, false);
        createItem(item, page, itemPosition);
    }

    private Item getSelectionItem(Cursor cursor) {
        Item item = new Item();
        int id = Integer.parseInt(cursor.getString(0));
        Type type = Type.valueOf(cursor.getString(1));
        String label = cursor.getString(2);
        int x = Integer.parseInt(cursor.getString(3));
        int y = Integer.parseInt(cursor.getString(4));
        String data = cursor.getString(5);
        item.setItemId(id);
        item.setLabel(label);
        item.x = x;
        item.y = y;
        item.type = type;
        switch (type) {
            case APP:
            case SHORTCUT:
                item.intent = Tool.getIntentFromString(data);
                if (!Setup.appSettings().enableImageCaching()) {
                    switch (type) {
                        case APP:
                        case SHORTCUT:
                            App app = Setup.appLoader().findItemApp(item);
                            item.iconProvider = app != null ? app.getIconProvider() : null;
                            break;
                    }
                }
                item.iconProvider = Setup.imageLoader().createIconProvider(Tool.getIcon(Home.launcher, Integer.toString(id)));
                break;
            case GROUP:
                item.items = new ArrayList();
                for (String s : data.split(Definitions.INT_SEP)) {
                    if (!s.equals("")) {
                        try {
                            if (!s.equals(cursor.getString(0))) {
                                item.items.add(getItem(Integer.parseInt(s)));
                            }
                        } catch (Exception e) {
                        }
                    }
                }
                break;
            case ACTION:
                item.actionValue = Integer.parseInt(data);
                break;
            case WIDGET:
                String[] dataSplit = data.split(Definitions.INT_SEP);
                item.widgetValue = Integer.parseInt(dataSplit[0]);
                item.spanX = Integer.parseInt(dataSplit[1]);
                item.spanY = Integer.parseInt(dataSplit[2]);
                break;
        }
        return item;
    }

    public void deleteGesture(int id) {
        this.db.delete(TABLE_GESTURE, "time = ?", new String[]{String.valueOf(id)});
    }
}
