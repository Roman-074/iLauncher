package com.benny.openlauncher.core.widget;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.WindowInsets;
import android.widget.Toast;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.Definitions.ItemPosition;
import com.benny.openlauncher.core.util.DragAction;
import com.benny.openlauncher.core.util.DragAction.Action;
import com.benny.openlauncher.core.util.DragDropHandler;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.viewutil.DesktopCallBack;
import com.benny.openlauncher.core.viewutil.ItemViewFactory;
import com.benny.openlauncher.core.widget.CellContainer.LayoutParams;
import java.util.List;

public class Dock extends CellContainer implements OnDragListener, DesktopCallBack {
    public static int bottomInset;
    private DockListener dockListener;
    private Home home;
    public Item previousItem;
    public View previousItemView;
    private float startPosX;
    private float startPosY;

    public interface DockListener {
        void onInitDone();
    }

    public Dock(Context c) {
        this(c, null);
    }

    public Dock(Context c, AttributeSet attr) {
        super(c, attr);
    }

    public void init() {
        if (!isInEditMode()) {
            setOnDragListener(this);
            super.init();
        }
    }

    @SuppressLint("WrongConstant")
    public void initDockItem(Home home) {
        int columns = Setup.appSettings().getDockSize();
        setGridSize(columns, 1);
        List<Item> dockItems = Home.db.getDock();
        PackageManager packageManager = getContext().getPackageManager();
        for (Item item : dockItems) {
            try {
                item.setLabel((String) packageManager.getActivityInfo(new ComponentName(item.getPackageName(), item.getClassName()), 128).loadLabel(packageManager));
            } catch (Exception e) {
                Log.e("HuyAnh", "error get label dock: " + e.getMessage());
            }
        }
        this.home = home;
        removeAllViews();
        for (Item item2 : dockItems) {
            if (item2.getX() < columns && item2.getY() == 0) {
                addItemToPage(item2, 0);
            }
        }
        if (this.dockListener != null) {
            this.dockListener.onInitDone();
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return true;
    }

    public boolean onDrag(View p1, DragEvent p2) {
        switch (p2.getAction()) {
            case 1:
                try {
                    if (((DragAction) p2.getLocalState()).action == Action.WIDGET) {
                        return false;
                    }
                } catch (Exception e) {
                }
                return true;
            case 3:
                Item item = (Item) DragDropHandler.getDraggedObject(p2);
                if (((DragAction) p2.getLocalState()).action == Action.APP_DRAWER) {
                    item.reset();
                }
                if (addItemToPoint(item, (int) p2.getX(), (int) p2.getY())) {
                    this.home.desktop.consumeRevert();
                    this.home.dock.consumeRevert();
                    Home home = this.home;
                    Home.db.saveItem(item, 0, ItemPosition.Dock);
                } else {
                    View itemView = coordinateToChildView(touchPosToCoordinate((int) p2.getX(), (int) p2.getY(), item.getSpanX(), item.getSpanY(), false));
                    if (itemView != null) {
                        if (Desktop.handleOnDropOver(this.home, item, (Item) itemView.getTag(), itemView, this, 0, ItemPosition.Dock, this)) {
                            this.home.desktop.consumeRevert();
                            this.home.dock.consumeRevert();
                        } else {
                            Toast.makeText(getContext(), R.string.toast_not_enough_space, Toast.LENGTH_SHORT).show();
                            this.home.dock.revertLastItem();
                            this.home.desktop.revertLastItem();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.toast_not_enough_space, Toast.LENGTH_SHORT).show();
                        this.home.dock.revertLastItem();
                        this.home.desktop.revertLastItem();
                    }
                }
                return true;
            case 4:
                return true;
            case 5:
                return true;
            case 6:
                return true;
            default:
                return false;
        }
    }

    public void setLastItem(Object... args) {
        View v = (View)args[1];
        Item item = (Item) args[0];
        this.previousItemView = v;
        this.previousItem = item;
        removeView(v);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (VERSION.SDK_INT >= 20) {
            bottomInset = insets.getSystemWindowInsetBottom();
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), bottomInset);
        }
        return insets;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int iconSize = Setup.appSettings().getDockIconSize();
        if (Setup.appSettings().isDockShowLabel()) {
            height = Tool.dp2px(((iconSize + 16) + 14) + 10, getContext()) + bottomInset;
        } else {
            height = Tool.dp2px((iconSize + 16) + 10, getContext()) + bottomInset;
        }
        getLayoutParams().height = height;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void consumeRevert() {
        this.previousItem = null;
        this.previousItemView = null;
    }

    public void revertLastItem() {
        if (this.previousItemView != null) {
            addViewToGrid(this.previousItemView);
            this.previousItem = null;
            this.previousItemView = null;
        }
    }

    public boolean addItemToPage(Item item, int page) {
        View itemView = ItemViewFactory.getItemView(getContext(), item, Setup.appSettings().isDockShowLabel(), (DesktopCallBack) this, Setup.appSettings().getDockIconSize(), -1);

        if (itemView == null) {
            Log.e("HuyAnh", "addItemToPage Dock: " + item.getLabel());
            Home home = this.home;
            Home.db.deleteItem(item, true);
            return false;
        }
        addViewToGrid(itemView, item.getX(), item.getY(), item.getSpanX(), item.getSpanY());
        return true;
    }

    public boolean addItemToPoint(Item item, int x, int y) {
        LayoutParams positionToLayoutPrams = coordinateToLayoutParams(x, y, item.getSpanX(), item.getSpanY());
        if (positionToLayoutPrams == null) {
            return false;
        }
        item.setX(positionToLayoutPrams.x);
        item.setY(positionToLayoutPrams.y);
        View itemView = ItemViewFactory.getItemView(getContext(), item, Setup.appSettings().isDockShowLabel(), (DesktopCallBack) this, Setup.appSettings().getDockIconSize(), -1);
        if (itemView != null) {
            itemView.setLayoutParams(positionToLayoutPrams);
            addView(itemView);
        }
        return true;
    }

    public boolean addItemToCell(Item item, int x, int y) {
        item.setX(x);
        item.setY(y);
        View itemView = ItemViewFactory.getItemView(getContext(), item, Setup.appSettings().isDockShowLabel(), (DesktopCallBack) this, Setup.appSettings().getDockIconSize(), -1);
        if (itemView == null) {
            return false;
        }
        addViewToGrid(itemView, item.getX(), item.getY(), item.getSpanX(), item.getSpanY());
        return true;
    }

    public void removeItem(View view) {
        removeViewInLayout(view);
    }

    public void setDockListener(DockListener dockListener) {
        this.dockListener = dockListener;
    }
}
