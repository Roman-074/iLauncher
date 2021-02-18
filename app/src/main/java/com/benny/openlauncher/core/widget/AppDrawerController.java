package com.benny.openlauncher.core.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout.LayoutParams;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.Tool;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class AppDrawerController extends RevealFrameLayout {
    private Animator appDrawerAnimator;
    private CallBack closeCallBack;
    private Long drawerAnimationTime = Long.valueOf(200);
    public int drawerMode;
    public AppDrawerVertical drawerViewGrid;
    public AppDrawerPaged drawerViewPaged;
    private CallBack openCallBack;

    public interface CallBack {
        void onEnd();

        void onStart();
    }

    public static class DrawerMode {
        public static final int HORIZONTAL_PAGED = 0;
        public static final int VERTICAL = 1;
    }

    public AppDrawerController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppDrawerController(Context context) {
        super(context);
    }

    public AppDrawerController(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCallBack(CallBack openCallBack, CallBack closeCallBack) {
        this.openCallBack = openCallBack;
        this.closeCallBack = closeCallBack;
    }

    public View getDrawer() {
        return getChildAt(0);
    }

    public void open(int cx, int cy, int startRadius, int finalRadius) {
        this.appDrawerAnimator = ViewAnimationUtils.createCircularReveal(getChildAt(0), cx, cy, (float) startRadius, (float) finalRadius);
        this.appDrawerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.appDrawerAnimator.setDuration(this.drawerAnimationTime.longValue());
        this.appDrawerAnimator.setStartDelay(100);
        this.openCallBack.onStart();
        this.appDrawerAnimator.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator p1) {
                AppDrawerController.this.getChildAt(0).setVisibility(VISIBLE);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(AppDrawerController.this.getBackground(), new PropertyValuesHolder[]{PropertyValuesHolder.ofInt("alpha", new int[]{0, 255})});
                animator.setDuration(200);
                animator.start();
                switch (AppDrawerController.this.drawerMode) {
                    case 0:
                        for (int i = 0; i < AppDrawerController.this.drawerViewPaged.pages.size(); i++) {
                            ((ViewGroup) AppDrawerController.this.drawerViewPaged.pages.get(i)).findViewById(R.id.group).setAlpha(Tool.DEFAULT_BACKOFF_MULT);
                        }
                        if (AppDrawerController.this.drawerViewPaged.pages.size() > 0) {
                            View mGrid = ((ViewGroup) AppDrawerController.this.drawerViewPaged.pages.get(AppDrawerController.this.drawerViewPaged.getCurrentItem())).findViewById(R.id.group);
                            mGrid.setAlpha(0.0f);
                            mGrid.animate().alpha(Tool.DEFAULT_BACKOFF_MULT).setDuration(150).setStartDelay(AppDrawerController.this.drawerAnimationTime.longValue() - 50).setInterpolator(new AccelerateDecelerateInterpolator());
                            return;
                        }
                        return;
                    case 1:
                        AppDrawerController.this.drawerViewGrid.recyclerView.setAlpha(0.0f);
                        AppDrawerController.this.drawerViewGrid.recyclerView.animate().alpha(Tool.DEFAULT_BACKOFF_MULT).setDuration(150).setStartDelay(AppDrawerController.this.drawerAnimationTime.longValue() - 50).setInterpolator(new AccelerateDecelerateInterpolator());
                        return;
                    default:
                        return;
                }
            }

            public void onAnimationEnd(Animator p1) {
                AppDrawerController.this.openCallBack.onEnd();
            }

            public void onAnimationCancel(Animator p1) {
            }

            public void onAnimationRepeat(Animator p1) {
            }
        });
        this.appDrawerAnimator.start();
    }

    public void close(int cx, int cy, int startRadius, int finalRadius) {
        if (this.appDrawerAnimator != null && !this.appDrawerAnimator.isRunning()) {
            this.appDrawerAnimator = ViewAnimationUtils.createCircularReveal(getChildAt(0), cx, cy, (float) finalRadius, (float) startRadius);
            this.appDrawerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            this.appDrawerAnimator.setDuration(this.drawerAnimationTime.longValue());
            this.appDrawerAnimator.addListener(new AnimatorListener() {
                public void onAnimationStart(Animator p1) {
                    AppDrawerController.this.closeCallBack.onStart();
                    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(AppDrawerController.this.getBackground(), new PropertyValuesHolder[]{PropertyValuesHolder.ofInt("alpha", new int[]{255, 0})});
                    animator.setDuration(200);
                    animator.start();
                }

                public void onAnimationEnd(Animator p1) {
                    AppDrawerController.this.closeCallBack.onEnd();
                }

                public void onAnimationCancel(Animator p1) {
                }

                public void onAnimationRepeat(Animator p1) {
                }
            });
            switch (this.drawerMode) {
                case 0:
                    if (this.drawerViewPaged.pages.size() > 0) {
                        ((ViewGroup) this.drawerViewPaged.pages.get(this.drawerViewPaged.getCurrentItem())).findViewById(R.id.group).animate().setStartDelay(0).alpha(0.0f).setDuration(60).withEndAction(new Runnable() {
                            public void run() {
                                AppDrawerController.this.appDrawerAnimator.start();
                            }
                        });
                        return;
                    }
                    return;
                case 1:
                    this.drawerViewGrid.recyclerView.animate().setStartDelay(0).alpha(0.0f).setDuration(60).withEndAction(new Runnable() {
                        public void run() {
                            AppDrawerController.this.appDrawerAnimator.start();
                        }
                    });
                    return;
                default:
                    return;
            }
        }
    }

    public void init() {
        if (!isInEditMode()) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            this.drawerMode = Setup.appSettings().getDrawerStyle();
            switch (this.drawerMode) {
                case 0:
                    this.drawerViewPaged = (AppDrawerPaged) layoutInflater.inflate(R.layout.view_app_drawer_paged, this, false);
                    addView(this.drawerViewPaged);
                    addView((PagerIndicator) layoutInflater.inflate(R.layout.view_drawer_indicator, this, false));
                    return;
                case 1:
                    this.drawerViewGrid = (AppDrawerVertical) layoutInflater.inflate(R.layout.view_app_drawer_vertical, this, false);
                    int marginHorizontal = Tool.dp2px(Setup.appSettings().getVerticalDrawerHorizontalMargin(), getContext());
                    int marginVertical = Tool.dp2px(Setup.appSettings().getVerticalDrawerVerticalMargin(), getContext());
                    LayoutParams lp = new LayoutParams(-1, -1);
                    lp.leftMargin = marginHorizontal;
                    lp.rightMargin = marginHorizontal;
                    lp.topMargin = marginVertical;
                    lp.bottomMargin = marginVertical;
                    addView(this.drawerViewGrid, lp);
                    return;
                default:
                    return;
            }
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (VERSION.SDK_INT >= 20) {
            setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
        }
        return insets;
    }

    public void reloadDrawerCardTheme() {
        switch (this.drawerMode) {
            case 0:
                this.drawerViewPaged.resetAdapter();
                return;
            case 1:
                if (Setup.appSettings().isDrawerShowCardView()) {
                    this.drawerViewGrid.setCardBackgroundColor(Setup.appSettings().getDrawerCardColor());
                    this.drawerViewGrid.setCardElevation((float) Tool.dp2px(4, getContext()));
                } else {
                    this.drawerViewGrid.setCardBackgroundColor(0);
                    this.drawerViewGrid.setCardElevation(0.0f);
                }
                if (this.drawerViewGrid.gridDrawerAdapter != null) {
                    this.drawerViewGrid.gridDrawerAdapter.notifyDataSetChanged();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void scrollToStart() {
        switch (this.drawerMode) {
            case 0:
                this.drawerViewPaged.setCurrentItem(0, false);
                return;
            case 1:
                this.drawerViewGrid.recyclerView.scrollToPosition(0);
                return;
            default:
                return;
        }
    }

    public void setHome(Home home) {
        switch (this.drawerMode) {
            case 0:
                this.drawerViewPaged.withHome(home, (PagerIndicator) findViewById(R.id.appDrawerIndicator));
                return;
            default:
                return;
        }
    }
}
