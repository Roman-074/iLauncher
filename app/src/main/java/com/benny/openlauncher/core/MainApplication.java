package com.benny.openlauncher.core;

import android.text.TextUtils;

import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.core.model.AppNotifyItem;
import com.benny.openlauncher.core.sql.DBHelper;
import com.benny.openlauncher.core.util.SharedPreferencesGlobalUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class MainApplication extends BaseApplication {
    public DBHelper dbHelper;
    private HashMap<String, AppNotifyItem> mapNumberNotify;

    public void onCreate() {
        super.onCreate();
        this.dbHelper = new DBHelper(this);
    }

    public HashMap<String, AppNotifyItem> getMapNumberNotify() {
        if (this.mapNumberNotify == null) {
            try {
                String str = SharedPreferencesGlobalUtil.getValue(this, "number_notify");
                if (!TextUtils.isEmpty(str)) {
                    this.mapNumberNotify = (HashMap) new Gson().fromJson(str, new TypeToken<HashMap<String, AppNotifyItem>>() {
                    }.getType());
                }
            } catch (Exception e) {
            }
        }
        if (this.mapNumberNotify == null) {
            this.mapNumberNotify = new HashMap();
        }
        return this.mapNumberNotify;
    }

    public void saveMapNumberNotify() {
        try {
            SharedPreferencesGlobalUtil.setValue(this, "number_notify", new Gson().toJson(getMapNumberNotify()));
        } catch (Exception e) {
        }
    }
}
