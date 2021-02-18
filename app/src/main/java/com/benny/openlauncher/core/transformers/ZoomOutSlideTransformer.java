package com.benny.openlauncher.core.transformers;

import android.view.View;
import com.benny.openlauncher.core.util.Tool;


public class ZoomOutSlideTransformer extends BaseTransformer {
    private static final float MIN_ALPHA = 0.5f;
    private static final float MIN_SCALE = 0.85f;

    protected void onTransform(View view, float position) {
        if (position >= -1.0f || position <= Tool.DEFAULT_BACKOFF_MULT) {
            float height = (float) view.getHeight();
            float scaleFactor = Math.max(MIN_SCALE, Tool.DEFAULT_BACKOFF_MULT - Math.abs(position));
            float vertMargin = ((Tool.DEFAULT_BACKOFF_MULT - scaleFactor) * height) / Tool.DEFAULT_IMAGE_BACKOFF_MULT;
            float horzMargin = (((float) view.getWidth()) * (Tool.DEFAULT_BACKOFF_MULT - scaleFactor)) / Tool.DEFAULT_IMAGE_BACKOFF_MULT;
            view.setPivotY(MIN_ALPHA * height);
            if (position < 0.0f) {
                view.setTranslationX(horzMargin - (vertMargin / Tool.DEFAULT_IMAGE_BACKOFF_MULT));
            } else {
                view.setTranslationX((-horzMargin) + (vertMargin / Tool.DEFAULT_IMAGE_BACKOFF_MULT));
            }
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setAlpha((((scaleFactor - MIN_SCALE) / 0.14999998f) * MIN_ALPHA) + MIN_ALPHA);
        }
    }
}
