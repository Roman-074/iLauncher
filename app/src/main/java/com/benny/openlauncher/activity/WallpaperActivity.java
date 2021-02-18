package com.benny.openlauncher.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.adapter.AdapterSliderWallpaper;


public class WallpaperActivity extends AppCompatActivity {
    private AdapterSliderWallpaper adapterSliderWallpaper;
    private long delayTime = 0;
    @BindView(R.id.ivPreview)
    public ImageView ivPreview;
    @BindView(R.id.recycler_view)
    RecyclerView rc;
    @BindView(R.id.rlCheck)
    RelativeLayout rlCheck;
    private WallpaperManager wallpaperManager;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("my", "onCreate: WallpaperActivity");
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.wallpaper_activity);
        ButterKnife.bind((Activity) this);
        this.ivPreview.setImageResource(R.drawable.w1);
        this.adapterSliderWallpaper = new AdapterSliderWallpaper(this);
        this.rc.setLayoutManager(new LinearLayoutManager(getApplicationContext(), 0, false));
        this.rc.setItemAnimator(new DefaultItemAnimator());
        this.rc.setAdapter(this.adapterSliderWallpaper);
        this.wallpaperManager = WallpaperManager.getInstance(this);
        this.rlCheck.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                WallpaperActivity.this.changeWallpaper();
            }
        });
    }

    private void changeWallpaper() {
        if (System.currentTimeMillis() - this.delayTime >= 2000) {
            this.delayTime = System.currentTimeMillis();
            try {
                this.wallpaperManager.setBitmap(((BitmapDrawable) getResources().getDrawable(this.adapterSliderWallpaper.getIdCurrent())).getBitmap());
            } catch (Exception e) {
            }
            Toast.makeText(this, getResources().getString(R.string.wall_paper_select_successful), Toast.LENGTH_SHORT).show();
        }
    }
}
