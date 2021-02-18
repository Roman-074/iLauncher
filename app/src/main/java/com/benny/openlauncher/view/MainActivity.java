package com.benny.openlauncher.view;

import android.content.Intent;
import androidx.annotation.NonNull;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.benny.openlauncher.R;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, com.benny.openlauncher.activity.Home.class));
    }


}
