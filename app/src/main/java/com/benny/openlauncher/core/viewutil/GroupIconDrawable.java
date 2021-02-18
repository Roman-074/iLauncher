package com.benny.openlauncher.core.viewutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenu;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.MainApplication;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.IconDrawer;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.manager.Setup.Logger;
import com.benny.openlauncher.core.model.AppNotifyItem;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.util.Tool;
import java.util.HashMap;

public class GroupIconDrawable extends Drawable implements IconDrawer {
    public static int SIZE_FOLDER_SHOW = 9;
    private Context context;
    private float iconSize;
    private int iconSizeTini = 0;
    private Drawable[] icons;
    private Item item;
    private boolean needAnimate;
    private boolean needAnimateScale;
    private int outline;
    private float padding;
    private Paint paintIcon;
    private Paint paintInnerCircle;
    private Paint paintOuterCircle;
    private float scaleFactor = Tool.DEFAULT_BACKOFF_MULT;
    private Typeface typefaceNumber;

    public GroupIconDrawable(Context context, Item item, int iconSize) {
        int i;
        this.context = context;
        this.item = item;
        float size = (float) Tool.dp2px(iconSize, context);
        Drawable[] icons = new Drawable[(SIZE_FOLDER_SHOW + 1)];
        for (i = 0; i < icons.length; i++) {
            icons[i] = null;
        }
        init(icons, size);
        i = 0;
        while (i < icons.length && i < item.items.size()) {
            Item temp = (Item) item.items.get(i);
            App app = null;
            if (temp != null) {
                app = Setup.appLoader().findItemApp(temp);
            }
            if (app == null) {
                Logger logger = Setup.logger();
                String str = "Item %s has a null app at index %d (Intent: %s)";
                Object[] objArr = new Object[3];
                objArr[0] = item.getLabel();
                objArr[1] = Integer.valueOf(i);
                objArr[2] = temp == null ? "Item is NULL" : temp.getIntent();
                logger.log(this, 3, null, str, objArr);
                icons[i] = new ColorDrawable(0);
            } else {
                app.getIconProvider().loadIconIntoIconDrawer(this, (int) size, i);
            }
            i++;
        }
        if (this.typefaceNumber == null) {
            this.typefaceNumber = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        }
    }

    private void init(Drawable[] icons, float size) {
        this.icons = icons;
        this.iconSize = size;
        this.padding = this.iconSize / 25.0f;
        this.iconSizeTini = (int) ((this.iconSize - (this.padding * 4.0f)) / 3.0f);
        this.paintInnerCircle = new Paint();
        this.paintInnerCircle.setColor(-1);
        this.paintInnerCircle.setAlpha(150);
        this.paintInnerCircle.setAntiAlias(true);
        this.paintOuterCircle = new Paint();
        this.paintOuterCircle.setColor(-1);
        this.paintOuterCircle.setAntiAlias(true);
        this.paintOuterCircle.setFlags(1);
        this.paintOuterCircle.setStyle(Style.STROKE);
        this.outline = Tool.dp2px(2, Home.launcher);
        this.paintOuterCircle.setStrokeWidth((float) this.outline);
        this.paintIcon = new Paint();
        this.paintIcon.setAntiAlias(true);
        this.paintIcon.setFilterBitmap(true);
    }

    public void popUp() {
        this.needAnimate = true;
        this.needAnimateScale = true;
        invalidateSelf();
    }

    public void popBack() {
        this.needAnimate = false;
        this.needAnimateScale = false;
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        canvas.save();
        if (this.needAnimateScale) {
            this.scaleFactor = Tool.clampFloat(this.scaleFactor - 0.09f, 0.5f, Tool.DEFAULT_BACKOFF_MULT);
        } else {
            this.scaleFactor = Tool.clampFloat(this.scaleFactor + 0.09f, 0.5f, Tool.DEFAULT_BACKOFF_MULT);
        }
        canvas.scale(this.scaleFactor, this.scaleFactor, this.iconSize / Tool.DEFAULT_IMAGE_BACKOFF_MULT, this.iconSize / Tool.DEFAULT_IMAGE_BACKOFF_MULT);
        if (this.icons[0] != null) {
            drawIcon(canvas, this.icons[0], this.padding, this.padding, ((float) this.iconSizeTini) + this.padding, ((float) this.iconSizeTini) + this.padding, this.paintIcon);
        }
        if (this.icons[1] != null) {
            drawIcon(canvas, this.icons[1], ((float) this.iconSizeTini) + (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT), this.padding, (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT), ((float) this.iconSizeTini) + this.padding, this.paintIcon);
        }
        if (this.icons[2] != null) {
            drawIcon(canvas, this.icons[2], (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + (this.padding * 3.0f), this.padding, this.iconSize - this.padding, ((float) this.iconSizeTini) + this.padding, this.paintIcon);
        }
        if (this.icons[3] != null) {
            drawIcon(canvas, this.icons[3], this.padding, (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + ((float) this.iconSizeTini), ((float) this.iconSizeTini) + this.padding, (((float) this.iconSizeTini) + this.padding) * Tool.DEFAULT_IMAGE_BACKOFF_MULT, this.paintIcon);
        }
        if (this.icons[4] != null) {
            drawIcon(canvas, this.icons[4], ((float) this.iconSizeTini) + (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT), (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + ((float) this.iconSizeTini), (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT), (((float) this.iconSizeTini) + this.padding) * Tool.DEFAULT_IMAGE_BACKOFF_MULT, this.paintIcon);
        }
        if (this.icons[5] != null) {
            drawIcon(canvas, this.icons[5], (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + (this.padding * 3.0f), (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + ((float) this.iconSizeTini), this.iconSize - this.padding, (((float) this.iconSizeTini) + this.padding) * Tool.DEFAULT_IMAGE_BACKOFF_MULT, this.paintIcon);
        }
        if (this.icons[6] != null) {
            drawIcon(canvas, this.icons[6], this.padding, (this.padding * 3.0f) + (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT), ((float) this.iconSizeTini) + this.padding, (((float) this.iconSizeTini) + this.padding) * 3.0f, this.paintIcon);
        }
        if (this.icons[7] != null) {
            drawIcon(canvas, this.icons[7], ((float) this.iconSizeTini) + (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT), (this.padding * 3.0f) + (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT), (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + (this.padding * Tool.DEFAULT_IMAGE_BACKOFF_MULT), (((float) this.iconSizeTini) + this.padding) * 3.0f, this.paintIcon);
        }
        if (this.icons[9] != null) {
            drawIcon(canvas, ContextCompat.getDrawable(this.context, R.drawable.ic_add_black_48dp), (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + (this.padding * 3.0f), (this.padding * 3.0f) + (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT), this.iconSize - this.padding, (((float) this.iconSizeTini) + this.padding) * 3.0f, this.paintIcon);
        } else if (this.icons[8] != null) {
            drawIcon(canvas, this.icons[8], (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT) + (this.padding * 3.0f), (this.padding * 3.0f) + (((float) this.iconSizeTini) * Tool.DEFAULT_IMAGE_BACKOFF_MULT), this.iconSize - this.padding, (((float) this.iconSizeTini) + this.padding) * 3.0f, this.paintIcon);
        }
        canvas.restore();
        if (this.needAnimate) {
            this.paintIcon.setAlpha(Tool.clampInt(this.paintIcon.getAlpha() - 25, 0, 255));
            invalidateSelf();
        } else if (this.paintIcon.getAlpha() != 255) {
            this.paintIcon.setAlpha(Tool.clampInt(this.paintIcon.getAlpha() + 25, 0, 255));
            invalidateSelf();
        }
        try {
            float cx = this.iconSize - this.padding;
            int numberNotify = 0;
            HashMap<String, AppNotifyItem> mapNumberNotify = ((MainApplication) this.context.getApplicationContext()).getMapNumberNotify();
            for (int i = 0; i < this.item.getGroupItems().size(); i++) {
                Item itemChild = (Item) this.item.getGroupItems().get(i);
                if (mapNumberNotify.containsKey(itemChild.getPackageName())) {
                    numberNotify += ((AppNotifyItem) mapNumberNotify.get(itemChild.getPackageName())).getNumber();
                }
            }
            if (numberNotify > 0) {
                canvas.save();
                Paint paint = new Paint(1);
                paint.setTextSize((float) Tool.sp2px(this.context, 10.0f));
                paint.setColor(-1);
                paint.setTypeface(this.typefaceNumber);
                Rect rect = new Rect();
                String numberTemp = "99";
                paint.getTextBounds(numberTemp, 0, numberTemp.length(), rect);
                paint = new Paint(1);
                paint.setColor(SupportMenu.CATEGORY_MASK);
                canvas.translate(cx, 0.0f);
                canvas.drawCircle(0.0f, 0.0f, (((float) rect.width()) / Tool.DEFAULT_IMAGE_BACKOFF_MULT) + 15.0f, paint);
                canvas.restore();
                canvas.save();
                String strNumber = String.valueOf(numberNotify);
                paint.getTextBounds(strNumber, 0, strNumber.length(), rect);
                canvas.translate(cx - ((float) (rect.width() / 2)), ((float) (rect.height() / 2)) + 0.0f);
                canvas.drawText(strNumber, 0.0f, 0.0f, paint);
                canvas.restore();
            }
        } catch (Exception e) {
        }
    }

    private void drawIcon(Canvas canvas, Drawable icon, float l, float t, float r, float b, Paint paint) {
        icon.setBounds((int) l, (int) t, (int) r, (int) b);
        icon.setFilterBitmap(true);
        icon.setAlpha(paint.getAlpha());
        icon.draw(canvas);
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    @SuppressLint("WrongConstant")
    public int getOpacity() {
        return -2;
    }

    public void onIconAvailable(Drawable drawable, int index) {
        this.icons[index] = drawable;
        invalidateSelf();
    }

    public void onIconCleared(Drawable placeholder, int index) {
        Drawable[] drawableArr = this.icons;
        if (placeholder == null) {
            placeholder = new ColorDrawable(0);
        }
        drawableArr[index] = placeholder;
        invalidateSelf();
    }
}
