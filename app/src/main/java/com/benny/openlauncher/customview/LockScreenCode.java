package com.benny.openlauncher.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.manager.Setup;


public class LockScreenCode extends RelativeLayout {
    @BindView(R.id.keyboard)
    KeyBoardPassCode keyboard;
    private LockScreenCodeAction lockScreenCodeAction = null;

    public LockScreenCode(Context context) {
        super(context);
        initView();
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        View view = inflate(getContext(), R.layout.view_lock_screen_code, null);
        addView(view);
        ButterKnife.bind((Object) this, view);
        if (Setup.get() != null) {
            if (Setup.appSettings().isLockScreenEnablePassCode()) {
                this.keyboard.setVisibility(0);
            } else {
                this.keyboard.setVisibility(8);
            }
        }
        this.keyboard.setKeyBoardPassCodeAction(new KeyBoardPassCodeAction() {
            public void onDone(String passCode) {
                if (passCode.equals(Setup.appSettings().getPassCodeLockScreen()) && LockScreenCode.this.lockScreenCodeAction != null) {
                    LockScreenCode.this.lockScreenCodeAction.unLock();
                }
                LockScreenCode.this.keyboard.resetDot(LockScreenCode.this.getContext().getString(R.string.keyboard_pass_code_title));
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void refreshView(LockScreenCodeAction lockScreenCodeAction) {
        this.lockScreenCodeAction = lockScreenCodeAction;
        if (Setup.get() != null && this.keyboard != null) {
            if (Setup.appSettings().isLockScreenEnablePassCode()) {
                this.keyboard.setVisibility(0);
            } else {
                this.keyboard.setVisibility(8);
            }
        }
    }
}
