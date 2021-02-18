package com.benny.openlauncher.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.AppUpdateListener;
import com.benny.openlauncher.core.interfaces.FastItem.AppItem;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.DrawerAppItem;
import com.benny.openlauncher.core.util.Tool;
import com.mikepenz.fastadapter.IItemAdapter.Predicate;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;
import com.turingtechnologies.materialscrollbar.INameableAdapter;
import java.util.ArrayList;
import java.util.List;

public class AppDrawerVertical extends CardView {
    private static List<App> apps;
    public static int itemHeightPadding;
    public static int itemWidth;
    public GridAppDrawerAdapter gridDrawerAdapter;
    private GridLayoutManager layoutManager;
    public RecyclerView recyclerView;
    private RelativeLayout rl;
    public DragScrollBar scrollBar;

    public static class GridAppDrawerAdapter extends FastItemAdapter<AppItem> implements INameableAdapter {
        GridAppDrawerAdapter() {
            getItemFilter().withFilterPredicate(new Predicate<AppItem>() {
                public boolean filter(AppItem item, CharSequence constraint) {
                    return !item.getApp().getLabel().toLowerCase().contains(constraint.toString().toLowerCase());
                }
            });
        }

        public Character getCharacterForElement(int element) {
            if (AppDrawerVertical.apps == null || element >= AppDrawerVertical.apps.size() || AppDrawerVertical.apps.get(element) == null || ((App) AppDrawerVertical.apps.get(element)).getLabel().length() <= 0) {
                return Character.valueOf('#');
            }
            return Character.valueOf(((App) AppDrawerVertical.apps.get(element)).getLabel().charAt(0));
        }
    }

    public AppDrawerVertical(Context context) {
        super(context);
        preInit();
    }

    public AppDrawerVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit();
    }

    public AppDrawerVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit();
    }

    public void preInit() {
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressLint("WrongConstant")
            public void onGlobalLayout() {
                AppDrawerVertical.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                AppDrawerVertical.this.rl = (RelativeLayout) LayoutInflater.from(AppDrawerVertical.this.getContext()).inflate(R.layout.view_app_drawer_vertical_inner, AppDrawerVertical.this, false);
                AppDrawerVertical.this.recyclerView = (RecyclerView) AppDrawerVertical.this.rl.findViewById(R.id.vDrawerRV);
                AppDrawerVertical.this.layoutManager = new GridLayoutManager(AppDrawerVertical.this.getContext(), Setup.appSettings().getDrawerColumnCount());
                AppDrawerVertical.itemWidth = ((AppDrawerVertical.this.getWidth() - AppDrawerVertical.this.recyclerView.getPaddingRight()) - AppDrawerVertical.this.recyclerView.getPaddingRight()) / AppDrawerVertical.this.layoutManager.getSpanCount();
                AppDrawerVertical.this.init();
                if (!Setup.appSettings().isDrawerShowIndicator()) {
                    AppDrawerVertical.this.scrollBar.setVisibility(8);
                }
            }
        });
    }

    protected void onConfigurationChanged(Configuration newConfig) {
        if (apps == null) {
            super.onConfigurationChanged(newConfig);
            return;
        }
        if (newConfig.orientation == 2) {
            setLandscapeValue();
        } else if (newConfig.orientation == 1) {
            setPortraitValue();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void setPortraitValue() {
        this.layoutManager.setSpanCount(Setup.appSettings().getDrawerColumnCount());
        this.gridDrawerAdapter.notifyAdapterDataSetChanged();
    }

    private void setLandscapeValue() {
        this.layoutManager.setSpanCount(Setup.appSettings().getDrawerRowCount());
        this.gridDrawerAdapter.notifyAdapterDataSetChanged();
    }

    private void init() {
        itemHeightPadding = Tool.dp2px(15, getContext());
        this.scrollBar = (DragScrollBar) this.rl.findViewById(R.id.dragScrollBar);
        this.scrollBar.setIndicator(new AlphabetIndicator(getContext()), true);
        this.scrollBar.setClipToPadding(true);
        this.scrollBar.setDraggableFromAnywhere(true);
        this.scrollBar.post(new Runnable() {
            public void run() {
                AppDrawerVertical.this.scrollBar.setHandleColour(Setup.appSettings().getDrawerFastScrollerColor());
            }
        });
        boolean mPortrait = getContext().getResources().getConfiguration().orientation == 1;
        this.gridDrawerAdapter = new GridAppDrawerAdapter();
        this.recyclerView.setAdapter(this.gridDrawerAdapter);
        if (mPortrait) {
            setPortraitValue();
        } else {
            setLandscapeValue();
        }
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setDrawingCacheEnabled(true);
        List<App> allApps = Setup.appLoader().getAllApps(getContext());
        if (allApps.size() != 0) {
            apps = allApps;
            ArrayList<AppItem> items = new ArrayList();
            for (int i = 0; i < apps.size(); i++) {
                items.add(new DrawerAppItem((App) apps.get(i)));
            }
            this.gridDrawerAdapter.set(items);
        }
        Setup.appLoader().addUpdateListener(new AppUpdateListener<App>() {
            public boolean onAppUpdated(List<App> apps) {
                AppDrawerVertical appDrawerVertical = AppDrawerVertical.this;
                AppDrawerVertical.apps = apps;
                ArrayList<AppItem> items = new ArrayList();
                for (int i = 0; i < apps.size(); i++) {
                    items.add(new DrawerAppItem((App) apps.get(i)));
                }
                AppDrawerVertical.this.gridDrawerAdapter.set(items);
                return false;
            }
        });
        addView(this.rl);
    }
}
