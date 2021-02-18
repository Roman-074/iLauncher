package com.benny.openlauncher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;


import com.benny.openlauncher.R;
import com.benny.openlauncher.adapter.AdapterSelectMusicPlayer;


public class SelectMusicPlayer extends AppCompatActivity {
    private AdapterSelectMusicPlayer adapterSelectMusicPlayer;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("my", "onCreate: SelectMusicPlayer");
        setContentView((int) R.layout.select_music_player);
        ButterKnife.bind((Activity) this);
        this.adapterSelectMusicPlayer = new AdapterSelectMusicPlayer(this);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), 1, false));
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(this.adapterSelectMusicPlayer);
        this.ivBack.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SelectMusicPlayer.this.onBackPressed();
            }
        });
    }
}
