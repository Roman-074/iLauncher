package com.benny.openlauncher.viewutil;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class RoundDrawable extends Drawable {
    Bitmap bmp;

    public RoundDrawable(Bitmap bmp) {
        this.bmp = bmp;
    }

    public void draw(Canvas canvas) {
        if (this.bmp != null) {
            canvas.drawBitmap(getCroppedBitmap(this.bmp, getBounds().width()), 0.0f, 0.0f, null);
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        String color = "#BAB399";
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, radius, radius);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(((float) (radius / 2)) + 0.7f, ((float) (radius / 2)) + 0.7f, ((float) (radius / 2)) + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bmp, rect, rect, paint);
        return output;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    @SuppressLint("WrongConstant")
    public int getOpacity() {
        return -1;
    }
}
