package com.benny.openlauncher.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.DialogListener.OnEditDialogListener;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.model.Item.Type;
import com.benny.openlauncher.core.util.DragAction;
import com.benny.openlauncher.core.util.DragAction.Action;
import com.benny.openlauncher.core.util.DragDropHandler;
import com.benny.openlauncher.core.util.Tool;

public class DragOptionView extends CardView {
    private Long animSpeed = Long.valueOf(120);
    private TextView deleteIcon;
    private LinearLayout dragOptions;
    public boolean dragging = false;
    private TextView editIcon;
    private View[] hideViews;
    private Home home;
    private TextView infoIcon;
    public boolean isDraggedFromDrawer = false;
    private TextView removeIcon;

    public DragOptionView(Context context) {
        super(context);
        init();
    }

    public DragOptionView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public void setAutoHideView(View... v) {
        this.hideViews = v;
    }

    public void resetAutoHideView() {
        this.hideViews = null;
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (VERSION.SDK_INT >= 20) {
            ((MarginLayoutParams) getLayoutParams()).topMargin = insets.getSystemWindowInsetTop() + Tool.dp2px(14, getContext());
        }
        return insets;
    }

    @SuppressLint("WrongConstant")
    private void init() {
        setCardElevation((float) Tool.dp2px(4, getContext()));
        setRadius((float) Tool.dp2px(2, getContext()));
        this.dragOptions = (LinearLayout) ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.view_drag_option, this, false);
        addView(this.dragOptions);
        this.editIcon = (TextView) this.dragOptions.findViewById(R.id.editIcon);
        this.editIcon.setOnDragListener(new OnDragListener() {
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case 1:
                        if (((DragAction) dragEvent.getLocalState()).action == Action.APP_DRAWER) {
                            return false;
                        }
                        return true;
                    case 3:
                        final Item item = (Item) DragDropHandler.getDraggedObject(dragEvent);
                        Setup.eventHandler().showEditDialog(DragOptionView.this.getContext(), item, new OnEditDialogListener() {
                            public void onRename(String name) {
                                item.setLabel(name);
                                Home.db.saveItem(item);
                                Home.launcher.desktop.addItemToCell(item, item.getX(), item.getY());
                                Home.launcher.desktop.removeItem(Home.launcher.desktop.getCurrentPage().coordinateToChildView(new Point(item.getX(), item.getY())));
                            }
                        });
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
        });
        this.removeIcon = (TextView) this.dragOptions.findViewById(R.id.removeIcon);
        this.removeIcon.setOnDragListener(new OnDragListener() {
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case 1:
                        switch (((DragAction) dragEvent.getLocalState()).action) {
                            case GROUP:
                            case APP:
                            case WIDGET:
                            case SHORTCUT:
                            case APP_DRAWER:
                            case ACTION:
                                return true;
                        }
                        break;
                    case 3:
                        Item item = (Item) DragDropHandler.getDraggedObject(dragEvent);
                        Home home = Home.launcher;
                        Home.db.deleteItem(item, true);
                        DragOptionView.this.home.desktop.consumeRevert();
                        DragOptionView.this.home.dock.consumeRevert();
                        return true;
                    case 4:
                        return true;
                    case 5:
                        break;
                    case 6:
                        return true;
                    default:
                        return false;
                }
                return true;
            }
        });
        this.infoIcon = (TextView) this.dragOptions.findViewById(R.id.infoIcon);
        this.infoIcon.setOnDragListener(new OnDragListener() {
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case 1:
                        switch (((DragAction) dragEvent.getLocalState()).action) {
                            case APP:
                            case APP_DRAWER:
                                return true;
                        }
                        break;
                    case 3:
                        Item item = (Item) DragDropHandler.getDraggedObject(dragEvent);
                        if (item.getType() == Type.APP) {
                            try {
                                DragOptionView.this.getContext().startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + item.getIntent().getComponent().getPackageName())));
                            } catch (Exception e) {
                                Tool.toast(DragOptionView.this.getContext(), (int) R.string.toast_app_uninstalled);
                            }
                        }
                        return true;
                    case 4:
                        return true;
                    case 5:
                        break;
                    case 6:
                        return true;
                    default:
                        return false;
                }
                return true;
            }
        });
        this.deleteIcon = (TextView) this.dragOptions.findViewById(R.id.deleteIcon);
        this.deleteIcon.setOnDragListener(new OnDragListener() {
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case 1:
                        switch (((DragAction) dragEvent.getLocalState()).action) {
                            case APP:
                            case APP_DRAWER:
                                return true;
                        }
                        break;
                    case 3:
                        Setup.eventHandler().showDeletePackageDialog(DragOptionView.this.getContext(), dragEvent);
                        return true;
                    case 4:
                        return true;
                    case 5:
                        break;
                    case 6:
                        return true;
                    default:
                        return false;
                }
                return true;
            }
        });
        this.editIcon.setText(this.editIcon.getText(), BufferType.SPANNABLE);
        this.removeIcon.setText(this.removeIcon.getText(), BufferType.SPANNABLE);
        this.infoIcon.setText(this.infoIcon.getText(), BufferType.SPANNABLE);
        this.deleteIcon.setText(this.deleteIcon.getText(), BufferType.SPANNABLE);
        for (int i = 0; i < this.dragOptions.getChildCount(); i++) {
            this.dragOptions.getChildAt(i).setVisibility(8);
        }
    }

    public boolean dispatchDragEvent(DragEvent ev) {
        final DragEvent event = ev;
        boolean r = super.dispatchDragEvent(ev);
        if (r && (ev.getAction() == 1 || ev.getAction() == 4)) {
            post(new Runnable() {
                public void run() {
                    DragOptionView.this.onDragEvent(event);
                }
            });
            try {
                super.dispatchDragEvent(ev);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return r;
    }

    private void animShowView() {
        if (this.hideViews != null) {
            this.isDraggedFromDrawer = true;
            if (Setup.get().getAppSettings().getSearchBarEnable()) {
                Tool.invisibleViews((long) Math.round(((float) this.animSpeed.longValue()) / 1.3f), this.hideViews);
            }
            animate().alpha(Tool.DEFAULT_BACKOFF_MULT);
        }
    }

    @SuppressLint("WrongConstant")
    public boolean onDragEvent(DragEvent event) {
        switch (event.getAction()) {
            case 1:
                this.dragging = true;
                animShowView();
                this.home.dock.setHideGrid(false);
                for (CellContainer cellContainer : this.home.desktop.pages) {
                    cellContainer.setHideGrid(false);
                }
                try {
                    switch (((DragAction) event.getLocalState()).action) {
                        case GROUP:
                            this.editIcon.setVisibility(8);
                            return true;
                        case APP:
                            this.editIcon.setVisibility(8);
                            this.infoIcon.setVisibility(0);
                            this.deleteIcon.setVisibility(0);
                            break;
                        case WIDGET:
                            this.removeIcon.setVisibility(0);
                            return true;
                        case SHORTCUT:
                            return true;
                        case ACTION:
                            this.editIcon.setVisibility(8);
                            return true;
                    }
                } catch (Exception e) {
                }
                return true;
            case 3:
                return true;
            case 4:
                this.dragging = false;
                this.home.dock.setHideGrid(true);
                for (CellContainer cellContainer2 : this.home.desktop.pages) {
                    cellContainer2.setHideGrid(true);
                }
                animate().alpha(0.0f);
                this.editIcon.setVisibility(8);
                this.removeIcon.setVisibility(8);
                this.infoIcon.setVisibility(8);
                this.deleteIcon.setVisibility(8);
                if (Setup.get().getAppSettings().getSearchBarEnable()) {
                    Tool.visibleViews((long) Math.round(((float) this.animSpeed.longValue()) / 1.3f), this.hideViews);
                }
                this.isDraggedFromDrawer = false;
                this.home.dock.revertLastItem();
                this.home.desktop.revertLastItem();
                return true;
            case 5:
                return true;
            case 6:
                return true;
            default:
                return false;
        }
    }
}
