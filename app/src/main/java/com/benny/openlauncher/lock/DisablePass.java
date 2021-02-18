package com.benny.openlauncher.lock;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.SettingsActivity;


public class DisablePass extends AppCompatActivity {
    @TargetApi(16)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_dissable_pass);
        int requestCode = getIntent().getIntExtra(Values.REQUEST_CODE, 0);
        FragmentDissablePasscode fragmentDissablePasscode;
        FragmentTransaction transaction;
        if (requestCode == SettingsActivity.KEY_DISABLE_PASS) {
            fragmentDissablePasscode = new FragmentDissablePasscode();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, fragmentDissablePasscode);
            transaction.commit();
        } else if (requestCode == SettingsActivity.KEY_NEW_PASS) {
            FragementChangePassCode fragementChangePassCode = new FragementChangePassCode();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, fragementChangePassCode);
            transaction.commit();
        } else if (requestCode == SettingsActivity.KEY_CHANGE_PASS) {
            fragmentDissablePasscode = new FragmentDissablePasscode();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, fragmentDissablePasscode);
            transaction.commit();
        }
    }
}
