package com.benny.openlauncher.core.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.core.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.BaseActivity;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.AppDeleteListener;
import com.benny.openlauncher.core.interfaces.AppUpdateListener;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.manager.Setup.DataManager;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.AppUpdateReceiver;
import com.benny.openlauncher.core.util.Definitions.ItemPosition;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.viewutil.DragNavigationControl;
import com.benny.openlauncher.core.viewutil.WidgetHost;
import com.benny.openlauncher.core.widget.AppItemView;
import com.benny.openlauncher.core.widget.CellContainer;
import com.benny.openlauncher.core.widget.Desktop;
import com.benny.openlauncher.core.widget.Desktop.OnDesktopEditListener;
import com.benny.openlauncher.core.widget.DesktopOptionView;
import com.benny.openlauncher.core.widget.DesktopOptionView.DesktopOptionViewListener;
import com.benny.openlauncher.core.widget.Dock;
import com.benny.openlauncher.core.widget.DragOptionView;
import com.benny.openlauncher.core.widget.GroupPopupViewNew;
import com.benny.openlauncher.core.widget.PagerIndicator;
import com.benny.openlauncher.core.widget.SmoothViewPager.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;

public abstract class Home extends BaseActivity implements OnDesktopEditListener, DesktopOptionViewListener {
    public static final int REQUEST_CREATE_APPWIDGET = 13896;
    public static final int REQUEST_PERMISSION_CALL = 9966229;
    public static final int REQUEST_PERMISSION_READ_CALL_LOG = 9966228;
    public static final int REQUEST_PERMISSION_STORAGE = 9966230;
    public static final int REQUEST_PICK_APPWIDGET = 25717;
    private static final IntentFilter appUpdateIntentFilter = new IntentFilter();
    public static WidgetHost appWidgetHost;
    public static AppWidgetManager appWidgetManager;
    public static boolean consumeNextResume;
    public static DataManager db;
    public static Home launcher;
    public static Resources resources;
    public static int touchX = 0;
    public static int touchY = 0;
    private PagerIndicator appDrawerIndicator;
    private final BroadcastReceiver appUpdateReceiver = new AppUpdateReceiver();
    public View background;
    public ConstraintLayout baseLayout;
    private int cx;
    private int cy;
    public Desktop desktop;
    public DesktopOptionView desktopEditOptionView;
    public PagerIndicator desktopIndicator;
    public Dock dock;
    public View dragLeft;
    public DragOptionView dragOptionView;
    public View dragRight;
    public boolean first_show_popup = true;
    public GroupPopupViewNew groupPopup;
    protected ViewGroup myScreen;
    private int rad;
    private int count=0;
    protected abstract void initStaticHelper();

    static {
        appUpdateIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        appUpdateIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        appUpdateIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        appUpdateIntentFilter.addDataScheme("package");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("my", "onCreate: Home core");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.baseApplication.widthPixels = metrics.widthPixels;
        this.baseApplication.heightPixels = metrics.heightPixels;
        if (!Setup.wasInitialised()) {
            initStaticHelper();
        }
        resources = getResources();
        launcher = this;
        db = Setup.dataManager();
        this.myScreen = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_home, this.myScreen);
        setContentView(this.myScreen);
        bindViews();
        if (VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(1536);
        }
        init();
    }

    protected void bindViews() {
        this.desktop = (Desktop) findViewById(R.id.desktop);
        this.background = findViewById(R.id.background);
        this.dragLeft = findViewById(R.id.left);
        this.dragRight = findViewById(R.id.right);
        this.desktopIndicator = (PagerIndicator) findViewById(R.id.desktopIndicator);
        this.dock = (Dock) findViewById(R.id.dock);
        this.baseLayout = (ConstraintLayout) findViewById(R.id.baseLayout);
        this.dragOptionView = (DragOptionView) findViewById(R.id.dragOptionPanel);
        this.desktopEditOptionView = (DesktopOptionView) findViewById(R.id.desktopEditOptionPanel);
        this.groupPopup = (GroupPopupViewNew) findViewById(R.id.groupPopup);
    }

    protected void unbindViews() {
        this.desktop = null;
        this.background = null;
        this.dragLeft = null;
        this.dragRight = null;
        this.desktopIndicator = null;
        this.dock = null;
        this.baseLayout = null;
        this.dragOptionView = null;
        this.desktopEditOptionView = null;
        this.groupPopup = null;
    }

    private void init() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Home.appWidgetHost = new WidgetHost(Home.this.getApplicationContext(), R.id.app_widget_host);
                Home.appWidgetManager = AppWidgetManager.getInstance(Home.this);
                Home.appWidgetHost.startListening();
                Home.this.initViews();
                Home.this.registerBroadcastReceiver();
                Home.this.initAppManager();
                Home.this.initSettings();
                System.runFinalization();
                System.gc();
            }
        }, 1000);
    }

    protected void initViews() {
        initDock();
        DragNavigationControl.init(this, this.dragLeft, this.dragRight);
        this.appDrawerIndicator = (PagerIndicator) findViewById(R.id.appDrawerIndicator);
        this.dragOptionView.setHome(this);
        this.desktop.init();
        this.desktop.setDesktopEditListener(this);
        this.desktopEditOptionView.setDesktopOptionViewListener(this);
        this.desktopEditOptionView.updateLockIcon(Setup.appSettings().isDesktopLock());
        this.desktop.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                count++;
                Log.e("count",count+"  ");
                if (count==20){

                    loadAd(Home.this);
                    count=0;
                }
                if (Home.this.desktopEditOptionView != null) {
                    Home.this.desktopEditOptionView.updateHomeIcon(Setup.appSettings().getDesktopPageCurrent() == position);
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        this.desktop.setPageIndicator(this.desktopIndicator);
        this.dragOptionView.setAutoHideView(this.desktopIndicator);
    }
    private void loadAd(Context context) {
//        mInterstitialAd = new InterstitialAd(context);
//        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.full_ads));
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mInterstitialAd.loadAd(adRequest);
//        mInterstitialAd.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//
//                }
//            }
//        });
    }
    protected void initAppManager() {
        Setup.appLoader().addUpdateListener(new AppUpdateListener<App>() {
            public boolean onAppUpdated(List<App> list) {
                if (Setup.appSettings().getDesktopStyle() != 1 && Setup.appSettings().isAppFirstLaunch()) {
                    Setup.appSettings().setAppFirstLaunch(false);
                    Item appDrawerBtnItem = Item.newActionItem(8);
                    appDrawerBtnItem.setX(2);
                    Home.db.saveItem(appDrawerBtnItem, 0, ItemPosition.Dock);
                }
                if (Home.this.desktop != null) {
                    if (Setup.appSettings().getDesktopStyle() == 0) {
                        Home.this.desktop.initDesktopNormal(Home.this);
                    } else if (Setup.appSettings().getDesktopStyle() == 1) {
                        Home.this.desktop.initDesktopShowAll(Home.this, Home.this);
                    }
                    if (Home.this.dock != null) {
                        Home.this.dock.initDockItem(Home.this);
                    }
                }
                return false;
            }
        });
        Setup.appLoader().addDeleteListener(new AppDeleteListener<App>() {
            public boolean onAppDeleted(List<App> list) {
                if (Home.this.desktop != null) {
                    if (Setup.appSettings().getDesktopStyle() == 0) {
                        Home.this.desktop.initDesktopNormal(Home.this);
                    } else if (Setup.appSettings().getDesktopStyle() == 1) {
                        Home.this.desktop.initDesktopShowAll(Home.this, Home.this);
                    }
                    if (Home.this.dock != null) {
                        Home.this.dock.initDockItem(Home.this);
                    }
                }
                return false;
            }
        });
    }

    public void onDesktopEdit() {
        this.dragOptionView.resetAutoHideView();
        Tool.visibleViews(100, this.desktopEditOptionView);
        Tool.invisibleViews(100, this.desktopIndicator);
        updateDock(false);
    }

    public void onFinishDesktopEdit() {
        Tool.visibleViews(100, this.desktopIndicator);
        Tool.invisibleViews(100, this.desktopEditOptionView);
        updateDock(true);
        this.dragOptionView.setAutoHideView(this.desktopIndicator);
    }

    public void onRemovePage() {
        this.desktop.removeCurrentPage();
    }

    public void onSetPageAsHome() {
        Setup.appSettings().setDesktopPageCurrent(this.desktop.getCurrentItem());
    }

    public void onLaunchSettings() {
        consumeNextResume = true;
        Setup.eventHandler().showLauncherSettings(this);
    }

    public void onPickDesktopAction() {
    }

    public void onPickWidget() {
        pickWidget();
    }

    protected void initSettings() {
        updateHomeLayout();
        if (Setup.appSettings().isDesktopFullscreen()) {
            getWindow().setFlags(1024, 1024);
        } else {
            getWindow().setFlags(2048, 2048);
        }
        if (this.desktop != null) {
            Log.e("color",Setup.appSettings().getDesktopColor()+"");
            this.desktop.setBackgroundColor(Setup.appSettings().getDesktopColor());
        }
        if (this.dock != null) {
            this.dock.setBackgroundResource(R.drawable.bg_dock_iporn_x);
            Drawable background = this.dock.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(Setup.appSettings().getDockColor());
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(Setup.appSettings().getDockColor());
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable) background).setColor(Setup.appSettings().getDockColor());
            }
            LayoutParams params = (LayoutParams) this.dock.getLayoutParams();
            int A = ((this.dock.getWidth() - (Setup.appSettings().getDesktopColumnCount() * BaseUtils.genpx((Context) this, Setup.appSettings().getDesktopIconSize()))) * ((int) AppItemView.partPadding)) / ((((Setup.appSettings().getDesktopColumnCount() * 2) * ((int) AppItemView.partPadding)) + (Setup.appSettings().getDesktopColumnCount() * 4)) + (((int) AppItemView.partPadding) * 2));
//            params.leftMargin = A;
//            params.rightMargin = A;
            //params.bottomMargin = BaseUtils.genpx(this.activity, 10);
        }
    }

    private void initDock() {
        int iconSize = Setup.appSettings().getDockIconSize();
        this.dock.init();
        if (Setup.appSettings().isDockShowLabel()) {
            this.dock.getLayoutParams().height = Tool.dp2px(((iconSize + 16) + 14) + 10, (Context) this) + Dock.bottomInset;
            return;
        }

        this.dock.getLayoutParams().height = Tool.dp2px((iconSize + 16) + 10, (Context) this) + Dock.bottomInset;
    }

    public void updateDock(boolean show) {
        if (this.dock != null && this.desktop.getLayoutParams() != null && this.desktopIndicator.getLayoutParams() != null) {
            if (Setup.appSettings().getDockEnable() && show) {
                Tool.visibleViews(100, this.dock);
                ((MarginLayoutParams) this.desktop.getLayoutParams()).bottomMargin = Tool.dp2px(4, (Context) this);
                ((MarginLayoutParams) this.desktopIndicator.getLayoutParams()).bottomMargin = Tool.dp2px(4, (Context) this);
            } else if (Setup.appSettings().getDockEnable()) {
                Tool.invisibleViews(100, this.dock);
            } else {
                Tool.goneViews(100, this.dock);
                ((MarginLayoutParams) this.desktopIndicator.getLayoutParams()).bottomMargin = Desktop.bottomInset + Tool.dp2px(4, (Context) this);
                ((MarginLayoutParams) this.desktop.getLayoutParams()).bottomMargin = Tool.dp2px(4, (Context) this);
            }
        }
    }

    public void updateHomeLayout() {
        updateDock(true);
        if (!(this.desktopIndicator == null || Setup.appSettings().isDesktopShowIndicator())) {
            Tool.goneViews(100, this.desktopIndicator);
        }
        if (this.desktop != null) {
            if (!Setup.appSettings().getSearchBarEnable()) {
                ((MarginLayoutParams) this.dragLeft.getLayoutParams()).topMargin = Desktop.topInset;
                ((MarginLayoutParams) this.dragRight.getLayoutParams()).topMargin = Desktop.topInset;
                this.desktop.setPadding(0, Desktop.topInset, 0, 0);
            }
            if (!Setup.appSettings().getDockEnable()) {
                this.desktop.setPadding(0, 0, 0, Desktop.bottomInset);
            }

        }
    }

    private void registerBroadcastReceiver() {
        registerReceiver(this.appUpdateReceiver, appUpdateIntentFilter);
    }

    private void pickWidget() {
        if (appWidgetHost != null) {
            consumeNextResume = true;
            int appWidgetId = appWidgetHost.allocateAppWidgetId();
            Intent pickIntent = new Intent("android.appwidget.action.APPWIDGET_PICK");
            pickIntent.putExtra("appWidgetId", appWidgetId);
            addEmptyData(pickIntent);
            startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
            Log.d("HuyAnh", "pickWidget " + appWidgetId);
        }
    }

    private void addEmptyData(Intent pickIntent) {
        pickIntent.putParcelableArrayListExtra("customInfo", new ArrayList());
        pickIntent.putParcelableArrayListExtra("customExtras", new ArrayList());
    }

    private void configureWidget(Intent data) {
        try {
            int appWidgetId = data.getExtras().getInt("appWidgetId", -1);
            Log.d("HuyAnh", "configureWidget: " + appWidgetId);
            AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
            if (appWidgetInfo.configure != null) {
                Log.v("HuyAnh", "appWidgetInfo.configure != null");
                Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
                intent.setComponent(appWidgetInfo.configure);
                intent.putExtra("appWidgetId", appWidgetId);
                startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
                return;
            }
            createWidget(data);
        } catch (Exception e) {
            Log.e("HuyAnh", "error configureWidget: " + e.getMessage());
        }
    }

    private void createWidget(Intent data) {
        if (((CellContainer) this.desktop.pages.get(launcher.desktop.getCurrentItem())).cellWidth != 0 && ((CellContainer) this.desktop.pages.get(launcher.desktop.getCurrentItem())).cellHeight != 0) {
            int appWidgetId = data.getExtras().getInt("appWidgetId", -1);
            Log.d("HuyAnh", "createWidget: " + appWidgetId);
            AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
            Item item = Item.newWidgetItem(appWidgetId);
            item.setSpanX(((appWidgetInfo.minWidth - 1) / ((CellContainer) this.desktop.pages.get(launcher.desktop.getCurrentItem())).cellWidth) + 1);
            item.setSpanY(((appWidgetInfo.minHeight - 1) / ((CellContainer) this.desktop.pages.get(launcher.desktop.getCurrentItem())).cellHeight) + 1);
            Point point = this.desktop.getCurrentPage().findFreeSpace(item.getSpanX(), item.getSpanY());
            if (point != null) {
                item.setX(point.x);
                item.setY(point.y);
                db.saveItem(item, this.desktop.getCurrentItem(), ItemPosition.Desktop);
                this.desktop.addItemToPage(item, this.desktop.getCurrentItem());
                return;
            }
            Tool.toast((Context) this, (int) R.string.toast_not_enough_space);
        }
    }

    protected void onDestroy() {
        if (appWidgetHost != null) {
            appWidgetHost.stopListening();
        }
        appWidgetHost = null;
        try {
            if (this.appUpdateReceiver != null) {
                unregisterReceiver(this.appUpdateReceiver);
            }
        } catch (Exception e) {
        }
        launcher = null;
        unbindViews();
        super.onDestroy();
    }

    public void onLowMemory() {
        System.runFinalization();
        System.gc();
        super.onLowMemory();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("HuyAnh", "onActivityResult Home core. requestCode : " + requestCode + " resultCode:" + resultCode);
        if (resultCode == -1) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                Log.d("HuyAnh", "REQUEST_PICK_APPWIDGET");
                configureWidget(data);
            } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                Log.d("HuyAnh", "REQUEST_CREATE_APPWIDGET");
                createWidget(data);
            }
        } else if (resultCode == 0 && data != null) {
            int appWidgetId = data.getIntExtra("appWidgetId", -1);
            Log.d("HuyAnh", "RESULT_CANCELED: " + appWidgetId);
            if (appWidgetId != -1) {
                appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    protected void onStart() {
        launcher = this;
        if (appWidgetHost != null) {
            appWidgetHost.startListening();
        }
        super.onStart();
    }

    public void onBackPressed() {
        handleLauncherPause();
    }

    public void resetFirstShowPopup() {
        this.first_show_popup = true;
    }

    @SuppressLint("WrongConstant")
    protected void onResume() {
        if (Setup.appSettings().getAppRestartRequired()) {
            Setup.appSettings().setAppRestartRequired(false);
            ((AlarmManager) getSystemService(NotificationCompat.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 100, PendingIntent.getActivity(this, 123556, new Intent(this, Home.class), 268435456));
            System.exit(0);
            return;
        }
        launcher = this;
        if (appWidgetHost != null) {
            appWidgetHost.startListening();
        }
        handleLauncherPause();
        super.onResume();
    }

    private void handleLauncherPause() {
        if (consumeNextResume) {
            consumeNextResume = false;
        } else {
            onHandleLauncherPause();
        }
    }

    protected void onHandleLauncherPause() {
        this.groupPopup.dismissPopup();
        if (this.desktop != null) {
            if (this.desktop.inEditMode) {
                ((CellContainer) this.desktop.pages.get(this.desktop.getCurrentItem())).performClick();
            } else {
                this.desktop.setCurrentItem(Setup.appSettings().getDesktopPageCurrent());
            }
        }
    }
}
