package com.benny.openlauncher.core.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.PopupWindow.OnDismissListener;

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

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class GroupPopupView extends RevealFrameLayout {
    private static final Long folderAnimationTime = Long.valueOf(200);
    private CellContainer cellContainer;
    private int cx;
    private int cy;
    private OnDismissListener dismissListener;
    private Animator folderAnimator;
    private boolean isShowing;
    private CardView popupCard;

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

    public GroupPopupView(Context context) {
        super(context);
        init();
    }

    public GroupPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("WrongConstant")
    private void init() {
        if (!isInEditMode()) {
            this.popupCard = (CardView) LayoutInflater.from(getContext()).inflate(R.layout.view_group_popup, this, false);
            if (Setup.appSettings().getPopupColor() != -1) {
                int color = Setup.appSettings().getPopupColor();
                int alpha = Color.alpha(color);
                this.popupCard.setCardBackgroundColor(color);
                if (alpha != 0) {
                    this.popupCard.setCardElevation(0.0f);
                }
            }
            this.cellContainer = (CellContainer) this.popupCard.findViewById(R.id.group);
            bringToFront();
            setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (GroupPopupView.this.dismissListener != null) {
                        GroupPopupView.this.dismissListener.onDismiss();
                    }
                    GroupPopupView.this.dismissPopup();
                }
            });
            addView(this.popupCard);
            this.popupCard.setVisibility(4);
            setVisibility(4);
        }
    }

    @SuppressLint("WrongConstant")
    public boolean showWindowV(Item item, View itemView, DesktopCallBack callBack) {
        if (this.isShowing || getVisibility() == 0) {
            return false;
        }
        this.isShowing = true;
        final Context c = itemView.getContext();
        int[] cellSize = GroupDef.getCellSize(item.getGroupItems().size());
        this.cellContainer.setGridSize(cellSize[0], cellSize[1]);
        int iconSize = Tool.dp2px(Setup.appSettings().getDesktopIconSize(), c);
        int textSize = Tool.dp2px(22, c);
        int contentPadding = Tool.dp2px(5, c);
        for (int x2 = 0; x2 < cellSize[0]; x2++) {
            for (int y2 = 0; y2 < cellSize[1]; y2++) {
                if ((cellSize[0] * y2) + x2 <= item.getGroupItems().size() - 1) {
                    final Item groupItem = (Item) item.getGroupItems().get((cellSize[0] * y2) + x2);
                    final View view = AppItemView.createAppItemViewPopup(getContext(), groupItem, groupItem.getType() != Type.SHORTCUT ? Setup.appLoader().findItemApp(groupItem) : null, Setup.appSettings().getDesktopIconSize()).getView();
                    final DesktopCallBack desktopCallBack = callBack;
                    final Item item2 = item;
                    View view2 = itemView;
                    view.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View view2) {
                            if (Setup.appSettings().isDesktopLock()) {
                                return false;
                            }
                            GroupPopupView.this.removeItem(c, desktopCallBack, item2, groupItem, (AppItemView) view2);
                            view2.performHapticFeedback(0);
                            DragDropHandler.startDrag(view, groupItem, groupItem.getType() == Type.SHORTCUT ? Action.SHORTCUT : Action.APP, null);
                            GroupPopupView.this.dismissPopup();
                            GroupPopupView.this.updateItem(c, desktopCallBack, item2, groupItem, view2);
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
                                        GroupPopupView.this.dismissPopup();
                                        GroupPopupView.this.setVisibility(4);
                                        view.getContext().startActivity(groupItem.getIntent());
                                    }
                                });
                            }
                        });
                    }
                    this.cellContainer.addViewToGrid(view, x2, y2, 1, 1);
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
        int popupWidth = (((contentPadding * 8) + this.popupCard.getContentPaddingLeft()) + this.popupCard.getContentPaddingRight()) + (cellSize[0] * iconSize);
        this.popupCard.getLayoutParams().width = popupWidth;
        int popupHeight = ((((contentPadding * 2) + this.popupCard.getContentPaddingTop()) + this.popupCard.getContentPaddingBottom()) + Tool.dp2px(30, c)) + ((iconSize + textSize) * cellSize[1]);
        this.popupCard.getLayoutParams().height = popupHeight;
        this.cx = popupWidth / 2;
        this.cy = popupHeight / 2;
        int[] coordinates = new int[2];
        itemView.getLocationInWindow(coordinates);
        coordinates[0] = coordinates[0] + (itemView.getWidth() / 2);
        coordinates[1] = coordinates[1] + (itemView.getHeight() / 2);
        coordinates[0] = coordinates[0] - (popupWidth / 2);
        coordinates[1] = coordinates[1] - (popupHeight / 2);
        int width = getWidth();
        int height = getHeight();
        if (coordinates[0] + popupWidth > width) {
            coordinates[0] = coordinates[0] + (width - (coordinates[0] + popupWidth));
        }
        if (coordinates[1] + popupHeight > height) {
            coordinates[1] = coordinates[1] + (height - (coordinates[1] + popupHeight));
        }
        if (coordinates[0] < 0) {
            coordinates[0] = coordinates[0] - (itemView.getWidth() / 2);
            coordinates[0] = coordinates[0] + (popupWidth / 2);
        }
        if (coordinates[1] < 0) {
            coordinates[1] = coordinates[1] - (itemView.getHeight() / 2);
            coordinates[1] = coordinates[1] + (popupHeight / 2);
        }
        int x = coordinates[0];
        int y = coordinates[1];
        this.popupCard.setPivotX(0.0f);
        this.popupCard.setPivotX(0.0f);
        this.popupCard.setX((float) x);
        this.popupCard.setY((float) (y - 200));
        setVisibility(0);
        this.popupCard.setVisibility(0);
        animateFolderOpen(itemView);
        return true;
    }

    private void animateFolderOpen(View itemView) {
        this.folderAnimator = ViewAnimationUtils.createCircularReveal(this.popupCard, this.cx, this.cy, 0.0f, (float) Math.max(this.popupCard.getWidth(), this.popupCard.getHeight()));
        this.folderAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.folderAnimator.setDuration(folderAnimationTime.longValue());
        this.folderAnimator.start();
    }

    public void dismissPopup() {
        if (this.isShowing && this.folderAnimator != null && !this.folderAnimator.isRunning()) {
            this.folderAnimator = ViewAnimationUtils.createCircularReveal(this.popupCard, this.cx, this.cy, (float) Math.max(this.popupCard.getWidth(), this.popupCard.getHeight()), 0.0f);
            this.folderAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            this.folderAnimator.setDuration(folderAnimationTime.longValue());
            this.folderAnimator.addListener(new AnimatorListener() {
                public void onAnimationStart(Animator p1) {
                }

                @SuppressLint("WrongConstant")
                public void onAnimationEnd(Animator p1) {
                    GroupPopupView.this.popupCard.setVisibility(4);
                    GroupPopupView.this.isShowing = false;
                    if (GroupPopupView.this.dismissListener != null) {
                        GroupPopupView.this.dismissListener.onDismiss();
                    }
                    GroupPopupView.this.cellContainer.removeAllViews();
                    GroupPopupView.this.setVisibility(4);
                }

                public void onAnimationCancel(Animator p1) {
                }

                public void onAnimationRepeat(Animator p1) {
                }
            });
            this.folderAnimator.start();
        }
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
                item.setX(currentItem.getX());
                item.setY(currentItem.getY());
                Home.db.saveItem(item);
                Home.db.updateSate(item, ItemState.Visible);
                Home.db.deleteItem(currentItem, true);
                callBack.removeItem(currentView);
                callBack.addItemToCell(item, item.getX(), item.getY());
            }
            if (Home.launcher != null) {
                Home.launcher.desktop.requestLayout();
            }
        }
    }
}
