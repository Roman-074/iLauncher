package com.benny.openlauncher.core.util;

import android.content.ClipData;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.DragEvent;
import android.view.View;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.DragAction.Action;
import com.benny.openlauncher.core.viewutil.GoodDragShadowBuilder;
import com.benny.openlauncher.core.widget.AppItemView.LongPressCallBack;

public class DragDropHandler {
    private static final String DRAG_DROP_EXTRA = "DRAG_DROP_EXTRA";
    private static final String DRAG_DROP_INTENT = "DRAG_DROP_INTENT";

    public static <T extends Parcelable> void startDrag(View v, T item, Action action, @Nullable LongPressCallBack eventAction) {
        Intent i = new Intent();
        i.putExtra(DRAG_DROP_EXTRA, item);
        ClipData data = ClipData.newIntent(DRAG_DROP_INTENT, i);
        try {
            if (VERSION.SDK_INT >= 24) {
                v.startDragAndDrop(data, new GoodDragShadowBuilder(v), new DragAction(action), 0);
            } else {
                v.startDrag(data, new GoodDragShadowBuilder(v), new DragAction(action), 0);
            }
            if (eventAction != null) {
                eventAction.afterDrag(v);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Parcelable> T getDraggedObject(DragEvent dragEvent) {
        Intent intent = dragEvent.getClipData().getItemAt(0).getIntent();
        intent.setExtrasClassLoader(Item.class.getClassLoader());
        return intent.getParcelableExtra(DRAG_DROP_EXTRA);
    }
}
