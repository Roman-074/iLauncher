package com.benny.openlauncher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.util.Tool;


public class LauncherLoadingIcon extends FrameLayout {
    private static final Long ANIM_DURATION = Long.valueOf(250);
    private static final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private final Runnable ANIM_1;
    private final Runnable ANIM_2;
    private final Runnable ANIM_3;
    private final Runnable ANIM_4;
    private ImageView[] iv;
    private boolean loading;

    public boolean isLoading() {
        return this.loading;
    }

    public void setLoading(boolean loading) {
        if (loading != this.loading && loading) {
            removeCallbacks(null);
            post(this.ANIM_1);
        }
        this.loading = loading;
    }

    public LauncherLoadingIcon(Context context) {
        this(context, null);
    }

    public LauncherLoadingIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.loading = false;
        this.ANIM_1 = new Runnable() {
            public void run() {
                if (LauncherLoadingIcon.this.iv != null && LauncherLoadingIcon.this.iv[0] != null) {
                    LauncherLoadingIcon.this.iv[0].setScaleX(0.0f);
                    LauncherLoadingIcon.this.iv[1].setScaleY(0.0f);
                    LauncherLoadingIcon.this.iv[2].setScaleX(0.0f);
                    LauncherLoadingIcon.this.iv[0].animate().setDuration(LauncherLoadingIcon.ANIM_DURATION.longValue()).scaleX(Tool.DEFAULT_BACKOFF_MULT).alpha(Tool.DEFAULT_BACKOFF_MULT).withEndAction(LauncherLoadingIcon.this.ANIM_2).setInterpolator(LauncherLoadingIcon.interpolator);
                }
            }
        };
        this.ANIM_2 = new Runnable() {
            public void run() {
                if (LauncherLoadingIcon.this.iv != null && LauncherLoadingIcon.this.iv[1] != null) {
                    LauncherLoadingIcon.this.iv[1].animate().setDuration(LauncherLoadingIcon.ANIM_DURATION.longValue()).scaleY(Tool.DEFAULT_BACKOFF_MULT).alpha(Tool.DEFAULT_BACKOFF_MULT).withEndAction(LauncherLoadingIcon.this.ANIM_3).setInterpolator(LauncherLoadingIcon.interpolator);
                }
            }
        };
        this.ANIM_3 = new Runnable() {
            public void run() {
                if (LauncherLoadingIcon.this.iv != null && LauncherLoadingIcon.this.iv[2] != null) {
                    LauncherLoadingIcon.this.iv[2].animate().setDuration(LauncherLoadingIcon.ANIM_DURATION.longValue()).scaleX(Tool.DEFAULT_BACKOFF_MULT).alpha(Tool.DEFAULT_BACKOFF_MULT).withEndAction(LauncherLoadingIcon.this.ANIM_4).setInterpolator(LauncherLoadingIcon.interpolator);
                }
            }
        };
        this.ANIM_4 = new Runnable() {
            public void run() {
                LauncherLoadingIcon.this.iv[0].animate().setDuration(LauncherLoadingIcon.ANIM_DURATION.longValue()).alpha(0.0f).setInterpolator(LauncherLoadingIcon.interpolator);
                LauncherLoadingIcon.this.iv[1].animate().setDuration(LauncherLoadingIcon.ANIM_DURATION.longValue()).alpha(0.0f).setInterpolator(LauncherLoadingIcon.interpolator);
                if (LauncherLoadingIcon.this.loading) {
                    LauncherLoadingIcon.this.iv[2].animate().setDuration(LauncherLoadingIcon.ANIM_DURATION.longValue()).alpha(0.0f).withEndAction(LauncherLoadingIcon.this.ANIM_1).setInterpolator(LauncherLoadingIcon.interpolator);
                } else {
                    LauncherLoadingIcon.this.iv[2].animate().setDuration(LauncherLoadingIcon.ANIM_DURATION.longValue()).alpha(0.0f).setInterpolator(LauncherLoadingIcon.interpolator);
                }
            }
        };
        int[] res = new int[]{R.drawable.ol_loading_3, R.drawable.ol_loading_2, R.drawable.ol_loading_1};
        this.iv = new ImageView[3];
        for (int i = 0; i < this.iv.length; i++) {
            this.iv[i] = new ImageView(getContext());
            this.iv[i].setImageResource(res[i]);
            addView(this.iv[i]);
            this.iv[i].setLayoutParams(new LayoutParams(-1, -1));
            this.iv[i].setScaleType(ScaleType.CENTER_CROP);
        }
        this.iv[0].setScaleX(0.0f);
        this.iv[1].setScaleY(0.0f);
        this.iv[2].setScaleX(0.0f);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.iv[0].setPivotX((float) getHeight());
        this.iv[0].setPivotY((float) getHeight());
        this.iv[1].setPivotY((float) getHeight());
        this.iv[1].setPivotX(0.0f);
        this.iv[2].setPivotX(0.0f);
        this.iv[2].setPivotY(0.0f);
        super.onLayout(changed, left, top, right, bottom);
    }
}
