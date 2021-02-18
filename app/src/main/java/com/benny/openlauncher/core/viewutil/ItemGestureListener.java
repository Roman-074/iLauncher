package com.benny.openlauncher.core.viewutil;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.Item;

public class ItemGestureListener extends SimpleOnGestureListener implements OnGestureListener, OnDoubleTapListener {
    private static final int SWIPE_THRESHOLD = 30;
    private static final int SWIPE_VELOCITY_THRESHOLD = 20;
    private final ItemGestureCallback callback;
    private GestureDetectorCompat detector;
    private final Item item;

    public interface ItemGestureCallback {
        boolean onItemGesture(Item item, Type type);
    }

    public enum Type {
        Click,
        SwipeUp,
        SwipeDown,
        SwipeLeft,
        SwipeRight
    }

    public ItemGestureListener(Context context, Item item, ItemGestureCallback callback) {
        this.detector = new GestureDetectorCompat(context, this);
        this.detector.setOnDoubleTapListener(this);
        this.item = item;
        this.callback = callback;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.detector.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent event) {
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) <= 30.0f || Math.abs(velocityX) <= 20.0f) {
                    return false;
                }
                if (diffX > 0.0f) {
                    return this.callback.onItemGesture(this.item, Type.SwipeRight);
                }
                return this.callback.onItemGesture(this.item, Type.SwipeLeft);
            } else if (Math.abs(diffY) <= 30.0f || Math.abs(velocityY) <= 20.0f) {
                return false;
            } else {
                if (diffY > 0.0f) {
                    return this.callback.onItemGesture(this.item, Type.SwipeDown);
                }
                return this.callback.onItemGesture(this.item, Type.SwipeUp);
            }
        } catch (Exception exception) {
            Setup.logger().log(this, 6, null, exception.getMessage(), new Object[0]);
            return false;
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        return this.callback.onItemGesture(this.item, Type.Click);
    }
}
