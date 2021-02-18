package com.benny.openlauncher.core.viewutil;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;
import android.view.View.DragShadowBuilder;
import com.benny.openlauncher.core.activity.Home;

public class GoodDragShadowBuilder extends DragShadowBuilder {
    int x = Home.touchX;
    int y = Home.touchY;

    public GoodDragShadowBuilder(View view) {
        super(view);
    }

    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        shadowSize.set(getView().getWidth(), getView().getHeight());
        shadowTouchPoint.set(this.x, this.y);
    }

    public void onDrawShadow(Canvas canvas) {
        getView().draw(canvas);
    }
}
