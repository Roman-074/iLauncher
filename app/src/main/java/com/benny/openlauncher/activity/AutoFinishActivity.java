package com.benny.openlauncher.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AutoFinishActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("my", "onCreate: AutoFinishActivity");
        finish();
        super.onCreate(savedInstanceState);
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, AutoFinishActivity.class));
    }
}
