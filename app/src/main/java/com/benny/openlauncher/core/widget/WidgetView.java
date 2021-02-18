package com.benny.openlauncher.core.widget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import com.benny.openlauncher.core.util.Tool;

public class WidgetView extends AppWidgetHostView {
    private static final int LONG_PRESS_TIMEOUT = 500;
    private final int THRESHOLD;
    private boolean hasPerformedLongPress;
    private OnLongClickListener longClick;
    private float longPressDownX;
    private float longPressDownY;
    private OnTouchListener onTouchListener = null;
    private CheckForLongPress pendingCheckForLongPress;

    class CheckForLongPress implements Runnable {
        private int mOriginalWindowAttachCount;

        CheckForLongPress() {
        }

        public void run() {
            if (WidgetView.this.getParent() != null && this.mOriginalWindowAttachCount == WidgetView.this.getWindowAttachCount() && !WidgetView.this.hasPerformedLongPress && WidgetView.this.onLongPress()) {
                WidgetView.this.hasPerformedLongPress = true;
            }
        }

        public void rememberWindowAttachCount() {
            this.mOriginalWindowAttachCount = WidgetView.this.getWindowAttachCount();
        }
    }

    public WidgetView(Context context) {
        super(context);
        this.THRESHOLD = Tool.dp2px(5, context);
    }

    public void setOnLongClickListener(OnLongClickListener l) {
        this.longClick = l;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.onTouchListener != null && this.onTouchListener.onTouch(this, ev)) {
            return true;
        }
        if (this.hasPerformedLongPress) {
            this.hasPerformedLongPress = false;
            return true;
        }
        switch (ev.getAction()) {
            case 0:
                this.longPressDownX = ev.getX();
                this.longPressDownY = ev.getY();
                postCheckForLongClick();
                break;
            case 1:
            case 3:
                this.hasPerformedLongPress = false;
                if (this.pendingCheckForLongPress != null) {
                    removeCallbacks(this.pendingCheckForLongPress);
                    break;
                }
                break;
            case 2:
                float diffX = Math.abs(this.longPressDownX - ev.getX());
                float diffY = Math.abs(this.longPressDownY - ev.getY());
                if (diffX >= ((float) this.THRESHOLD) || diffY >= ((float) this.THRESHOLD)) {
                    this.hasPerformedLongPress = false;
                    if (this.pendingCheckForLongPress != null) {
                        removeCallbacks(this.pendingCheckForLongPress);
                        break;
                    }
                }
                break;
        }
        return false;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.hasPerformedLongPress = false;
        if (this.pendingCheckForLongPress != null) {
            removeCallbacks(this.pendingCheckForLongPress);
        }
    }

    @SuppressLint("WrongConstant")
    public int getDescendantFocusability() {
        return 393216;
    }

    private boolean onLongPress() {
        return this.longClick.onLongClick(this);
    }

    public final void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    private void postCheckForLongClick() {
        this.hasPerformedLongPress = false;
        if (this.pendingCheckForLongPress == null) {
            this.pendingCheckForLongPress = new CheckForLongPress();
        }
        this.pendingCheckForLongPress.rememberWindowAttachCount();
        postDelayed(this.pendingCheckForLongPress, 500);
    }
}
