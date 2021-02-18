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
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
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
import android.widget.RelativeLayout;

import com.benny.openlauncher.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.Calendar;

public class LockNoPasscode extends FragmentActivity implements OnClickListener {
    public static final String TAG = "LockNoPasscode";
    private static boolean isCall = false;
    public static boolean isNotFirst = true;
    public static boolean isfirst = true;
    public static LockNoPasscode lockNoPasscode = null;
    static int number;
    Broadcast broadcast;
    CustomPhoneStateListener customPhoneListener;
    private Handler handler = new Handler();
    private boolean hasPassCode = false;
    private ImageView imgBackground;
    private ImageView imgBattery;
    private ImageView imgCharging;
    private ImageView imgSignal;
    private ImageView imgWifi;
    private RelativeLayout layout = null;
    private int mScreenWidth = 0;
    private WindowManager mWindowManager = null;
    private ViewPager pager;
    PhoneStateListener phoneStateListener = new PhoneState();
    private Runnable resetTime = new Runnable() {
        public void run() {
            LockNoPasscode.this.setTime();
            LockNoPasscode.this.handler.postDelayed(this, 1000);
        }
    };
    private View screen;
    private SharedPreferences sharedPreferences;
    private Shimmer shimmer;
    TelephonyManager telephony;
    TelephonyManager telephonyManager;
    private TextViewSanFranciscoLight tvBattery;
    private TextViewSanFranciscoLight tvDate;
    private TextViewSanFranciscoThin tvFormat;
    private TextViewSanFranciscoLight tvOperatorName;
    private ShimmerTextView tvShimmer;
    private TextViewSanFranciscoThin tvTime;
    private View vLock;
    private View vPassCode;
    private LayoutParams wmParams;

    class Broadcast extends BroadcastReceiver {
        Broadcast() {
        }

        @SuppressLint("WrongConstant")
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                int plugin = intent.getIntExtra("plugged", -1);
                int level = intent.getIntExtra("level", -1);
                LockNoPasscode.this.tvBattery.setText(level + "%");
                if (plugin != 0) {
                    LockNoPasscode.this.imgCharging.setVisibility(0);
                    LockNoPasscode.this.charging(true, level);
                    return;
                }
                LockNoPasscode.this.imgCharging.setVisibility(8);
                LockNoPasscode.this.charging(false, level);
            }
        }
    }

    public class CustomPhoneStateListener extends PhoneStateListener {
        private static final String TAG = "CustomPhoneStateListener";

        @SuppressLint("WrongConstant")
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.v("HuyAnh", "onCallStateChanged: " + state);
            switch (state) {
                case 0:
                    Log.e("HuyAnh", "CALL_STATE_IDLE ");
                    if (!LockNoPasscode.isfirst) {
                        Log.e("HuyAnh", "CALL_STATE_IDLE startactivity ");
                        Intent intent1 = new Intent(LockNoPasscode.this, LockNoPasscode.class);
                        intent1.setFlags(268435456);
                        LockNoPasscode.this.startActivity(intent1);
                        LockNoPasscode.isfirst = true;
                        if (LockNoPasscode.this.telephony != null && LockNoPasscode.this.customPhoneListener != null) {
                            LockNoPasscode.this.telephony.listen(LockNoPasscode.this.customPhoneListener, 0);
                            return;
                        }
                        return;
                    }
                    return;
                case 1:
                    Log.e("HuyAnh", "CALL_STATE_RINGING");
                    LockNoPasscode.this.unlock();
                    LockNoPasscode.isfirst = false;
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
            collection.addView(LockNoPasscode.this.vLock, 0);
            collection.addView(LockNoPasscode.this.vPassCode, 1);
            switch (position) {
                case 0:
                    return LockNoPasscode.this.vPassCode;
                case 1:
                    return LockNoPasscode.this.vLock;
                default:
                    return LockNoPasscode.this.vLock;
            }
        }

        public int getItemPosition(Object obj) {
            if (obj == LockNoPasscode.this.vPassCode) {
                return 0;
            }
            return obj == LockNoPasscode.this.vLock ? 1 : -1;
        }

        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }

    class PagerChange implements Runnable {
        PagerChange() {
        }

        public void run() {
            LockNoPasscode.this.pager.setCurrentItem(1, false);
        }
    }

    class PagerChangeListener implements OnPageChangeListener {
        boolean start = false;

        PagerChangeListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.e(".............", positionOffset + " " + position);
        }

        public void onPageSelected(int position) {
            Log.e(".............", position + "");
            if (position == 0) {
                LockNoPasscode.this.unlock();
                if (LockNoPasscode.this.telephony != null && LockNoPasscode.this.customPhoneListener != null) {
                    LockNoPasscode.this.telephony.listen(LockNoPasscode.this.customPhoneListener, 0);
                }
            }
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    class PhoneState extends PhoneStateListener {
        int MAX_SIGNAL_DBM_VALUE = 31;

        PhoneState() {
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.isGsm()) {
                LockNoPasscode.this.setImgSignal(calculateSignalStrengthInPercent(signalStrength.getGsmSignalStrength()));
            }
        }

        private int calculateSignalStrengthInPercent(int signalStrength) {
            return (int) ((((float) signalStrength) / ((float) this.MAX_SIGNAL_DBM_VALUE)) * 100.0f);
        }
    }

    class TexViewAnimator implements AnimatorListener {
        TexViewAnimator() {
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

    @SuppressLint("WrongConstant")
    private boolean isScreenOn() {
        if (VERSION.SDK_INT < 20) {
            return ((PowerManager) getSystemService("power")).isScreenOn();
        }
        boolean screenOn = false;
        for (Display display : ((DisplayManager) getSystemService("display")).getDisplays()) {
            if (display.getState() != 1) {
                screenOn = true;
            }
        }
        return screenOn;
    }
    @SuppressLint("WrongConstant")
    protected void onCreate(Bundle savedInstanceState) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.hasPassCode = this.sharedPreferences.getBoolean(Values.ENABLE_PASSCODE, false);
        super.onCreate(savedInstanceState);
        lockNoPasscode = this;
        if (isScreenOn()) {
            Log.v("HuyAnh", "--------- isScreenOn: true");
            lockNoPasscode = null;
            finish();
        } else if (VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
            this.layout = (RelativeLayout) View.inflate(this, R.layout.activity_lock2, null);
            this.pager = (ViewPager) this.layout.findViewById(R.id.pager);
            this.pager.setAdapter(new MyPagerAdapter(this));
            this.pager.setCurrentItem(1, true);
            this.pager.post(new PagerChange());
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
            this.vPassCode = layoutInflater.inflate(R.layout.view_null, null);
            this.vLock = layoutInflater.inflate(R.layout.fragment_lock, null);
            bindView();
            setTvShimmer();
            mWindowAddView();
            this.pager.setOnPageChangeListener(new PagerChangeListener());
            this.telephony = (TelephonyManager) getSystemService("phone");
            this.customPhoneListener = new CustomPhoneStateListener();
            this.telephony.listen(this.customPhoneListener, 32);
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.BATTERY_CHANGED");
            registerReceiver(this.broadcast, filter);
            this.handler.post(this.resetTime);
        } else {
            Log.d("HuyAnh", "canDrawOverlays LockNoPasscode false");
            lockNoPasscode = null;
            finish();
        }
    }

    private void bindView() {
        this.tvShimmer = (ShimmerTextView) this.vLock.findViewById(R.id.shimmer_tv);
        this.tvDate = (TextViewSanFranciscoLight) this.vLock.findViewById(R.id.tvDate);
        this.tvTime = (TextViewSanFranciscoThin) this.vLock.findViewById(R.id.tvTime);
        this.imgSignal = (ImageView) this.layout.findViewById(R.id.imgSignal);
        this.imgWifi = (ImageView) this.layout.findViewById(R.id.imgWifi);
        this.imgBattery = (ImageView) this.layout.findViewById(R.id.imgBattery);
        this.imgCharging = (ImageView) this.layout.findViewById(R.id.imgCharging);
        this.tvOperatorName = (TextViewSanFranciscoLight) this.layout.findViewById(R.id.tvOperatorName);
        this.tvBattery = (TextViewSanFranciscoLight) this.layout.findViewById(R.id.tvBattery);
        setStatusBar();
        this.imgBackground = (ImageView) this.layout.findViewById(R.id.imgBackground);
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            if (wallpaperManager.getDrawable() != null) {
                this.imgBackground.setImageDrawable(wallpaperManager.getDrawable());
            }
        } catch (Exception e) {
        }
        this.tvFormat = (TextViewSanFranciscoThin) this.vLock.findViewById(R.id.tvFormat);
    }

    private int[] getSizeDevide() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new int[]{width, height};
    }

    @SuppressLint("WrongConstant")
    public void setStatusBar() {
        this.telephonyManager = (TelephonyManager) getSystemService("phone");
        this.telephonyManager.listen(this.phoneStateListener, 1);
        this.tvOperatorName.setText(this.telephonyManager.getNetworkOperatorName());
        this.broadcast = new Broadcast();
        if (Utils.wifiConnected(this)) {
            this.imgWifi.setVisibility(View.VISIBLE);
        } else {
            this.imgWifi.setVisibility(View.GONE);
        }
    }

    private String getpreferences(String paramString) {
        return getSharedPreferences("pref", 0).getString(paramString, "0");
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
        Log.d("HuyAnh", "onResume LockNoPasscode");
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

    @SuppressLint("WrongConstant")
    public void unlock() {
        if (this.sharedPreferences.getBoolean(Values.ENABLE_VIBRATE, true)) {
            Utils.vibrate(this);
        }
        if (this.sharedPreferences.getBoolean(Values.ENABLE_SOUND, true)) {
            Utils.sound(this, R.raw.unlock);
        }
        try {
            if (this.mWindowManager != null) {
                this.mWindowManager.removeView(this.layout);
            }
        } catch (Exception e) {
            try {
                this.mWindowManager = (WindowManager) getApplicationContext().getSystemService("window");
                this.mWindowManager.removeView(this.layout);
            } catch (Exception e2) {
            }
        }
        this.mWindowManager = null;
        lockNoPasscode = null;
        Log.d("HuyAnh", "unlock LockNoPasscode");
        finish();
    }

    private void setTvShimmer() {
        this.shimmer = new Shimmer();
        this.shimmer.setRepeatCount(-1).setDuration(2000).setStartDelay(1000).setDirection(0).setAnimatorListener(new TexViewAnimator());
        this.tvShimmer.setText(this.sharedPreferences.getString(Values.UNLOCK_TEXT, getString(R.string.slidetounlock)));
        this.tvShimmer.setTypeface(Utils.getTypefaceRobotoLight(this));
        this.shimmer.start(this.tvShimmer);
    }

    @SuppressLint("WrongConstant")
    private void mWindowAddView() {
        this.mWindowManager = (WindowManager) getApplicationContext().getSystemService("window");
        if (VERSION.SDK_INT >= 26) {
            this.wmParams = new LayoutParams(-1, -1, 0, 0, 2038, 4718592, -2);
        } else {
            this.wmParams = new LayoutParams(-1, -1, 0, 0, 2010, 4718592, -2);
        }
        if (VERSION.SDK_INT > 15) {
            this.layout.setSystemUiVisibility(770);
        }
        if (VERSION.SDK_INT > 18) {
            this.layout.setSystemUiVisibility(4866);
        }
        if (VERSION.SDK_INT == 15) {
            this.layout.setSystemUiVisibility(2);
        }
        this.mWindowManager.addView(this.layout, this.wmParams);
    }

    public void onClick(View v) {
        v.getId();
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
            this.mWindowManager.updateViewLayout(this.layout, this.wmParams);
            Log.d(TAG, "UpdateView");
        } catch (Exception e) {
            Log.d(TAG, "" + e.toString());
        }
        this.layout.postInvalidate();
    }

    public void setImgSignal(int level) {
        this.imgSignal.setImageResource(new int[]{R.drawable.phone_signal_1, R.drawable.phone_signal_2, R.drawable.phone_signal_3, R.drawable.signal_4, R.drawable.phone_signal_4}[level - 1]);
    }
}
