package com.benny.openlauncher.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HelpActivity extends AppCompatActivity {
    @BindView(R.id.ivBG)
    ImageView ivBG;
    @BindView(R.id.llHelp)
    LinearLayout llHelp;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("my", "onCreate: HelpActivity");
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(
                R.layout.help_activity);
        ButterKnife.bind((Activity) this);
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            if (wallpaperManager.getDrawable() != null) {
                this.ivBG.setImageDrawable(wallpaperManager.getDrawable());
            }
        } catch (Exception e) {
            Log.e("error set bg lock screen");
        }
        this.llHelp.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                HelpActivity.this.onBackPressed();
            }
        });
    }
}
