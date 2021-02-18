package com.benny.openlauncher.core.transformers;

import android.view.View;

import com.benny.openlauncher.core.util.Tool;


public class DrawFromBackTransformer extends BaseTransformer {
    private static final float MIN_SCALE = 0.75f;

    protected void onTransform(View var1, float var2) {
    }

    public void transformPage(View view, float position) {
        float scaleFactor=0;
        int pageWidth = view.getWidth();
        if (position < -1.0f || position > Tool.DEFAULT_BACKOFF_MULT) {
            view.setAlpha(0.0f);
        } else if (position <= 0.0f) {
            view.setAlpha(Tool.DEFAULT_BACKOFF_MULT + position);
            view.setTranslationX(((float) pageWidth) * (-position));
            scaleFactor = 0.75f + (0.25f * (Tool.DEFAULT_BACKOFF_MULT - Math.abs(position)));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else if (((double) position) > 0.5d && position <= Tool.DEFAULT_BACKOFF_MULT) {
            view.setAlpha(0.0f);
            view.setTranslationX(((float) pageWidth) * (-position));
        } else if (((double) position) > 0.3d && ((double) position) <= 0.5d) {
            view.setAlpha(Tool.DEFAULT_BACKOFF_MULT);
            view.setTranslationX(((float) pageWidth) * position);
            view.setScaleX(0.75f);
            view.setScaleY(0.75f);
        } else if (((double) position) <= 0.3d) {
            view.setAlpha(Tool.DEFAULT_BACKOFF_MULT);
            view.setTranslationX(((float) pageWidth) * position);
            float v = (float) (0.3d - ((double) position));
            if (v >= 0.25f) {
                v = 0.25f;
            }
            scaleFactor = 0.75f + v;
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }
}
