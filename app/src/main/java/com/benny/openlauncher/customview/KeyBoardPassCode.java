package com.benny.openlauncher.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benny.openlauncher.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class KeyBoardPassCode extends RelativeLayout implements OnClickListener {
    @BindView(R.id.ivDot0)
    ImageView ivDot0;
    @BindView(R.id.ivDot1)
    ImageView ivDot1;
    @BindView(R.id.ivDot2)
    ImageView ivDot2;
    @BindView(R.id.ivDot3)
    ImageView ivDot3;
    private KeyBoardPassCodeAction keyBoardPassCodeAction = null;
    @BindView(R.id.llDot)
    LinearLayout llDot;
    private String passCodeCurrent = "";
    Animation shakeAnimation;
    @BindView(R.id.imgPass)
    ImageView imgPass;
    @BindView(R.id.tv0)
    TextView tv0;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.tv5)
    TextView tv5;
    @BindView(R.id.tv6)
    TextView tv6;
    @BindView(R.id.tv7)
    TextView tv7;
    @BindView(R.id.tv8)
    TextView tv8;
    @BindView(R.id.tv9)
    TextView tv9;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    public KeyBoardPassCode(Context context) {
        super(context);
        initView();
    }

    public KeyBoardPassCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public KeyBoardPassCode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setKeyBoardPassCodeAction(KeyBoardPassCodeAction keyBoardPassCodeAction) {
        this.keyBoardPassCodeAction = keyBoardPassCodeAction;
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.view_keyboard_passcode, null);
        addView(view);
        ButterKnife.bind((Object) this, view);
        this.tv0.setOnClickListener(this);
        this.tv1.setOnClickListener(this);
        this.tv2.setOnClickListener(this);
        this.tv3.setOnClickListener(this);
        this.tv4.setOnClickListener(this);
        this.tv5.setOnClickListener(this);
        this.tv6.setOnClickListener(this);
        this.tv7.setOnClickListener(this);
        this.tv8.setOnClickListener(this);
        this.tv9.setOnClickListener(this);
        this.shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_new);
        this.shakeAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (KeyBoardPassCode.this.keyBoardPassCodeAction != null) {
                    KeyBoardPassCode.this.keyBoardPassCodeAction.onDone(KeyBoardPassCode.this.passCodeCurrent.substring(0, 6));
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void onClick(View v) {
        this.passCodeCurrent += getChar(v);
        initDot();
    }

    private void initDot() {
        switch (passCodeCurrent.length()){
            case 0:
                imgPass.setImageResource(R.drawable.pas7);
                break;
            case 1:
                imgPass.setImageResource(R.drawable.pas1);
                break;
            case 2:
                imgPass.setImageResource(R.drawable.pas2);
                break;
            case 3:
                imgPass.setImageResource(R.drawable.pas3);
                break;
            case 4:
                imgPass.setImageResource(R.drawable.pas4);
                break;
            case 5:
                imgPass.setImageResource(R.drawable.pas5);
                break;
            case 6:
                imgPass.setImageResource(R.drawable.pas6);
                break;

        }
        if (this.passCodeCurrent.length() > 0) {
            this.ivDot0.setImageResource(R.drawable.keyboard_passcode_bg_iv_dot_small_fill);
            if (this.passCodeCurrent.length() > 1) {
                this.ivDot1.setImageResource(R.drawable.keyboard_passcode_bg_iv_dot_small_fill);
                if (this.passCodeCurrent.length() > 2) {
                    this.ivDot2.setImageResource(R.drawable.keyboard_passcode_bg_iv_dot_small_fill);
                    if (this.passCodeCurrent.length() > 3) {
                        this.ivDot3.setImageResource(R.drawable.keyboard_passcode_bg_iv_dot_small_fill);
                        this.llDot.startAnimation(this.shakeAnimation);
                    }
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void resetDot(String title) {
        imgPass.setImageResource(R.drawable.pas7);
        this.ivDot0.setImageResource(R.drawable.keyboard_passcode_bg_iv_dot_small);
        this.ivDot1.setImageResource(R.drawable.keyboard_passcode_bg_iv_dot_small);
        this.ivDot2.setImageResource(R.drawable.keyboard_passcode_bg_iv_dot_small);
        this.ivDot3.setImageResource(R.drawable.keyboard_passcode_bg_iv_dot_small);
        try {
            ((Vibrator) getContext().getSystemService("vibrator")).vibrate(75);
        } catch (Exception e) {
        }
        this.tvTitle.setText(title);
        this.passCodeCurrent = "";
    }

    public void setTitle(String title) {
        if (this.tvTitle != null) {
            this.tvTitle.setText(title);
        }
    }

    private String getChar(View v) {
        switch (v.getId()) {
            case R.id.tv0:
                return "0";
            case R.id.tv1:
                return "1";
            case R.id.tv2:
                return "2";
            case R.id.tv3:
                return "3";
            case R.id.tv4:
                return "4";
            case R.id.tv5:
                return "5";
            case R.id.tv6:
                return "6";
            case R.id.tv7:
                return "7";
            case R.id.tv8:
                return "8";
            case R.id.tv9:
                return "9";
            default:
                return "";
        }
    }
}
