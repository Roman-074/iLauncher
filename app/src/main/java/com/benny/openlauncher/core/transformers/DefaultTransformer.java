package com.benny.openlauncher.core.transformers;

import android.view.View;

public class DefaultTransformer extends BaseTransformer {
    protected void onTransform(View view, float position) {
    }

    public boolean isPagingEnabled() {
        return true;
    }
}
