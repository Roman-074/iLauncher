package com.benny.openlauncher.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.AppUpdateListener;
import com.benny.openlauncher.core.interfaces.FastItem.LabelItem;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.IconLabelItem;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.viewutil.CircleDrawable;
import com.mikepenz.fastadapter.IItemAdapter.Predicate;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchBar extends FrameLayout {
    private static final long ANIM_TIME = 200;
    private FastItemAdapter<LabelItem> adapter = new FastItemAdapter();
    private CallBack callback;
    private boolean expanded;
    private Mode mode = Mode.DateAll;
    public AppCompatImageView searchButton;
    private float searchClockSubTextFactor = 0.5f;
    private int searchClockTextSize = 28;
    public AppCompatEditText searchInput;
    private boolean searchInternetEnabled = true;
    public RecyclerView searchRecycler;
    public AppCompatImageView switchButton;

    public interface CallBack {
        void onCollapse();

        void onExpand();

        void onInternetSearch(String str);
    }

    public enum Mode {
        DateAll(1, new SimpleDateFormat("MMMM dd'\n'EEEE',' yyyy", Locale.getDefault())),
        DateNoYearAndTime(2, new SimpleDateFormat("MMMM dd'\n'HH':'mm", Locale.getDefault())),
        DateAllAndTime(3, new SimpleDateFormat("MMMM dd',' yyyy'\n'HH':'mm", Locale.getDefault())),
        TimeAndDateAll(4, new SimpleDateFormat("HH':'mm'\n'MMMM dd',' yyyy", Locale.getDefault())),
        Custom(0, null);
        
        int id;
        SimpleDateFormat sdf;

        public static Mode getById(int id) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].getId() == id) {
                    return values()[i];
                }
            }
            throw new RuntimeException("ID not found!");
        }

        public static Mode getByIndex(int index) {
            return values()[index];
        }

        public static int getIndex(int id) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].getId() == id) {
                    return i;
                }
            }
            throw new RuntimeException("ID not found!");
        }

        private Mode(int id, SimpleDateFormat sdf) {
            this.id = id;
            this.sdf = sdf;
        }

        public int getId() {
            return this.id;
        }
    }

    public SearchBar(@NonNull Context context) {
        super(context);
        init();
    }

    public SearchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SearchBar setSearchInternetEnabled(boolean enabled) {
        this.searchInternetEnabled = enabled;
        return this;
    }

    public SearchBar setSearchClockTextSize(int size) {
        this.searchClockTextSize = size;
        return this;
    }

    public SearchBar setSearchClockSubTextFactor(float factor) {
        this.searchClockSubTextFactor = factor;
        return this;
    }

    public SearchBar setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public void setCallback(CallBack callback) {
        this.callback = callback;
    }

    public boolean collapse() {
        if (!this.expanded) {
            return false;
        }
        this.searchButton.callOnClick();
        if (this.expanded) {
            return false;
        }
        return true;
    }

    @SuppressLint("WrongConstant")
    private void init() {
        int dp1 = Tool.dp2px(1, getContext());
        int iconMarginOutside = dp1 * 16;
        int iconMarginTop = dp1 * 10;
        int searchTextHorizontalMargin = dp1 * 8;
        int searchTextMarginTop = dp1 * 8;
        int iconSize = dp1 * 24;
        int iconPadding = dp1 * 6;
        LayoutParams switchButtonParams = null;
        if (!isInEditMode() && Setup.appSettings().isSearchGridListSwitchEnabled()) {
            this.switchButton = new AppCompatImageView(getContext());
            updateSwitchIcon();
            this.switchButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Setup.appSettings().setSearchUseGrid(!Setup.appSettings().isSearchUseGrid());
                    SearchBar.this.updateSwitchIcon();
                    SearchBar.this.updateRecyclerViewLayoutManager();
                }
            });
            this.switchButton.setVisibility(8);
            this.switchButton.setPadding(0, iconPadding, 0, iconPadding);
            switchButtonParams = new LayoutParams(-2, -2);
            switchButtonParams.setMargins(iconMarginOutside, iconMarginTop, 0, 0);
            switchButtonParams.gravity = GravityCompat.START;
        }
        if (!isInEditMode()) {
            CircleDrawable icon = new CircleDrawable(getContext(), getResources().getDrawable(R.drawable.ic_search_light_24dp), ViewCompat.MEASURED_STATE_MASK);
            this.searchButton = new AppCompatImageView(getContext());
            this.searchButton.setImageDrawable(icon);
            this.searchButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!SearchBar.this.expanded || SearchBar.this.searchInput.getText().length() <= 0) {
                        boolean z;
                        SearchBar searchBar = SearchBar.this;
                        if (SearchBar.this.expanded) {
                            z = false;
                        } else {
                            z = true;
                        }
                        searchBar.expanded = z;
                        if (SearchBar.this.expanded) {
                            if (SearchBar.this.callback != null) {
                                SearchBar.this.callback.onExpand();
                            }
                            if (Setup.appSettings().isResetSearchBarOnOpen()) {
                                LayoutManager lm = SearchBar.this.searchRecycler.getLayoutManager();
                                if (lm instanceof LinearLayoutManager) {
                                    ((LinearLayoutManager) SearchBar.this.searchRecycler.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                                } else if (lm instanceof GridLayoutManager) {
                                    ((GridLayoutManager) SearchBar.this.searchRecycler.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                                }
                            }
                            SearchBar.this.searchButton.setImageDrawable(new CircleDrawable(SearchBar.this.getContext(), SearchBar.this.getResources().getDrawable(R.drawable.ic_clear_white_24dp), ViewCompat.MEASURED_STATE_MASK));
                            Tool.visibleViews(SearchBar.ANIM_TIME, SearchBar.this.searchInput, SearchBar.this.searchRecycler, SearchBar.this.switchButton);
                            return;
                        }
                        if (SearchBar.this.callback != null) {
                            SearchBar.this.callback.onCollapse();
                        }
                        SearchBar.this.searchButton.setImageDrawable(new CircleDrawable(SearchBar.this.getContext(), SearchBar.this.getResources().getDrawable(R.drawable.ic_search_light_24dp), ViewCompat.MEASURED_STATE_MASK));
                        Tool.goneViews(SearchBar.ANIM_TIME, SearchBar.this.searchInput, SearchBar.this.searchRecycler, SearchBar.this.switchButton);
                        SearchBar.this.searchInput.getText().clear();
                        return;
                    }
                    SearchBar.this.searchInput.getText().clear();
                }
            });
            LayoutParams buttonParams = new LayoutParams(-2, -2);
            buttonParams.setMargins(0, iconMarginTop, iconMarginOutside, 0);
            buttonParams.gravity = GravityCompat.END;
            this.searchInput = new AppCompatEditText(getContext());
            this.searchInput.setVisibility(8);
            this.searchInput.setBackground(null);
            this.searchInput.setHint(R.string.search_hint);
            this.searchInput.setHintTextColor(-1);
            this.searchInput.setTextColor(-1);
            this.searchInput.setSingleLine();
            this.searchInput.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    SearchBar.this.adapter.filter(s);
                }

                public void afterTextChanged(Editable s) {
                }
            });
            LayoutParams inputParams = new LayoutParams(-1, -2);
            inputParams.setMargins((this.switchButton != null ? iconMarginOutside + iconSize : 0) + searchTextHorizontalMargin, searchTextMarginTop, (iconMarginOutside + iconSize) + searchTextHorizontalMargin, 0);
            initRecyclerView();
            Setup.appLoader().addUpdateListener(new AppUpdateListener<App>() {
                public boolean onAppUpdated(List<App> apps) {
                    SearchBar.this.adapter.clear();
                    List<LabelItem> items = new ArrayList();
                    if (SearchBar.this.searchInternetEnabled) {
                        items.add(new IconLabelItem(SearchBar.this.getContext(), R.string.search_online).withIconGravity(GravityCompat.START).withOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                SearchBar.this.callback.onInternetSearch(SearchBar.this.searchInput.getText().toString());
                                SearchBar.this.searchInput.getText().clear();
                            }
                        }).withTextColor(-1).withDrawablePadding(SearchBar.this.getContext(), 8).withBold(true).withMatchParent(true).withTextGravity(GravityCompat.END));
                    }
                    for (int i = 0; i < apps.size(); i++) {
                        final App app = (App) apps.get(i);
                        IconLabelItem iconLabelItem = new IconLabelItem(SearchBar.this.getContext(), app.getIconProvider(), app.getLabel(), 36);
                        int i2 = (Setup.appSettings().getSearchGridSize() <= 1 || Setup.appSettings().getSearchLabelLines() != 0) ? GravityCompat.START : 48;
                        items.add(iconLabelItem.withIconGravity(i2).withOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                SearchBar.this.startApp(v.getContext(), app);
                            }
                        }).withTextColor(-1).withMatchParent(true).withDrawablePadding(SearchBar.this.getContext(), 8).withMaxTextLines(Setup.appSettings().getSearchLabelLines()));
                    }
                    SearchBar.this.adapter.set(items);
                    return false;
                }
            });
            this.adapter.getItemFilter().withFilterPredicate(new Predicate<LabelItem>() {
                public boolean filter(LabelItem item, CharSequence constraint) {
                    if (item.getLabel().equals(SearchBar.this.getContext().getString(R.string.search_online))) {
                        return false;
                    }
                    String s = constraint.toString();
                    if (s.isEmpty()) {
                        return true;
                    }
                    if (item.getLabel().toLowerCase().contains(s.toLowerCase())) {
                        return false;
                    }
                    return true;
                }
            });
            final LayoutParams recyclerParams = new LayoutParams(-1, -2);
            addView(this.searchRecycler, recyclerParams);
            addView(this.searchInput, inputParams);
            addView(this.searchButton, buttonParams);
            if (this.switchButton != null) {
                addView(this.switchButton, switchButtonParams);
            }
            this.searchInput.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    SearchBar.this.searchInput.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int marginTop = Tool.dp2px(50, SearchBar.this.getContext()) + SearchBar.this.searchInput.getHeight();
                    int marginBottom = Desktop.bottomInset;
                    recyclerParams.setMargins(0, marginTop, 0, marginBottom);
                    recyclerParams.height = (((View) SearchBar.this.getParent()).getHeight() - marginTop) - marginBottom;
                    SearchBar.this.searchRecycler.setLayoutParams(recyclerParams);
                    SearchBar.this.searchRecycler.setPadding(0, 0, 0, marginBottom * 2);
                }
            });
        }
    }

    private void updateSwitchIcon() {
        this.switchButton.setImageResource(Setup.appSettings().isSearchUseGrid() ? R.drawable.ic_apps_white_24dp : R.drawable.ic_view_list_white_24dp);
    }

    private void updateRecyclerViewLayoutManager() {
        int gridSize;
        if (Setup.appSettings().isSearchUseGrid()) {
            gridSize = Setup.appSettings().getSearchGridSize();
        } else {
            gridSize = 1;
        }
        if (gridSize == 1) {
            this.searchRecycler.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        } else {
            this.searchRecycler.setLayoutManager(new GridLayoutManager(getContext(), gridSize, 1, false));
        }
        this.searchRecycler.getLayoutManager().setAutoMeasureEnabled(false);
    }

    protected void initRecyclerView() {
        this.searchRecycler = new RecyclerView(getContext());
        this.searchRecycler.setVisibility(GONE);
        this.searchRecycler.setAdapter(this.adapter);
        this.searchRecycler.setClipToPadding(false);
        this.searchRecycler.setHasFixedSize(true);
        updateRecyclerViewLayoutManager();
    }

    protected void startApp(Context context, App app) {
        Tool.startApp(context, app);
    }

    public void updateClock() {
        if (Setup.appSettings().isSearchBarTimeEnabled()) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            SimpleDateFormat sdf = this.mode.sdf;
            if (sdf == null) {
                sdf = Setup.appSettings().getUserDateFormat();
            }
            sdf.format(calendar.getTime()).split("\n");
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (VERSION.SDK_INT >= 20) {
            setPadding(getPaddingLeft(), Tool.dp2px(10, getContext()) + insets.getSystemWindowInsetTop(), getPaddingRight(), getPaddingBottom());
        }
        return insets;
    }

    public void onClickIvSearch() {
        if (!this.expanded || this.searchInput.getText().length() <= 0) {
            boolean z;
            if (this.expanded) {
                z = false;
            } else {
                z = true;
            }
            this.expanded = z;
            if (this.expanded) {
                if (this.callback != null) {
                    this.callback.onExpand();
                }
                if (Setup.appSettings().isResetSearchBarOnOpen()) {
                    LayoutManager lm = this.searchRecycler.getLayoutManager();
                    if (lm instanceof LinearLayoutManager) {
                        ((LinearLayoutManager) this.searchRecycler.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                    } else if (lm instanceof GridLayoutManager) {
                        ((GridLayoutManager) this.searchRecycler.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                    }
                }
                this.searchButton.setImageDrawable(new CircleDrawable(getContext(), getResources().getDrawable(R.drawable.ic_clear_white_24dp), ViewCompat.MEASURED_STATE_MASK));
                Tool.visibleViews(ANIM_TIME, this.searchInput, this.searchRecycler, this.switchButton);
                return;
            }
            if (this.callback != null) {
                this.callback.onCollapse();
            }
            this.searchButton.setImageDrawable(new CircleDrawable(getContext(), getResources().getDrawable(R.drawable.ic_search_light_24dp), ViewCompat.MEASURED_STATE_MASK));
            Tool.goneViews(ANIM_TIME, this.searchInput, this.searchRecycler, this.switchButton);
            this.searchInput.getText().clear();
            return;
        }
        this.searchInput.getText().clear();
    }
}
