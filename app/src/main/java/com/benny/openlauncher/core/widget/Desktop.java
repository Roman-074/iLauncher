package com.benny.openlauncher.core.widget;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.MainApplication;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.DialogListener.OnEditDialogListener;
import com.benny.openlauncher.core.interfaces.SwipeListener;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.model.Item.Type;
import com.benny.openlauncher.core.transformers.AccordionTransformer;
import com.benny.openlauncher.core.transformers.BackgroundToForegroundTransformer;
import com.benny.openlauncher.core.transformers.BaseTransformer;
import com.benny.openlauncher.core.transformers.DefaultTransformer;
import com.benny.openlauncher.core.transformers.DepthPageTransformer;
import com.benny.openlauncher.core.transformers.FlipHorizontalTransformer;
import com.benny.openlauncher.core.transformers.FlipVerticalTransformer;
import com.benny.openlauncher.core.transformers.ForegroundToBackgroundTransformer;
import com.benny.openlauncher.core.transformers.RotateDownTransformer;
import com.benny.openlauncher.core.transformers.RotateUpTransformer;
import com.benny.openlauncher.core.transformers.StackTransformer;
import com.benny.openlauncher.core.transformers.TabletTransformer;
import com.benny.openlauncher.core.transformers.ZoomInTransformer;
import com.benny.openlauncher.core.transformers.ZoomOutSlideTransformer;
import com.benny.openlauncher.core.util.Definitions.ItemPosition;
import com.benny.openlauncher.core.util.Definitions.ItemState;
import com.benny.openlauncher.core.util.DragAction;
import com.benny.openlauncher.core.util.DragAction.Action;
import com.benny.openlauncher.core.util.DragDropHandler;
import com.benny.openlauncher.core.util.more.LiveWallPaper;
import com.benny.openlauncher.core.util.more.iRingTone;
import com.benny.openlauncher.core.viewutil.DesktopCallBack;
import com.benny.openlauncher.core.viewutil.DesktopGestureListener;
import com.benny.openlauncher.core.viewutil.ItemViewFactory;
import com.benny.openlauncher.core.viewutil.SmoothPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Desktop extends SmoothViewPager implements OnDragListener, DesktopCallBack {
    public static int bottomInset;
    public static int topInset;
    public OnDesktopEditListener desktopEditListener;
    private Home home;
    public boolean inEditMode;
    private BaseTransformer[] listTransformer;
    public int pageCount;
    private PagerIndicator pageIndicator;
    public List<CellContainer> pages;
    public Item previousItem;
    public View previousItemView;
    public int previousPage;
    private SwipeListener swipeListener;

    public interface OnDesktopEditListener {
        void onDesktopEdit();

        void onFinishDesktopEdit();
    }

    public class DesktopAdapter extends SmoothPagerAdapter {
        private Desktop desktop;
        float scaleFactor = Tool.DEFAULT_BACKOFF_MULT;
        float translateFactor = 0.0f;

        public DesktopAdapter(Desktop desktop) {
            this.desktop = desktop;
            desktop.pages = new ArrayList();
            for (int i = 0; i < getCount(); i++) {
                desktop.pages.add(getItemLayout());
            }
        }

        public void addPageLeft() {
            this.desktop.pages.add(0, getItemLayout());
            notifyDataSetChanged();
        }

        public void addPageRight() {
            Log.i("addPageRight Desktop adapter");
            this.desktop.pages.add(getItemLayout());
            notifyDataSetChanged();
        }

        public void removePage(int position, boolean deleteItems) {
            if (deleteItems) {
                for (View v : ((CellContainer) this.desktop.pages.get(position)).getAllCells()) {
                    if (v != null) {
                        Object item = v.getTag();
                        if (item instanceof Item) {
                            Home home = Home.launcher;
                            Home.db.deleteItem((Item) item, true);
                        }
                    }
                }
            }
            this.desktop.pages.remove(position);
            notifyDataSetChanged();
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public int getCount() {
            return this.desktop.pageCount;
        }

        public boolean isViewFromObject(View p1, Object p2) {
            return p1 == p2;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public Object instantiateItem(ViewGroup container, int pos) {
            ViewGroup layout = (ViewGroup) this.desktop.pages.get(pos);
            container.addView(layout);
            return layout;
        }

        private DesktopGestureListener getGestureListener() {
            return new DesktopGestureListener(this.desktop, Setup.desktopGestureCallback());
        }

        private CellContainer getItemLayout() {
            CellContainer layout = new CellContainer(this.desktop.getContext());
            layout.setSoundEffectsEnabled(false);
//            if (layout.onItemRearrangeListener!=null){
//                layout.setOnItemRearrangeListener(new OnItemRearrangeListener() {
//                    @Override
//                    public void onItemRearrange(Point from, Point to) {
//                        Item itemFromCoordinate = Desktop.getItemFromCoordinate(from, Desktop.this.getCurrentItem());
//                        if (itemFromCoordinate != null) {
//                            android.util.Log.e("point",to.x+"   "+to.y);
//                            itemFromCoordinate.setX(to.x);
//                            itemFromCoordinate.setY(to.y);
//                        }
//                    }
//                });
////            }
            layout.onItemRearrangeListener = new CellContainer.OnItemRearrangeListener() {
                public void onItemRearrange(Point from, Point to) {
                    Item itemFromCoordinate = Desktop.getItemFromCoordinate(from, Desktop.this.getCurrentItem());
                    if (itemFromCoordinate != null) {
                        android.util.Log.e("point", to.x + "   " + to.y);
                        itemFromCoordinate.setX(to.x);
                        itemFromCoordinate.setY(to.y);
                    }
                }
            };
            if (Desktop.this.swipeListener != null) {
                layout.setSwipeListener(Desktop.this.swipeListener);
            }

            layout.setGridSize(Setup.appSettings().getDesktopColumnCount(), Setup.appSettings().getDesktopRowCount());
            layout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Desktop.this.swipeListener.onModifySearch(true);
                    DesktopAdapter.this.exitDesktopEditMode();
                }
            });
            layout.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    if (Desktop.this.getCurrentItem() != 0) {
                        Desktop.this.swipeListener.onModifySearch(false);
                        DesktopAdapter.this.enterDesktopEditMode();
                    }
                    return true;
                }
            });


            return layout;
        }

        public void enterDesktopEditMode() {
            this.scaleFactor = 0.7f;
            this.translateFactor = 180.0f;
            for (CellContainer v : this.desktop.pages) {
                v.blockTouch = true;
                v.animateBackgroundShow();
                v.animate().scaleX(this.scaleFactor).scaleY(this.scaleFactor).translationY(this.translateFactor).setInterpolator(new AccelerateDecelerateInterpolator());
            }
            this.desktop.inEditMode = true;
            if (this.desktop.desktopEditListener != null) {
                this.desktop.desktopEditListener.onDesktopEdit();
            }
        }

        public void exitDesktopEditMode() {
            this.scaleFactor = Tool.DEFAULT_BACKOFF_MULT;
            this.translateFactor = 0.0f;
            for (CellContainer v : this.desktop.pages) {
                v.blockTouch = false;
                v.animateBackgroundHide();
                v.animate().scaleX(this.scaleFactor).scaleY(this.scaleFactor).translationY(this.translateFactor).setInterpolator(new AccelerateDecelerateInterpolator());
            }
            this.desktop.inEditMode = false;
            if (this.desktop.desktopEditListener != null) {
                this.desktop.desktopEditListener.onFinishDesktopEdit();
            }
        }
    }

    public static class DesktopMode {
        public static final int NORMAL = 0;
        public static final int SHOW_ALL_APPS = 1;
    }

    public Desktop(Context c) {
        this(c, null);
    }

    public Desktop(Context c, AttributeSet attr) {
        super(c, attr);
        this.pages = new ArrayList();
        this.previousPage = -1;
        this.listTransformer = new BaseTransformer[]{new DefaultTransformer(), new AccordionTransformer(), new BackgroundToForegroundTransformer(), new DepthPageTransformer(), new FlipHorizontalTransformer(), new FlipVerticalTransformer(), new ForegroundToBackgroundTransformer(), new RotateDownTransformer(), new RotateUpTransformer(), new StackTransformer(), new TabletTransformer(), new ZoomInTransformer(), new ZoomOutSlideTransformer()};
    }

    public static Item getItemFromCoordinate(Point point, int page) {
        Home home = Home.launcher;
        List<List<Item>> desktopItems = Home.db.getDesktop();
        if (page >= desktopItems.size()) {
            return null;
        }
        List<Item> pageData = (List) desktopItems.get(page);
        for (int i = 0; i < pageData.size(); i++) {
            Item item = (Item) pageData.get(i);
            if (item.getX() == point.x && item.getY() == point.y && item.getSpanX() == 1 && item.getSpanY() == 1) {
                return (Item) pageData.get(i);
            }
        }
        return null;
    }

    public static boolean handleOnDropOver(Home home, Item dropItem, Item item, View itemView, ViewGroup parent, int page, ItemPosition itemPosition, DesktopCallBack callback) {
        if (item == null || dropItem == null) {
            return false;
        }
        switch (item.getType()) {
            case APP:
            case SHORTCUT:
                if (dropItem.getType() == Type.APP || dropItem.getType() == Type.SHORTCUT) {
                    parent.removeView(itemView);
                    final Item group = Item.newGroupItem();
                    group.getGroupItems().add(item);
                    group.getGroupItems().add(dropItem);
                    group.setX(item.getX());
                    group.setY(item.getY());
                    Home.db.saveItem(dropItem, page, itemPosition);
                    Home.db.updateSate(item, ItemState.Hidden);
                    Home.db.updateSate(dropItem, ItemState.Hidden);
                    Home.db.saveItem(item, page, itemPosition);
                    Home.db.saveItem(group, page, itemPosition);
                    callback.addItemToPage(group, page);
                    home.desktop.consumeRevert();
                    home.dock.consumeRevert();
                    Setup.eventHandler().showEditDialog(home, group, new OnEditDialogListener() {
                        public void onRename(String name) {
                            group.setLabel(name);
                            Home.db.saveItem(group);
                        }
                    });
                    return true;
                }
            case GROUP:
                break;
        }
        if ((dropItem.getType() == Type.APP || dropItem.getType() == Type.SHORTCUT) && item.getGroupItems().size() < GroupPopupView.GroupDef.maxItem) {
            parent.removeView(itemView);
            item.getGroupItems().add(dropItem);
            Home.db.saveItem(dropItem, page, itemPosition);
            Home.db.updateSate(dropItem, ItemState.Hidden);
            Home.db.saveItem(item, page, itemPosition);
            callback.addItemToPage(item, page);
            home.desktop.consumeRevert();
            home.dock.consumeRevert();
            return true;
        }
        return false;
    }

    public void setDesktopEditListener(OnDesktopEditListener desktopEditListener) {
        this.desktopEditListener = desktopEditListener;
    }

    public void setPageIndicator(PagerIndicator pageIndicator) {
        this.pageIndicator = pageIndicator;
    }

    public void init() {
        if (!isInEditMode()) {
            this.pageCount = 1;
            setOnDragListener(this);
            setCurrentItem(Setup.appSettings().getDesktopPageCurrent());
        }
    }

    public void initDesktopNormal(Home home) {
        setAdapter(new DesktopAdapter(this));
        setPageTransformer(true, this.listTransformer[Setup.appSettings().getDesktopEffect()]);
        if (Setup.appSettings().isDesktopShowIndicator() && this.pageIndicator != null) {
            this.pageIndicator.setViewPager(this);
        }
        this.home = home;
        int columns = Setup.appSettings().getDesktopColumnCount();
        int rows = Setup.appSettings().getDesktopRowCount();
        Home home2 = Home.launcher;
        List<List<Item>> desktopItems = Home.db.getDesktop();
        int pageCount = 0;
        while (pageCount < desktopItems.size() && this.pages.size() > pageCount) {
            ((CellContainer) this.pages.get(pageCount)).removeAllViews();
            List<Item> items = (List) desktopItems.get(pageCount);
            for (int j = 0; j < items.size(); j++) {
                Item item = (Item) items.get(j);
                if (item.getX() + item.getSpanX() <= columns && item.getY() + item.getSpanY() <= rows) {
                    addItemToPage(item, pageCount);
                }
            }
            pageCount++;
        }
    }

    public void initDesktopShowAll(Context c, Home home) {
        this.home = home;
        List<Item> apps = new ArrayList();
        List<App> allApps = new ArrayList();
        allApps.addAll(Setup.appLoader().getAllApps(c));
        List<Item> dockItems = Home.db.getDock();
        HashMap<String, Item> hashMap = new HashMap();
        MainApplication application = (MainApplication) getContext().getApplicationContext();
        for (Item item2 : dockItems) {
            for (App app : allApps) {
                if (item2.getIntent() != null && (item2.getIntent().getComponent().getPackageName() + "/" + item2.getIntent().getComponent().getClassName()).equals(app.getPackageName() + "/" + app.getClassName())) {
                    allApps.remove(app);
                    break;
                }
            }
        }
        for (int i=0;i<apps.size();i++){
            Item app=apps.get(i);
            if (!isInstall(c.getResources().getString(R.string.iRingtone),app)){
                apps.add(Item.newAppItem(new iRingTone(c)));
            }
            if (!isInstall(c.getResources().getString(R.string.livewallpaper),app)){
                apps.add(Item.newAppItem(new LiveWallPaper(c)));
            }
        }
        int columns = Setup.appSettings().getDesktopColumnCount();
        int rows = Setup.appSettings().getDesktopRowCount() - 1;
        if (Setup.appSettings().isFirstCategoryApps()) {
            for (App app2 : allApps) {
                android.util.Log.e("jj", "initDesktopShowAll: " + app2.getPackageName());
                String catName = application.dbHelper.getCatName(app2.getPackageName());
                if (!catName.equals("null") || catName.equals(c.getResources().getString(R.string.iRingtone)) || catName.equals(c.getResources().getString(R.string.livewallpaper))) {
                    if (hashMap.get(catName) == null) {
                        hashMap.put(catName, Item.newAppItem(app2));
                    } else {
                        Item itemChild = (Item) hashMap.get(catName);
                        if (itemChild.type == Type.GROUP) {
                            itemChild.getGroupItems().add(Item.newAppItem(app2));
                        } else {
                            Item group = Item.newGroupItem();
                            group.getGroupItems().add(itemChild);
                            group.getGroupItems().add(Item.newAppItem(app2));
                            group.setLabel(catName);
                            hashMap.put(catName, group);
                        }
                    }
                }
            }
            int pageStart = 2;
            int xStart = 0;
            int yStart = 0;
            for (String key : hashMap.keySet()) {
                try {
                    Item groupItem = (Item) hashMap.get(key);
                    if (groupItem.getType() == Type.GROUP) {
                        groupItem.setX(xStart);
                        groupItem.setY(yStart);
                        for (Item itemAbc : groupItem.getGroupItems()) {
                            Home.db.saveItem(itemAbc, pageStart, ItemPosition.Desktop);
                            Home.db.updateSate(itemAbc, ItemState.Hidden);
                        }
                        Home.db.saveItem(groupItem, pageStart, ItemPosition.Desktop);
                        if (xStart == columns - 1) {
                            xStart = 0;
                            if (yStart == rows - 1) {
                                yStart = 0;
                                pageStart++;
                            } else {
                                yStart++;
                            }
                        } else {
                            xStart++;
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("HuyAnh", "error hashMap to db. desktop: " + e.getMessage());
                }
            }
            Setup.appSettings().setFirstCategoryApps();
        } else {
            android.util.Log.d("HuyAnh", "bo qua init category");
        }
        List<String> classNameGroup = new ArrayList();
        for (Item groupItem2 : Home.db.getGroup()) {
            apps.add(groupItem2);
            for (Item item22 : groupItem2.getGroupItems()) {
                try {
                    classNameGroup.add(item22.getPackageName() + "/" + item22.getClassName());
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        for (App app22 : allApps) {
            boolean isAdd = true;
            if (classNameGroup.contains(app22.getPackageName() + "/" + app22.getClassName())) {
                isAdd = false;
            }
            if (isAdd) {
                if (app22.getPackageName().equals(getContext().getPackageName())) {
                    apps.add(0, Item.newAppItem(app22));
                } else if (!application.isPurchase()) {
                    apps.add(Item.newAppItem(app22));
                } else if (BaseUtils.isInstalled(getContext(), app22.getPackageName())) {
                    apps.add(Item.newAppItem(app22));
                } else {
                    android.util.Log.d("HuyAnh", "da purchase. remove shortcut: " + app22.getPackageName());
                }
            }
        }

        this.pageCount = 0;
        int appsSize = apps.size();
        android.util.Log.d("HuyAnh", "---- app size: " + appsSize);
        while (true) {
            appsSize -= columns * rows;
            if (appsSize < columns * rows && appsSize <= (-(columns * rows))) {
                break;
            }
            this.pageCount++;
        }
        if (Setup.dataManager().getMaxPage() > this.pageCount) {
            this.pageCount = Setup.dataManager().getMaxPage();
        }
        setAdapter(new DesktopAdapter(this));
        setPageTransformer(true, this.listTransformer[Setup.appSettings().getDesktopEffect()]);
        if (Setup.appSettings().isDesktopShowIndicator() && this.pageIndicator != null) {
            this.pageIndicator.setViewPager(this);
        }
        addPageLeft(false);
        setCurrentItem(Setup.appSettings().getDesktopPageCurrent());
        for (Item item222 : apps) {

            if (!(item222.getX() == -1 || item222.getY() == -1)) {
                int pageDb = Setup.dataManager().getPage(item222.getId().intValue());
                if (pageDb != -1 && Setup.dataManager().isShowDesktop(item222.getId().intValue())) {
                    addItemToPage(item222, pageDb);
                }
            }
        }
        int xPosition = 0;
        int yPosition = 0;
        int pagePostion = 1;
        int currentPostion = 0;
        while (currentPostion < apps.size()) {
            Item item2222 = (Item) apps.get(currentPostion);
            if (item2222.getX() == -1 || item2222.getY() == -1) {
                boolean isNull = true;
                if (((CellContainer) this.pages.get(pagePostion)).coordinateToChildView(new Point(xPosition, yPosition)) != null) {
                    isNull = false;
                }
                if (isNull) {
                    item2222.setX(xPosition);
                    item2222.setY(yPosition);
                    addItemToPage(item2222, pagePostion);
                }
                if (xPosition == columns - 1) {
                    xPosition = 0;
                    if (yPosition == rows - 1) {
                        yPosition = 0;
                        pagePostion++;
                    } else {
                        yPosition++;
                    }
                } else {
                    xPosition++;
                }
                if (isNull) {
                    currentPostion++;
                }
            } else {
                currentPostion++;
            }
        }
    }

    private boolean isInstall(String package_name, Item apps) {

        if (apps.getPackageName().equals(package_name)) {
            return true;
        }
        return false;
    }

    public void resetPivot() {
        getCurrentPage().setPivotX(((float) getCurrentPage().getWidth()) * 0.5f);
        getCurrentPage().setPivotY(((float) getCurrentPage().getHeight()) * 0.5f);
    }

    public void addPageRight(boolean showGrid) {
        Log.i("addPageRight Desktop 1");
        this.pageCount++;
        int previousPage = getCurrentItem();
        ((DesktopAdapter) getAdapter()).addPageRight();
        setCurrentItem(previousPage + 1);
        for (CellContainer cellContainer : this.pages) {
            cellContainer.setHideGrid(!showGrid);
        }
        this.pageIndicator.invalidate();
    }

    public void addPageLeft(boolean showGrid) {
        this.pageCount++;
        int previousPage = getCurrentItem();
        ((DesktopAdapter) getAdapter()).addPageLeft();
        setCurrentItem(previousPage + 1, false);
        setCurrentItem(previousPage - 1);
        for (CellContainer cellContainer : this.pages) {
            cellContainer.setHideGrid(!showGrid);
        }
        this.pageIndicator.invalidate();
    }

    public void removeCurrentPage() {
        if (Setup.appSettings().getDesktopStyle() != 1) {
            this.pageCount--;
            int previousPage = getCurrentItem();
            ((DesktopAdapter) getAdapter()).removePage(getCurrentItem(), true);
            for (CellContainer v : this.pages) {
                v.setAlpha(0.0f);
                v.animate().alpha(Tool.DEFAULT_BACKOFF_MULT);
                v.setScaleX(0.85f);
                v.setScaleY(0.85f);
                v.animateBackgroundShow();
            }
            if (this.pageCount == 0) {
                addPageRight(false);
                ((DesktopAdapter) getAdapter()).exitDesktopEditMode();
                return;
            }
            setCurrentItem(previousPage);
            this.pageIndicator.invalidate();
        }
    }

    public boolean onDrag(View p1, DragEvent p2) {
        switch (p2.getAction()) {
            case 1:
                Tool.print((Object) "ACTION_DRAG_STARTED");
                return true;
            case 2:
                Tool.print((Object) "ACTION_DRAG_LOCATION");
                if (getCurrentPage().getAllCells().size() <= (Setup.appSettings().getDesktopColumnCount() * Setup.appSettings().getDesktopRowCount()) - 1) {
                    getCurrentPage().peekItemAndSwap(p2);
                }
                return true;
            case 3:
                if (getCurrentItem() == 0) {
                    android.util.Log.d("HuyAnh", "drop item to page 0. do no thing");
                    return true;
                }
                android.util.Log.d("HuyAnh", "drop item to page khac 0");
                Item item = (Item) DragDropHandler.getDraggedObject(p2);
                if (((DragAction) p2.getLocalState()).action == Action.APP_DRAWER) {
                    item.reset();
                }
                if (addItemToPoint(item, (int) p2.getX(), (int) p2.getY())) {
                    this.home.desktop.consumeRevert();
                    this.home.dock.consumeRevert();
                    Home home = this.home;
                    Home.db.saveItem(item, getCurrentItem(), ItemPosition.Desktop);
                } else {
                    View itemView = getCurrentPage().coordinateToChildView(getCurrentPage().touchPosToCoordinate((int) p2.getX(), (int) p2.getY(), item.getSpanX(), item.getSpanY(), false));
                    if (itemView != null) {
                        if (handleOnDropOver(this.home, item, (Item) itemView.getTag(), itemView, getCurrentPage(), getCurrentItem(), ItemPosition.Desktop, this)) {
                            this.home.desktop.consumeRevert();
                            this.home.dock.consumeRevert();
                        }
                    }
                    Toast.makeText(getContext(), R.string.toast_not_enough_space, Toast.LENGTH_SHORT).show();
                    this.home.dock.revertLastItem();
                    this.home.desktop.revertLastItem();
                }
                return true;
            case 4:
                Tool.print((Object) "ACTION_DRAG_ENDED");
                return true;
            case 5:
                Tool.print((Object) "ACTION_DRAG_ENTERED");
                return true;
            case 6:
                Tool.print((Object) "ACTION_DRAG_EXITED");
                return true;
            default:
                return false;
        }
    }

    public boolean isCurrentPageEmpty() {
        return getCurrentPage().getChildCount() == 0;
    }

    public CellContainer getCurrentPage() {
        return (CellContainer) this.pages.get(getCurrentItem());
    }

    public void setLastItem(Object... args) {
        Item item = (Item) args[0];
        View v = (View) args[1];
        this.previousPage = getCurrentItem();
        this.previousItemView = v;
        this.previousItem = item;
        getCurrentPage().removeView(v);
    }

    public void revertLastItem() {
        if (this.previousItemView != null && getAdapter().getCount() >= this.previousPage && this.previousPage > -1) {
            getCurrentPage().addViewToGrid(this.previousItemView);
            this.previousItem = null;
            this.previousItemView = null;
            this.previousPage = -1;
        }
    }

    public void consumeRevert() {
        this.previousItem = null;
        this.previousItemView = null;
        this.previousPage = -1;
    }

    public boolean addItemToPage(Item item, int page) {
        View itemView = ItemViewFactory.getItemView(getContext(), item, Setup.appSettings().isDesktopShowLabel(), (DesktopCallBack) this, Setup.appSettings().getDesktopIconSize(), -1);
        if (itemView == null) {
            Home home = this.home;
            Home.db.deleteItem(item, true);
            return false;
        }
        ((CellContainer) this.pages.get(page)).addViewToGrid(itemView, item.getX(), item.getY(), item.getSpanX(), item.getSpanY());
        return true;
    }

    public boolean addItemToPoint(Item item, int x, int y) {
        CellContainer.LayoutParams positionToLayoutPrams = getCurrentPage().coordinateToLayoutParams(x, y, item.getSpanX(), item.getSpanY());
        if (positionToLayoutPrams == null) {
            return false;
        }
        item.setX(positionToLayoutPrams.x);
        item.setY(positionToLayoutPrams.y);
        View itemView = ItemViewFactory.getItemView(getContext(), item, Setup.appSettings().isDesktopShowLabel(), (DesktopCallBack) this, Setup.appSettings().getDesktopIconSize(), -1);
        if (itemView != null) {
            itemView.setLayoutParams(positionToLayoutPrams);
            getCurrentPage().addView(itemView);
        }
        return true;
    }

    public boolean addItemToCell(Item item, int x, int y) {
        item.setX(x);
        item.setY(y);
        View itemView = ItemViewFactory.getItemView(getContext(), item, Setup.appSettings().isDesktopShowLabel(), (DesktopCallBack) this, Setup.appSettings().getDesktopIconSize(), -1);
        if (itemView == null) {
            return false;
        }
        getCurrentPage().addViewToGrid(itemView, item.getX(), item.getY(), item.getSpanX(), item.getSpanY());
        return true;
    }

    public void removeItem(View view) {
        getCurrentPage().removeViewInLayout(view);
    }

    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        if (!isInEditMode()) {
            WallpaperManager.getInstance(getContext()).setWallpaperOffsets(getWindowToken(), (((float) position) + offset) / ((float) (this.pageCount - 1)), 0.0f);
            super.onPageScrolled(position, offset, offsetPixels);
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (VERSION.SDK_INT >= 20) {
            topInset = insets.getSystemWindowInsetTop();
            bottomInset = insets.getSystemWindowInsetBottom();
            if (Home.launcher != null) {
                Home.launcher.updateHomeLayout();
            }
        }
        return insets;
    }

    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }
}
