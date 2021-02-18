package com.benny.openlauncher.core.transformers;

import android.annotation.SuppressLint;
import android.view.View;

public class FlipHorizontalTransformer extends BaseTransformer {
    @SuppressLint("WrongConstant")
    protected void onTransform(View view, float position) {
        float rotation = 180.0f * position;
        int i = (rotation > 90.0f || rotation < -90.0f) ? 4 : 0;
        view.setVisibility(i);
        view.setPivotX(((float) view.getWidth()) * 0.5f);
        view.setPivotY(((float) view.getHeight()) * 0.5f);
        view.setRotationY(rotation);
    }
}
