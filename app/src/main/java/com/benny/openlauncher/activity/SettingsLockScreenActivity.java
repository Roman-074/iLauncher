package com.benny.openlauncher.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.customview.KeyBoardPassCode;
import com.benny.openlauncher.customview.KeyBoardPassCodeAction;


public class SettingsLockScreenActivity extends AppCompatActivity {
    private String currentPassCode = "";
    private boolean flagLock = false;
    @BindView(R.id.ibBgLockScreen)
    ImageView ibBgLockScreen;
    @BindView(R.id.keyboard)
    KeyBoardPassCode keyBoardPassCode;
    private String pass;
    private int statusActivity = 0;
    @BindView(R.id.tvCancel)
    TextView tvCancel;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        android.util.Log.d("my", "onCreate: SettingsLockScreenActivity");
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView( R.layout.activity_settings_lock_screen);
        ButterKnife.bind((Activity) this);
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            if (wallpaperManager.getDrawable() != null) {
                this.ibBgLockScreen.setImageDrawable(wallpaperManager.getDrawable());
            }
        } catch (Exception e) {
            Log.e("error set bg lock screen");
        }
        this.statusActivity = getIntent().getExtras().getInt(NotificationCompat.CATEGORY_STATUS);
        android.util.Log.i("jj", "onCreate: " + this.statusActivity);
        this.tvCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsLockScreenActivity.this.setResult(-1);
                SettingsLockScreenActivity.this.finish();
            }
        });
        this.keyBoardPassCode.setKeyBoardPassCodeAction(new KeyBoardPassCodeAction() {
            public void onDone(String passCode) {
                switch (SettingsLockScreenActivity.this.statusActivity) {
                    case 0:
                        Log.d("!isLockScreenEnablePassCode");
                        if (!SettingsLockScreenActivity.this.flagLock) {
                            SettingsLockScreenActivity.this.pass = passCode;
                            SettingsLockScreenActivity.this.keyBoardPassCode.resetDot(SettingsLockScreenActivity.this.getString(R.string.keyboard_pass_code_title_confirm));
                            SettingsLockScreenActivity.this.flagLock = true;
                            return;
                        } else if (SettingsLockScreenActivity.this.pass.equals(passCode)) {
                            Setup.appSettings().setLockScreenEnablePassCode(true);
                            Setup.appSettings().setPassCodeLockScreen(SettingsLockScreenActivity.this.pass);
                            Setup.appSettings().setLockScreenEnable(true);
                            Toast.makeText(SettingsLockScreenActivity.this, R.string.keyboard_pass_code_done, Toast.LENGTH_SHORT).show();
                            SettingsLockScreenActivity.this.setResult(-1);
                            SettingsLockScreenActivity.this.finish();
                            return;
                        } else {
                            SettingsLockScreenActivity.this.keyBoardPassCode.resetDot(SettingsLockScreenActivity.this.getString(R.string.keyboard_pass_code_title));
                            SettingsLockScreenActivity.this.flagLock = false;
                            return;
                        }
                    case 1:
                        if (passCode.equals(Setup.appSettings().getPassCodeLockScreen())) {
                            Setup.appSettings().setPassCodeLockScreen("");
                            SettingsLockScreenActivity.this.setResult(-1);
                            SettingsLockScreenActivity.this.finish();
                            return;
                        }
                        SettingsLockScreenActivity.this.statusActivity = 1;
                        SettingsLockScreenActivity.this.keyBoardPassCode.resetDot(SettingsLockScreenActivity.this.getString(R.string.keyboard_pass_code_title));
                        return;
                    case 2:
                        android.util.Log.d("jj", "onDone: change pass");
                        if (!Setup.appSettings().getPassCodeLockScreen().isEmpty()) {
                            if (passCode.equals(Setup.appSettings().getPassCodeLockScreen())) {
                                SettingsLockScreenActivity.this.statusActivity = 0;
                                SettingsLockScreenActivity.this.keyBoardPassCode.resetDot(SettingsLockScreenActivity.this.getString(R.string.keyboard_pass_code_title));
                                return;
                            }
                            SettingsLockScreenActivity.this.statusActivity = 2;
                            SettingsLockScreenActivity.this.keyBoardPassCode.resetDot(SettingsLockScreenActivity.this.getString(R.string.keyboard_pass_code_title));
                            return;
                        }
                        return;
                    case 11:
                        Log.d("off lock screen");
                        return;
                    default:
                        return;
                }
            }
        });
    }
}
