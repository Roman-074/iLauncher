package com.benny.openlauncher.core.transformers;

import android.view.View;

import com.benny.openlauncher.core.util.Tool;


public class BackgroundToForegroundTransformer extends BaseTransformer {
    protected void onTransform(View view, float position) {
        float f = Tool.DEFAULT_BACKOFF_MULT;
        float height = (float) view.getHeight();
        float width = (float) view.getWidth();
        if (position >= 0.0f) {
            f = Math.abs(Tool.DEFAULT_BACKOFF_MULT - position);
        }
        float scale = min(f, 0.5f);
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setPivotX(width * 0.5f);
        view.setPivotY(height * 0.5f);
        view.setTranslationX(position < 0.0f ? width * position : ((-width) * position) * 0.25f);
    }

    private static final float min(float val, float min) {
        return val < min ? min : val;
    }
}
