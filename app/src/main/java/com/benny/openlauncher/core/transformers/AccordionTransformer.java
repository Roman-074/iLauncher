package com.benny.openlauncher.core.transformers;

import android.view.View;


public class AccordionTransformer extends BaseTransformer {
    protected void onTransform(View view, float position) {
        view.setPivotX(position < 0.0f ? 0.0f : (float) view.getWidth());
        view.setScaleX(position < 0.0f ? 1.0f + position : 1.0f - position);
    }
}
