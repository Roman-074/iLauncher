package com.benny.openlauncher.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;


import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator;
import com.benny.openlauncher.R;
import com.benny.openlauncher.adapter.AdapterAppSearch;
import com.benny.openlauncher.adapter.AdapterAppSearchListener;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.DialogListener.OnAddAppDrawerItemListener;
import com.benny.openlauncher.core.interfaces.DialogListener.OnEditDialogListener;
import com.benny.openlauncher.core.interfaces.SettingsManager;
import com.benny.openlauncher.core.interfaces.SwipeListener;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.manager.Setup.DataManager;
import com.benny.openlauncher.core.manager.Setup.EventHandler;
import com.benny.openlauncher.core.manager.Setup.ImageLoader;
import com.benny.openlauncher.core.manager.Setup.Logger;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.BaseIconProvider;
import com.benny.openlauncher.core.util.SimpleIconProvider;
import com.benny.openlauncher.core.viewutil.DesktopGestureListener.DesktopGestureCallback;
import com.benny.openlauncher.core.viewutil.ItemGestureListener.ItemGestureCallback;
import com.benny.openlauncher.core.widget.Dock.DockListener;
import com.benny.openlauncher.core.widget.SmoothViewPager.OnPageChangeListener;
import com.benny.openlauncher.customview.StatusBar;
import com.benny.openlauncher.service.ChatHeadService;
import com.benny.openlauncher.service.ThreadUpdateNotify;
import com.benny.openlauncher.util.AppManager;
import com.benny.openlauncher.util.AppSettings;
import com.benny.openlauncher.util.Constant;
import com.benny.openlauncher.util.DatabaseHelper;
import com.benny.openlauncher.util.DialogAppCallback;
import com.benny.openlauncher.util.DialogPermission;
import com.benny.openlauncher.util.LauncherAction;
import com.benny.openlauncher.util.LauncherAction.Action;
import com.benny.openlauncher.util.NotificationEnabledUtil;
import com.benny.openlauncher.util.Tool;
import com.benny.openlauncher.viewutil.DialogHelper;
import com.benny.openlauncher.viewutil.DialogHelper.onItemEditListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
@SuppressLint("ResourceType")
public class Home extends com.benny.openlauncher.core.activity.Home implements DrawerListener, FileChooserDialog.FileCallback {
    public static Item itemEdit = null;
    private final int REQUEST_CODE_SELECT_ICON_ACTIVITY = 22100;
    private AdapterAppSearch adapterAppSearch;

    @BindView(R.id.btOpenPermissionDraw)
    Button btOpenPermissionDraw;
    @BindView(R.id.etSearch)
    EditText etSearch;
    private GridLayoutManager gridLayoutManager;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;
    @BindView(R.id.ivShop)
    ImageView ivShop;
    @BindView(R.id.ivSwipeIcSearch)
    ImageView ivSwipeIcSearch;
    private ArrayList<App> listApp = new ArrayList();
    @BindView(R.id.llHelpPermissionDraw)
    LinearLayout llHelpPermissionDraw;
    @BindView(R.id.llHelpSwipeDown)
    LinearLayout llHelpSwipeDown;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.loadingSplash)
    public FrameLayout loadingSplash;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.rlSearchBarOut)
    RelativeLayout rlSearchBarOut;
    @BindView(R.id.statusBar)
    StatusBar statusBar;
    @BindView(R.id.tvHelp)
    TextView tvHelp;
    private Unbinder unbinder;

    class getRecent extends AsyncTask<Void, Void, ArrayList<App>> {
        getRecent() {
        }

        protected ArrayList<App> doInBackground(Void... voids) {
            ArrayList<App> list = new ArrayList();
            PackageManager pm = Home.this.getPackageManager();
            for (App app : Setup.appLoader().getAllApps(Home.this)) {
                if (list.size() >= 3) {
                    break;
                }
                try {
                    if ((System.currentTimeMillis() - new File(pm.getApplicationInfo(app.getPackageName(), 0).sourceDir).lastModified()) / 1000 < 1800) {
                        list.add(app);
                    }
                } catch (Exception e) {
                }
            }
            ArrayList<App> listRecent = new ArrayList();
            final ArrayList<String> packageNames = ((com.benny.openlauncher.App) Home.this.getApplication()).dbHelper.getListRecent(list);
            for (App app2 : Setup.appLoader().getAllApps(Home.this)) {
                if (packageNames.contains(app2.getPackageName())) {
                    listRecent.add(app2);
                }
            }
            Collections.sort(listRecent, new Comparator<App>() {
                public int compare(App app, App app1) {
                    if (packageNames.indexOf(app.getPackageName()) > packageNames.indexOf(app1.getPackageName())) {
                        return 1;
                    }
                    if (packageNames.indexOf(app.getPackageName()) < packageNames.indexOf(app1.getPackageName())) {
                        return -1;
                    }
                    return 0;
                }
            });

            list.addAll(listRecent);
            for (int i=0;i<list.size();i++){
                Log.e("tuan",list.get(i).getLabel()+"   "+list.get(i).getPackageName());
            }
            return list;
        }

        protected void onPostExecute(ArrayList<App> apps) {
            super.onPostExecute(apps);
            if (Home.this.listApp != null && Home.this.adapterAppSearch != null) {
                Home.this.listApp.clear();
                Home.this.listApp.addAll(apps);
                Home.this.adapterAppSearch.notifyDataSetChanged();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("my", "onCreate: Home");
        ButterKnife.bind(this);
        BaseUtils.printKeyHash(this);
    }

    protected void bindViews() {
        try {
            super.bindViews();
            this.unbinder = ButterKnife.bind((Activity) this);
        } catch (Exception e) {
        }
    }

    protected void unbindViews() {
        try {
            super.unbindViews();
            if (this.unbinder != null) {
                this.unbinder.unbind();
            }
        } catch (Exception e) {
        }
    }

    protected void initAppManager() {
        super.initAppManager();
        AppManager.getInstance(this).init();
    }

    protected void initSettings() {
        super.initSettings();
    }

    protected void initViews() {
        try {
            super.initViews();
            this.desktop.setSwipeListener(new SwipeListener() {
                private boolean enableSearch = true;

                public void onModifySearch(boolean enableSearch) {
                    this.enableSearch = enableSearch;
                }

                public void onSwipeDown() {
                    if (Home.this.desktop.getCurrentItem() != 0 && this.enableSearch) {
                        Home.this.rlSearchBarOut.getLayoutParams().height = -1;
                        Home.this.rlSearchBarOut.setBackgroundColor(Color.parseColor("#66000000"));
                        Home.this.rlSearchBarOut.setVisibility(0);
                        ViewPropertyObjectAnimator.animate(Home.this.llSearch).alpha(Tool.DEFAULT_BACKOFF_MULT).translationY(0.0f).setDuration(250).addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (Home.this.etSearch != null) {
                                    ((InputMethodManager) Home.this.getSystemService("input_method")).showSoftInput(Home.this.etSearch, 1);
                                }
                                new getRecent().execute(new Void[0]);
                            }
                        }).start();
                    }
                }

                public void onSwipeUp() {
                    if (ChatHeadService.chatHeadService != null) {
                        ChatHeadService.chatHeadService.showCC();
                    }
                }

                public void onDoubleTap() {
                    Log.d("HuyAnh", "onDoubleTap");
                }

                public void onMoveDown(float deltaMoveY) {
                    if (Home.this.desktop.getCurrentItem() == 0) {
                        return;
                    }
                    if (deltaMoveY == 0.0f) {
                        Home.this.ivSwipeIcSearch.setTranslationY(0.0f);
                        Home.this.ivSwipeIcSearch.setVisibility(8);
                        return;
                    }
                    if (!Home.this.ivSwipeIcSearch.isShown()) {
                        Home.this.ivSwipeIcSearch.setVisibility(0);
                    }
                    Home.this.ivSwipeIcSearch.setTranslationY(deltaMoveY);
                }

                public void onUpAndCancel() {
                    if (Home.this.desktop != null && Home.this.desktop.getCurrentItem() != 0) {
                        ViewPropertyObjectAnimator.animate(Home.this.ivSwipeIcSearch).translationY(0.0f).setDuration(200).addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (Home.this.ivSwipeIcSearch != null) {
                                    Home.this.ivSwipeIcSearch.setVisibility(8);
                                }
                            }
                        }).start();
                    }
                }
            });

            this.dock.setSwipeListener(new SwipeListener() {
                public void onSwipeDown() {
                    Log.d("HuyAnh", "onSwipeDown");
                }

                public void onSwipeUp() {
                    Log.d("HuyAnh", "onSwipeUp");
                    if (ChatHeadService.chatHeadService != null) {
                        ChatHeadService.chatHeadService.showCC();
                    }
                }

                public void onDoubleTap() {
                }

                public void onMoveDown(float deltaMoveY) {
                }

                public void onUpAndCancel() {
                }

                public void onModifySearch(boolean enableSearch) {
                }
            });
            this.desktop.addOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    if (!Home.this.desktop.inEditMode) {
                        if (position == 0) {
                            Home.this.rlSearchBarOut.getLayoutParams().height = -2;
                            Home.this.rlSearchBarOut.setBackgroundColor(0);
                            Home.this.rlSearchBarOut.setVisibility(View.VISIBLE);
                            ViewPropertyObjectAnimator.animate(Home.this.llSearch).alpha(Tool.DEFAULT_BACKOFF_MULT).translationY(0.0f).setDuration(250).addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    new getRecent().execute(new Void[0]);
                                }
                            }).start();
                        } else if (Home.this.rlSearchBarOut.getVisibility() == 0) {
                            ViewPropertyObjectAnimator.animate(Home.this.llSearch).alpha(0.0f).translationY((float) (0 - Home.this.genpx(Home.this, 100))).setDuration(200).addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (Home.this.rlSearchBarOut != null && Home.this.etSearch != null) {
                                        Home.this.rlSearchBarOut.setVisibility(View.INVISIBLE);
                                        Home.this.etSearch.setText("");
                                        Home.this.adapterAppSearch.closeMenu();
                                    }
                                }
                            }).start();
                            ((InputMethodManager) Home.this.getSystemService("input_method")).hideSoftInputFromWindow(Home.this.etSearch.getWindowToken(), 0);
                        }
                    }
                }

                public void onPageScrollStateChanged(int state) {
                    if (state == 0) {
                        Home.this.desktop.resetPivot();
                    }
                }
            });
            this.dock.setDockListener(new DockListener() {
                public void onInitDone() {
                    try {
                        Home.this.myScreen.removeView(Home.this.loadingSplash);
                    } catch (Exception e) {
                    }
                }
            });
            this.rlSearchBarOut.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Home.this.onBackPressed();
                }
            });
            this.ivBack.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Home.this.onBackPressed();
                }
            });
            this.ivShop.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    try {
                        Home.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=" + Home.this.etSearch.getText().toString() + "&c=apps")));
                    } catch (Exception e) {
                        Log.e("HuyAnh", "error open ch play search: " + e.getMessage());
                    }
                }
            });
            this.gridLayoutManager = new GridLayoutManager(this, 5);
            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setLayoutManager(this.gridLayoutManager);
            this.recyclerView.setItemAnimator(new DefaultItemAnimator());
            this.adapterAppSearch = new AdapterAppSearch(this, this.listApp, new AdapterAppSearchListener() {
                @SuppressLint({"WrongConstant"})
                public void onClickItem() {
                    try {
                        if (Home.this.rlSearchBarOut.getVisibility() == 0) {
                            ViewPropertyObjectAnimator.animate(Home.this.llSearch).alpha(0.0f).translationY((float) (0 - Home.this.genpx(Home.this, 100))).setDuration(200).addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (Home.this.rlSearchBarOut != null && Home.this.etSearch != null) {
                                        Home.this.rlSearchBarOut.setVisibility(4);
                                        Home.this.etSearch.setText("");
                                        Home.this.adapterAppSearch.closeMenu();
                                    }
                                }
                            }).start();
                            ((InputMethodManager) Home.this.getSystemService("input_method")).hideSoftInputFromWindow(Home.this.etSearch.getWindowToken(), 0);
                        }
                    } catch (Exception e) {
                    }
                }
            });
            this.recyclerView.setAdapter(this.adapterAppSearch);
            this.etSearch.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().equals("")) {
                        new getRecent().execute(new Void[0]);
                        return;
                    }
                    Home.this.listApp.clear();
                    for (int i = 0; i < Setup.appLoader().getAllApps(Home.this).size(); i++) {
                        App app = (App) Setup.appLoader().getAllApps(Home.this).get(i);
                        if (app.getLabel().toLowerCase().contains(s.toString().toLowerCase())) {
                            Home.this.listApp.add(app);
                        }
                    }
                    Home.this.adapterAppSearch.notifyDataSetChanged();
                }

                public void afterTextChanged(Editable s) {
                }
            });
            if (Setup.appSettings().isFirstHelpSwipeDown()) {
                this.llHelpSwipeDown.setVisibility(0);
                this.llHelpSwipeDown.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (Home.this.baseApplication != null) {
                            Home.this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_TUTORIAL_1);
                        }
                        Home.this.ivHelp.setImageResource(R.drawable.help_swipe2);
                        Home.this.tvHelp.setText(Home.this.getString(R.string.help_swipe_up_to_cc));
                        Home.this.llHelpSwipeDown.setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                if (Home.this.baseApplication != null) {
                                    Home.this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_TUTORIAL_2);
                                }
                                try {
                                    Home.this.myScreen.removeView(Home.this.llHelpSwipeDown);
                                } catch (Exception e) {
                                    Log.e("HuyAnh", "error remove llHelpSwipeDown: " + e.getMessage());
                                }
                                Setup.appSettings().setHelpSwipeDown();
                            }
                        });
                    }
                });
            } else {
                try {
                    this.myScreen.removeView(this.llHelpSwipeDown);
                } catch (Exception e) {
                    Log.e("HuyAnh", "error remove llHelpSwipeDown: " + e.getMessage());
                }
            }
            if (Setup.appSettings().isDesktopFullscreen()) {
                this.statusBar.setVisibility(0);
            } else {
                this.statusBar.setVisibility(8);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            finish();
        }
    }

    protected void onResume() {
        super.onResume();
        Log.d("HuyAnh", "-------------- onResume Home");
        try {
            if (this.baseApplication != null) {
                this.baseApplication.loadNewDataConfig();
            }
            processUpdate();
        } catch (Exception e) {
        }
        try {
            if (VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(this)) {
                    if (this.llHelpPermissionDraw != null) {
                        this.llHelpPermissionDraw.setVisibility(8);
                    }
                    if (!BaseUtils.isMyServiceRunning(this, ChatHeadService.class.getName())) {
                        startService(new Intent(this, ChatHeadService.class));
                    }
                } else if (!(this.llHelpPermissionDraw == null || this.btOpenPermissionDraw == null)) {
                    this.llHelpPermissionDraw.setVisibility(0);
                    this.llHelpPermissionDraw.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            Home.this.llHelpPermissionDraw.setVisibility(8);
                        }
                    });
                    this.btOpenPermissionDraw.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            try {
                                Home.this.resetFirstShowPopup();
                                Home.this.llHelpPermissionDraw.setVisibility(8);
                                Home.this.startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + Home.this.getPackageName())), Constant.REQUEST_PERMISSION_DRAW_OVER_APP);
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            } else if (!BaseUtils.isMyServiceRunning(this, ChatHeadService.class.getName())) {
                startService(new Intent(this, ChatHeadService.class));
            }
        } catch (Exception e2) {
        }
        try {
            if (ChatHeadService.chatHeadService != null) {
                ChatHeadService.chatHeadService.hideTouchView();
            }
        } catch (Exception e3) {
        }
        try {
            if (this.statusBar.isShown()) {
                this.statusBar.resetTimer(1);
            }
        } catch (Exception e4) {
        }
        if (this.rlSearchBarOut.getVisibility() == 0) {
            ViewPropertyObjectAnimator.animate(this.llSearch).alpha(0.0f).translationY((float) (0 - genpx(this, 100))).setDuration(200).addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (Home.this.rlSearchBarOut != null && Home.this.etSearch != null) {
                        Home.this.rlSearchBarOut.setVisibility(4);
                        Home.this.etSearch.setText("");
                        Home.this.adapterAppSearch.closeMenu();
                    }
                }
            }).start();
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(this.etSearch.getWindowToken(), 0);
        }
        if (VERSION.SDK_INT < 21 || NotificationEnabledUtil.isServicePermission(getApplicationContext())) {
            ThreadUpdateNotify.startThread(getApplicationContext());
            NotificationEnabledUtil.startService(getApplicationContext());
            if (this.baseApplication != null) {
                this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_PERMISSION_1_ACCEPT);
            }
            requestPermissionData();
        } else {
//            DialogPermission.showDialog(this, new DialogAppCallback() {
//                public void cancelDialog() {
//                    Home.this.requestPermissionData();
//                }
//
//                public void okDialog() {
//                    Home.this.resetFirstShowPopup();
//                    NotificationEnabledUtil.requestPermission(Home.this.getApplicationContext());
//                    if (Home.this.baseApplication != null) {
//                        Home.this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_PERMISSION_1);
//                    }
//                }
//            }, 2);
        }
        if (isMyAppLauncherDefault() && this.baseApplication != null) {
            this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_DEFAULT_LAUNCHER);
        }
        if (this.baseApplication != null) {
            this.baseApplication.putEventsDay();
        }
    }

    private boolean isMyAppLauncherDefault() {
        try {
            PackageManager localPackageManager = getPackageManager();
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            if (localPackageManager.resolveActivity(intent, 65536).activityInfo.packageName.equals(getPackageName())) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void requestPermissionData() {
        if (!isPermissionToReadHistory()) {
            DialogPermission.showDialog(this, new DialogAppCallback() {
                public void cancelDialog() {
                }

                public void okDialog() {
                    Home.this.resetFirstShowPopup();
                    Home.this.requestReadHistoryAccess();
                    if (Home.this.baseApplication != null) {
                        Home.this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_PERMISSION_2);
                    }
                }
            }, 1);
        } else if (this.baseApplication != null) {
            this.baseApplication.putEvents(BaseApplication.EVENTS_NAME_PERMISSION_2_ACCEPT);
        }
    }

    public void requestReadHistoryAccess() {
        try {
            if (VERSION.SDK_INT >= 21) {
                startActivity(new Intent("android.settings.USAGE_ACCESS_SETTINGS"));
            }
        } catch (Exception e) {
        }
    }

    @SuppressLint({"WrongConstant"})
    private void startHome() {
        Intent intent;
        try {
            intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.setFlags(268435456);
            intent.addFlags(33554432);
            intent.addFlags(16777216);
            intent.addFlags(2097152);
            intent.setPackage(getPackageName());
            startActivity(intent);
        } catch (Exception e) {
            intent = new Intent(this, Home.class);
            if (getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
            }
            intent.addFlags(268468224);
            getApplicationContext().startActivity(intent);
        }
    }

    @SuppressLint({"WrongConstant"})
    private boolean isPermissionToReadHistory() {
        if (VERSION.SDK_INT < 21 || ((AppOpsManager) getSystemService("appops")).checkOpNoThrow("android:get_usage_stats", Process.myUid(), getPackageName()) == 0) {
            return true;
        }
        return false;
    }

    protected void onPause() {
        super.onPause();
        if (ChatHeadService.chatHeadService != null) {
            ChatHeadService.chatHeadService.showTouchView();
        }
    }

    public void onRemovePage() {
        if (this.desktop.isCurrentPageEmpty()) {
            try {
                super.onRemovePage();
                return;
            } catch (Exception e) {
                return;
            }
        }
        DialogHelper.alertDialog(this, getString(R.string.remove), "This page is not empty. Those item will also be removed.", new SingleButtonCallback() {
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            this.statusBar.unRegisterBroadcast();
        } catch (Exception e) {
        }
    }

    @SuppressLint({"WrongConstant"})
    public void onBackPressed() {
        if (this.rlSearchBarOut.getVisibility() == 0) {
            ViewPropertyObjectAnimator.animate(this.llSearch).alpha(0.0f).translationY((float) (0 - genpx(this, 100))).setDuration(200).addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (Home.this.rlSearchBarOut != null && Home.this.etSearch != null) {
                        Home.this.rlSearchBarOut.setVisibility(4);
                        Home.this.etSearch.setText("");
                        Home.this.adapterAppSearch.closeMenu();
                    }
                }
            }).start();
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(this.etSearch.getWindowToken(), 0);
        } else if (!BaseUtils.isMyServiceRunning(this, ChatHeadService.class.getName()) || ChatHeadService.chatHeadService == null || !ChatHeadService.chatHeadService.hideCC()) {
            if (this.llHelpPermissionDraw.isShown()) {
                this.llHelpPermissionDraw.setVisibility(8);
            } else {
                super.onBackPressed();
            }
        }
    }

    private int genpx(Context context, int dp) {
        int pxTemp = (int) TypedValue.applyDimension(1, (float) dp, context.getResources().getDisplayMetrics());
        return pxTemp != 0 ? pxTemp : dp * 4;
    }

    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    public void onDrawerOpened(View drawerView) {
    }

    public void onDrawerClosed(View drawerView) {
    }

    public void onDrawerStateChanged(int newState) {
    }

    protected void onHandleLauncherPause() {
        super.onHandleLauncherPause();
    }

    protected void initStaticHelper() {
        final SettingsManager settingsManager = AppSettings.get();
        final ImageLoader imageLoader = new ImageLoader() {
            public BaseIconProvider createIconProvider(Drawable drawable) {
                return new SimpleIconProvider(drawable);
            }

            public BaseIconProvider createIconProvider(int icon) {
                return new SimpleIconProvider(icon);
            }

            public BaseIconProvider createIconProvider(String iconUrl) {
                return new SimpleIconProvider(iconUrl, Home.this);
            }
        };
        final DataManager dataManager = new DatabaseHelper(this);
        final AppManager appLoader = AppManager.getInstance(this);
        final EventHandler eventHandler = new EventHandler() {
            public void showLauncherSettings(Context context) {
                LauncherAction.RunAction(Action.LauncherSettings, context);
            }

            public void showPickAction(Context context, final OnAddAppDrawerItemListener listener) {
                DialogHelper.addActionItemDialog(context, new ListCallback() {
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                listener.onAdd();
                                return;
                            default:
                                return;
                        }
                    }
                });
            }

            public void showEditDialog(Context context, final Item item, OnEditDialogListener listener) {
                if (item != null) {
                    DialogHelper.editItemDialog("Edit Item", item, context, new onItemEditListener() {
                        public void itemLabel(String label) {
                            item.setLabel(label);
                            Home.db.saveItem(item);
                            Home.launcher.desktop.addItemToCell(item, item.getX(), item.getY());
                            Home.launcher.desktop.removeItem(Home.launcher.desktop.getCurrentPage().coordinateToChildView(new Point(item.getX(), item.getY())));
                        }
                    });
                }
            }

            public void showDeletePackageDialog(Context context, DragEvent dragEvent) {
                DialogHelper.deletePackageDialog(Home.this, dragEvent);
            }
        };
        final Logger logger = new Logger() {
            public void log(Object source, int priority, String tag, String msg, Object... args) {
                Log.println(priority, tag, String.format(msg, args));
            }
        };
        Setup.init(new Setup<AppManager.App>() {
            public Context getAppContext() {
                return com.benny.openlauncher.App.get();
            }

            public SettingsManager getAppSettings() {
                return settingsManager;
            }

            public DesktopGestureCallback getDesktopGestureCallback() {
                return null;
            }

            public ItemGestureCallback getItemGestureCallback() {
                return null;
            }

            public ImageLoader getImageLoader() {
                return imageLoader;
            }

            public DataManager getDataManager() {
                return dataManager;
            }

            public AppManager getAppLoader() {
                return appLoader;
            }

            public EventHandler getEventHandler() {
                return eventHandler;
            }

            public Logger getLogger() {
                return logger;
            }
        });
    }

    public void showSelectImgDialog(Item item) {
        itemEdit = item;
        resetFirstShowPopup();
        startActivityForResult(new Intent(this, SelectIconActivity.class), 22100);
    }

    public void onFileSelection(FileChooserDialog dialog, File file, Object object) {
        Log.v("HuyAnh", "" + file.getAbsolutePath() + " object: " + object);
        if (object instanceof Item) {
            itemEdit = (Item) object;
            itemEdit.changeIconProvider(Drawable.createFromPath(file.getAbsolutePath()));
            DialogHelper.editItemDialog("Edit Item", itemEdit, this, new onItemEditListener() {
                public void itemLabel(String label) {
                    Home.itemEdit.setLabel(label);
                    Home.db.saveItem(Home.itemEdit);
                    Home.launcher.desktop.addItemToCell(Home.itemEdit, Home.itemEdit.getX(), Home.itemEdit.getY());
                    Home.launcher.desktop.removeItem(Home.launcher.desktop.getCurrentPage().coordinateToChildView(new Point(Home.itemEdit.getX(), Home.itemEdit.getY())));
                }
            });
        }
    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {

    }

    public void onFileChooserDismissed(FileChooserDialog dialog) {
        Log.v("HuyAnh", "File chooser dismissed!");
    }

    public void onPickDesktopAction() {
        super.onPickDesktopAction();
        startActivity(Intent.createChooser(new Intent("android.intent.action.SET_WALLPAPER"), getResources().getString(R.string.wallpaper_pick)));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_PERMISSION_DRAW_OVER_APP /*1252*/:
                if (VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this) && !BaseUtils.isMyServiceRunning(this, ChatHeadService.class.getName())) {
                    startService(new Intent(this, ChatHeadService.class));
                    return;
                }
                return;
            case 22100:
                if (itemEdit != null) {
                    DialogHelper.editItemDialog("Edit Item", itemEdit, this, new onItemEditListener() {
                        public void itemLabel(String label) {
                            Home.itemEdit.setLabel(label);
                            Home.db.saveItem(Home.itemEdit);
                            Home.launcher.desktop.addItemToCell(Home.itemEdit, Home.itemEdit.getX(), Home.itemEdit.getY());
                            Home.launcher.desktop.removeItem(Home.launcher.desktop.getCurrentPage().coordinateToChildView(new Point(Home.itemEdit.getX(), Home.itemEdit.getY())));
                        }
                    });
                    return;
                }
                return;
            default:
                return;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!(ChatHeadService.chatHeadService == null || ChatHeadService.chatHeadService.getViewControlCenter() == null || (keyCode != 25 && keyCode != 24))) {
            ChatHeadService.chatHeadService.getViewControlCenter().changeVolume();
        }
        return super.onKeyDown(keyCode, event);
    }
}
