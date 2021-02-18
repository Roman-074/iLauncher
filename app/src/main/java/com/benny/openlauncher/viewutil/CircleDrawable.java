package com.benny.openlauncher.viewutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import com.benny.openlauncher.core.util.Tool;

public class CircleDrawable extends Drawable {
    private float currentScale = Tool.DEFAULT_BACKOFF_MULT;
    private boolean hidingOldIcon;
    private Bitmap icon;
    private int iconPadding;
    private int iconSize;
    private int iconSizeReal;
    private Bitmap iconToFade;
    private Paint paint;
    private Paint paint2;
    private float scaleStep = Tool.DEFAULT_BOTTOM_PADDING_FRACTION;

    public CircleDrawable(Context context, Drawable icon, int color) {
        this.icon = Tool.drawableToBitmap(icon);
        this.iconPadding = Tool.dp2px(6, context);
        this.iconSizeReal = icon.getIntrinsicHeight();
        this.iconSize = icon.getIntrinsicHeight() + (this.iconPadding * 2);
        this.paint = new Paint(1);
        this.paint.setColor(color);
        this.paint.setAlpha(100);
        this.paint.setStyle(Style.FILL);
        this.paint2 = new Paint(1);
        this.paint2.setFilterBitmap(true);
    }

    public void setIcon(Drawable icon) {
        this.iconToFade = this.icon;
        this.hidingOldIcon = true;
        this.icon = Tool.drawableToBitmap(icon);
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle((float) (this.iconSize / 2), (float) (this.iconSize / 2), (float) (this.iconSize / 2), this.paint);
        if (this.iconToFade != null) {
            canvas.save();
            if (this.hidingOldIcon) {
                this.currentScale -= this.scaleStep;
            } else {
                this.currentScale += this.scaleStep;
            }
            this.currentScale = Tool.clampFloat(this.currentScale, 0.0f, Tool.DEFAULT_BACKOFF_MULT);
            canvas.scale(this.currentScale, this.currentScale, (float) (this.iconSize / 2), (float) (this.iconSize / 2));
            canvas.drawBitmap(this.hidingOldIcon ? this.iconToFade : this.icon, (float) ((this.iconSize / 2) - (this.iconSizeReal / 2)), (float) ((this.iconSize / 2) - (this.iconSizeReal / 2)), this.paint2);
            canvas.restore();
            if (this.currentScale == 0.0f) {
                this.hidingOldIcon = false;
            }
            if (!this.hidingOldIcon && this.scaleStep == Tool.DEFAULT_BACKOFF_MULT) {
                this.iconToFade = null;
            }
            invalidateSelf();
            return;
        }
        canvas.drawBitmap(this.icon, (float) ((this.iconSize / 2) - (this.iconSizeReal / 2)), (float) ((this.iconSize / 2) - (this.iconSizeReal / 2)), this.paint2);
    }

    public int getIntrinsicWidth() {
        return this.iconSize;
    }

    public int getIntrinsicHeight() {
        return this.iconSize;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    @SuppressLint("WrongConstant")
    public int getOpacity() {
        return -2;
    }
}
