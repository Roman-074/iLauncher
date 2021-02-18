package com.benny.openlauncher.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.util.Common;



public class WallpaperActivityNewPreview extends AppCompatActivity {
    @BindView(R.id.ivPreview)
    ImageView ivPreview;
//    private InterstitialAd mInterstitialAdMob;
    @BindView(R.id.pb)
    ProgressBar pb;
    private long timeDelay = 0;
    @BindView(R.id.tvApply)
    TextView tvApply;

    class setWallpaper extends AsyncTask<Integer, Void, Void> {
        setWallpaper() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            WallpaperActivityNewPreview.this.pb.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Integer... integers) {
            try {
                WallpaperManager.getInstance(WallpaperActivityNewPreview.this).setBitmap(((BitmapDrawable) WallpaperActivityNewPreview.this.getResources().getDrawable(integers[0].intValue())).getBitmap());
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            WallpaperActivityNewPreview.this.pb.setVisibility(View.GONE);
            Toast.makeText(WallpaperActivityNewPreview.this, WallpaperActivityNewPreview.this.getResources().getString(R.string.wall_paper_select_successful), 0).show();
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.wallpaper_activity_new_preview);
//        initAdmobFullAd();
//        loadAdmobAd();
        ButterKnife.bind((Activity) this);
        this.ivPreview.setImageResource(getIntent().getExtras().getInt("idIv"));
        this.tvApply.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (System.currentTimeMillis() - WallpaperActivityNewPreview.this.timeDelay >= 2000) {
                    WallpaperActivityNewPreview.this.timeDelay = System.currentTimeMillis();
                    //WallpaperActivityNewPreview.this.showAdmobInterstitial();
                    new setWallpaper().execute(new Integer[]{Integer.valueOf(WallpaperActivityNewPreview.this.getIntent().getExtras().getInt("idIv"))});
                }
            }
        });
    }

//    private void initAdmobFullAd() {
//        this.mInterstitialAdMob = new InterstitialAd(this);
//        this.mInterstitialAdMob.setAdUnitId(Common.getPrefString(this, SecureEnvironment.getString("admob_inter")));
//        this.mInterstitialAdMob.setAdListener(new AdListener() {
//            public void onAdClosed() {
//                WallpaperActivityNewPreview.this.loadAdmobAd();
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

//    private void loadAdmobAd() {
//        if (this.mInterstitialAdMob != null && !this.mInterstitialAdMob.isLoaded()) {
//            this.mInterstitialAdMob.loadAd(new Builder().build());
//        }
//    }
//
//    private void showAdmobInterstitial() {
//        if (this.mInterstitialAdMob != null && this.mInterstitialAdMob.isLoaded()) {
//            this.mInterstitialAdMob.show();
//        }
//    }
}
