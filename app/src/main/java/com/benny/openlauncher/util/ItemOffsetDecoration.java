package com.benny.openlauncher.util;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.State;
import android.view.View;

public class ItemOffsetDecoration extends ItemDecoration {
    private int mSpacing;

    public ItemOffsetDecoration(int itemOffset) {
        this.mSpacing = itemOffset;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(this.mSpacing, this.mSpacing, this.mSpacing, this.mSpacing);
    }
}
