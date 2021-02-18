package com.benny.openlauncher.core.viewutil;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.DragAction.Action;
import com.benny.openlauncher.core.util.DragDropHandler;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.widget.AppItemView.Builder;
import com.benny.openlauncher.core.widget.AppItemView.LongPressCallBack;
import com.benny.openlauncher.core.widget.CellContainer.LayoutParams;
import com.benny.openlauncher.core.widget.WidgetView;


public class ItemViewFactory {
    public static final int NO_FLAGS = 1;
    public static final int NO_LABEL = 2;
    private static Item item3;

    public static View getItemView(Context context, Item item, boolean showLabels, DesktopCallBack callBack, int iconSize, int textColor) {
        return getItemView(context, callBack, item, iconSize, showLabels ? 1 : 2, textColor);
    }

    private static View getItemView(Context context, DesktopCallBack callBack, Item item, int iconSize, int flags, int textColor) {
        View view = null;
        final DesktopCallBack desktopCallBack;
        final Item item2;
        Builder withOnLongClick;
        boolean z;
        switch (item.type) {
            case APP:
                App app = Setup.appLoader().findItemApp(item);
                if (app != null) {
                    desktopCallBack = callBack;
                    item2 = item;
                    withOnLongClick = new Builder(context, iconSize).setAppItem(item, app).withOnTouchGetPosition(item, Setup.itemGestureCallback()).vibrateWhenLongPress().withOnLongClick(item, Action.APP, (LongPressCallBack) new LongPressCallBack() {
                        public boolean readyForDrag(View view) {
                            return true;
                        }

                        public void afterDrag(View view) {
                            desktopCallBack.setLastItem(item2, view);
                        }
                    });
                    if ((flags & 2) != 2) {
                        z = true;
                    } else {
                        z = false;
                    }
                    view = withOnLongClick.setLabelVisibility(z).setTextColor(textColor).getView();
                    break;
                }
                break;
            case SHORTCUT:
                desktopCallBack = callBack;
                item2 = item;
                withOnLongClick = new Builder(context, iconSize).setShortcutItem(item).withOnTouchGetPosition(item, Setup.itemGestureCallback()).vibrateWhenLongPress().withOnLongClick(item, Action.SHORTCUT, (LongPressCallBack) new LongPressCallBack() {
                    public boolean readyForDrag(View view) {
                        return true;
                    }

                    public void afterDrag(View view) {
                        desktopCallBack.setLastItem(item2, view);
                    }
                });
                if ((flags & 2) != 2) {
                    z = true;
                } else {
                    z = false;
                }
                view = withOnLongClick.setLabelVisibility(z).setTextColor(textColor).getView();
                break;
            case GROUP:
                desktopCallBack = callBack;
                item2 = item;
                withOnLongClick = new Builder(context, iconSize).setGroupItem(context, callBack, item, iconSize).withOnTouchGetPosition(item, Setup.itemGestureCallback()).vibrateWhenLongPress().withOnLongClick(item, Action.GROUP, (LongPressCallBack) new LongPressCallBack() {
                    public boolean readyForDrag(View view) {
                        return true;
                    }

                    public void afterDrag(View view) {
                        desktopCallBack.setLastItem(item2, view);
                    }
                });
                if ((flags & 2) != 2) {
                    z = true;
                } else {
                    z = false;
                }
                view = withOnLongClick.setLabelVisibility(z).setTextColor(textColor).getView();
                view.setLayerType(1, null);
                break;
            case ACTION:
                desktopCallBack = callBack;
                item2 = item;
                withOnLongClick = new Builder(context, iconSize).setActionItem(item).withOnTouchGetPosition(item, Setup.itemGestureCallback()).vibrateWhenLongPress().withOnLongClick(item, Action.ACTION, (LongPressCallBack) new LongPressCallBack() {
                    public boolean readyForDrag(View view) {
                        return true;
                    }

                    public void afterDrag(View view) {
                        desktopCallBack.setLastItem(item2, view);
                    }
                });
                if ((flags & 2) != 2) {
                    z = true;
                } else {
                    z = false;
                }
                view = withOnLongClick.setLabelVisibility(z).setTextColor(textColor).getView();
                break;
            case WIDGET:
                AppWidgetProviderInfo appWidgetInfo = Home.appWidgetManager.getAppWidgetInfo(item.widgetValue);
                WidgetView widgetView = (WidgetView) Home.appWidgetHost.createView(context, item.widgetValue, appWidgetInfo);
                widgetView.setAppWidget(item.widgetValue, appWidgetInfo);
                item3 = item;
                widgetView.post(new Runnable() {
                    public void run() {
                        ItemViewFactory.updateWidgetOption(ItemViewFactory.item3);
                    }
                });
                FrameLayout widgetContainer = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.view_widget_container, null);
                widgetContainer.addView(widgetView);
                final View ve = widgetContainer.findViewById(R.id.vertexpand);
                ve.bringToFront();
                final View he = widgetContainer.findViewById(R.id.horiexpand);
                he.bringToFront();
                final View vl = widgetContainer.findViewById(R.id.vertless);
                vl.bringToFront();
                final View hl = widgetContainer.findViewById(R.id.horiless);
                hl.bringToFront();
                ve.animate().scaleY(Tool.DEFAULT_BACKOFF_MULT).scaleX(Tool.DEFAULT_BACKOFF_MULT);
                he.animate().scaleY(Tool.DEFAULT_BACKOFF_MULT).scaleX(Tool.DEFAULT_BACKOFF_MULT);
                vl.animate().scaleY(Tool.DEFAULT_BACKOFF_MULT).scaleX(Tool.DEFAULT_BACKOFF_MULT);
                hl.animate().scaleY(Tool.DEFAULT_BACKOFF_MULT).scaleX(Tool.DEFAULT_BACKOFF_MULT);
                final Runnable action = new Runnable() {
                    public void run() {
                        ve.animate().scaleY(0.0f).scaleX(0.0f);
                        he.animate().scaleY(0.0f).scaleX(0.0f);
                        vl.animate().scaleY(0.0f).scaleX(0.0f);
                        hl.animate().scaleY(0.0f).scaleX(0.0f);
                    }
                };
                widgetContainer.postDelayed(action, 2000);
                widgetView.setOnTouchListener(Tool.getItemOnTouchListener(item, Setup.itemGestureCallback()));
                item3 = item;
                desktopCallBack = callBack;
                final FrameLayout frameLayout = widgetContainer;
                widgetView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        if (Setup.appSettings().isDesktopLock()) {
                            return false;
                        }
                        view.performHapticFeedback(0);
                        DragDropHandler.startDrag(view, ItemViewFactory.item3, Action.WIDGET, null);
                        desktopCallBack.setLastItem(ItemViewFactory.item3, frameLayout);
                        return true;
                    }
                });
                item3 = item;

                ve.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (view.getScaleX() >= Tool.DEFAULT_BACKOFF_MULT) {
                            Item item = ItemViewFactory.item3;
                            item.spanY++;
                            ItemViewFactory.scaleWidget(frameLayout, ItemViewFactory.item3);
                            frameLayout.removeCallbacks(action);
                            frameLayout.postDelayed(action, 2000);
                        }
                    }
                });
                item3 = item;
                he.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (view.getScaleX() >= Tool.DEFAULT_BACKOFF_MULT) {
                            Item item = ItemViewFactory.item3;
                            item.spanX++;
                            ItemViewFactory.scaleWidget(frameLayout, ItemViewFactory.item3);
                            frameLayout.removeCallbacks(action);
                            frameLayout.postDelayed(action, 2000);
                        }
                    }
                });
                item3 = item;
                vl.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (view.getScaleX() >= Tool.DEFAULT_BACKOFF_MULT) {
                            Item item = ItemViewFactory.item3;
                            item.spanY--;
                            ItemViewFactory.scaleWidget(frameLayout, ItemViewFactory.item3);
                            frameLayout.removeCallbacks(action);
                            frameLayout.postDelayed(action, 2000);
                        }
                    }
                });
                item3 = item;
                hl.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (view.getScaleX() >= Tool.DEFAULT_BACKOFF_MULT) {
                            Item item = ItemViewFactory.item3;
                            item.spanX--;
                            ItemViewFactory.scaleWidget(frameLayout, ItemViewFactory.item3);
                            frameLayout.removeCallbacks(action);
                            frameLayout.postDelayed(action, 2000);
                        }
                    }
                });
                view = widgetContainer;
                break;
        }
        if (view != null) {
            view.setTag(item);
        }
        return view;
    }

    private static void scaleWidget(View view, Item item) {
        item.spanX = Math.min(item.spanX, Home.launcher.desktop.getCurrentPage().cellSpanH);
        item.spanX = Math.max(item.spanX, 1);
        item.spanY = Math.min(item.spanY, Home.launcher.desktop.getCurrentPage().cellSpanV);
        item.spanY = Math.max(item.spanY, 1);
        Home.launcher.desktop.getCurrentPage().setOccupied(false, (LayoutParams) view.getLayoutParams());
        if (Home.launcher.desktop.getCurrentPage().checkOccupied(new Point(item.x, item.y), item.spanX, item.spanY)) {
            Toast.makeText(Home.launcher.desktop.getContext(), R.string.toast_not_enough_space, Toast.LENGTH_SHORT).show();
            Home.launcher.desktop.getCurrentPage().setOccupied(true, (LayoutParams) view.getLayoutParams());
            return;
        }
        LayoutParams newWidgetLayoutParams = new LayoutParams(-2, -2, item.x, item.y, item.spanX, item.spanY);
        Home.launcher.desktop.getCurrentPage().setOccupied(true, newWidgetLayoutParams);
        view.setLayoutParams(newWidgetLayoutParams);
        updateWidgetOption(item);
        Home.db.saveItem(item);
    }

    private static void updateWidgetOption(Item item) {
        Bundle newOps = new Bundle();
        newOps.putInt("appWidgetMinWidth", item.spanX * Home.launcher.desktop.getCurrentPage().cellWidth);
        newOps.putInt("appWidgetMaxWidth", item.spanX * Home.launcher.desktop.getCurrentPage().cellWidth);
        newOps.putInt("appWidgetMinHeight", item.spanY * Home.launcher.desktop.getCurrentPage().cellHeight);
        newOps.putInt("appWidgetMaxHeight", item.spanY * Home.launcher.desktop.getCurrentPage().cellHeight);
        Home.appWidgetManager.updateAppWidgetOptions(item.widgetValue, newOps);
    }
}
