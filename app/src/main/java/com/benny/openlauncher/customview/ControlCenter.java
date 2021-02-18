package com.benny.openlauncher.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator;
import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.activity.SelectMusicPlayer;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.IconDrawer;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.Definitions;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.util.Constant;


import java.util.List;

public class ControlCenter extends RelativeLayout {
    private AudioManager audioManager;
    private Camera camera;
    private Handler handler = new Handler();
    private int heightRlBrighness;
    private Intent i;
    private boolean isFlashOn = false;
    @BindView(R.id.ivAir)
    ImageView ivAir;
    @BindView(R.id.ivBlueTooth)
    ImageView ivBlueTooth;
    @BindView(R.id.ivBrightness)
    ImageView ivBrightness;
    @BindView(R.id.ivCamera)
    ImageView ivCamera;
    @BindView(R.id.ivCollapse)
    ImageView ivCollapse;
    @BindView(R.id.ivFlashLight)
    ImageView ivFlashLight;
    @BindView(R.id.ivLocation)
    ImageView ivLocation;
    @BindView(R.id.ivMusicPlayer)
    ImageView ivMusicPlayer;
    @BindView(R.id.ivMute)
    ImageView ivMute;
    @BindView(R.id.ivNext)
    ImageView ivNext;
    @BindView(R.id.ivPlay)
    ImageView ivPlay;
    @BindView(R.id.ivPrev)
    ImageView ivPrev;
    @BindView(R.id.ivRotation)
    ImageView ivRotation;
    @BindView(R.id.ivTethering)
    ImageView ivTethering;
    @BindView(R.id.ivWifi)
    ImageView ivWifi;
    @BindView(R.id.llHome)
    LinearLayout llHome;
    @BindView(R.id.llSelectMusicPlayer)
    LinearLayout llSelectMusicPlayer;
    private App musicPlayer = null;
    private Parameters params;
    @BindView(R.id.rlBrightness)
    RelativeLayout rlBrightness;
    @BindView(R.id.rlCC)
    RelativeLayout rlCC;
    @BindView(R.id.rlVolume)
    RelativeLayout rlVolume;
    @BindView(R.id.tvLabelMusicPlayer)
    TextView tvLabelMusicPlayer;
    @BindView(R.id.rlBrightness_Selected)
    View viewBrightnessSelected;
    @BindView(R.id.rlVolume_Selected)
    View viewVolumeSelected;

    public ControlCenter(Context context) {
        super(context);
        initView();
    }

    public ControlCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ControlCenter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.view_control_center, null);
        addView(view);
        ButterKnife.bind((Object) this, view);
        initData();
        initListener();
        updateStateBt();
    }

    private void initData() {
    }

    @SuppressLint("WrongConstant")
    public void visibleOrGone(boolean isVisible) {
        if (isVisible) {
            ((Vibrator) getContext().getSystemService("vibrator")).vibrate(25);
            updateStateBt();
            setVisibility(0);
            ViewPropertyObjectAnimator.animate(this).translationY(0.0f).setDuration(300).start();
            return;
        }
        ViewPropertyObjectAnimator.animate(this).translationY((float) getHeight()).setDuration(300).addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (ControlCenter.this != null) {
                    ControlCenter.this.setVisibility(4);
                }
            }
        }).start();
    }
    @SuppressLint("WrongConstant")
    private void initListener() {
        this.ivFlashLight.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.ivFlashLight.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivFlashLight).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivFlashLight).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.onOffFlash();
                        break;
                }
                return true;
            }
        });
        this.ivCamera.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.ivCamera.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivCamera).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivCamera).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.openCamera();
                        break;
                }
                return true;
            }
        });
        this.ivMute.setOnTouchListener(new OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.ivMute.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivMute).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivMute).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.changeSilent();
                        break;
                }
                return true;
            }
        });
        this.ivRotation.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.ivRotation.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivRotation).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivRotation).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.changeRotationSettings();
                        break;
                }
                return true;
            }
        });
        this.ivTethering.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.ivTethering.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivTethering).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivTethering).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.openTetheringSettings();
                        break;
                }
                return true;
            }
        });
        this.ivBrightness.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.ivBrightness.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivBrightness).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivBrightness).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.changeAutoBrightness();
                        break;
                }
                return true;
            }
        });
        this.ivAir.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin) / ((float) ControlCenter.this.ivAir.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivAir).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivAir).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.changeFlightMode();
                        break;
                }
                return true;
            }
        });
        this.ivLocation.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin) / ((float) ControlCenter.this.ivLocation.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivLocation).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivLocation).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.changeLocation();
                        break;
                }
                return true;
            }
        });
        this.ivWifi.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin) / ((float) ControlCenter.this.ivWifi.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivWifi).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivWifi).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.changeWifiState();
                        break;
                }
                return true;
            }
        });
        this.ivBlueTooth.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin) / ((float) ControlCenter.this.ivBlueTooth.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivBlueTooth).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.ivBlueTooth).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.changeBlueToothState();
                        break;
                }
                return true;
            }
        });
        this.ivCollapse.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ControlCenter.this.visibleOrGone(false);
            }
        });
        this.rlCC.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ControlCenter.this.visibleOrGone(false);
            }
        });
        this.rlCC.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    ControlCenter.this.visibleOrGone(false);
                }
                return true;
            }
        });
        this.llHome.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("WrongConstant")
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.llHome.getWidth()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.llHome).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.llHome).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        ControlCenter.this.visibleOrGone(false);
                        Intent startMain = new Intent("android.intent.action.MAIN");
                        startMain.addCategory("android.intent.category.HOME");
                        startMain.setFlags(268435456);
                        ControlCenter.this.getContext().startActivity(startMain);
                        break;
                }
                return true;
            }
        });
        this.llSelectMusicPlayer.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                Intent intent = new Intent(ControlCenter.this.getContext(), SelectMusicPlayer.class);
                intent.setFlags(268435456);
                ControlCenter.this.getContext().startActivity(intent);
                ControlCenter.this.visibleOrGone(false);
            }
        });
        this.ivMusicPlayer.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                if (ControlCenter.this.musicPlayer == null) {
                    Intent intent = new Intent(ControlCenter.this.getContext(), SelectMusicPlayer.class);
                    intent.setFlags(268435456);
                    ControlCenter.this.getContext().startActivity(intent);
                } else {
                    Tool.startApp(ControlCenter.this.getContext(), ControlCenter.this.musicPlayer);
                }
                ControlCenter.this.visibleOrGone(false);
            }
        });
        this.ivPlay.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                if (ControlCenter.this.audioManager == null) {
                    ControlCenter.this.audioManager = (AudioManager) ControlCenter.this.getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
                }
                if (ControlCenter.this.audioManager.isMusicActive()) {
                    Intent i = new Intent(Constant.SERVICECMD);
                    i.putExtra(Constant.CMDNAME, Constant.CMDSTOP);
                    ControlCenter.this.getContext().getApplicationContext().sendBroadcast(i);
                    ControlCenter.this.ivPlay.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    return;
                }
                ControlCenter.this.i = new Intent(Constant.SERVICECMD);
                ControlCenter.this.i.putExtra(Constant.CMDNAME, Constant.CMDPLAY);
                ControlCenter.this.getContext().getApplicationContext().sendBroadcast(ControlCenter.this.i);
                ControlCenter.this.ivPlay.setImageResource(R.drawable.ic_pause_white_48dp);
            }
        });
        this.ivPrev.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                if (ControlCenter.this.audioManager == null) {
                    ControlCenter.this.audioManager = (AudioManager) ControlCenter.this.getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
                }
                if (ControlCenter.this.audioManager.isMusicActive()) {
                    Intent i = new Intent(Constant.SERVICECMD);
                    i.putExtra(Constant.CMDNAME, Constant.CMDPREVIOUS);
                    ControlCenter.this.getContext().getApplicationContext().sendBroadcast(i);
                }
            }
        });
        this.ivNext.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                if (ControlCenter.this.audioManager == null) {
                    ControlCenter.this.audioManager = (AudioManager) ControlCenter.this.getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
                }
                if (ControlCenter.this.audioManager.isMusicActive()) {
                    Intent i = new Intent(Constant.SERVICECMD);
                    i.putExtra(Constant.CMDNAME, Constant.CMDNEXT);
                    ControlCenter.this.getContext().getApplicationContext().sendBroadcast(i);
                }
            }
        });
        this.rlBrightness.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (VERSION.SDK_INT >= 23 && !System.canWrite(ControlCenter.this.getContext())) {
                        Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS", Uri.parse("package:" + ControlCenter.this.getContext().getPackageName()));
                        intent.setFlags(268435456);
                        ControlCenter.this.getContext().startActivity(intent);
                        return false;
                    }
                } catch (Exception e) {
                }
                switch (motionEvent.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.rlBrightness.getHeight()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.rlBrightness).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.rlBrightness).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        break;
                }
                try {
                    float y = motionEvent.getY();
                    LayoutParams params = (LayoutParams) ControlCenter.this.viewBrightnessSelected.getLayoutParams();
                    if (y < 0.0f) {
                        y = 0.0f;
                    }
                    if (y > ((float) ControlCenter.this.heightRlBrighness)) {
                        y = (float) ControlCenter.this.heightRlBrighness;
                    }
                    params.height = ControlCenter.this.heightRlBrighness - ((int) y);
                    ControlCenter.this.viewBrightnessSelected.setLayoutParams(params);
                    System.putInt(ControlCenter.this.getContext().getContentResolver(), "screen_brightness", (int) ((255.0f * ((float) params.height)) / ((float) ControlCenter.this.heightRlBrighness)));
                } catch (Exception e2) {
                    Log.e("HuyAnh", "error rlBrighness: " + e2.getMessage());
                }
                return true;
            }
        });
        this.rlVolume.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                        float scaleValue = Tool.DEFAULT_BACKOFF_MULT + (ControlCenter.this.getContext().getResources().getDimension(R.dimen.control_center_margin_half) / ((float) ControlCenter.this.rlVolume.getHeight()));
                        Log.v("HuyAnh", "scaleValue: " + scaleValue);
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.rlVolume).scales(scaleValue).setDuration(500).start();
                        break;
                    case 1:
                        ViewPropertyObjectAnimator.animate(ControlCenter.this.rlVolume).scales(Tool.DEFAULT_BACKOFF_MULT).setDuration(500).start();
                        break;
                }
                try {
                    float y = motionEvent.getY();
                    LayoutParams params = (LayoutParams) ControlCenter.this.viewVolumeSelected.getLayoutParams();
                    if (y < 0.0f) {
                        y = 0.0f;
                    }
                    if (y > ((float) ControlCenter.this.heightRlBrighness)) {
                        y = (float) ControlCenter.this.heightRlBrighness;
                    }
                    params.height = ControlCenter.this.heightRlBrighness - ((int) y);
                    ControlCenter.this.viewVolumeSelected.setLayoutParams(params);
                    if (ControlCenter.this.audioManager == null) {
                        ControlCenter.this.audioManager = (AudioManager) ControlCenter.this.getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
                    }
                    if (ControlCenter.this.audioManager.isMusicActive()) {
                        ControlCenter.this.audioManager.setStreamVolume(3, (int) ((((float) ControlCenter.this.audioManager.getStreamMaxVolume(3)) * ((float) params.height)) / ((float) ControlCenter.this.heightRlBrighness)), 0);
                    } else {
                        ControlCenter.this.audioManager.setStreamVolume(1, (int) ((((float) ControlCenter.this.audioManager.getStreamMaxVolume(1)) * ((float) params.height)) / ((float) ControlCenter.this.heightRlBrighness)), 0);
                    }
                } catch (Exception e) {
                    Log.e("HuyAnh", "error rlVolume: " + e.getMessage());
                }
                return true;
            }
        });
    }

    public void changeVolume() {
        this.handler.postDelayed(new Runnable() {
            @SuppressLint("WrongConstant")
            public void run() {
                ControlCenter.this.heightRlBrighness = ControlCenter.this.rlBrightness.getHeight();
                if (ControlCenter.this.heightRlBrighness != -1) {
                    if (ControlCenter.this.audioManager == null) {
                        ControlCenter.this.audioManager = (AudioManager) ControlCenter.this.getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
                    }
                    LayoutParams params = (LayoutParams) ControlCenter.this.viewVolumeSelected.getLayoutParams();
                    if (ControlCenter.this.audioManager.isMusicActive()) {
                        params.height = (int) ((((float) ControlCenter.this.heightRlBrighness) * ((float) ControlCenter.this.audioManager.getStreamVolume(3))) / ((float) ControlCenter.this.audioManager.getStreamMaxVolume(3)));
                    } else {
                        params.height = (int) ((((float) ControlCenter.this.heightRlBrighness) * ((float) ControlCenter.this.audioManager.getStreamVolume(1))) / ((float) ControlCenter.this.audioManager.getStreamMaxVolume(1)));
                    }
                    ControlCenter.this.viewVolumeSelected.setLayoutParams(params);
                }
            }
        }, 500);
    }

    @SuppressLint({"WifiManagerLeak", "WrongConstant", "ResourceType"})
    public void updateStateBt() {
        if (System.getInt(getContext().getContentResolver(), "accelerometer_rotation", 0) == 1) {
            this.ivRotation.setImageResource(R.drawable.ic_unlock_rotate);
            this.ivRotation.setBackgroundResource(R.drawable.control_center_bg_bt_state);
        } else {
            this.ivRotation.setImageResource(R.drawable.ic_lock_rotate);
            this.ivRotation.setBackgroundResource(R.drawable.control_center_bg_bt_state_rotation);
        }
        if (this.audioManager == null) {
            this.audioManager = (AudioManager) getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
        }
        if (this.audioManager.getRingerMode() == 0) {
            this.ivMute.setImageResource(R.drawable.ic_silent_on);
        } else {
            this.ivMute.setImageResource(R.drawable.ic_silent);
        }
        if (System.getInt(getContext().getContentResolver(), "screen_brightness_mode", 1) == 1) {
            this.ivBrightness.setImageResource(R.drawable.ic_brightness_auto_white_48dp);
        } else {
            this.ivBrightness.setImageResource(R.drawable.ic_brightness_low_white_48dp);
        }
        if (VERSION.SDK_INT >= 17) {
            if (Global.getInt(getContext().getContentResolver(), "airplane_mode_on", 1) == 1) {
                this.ivAir.setBackgroundResource(R.drawable.control_center_bg_bt_circle_air_selected);
            } else {
                this.ivAir.setBackgroundResource(R.drawable.control_center_bg_bt_circle_air);
            }
        } else if (System.getInt(getContext().getContentResolver(), "airplane_mode_on", 1) == 1) {
            this.ivAir.setBackgroundResource(R.drawable.control_center_bg_bt_circle_air_selected);
        } else {
            this.ivAir.setBackgroundResource(R.drawable.control_center_bg_bt_circle_air);
        }
        if (((WifiManager) getContext().getSystemService("wifi")).isWifiEnabled()) {
            this.ivWifi.setBackgroundResource(R.drawable.control_center_bg_bt_circle_wifi_bluetooth_selected);
        } else {
            this.ivWifi.setBackgroundResource(R.drawable.control_center_bg_bt_circle_wifi_bluetooth);
        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                this.ivBlueTooth.setBackgroundResource(R.drawable.control_center_bg_bt_circle_wifi_bluetooth_selected);
            } else {
                this.ivBlueTooth.setBackgroundResource(R.drawable.control_center_bg_bt_circle_wifi_bluetooth);
            }
        }
        boolean gps_enabled = false;
        try {
            gps_enabled = ((LocationManager) getContext().getSystemService("location")).isProviderEnabled("gps");
        } catch (Exception ex) {
            Log.e("HuyAnh", "gps_enabled: " + ex.getMessage());
        }
        if (gps_enabled) {
            this.ivLocation.setBackgroundResource(R.drawable.control_center_bg_bt_circle_data_mobile_selected);
            this.ivLocation.setImageResource(R.drawable.ic_location_on_white_48dp);
        } else {
            this.ivLocation.setBackgroundResource(R.drawable.control_center_bg_bt_circle_data_mobile);
            this.ivLocation.setImageResource(R.drawable.ic_location_off_white_48dp);
        }
        try {
            if (Setup.appSettings().getPackageMusicPlayer().equals("") && Definitions.packageNameDefaultApps.size() > 13 && !((String) Definitions.packageNameDefaultApps.get(13)).equals("")) {
                Setup.appSettings().setPackageMusicPlayer((String) Definitions.packageNameDefaultApps.get(13));
            }
        } catch (Exception e) {
        }
        try {
            if (Setup.appSettings().getPackageMusicPlayer().equals("")) {
                this.ivMusicPlayer.setImageResource(R.drawable.ic_settings_white_48dp);
                this.tvLabelMusicPlayer.setText(getResources().getString(R.string.control_center_select_music_player_title));
            } else {
                this.musicPlayer = null;
                if (this.musicPlayer == null) {
                    List<App> apps = Setup.appLoader().getAllApps(getContext());
                    String packageNameMusicPlayer = Setup.appSettings().getPackageMusicPlayer();
                    for (App appTemp : apps) {
                        if (appTemp.getPackageName().equals(packageNameMusicPlayer)) {
                            this.musicPlayer = appTemp;
                            break;
                        }
                    }
                }
                if (this.musicPlayer == null) {
                    this.ivMusicPlayer.setImageResource(R.drawable.ic_settings_white_48dp);
                    this.tvLabelMusicPlayer.setText(getResources().getString(R.string.control_center_select_music_player_title));
                } else {
                    this.musicPlayer.getIconProvider().loadIconIntoIconDrawer(new IconDrawer() {
                        public void onIconAvailable(Drawable drawable, int index) {
                            ControlCenter.this.ivMusicPlayer.setImageDrawable(drawable);
                        }

                        public void onIconCleared(Drawable placeholder, int index) {
                        }
                    }, (int) getResources().getDimension(17104896), 0);
                    this.tvLabelMusicPlayer.setText(this.musicPlayer.getLabel());
                    if (this.audioManager.isMusicActive()) {
                        this.ivPlay.setImageResource(R.drawable.ic_pause_white_48dp);
                    } else {
                        this.ivPlay.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    }
                }
            }
        } catch (Exception e2) {
        }
        this.handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    ControlCenter.this.heightRlBrighness = ControlCenter.this.rlBrightness.getHeight();
                    if (ControlCenter.this.heightRlBrighness == -1) {
                        ControlCenter.this.handler.postDelayed(this, 1000);
                        return;
                    }
                    LayoutParams params = (LayoutParams) ControlCenter.this.viewVolumeSelected.getLayoutParams();
                    if (ControlCenter.this.audioManager == null) {
                        ControlCenter.this.audioManager = (AudioManager) ControlCenter.this.getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
                    }
                    if (ControlCenter.this.audioManager.isMusicActive()) {
                        params.height = (int) ((((float) ControlCenter.this.heightRlBrighness) * ((float) ControlCenter.this.audioManager.getStreamVolume(3))) / ((float) ControlCenter.this.audioManager.getStreamMaxVolume(3)));
                    } else {
                        params.height = (int) ((((float) ControlCenter.this.heightRlBrighness) * ((float) ControlCenter.this.audioManager.getStreamVolume(1))) / ((float) ControlCenter.this.audioManager.getStreamMaxVolume(1)));
                    }
                    ControlCenter.this.viewVolumeSelected.setLayoutParams(params);
                    LayoutParams params1 = (LayoutParams) ControlCenter.this.viewBrightnessSelected.getLayoutParams();
                    params1.height = (int) (((float) ControlCenter.this.heightRlBrighness) * (((float) System.getInt(ControlCenter.this.getContext().getContentResolver(), "screen_brightness", 0)) / 255.0f));
                    ControlCenter.this.viewBrightnessSelected.setLayoutParams(params1);
                } catch (Exception e) {
                }
            }
        }, 1000);
    }

    @SuppressLint("WrongConstant")
    public void changeLocation() {
        Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
        intent.setFlags(268435456);
        getContext().startActivity(intent);
        visibleOrGone(false);
    }

    private void changeBlueToothState() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return;
        }
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            this.ivBlueTooth.setBackgroundResource(R.drawable.control_center_bg_bt_circle_wifi_bluetooth);
            return;
        }
        bluetoothAdapter.enable();
        this.ivBlueTooth.setBackgroundResource(R.drawable.control_center_bg_bt_circle_wifi_bluetooth_selected);
    }

    @SuppressLint({"WifiManagerLeak"})
    private void changeWifiState() {
        @SuppressLint("WrongConstant") WifiManager wifiManager = (WifiManager) getContext().getSystemService("wifi");
        boolean wifiEnabled = !wifiManager.isWifiEnabled();
        wifiManager.setWifiEnabled(wifiEnabled);
        if (wifiEnabled) {
            this.ivWifi.setBackgroundResource(R.drawable.control_center_bg_bt_circle_wifi_bluetooth_selected);
        } else {
            this.ivWifi.setBackgroundResource(R.drawable.control_center_bg_bt_circle_wifi_bluetooth);
        }
    }
    @SuppressLint("WrongConstant")
    public void changeFlightMode() {
        try {
            Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
            intent.setFlags(268435456);
            getContext().startActivity(intent);
        } catch (Exception e) {
        }
        visibleOrGone(false);
    }

    @SuppressLint("WrongConstant")
    public void changeAutoBrightness() {
        int i = 0;
        try {
            if (VERSION.SDK_INT < 23 || System.canWrite(getContext())) {
                ContentResolver contentResolver = getContext().getContentResolver();
                String str = "screen_brightness_mode";
                if (System.getInt(getContext().getContentResolver(), "screen_brightness_mode", 1) != 1) {
                    i = 1;
                }
                System.putInt(contentResolver, str, i);
                if (System.getInt(getContext().getContentResolver(), "screen_brightness_mode", 1) == 1) {
                    this.ivBrightness.setImageResource(R.drawable.ic_brightness_auto_white_48dp);
                    return;
                } else {
                    this.ivBrightness.setImageResource(R.drawable.ic_brightness_low_white_48dp);
                    return;
                }
            }
            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS", Uri.parse("package:" + getContext().getPackageName()));
            intent.setFlags(268435456);
            getContext().startActivity(intent);
            visibleOrGone(false);
        } catch (Exception e) {
        }
    }
    @SuppressLint("WrongConstant")
    private void openTetheringSettings() {
        try {
            Intent intent = new Intent("android.intent.action.MAIN", null);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.TetherSettings"));
            intent.setFlags(268435456);
            getContext().startActivity(intent);
        } catch (Exception e) {
        }
        visibleOrGone(false);
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changeSilent() {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Tool.TABLE_NAME);
        if (VERSION.SDK_INT < 24 || notificationManager.isNotificationPolicyAccessGranted()) {
            if (this.audioManager == null) {
                this.audioManager = (AudioManager) getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
            }
            this.audioManager.setRingerMode(this.audioManager.getRingerMode() != 0 ? 0 : 2);
            if (this.audioManager.getRingerMode() == 0) {
                this.ivMute.setImageResource(R.drawable.ic_silent_on);
                return;
            } else {
                this.ivMute.setImageResource(R.drawable.ic_silent);
                return;
            }
        }
        getContext().startActivity(new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"));
        visibleOrGone(false);
    }

    @SuppressLint("WrongConstant")
    public void changeRotationSettings() {
        int i = 0;
        try {
            if (VERSION.SDK_INT >= 23 && !System.canWrite(getContext())) {
                Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS", Uri.parse("package:" + getContext().getPackageName()));
                intent.setFlags(268435456);
                getContext().startActivity(intent);
                visibleOrGone(false);
                return;
            }
        } catch (Exception e) {
        }
        try {
            ContentResolver contentResolver = getContext().getContentResolver();
            String str = "accelerometer_rotation";
            if (System.getInt(getContext().getContentResolver(), "accelerometer_rotation", 0) == 0) {
                i = 1;
            }
            System.putInt(contentResolver, str, i);
            if (System.getInt(getContext().getContentResolver(), "accelerometer_rotation", 0) == 1) {
                this.ivRotation.setImageResource(R.drawable.ic_unlock_rotate);
                this.ivRotation.setBackgroundResource(R.drawable.control_center_bg_bt_state);
                return;
            }
            this.ivRotation.setImageResource(R.drawable.ic_lock_rotate);
            this.ivRotation.setBackgroundResource(R.drawable.control_center_bg_bt_state_rotation);
        } catch (Exception e2) {
        }
    }
    @SuppressLint("WrongConstant")
    public void openCamera() {
        try {
            if (!getContext().getPackageManager().hasSystemFeature("android.hardware.camera")) {
                return;
            }
            if (VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(getContext(), "android.permission.CAMERA") == 0) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.setFlags(268435456);
                getContext().startActivity(intent);
                visibleOrGone(false);
            } else if (Home.launcher == null) {
                visibleOrGone(false);
            } else {
                ActivityCompat.requestPermissions(Home.launcher, new String[]{"android.permission.CAMERA"}, 123);
                visibleOrGone(false);
            }
        } catch (Exception e) {
        }
    }

    public void onOffFlash() {
        if (!getContext().getPackageManager().hasSystemFeature("android.hardware.camera.flash")) {
            return;
        }
        if (VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(getContext(), "android.permission.CAMERA") == 0) {
            getCamera();
            if (this.camera != null && this.params != null) {
                try {
                    if (this.isFlashOn) {
                        this.params.setFlashMode("off");
                        this.camera.setParameters(this.params);
                        this.camera.stopPreview();
                        this.isFlashOn = false;
                        this.ivFlashLight.setImageResource(R.drawable.ic_flash);
                        return;
                    }
                    this.params.setFlashMode("torch");
                    this.camera.setParameters(this.params);
                    this.camera.startPreview();
                    this.isFlashOn = true;
                    this.ivFlashLight.setImageResource(R.drawable.ic_flash_on);
                } catch (Exception e) {
                }
            }
        } else if (Home.launcher == null) {
            visibleOrGone(false);
        } else {
            ActivityCompat.requestPermissions(Home.launcher, new String[]{"android.permission.CAMERA"}, 123);
            visibleOrGone(false);
        }
    }

    private void getCamera() {
        if (this.camera == null || this.params == null) {
            try {
                this.camera = Camera.open();
                this.params = this.camera.getParameters();
            } catch (Exception e) {
                Log.e("HuyAnh", "error get camera: " + e.getMessage());
            }
        }
    }
}
