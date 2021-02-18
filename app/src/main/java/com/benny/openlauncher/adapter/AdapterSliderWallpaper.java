package com.benny.openlauncher.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.WallpaperActivity;
import com.benny.openlauncher.util.Constant;
import com.bumptech.glide.Glide;


public class AdapterSliderWallpaper extends Adapter<AdapterSliderWallpaper.AppSearchViewHolder> {
    private WallpaperActivity activity;
    private int selectedPosition = 0;

    class AppSearchViewHolder extends ViewHolder {
        private ImageView ivCheck;
        public ImageView ivPreview;

        public AppSearchViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Glide.with(AdapterSliderWallpaper.this.activity).load(Integer.valueOf(Constant.BG[AppSearchViewHolder.this.getAdapterPosition()])).into(AdapterSliderWallpaper.this.activity.ivPreview);
                    AdapterSliderWallpaper.this.selectedPosition = AppSearchViewHolder.this.getAdapterPosition();
                    AdapterSliderWallpaper.this.notifyDataSetChanged();
                }
            });
            this.ivPreview = (ImageView) itemView.findViewById(R.id.ivPreview);
            this.ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
        }
    }

    public AdapterSliderWallpaper(WallpaperActivity activity) {
        this.activity = activity;
    }

    public AdapterSliderWallpaper.AppSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_activity_slider_item, null));
    }
    @Override
    public void onBindViewHolder(AdapterSliderWallpaper.AppSearchViewHolder holder, int position) {
        Glide.with(this.activity).load(Integer.valueOf(Constant.BG[position])).into(holder.ivPreview);
        if (position == this.selectedPosition) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.GONE);
        }
    }

    private int genpx(Context context, int dp) {
        int pxTemp = (int) TypedValue.applyDimension(1, (float) dp, context.getResources().getDisplayMetrics());
        return pxTemp != 0 ? pxTemp : dp * 4;
    }

    public int getItemCount() {
        return Constant.BG.length;
    }

    public int getIdCurrent() {
        return Constant.BG[this.selectedPosition];
    }
}
