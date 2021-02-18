package com.benny.openlauncher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ListView;
import com.benny.openlauncher.core.util.Tool;

public class SwipeListView extends ListView {
    private GestureDetector mGestureDetector;
    private OnSwipeRight onSwipeRight;

    public interface OnSwipeRight {
        void onSwipe(int i, float f, float f2);
    }

    public void setOnSwipeRight(OnSwipeRight onSwipeRight) {
        this.onSwipeRight = onSwipeRight;
    }

    public SwipeListView(Context context) {
        super(context);
        init();
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            final float dis = (float) Tool.dp2px(10, getContext());
            final float vDis = (float) Tool.dp2px(30, getContext());
            this.mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (e1 != null) {
                        int dy = (int) (e2.getY() - e1.getY());
                        if (((float) Math.abs((int) (e2.getX() - e1.getX()))) > dis && ((float) Math.abs(dy)) < vDis && velocityX > 0.0f) {
                            try {
                                int pos = SwipeListView.this.pointToPosition((int) e1.getX(), (int) e1.getY());
                                if (!(pos == -1 || SwipeListView.this.onSwipeRight == null)) {
                                    SwipeListView.this.onSwipeRight.onSwipe(pos, e1.getX(), e1.getY());
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                    return true;
                }
            });
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
