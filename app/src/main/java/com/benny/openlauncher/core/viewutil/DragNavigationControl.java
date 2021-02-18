package com.benny.openlauncher.core.viewutil;

import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.util.DragAction;
import com.benny.openlauncher.core.util.Tool;


public class DragNavigationControl {
    private static boolean leftok = true;
    private static boolean rightok = true;

    public static void init(final Home home, final View leftView, final View rightView) {
        final Handler l = new Handler();
        final Runnable right = new Runnable() {
            public void run() {
                if (home.desktop.getCurrentItem() < home.desktop.pageCount - 1) {
                    home.desktop.setCurrentItem(home.desktop.getCurrentItem() + 1);
                } else if (home.desktop.getCurrentItem() == home.desktop.pageCount - 1) {
                    Log.i("addPageRight DragNavigationControl");
                    home.desktop.addPageRight(true);
                }
                l.postDelayed(this, 1000);
            }
        };
        final Runnable left = new Runnable() {
            public void run() {
                if (home.desktop.getCurrentItem() > 1) {
                    home.desktop.setCurrentItem(home.desktop.getCurrentItem() - 1);
                }
                l.postDelayed(this, 1000);
            }
        };
        leftView.setOnDragListener(new OnDragListener() {
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case 1:
                        if (dragEvent == null || dragEvent.getLocalState() == null) {
                            return false;
                        }
                        switch (((DragAction) dragEvent.getLocalState()).action) {
                            case APP:
                            case WIDGET:
                            case APP_DRAWER:
                            case GROUP:
                            case SHORTCUT:
                            case ACTION:
                                leftView.animate().alpha(Tool.DEFAULT_BACKOFF_MULT);
                                return true;
                            default:
                                return false;
                        }
                    case 2:
                        return true;
                    case 3:
                        return false;
                    case 4:
                        l.removeCallbacksAndMessages(null);
                        DragNavigationControl.rightok = true;
                        DragNavigationControl.leftok = true;
                        leftView.animate().alpha(0.0f);
                        return true;
                    case 5:
                        if (DragNavigationControl.leftok) {
                            DragNavigationControl.leftok = false;
                            l.post(left);
                        }
                        return true;
                    case 6:
                        l.removeCallbacksAndMessages(null);
                        DragNavigationControl.rightok = true;
                        DragNavigationControl.leftok = true;
                        return true;
                    default:
                        return false;
                }
            }
        });
        rightView.setOnDragListener(new OnDragListener() {
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case 1:
                        if (dragEvent == null || dragEvent.getLocalState() == null) {
                            return false;
                        }
                        switch (((DragAction) dragEvent.getLocalState()).action) {
                            case APP:
                            case WIDGET:
                            case APP_DRAWER:
                            case GROUP:
                            case SHORTCUT:
                            case ACTION:
                                rightView.animate().alpha(Tool.DEFAULT_BACKOFF_MULT);
                                return true;
                            default:
                                return false;
                        }
                    case 2:
                        return true;
                    case 3:
                        return false;
                    case 4:
                        l.removeCallbacksAndMessages(null);
                        DragNavigationControl.rightok = true;
                        DragNavigationControl.leftok = true;
                        rightView.animate().alpha(0.0f);
                        return true;
                    case 5:
                        if (DragNavigationControl.rightok) {
                            DragNavigationControl.rightok = false;
                            l.post(right);
                        }
                        return true;
                    case 6:
                        l.removeCallbacksAndMessages(null);
                        DragNavigationControl.rightok = true;
                        DragNavigationControl.leftok = true;
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
