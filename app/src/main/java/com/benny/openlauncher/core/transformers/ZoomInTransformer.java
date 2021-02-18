package com.benny.openlauncher.core.transformers;

import android.view.View;
import com.benny.openlauncher.core.util.Tool;

public class ZoomInTransformer extends BaseTransformer {
    protected void onTransform(View view, float position) {
        float f = 0.0f;
        float scale = position < 0.0f ? position + Tool.DEFAULT_BACKOFF_MULT : Math.abs(Tool.DEFAULT_BACKOFF_MULT - position);
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setPivotX(((float) view.getWidth()) * 0.5f);
        view.setPivotY(((float) view.getHeight()) * 0.5f);
        if (position >= -1.0f && position <= Tool.DEFAULT_BACKOFF_MULT) {
            f = Tool.DEFAULT_BACKOFF_MULT - (scale - Tool.DEFAULT_BACKOFF_MULT);
        }
        view.setAlpha(f);
    }
}
