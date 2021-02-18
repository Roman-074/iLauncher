package com.benny.openlauncher.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.adapter.AdapterWallpaperNew;

import com.benny.openlauncher.util.Common;



public class WallpaperActivityNew extends AppCompatActivity {
    private AdapterWallpaperNew adapterWallpaperNew;
    private GridLayoutManager gridLayoutManager;
//    private InterstitialAd mInterstitialAdMob;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private WallpaperManager wallpaperManager;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.wallpaper_activity_new);
//        initAdmobFullAd();
//        loadAdmobAd();
        ButterKnife.bind((Activity) this);
        this.gridLayoutManager = new GridLayoutManager(this, 3);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(this.gridLayoutManager);
        this.adapterWallpaperNew = new AdapterWallpaperNew(this);
        this.recyclerView.setAdapter(this.adapterWallpaperNew);
        Log.i("jj", "onCreate: " + this.adapterWallpaperNew.getItemCount());
    }

//    private void initAdmobFullAd() {
//        this.mInterstitialAdMob = new InterstitialAd(this);
//        this.mInterstitialAdMob.setAdUnitId(Common.getPrefString(this, SecureEnvironment.getString("admob_inter")));
//        this.mInterstitialAdMob.setAdListener(new AdListener() {
//            public void onAdClosed() {
//                WallpaperActivityNew.this.loadAdmobAd();
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
//        if (Common.isApproved && this.mInterstitialAdMob != null && this.mInterstitialAdMob.isLoaded()) {
//            this.mInterstitialAdMob.show();
//        }
//    }

    public void onBackPressed() {
        super.onBackPressed();
//        showAdmobInterstitial();
    }
}
