package com.benny.openlauncher.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.customview.ControlCenter;
import com.benny.openlauncher.customview.LockScreen;
import com.benny.openlauncher.customview.LockScreenAction;
import com.benny.openlauncher.lock.Utils;



import java.lang.reflect.Field;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class ChatHeadService extends Service {
    static final int MIN_DISTANCE = 100;
    public static ChatHeadService chatHeadService;
    private Intent defaultIntentBattery = null;
    private Handler handler = new Handler();
    private LockScreen lockScreen = null;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private WindowManager mWindowManager;
    private int orientationCurrent = 1;
    private PhoneStateListener phoneStateListener = null;
    private ScreenReceiver receiverScreen;
    private int signalStrengthLevel = 0;
    private View taithoView = null;
    private View touchView;
    private ControlCenter viewControlCenter;
    private float x1;
    private float x2;
    private float y1;
    private float y2;

    private class ScreenReceiver extends BroadcastReceiver {
        private ScreenReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() == "android.intent.action.BATTERY_CHANGED") {
                    ChatHeadService.this.defaultIntentBattery = intent;
                    if (ChatHeadService.this.lockScreen != null && ChatHeadService.this.lockScreen.isShown()) {
                        ChatHeadService.this.lockScreen.updateBattery(intent);
                        return;
                    }
                    return;
                }
            } catch (Exception e) {
            }
            if (intent != null) {
                try {
                    if (intent.getAction() != null) {
                        Log.d("HuyAnh", "action on/off: " + intent.getAction());
                        if (Setup.appSettings().isLockScreenEnable() && intent.getAction() == "android.intent.action.SCREEN_OFF") {
                            ChatHeadService.this.addLockScreen(false);
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
                if (ChatHeadService.this.isScreenOn(context)) {
                    ChatHeadService.this.startRun();
                } else {
                    ChatHeadService.this.resetAll();
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    public ControlCenter getViewControlCenter() {
        return this.viewControlCenter;
    }

    public void showCC() {
        if (this.viewControlCenter == null) {
            return;
        }
        if (this.viewControlCenter.getTranslationY() == 0.0f) {
            this.viewControlCenter.visibleOrGone(false);
        } else if (this.orientationCurrent == 1) {
            this.viewControlCenter.visibleOrGone(true);
        }
    }

    public boolean hideCC() {
        if (this.viewControlCenter == null) {
            return true;
        }
        if (this.viewControlCenter.getTranslationY() != 0.0f) {
            return false;
        }
        this.viewControlCenter.visibleOrGone(false);
        return true;
    }

    public void showTouchView() {
        if (this.touchView != null && !this.touchView.isShown()) {
            this.touchView.setVisibility(View.VISIBLE);
        }
    }

    public void hideTouchView() {
        if (this.touchView != null && this.touchView.isShown()) {
            this.touchView.setVisibility(View.GONE);
        }
    }

    public boolean isPermissionOverlay() {
        if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
            return true;
        }
        return false;
    }

    public void onCreate() {
        super.onCreate();
        chatHeadService = this;
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isPermissionOverlay()) {
            LayoutParams params;
            LayoutParams paramsTouchView;
            chatHeadService = this;
            this.mWindowManager = (WindowManager) getSystemService("window");
            removeAll();
            drawTaiTho();
            this.viewControlCenter = (ControlCenter) LayoutInflater.from(this).inflate(R.layout.view_control_center_only, null);
            if (VERSION.SDK_INT >= 26) {
                params = new LayoutParams(-1, -1, 2038, 256, -3);
            } else {
                params = new LayoutParams(-1, -1, Tool.CACHE_ERROR_CODE, 256, -3);
            }
            this.mWindowManager.addView(this.viewControlCenter, params);
            this.viewControlCenter.setVisibility(4);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (ChatHeadService.this.viewControlCenter != null) {
                        ChatHeadService.this.viewControlCenter.setTranslationY((float) ChatHeadService.this.viewControlCenter.getHeight());
                    }
                }
            }, 2000);
            this.touchView = new View(this);
            if (VERSION.SDK_INT >= 26) {
                paramsTouchView = new LayoutParams(-1, BaseUtils.genpx((Context) this, 15), 2038, 8, -3);
            } else {
                paramsTouchView = new LayoutParams(-1, BaseUtils.genpx((Context) this, 15), Tool.CACHE_ERROR_CODE, 8, -3);
            }
            paramsTouchView.gravity = 80;
            this.touchView.setBackgroundColor(0);
            this.touchView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case 0:
                            ChatHeadService.this.x1 = event.getX();
                            ChatHeadService.this.y1 = event.getY();
                            break;
                        case 1:
                        case 2:
                            ChatHeadService.this.x2 = event.getX();
                            ChatHeadService.this.y2 = event.getY();
                            float deltaX = ChatHeadService.this.x2 - ChatHeadService.this.x1;
                            float deltaY = ChatHeadService.this.y2 - ChatHeadService.this.y1;
                            if ((Math.abs(deltaX) <= Math.abs(deltaY) || Math.abs(deltaX) < 100.0f) && Math.abs(deltaX) <= Math.abs(deltaY) && Math.abs(deltaY) >= 100.0f && ChatHeadService.this.y2 < ChatHeadService.this.y1 && ChatHeadService.this.orientationCurrent == 1) {
                                ChatHeadService.this.viewControlCenter.visibleOrGone(true);
                                break;
                            }
                    }
                    return false;
                }
            });
            this.mWindowManager.addView(this.touchView, paramsTouchView);
            this.touchView.setVisibility(8);
            if (this.receiverScreen != null) {
                unregisterReceiver(this.receiverScreen);
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.SCREEN_ON");
            filter.addAction("android.intent.action.SCREEN_OFF");
            filter.addAction("android.intent.action.BATTERY_CHANGED");
            this.receiverScreen = new ScreenReceiver();
            registerReceiver(this.receiverScreen, filter);
            startRun();
            return 3;
        }
        stopSelf();
        return 2;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v("HuyAnh", "onConfigurationChanged " + newConfig.orientation);
        this.orientationCurrent = newConfig.orientation;
        hideCC();
        drawTaiTho();
    }

    @SuppressLint("WrongConstant")
    private void removeAll() {
        try {
            if (this.mWindowManager == null) {
                this.mWindowManager = (WindowManager) getSystemService("window");
            }
            if (this.viewControlCenter != null) {
                this.mWindowManager.removeView(this.viewControlCenter);
                this.viewControlCenter = null;
            }
            if (this.touchView != null) {
                this.mWindowManager.removeView(this.touchView);
                this.touchView = null;
            }
        } catch (Exception e) {
        }
    }

    @SuppressLint("WrongConstant")
    private void removeTaiTho() {
        try {
            if (this.mWindowManager == null) {
                this.mWindowManager = (WindowManager) getSystemService("window");
            }
            if (this.taithoView != null) {
                this.mWindowManager.removeView(this.taithoView);
                this.taithoView = null;
            }
        } catch (Exception e) {
        }
    }

    @SuppressLint("WrongConstant")
    private void drawTaiTho() {
        try {
            Log.i("HuyAnh", "Setup.appSettings().isIpornX(): " + Setup.appSettings().isIpornX());
            removeTaiTho();
            if (Setup.appSettings().isIpornX() && this.orientationCurrent == 1) {
                LayoutParams paramsTaiTho;
                this.taithoView = View.inflate(getApplicationContext(), R.layout.ip_ox11, null);
                if (VERSION.SDK_INT >= 26) {
                    paramsTaiTho = new LayoutParams((int) (((float) getWidthScreen()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT), -2, 2038, 296, -3);
                } else if (VERSION.SDK_INT >= 21) {
                    paramsTaiTho = new LayoutParams((int) (((float) getWidthScreen()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT), -2, Tool.CACHE_ERROR_CODE, 296, -3);
                } else {
                    paramsTaiTho = new LayoutParams((int) (((float) getWidthScreen()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT), -2, 2006, 296, -3);
                }
                paramsTaiTho.gravity = 49;
                if (this.mWindowManager == null) {
                    this.mWindowManager = (WindowManager) getSystemService("window");
                }
                this.mWindowManager.addView(this.taithoView, paramsTaiTho);
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "error draw tai tho: " + e.getMessage());
        }
    }

    public void onDestroy() {
        resetAll();
        if (this.receiverScreen != null) {
            unregisterReceiver(this.receiverScreen);
        }
        this.receiverScreen = null;
        chatHeadService = null;
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    private int getWidthScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (this.mWindowManager == null) {
            this.mWindowManager = (WindowManager) getSystemService("window");
        }
        this.mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @SuppressLint("WrongConstant")
    private void addLockScreen(boolean withOutCheckScreen) {
        if (Home.launcher != null) {
            Home.launcher.resetFirstShowPopup();
        }
        if (this.phoneStateListener == null) {
            this.phoneStateListener = new PhoneStateListener() {
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged(signalStrength);
                    try {
                        ChatHeadService.this.signalStrengthLevel = signalStrength.getGsmSignalStrength();
                        if (ChatHeadService.this.lockScreen != null && ChatHeadService.this.lockScreen.isShown()) {
                            ChatHeadService.this.lockScreen.updateSignalStrength(ChatHeadService.this.signalStrengthLevel);
                        }
                    } catch (Exception e) {
                    }
                }

                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                        case 0:
                            com.benny.openlauncher.base.utils.Log.w("TelephonyManager.CALL_STATE_IDLE");
                            if (Setup.appSettings().isLockScreenEnable()) {
                                ChatHeadService.this.addLockScreen(true);
                                break;
                            }
                            break;
                        case 1:
                            com.benny.openlauncher.base.utils.Log.w("TelephonyManager.CALL_STATE_RINGING");
                            try {
                                if (ChatHeadService.this.lockScreen != null) {
                                    ChatHeadService.this.lockScreen.stopUpdateTime();
                                    ChatHeadService.this.lockScreen.setVisibility(8);
                                    break;
                                }
                            } catch (Exception e) {
                                Log.e("HuyAnh", "error unlock screen: " + e.getMessage());
                                break;
                            }
                            break;
                        case 2:
                            com.benny.openlauncher.base.utils.Log.w("TelephonyManager.CALL_STATE_OFFHOOK");
                            try {
                                if (ChatHeadService.this.lockScreen != null) {
                                    ChatHeadService.this.lockScreen.stopUpdateTime();
                                    ChatHeadService.this.lockScreen.setVisibility(8);
                                    break;
                                }
                            } catch (Exception e2) {
                                Log.e("HuyAnh", "error unlock screen: " + e2.getMessage());
                                break;
                            }
                            break;
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
        }
        if (this.lockScreen == null) {
            LayoutParams lockScreenParams;
            Log.d("HuyAnh", "create and add lock screen");
            this.lockScreen = new LockScreen(getApplicationContext(), new LockScreenAction() {
                public void unLock() {
                    Log.d("HuyAnh", "unLock screen");
                    try {
                        if (ChatHeadService.this.lockScreen != null) {
                            ChatHeadService.this.lockScreen.stopUpdateTime();
                            ChatHeadService.this.lockScreen.setVisibility(8);
                        }
                        Utils.vibrate(ChatHeadService.this.getApplicationContext());
                    } catch (Exception e) {
                        Log.e("HuyAnh", "error unlock screen: " + e.getMessage());
                    }
                    if (ChatHeadService.this.phoneStateListener != null) {
                        try {
                            ((TelephonyManager) ChatHeadService.this.getApplication().getSystemService("phone")).listen(ChatHeadService.this.phoneStateListener, 0);
                        } catch (Exception e2) {
                        }
                    }
                }
            });
            if (VERSION.SDK_INT >= 26) {
                lockScreenParams = new LayoutParams(-1, -1, 2038, 256, -3);
            } else {
                lockScreenParams = new LayoutParams(-1, -1, 2010, 256, -3);
            }
            if (this.mWindowManager == null) {
                this.mWindowManager = (WindowManager) getSystemService("window");
            }
            this.mWindowManager.addView(this.lockScreen, lockScreenParams);
            this.lockScreen.startUpdateTime();
            this.lockScreen.update(this.signalStrengthLevel, this.defaultIntentBattery);
            ((TelephonyManager) getApplication().getSystemService("phone")).listen(this.phoneStateListener, 288);
        } else if ((withOutCheckScreen || !isScreenOn(getApplication())) && !this.lockScreen.isShown()) {
            Log.d("HuyAnh", "visible lock screen");
            this.lockScreen.setVisibility(0);
            this.lockScreen.resetViewpager();
            this.lockScreen.startUpdateTime();
            this.lockScreen.update(this.signalStrengthLevel, this.defaultIntentBattery);
            ((TelephonyManager) getApplication().getSystemService("phone")).listen(this.phoneStateListener, 288);
        }
    }

    private void startRun() {
        try {
            resetAll();
            scheduleCheckDialogPermission(0.5f, Tool.DEFAULT_BACKOFF_MULT);
        } catch (Exception e) {
        }
    }

    private void scheduleCheckDialogPermission(float delay, float period) {
        this.mTimer = new Timer();
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
        }
        this.mTimerTask = new TimerTask() {
            public void run() {
                try {
                    if (ChatHeadService.chatHeadService == null || !ChatHeadService.this.isScreenOn(ChatHeadService.this.getApplicationContext())) {
                        ChatHeadService.this.resetAll();
                        return;
                    }
                    String packageNow = ChatHeadService.this.getPakageCurrent();
                    if (!TextUtils.isEmpty(packageNow)) {
                        if (packageNow.contains(".android.packageinstaller")) {
                            ChatHeadService.this.handler.post(new Runnable() {
                                public void run() {
                                    ChatHeadService.this.hideTouchView();
                                    try {
                                        if (ChatHeadService.this.taithoView != null && ChatHeadService.this.taithoView.isShown()) {
                                            ChatHeadService.this.taithoView.setVisibility(View.GONE);
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            });
                        } else {
                            ChatHeadService.this.handler.post(new Runnable() {
                                public void run() {
                                    ChatHeadService.this.showTouchView();
                                    try {
                                        if (Setup.appSettings().isIpornX() && ChatHeadService.this.orientationCurrent == 1 && ChatHeadService.this.taithoView != null && !ChatHeadService.this.taithoView.isShown()) {
                                            ChatHeadService.this.taithoView.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            });
                        }
                    }
                    try {
                        if (ChatHeadService.this.lockScreen != null && ChatHeadService.this.lockScreen.isShown()) {
                            ChatHeadService.this.handler.post(new Runnable() {
                                public void run() {
                                    ChatHeadService.this.lockScreen.updateInternet();
                                }
                            });
                        }
                    } catch (Exception e) {
                    }
                } catch (Exception e2) {
                    Log.e("HuyAnh", "error scheduleCheckDialogPermission: " + e2.getMessage());
                    ChatHeadService.this.resetAll();
                }
            }
        };
        this.mTimer.schedule(this.mTimerTask, ((long) delay) * 1000, ((long) period) * 1000);
    }

    private void resetAll() {
        try {
            if (this.mTimerTask != null) {
                this.mTimerTask.cancel();
            }
            this.mTimerTask = null;
        } catch (Exception e) {
        }
        try {
            if (this.mTimer != null) {
                this.mTimer.cancel();
            }
            this.mTimer = null;
        } catch (Exception e2) {
        }
    }

    private boolean isScreenOn(Context context) {
        @SuppressLint("WrongConstant") PowerManager pm = (PowerManager) context.getSystemService("power");
        if (VERSION.SDK_INT < 20) {
            return pm.isScreenOn();
        }
        return pm.isInteractive();
    }

    private String getPakageCurrent() {
        if (VERSION.SDK_INT < 21) {
            try {
                @SuppressLint("WrongConstant") List<RunningTaskInfo> taskInfo = ((ActivityManager) getSystemService("activity")).getRunningTasks(1);
                if (taskInfo != null) {
                    ComponentName componentInfo = ((RunningTaskInfo) taskInfo.get(0)).topActivity;
                    if (componentInfo.getPackageName() != null) {
                        return componentInfo.getPackageName();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (VERSION.SDK_INT == 21) {
            RunningAppProcessInfo currentInfo = null;
            Field field = null;
            try {
                field = RunningAppProcessInfo.class.getDeclaredField("processState");
            } catch (Exception e2) {
            }
            try {
                @SuppressLint("WrongConstant") List<RunningAppProcessInfo> appList = ((ActivityManager) getSystemService("activity")).getRunningAppProcesses();
                for (RunningAppProcessInfo app : appList) {
                    if (app.importance == 100 && app.importanceReasonCode == 0) {
                        Integer state = null;
                        try {
                            state = Integer.valueOf(field.getInt(app));
                        } catch (Exception e3) {
                        }
                        if (state == null) {
                            continue;
                        } else if (state.intValue() == 2) {
                            currentInfo = app;
                            break;
                        }
                    }
                }
                if (currentInfo == null && appList.size() > 1) {
                    currentInfo = (RunningAppProcessInfo) appList.get(0);
                }
                return currentInfo.processName;
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        } else if (VERSION.SDK_INT >= 22) {
            String mpackageName = null;
            try {
                @SuppressLint("WrongConstant") UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
                long time = System.currentTimeMillis();
                List<UsageStats> appList2 = usm.queryUsageStats(0, time - 18000000, time);
                if (appList2 != null && appList2.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap();
                    for (UsageStats usageStats : appList2) {
                        mySortedMap.put(Long.valueOf(usageStats.getLastTimeUsed()), usageStats);
                    }
                    if (!mySortedMap.isEmpty()) {
                        mpackageName = ((UsageStats) mySortedMap.get(mySortedMap.lastKey())).getPackageName();
                    }
                }
            } catch (Exception e5) {
            }
            if (!TextUtils.isEmpty(mpackageName)) {
                return mpackageName;
            }
        }
        return "XXX";
    }

    @RequiresApi(api = 19)
    public void addNotification(StatusBarNotification sbn) {
        try {
            if (this.lockScreen != null && this.lockScreen.isShown()) {
                this.lockScreen.addNotification(sbn);
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "error chatheadservice addNotification: " + e.getMessage());
        }
    }

    @RequiresApi(api = 19)
    public void removeNotification(StatusBarNotification sbn) {
        try {
            if (this.lockScreen != null && this.lockScreen.isShown()) {
                this.lockScreen.removeNotification(sbn);
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "error chatheadservice removeNotification: " + e.getMessage());
        }
    }
}
