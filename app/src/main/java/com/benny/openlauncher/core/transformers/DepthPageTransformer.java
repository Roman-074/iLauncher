package com.benny.openlauncher.core.transformers;

import android.view.View;
import com.benny.openlauncher.core.util.Tool;

public class DepthPageTransformer extends BaseTransformer {
    private static final float MIN_SCALE = 0.75f;

    protected void onTransform(View view, float position) {
        if (position <= 0.0f) {
            view.setTranslationX(0.0f);
            view.setScaleX(Tool.DEFAULT_BACKOFF_MULT);
            view.setScaleY(Tool.DEFAULT_BACKOFF_MULT);
        } else if (position <= Tool.DEFAULT_BACKOFF_MULT) {
            float scaleFactor = 0.75f + (0.25f * (Tool.DEFAULT_BACKOFF_MULT - Math.abs(position)));
            view.setAlpha(Tool.DEFAULT_BACKOFF_MULT - position);
            view.setPivotY(0.5f * ((float) view.getHeight()));
            view.setTranslationX(((float) view.getWidth()) * (-position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }

    protected boolean isPagingEnabled() {
        return true;
    }
}
