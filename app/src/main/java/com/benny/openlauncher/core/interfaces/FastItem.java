package com.benny.openlauncher.core.interfaces;

import android.support.v7.widget.RecyclerView.ViewHolder;
import com.mikepenz.fastadapter.IItem;

public interface FastItem {

    public interface AppItem<T, VH extends ViewHolder> extends IItem<T, VH> {
        App getApp();
    }

    public interface DesktopOptionsItem<T, VH extends ViewHolder> extends IItem<T, VH> {
        void setIcon(int i);
    }

    public interface LabelItem<T, VH extends ViewHolder> extends IItem<T, VH>, LabelProvider {
        String getLabel();
    }
}
