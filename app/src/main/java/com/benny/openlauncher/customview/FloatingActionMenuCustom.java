package com.benny.openlauncher.customview;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import com.benny.openlauncher.activity.Home;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu.Item;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu.MenuStateChangeListener;
import com.oguzdev.circularfloatingactionmenu.library.animation.MenuAnimationHandler;
import java.util.ArrayList;

public class FloatingActionMenuCustom extends FloatingActionMenu {
    private View mainView = null;

    public FloatingActionMenuCustom(View mainActionView, int startAngle, int endAngle, int radius, ArrayList<Item> subActionItems, MenuAnimationHandler animationHandler, boolean animated, MenuStateChangeListener stateChangeListener) {
        super(mainActionView, startAngle, endAngle, radius, subActionItems, animationHandler, animated, stateChangeListener);
    }

    public void setMainView(View mainView) {
        this.mainView = mainView;
    }

    public Point getActionViewCenter() {
        Point point = getActionViewCoordinates();
        point.x += this.mainView.getMeasuredWidth() / 2;
        point.y += this.mainView.getMeasuredHeight() / 2;
        return point;
    }

    private Point getActionViewCoordinates() {
        int[] coords = new int[2];
        if (this.mainView == null) {
            this.mainView = Home.launcher.desktop;
        }
        if (this.mainView == null) {
            return new Point(0, 0);
        }
        this.mainView.getLocationOnScreen(coords);
        Rect activityFrame = new Rect();
        getActivityContentView().getWindowVisibleDisplayFrame(activityFrame);
        coords[0] = coords[0] - (getScreenSize().x - getActivityContentView().getMeasuredWidth());
        coords[1] = coords[1] - ((activityFrame.height() + activityFrame.top) - getActivityContentView().getMeasuredHeight());
        return new Point(coords[0], coords[1]);
    }

    private Point getScreenSize() {
        Point size = new Point();
        ((Activity) this.mainView.getContext()).getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }
}
