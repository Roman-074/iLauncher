package com.benny.openlauncher.base;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.benny.openlauncher.base.dao.BaseConfig;
import com.benny.openlauncher.base.dao.BaseTypeface;
import com.benny.openlauncher.base.utils.BaseConstant;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.base.utils.Log;
import com.google.gson.Gson;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class BaseApplication extends Application {
    public static final String EVENTS_NAME_CLICK_ICON_ON_SEARCH_BAR = "custom_events_icon_search_bar";
    public static final String EVENTS_NAME_CLICK_ICON_SETTINGS = "custom_events_icon_settings";
    public static final String EVENTS_NAME_DAY = "custom_events_day_";
    public static final String EVENTS_NAME_DAY_TIME_FIRST = "custom_events_day_time_first";
    public static final String EVENTS_NAME_DEFAULT_LAUNCHER = "custom_events_default_launcher";
    public static final String EVENTS_NAME_GET_STARTED = "custom_events_get_started";
    public static final String EVENTS_NAME_OPEN_SPLASH = "custom_events_open_splash";
    public static final String EVENTS_NAME_PERMISSION_1 = "custom_events_permission_1";
    public static final String EVENTS_NAME_PERMISSION_1_ACCEPT = "custom_events_permission_1_accept";
    public static final String EVENTS_NAME_PERMISSION_2 = "custom_events_permission_2";
    public static final String EVENTS_NAME_PERMISSION_2_ACCEPT = "custom_events_permission_2_accept";
    public static final String EVENTS_NAME_TUTORIAL_1 = "custom_events_tutorial_1";
    public static final String EVENTS_NAME_TUTORIAL_2 = "custom_events_tutorial_2";
    private static final String PREF_IS_PURCHASE = "pref_key_base_is_inapp_billing";
    private static final String PREF_TIME_UPDATE_DATA_CONFIG = "pref_key_time_final_update_data_config";
    private BaseApplicationListener baseApplicationListener = null;
    private BaseConfig baseConfig = new BaseConfig();
    private BaseTypeface baseTypeface;
    public Editor editor;
    public Gson gson;
    public int heightPixels = 0;
    private OkHttpClient okHttpClient;
    public SharedPreferences pref;
    private long timeDelay = 86400000;
    public int widthPixels = 0;

    class LoadData extends AsyncTask<Void, Void, Void> {
        LoadData() {
        }

        protected Void doInBackground(Void... params) {
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            BaseApplication.this.baseConfig.initMoreApps(BaseApplication.this.getApplicationContext());
            if (BaseApplication.this.baseApplicationListener == null) {
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        new File(getFilesDir().getPath() + "/txt/").mkdirs();
        this.pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.editor = this.pref.edit();
        this.gson = new Gson();
        BaseUtils.setDateInstall(getApplicationContext());
        this.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_SHOWED_POPUP, 0);
        this.editor.putLong(BaseConstant.KEY_CONTROLADS_TIME_CLICKED_POPUP, 0);
        this.editor.putLong(BaseConstant.KEY_TIME_START_APP, System.currentTimeMillis());
        this.editor.apply();
        if (this.baseConfig == null) {
            this.baseConfig = new BaseConfig();
        }
    }

    public void setPurchase() {
        this.editor.putBoolean(PREF_IS_PURCHASE, true);
        this.editor.apply();
    }

    public boolean isPurchase() {
        return this.pref.getBoolean(PREF_IS_PURCHASE, false);
    }

    public void loadNewDataConfig() {
        if (System.currentTimeMillis() - this.pref.getLong(PREF_TIME_UPDATE_DATA_CONFIG, 0) >= this.timeDelay) {
            Log.d("load new data config");
            this.baseApplicationListener = null;
            this.editor.putLong(PREF_TIME_UPDATE_DATA_CONFIG, System.currentTimeMillis());
            this.editor.apply();
            new LoadData().execute(new Void[0]);
            return;
        }
        Log.e("chua du thoi gian delay load new data config");
    }

    public void loadNewDataConfig(BaseApplicationListener baseApplicationListener) {
        Log.d("load new data config force");
        this.baseApplicationListener = baseApplicationListener;
        new LoadData().execute(new Void[0]);
    }

    public BaseTypeface getBaseTypeface() {
        if (this.baseTypeface == null) {
            this.baseTypeface = new BaseTypeface(this);
        }
        return this.baseTypeface;
    }

    public OkHttpClient getOkHttpClient() {
        if (this.okHttpClient == null) {
            this.okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
        }
        return this.okHttpClient;
    }

    public BaseConfig getBaseConfig() {
        return this.baseConfig;
    }

    public boolean putEvents(String content) {
        if (this.pref == null || this.editor == null || !this.pref.getBoolean(content, true)) {
            return false;
        }
        Log.v("log event firebase " + content);
        this.editor.putBoolean(content, false);
        this.editor.apply();
        return true;
    }

    public void putEventsDay() {
        try {
            if (this.pref != null && this.editor != null) {
                if (this.pref.getLong(EVENTS_NAME_DAY_TIME_FIRST, 0) == 0) {
                    this.editor.putLong(EVENTS_NAME_DAY_TIME_FIRST, Calendar.getInstance().getTimeInMillis() / 1000);
                    this.editor.apply();
                }
                putEvents(EVENTS_NAME_DAY + ((int) (((Calendar.getInstance().getTimeInMillis() / 1000) - this.pref.getLong(EVENTS_NAME_DAY_TIME_FIRST, 0)) / 86400)));
            }
        } catch (Exception e) {
        }
    }
}
