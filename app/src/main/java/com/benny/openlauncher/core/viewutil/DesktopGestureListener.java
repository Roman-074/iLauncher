package com.benny.openlauncher.core.viewutil;

import com.benny.openlauncher.SimpleFingerGestures;
import com.benny.openlauncher.core.widget.Desktop;

public class DesktopGestureListener implements SimpleFingerGestures.OnFingerGestureListener {
    private final DesktopGestureCallback callback;
    private final Desktop desktop;

    public interface DesktopGestureCallback {
        boolean onDrawerGesture(Desktop desktop, Type type);
    }

    public enum Type {
        SwipeUp,
        SwipeDown,
        SwipeLeft,
        SwipeRight,
        Pinch,
        Unpinch,
        DoubleTap
    }

    public DesktopGestureListener(Desktop desktop, DesktopGestureCallback callback) {
        this.desktop = desktop;
        this.callback = callback;
    }

    public boolean onSwipeUp(int i, long l, double v) {
        return this.callback.onDrawerGesture(this.desktop, Type.SwipeUp);
    }

    public boolean onSwipeDown(int i, long l, double v) {
        return this.callback.onDrawerGesture(this.desktop, Type.SwipeDown);
    }

    public boolean onSwipeLeft(int i, long l, double v) {
        return this.callback.onDrawerGesture(this.desktop, Type.SwipeLeft);
    }

    public boolean onSwipeRight(int i, long l, double v) {
        return this.callback.onDrawerGesture(this.desktop, Type.SwipeRight);
    }

    public boolean onPinch(int i, long l, double v) {
        return this.callback.onDrawerGesture(this.desktop, Type.Pinch);
    }

    public boolean onUnpinch(int i, long l, double v) {
        return this.callback.onDrawerGesture(this.desktop, Type.Unpinch);
    }

    public boolean onDoubleTap(int i) {
        return this.callback.onDrawerGesture(this.desktop, Type.DoubleTap);
    }
}
