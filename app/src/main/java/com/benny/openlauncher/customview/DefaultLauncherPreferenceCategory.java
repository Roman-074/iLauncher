package com.benny.openlauncher.customview;

import android.content.Context;
import android.preference.Preference;
import androidx.core.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.View;

public class DefaultLauncherPreferenceCategory extends Preference {
    public DefaultLauncherPreferenceCategory(Context context) {
        super(context);
    }

    public DefaultLauncherPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultLauncherPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onBindView(View view) {
        super.onBindView(view);
//        ((TextView) view.findViewById(16908310)).setTextColor(-1);
//        view.setBackgroundColor(SupportMenu.CATEGORY_MASK);
    }
}
