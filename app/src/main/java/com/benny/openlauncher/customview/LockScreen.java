package com.benny.openlauncher.customview;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.App;
import com.benny.openlauncher.R;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.Tool;


public class LockScreen extends RelativeLayout {
    private App application;
    @BindView(R.id.ibBgLockScreen)
    ImageView ibBgLockScreen;
    @BindView(R.id.rlStatusBar_ivBattery)
    ImageView ivBattery;
    @BindView(R.id.rlStatusBar_ivInternet)
    ImageView ivInternet;
    @BindView(R.id.rlStatusBar_ivLocation)
    ImageView ivLocation;
    @BindView(R.id.rlStatusBar_ivSignalStrength)
    ImageView ivSignalStrength;
    @BindView(R.id.rlStatusBar_ivTaiTho)
    ImageView ivTaiTho;
    private LockScreenAction lockScreenAction = null;
    private LockScreenAdapter lockScreenAdapter = null;
    @BindView(R.id.rlStatusBar)
    RelativeLayout rlStatusBar;
    @BindView(R.id.rlStatusBar_tvBattery)
    TextView tvBattery;
    @BindView(R.id.rlStatusBar_tvInternet)
    TextView tvInternet;
    @BindView(R.id.rlStatusBar_tvNetwork)
    TextView tvNetwork;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    public LockScreen(Context context, LockScreenAction lockScreenAction) {
        super(context);
        this.lockScreenAction = lockScreenAction;
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.view_lock_screen, null);
        addView(view);
        ButterKnife.bind((Object) this, view);
        this.application = (App) getContext().getApplicationContext();
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
            if (wallpaperManager.getDrawable() != null) {
                this.ibBgLockScreen.setImageDrawable(wallpaperManager.getDrawable());
            }
        } catch (Exception e) {
            Log.e("error set bg lock screen");
        }
        this.lockScreenAdapter = new LockScreenAdapter(getContext());
        this.lockScreenAdapter.getLockScreenMain().setLockScreenMainListener(new LockScreenMainListener() {
            public void unLock() {
                LockScreen.this.viewPager.setCurrentItem(0, true);
            }
        });
        this.viewPager.setAdapter(this.lockScreenAdapter);
        this.viewPager.post(new Runnable() {
            public void run() {
                LockScreen.this.viewPager.setCurrentItem(1, false);
            }
        });
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (position == 0) {
                    if (Setup.appSettings().isLockScreenEnablePassCode() && !Setup.appSettings().getPassCodeLockScreen().equals("")) {
                        LockScreen.this.lockScreenAdapter.getLockScreenCode().refreshView(new LockScreenCodeAction() {
                            public void unLock() {
                                if (LockScreen.this.lockScreenAction != null) {
                                    LockScreen.this.lockScreenAction.unLock();
                                }
                            }
                        });
                    } else if (LockScreen.this.lockScreenAction != null) {
                        LockScreen.this.lockScreenAction.unLock();
                    }
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        this.ivTaiTho.getLayoutParams().width = (int) (((float) getWidthScreen()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT);
        if (!Setup.appSettings().isIpornX()) {
            this.ivTaiTho.setImageDrawable(null);
        }
        this.tvNetwork.setTypeface(this.application.getBaseTypeface().getRegular());
        this.tvNetwork.setText(BaseUtils.getNetwork(getContext()));
        this.tvBattery.setTypeface(this.application.getBaseTypeface().getRegular());
    }

    public void startUpdateTime() {
        if (this.lockScreenAdapter != null && this.lockScreenAdapter.getLockScreenMain() != null) {
            this.lockScreenAdapter.getLockScreenMain().startUpdateTime();
        }
    }

    public void stopUpdateTime() {
        if (this.lockScreenAdapter != null && this.lockScreenAdapter.getLockScreenMain() != null) {
            this.lockScreenAdapter.getLockScreenMain().stopUpdateTime();
        }
    }

    public void resetViewpager() {
        try {
            this.viewPager.post(new Runnable() {
                public void run() {
                    LockScreen.this.viewPager.setCurrentItem(1, false);
                    LockScreen.this.lockScreenAdapter.getLockScreenCode().refreshView(null);
                }
            });
        } catch (Exception e) {
        }
    }

    @SuppressLint("WrongConstant")
    private int getWidthScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @SuppressLint("WrongConstant")
    public void update(int signalStrength, Intent intentBattery) {
        if (Setup.appSettings().isIpornX()) {
            this.ivTaiTho.setImageResource(R.drawable.ip_taitho);
        } else {
            this.ivTaiTho.setImageDrawable(null);
        }
        if (Setup.appSettings().isDesktopFullscreen()) {
            this.ivSignalStrength.setVisibility(0);
            this.tvNetwork.setVisibility(0);
            this.ivInternet.setVisibility(0);
            this.tvInternet.setVisibility(0);
            this.tvBattery.setVisibility(0);
            this.ivBattery.setVisibility(0);
            updateSignalStrength(signalStrength);
            updateBattery(intentBattery);
        } else {
            this.ivSignalStrength.setVisibility(8);
            this.tvNetwork.setVisibility(8);
            this.ivInternet.setVisibility(8);
            this.tvInternet.setVisibility(8);
            this.tvBattery.setVisibility(8);
            this.ivBattery.setVisibility(8);
        }
        try {
            if (VERSION.SDK_INT >= 19) {
                this.lockScreenAdapter.getLockScreenMain().initNotification();
            }
        } catch (Exception e) {
        }
    }

    public void updateSignalStrength(int signalStrength) {
        if (signalStrength >= 30) {
            this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_4_bar_white_48dp);
        } else if (signalStrength < 30 && signalStrength >= 20) {
            this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_3_bar_white_48dp);
        } else if (signalStrength < 20 && signalStrength >= 10) {
            this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_2_bar_white_48dp);
        } else if (signalStrength < 10 && signalStrength >= 3) {
            this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_1_bar_white_48dp);
        } else if (signalStrength >= 3) {
        } else {
            if (signalStrength == -1) {
                this.ivSignalStrength.setImageDrawable(null);
            } else {
                this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_0_bar_white_48dp);
            }
        }
    }

    public void updateBattery(Intent intent) {
        try {
            int level = intent.getIntExtra("level", -1);
            this.tvBattery.setText(level + "%");
            int status = intent.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
            boolean isCharging = status == 2 || status == 5;
            if (level == 100) {
                if (isCharging) {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_charging_full_white_48dp);
                } else {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_full_white_48dp);
                }
            } else if (level >= 90) {
                if (isCharging) {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_charging_90_white_48dp);
                } else {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_90_white_48dp);
                }
            } else if (level >= 80) {
                if (isCharging) {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_charging_80_white_48dp);
                } else {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_80_white_48dp);
                }
            } else if (level >= 60) {
                if (isCharging) {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_charging_60_white_48dp);
                } else {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_60_white_48dp);
                }
            } else if (level >= 50) {
                if (isCharging) {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_charging_50_white_48dp);
                } else {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_50_white_48dp);
                }
            } else if (level >= 40) {
                if (isCharging) {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_charging_40_white_48dp);
                } else {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_40_white_48dp);
                }
            } else if (level >= 30) {
                if (isCharging) {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_charging_30_white_48dp);
                } else {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_30_white_48dp);
                }
            } else if (level >= 20) {
                if (isCharging) {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_charging_20_white_48dp);
                } else {
                    this.ivBattery.setImageResource(R.drawable.ic_battery_20_white_48dp);
                }
            } else if (isCharging) {
                this.ivBattery.setImageResource(R.drawable.ic_battery_charging_20_white_48dp);
            } else {
                this.ivBattery.setImageResource(R.drawable.ic_battery_alert_48dp);
            }
        } catch (Exception e) {
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateInternet() {
        try {
            if (isConnectedViaWifi()) {
                WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                int rssi = wifi.getConnectionInfo().getRssi();
                int status = WifiManager.calculateSignalLevel(rssi, 4);
                switch (status) {
                    case 0:
                        ivInternet.setImageResource(R.drawable.ic_signal_wifi_0_bar_white_48dp);
                        break;
                    case 1:
                        ivInternet.setImageResource(R.drawable.ic_signal_wifi_1_bar_white_48dp);
                        break;
                    case 2:
                        ivInternet.setImageResource(R.drawable.ic_signal_wifi_2_bar_white_48dp);
                        break;
                    case 3:
                        ivInternet.setImageResource(R.drawable.ic_signal_wifi_3_bar_white_48dp);
                        break;
                    default:
                        break;
                }
                setLocationVisible();
                return;
            } else {
                ivInternet.setImageDrawable(null);
                if (!isConnectedViaMobile()) {
                    tvInternet.setText("");
                }else {
                    String s=getNetworkClass(getContext());
                    tvInternet.setText(s);
                    setLocationVisible();
                }
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        /*
        r7 = this;
        r4 = r7.isConnectedViaWifi();	 Catch:{ Exception -> 0x0053 }
        if (r4 == 0) goto L_0x0070;
    L_0x0006:
        r4 = r7.getContext();	 Catch:{ Exception -> 0x0053 }
        r4 = r4.getApplicationContext();	 Catch:{ Exception -> 0x0053 }
        r5 = "wifi";
        r4 = r4.getSystemService(r5);	 Catch:{ Exception -> 0x0053 }
        r4 = (android.net.wifi.WifiManager) r4;	 Catch:{ Exception -> 0x0053 }
        r4 = r4.getConnectionInfo();	 Catch:{ Exception -> 0x0053 }
        r4 = r4.getRssi();	 Catch:{ Exception -> 0x0053 }
        r5 = 4;
        r3 = android.net.wifi.WifiManager.calculateSignalLevel(r4, r5);	 Catch:{ Exception -> 0x0053 }
        r4 = r7.tvInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = "";
        r4.setText(r5);	 Catch:{ Exception -> 0x0053 }
        switch(r3) {
            case 0: goto L_0x004a;
            case 1: goto L_0x0055;
            case 2: goto L_0x005e;
            case 3: goto L_0x0067;
            default: goto L_0x002e;
        };
    L_0x002e:
        r2 = 0;
        r4 = r7.getContext();	 Catch:{ Exception -> 0x0098 }
        r5 = "location";
        r4 = r4.getSystemService(r5);	 Catch:{ Exception -> 0x0098 }
        r4 = (android.location.LocationManager) r4;	 Catch:{ Exception -> 0x0098 }
        r5 = "gps";
        r2 = r4.isProviderEnabled(r5);	 Catch:{ Exception -> 0x0098 }
    L_0x0041:
        if (r2 == 0) goto L_0x00d3;
    L_0x0043:
        r4 = r7.ivLocation;	 Catch:{ Exception -> 0x00b6 }
        r5 = 0;
        r4.setVisibility(r5);	 Catch:{ Exception -> 0x00b6 }
    L_0x0049:
        return;
    L_0x004a:
        r4 = r7.ivInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = 2131231023; // 0x7f08012f float:1.8078115E38 double:1.052968032E-314;
        r4.setImageResource(r5);	 Catch:{ Exception -> 0x0053 }
        goto L_0x002e;
    L_0x0053:
        r4 = move-exception;
        goto L_0x002e;
    L_0x0055:
        r4 = r7.ivInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = 2131231024; // 0x7f080130 float:1.8078117E38 double:1.0529680323E-314;
        r4.setImageResource(r5);	 Catch:{ Exception -> 0x0053 }
        goto L_0x002e;
    L_0x005e:
        r4 = r7.ivInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = 2131231025; // 0x7f080131 float:1.807812E38 double:1.052968033E-314;
        r4.setImageResource(r5);	 Catch:{ Exception -> 0x0053 }
        goto L_0x002e;
    L_0x0067:
        r4 = r7.ivInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = 2131231026; // 0x7f080132 float:1.8078121E38 double:1.0529680333E-314;
        r4.setImageResource(r5);	 Catch:{ Exception -> 0x0053 }
        goto L_0x002e;
    L_0x0070:
        r4 = r7.isConnectedViaMobile();	 Catch:{ Exception -> 0x0053 }
        if (r4 == 0) goto L_0x008a;
    L_0x0076:
        r4 = r7.ivInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = 0;
        r4.setImageDrawable(r5);	 Catch:{ Exception -> 0x0053 }
        r4 = r7.tvInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = r7.getContext();	 Catch:{ Exception -> 0x0053 }
        r5 = r7.getNetworkClass(r5);	 Catch:{ Exception -> 0x0053 }
        r4.setText(r5);	 Catch:{ Exception -> 0x0053 }
        goto L_0x002e;
    L_0x008a:
        r4 = r7.ivInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = 0;
        r4.setImageDrawable(r5);	 Catch:{ Exception -> 0x0053 }
        r4 = r7.tvInternet;	 Catch:{ Exception -> 0x0053 }
        r5 = "";
        r4.setText(r5);	 Catch:{ Exception -> 0x0053 }
        goto L_0x002e;
    L_0x0098:
        r1 = move-exception;
        r4 = "HuyAnh";
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b6 }
        r5.<init>();	 Catch:{ Exception -> 0x00b6 }
        r6 = "gps_enabled: ";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x00b6 }
        r6 = r1.getMessage();	 Catch:{ Exception -> 0x00b6 }
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x00b6 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x00b6 }
        android.util.Log.e(r4, r5);	 Catch:{ Exception -> 0x00b6 }
        goto L_0x0041;
    L_0x00b6:
        r0 = move-exception;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "error update location: ";
        r4 = r4.append(r5);
        r5 = r0.getMessage();
        r4 = r4.append(r5);
        r4 = r4.toString();
        com.huyanh.base.utils.Log.e(r4);
        goto L_0x0049;
    L_0x00d3:
        r4 = r7.ivLocation;	 Catch:{ Exception -> 0x00b6 }
        r5 = 8;
        r4.setVisibility(r5);	 Catch:{ Exception -> 0x00b6 }
        goto L_0x0049;
        */
        //throw new UnsupportedOperationException("Method not decompiled: com.benny.openlauncher.customview.LockScreen.updateInternet():void");
    }

    private void setLocationVisible() {
        LocationManager locationManager = (LocationManager) getContext().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled("gps")) {
            ivLocation.setVisibility(VISIBLE);
        } else {
            ivLocation.setVisibility(GONE);
        }
    }

    @SuppressLint("WrongConstant")
    private boolean isConnectedViaWifi() {
        try {
            return ((ConnectivityManager) getContext().getSystemService("connectivity")).getNetworkInfo(1).isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    private boolean isConnectedViaMobile() {
        boolean z = false;
        try {
            z = ((ConnectivityManager) getContext().getSystemService("connectivity")).getNetworkInfo(0).isConnected();
        } catch (Exception e) {
        }
        return z;
    }

    @SuppressLint("WrongConstant")
    private String getNetworkClass(Context context) {
        try {
            switch (((TelephonyManager) context.getSystemService("phone")).getNetworkType()) {
                case 1:
                case 2:
                case 4:
                case 7:
                case 11:
                case 16:
                    return "E";
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                case 17:
                    return "H";
                case 13:
                case 18:
                case 19:
                    return "LTE";
                default:
                    return "LTE";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    @RequiresApi(api = 19)
    public void addNotification(StatusBarNotification sbn) {
        try {
            this.lockScreenAdapter.getLockScreenMain().addNotification(sbn);
        } catch (Exception e) {
            Log.e("error lock screen addNotification: " + e.getMessage());
        }
    }

    @RequiresApi(api = 19)
    public void removeNotification(StatusBarNotification sbn) {
        try {
            this.lockScreenAdapter.getLockScreenMain().removeNotification(sbn);
        } catch (Exception e) {
            Log.e("error lock screen removeNotification: " + e.getMessage());
        }
    }
}
