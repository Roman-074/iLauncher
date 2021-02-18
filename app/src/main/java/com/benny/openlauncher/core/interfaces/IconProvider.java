package com.benny.openlauncher.core.interfaces;

import android.graphics.drawable.Drawable;

public interface IconProvider {

    public enum IconTargetType {
        ImageView,
        TextView,
        IconDrawer
    }

    void cancelLoad(IconTargetType iconTargetType, Object obj);

    Drawable getDrawableSynchronously(int i);

    boolean isGroupIconDrawable();

    void loadIcon(IconTargetType iconTargetType, int i, Object obj, Object... objArr);
}
