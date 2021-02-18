package com.benny.openlauncher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.adapter.AdapterViewPagerSplash;
import com.benny.openlauncher.base.BaseActivity;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.core.sql.DBHelper;
import com.benny.openlauncher.util.Constant;
import com.benny.openlauncher.util.Tool;


public class SplashActivity extends BaseActivity {
    private static final int MY_REQUEST_CODE = 1;
    private AdapterViewPagerSplash adapterViewPagerSplash;
    private BaseApplication baseApplication;
    @BindView(R.id.cbUseWallpaper)
    AppCompatCheckBox cbUse;
    public DBHelper dbHelper;
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.rlSplash)
    RelativeLayout rlSplash;
    @BindView(R.id.tvStart)
    TextView tvStart;
    @BindView(R.id.vp)
    ViewPager vp;

    class setDefaultWallpaper extends AsyncTask<Integer, Void, Void> {
        setDefaultWallpaper() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            SplashActivity.this.pb.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Integer... integers) {
            try {
                WallpaperManager.getInstance(SplashActivity.this).setBitmap(((BitmapDrawable) SplashActivity.this.getResources().getDrawable(integers[0].intValue())).getBitmap());
            } catch (Exception e) {
                Log.e("HuyAnh", "error set background default: " + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SplashActivity.this.startHome();
            //SplashActivity.this.showAdmobInterstitial();
            SplashActivity.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        //new Builder().addTestDevice("E873FCC3AB6220B9E3B68D32D8FE7E52");
//        initAdmobFullAd();
//        loadAdmobAd();
        setContentView( R.layout.activity_splash);
        this.baseApplication = (BaseApplication) getApplication();
        this.dbHelper = new DBHelper(this);
//        if (!Common.getPrefBoolean(this, "isToken")) {
//            startService(new Intent(this, RegistrationIntentService.class));
//        }
        try {
            if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                this.dbHelper.createDataBase();
                this.dbHelper.openDataBase();
                this.baseApplication.loadNewDataConfig();
            } else if (VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            } else {
                this.dbHelper.createDataBase();
                this.dbHelper.openDataBase();
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "error creat, open db: " + e.getMessage());
        }
        ButterKnife.bind(this);
        if (this.baseApplication != null) {
            this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_OPEN_SPLASH);
        }
        this.cbUse.setTypeface(this.baseApplication.getBaseTypeface().getRegular());
        this.cbUse.setText(getString(R.string.splash_start_use_wallpaper).replace("xxxxxx", getString(R.string.app_name)));
        this.adapterViewPagerSplash = new AdapterViewPagerSplash(getSupportFragmentManager(), this);
        this.vp.setAdapter(this.adapterViewPagerSplash);
        this.vp.setClipToPadding(false);
        this.vp.setPadding(120, 0, 120, 0);
        this.vp.setPageMargin(30);
        this.vp.setOffscreenPageLimit(1);
        new Handler().postDelayed(() -> new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.rlSplash.setVisibility(View.GONE);
                SplashActivity.this.tvStart.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (SplashActivity.this.baseApplication != null) {
                            SplashActivity.this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_GET_STARTED);
                        }
                        if (SplashActivity.this.cbUse.isChecked()) {
                            new setDefaultWallpaper().execute(new Integer[]{Integer.valueOf(Constant.BG[SplashActivity.this.vp.getCurrentItem()])});
                            SplashActivity.this.tvStart.setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                }
                            });
                            return;
                        }
                        SplashActivity.this.startHome();
                        //SplashActivity.this.showAdmobInterstitial();
                        SplashActivity.this.finish();
                    }
                });
            }
        }, 1000), Tool.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
                    return;
                }
                try {
                    this.dbHelper.createDataBase();
                    this.dbHelper.openDataBase();
                    this.baseApplication.loadNewDataConfig();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            default:
                return;
        }
    }

    @SuppressLint({"WrongConstant"})
    private void startHome() {
        Intent intent;
        try {
            intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.setFlags(268435456);
            intent.addFlags(33554432);
            intent.addFlags(16777216);
            intent.addFlags(2097152);
            intent.setPackage(getPackageName());
            startActivity(intent);
        } catch (Exception e) {
            intent = new Intent(this, Home.class);
            intent.addFlags(65536);
            startActivity(intent);
        }
    }

//    private void initAdmobFullAd() {
//        this.mInterstitialAdMob = new InterstitialAd(this);
//        this.mInterstitialAdMob.setAdUnitId(Common.getPrefString(this, SecureEnvironment.getString("admob_inter")));
//        this.mInterstitialAdMob.setAdListener(new AdListener() {
//            public void onAdClosed() {
//                SplashActivity.this.loadAdmobAd();
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
//                Log.i("Failed status:", i + "");
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
//        if (this.mInterstitialAdMob != null && !this.mInterstitialAdMob.isLoaded()) {
//            this.mInterstitialAdMob.loadAd(new Builder().build());
//        }
//    }
//
//    private void showAdmobInterstitial() {
//        if (this.mInterstitialAdMob != null && this.mInterstitialAdMob.isLoaded()) {
//            this.mInterstitialAdMob.show();
//            Log.i("Show", "DONE");
//        }
//    }
}
