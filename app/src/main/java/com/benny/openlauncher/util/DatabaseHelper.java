package com.benny.openlauncher.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.benny.openlauncher.core.util.BaseDatabaseHelper;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.util.LauncherAction.Action;
import com.benny.openlauncher.util.LauncherAction.ActionItem;

public class DatabaseHelper extends BaseDatabaseHelper {
    public DatabaseHelper(Context c) {
        super(c);
    }

    public ActionItem getGesture(int value) {
        Cursor cursor = this.db.rawQuery("SELECT * FROM gesture WHERE time = " + value, null);
        ActionItem item = null;
        if (cursor.moveToFirst()) {
            item = new ActionItem(Action.valueOf(cursor.getString(1)), Tool.getIntentFromString(cursor.getString(2)));
        }
        cursor.close();
        return item;
    }

    public void setGesture(int id, ActionItem actionItem) {
        ContentValues values = new ContentValues();
        values.put("time", Integer.valueOf(id));
        values.put("type", actionItem.action.toString());
        values.put("data", Tool.getIntentAsString(actionItem.extraData));
        Cursor cursorItem = this.db.rawQuery("SELECT * FROM gesture WHERE time = " + id, null);
        if (cursorItem.getCount() == 0) {
            this.db.insert("gesture", null, values);
        } else if (cursorItem.getCount() == 1) {
            this.db.update("gesture", values, "time = " + id, null);
        }
    }
}
