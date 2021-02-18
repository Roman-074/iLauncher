package com.benny.openlauncher.core.transformers;

import android.view.View;
import com.benny.openlauncher.core.util.Tool;

public class ParallaxPageTransformer extends BaseTransformer {
    private final int viewToParallax;

    public ParallaxPageTransformer(int viewToParallax) {
        this.viewToParallax = viewToParallax;
    }

    protected void onTransform(View view, float position) {
        int pageWidth = view.getWidth();
        if (position < -1.0f) {
            view.setAlpha(Tool.DEFAULT_BACKOFF_MULT);
        } else if (position <= Tool.DEFAULT_BACKOFF_MULT) {
            view.findViewById(this.viewToParallax).setTranslationX((-position) * ((float) (pageWidth / 2)));
        } else {
            view.setAlpha(Tool.DEFAULT_BACKOFF_MULT);
        }
    }
}
