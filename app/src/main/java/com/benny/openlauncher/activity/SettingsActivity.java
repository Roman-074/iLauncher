package com.benny.openlauncher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Process;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.adapter.SettingsMoreApps;
import com.benny.openlauncher.adapter.SettingsMoreAppsListener;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.base.BaseApplicationListener;
import com.benny.openlauncher.base.dao.BaseConfig;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.interfaces.SettingsManager;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.service.ChatHeadService;
import com.benny.openlauncher.service.ThreadUpdateNotify;
import com.benny.openlauncher.util.AppSettings;
import com.benny.openlauncher.util.AppSettingsBase;
import com.benny.openlauncher.util.Constant;
import com.benny.openlauncher.util.DialogAppCallback;
import com.benny.openlauncher.util.DialogPermission;
import com.benny.openlauncher.util.NotificationEnabledUtil;


public class SettingsActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {
    public static final int KEY_CHANGE_PASS = 1243;
    public static final int KEY_DISABLE_PASS = 1213;
    public static final int KEY_NEW_PASS = 1276;
    protected static Context context;

    private BaseApplication application;
    //    public static InterstitialAd mInterstitialAdMob;
    @BindView(R.id.settings_appbar)
    protected AppBarLayout appBarLayout;
    private AppSettings appSettings;
    @BindView(R.id.settings_rcView)
    RecyclerView rcView;
    @BindView(R.id.rlMoreApps)
    RelativeLayout rlMoreApps;
    private SettingsMoreApps settingsMoreAppsAdapter;
    private boolean shouldLauncherRestart = false;
    @BindView(R.id.settings_toolbar)

    protected Toolbar toolbar;
    @BindView(R.id.rlMoreApps_tv)
    TextView tvMoreAppsTitle;
//    @BindView(R.id.adView)
//    AdView mAdView;
    public static class SettingsFragmentDesktop extends PreferenceFragment {
        public static final String TAG = "com.benny.openlauncher.settings.SettingsFragmentDesktop";

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            android.util.Log.d("my", "onCreate: SettingsActivity(SettingsFragmentDesktop)");
            getPreferenceManager().setSharedPreferencesName(AppSettingsBase.SHARED_PREF_APP);
            addPreferencesFromResource(R.xml.preferences_desktop);
        }

        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            return super.onPreferenceTreeClick(screen, preference);
        }

        @SuppressLint({"DefaultLocale"})
        public void onResume() {
            super.onResume();
            AppSettings settings = AppSettings.get();
            findPreference(getString(R.string.pref_key__desktop_columns)).setSummary(getString(R.string.pref_title__size_of_colums) + ": " + settings.getDesktopColumnCount());
            findPreference(getString(R.string.pref_key__desktop_rows)).setSummary(getString(R.string.pref_title__size_of_rows) + ": " + settings.getDesktopRowCount());
            findPreference(getString(R.string.pref_key__desktop_fullscreen)).setSummary(getString(R.string.pref_title__new_status_bar_message).replace("xxxxxx", getString(R.string.app_name)));
        }
    }

    public static class SettingsFragmentDock extends PreferenceFragment {
        public static final String TAG = "com.benny.openlauncher.settings.SettingsFragmentDock";

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName(AppSettingsBase.SHARED_PREF_APP);
            addPreferencesFromResource(R.xml.preferences_dock_iporn_x);
        }

        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (!isAdded() || !preference.hasKey() || !preference.getKey().equals(getString(R.string.pref_key__dock_enable))) {
                return super.onPreferenceTreeClick(screen, preference);
            }
            Home.launcher.updateDock(true);
            return true;
        }
    }

    public static class SettingsFragmentLockScreen extends PreferenceFragment {
        public static final String TAG = "com.benny.openlauncher.settings.SettingsFragmentLockScreen";
        private Preference preferenceChangePassCode;
        private BaseApplication baseApplication;
        private SwitchPreference switchPreferencePassCode;

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            Log.d("HuyAnh", "onCreate fragment settings lock screen");
            this.preferenceChangePassCode = new Preference(getActivity());
            this.baseApplication = (BaseApplication) getActivity().getApplication();
            this.preferenceChangePassCode.setTitle(getString(R.string.pref_title__lock_screen_passcode_change_title));
            this.preferenceChangePassCode.setKey(getString(R.string.pref_key__lock_screen_passcode_change));
            this.switchPreferencePassCode = new SwitchPreference(getActivity());
            this.switchPreferencePassCode.setTitle(getString(R.string.pref_title__lock_screen_on_off_passcode));
            this.switchPreferencePassCode.setKey(getString(R.string.pref_key__lock_screen_enable_passcode));
            getPreferenceManager().setSharedPreferencesName(AppSettingsBase.SHARED_PREF_APP);
            addPreferencesFromResource(R.xml.preferences_lock_screen);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                String key = preference.getKey();
                Intent it;
                if (key.equals(getString(R.string.pref_key__lock_screen_enable))) {
                    if (VERSION.SDK_INT < 21 || NotificationEnabledUtil.isServicePermission(getActivity())) {
                        ThreadUpdateNotify.startThread(getActivity());
                        NotificationEnabledUtil.startService(getActivity());
                        if (this.baseApplication != null) {
                            this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_PERMISSION_1_ACCEPT);
                        }
                        requestPermissionData();

                    } else {
                        DialogPermission.showDialog(getActivity(), new DialogAppCallback() {
                            public void cancelDialog() {
                                requestPermissionData();
                            }

                            public void okDialog() {
                                //resetFirstShowPopup();
                                NotificationEnabledUtil.requestPermission(getActivity());
                                if (baseApplication != null) {
                                    baseApplication.putEvents(BaseApplication.EVENTS_NAME_PERMISSION_1);
                                }
                            }
                        }, 2);
                    }
                    if (Setup.appSettings().isLockScreenEnable() || !Setup.appSettings().isLockScreenEnablePassCode() || Setup.appSettings().getPassCodeLockScreen().equals("")) {
                        onResume();
                    } else {
                        Setup.appSettings().setLockScreenEnable(true);
                        it = new Intent(getActivity(), SettingsLockScreenActivity.class);
                        it.putExtra(NotificationCompat.CATEGORY_STATUS, 11);
                        getActivity().startActivity(it);
                    }


                } else if (key.equals(getString(R.string.pref_key__lock_screen_enable_passcode))) {
                    boolean z;
                    SettingsManager appSettings = Setup.appSettings();
                    if (Setup.appSettings().isLockScreenEnablePassCode()) {
                        z = false;
                    } else {
                        z = true;
                    }
                    appSettings.setLockScreenEnablePassCode(z);
                    it = new Intent(getActivity(), SettingsLockScreenActivity.class);
                    if (!Setup.appSettings().isLockScreenEnablePassCode() || Setup.appSettings().getPassCodeLockScreen().equals("")) {
                        it.putExtra(NotificationCompat.CATEGORY_STATUS, 0);
                    } else {
                        it.putExtra(NotificationCompat.CATEGORY_STATUS, 1);
                    }
                    getActivity().startActivity(it);
                    return true;
                } else if (key.equals(getString(R.string.pref_key__lock_screen_passcode_change))) {
                    it = new Intent(getActivity(), SettingsLockScreenActivity.class);
                    it.putExtra(NotificationCompat.CATEGORY_STATUS, 2);
                    getActivity().startActivity(it);
                    return true;
                }
            }
            return super.onPreferenceTreeClick(screen, preference);
        }
        public void requestReadHistoryAccess() {
            try {
                if (VERSION.SDK_INT >= 21) {
                    startActivity(new Intent("android.settings.USAGE_ACCESS_SETTINGS"));
                }
            } catch (Exception e) {
            }
        }


        private void requestPermissionData() {
            if (!isPermissionToReadHistory(getActivity())) {
                DialogPermission.showDialog(getActivity(), new DialogAppCallback() {
                    public void cancelDialog() {
                    }

                    public void okDialog() {
                        //resetFirstShowPopup();
                        requestReadHistoryAccess();
                        if (baseApplication != null) {
                            baseApplication.putEvents(BaseApplication.EVENTS_NAME_PERMISSION_2);
                        }
                    }
                }, 1);
            } else if (this.baseApplication != null) {
                this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_PERMISSION_2_ACCEPT);
            }
        }
        public void onResume() {
            super.onResume();
            Log.d("HuyAnh", "onResume fragment settings lock screen");
            try {
                ((SwitchPreference) findPreference(getString(R.string.pref_key__lock_screen_enable))).setChecked(Setup.appSettings().isLockScreenEnable());
            } catch (Exception e) {
            }
            try {
                if (Setup.appSettings().isLockScreenEnable()) {
                    if (getPreferenceScreen().findPreference(getString(R.string.pref_key__lock_screen_enable_passcode)) == null) {
                        getPreferenceScreen().addPreference(this.switchPreferencePassCode);
                    }
                    this.switchPreferencePassCode.setChecked(Setup.appSettings().isLockScreenEnablePassCode());
                } else {
                    if (getPreferenceScreen().findPreference(getString(R.string.pref_key__lock_screen_enable_passcode)) == this.switchPreferencePassCode) {
                        getPreferenceScreen().removePreference(this.switchPreferencePassCode);
                    }
                    Setup.appSettings().setLockScreenEnablePassCode(false);
                    Setup.appSettings().setPassCodeLockScreen("");
                }
            } catch (Exception e2) {
            }
            try {
                if (Setup.appSettings().isLockScreenEnablePassCode()) {
                    if (getPreferenceScreen().findPreference(getString(R.string.pref_key__lock_screen_passcode_change)) == null) {
                        getPreferenceScreen().addPreference(this.preferenceChangePassCode);
                    }
                } else if (getPreferenceScreen().findPreference(getString(R.string.pref_key__lock_screen_passcode_change)) == this.preferenceChangePassCode) {
                    getPreferenceScreen().removePreference(this.preferenceChangePassCode);
                }
            } catch (Exception e3) {
            }
        }
    }

    public static class SettingsFragmentMaster extends PreferenceFragment {
        public static final String TAG = "com.benny.openlauncher.settings.SettingsFragmentMaster";

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName(AppSettingsBase.SHARED_PREF_APP);
            addPreferencesFromResource(R.xml.preferences_master_iporn);
        }

        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                String key = preference.getKey();
                if (key.equals(getString(R.string.pref_key__cat_desktop))) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentDesktop.TAG, true);
                    //SettingsActivity.showAdmobInterstitial();
                    return true;
                } else if (key.equals(getString(R.string.pref_key__cat_dock))) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentDock.TAG, true);
                    //SettingsActivity.showAdmobInterstitial();
                    return true;
                } else if (key.equals(getString(R.string.pref_key__cat_wallpaper))) {
                    getActivity().startActivity(new Intent(getActivity(), WallpaperActivityNew.class));
                    //SettingsActivity.showAdmobInterstitial();
                    return true;
                } else if (key.equals(getString(R.string.pref_key__lock_screen_settings))) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentLockScreen.TAG, true);
                    return true;
                } else if (key.equals(getString(R.string.pref_key__on_off_iporn))) {
                    getActivity().startService(new Intent(getActivity(), ChatHeadService.class));
                } else if (key.equals(getString(R.string.setting_warning_key))) {
                    SettingsActivity.resetPreferredLauncherAndOpenChooser(getActivity());
                } else if (key.equals(getString(R.string.pref_key__share_friends))) {
                    BaseUtils.shareText(SettingsActivity.context, "https://play.google.com/store/apps/details?id=" + SettingsActivity.context.getPackageName(), getString(R.string.app_name), "Select Application");
                } else if (key.equals(getString(R.string.pref_key__cat_control_center))) {
                    getActivity().startActivity(new Intent(getActivity(), HelpActivity.class));
                    //SettingsActivity.showAdmobInterstitial();
                    return true;
                } else if (key.equals(getString(R.string.pref_key__cat_default_launcher))) {
                    SettingsActivity.resetPreferredLauncherAndOpenChooser(getActivity());
                }
            }
            return super.onPreferenceTreeClick(screen, preference);
        }

        @SuppressLint({"DefaultLocale"})
        public void onResume() {
            super.onResume();
            ((SettingsActivity) getActivity()).toolbar.setTitle((int) R.string.settings);
            AppSettings settings = AppSettings.get();
            findPreference(getString(R.string.pref_key__cat_desktop)).setSummary(String.format("%s: %d x %d", new Object[]{getString(R.string.pref_title__size), Integer.valueOf(settings.getDesktopColumnCount()), Integer.valueOf(settings.getDesktopRowCount())}));
            findPreference(getString(R.string.pref_key__cat_dock)).setSummary(String.format("%s: %d", new Object[]{getString(R.string.pref_title__size), Integer.valueOf(settings.getDockSize())}));
            findPreference(getString(R.string.pref_key__icon_size)).setSummary(String.format("%s: %d", new Object[]{getString(R.string.pref_title__size), Integer.valueOf(settings.getIconSize())}));
            String effectSummary = getString(R.string.pref_title__effect_desktop_dialog_default);
            if (settings.getDesktopEffect() != 0) {
                effectSummary = getString(R.string.pref_title__effect_desktop_dialog_title) + " " + settings.getDesktopEffect();
            }
            findPreference(getString(R.string.pref_key_effect_desktop)).setSummary(effectSummary);
        }
    }

    @SuppressLint("WrongConstant")
    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, HomeReset.class);
        packageManager.setComponentEnabledSetting(componentName, 1, 1);
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        context.startActivity(intent);
        packageManager.setComponentEnabledSetting(componentName, 0, 1);
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView((int) R.layout.activity_settings);
//        initAdmobFullAd();
//        loadAdmobAd();
        ButterKnife.bind((Activity) this);
        this.application = (BaseApplication) getApplication();
        context = this;
        this.toolbar.setTitle((int) R.string.settings);
        setSupportActionBar(this.toolbar);
        this.appSettings = AppSettings.get();
        this.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24px));
        this.toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsActivity.this.onBackPressed();
            }
        });
        showFragment(SettingsFragmentMaster.TAG, false);
        if (VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this) && !BaseUtils.isMyServiceRunning(this, ChatHeadService.class.getName())) {
                startService(new Intent(this, ChatHeadService.class));
            }
        } else if (!BaseUtils.isMyServiceRunning(this, ChatHeadService.class.getName())) {
            startService(new Intent(this, ChatHeadService.class));
        }
        this.application.loadNewDataConfig(new BaseApplicationListener() {
            public void onDoneLoadData() {
                try {
                    SettingsActivity.this.initMoreApps();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        if (Tool.isNetworkConnected(SettingsActivity.this)){
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
//        }else {
//            mAdView.setVisibility(View.GONE);
//        }

    }

    private void initMoreApps() {
        this.application.getBaseConfig().initMoreApps(this);
        this.tvMoreAppsTitle.setTypeface(this.application.getBaseTypeface().getRegular());
        if (this.application.getBaseConfig().getMore_apps().size() > 0) {
            this.rlMoreApps.setVisibility(View.VISIBLE);
            this.settingsMoreAppsAdapter = new SettingsMoreApps(this, new SettingsMoreAppsListener() {
                public void onClick(BaseConfig.more_apps more_apps) {
                    BaseUtils.gotoStore(SettingsActivity.this, more_apps.getPackagename());
                }
            });
            this.rcView.setLayoutManager(new LinearLayoutManager(this, 0, false));
            this.rcView.setAdapter(this.settingsMoreAppsAdapter);
            return;
        }
        this.rlMoreApps.setVisibility(View.GONE);
    }

    protected void showFragment(String tag, boolean addToBackStack) {
        PreferenceFragment fragment = (PreferenceFragment) getFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            int obj = -1;
            switch (tag.hashCode()) {
                case -1942263551:
                    if (tag.equals(SettingsFragmentMaster.TAG)) {
                        obj = 3;
                        break;
                    }
                    break;
                case -431326062:
                    if (tag.equals(SettingsFragmentDock.TAG)) {
                        obj = 1;
                        break;
                    }
                    break;
                case 636036445:
                    if (tag.equals(SettingsFragmentDesktop.TAG)) {
                        obj = 0;
                        break;
                    }
                    break;
                case 1818328214:
                    if (tag.equals(SettingsFragmentLockScreen.TAG)) {
                        obj = 2;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case 0:
                    fragment = new SettingsFragmentDesktop();
                    this.toolbar.setTitle((int) R.string.pref_title__desktop);
                    break;
                case 1:
                    fragment = new SettingsFragmentDock();
                    this.toolbar.setTitle((int) R.string.pref_title__dock);
                    break;
                case 2:
                    fragment = new SettingsFragmentLockScreen();
                    this.toolbar.setTitle((int) R.string.pref_title__lock_screen);
                    break;
                default:
                    fragment = new SettingsFragmentMaster();
                    this.toolbar.setTitle((int) R.string.settings);
                    break;
            }
        }
        FragmentTransaction t = getFragmentManager().beginTransaction();
        if (addToBackStack) {
            t.addToBackStack(tag);
        }
        t.replace(R.id.settings_fragment_container, fragment, tag).commit();
    }

    protected void onResume() {
        this.appSettings.registerPreferenceChangedListener(this);
        super.onResume();
        if (Home.launcher != null) {
            Home.launcher.resetFirstShowPopup();
        }
        try {
            initMoreApps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onPause() {
        Log.d("HuyAnh", "shouldLauncherRestart: " + this.shouldLauncherRestart);
        this.appSettings.unregisterPreferenceChangedListener(this);
        this.appSettings.setAppRestartRequired(this.shouldLauncherRestart);
        super.onPause();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("HuyAnh", "onSharedPreferenceChanged " + key);
        if (key.equals(getString(R.string.pref_key__icon_size)) || key.equals(getString(R.string.pref_key_effect_desktop)) || key.equals(getString(R.string.pref_key__desktop_columns)) || key.equals(getString(R.string.pref_key__desktop_rows)) || key.equals(getString(R.string.pref_key__desktop_fullscreen)) || key.equals(getString(R.string.pref_key__desktop_show_position_indicator)) || key.equals(getString(R.string.pref_key__desktop_show_label)) || key.equals(getString(R.string.pref_key__desktop_background_color)) || key.equals(getString(R.string.pref_key__dock_size)) || key.equals(getString(R.string.pref_key__dock_show_label)) || key.equals(getString(R.string.pref_key__dock_background_color))) {
            this.shouldLauncherRestart = true;
        }
    }

    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            finish();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("HuyAnh", "onActivityResult settings activity");
        switch (requestCode) {
            case Constant.REQUEST_PERMISSION_DRAW_OVER_APP /*1252*/:
                if (VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this) && !BaseUtils.isMyServiceRunning(this, ChatHeadService.class.getName())) {
                    startService(new Intent(this, ChatHeadService.class));
                    return;
                }
                return;
            default:
                return;
        }
    }
    @SuppressLint({"WrongConstant"})
    private static boolean isPermissionToReadHistory(Context context) {
        if (VERSION.SDK_INT < 21 || ((AppOpsManager) context.getSystemService("appops")).checkOpNoThrow("android:get_usage_stats", Process.myUid(), context.getPackageName()) == 0) {
            return true;
        }
        return false;
    }
//    private void initAdmobFullAd() {
//        mInterstitialAdMob = new InterstitialAd(this);
//        mInterstitialAdMob.setAdUnitId(Common.getPrefString(this, SecureEnvironment.getString("admob_inter")));
//        mInterstitialAdMob.setAdListener(new AdListener() {
//            public void onAdClosed() {
//                SettingsActivity.this.loadAdmobAd();
//            }
//
//            public void onAdLoaded() {
//            }
//
//            public void onAdOpened() {
//            }
//
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//            }
//
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
//            }
//
//            public void onAdClicked() {
//                super.onAdClicked();
//            }
//
//            public void onAdImpression() {
//                super.onAdImpression();
//            }
//        });
//    }
//
//    private void loadAdmobAd() {
//        if (mInterstitialAdMob != null && !mInterstitialAdMob.isLoaded()) {
//            mInterstitialAdMob.loadAd(new Builder().build());
//        }
//    }
//
//    private static void showAdmobInterstitial() {
//        if (mInterstitialAdMob != null && mInterstitialAdMob.isLoaded()) {
//            mInterstitialAdMob.show();
//        }
//    }
}
