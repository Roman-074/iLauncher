package com.benny.openlauncher.core.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import com.benny.openlauncher.R;
import com.benny.openlauncher.core.interfaces.FastItem.DesktopOptionsItem;
import com.benny.openlauncher.core.interfaces.SettingsManager;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.IconLabelItem;
import com.benny.openlauncher.core.util.Tool;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import java.util.ArrayList;
import java.util.List;

public class DesktopOptionView extends FrameLayout {
    private FastItemAdapter<DesktopOptionsItem>[] actionAdapters;
    private RecyclerView[] actionRecyclerViews;
    private DesktopOptionViewListener desktopOptionViewListener;

    public interface DesktopOptionViewListener {
        void onLaunchSettings();

        void onPickDesktopAction();

        void onPickWidget();

        void onRemovePage();

        void onSetPageAsHome();
    }

    public DesktopOptionView(@NonNull Context context) {
        super(context);
        this.actionRecyclerViews = new RecyclerView[2];
        this.actionAdapters = new FastItemAdapter[2];
        init();
    }

    public DesktopOptionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.actionRecyclerViews = new RecyclerView[2];
        this.actionAdapters = new FastItemAdapter[2];
        init();
    }

    public DesktopOptionView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.actionRecyclerViews = new RecyclerView[2];
        this.actionAdapters = new FastItemAdapter[2];
        init();
    }

    public void setDesktopOptionViewListener(DesktopOptionViewListener desktopOptionViewListener) {
        this.desktopOptionViewListener = desktopOptionViewListener;
    }

    public void updateHomeIcon(final boolean home) {
        post(new Runnable() {
            public void run() {
                if (DesktopOptionView.this.actionAdapters.length >= 1 && DesktopOptionView.this.actionAdapters[0].getAdapterItems().size() >= 2) {
                    if (home) {
                        ((DesktopOptionsItem) DesktopOptionView.this.actionAdapters[0].getAdapterItem(1)).setIcon(R.drawable.ic_star_white_36dp);
                    } else {
                        ((DesktopOptionsItem) DesktopOptionView.this.actionAdapters[0].getAdapterItem(1)).setIcon(R.drawable.ic_star_border_white_36dp);
                    }
                    DesktopOptionView.this.actionAdapters[0].notifyAdapterItemChanged(1);
                }
            }
        });
    }

    public void updateLockIcon(final boolean lock) {
        post(new Runnable() {
            public void run() {
                try {
                    if (lock) {
                        ((DesktopOptionsItem) DesktopOptionView.this.actionAdapters[0].getAdapterItem(2)).setIcon(R.drawable.ic_lock_white_36dp);
                    } else {
                        ((DesktopOptionsItem) DesktopOptionView.this.actionAdapters[0].getAdapterItem(2)).setIcon(R.drawable.ic_lock_open_white_36dp);
                    }
                    DesktopOptionView.this.actionAdapters[0].notifyAdapterItemChanged(2);
                } catch (Exception e) {
                }
            }
        });
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (VERSION.SDK_INT >= 20) {
            setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
        }
        return insets;
    }

    private void init() {
        if (!isInEditMode()) {
            final int paddingHorizontal = Tool.dp2px(42, getContext());
            final Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "RobotoCondensed-Regular.ttf");
            this.actionAdapters[0] = new FastItemAdapter();
            this.actionAdapters[1] = new FastItemAdapter();
            this.actionRecyclerViews[0] = createRecyclerView(this.actionAdapters[0], 49, paddingHorizontal);
            this.actionRecyclerViews[1] = createRecyclerView(this.actionAdapters[1], 81, paddingHorizontal);
            final com.mikepenz.fastadapter.listeners.OnClickListener<DesktopOptionsItem> clickListener = new com.mikepenz.fastadapter.listeners.OnClickListener<DesktopOptionsItem>() {
                public boolean onClick(View v, IAdapter<DesktopOptionsItem> iAdapter, DesktopOptionsItem item, int position) {
                    boolean z = false;
                    if (DesktopOptionView.this.desktopOptionViewListener == null) {
                        return false;
                    }
                    int id = (int) item.getIdentifier();
                    if (id == R.string.home) {
                        DesktopOptionView.this.updateHomeIcon(true);
                        DesktopOptionView.this.desktopOptionViewListener.onSetPageAsHome();
                    } else if (id == R.string.remove) {
                        if (Setup.appSettings().isDesktopLock()) {
                            Tool.toast(DesktopOptionView.this.getContext(), "Desktop is locked.");
                        } else {
                            DesktopOptionView.this.desktopOptionViewListener.onRemovePage();
                        }
                    } else if (id == R.string.widget) {
                        if (Setup.appSettings().isDesktopLock()) {
                            Tool.toast(DesktopOptionView.this.getContext(), "Desktop is locked.");
                        } else {
                            DesktopOptionView.this.desktopOptionViewListener.onPickWidget();
                        }
                    } else if (id == R.string.action) {
                        if (Setup.appSettings().isDesktopLock()) {
                            Tool.toast(DesktopOptionView.this.getContext(), "Desktop is locked.");
                        } else {
                            DesktopOptionView.this.desktopOptionViewListener.onPickDesktopAction();
                        }
                    } else if (id == R.string.lock) {
                        SettingsManager appSettings = Setup.appSettings();
                        if (!Setup.appSettings().isDesktopLock()) {
                            z = true;
                        }
                        appSettings.setDesktopLock(z);
                        DesktopOptionView.this.updateLockIcon(Setup.appSettings().isDesktopLock());
                    } else if (id != R.string.settings) {
                        return false;
                    } else {
                        DesktopOptionView.this.desktopOptionViewListener.onLaunchSettings();
                    }
                    return true;
                }
            };
            getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    DesktopOptionView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    DesktopOptionView.this.initItems(typeface, clickListener, (DesktopOptionView.this.getWidth() - (paddingHorizontal * 2)) / 3);
                }
            });
        }
    }

    private void initItems(Typeface typeface, com.mikepenz.fastadapter.listeners.OnClickListener<DesktopOptionsItem> clickListener, int itemWidth) {
        List<DesktopOptionsItem> itemsTop = new ArrayList();
        itemsTop.add(createItem(R.drawable.ic_delete_white_36dp, R.string.remove, typeface, itemWidth));
        itemsTop.add(createItem(R.drawable.ic_star_white_36dp, R.string.home, typeface, itemWidth));
        itemsTop.add(createItem(R.drawable.ic_lock_open_white_36dp, R.string.lock, typeface, itemWidth));
        this.actionAdapters[0].set(itemsTop);
        this.actionAdapters[0].withOnClickListener(clickListener);
        List<DesktopOptionsItem> itemsBottom = new ArrayList();
        itemsBottom.add(createItem(R.drawable.ic_dashboard_white_36dp, R.string.widget, typeface, itemWidth));
        itemsBottom.add(createItem(R.drawable.ic_photo_white_36dp, R.string.action, typeface, itemWidth));
        itemsBottom.add(createItem(R.drawable.ic_settings_launcher_white_36dp, R.string.settings, typeface, itemWidth));
        this.actionAdapters[1].set(itemsBottom);
        this.actionAdapters[1].withOnClickListener(clickListener);
        ((MarginLayoutParams) ((View) this.actionRecyclerViews[0].getParent()).getLayoutParams()).topMargin = Tool.dp2px(Setup.appSettings().getSearchBarEnable() ? 36 : 4, getContext());
    }

    private RecyclerView createRecyclerView(FastAdapter adapter, int gravity, int paddingHorizontal) {
        RecyclerView actionRecyclerView = new RecyclerView(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 0, false);
        actionRecyclerView.setClipToPadding(false);
        actionRecyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        actionRecyclerView.setLayoutManager(linearLayoutManager);
        actionRecyclerView.setAdapter(adapter);
        actionRecyclerView.setOverScrollMode(0);
        LayoutParams actionRecyclerViewLP = new LayoutParams(-1, -2);
        actionRecyclerViewLP.gravity = gravity;
        addView(actionRecyclerView, actionRecyclerViewLP);
        return actionRecyclerView;
    }

    private DesktopOptionsItem createItem(int icon, int label, Typeface typeface, int width) {
        return ((IconLabelItem) new IconLabelItem(getContext(), icon, getContext().getString(label), -1).withIdentifier((long) label)).withOnClickListener(null).withTextColor(-1).withDrawablePadding(getContext(), 0).withIconGravity(48).withGravity(17).withMatchParent(false).withWidth(width).withTypeface(typeface).withTextGravity(17);
    }
}
