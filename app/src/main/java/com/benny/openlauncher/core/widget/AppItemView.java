package com.benny.openlauncher.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.MainApplication;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.IconDrawer;
import com.benny.openlauncher.core.interfaces.IconProvider;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.AppNotifyItem;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.model.Item.Type;
import com.benny.openlauncher.core.util.BaseIconProvider;
import com.benny.openlauncher.core.util.DragAction.Action;
import com.benny.openlauncher.core.util.DragDropHandler;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.core.viewutil.DesktopCallBack;
import com.benny.openlauncher.core.viewutil.GroupIconDrawable;
import com.benny.openlauncher.core.viewutil.ItemGestureListener.ItemGestureCallback;

import java.util.HashMap;

public class AppItemView extends View implements Callback, IconDrawer {
    public static float cx_Notify = 0.0f;
    public static float cy_Notify = 0.0f;
    public static float partPadding = 8.0f;
    private App app;
    private final int count_process_pixel;
    private boolean fastAdapterItem;
    private float heightPadding;
    private Drawable icon;
    private BaseIconProvider iconProvider;
    private float iconSize;
    private Item itemGroup;
    private String label;
    private float labelHeight;
    private boolean showLabel;
    private int targetedHeightPadding;
    private int targetedWidth;
    private Rect textContainer;
    private Paint textPaint;
    private Typeface typeface;
    private Typeface typefaceNumber;
    private boolean vibrateWhenLongPress;

    public interface LongPressCallBack {
        void afterDrag(View view);

        boolean readyForDrag(View view);
    }

    public static class Builder {
        AppItemView view;

        public Builder(Context context, int iconSize) {
            this.view = new AppItemView(context);
            this.view.setIconSize((float) Tool.dp2px(iconSize, this.view.getContext()));
        }

        public Builder(AppItemView view, int iconSize) {
            this.view = view;
            view.setIconSize((float) Tool.dp2px(iconSize, view.getContext()));
        }

        public AppItemView getView() {
            return this.view;
        }

        public Builder setAppItem(final App app) {
            this.view.setLabel(app.getLabel());
            this.view.setApp(app);
            this.view.setIconProvider(app.getIconProvider());
            this.view.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Tool.createScaleInScaleOutAnim(Builder.this.view, new Runnable() {
                        public void run() {
                            Tool.startApp(Builder.this.view.getContext(), app);
                        }
                    });
                }
            });
            return this;
        }

        public Builder setAppItem(final Item item, App app) {
            this.view.setLabel(item.getLabel());
            this.view.setApp(app);
            try {
                if (Setup.dataManager().getItem(item.getId().intValue()) != null) {
                    if (item.getIconProvider().getDrawableSynchronously(-1) != null) {
                        this.view.setIconProvider(item.getIconProvider());
                    } else {
                        this.view.setIconProvider(app.getIconProvider());
                    }
                    this.view.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Tool.createScaleInScaleOutAnim(Builder.this.view, new Runnable() {
                                public void run() {

                                    Tool.startApp(Builder.this.view.getContext(), item.intent);
                                }
                            });
                        }
                    });
                } else {
                    this.view.setIconProvider(app.getIconProvider());
                    this.view.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Tool.createScaleInScaleOutAnim(Builder.this.view, new Runnable() {
                                public void run() {
                                    android.util.Log.e("tuan","click2");

                                    CellContainer.LayoutParams lp = (CellContainer.LayoutParams) view.getLayoutParams();

                                    Tool.startApp(Builder.this.view.getContext(), item.intent);
                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {
                this.view.setIconProvider(app.getIconProvider());
            }
            return this;
        }

        public Builder setShortcutItem(final Item item) {
            this.view.setLabel(item.getLabel());
            this.view.setIconProvider(item.getIconProvider());
            this.view.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Tool.createScaleInScaleOutAnim(Builder.this.view, new Runnable() {
                        public void run() {
                            Builder.this.view.getContext().startActivity(item.intent);
                        }
                    });
                }
            });
            return this;
        }

        public Builder setGroupItem(Context context, final DesktopCallBack callback, final Item item, int iconSize) {
            this.view.setItemGroup(item);
            this.view.setLabel(item.getLabel());
            this.view.setIconProvider(Setup.imageLoader().createIconProvider(new GroupIconDrawable(context, item, iconSize)));
            this.view.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Log.d("onClick Group");
                    if (Home.launcher != null && Home.launcher.groupPopup.showWindowV(item, v, callback)) {
                        ((GroupIconDrawable) ((AppItemView) v).getCurrentIcon()).popUp();
                    }
                }
            });
            return this;
        }

        public Builder setActionItem(Item item) {
            this.view.setLabel(item.getLabel());
            this.view.setIconProvider(Setup.imageLoader().createIconProvider((int) R.drawable.ic_app_drawer_24dp));
            return this;
        }

        public Builder withOnLongClick(App app, Action action, @Nullable LongPressCallBack eventAction) {
            withOnLongClick(Item.newAppItem(app), action, eventAction);
            return this;
        }

        public Builder withOnLongClick(final Item item, final Action action, @Nullable final LongPressCallBack eventAction) {
            this.view.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    if (Setup.appSettings().isDesktopLock()) {
                        return false;
                    }
                    if (eventAction != null && !eventAction.readyForDrag(v)) {
                        return false;
                    }
                    if (Builder.this.view.vibrateWhenLongPress) {
                        v.performHapticFeedback(0);
                    }
                    DragDropHandler.startDrag(v, item, action, eventAction);
                    return true;
                }
            });
            return this;
        }

        public Builder withOnTouchGetPosition(Item item, ItemGestureCallback itemGestureCallback) {
            this.view.setOnTouchListener(Tool.getItemOnTouchListener(item, itemGestureCallback));
            return this;
        }

        public Builder setTextColor(@ColorInt int color) {
            this.view.textPaint.setColor(color);
            return this;
        }

        public Builder setLabelVisibility(boolean visible) {
            this.view.showLabel = !visible;
            return this;
        }

        public Builder vibrateWhenLongPress() {
            this.view.vibrateWhenLongPress = true;
            return this;
        }

        public Builder setFastAdapterItem() {
            this.view.fastAdapterItem = true;
            return this;
        }
    }

    public static AppItemView createAppItemViewPopup(Context context, Item groupItem, App item, int iconSize) {
        Builder b = new Builder(context, iconSize).withOnTouchGetPosition(groupItem, Setup.itemGestureCallback()).setTextColor(Setup.appSettings().getPopupLabelColor());
        if (groupItem.type == Type.SHORTCUT) {
            b.setShortcutItem(groupItem);
        } else {
            App app = Setup.appLoader().findItemApp(groupItem);
            if (app != null) {
                b.setAppItem(groupItem, app);
            }
        }
        return b.getView();
    }

    public static View createDrawerAppItemView(Context context, Home home, App app, int iconSize, LongPressCallBack longPressCallBack) {
        return new Builder(context, iconSize).setAppItem(app).withOnTouchGetPosition(null, null).withOnLongClick(app, Action.APP_DRAWER, longPressCallBack).setLabelVisibility(Setup.appSettings().isDrawerShowLabel()).setTextColor(Setup.appSettings().getDrawerLabelColor()).getView();
    }

    public App getApp() {
        return this.app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Item getItemGroup() {
        return this.itemGroup;
    }

    public void setItemGroup(Item itemGroup) {
        this.itemGroup = itemGroup;
    }

    public View getView() {
        return this;
    }

    public IconProvider getIconProvider() {
        return this.iconProvider;
    }

    public Drawable getCurrentIcon() {
        return this.icon;
    }

    public void setIconProvider(BaseIconProvider iconProvider) {
        this.iconProvider = iconProvider;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getIconSize() {
        return this.iconSize;
    }

    public void setIconSize(float iconSize) {
        this.iconSize = iconSize;
    }

    public boolean getShowLabel() {
        return this.showLabel;
    }

    public void setTargetedWidth(int width) {
        this.targetedWidth = width;
    }

    public void setTargetedHeightPadding(int padding) {
        this.targetedHeightPadding = padding;
    }

    public AppItemView(Context context) {
        this(context, null);
    }

    @SuppressLint("WrongConstant")
    public AppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.icon = null;
        this.textPaint = new Paint(1);
        this.textContainer = new Rect();
        this.showLabel = false;
        this.count_process_pixel = 10;
        if (this.typeface == null) {
            this.typeface = Typeface.createFromAsset(getContext().getAssets(), "RobotoCondensed-Regular.ttf");
        }
        if (this.typefaceNumber == null) {
            this.typefaceNumber = Typeface.create(Typeface.DEFAULT, 1);
        }
        setWillNotDraw(false);
        setDrawingCacheEnabled(true);
        setWillNotCacheDrawing(false);
        this.labelHeight = (float) Tool.sp2px(getContext(), 13.0f);
        this.textPaint.setTextSize((float) Tool.sp2px(getContext(), 13.0f));
        this.textPaint.setColor(-12303292);
        this.textPaint.setTypeface(this.typeface);
    }

    public void load() {
        if (this.iconProvider != null) {
            this.iconProvider.loadIconIntoIconDrawer(this, (int) this.iconSize, 0);
        }
    }

    public void reset() {
        if (this.iconProvider != null) {
            this.iconProvider.cancelLoad((IconDrawer) this);
        }
        this.icon = null;
        invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float mWidth = this.iconSize;
        float mHeight = this.iconSize + (this.showLabel ? 0.0f : this.labelHeight);
        if (this.targetedWidth != 0) {
            mWidth = (float) this.targetedWidth;
        }
        int wi= (int)Math.ceil((double) mWidth);
        int hei=((int) Math.ceil((double) ((int) mHeight))) + Tool.dp2px(2, getContext()) + (this.targetedHeightPadding * 2);
        setMeasuredDimension((int) Math.ceil((double) mWidth), hei);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw(canvas);
        try {
            cx_Notify = ((((((float) getWidth()) - this.iconSize) / Tool.DEFAULT_IMAGE_BACKOFF_MULT) - ((((float) getWidth()) - this.iconSize) / partPadding)) + this.iconSize) + ((((float) getWidth()) - this.iconSize) / (partPadding / Tool.DEFAULT_IMAGE_BACKOFF_MULT));
            cy_Notify = (this.heightPadding - ((((float) getWidth()) - this.iconSize) / partPadding)) + 10.0f;
            int numberNotify = 0;
            HashMap<String, AppNotifyItem> mapNumberNotify = ((MainApplication) getContext().getApplicationContext()).getMapNumberNotify();
            if (this.app != null) {
                if (mapNumberNotify.containsKey(this.app.getPackageName())) {
                    numberNotify = ((AppNotifyItem) mapNumberNotify.get(this.app.getPackageName())).getNumber();
                }
                if (numberNotify > 0) {
                    canvas.save();
                    Paint paintNumber = new Paint(1);
                    paintNumber.setTextSize((float) Tool.sp2px(getContext(), 10.0f));
                    paintNumber.setColor(-1);
                    paintNumber.setTypeface(this.typefaceNumber);
                    Rect rect = new Rect();
                    String numberTemp = "88";
                    paintNumber.getTextBounds(numberTemp, 0, numberTemp.length(), rect);
                    Paint paintNotify = new Paint(1);
                    paintNotify.setColor(SupportMenu.CATEGORY_MASK);
                    canvas.translate(cx_Notify, cy_Notify);
                    canvas.drawCircle(0.0f, 0.0f, (((float) rect.width()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT) + 15.0f, paintNotify);
                    canvas.restore();
                    canvas.save();
                    String strNumber = String.valueOf(numberNotify);
                    paintNumber.getTextBounds(strNumber, 0, strNumber.length(), rect);
                    canvas.translate((cx_Notify - (((float) rect.width()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT)) - 1.5f, cy_Notify + (((float) rect.height()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT));
                    canvas.drawText(strNumber, 0.0f, 0.0f, paintNumber);
                    canvas.restore();
                }
            }
        } catch (Exception e) {
            Log.e("error draw red circle: " + e.getMessage());
        }
    }

    private void initDraw(Canvas canvas) {
        this.heightPadding = ((((float) getHeight()) - this.iconSize) - (this.showLabel ? 0.0f : this.labelHeight)) / Tool.DEFAULT_IMAGE_BACKOFF_MULT;
        drawLabelText(canvas);
        drawIcon(canvas);
    }

    private void drawLabelText(Canvas canvas) {
        if (!(this.label == null || this.showLabel)) {
            this.textPaint.getTextBounds(this.label, 0, this.label.length(), this.textContainer);
        }
        if (this.label != null && !this.showLabel && this.textContainer.width() > 0 && this.label.length() != 0) {
            float characterSize = (float) (this.textContainer.width() / this.label.length());
            int charToTruncate = (int) Math.ceil((double) (((((float) this.label.length()) * characterSize) - ((float) getWidth())) / characterSize));
            float x = Math.max(8.0f, ((float) (getWidth() - this.textContainer.width())) / Tool.DEFAULT_IMAGE_BACKOFF_MULT);
            if (this.textContainer.width() <= getWidth() || (this.label.length() - 3) - charToTruncate <= 0) {
                canvas.drawText(this.label, x, (((float) getHeight()) - this.heightPadding) + ((((float) getWidth()) - this.iconSize) / partPadding), this.textPaint);
                return;
            }
            try {
                canvas.drawText(this.label.substring(0, (this.label.length() - 3) - charToTruncate) + "...", x, (((float) getHeight()) - this.heightPadding) + ((((float) getWidth()) - this.iconSize) / partPadding), this.textPaint);
            } catch (Exception e) {
                canvas.drawText(this.label, x, (((float) getHeight()) - this.heightPadding) + ((((float) getWidth()) - this.iconSize) / partPadding), this.textPaint);
            }
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.save();
        Paint paint = new Paint(1);
        paint.setColor(Setup.appSettings().getBackgroundIconDesktopColor());
        canvas.translate(((((float) getWidth()) - this.iconSize) / Tool.DEFAULT_IMAGE_BACKOFF_MULT) - ((((float) getWidth()) - this.iconSize) / partPadding), this.heightPadding - ((((float) getWidth()) - this.iconSize) / partPadding));
        RectF rectF = new RectF();
        rectF.set(0.0f, 0.0f, (float) ((int) (this.iconSize + ((((float) getWidth()) - this.iconSize) / (partPadding / Tool.DEFAULT_IMAGE_BACKOFF_MULT)))), (float) ((int) (this.iconSize + ((((float) getWidth()) - this.iconSize) / (partPadding / Tool.DEFAULT_IMAGE_BACKOFF_MULT)))));
        canvas.drawRoundRect(rectF, 45.0f, 45.0f, paint);
        canvas.restore();
    }

    private void drawCoreIcon(Canvas canvas, boolean isResize) {
        if (isResize) {
            canvas.save();
            canvas.translate(((((float) getWidth()) - this.iconSize) / Tool.DEFAULT_IMAGE_BACKOFF_MULT) - ((((float) getWidth()) - this.iconSize) / partPadding), this.heightPadding - ((((float) getWidth()) - this.iconSize) / partPadding));
            this.icon.setBounds(0, 0, (int) (this.iconSize + ((((float) getWidth()) - this.iconSize) / (partPadding / Tool.DEFAULT_IMAGE_BACKOFF_MULT))), (int) (this.iconSize + ((((float) getWidth()) - this.iconSize) / (partPadding / Tool.DEFAULT_IMAGE_BACKOFF_MULT))));
            this.icon.draw(canvas);
            canvas.restore();
            return;
        }
        canvas.save();
        canvas.translate((((float) getWidth()) - this.iconSize) / Tool.DEFAULT_IMAGE_BACKOFF_MULT, this.heightPadding);
        this.icon.setBounds(0, 0, (int) this.iconSize, (int) this.iconSize);
        this.icon.draw(canvas);
        canvas.restore();
    }

    private void drawIcon(Canvas canvas) {
        if (this.icon != null) {
            if (this.icon instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) this.icon).getBitmap();
                drawBackground(canvas);
                drawCoreIcon(canvas, false);
                return;
            }
            drawBackground(canvas);
            drawCoreIcon(canvas, false);
        }
    }

    public void onIconAvailable(Drawable drawable, int index) {
        this.icon = drawable;
        super.invalidate();
    }

    public void onIconCleared(Drawable placeholder, int index) {
        this.icon = placeholder;
        super.invalidate();
    }

    public void onAttachedToWindow() {
        if (!(this.fastAdapterItem || this.iconProvider == null)) {
            this.iconProvider.loadIconIntoIconDrawer(this, (int) this.iconSize, 0);
        }
        super.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        if (!(this.fastAdapterItem || this.iconProvider == null)) {
            this.iconProvider.cancelLoad((IconDrawer) this);
            this.icon = null;
        }
        super.onDetachedFromWindow();
    }

    public void invalidate() {
        if (this.fastAdapterItem || this.iconProvider == null) {
            super.invalidate();
            return;
        }
        this.iconProvider.cancelLoad((IconDrawer) this);
        this.icon = null;
    }
}
