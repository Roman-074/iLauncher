package com.benny.openlauncher.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.model.Item.Type;
import com.benny.openlauncher.core.util.Definitions.ItemState;
import com.benny.openlauncher.core.util.DragAction.Action;
import com.benny.openlauncher.core.util.DragDropHandler;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.viewutil.DesktopCallBack;
import com.benny.openlauncher.core.viewutil.GroupIconDrawable;


public class GroupPopupViewNew extends FrameLayout {
    private CellContainer cellContainer;
    private OnDismissListener dismissListener;
    private boolean init = false;
    public boolean isShowing = false;
    private RelativeLayout popupParent;
    private TextView tvLabel;

    static class GroupDef {
        static int maxItem = 12;

        GroupDef() {
        }

        static int[] getCellSize(int count) {
            if (count <= 1) {
                return new int[]{1, 1};
            }
            if (count <= 2) {
                return new int[]{2, 1};
            }
            if (count <= 4) {
                return new int[]{2, 2};
            }
            if (count <= 6) {
                return new int[]{3, 2};
            }
            if (count <= 9) {
                return new int[]{3, 3};
            }
            if (count <= 12) {
                return new int[]{4, 3};
            }
            return new int[]{0, 0};
        }
    }

    public GroupPopupViewNew(Context context) {
        super(context);
        init();
    }

    public GroupPopupViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            this.popupParent = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_group_popup_new, this, false);
            this.tvLabel = (TextView) this.popupParent.findViewById(R.id.tvLabelGroup);
            this.cellContainer = (CellContainer) this.popupParent.findViewById(R.id.group);
            bringToFront();
            setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (GroupPopupViewNew.this.dismissListener != null) {
                        GroupPopupViewNew.this.dismissListener.onDismiss();
                    }
                    GroupPopupViewNew.this.dismissPopup();
                }
            });
        }
    }

    public void dismissPopup() {
        removeAllViews();
        if (this.dismissListener != null) {
            this.dismissListener.onDismiss();
        }
        this.cellContainer.removeAllViews();
        setVisibility(INVISIBLE);
    }

    @SuppressLint("WrongConstant")
    public boolean showWindowV(Item item, View itemView, DesktopCallBack callBack) {
        if (getVisibility() == 0) {
            return false;
        }
        setVisibility(0);
        this.popupParent.setVisibility(0);
        this.tvLabel.setText(item.getLabel());
        final Context c = itemView.getContext();
        int[] cellSize = GroupDef.getCellSize(item.getGroupItems().size());
        this.cellContainer.setGridSize(cellSize[0], cellSize[1]);
        int iconSize = Tool.dp2px(Setup.appSettings().getDesktopIconSize(), c);
        int textSize = Tool.dp2px(22, c);
        for (int x2 = 0; x2 < cellSize[0]; x2++) {
            for (int y2 = 0; y2 < cellSize[1]; y2++) {
                if ((cellSize[0] * y2) + x2 <= item.getGroupItems().size() - 1) {
                    final Item groupItem = (Item) item.getGroupItems().get((cellSize[0] * y2) + x2);
                    if (groupItem != null) {
                        final View view = AppItemView.createAppItemViewPopup(getContext(), groupItem, groupItem.getType() != Type.SHORTCUT ? Setup.appLoader().findItemApp(groupItem) : null, Setup.appSettings().getDesktopIconSize()).getView();
                        final DesktopCallBack desktopCallBack = callBack;
                        final Item item2 = item;
                        View view2 = itemView;
                        view.setOnLongClickListener(new OnLongClickListener() {
                            public boolean onLongClick(View view2) {
                                if (Setup.appSettings().isDesktopLock()) {
                                    return false;
                                }
                                GroupPopupViewNew.this.removeItem(c, desktopCallBack, item2, groupItem, (AppItemView) view2);
                                view2.performHapticFeedback(0);
                                DragDropHandler.startDrag(view, groupItem, groupItem.getType() == Type.SHORTCUT ? Action.SHORTCUT : Action.APP, null);
                                GroupPopupViewNew.this.dismissPopup();
                                GroupPopupViewNew.this.updateItem(c, desktopCallBack, item2, groupItem, view2);
                                return true;
                            }
                        });
                        if (Setup.appLoader().findItemApp(groupItem) == null) {
                            removeItem(c, callBack, item, groupItem, (AppItemView) itemView);
                        } else {
                            view.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    Tool.createScaleInScaleOutAnim(view, new Runnable() {
                                        public void run() {
                                            GroupPopupViewNew.this.dismissPopup();
                                            GroupPopupViewNew.this.setVisibility(4);
                                            Tool.startApp(view.getContext(), groupItem.getIntent());
                                        }
                                    });
                                }
                            });
                        }
                        this.cellContainer.addViewToGrid(view, x2, y2, 1, 1);
                    }
                }
            }
        }
        final View view3 = itemView;
        this.dismissListener = new OnDismissListener() {
            public void onDismiss() {
                if (((AppItemView) view3).getIconProvider().isGroupIconDrawable() && ((AppItemView) view3).getCurrentIcon() != null) {
                    ((GroupIconDrawable) ((AppItemView) view3).getCurrentIcon()).popBack();
                }
            }
        };
        this.cellContainer.getLayoutParams().height = ((Tool.dp2px(40, c) + iconSize) + textSize) * cellSize[1];
        addView(this.popupParent);
        return true;
    }

    private void removeItem(Context context, DesktopCallBack callBack, Item currentItem, Item dragOutItem, AppItemView currentView) {
        currentItem.getGroupItems().remove(dragOutItem);
        Home.db.updateSate(dragOutItem, ItemState.Visible);
        Home.db.saveItem(currentItem);
        currentView.setIconProvider(Setup.imageLoader().createIconProvider(new GroupIconDrawable(context, currentItem, Setup.appSettings().getDesktopIconSize())));
    }

    public void updateItem(Context context, DesktopCallBack callBack, Item currentItem, Item dragOutItem, View currentView) {
        if (currentItem.getGroupItems().size() == 1) {
            if (Setup.appLoader().findItemApp((Item) currentItem.getGroupItems().get(0)) != null) {
                Item item = Home.db.getItem(((Item) currentItem.getGroupItems().get(0)).getId().intValue());
                if (item != null) {
                    item.setX(currentItem.getX());
                    item.setY(currentItem.getY());
                    Home.db.saveItem(item);
                    Home.db.updateSate(item, ItemState.Visible);
                    Home.db.deleteItem(currentItem, true);
                    callBack.removeItem(currentView);
                    callBack.addItemToCell(item, item.getX(), item.getY());
                } else {
                    return;
                }
            }
            if (Home.launcher != null) {
                Home.launcher.desktop.requestLayout();
            }
        }
    }
}
