package com.benny.openlauncher.core.viewutil;

import android.view.View;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.RevertibleAction;

public interface DesktopCallBack<V extends View> extends RevertibleAction {
    boolean addItemToCell(Item item, int i, int i2);

    boolean addItemToPage(Item item, int i);

    boolean addItemToPoint(Item item, int i, int i2);

    void removeItem(V v);
}
