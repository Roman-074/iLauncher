package com.benny.openlauncher.core.transformers;

import android.view.View;

public class CubeInTransformer extends BaseTransformer {
    protected void onTransform(View view, float position) {
        view.setPivotX(position > 0.0f ? 0.0f : (float) view.getWidth());
        view.setPivotY(0.0f);
        view.setRotationY(-90.0f * position);
    }

    public boolean isPagingEnabled() {
        return true;
    }
}
