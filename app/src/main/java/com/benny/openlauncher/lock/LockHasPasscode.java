package com.benny.openlauncher.lock;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benny.openlauncher.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.Calendar;

public class LockHasPasscode extends FragmentActivity implements OnClickListener {
    private static final int EXCELLENT_LEVEL = 75;
    private static final int GOOD_LEVEL = 50;
    static int IDLE = 0;
    private static final int MODERATE_LEVEL = 25;
    private static final int WEAK_LEVEL = 0;
    static CustomPhoneStateListener customPhoneListener;
    static boolean isCallRegister = false;
    public static boolean isfirst = true;
    private static RelativeLayout layout = null;
    public static LockHasPasscode lockHasPasscode = null;
    private static WindowManager mWindowManager;
    static TelephonyManager telephony;
    private Bitmap blur;
    Broadcast broadcast;
    private ImageView btn0;
    private ImageView btn1;
    private ImageView btn2;
    private ImageView btn3;
    private ImageView btn4;
    private ImageView btn5;
    private ImageView btn6;
    private ImageView btn7;
    private ImageView btn8;
    private ImageView btn9;
    private TextViewSanFranciscoLight btnCancel;
    private StringBuilder code;
    private Handler handler = new Handler();
    private boolean hasPassCode = true;
    private ImageView imgBackground;
    private ImageView imgBattery;
    private ImageView imgCharging;
    private ImageView imgLockPassCode0;
    private ImageView imgLockPassCode1;
    private ImageView imgLockPassCode2;
    private ImageView imgLockPassCode3;
    private ImageView imgSignal;
    private ImageView imgWifi;
    private String key;
    int lastPage;
    private LinearLayout layoutPassCodeLock;
    private int mScreenWidth = 0;
    private ViewPager pager;
    PhoneStateListener phoneStateListener = new PhoneStateListener();
    private Runnable resetTime = new Runnable() {
        public void run() {
            LockHasPasscode.this.setTime();
            LockHasPasscode.this.handler.postDelayed(this, 1000);
        }
    };
    private View screen;
    private SharedPreferences sharedPreferences;
    private Shimmer shimmer;
    TelephonyManager telephonyManager;
    private TextViewSanFranciscoLight tvBattery;
    private TextViewSanFranciscoLight tvDate;
    private TextView tvEmerency;
    private TextViewSanFranciscoThin tvFormat;
    private TextViewSanFranciscoLight tvOperatorName;
    private ShimmerTextView tvShimmer;
    private TextViewSanFranciscoThin tvTime;
    private TextViewSanFranciscoLight tvTitle;
    private View vLock;
    private View vPassCode;
    private LayoutParams wmParams;

    class Broadcast extends BroadcastReceiver {
        Broadcast() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.d("HuyAnh", "onReceive BroadcastReceiver LockHasPasscode: " + intent.getAction());
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                int intExtra = intent.getIntExtra("level", 0);
                int plugin = intent.getIntExtra("plugged", 0);
                LockHasPasscode.this.tvBattery.setText(intExtra + "%");
                if (plugin != 0) {
                    LockHasPasscode.this.imgCharging.setVisibility(View.VISIBLE);
                    LockHasPasscode.this.charging(true, intExtra);
                    return;
                }
                LockHasPasscode.this.imgCharging.setVisibility(View.GONE);
                LockHasPasscode.this.charging(false, intExtra);
            }
        }
    }

    class C02304 implements Runnable {
        C02304() {
        }

        public void run() {
            LockHasPasscode.this.pager.setCurrentItem(1, true);
        }
    }

    class CustomPhoneStateListener extends PhoneStateListener {
        private static final String TAG = "CustomPhoneStateListener";

        CustomPhoneStateListener() {
        }

        @SuppressLint("WrongConstant")
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.v("HuyAnh", "onCallStateChanged: " + state);
            switch (state) {
                case 0:
                    Log.e("HuyAnh", "CALL_STATE_IDLE");
                    if (!LockHasPasscode.isfirst) {
                        Intent intent1 = new Intent(LockHasPasscode.this, LockHasPasscode.class);
                        intent1.setFlags(268435456);
                        LockHasPasscode.this.startActivity(intent1);
                        Log.e("HuyAnh", "startactivitylock");
                        LockHasPasscode.isfirst = true;
                        if (LockHasPasscode.telephony != null && LockHasPasscode.customPhoneListener != null) {
                            LockHasPasscode.telephony.listen(LockHasPasscode.customPhoneListener, 0);
                            return;
                        }
                        return;
                    }
                    return;
                case 1:
                    Log.e("HuyAnh", "CALL_STATE_RINGING");
                    LockHasPasscode.this.unlock();
                    LockHasPasscode.isfirst = false;
                    return;
                case 2:
                    Log.e("HuyAnh", "CALL_STATE_OFFHOOK");
                    return;
                default:
                    return;
            }
        }
    }

    public class MyPagerAdapter extends PagerAdapter {
        Activity context;

        public MyPagerAdapter(Activity context) {
            this.context = context;
        }

        public int getCount() {
            return 2;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup collection, int position) {
            collection.addView(LockHasPasscode.this.vLock, 0);
            collection.addView(LockHasPasscode.this.vPassCode, 1);
            switch (position) {
                case 0:
                    return LockHasPasscode.this.vPassCode;
                case 1:
                    return LockHasPasscode.this.vLock;
                default:
                    return LockHasPasscode.this.vLock;
            }
        }

        public int getItemPosition(Object obj) {
            if (obj == LockHasPasscode.this.vPassCode) {
                return 0;
            }
            return obj == LockHasPasscode.this.vLock ? 1 : -1;
        }

        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }

    class PagerChangeItem1 implements Runnable {
        PagerChangeItem1() {
        }

        public void run() {
            LockHasPasscode.this.pager.setCurrentItem(1, true);
        }
    }

    class PhoneState extends PhoneStateListener {
        int MAX_SIGNAL_DBM_VALUE = 31;

        PhoneState() {
        }

        public void onSignalStrengthChanged(int asu) {
            LockHasPasscode.this.setSignalLevel(asu);
            super.onSignalStrengthChanged(asu);
        }
    }

    class ViewPagerListener implements OnPageChangeListener {
        private boolean checkDirection;
        private boolean scrollStarted;
        public float thresholdOffset;

        ViewPagerListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            if (position != 1) {
                LockHasPasscode.this.resetTextandPassCode();
            }
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    class animation implements AnimatorListener {
        animation() {
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
        }

        public void onAnimationCancel(Animator animation) {
        }

        public void onAnimationRepeat(Animator animation) {
        }
    }

    private void resetTextandPassCode() {
        this.code = new StringBuilder();
        setImageCode();
        this.tvTitle.setText(getResources().getString(R.string.enter_passcode));
    }

    public boolean isPermissionOverlay() {
        if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
            return true;
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    private boolean isScreenOn() {
        if (VERSION.SDK_INT < 20) {
            return ((PowerManager) getSystemService("power")).isScreenOn();
        }
        boolean screenOn = false;
        for (Display display : ((DisplayManager) getSystemService("power")).getDisplays()) {
            if (display.getState() != 1) {
                screenOn = true;
            }
        }
        return screenOn;
    }

    @SuppressLint("WrongConstant")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockHasPasscode = this;
        if (isScreenOn()) {
            Log.v("HuyAnh", "--------- isScreenOn: true");
            lockHasPasscode = null;
            finish();
        } else if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
            layout = (RelativeLayout) View.inflate(this, R.layout.activity_lock2, null);
            this.pager = (ViewPager) layout.findViewById(R.id.pager);
            this.pager.setOffscreenPageLimit(2);
            this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            this.hasPassCode = this.sharedPreferences.getBoolean(Values.ENABLE_PASSCODE, true);
            this.key = this.sharedPreferences.getString(Values.PASSCODE, "");
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.vPassCode = layoutInflater.inflate(R.layout.fragment_lock_passcode, null);
            this.tvTitle = (TextViewSanFranciscoLight) this.vPassCode.findViewById(R.id.tvTitle);
            this.vLock = layoutInflater.inflate(R.layout.fragment_lock, null);
            this.layoutPassCodeLock = (LinearLayout) this.vPassCode.findViewById(R.id.layout_passcodelock);
            this.layoutPassCodeLock.setVisibility(0);
            bindView();
            bindViewIphone();
            this.btnCancel = (TextViewSanFranciscoLight) this.vPassCode.findViewById(R.id.btnCancel);
            this.btnCancel.setOnClickListener(this);
            setTvShimmer();
            this.code = new StringBuilder();
            mWindowAddView();
            this.pager.setAdapter(new MyPagerAdapter(this));
            this.pager.post(new PagerChangeItem1());
            this.pager.setCurrentItem(1, true);
            this.pager.setOnPageChangeListener(new ViewPagerListener());
            telephony = (TelephonyManager) getSystemService("phone");
            customPhoneListener = new CustomPhoneStateListener();
            telephony.listen(customPhoneListener, 32);
            Log.e("............", "register");
            IDLE = 0;
            isCallRegister = true;
            this.telephonyManager = (TelephonyManager) getSystemService("phone");
            this.telephonyManager.listen(this.phoneStateListener, 1);
            this.tvOperatorName.setText(this.telephonyManager.getNetworkOperatorName());
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.BATTERY_CHANGED");
            registerReceiver(this.broadcast, filter);
            this.handler.post(this.resetTime);
        } else {
            lockHasPasscode = null;
            finish();
        }
    }

    private void bindViewIphone() {
        this.btn0 = (ImageView) this.vPassCode.findViewById(R.id.btn0);
        this.btn0.setOnClickListener(this);
        this.btn1 = (ImageView) this.vPassCode.findViewById(R.id.btn1);
        this.btn1.setOnClickListener(this);
        this.btn2 = (ImageView) this.vPassCode.findViewById(R.id.btn2);
        this.btn2.setOnClickListener(this);
        this.btn3 = (ImageView) this.vPassCode.findViewById(R.id.btn3);
        this.btn3.setOnClickListener(this);
        this.btn4 = (ImageView) this.vPassCode.findViewById(R.id.btn4);
        this.btn4.setOnClickListener(this);
        this.btn5 = (ImageView) this.vPassCode.findViewById(R.id.btn5);
        this.btn5.setOnClickListener(this);
        this.btn6 = (ImageView) this.vPassCode.findViewById(R.id.btn6);
        this.btn6.setOnClickListener(this);
        this.btn7 = (ImageView) this.vPassCode.findViewById(R.id.btn7);
        this.btn7.setOnClickListener(this);
        this.btn8 = (ImageView) this.vPassCode.findViewById(R.id.btn8);
        this.btn8.setOnClickListener(this);
        this.btn9 = (ImageView) this.vPassCode.findViewById(R.id.btn9);
        this.btn9.setOnClickListener(this);
    }

    private void bindView() {
        this.tvShimmer = (ShimmerTextView) this.vLock.findViewById(R.id.shimmer_tv);
        this.tvDate = (TextViewSanFranciscoLight) this.vLock.findViewById(R.id.tvDate);
        this.tvTime = (TextViewSanFranciscoThin) this.vLock.findViewById(R.id.tvTime);
        this.tvEmerency = (TextView) this.vPassCode.findViewById(R.id.btnEmerency);
        this.tvEmerency.setVisibility(View.INVISIBLE);
        this.imgLockPassCode0 = (ImageView) this.vPassCode.findViewById(R.id.lock_passcode_dot0);
        this.imgLockPassCode1 = (ImageView) this.vPassCode.findViewById(R.id.lock_passcode_dot1);
        this.imgLockPassCode2 = (ImageView) this.vPassCode.findViewById(R.id.lock_passcode_dot2);
        this.imgLockPassCode3 = (ImageView) this.vPassCode.findViewById(R.id.lock_passcode_dot3);
        this.tvFormat = (TextViewSanFranciscoThin) this.vLock.findViewById(R.id.tvFormat);
        this.imgSignal = (ImageView) layout.findViewById(R.id.imgSignal);
        this.imgWifi = (ImageView) layout.findViewById(R.id.imgWifi);
        this.imgBattery = (ImageView) layout.findViewById(R.id.imgBattery);
        this.imgCharging = (ImageView) layout.findViewById(R.id.imgCharging);
        this.tvOperatorName = (TextViewSanFranciscoLight) layout.findViewById(R.id.tvOperatorName);
        this.tvBattery = (TextViewSanFranciscoLight) layout.findViewById(R.id.tvBattery);
        setStatusBar();
        this.imgBackground = (ImageView) layout.findViewById(R.id.imgBackground);
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            if (wallpaperManager.getDrawable() != null) {
                this.imgBackground.setImageDrawable(wallpaperManager.getDrawable());
            }
        } catch (Exception e) {
        }
    }

    private int[] getSizeDevide() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new int[]{width, height};
    }

    private String getpreferences(String paramString) {
        return getSharedPreferences("pref", 0).getString(paramString, "0");
    }

    public void setStatusBar() {
        this.broadcast = new Broadcast();
        if (Utils.wifiConnected(this)) {
            this.imgWifi.setVisibility(View.VISIBLE);
        } else {
            this.imgWifi.setVisibility(View.GONE);
        }
    }

    public void updateTime() {
        Log.d("HuyAnh", "update time");
        if (this.handler == null) {
            this.handler = new Handler();
        }
        this.handler.removeCallbacksAndMessages(null);
        this.handler.post(this.resetTime);
    }

    protected void onResume() {
        super.onResume();
        updateTime();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("HuyAnh", "onNewIntent lock screen: " + intent);
        updateTime();
    }
    @SuppressLint("WrongConstant")
    public void setTime() {
        try {
            Calendar c = Calendar.getInstance();
            int hour = c.get(11);
            int minute = c.get(12);
            int date = c.get(5);
            int dateWeek = c.get(7);
            int month = c.get(2) + 1;
            String minuteString = minute + "";
            if (minuteString.length() == 1) {
                minuteString = "0" + minuteString;
            }
            this.tvFormat.setVisibility(8);
            this.tvTime.setText(hour + ":" + minuteString);
            this.tvDate.setText(Utils.getDateInWeek(this, dateWeek) + ", " + Utils.getMonthString(this, month) + " " + date);
        } catch (Exception e) {
            Log.e("HuyAnh", "error setTime: " + e.getMessage());
        }
    }

    protected void onDestroy() {
        Log.e("..............", "onDestroy HasPass");
        super.onDestroy();
        try {
            this.handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(this.broadcast);
        } catch (Exception e2) {
        }
    }

    private void setImageCode() {
        int size = this.code.length();
        Utils.sound(this, R.raw.type_keyboard);
        switch (size) {
            case 0:
                setDotSoild(0);
                return;
            case 1:
                setDotSoild(1);
                return;
            case 2:
                setDotSoild(2);
                return;
            case 3:
                setDotSoild(3);
                return;
            case 4:
                setDotSoild(4);
                if (this.code.toString().equals(this.key)) {
                    unlock();
                    if (telephony != null && customPhoneListener != null) {
                        telephony.listen(customPhoneListener, 0);
                        Log.e("................", "unregister");
                        isCallRegister = false;
                        return;
                    }
                    return;
                }
                this.tvTitle.setText(getResources().getString(R.string.try_again));
                Utils.vibrateTime(this, 400);
                this.code = new StringBuilder();
                Utils.shake(this, this.imgLockPassCode0, this.imgLockPassCode1, this.imgLockPassCode2, this.imgLockPassCode3);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        LockHasPasscode.this.setImageCode();
                    }
                }, 1000);
                return;
            default:
                return;
        }
    }

    private void setDotSoild(int numberDot) {
        switch (numberDot) {
            case 0:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_hollow);
                return;
            case 1:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_hollow);
                return;
            case 2:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_hollow);
                return;
            case 3:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_hollow);
                return;
            case 4:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_soild);
                return;
            default:
                return;
        }
    }

    public void unlock() {
        WindowManager windowManager = mWindowManager;
        if (this.sharedPreferences.getBoolean(Values.ENABLE_VIBRATE, true)) {
            Utils.vibrate(this);
        }
        if (this.sharedPreferences.getBoolean(Values.ENABLE_SOUND, true)) {
            Utils.sound(this, R.raw.unlock);
        }
        if (windowManager != null) {
            windowManager.removeView(layout);
        }
        mWindowManager = null;
        finish();
        lockHasPasscode = null;
    }

    private void setTvShimmer() {
        this.shimmer = new Shimmer();
        this.shimmer.setRepeatCount(-1).setDuration(2000).setStartDelay(500).setDirection(0).setAnimatorListener(new animation());
        this.tvShimmer.setText(this.sharedPreferences.getString(Values.UNLOCK_TEXT, getString(R.string.slidetounlock)));
        this.tvShimmer.setTypeface(Utils.getTypefaceRobotoLight(this));
        this.shimmer.start(this.tvShimmer);
    }

    @SuppressLint("WrongConstant")
    private void mWindowAddView() {
        mWindowManager = (WindowManager) getApplicationContext().getSystemService("window");
        if (VERSION.SDK_INT >= 26) {
            this.wmParams = new LayoutParams(-1, -1, 0, 0, 2038, 4718592, -2);
        } else {
            this.wmParams = new LayoutParams(-1, -1, 0, 0, 2010, 4718592, -2);
        }
        if (VERSION.SDK_INT > 15) {
            layout.setSystemUiVisibility(770);
        }
        if (VERSION.SDK_INT > 18) {
            layout.setSystemUiVisibility(4866);
        }
        if (VERSION.SDK_INT == 15) {
            layout.setSystemUiVisibility(2);
        }
        mWindowManager.addView(layout, this.wmParams);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0:
                this.code.append(0);
                setImageCode();
                return;
            case R.id.btn1:
                this.code.append(1);
                setImageCode();
                return;
            case R.id.btn2:
                this.code.append(2);
                setImageCode();
                return;
            case R.id.btn3:
                this.code.append(3);
                setImageCode();
                return;
            case R.id.btn4:
                this.code.append(4);
                setImageCode();
                return;
            case R.id.btn5:
                this.code.append(5);
                setImageCode();
                return;
            case R.id.btn6:
                this.code.append(6);
                setImageCode();
                return;
            case R.id.btn7:
                this.code.append(7);
                setImageCode();
                return;
            case R.id.btn8:
                this.code.append(8);
                setImageCode();
                return;
            case R.id.btn9:
                this.code.append(9);
                setImageCode();
                return;
            case R.id.btnCancel:
                if (this.code.length() > 0) {
                    this.code.deleteCharAt(this.code.length() - 1);
                    setImageCode();
                    return;
                }
                this.pager.post(new C02304());
                return;
            default:
                return;
        }
    }

    public void charging(boolean charging, int intExtra) {
        if (charging) {
            if (intExtra <= 10) {
                this.imgBattery.setImageResource(R.drawable.battery_10_green);
            } else if (intExtra > 10 && intExtra <= 20) {
                this.imgBattery.setImageResource(R.drawable.battery_20_green);
            } else if (intExtra > 20 && intExtra <= 35) {
                this.imgBattery.setImageResource(R.drawable.battery_35_green);
            } else if (intExtra > 35 && intExtra <= 50) {
                this.imgBattery.setImageResource(R.drawable.battery_50_green);
            } else if (intExtra > 50 && intExtra <= 75) {
                this.imgBattery.setImageResource(R.drawable.battery_75_green);
            } else if (intExtra > 75 && intExtra <= 90) {
                this.imgBattery.setImageResource(R.drawable.battery_90_green);
            } else if (intExtra > 90) {
                this.imgBattery.setImageResource(R.drawable.battery_full_green);
            }
        } else if (intExtra <= 10) {
            this.imgBattery.setImageResource(R.drawable.battery_10);
        } else if (intExtra > 10 && intExtra <= 20) {
            this.imgBattery.setImageResource(R.drawable.battery_20);
        } else if (intExtra > 20 && intExtra <= 35) {
            this.imgBattery.setImageResource(R.drawable.battery_35);
        } else if (intExtra > 35 && intExtra <= 50) {
            this.imgBattery.setImageResource(R.drawable.battery_50);
        } else if (intExtra > 50 && intExtra <= 75) {
            this.imgBattery.setImageResource(R.drawable.battery_75);
        } else if (intExtra > 75 && intExtra <= 90) {
            this.imgBattery.setImageResource(R.drawable.battery_90);
        } else if (intExtra > 90) {
            this.imgBattery.setImageResource(R.drawable.battery_full);
        }
        try {
            mWindowManager.updateViewLayout(layout, this.wmParams);
        } catch (Exception e) {
        }
        layout.postInvalidate();
    }

    public void setImgSignal(int level) {
        this.imgSignal.setImageResource(new int[]{R.drawable.phone_signal_1, R.drawable.phone_signal_2, R.drawable.phone_signal_3, R.drawable.signal_4, R.drawable.phone_signal_4}[level - 1]);
    }

    @SuppressLint("WrongConstant")
    private void setSignalLevel(int level) {
        int progress = (int) ((((double) ((float) level)) / 31.0d) * 100.0d);
        if (progress > 75) {
            this.imgSignal.setImageResource(R.drawable.phone_signal_5);
        } else if (progress > 50) {
            this.imgSignal.setImageResource(R.drawable.phone_signal_4);
        } else if (progress > 25) {
            this.imgSignal.setImageResource(R.drawable.phone_signal_3);
        } else if (progress > 0) {
            this.imgSignal.setImageResource(R.drawable.phone_signal_2);
        } else if (progress <= 0) {
            this.imgSignal.setVisibility(8);
            this.tvOperatorName.setText(getString(R.string.paddy_no_signal));
        }
    }
}
