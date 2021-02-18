package com.benny.openlauncher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.benny.openlauncher.R;

import butterknife.ButterKnife;


public class SelectWidgetActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("my", "onCreate: SelectWidgetActivity");
        setContentView((int) R.layout.select_widget);
        ButterKnife.bind((Activity) this);
    }
}
