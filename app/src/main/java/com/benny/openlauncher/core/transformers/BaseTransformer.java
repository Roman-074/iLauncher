package com.benny.openlauncher.core.transformers;

import android.view.View;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.widget.SmoothViewPager.PageTransformer;

public abstract class BaseTransformer implements PageTransformer {
    protected abstract void onTransform(View view, float f);

    public void transformPage(View view, float position) {
        onPreTransform(view, position);
        onTransform(view, position);
        onPostTransform(view, position);
    }

    protected boolean hideOffscreenPages() {
        return true;
    }

    protected boolean isPagingEnabled() {
        return false;
    }

    protected void onPreTransform(View view, float position) {
        float f = 0.0f;
        float f2 = 0.0f;
        float width = (float) view.getWidth();
        view.setRotationX(0.0f);
        view.setRotationY(0.0f);
        view.setRotation(0.0f);
        view.setScaleX(Tool.DEFAULT_BACKOFF_MULT);
        view.setScaleY(Tool.DEFAULT_BACKOFF_MULT);
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        view.setTranslationY(0.0f);
        if (!isPagingEnabled()) {
            f = (-width) * position;
        }
        view.setTranslationX(f);
        if (hideOffscreenPages()) {
            if (position > -1.0f && position < Tool.DEFAULT_BACKOFF_MULT) {
                f2 = Tool.DEFAULT_BACKOFF_MULT;
            }
            view.setAlpha(f2);
            return;
        }
        view.setAlpha(Tool.DEFAULT_BACKOFF_MULT);
    }

    protected void onPostTransform(View view, float position) {
    }
}
