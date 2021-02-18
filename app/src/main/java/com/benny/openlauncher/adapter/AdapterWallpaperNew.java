package com.benny.openlauncher.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.WallpaperActivityNew;
import com.benny.openlauncher.activity.WallpaperActivityNewPreview;
import com.benny.openlauncher.util.Constant;


public class AdapterWallpaperNew extends Adapter<AdapterWallpaperNew.AppSearchViewHolder> {
    private WallpaperActivityNew activityNew;
    private Context context;
//    private InterstitialAd mInterstitialAdMob;

    public class AppSearchViewHolder extends ViewHolder {
        public ImageView ivWallpaper = ((ImageView) this.itemView.findViewById(R.id.ivWallpaperNew));

        public AppSearchViewHolder(View view) {
            super(view);
            Log.i("jj", "AppSearchViewHolder: ");
            this.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Intent it = new Intent(AdapterWallpaperNew.this.activityNew, WallpaperActivityNewPreview.class);
                    it.putExtra("idIv", Constant.BG[AppSearchViewHolder.this.getAdapterPosition()]);
                    AdapterWallpaperNew.this.activityNew.startActivity(it);
//                    AdapterWallpaperNew.this.showAdmobInterstitial();
                }
            });
        }
    }

    public AdapterWallpaperNew(WallpaperActivityNew activityNew) {
        this.activityNew = activityNew;
        this.context = activityNew;
    }

    public AdapterWallpaperNew.AppSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_activity_new_item, parent, false);
//        initAdmobFullAd();
//        loadAdmobAd();
        return new AdapterWallpaperNew.AppSearchViewHolder(itemView);
    }
@Override
    public void onBindViewHolder(AdapterWallpaperNew.AppSearchViewHolder holder, int position) {
        holder.ivWallpaper.setImageResource(Constant.BG[position]);
    }

    public int getItemCount() {
        return Constant.BG.length;
    }

//    private void initAdmobFullAd() {
//        this.mInterstitialAdMob = new InterstitialAd(this.context);
//        this.mInterstitialAdMob.setAdUnitId(Common.getPrefString(this.context, SecureEnvironment.getString("admob_inter")));
//        this.mInterstitialAdMob.setAdListener(new AdListener() {
//            public void onAdClosed() {
//                AdapterWallpaperNew.this.loadAdmobAd();
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
