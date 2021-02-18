package com.benny.openlauncher.customview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.benny.openlauncher.App;
import com.benny.openlauncher.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

public class StatusBar extends RelativeLayout {
    private App application;
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() == "android.intent.action.BATTERY_CHANGED") {
                    try {
                        int level = intent.getIntExtra("level", -1);
                        StatusBar.this.tvBattery.setText(level + "%");
                        int status = intent.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
                        boolean isCharging = status == 2 || status == 5;
                        if (level == 100) {
                            if (isCharging) {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_full_white_48dp);
                            } else {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_full_white_48dp);
                            }
                        } else if (level >= 90) {
                            if (isCharging) {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_90_white_48dp);
                            } else {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_90_white_48dp);
                            }
                        } else if (level >= 80) {
                            if (isCharging) {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_80_white_48dp);
                            } else {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_80_white_48dp);
                            }
                        } else if (level >= 60) {
                            if (isCharging) {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_60_white_48dp);
                            } else {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_60_white_48dp);
                            }
                        } else if (level >= 50) {
                            if (isCharging) {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_50_white_48dp);
                            } else {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_50_white_48dp);
                            }
                        } else if (level >= 40) {
                            if (isCharging) {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_40_white_48dp);
                            } else {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_40_white_48dp);
                            }
                        } else if (level >= 30) {
                            if (isCharging) {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_30_white_48dp);
                            } else {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_30_white_48dp);
                            }
                        } else if (level >= 20) {
                            if (isCharging) {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_20_white_48dp);
                            } else {
                                StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_20_white_48dp);
                            }
                        } else if (isCharging) {
                            StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_charging_20_white_48dp);
                        } else {
                            StatusBar.this.ivBattery.setImageResource(R.drawable.ic_battery_alert_48dp);
                        }
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e2) {
            }
        }
    };
    private Handler handler = new Handler();
    @BindView(R.id.ivBattery)
    ImageView ivBattery;
    @BindView(R.id.ivInternet)
    ImageView ivInternet;
    @Nullable
    @BindView(R.id.ivLocationStatusBar)
    ImageView ivLocation;
    @BindView(R.id.ivSignalStrength)
    ImageView ivSignalStrength;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private PhoneStateListener phoneStateListener = null;
    @BindView(R.id.tvBattery)
    TextView tvBattery;
    @BindView(R.id.tvInternet)
    TextView tvInternet;
    @BindView(R.id.tvTime)
    TextView tvTime;

    public StatusBar(Context context) {
        super(context);
        initView();
    }

    public StatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public StatusBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        View view = inflate(getContext(), R.layout.view_status_bar, null);
        addView(view);
        ButterKnife.bind( this, view);
        this.application = (App) getContext().getApplicationContext();
        this.tvTime.setTypeface(this.application.getBaseTypeface().getBold());
        this.tvInternet.setTypeface(this.application.getBaseTypeface().getRegular());
        setupTime(1);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        getContext().registerReceiver(this.batteryReceiver, filter);
        this.phoneStateListener = new PhoneStateListener() {
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                try {
                    int signalStrengthLevel = signalStrength.getGsmSignalStrength();
                    if (signalStrengthLevel >= 30) {
                        StatusBar.this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_4_bar_white_48dp);
                    } else if (signalStrengthLevel < 30 && signalStrengthLevel >= 20) {
                        StatusBar.this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_3_bar_white_48dp);
                    } else if (signalStrengthLevel < 20 && signalStrengthLevel >= 10) {
                        StatusBar.this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_2_bar_white_48dp);
                    } else if (signalStrengthLevel < 10 && signalStrengthLevel >= 3) {
                        StatusBar.this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_1_bar_white_48dp);
                    } else if (signalStrengthLevel >= 3) {
                    } else {
                        if (signalStrengthLevel == -1) {
                            StatusBar.this.ivSignalStrength.setImageDrawable(null);
                        } else {
                            StatusBar.this.ivSignalStrength.setImageResource(R.drawable.ic_signal_cellular_0_bar_white_48dp);
                        }
                    }
                } catch (Exception e) {
                }
            }
        };
        ((TelephonyManager) getContext().getSystemService("phone")).listen(this.phoneStateListener, 256);
    }

    public void unRegisterBroadcast() {
        getContext().unregisterReceiver(this.batteryReceiver);
    }

    private void setupTime(int delay) {
        this.mTimer = new Timer();
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
        }
        this.mTimerTask = new TimerTask() {
            public void run() {
                StatusBar.this.handler.post(new Runnable() {
                    @SuppressLint("WrongConstant")
                    public void run() {
                        try {
                            StatusBar.this.tvTime.setText(StatusBar.this.getTime());
                            if (StatusBar.this.isConnectedViaWifi()) {
                                int level = WifiManager.calculateSignalLevel(((WifiManager) StatusBar.this.getContext().getApplicationContext().getSystemService("wifi")).getConnectionInfo().getRssi(), 5);
                                StatusBar.this.ivInternet.setVisibility(0);
                                StatusBar.this.tvInternet.setVisibility(GONE);
                                switch (level) {
                                    case 0:
                                        StatusBar.this.ivInternet.setImageResource(R.drawable.ic_signal_wifi_0_bar_white_48dp);
                                        break;
                                    case 1:
                                        StatusBar.this.ivInternet.setImageResource(R.drawable.ic_signal_wifi_1_bar_white_48dp);
                                        break;
                                    case 2:
                                        StatusBar.this.ivInternet.setImageResource(R.drawable.ic_signal_wifi_2_bar_white_48dp);
                                        break;
                                    case 3:
                                        StatusBar.this.ivInternet.setImageResource(R.drawable.ic_signal_wifi_3_bar_white_48dp);
                                        break;
                                    case 4:
                                        StatusBar.this.ivInternet.setImageResource(R.drawable.ic_signal_wifi_4_bar_white_48dp);
                                        break;
                                }
                            } else if (StatusBar.this.isConnectedViaMobile()) {
                                StatusBar.this.ivInternet.setVisibility(8);
                                StatusBar.this.tvInternet.setVisibility(0);
                                StatusBar.this.tvInternet.setText(StatusBar.this.getNetworkClass(StatusBar.this.getContext()));
                            } else {
                                StatusBar.this.ivInternet.setVisibility(8);
                                StatusBar.this.tvInternet.setVisibility(8);
                            }
                            boolean gps_enabled = false;
                            try {
                                gps_enabled = ((LocationManager) StatusBar.this.getContext().getSystemService("location")).isProviderEnabled("gps");
                            } catch (Exception ex) {
                                Log.e("HuyAnh", "gps_enabled: " + ex.getMessage());
                            }
                            if (gps_enabled) {
                                StatusBar.this.ivLocation.setVisibility(VISIBLE);
                            } else {
                                StatusBar.this.ivLocation.setVisibility(GONE);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        this.mTimer.schedule(this.mTimerTask, ((long) delay) * 1000, 10000);
    }

    public void resetTimer(int delay) {
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
        setupTime(delay);
    }

    private String getTime() {
        try {
            return new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            return "N/A";
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
}
