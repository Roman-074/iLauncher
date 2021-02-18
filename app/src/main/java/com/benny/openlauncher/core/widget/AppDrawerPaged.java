package com.benny.openlauncher.core.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.AppUpdateListener;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.viewutil.SmoothPagerAdapter;
import com.benny.openlauncher.core.widget.AppItemView.LongPressCallBack;
import com.benny.openlauncher.core.widget.CellContainer.LayoutParams;
import java.util.ArrayList;
import java.util.List;

public class AppDrawerPaged extends SmoothViewPager {
    private static int columnCellCount;
    private static int rowCellCount;
    private PagerIndicator appDrawerIndicator;
    private List<App> apps;
    private Home home;
    private int pageCount = 0;
    public List<ViewGroup> pages = new ArrayList();

    public class Adapter extends SmoothPagerAdapter {
        private View getItemView(int page, int x, int y) {
            int pos = ((AppDrawerPaged.rowCellCount * AppDrawerPaged.columnCellCount) * page) + ((AppDrawerPaged.columnCellCount * y) + x);
            if (pos >= AppDrawerPaged.this.apps.size()) {
                return null;
            }
            return AppItemView.createDrawerAppItemView(AppDrawerPaged.this.getContext(), AppDrawerPaged.this.home, (App) AppDrawerPaged.this.apps.get(pos), Setup.appSettings().getDrawerIconSize(), new LongPressCallBack() {
                public boolean readyForDrag(View view) {
                    return Setup.appSettings().getDesktopStyle() != 1;
                }

                public void afterDrag(View view) {
                }
            });
        }

        public Adapter() {
            AppDrawerPaged.this.pages.clear();
            for (int i = 0; i < getCount(); i++) {
                ViewGroup layout = (ViewGroup) LayoutInflater.from(AppDrawerPaged.this.getContext()).inflate(R.layout.view_app_drawer_paged_inner, null);
                if (Setup.appSettings().isDrawerShowCardView()) {
                    ((CardView) layout.getChildAt(0)).setCardBackgroundColor(Setup.appSettings().getDrawerCardColor());
                    ((CardView) layout.getChildAt(0)).setCardElevation((float) Tool.dp2px(4, AppDrawerPaged.this.getContext()));
                } else {
                    ((CardView) layout.getChildAt(0)).setCardBackgroundColor(0);
                    ((CardView) layout.getChildAt(0)).setCardElevation(0.0f);
                }
                CellContainer cc = (CellContainer) layout.findViewById(R.id.group);
                cc.setGridSize(AppDrawerPaged.columnCellCount, AppDrawerPaged.rowCellCount);
                for (int x = 0; x < AppDrawerPaged.columnCellCount; x++) {
                    for (int y = 0; y < AppDrawerPaged.rowCellCount; y++) {
                        View view = getItemView(i, x, y);
                        if (view != null) {
                            CellContainer.LayoutParams lp = new CellContainer.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, x, y, 1, 1);
                            view.setLayoutParams(lp);
                            cc.addViewToGrid(view);
                        }
                    }
                }
                AppDrawerPaged.this.pages.add(layout);
            }
        }

        public int getCount() {
            return AppDrawerPaged.this.pageCount;
        }

        public boolean isViewFromObject(View p1, Object p2) {
            return p1 == p2;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public int getItemPosition(Object object) {
            int index = AppDrawerPaged.this.pages.indexOf(object);
            if (index == -1) {
                return -2;
            }
            return index;
        }

        public Object instantiateItem(ViewGroup container, int pos) {
            ViewGroup layout = (ViewGroup) AppDrawerPaged.this.pages.get(pos);
            container.addView(layout);
            return layout;
        }
    }

    public AppDrawerPaged(Context c, AttributeSet attr) {
        super(c, attr);
        init(c);
    }

    public AppDrawerPaged(Context c) {
        super(c);
        init(c);
    }

    protected void onConfigurationChanged(Configuration newConfig) {
        if (this.apps == null) {
            super.onConfigurationChanged(newConfig);
            return;
        }
        if (newConfig.orientation == 2) {
            setLandscapeValue();
            calculatePage();
            setAdapter(new Adapter());
        } else if (newConfig.orientation == 1) {
            setPortraitValue();
            calculatePage();
            setAdapter(new Adapter());
        }
        super.onConfigurationChanged(newConfig);
    }

    private void setPortraitValue() {
        columnCellCount = Setup.appSettings().getDrawerColumnCount();
        rowCellCount = Setup.appSettings().getDrawerRowCount();
    }

    private void setLandscapeValue() {
        columnCellCount = Setup.appSettings().getDrawerRowCount();
        rowCellCount = Setup.appSettings().getDrawerColumnCount();
    }

    private void calculatePage() {
        this.pageCount = 0;
        int appsSize = this.apps.size();
        while (true) {
            appsSize -= rowCellCount * columnCellCount;
            if (appsSize >= rowCellCount * columnCellCount || appsSize > (-(rowCellCount * columnCellCount))) {
                this.pageCount++;
            } else {
                return;
            }
        }
    }

    private void init(Context c) {
        boolean mPortrait = true;
        if (!isInEditMode()) {
            if (c.getResources().getConfiguration().orientation != 1) {
                mPortrait = false;
            }
            if (mPortrait) {
                setPortraitValue();
            } else {
                setLandscapeValue();
            }
            List<App> allApps = Setup.appLoader().getAllApps(c);
            if (allApps.size() != 0) {
                this.apps = allApps;
                calculatePage();
                setAdapter(new Adapter());
                if (this.appDrawerIndicator != null) {
                    this.appDrawerIndicator.setViewPager(this);
                }
            }
            Setup.appLoader().addUpdateListener(new AppUpdateListener<App>() {
                public boolean onAppUpdated(List<App> apps) {
                    AppDrawerPaged.this.apps = apps;
                    AppDrawerPaged.this.calculatePage();
                    AppDrawerPaged.this.setAdapter(new Adapter());
                    if (AppDrawerPaged.this.appDrawerIndicator != null) {
                        AppDrawerPaged.this.appDrawerIndicator.setViewPager(AppDrawerPaged.this);
                    }
                    return false;
                }
            });
        }
    }

    public void withHome(Home home, PagerIndicator appDrawerIndicator) {
        this.home = home;
        this.appDrawerIndicator = appDrawerIndicator;
        if (getAdapter() != null) {
            appDrawerIndicator.setViewPager(this);
        }
    }

    public void resetAdapter() {
        setAdapter(null);
        setAdapter(new Adapter());
    }
}
