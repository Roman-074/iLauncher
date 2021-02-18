package com.benny.openlauncher.lock;

import android.app.WallpaperManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.benny.openlauncher.App;
import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.SettingsActivity;


public class FragmentDissablePasscode extends Fragment implements OnClickListener {
    private App application;
    private ImageView btn0;
    private ImageView btn1;
    private ImageView btn2;
    private ImageView btn3;
    private ImageView btn4;
    private ImageView btn5;
    private ImageView btn6;
    private ImageView btn7;
    private ImageView btn8;
    private ImageView btn9;
    private TextViewSanFranciscoLight btnCancel;
    private StringBuilder code;
    private StringBuilder code2;
    private boolean first = false;
    private ImageView imgBackroundBlur;
    private ImageView imgLockPassCode0;
    private ImageView imgLockPassCode1;
    private ImageView imgLockPassCode2;
    private ImageView imgLockPassCode3;
    private LinearLayout layoutPassCodeLock;
    private int request;
    private TextViewSanFranciscoLight tvTitle;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock_passcode, container, false);
        this.application = (App) getActivity().getApplication();
        this.layoutPassCodeLock = (LinearLayout) view.findViewById(R.id.layout_passcodelock);
        this.request = getActivity().getIntent().getIntExtra(Values.REQUEST_CODE, SettingsActivity.KEY_DISABLE_PASS);
        this.layoutPassCodeLock.setVisibility(View.VISIBLE);
        bindViewPassCodeLayout(view);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        this.imgBackroundBlur = (ImageView) view.findViewById(R.id.imgbgBlur);
        this.imgBackroundBlur.setImageDrawable(WallpaperManager.getInstance(getActivity()).getDrawable());
        this.tvTitle = (TextViewSanFranciscoLight) view.findViewById(R.id.tvTitle);
        this.btnCancel = (TextViewSanFranciscoLight) view.findViewById(R.id.btnCancel);
        this.imgLockPassCode0 = (ImageView) view.findViewById(R.id.lock_passcode_dot0);
        this.imgLockPassCode1 = (ImageView) view.findViewById(R.id.lock_passcode_dot1);
        this.imgLockPassCode2 = (ImageView) view.findViewById(R.id.lock_passcode_dot2);
        this.imgLockPassCode3 = (ImageView) view.findViewById(R.id.lock_passcode_dot3);
        this.btnCancel.setOnClickListener(this);
        this.code = new StringBuilder();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0:
                if (this.code.length() < 4) {
                    this.code.append(0);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn1:
                if (this.code.length() < 4) {
                    this.code.append(1);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn2:
                if (this.code.length() < 4) {
                    this.code.append(2);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn3:
                if (this.code.length() < 4) {
                    this.code.append(3);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn4:
                if (this.code.length() < 4) {
                    this.code.append(4);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn5:
                if (this.code.length() < 4) {
                    this.code.append(5);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn6:
                if (this.code.length() < 4) {
                    this.code.append(6);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn7:
                if (this.code.length() < 4) {
                    this.code.append(7);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn8:
                if (this.code.length() < 4) {
                    this.code.append(8);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btn9:
                if (this.code.length() < 4) {
                    this.code.append(9);
                    setImageCode();
                    return;
                }
                return;
            case R.id.btnCancel:
                if (this.code.length() > 0) {
                    this.code.deleteCharAt(this.code.length() - 1);
                    setImageCode();
                    return;
                }
                getActivity().finish();
                return;
            default:
                return;
        }
    }

    private void setImageCode() {
        switch (this.code.length()) {
            case 0:
                setDotSoild(0);
                return;
            case 1:
                setDotSoild(1);
                return;
            case 2:
                setDotSoild(2);
                return;
            case 3:
                setDotSoild(3);
                return;
            case 4:
                setDotSoild(4);
                if (!this.application.pref.getString(Values.PASSCODE, "").equals(this.code.toString())) {
                    Utils.vibrate(getActivity());
                    Handler handler = new Handler();
                    this.code = new StringBuilder();
                    this.tvTitle.setText(R.string.try_again);
                    Utils.shake(getActivity(), this.imgLockPassCode0, this.imgLockPassCode1, this.imgLockPassCode2, this.imgLockPassCode3);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            FragmentDissablePasscode.this.setImageCode();
                        }
                    }, 1000);
                    return;
                } else if (this.request == SettingsActivity.KEY_DISABLE_PASS) {
                    this.application.editor.putBoolean(Values.ENABLE_PASSCODE, false);
                    this.application.editor.commit();
                    getActivity().finish();
                    return;
                } else if (this.request == SettingsActivity.KEY_CHANGE_PASS) {
                    getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.fragment, new FragementChangePassCode()).commit();
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    private void setDotSoild(int numberDot) {
        switch (numberDot) {
            case 0:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_hollow);
                return;
            case 1:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_hollow);
                return;
            case 2:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_hollow);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_hollow);
                return;
            case 3:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_hollow);
                return;
            case 4:
                this.imgLockPassCode0.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode1.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode2.setImageResource(R.drawable.passcode_dot_soild);
                this.imgLockPassCode3.setImageResource(R.drawable.passcode_dot_soild);
                return;
            default:
                return;
        }
    }

    private void bindViewPassCodeLayout(View view) {
        this.btn0 = (ImageView) view.findViewById(R.id.btn0);
        this.btn0.setOnClickListener(this);
        this.btn1 = (ImageView) view.findViewById(R.id.btn1);
        this.btn1.setOnClickListener(this);
        this.btn2 = (ImageView) view.findViewById(R.id.btn2);
        this.btn2.setOnClickListener(this);
        this.btn3 = (ImageView) view.findViewById(R.id.btn3);
        this.btn3.setOnClickListener(this);
        this.btn4 = (ImageView) view.findViewById(R.id.btn4);
        this.btn4.setOnClickListener(this);
        this.btn5 = (ImageView) view.findViewById(R.id.btn5);
        this.btn5.setOnClickListener(this);
        this.btn6 = (ImageView) view.findViewById(R.id.btn6);
        this.btn6.setOnClickListener(this);
        this.btn7 = (ImageView) view.findViewById(R.id.btn7);
        this.btn7.setOnClickListener(this);
        this.btn8 = (ImageView) view.findViewById(R.id.btn8);
        this.btn8.setOnClickListener(this);
        this.btn9 = (ImageView) view.findViewById(R.id.btn9);
        this.btn9.setOnClickListener(this);
    }
}
