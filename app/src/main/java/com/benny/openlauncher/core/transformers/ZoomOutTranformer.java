package com.benny.openlauncher.core.transformers;

import android.view.View;
import com.benny.openlauncher.core.util.Tool;

public class ZoomOutTranformer extends BaseTransformer {
    protected void onTransform(View view, float position) {
        float scale = Tool.DEFAULT_BACKOFF_MULT + Math.abs(position);
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setPivotX(((float) view.getWidth()) * 0.5f);
        view.setPivotY(((float) view.getHeight()) * 0.5f);
        float f = (position < -1.0f || position > Tool.DEFAULT_BACKOFF_MULT) ? 0.0f : Tool.DEFAULT_BACKOFF_MULT - (scale - Tool.DEFAULT_BACKOFF_MULT);
        view.setAlpha(f);
        if (position == -1.0f) {
            view.setTranslationX((float) (view.getWidth() * -1));
        }
    }
}
