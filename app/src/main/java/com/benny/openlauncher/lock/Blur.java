package com.benny.openlauncher.lock;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.renderscript.Allocation;
import android.renderscript.Allocation.MipmapControl;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import androidx.annotation.RequiresApi;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import android.util.Log;
import java.lang.reflect.Array;

public class Blur {
    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {
        int bsum = 0;
        int gsum = 0;
        int rsum = 0;
        int boutsum = 0;
        int goutsum = 0;
        int routsum = 0;
        int binsum = 0;
        int ginsum = 0;
        int rinsum = 0;
        if (VERSION.SDK_INT > 16) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            final Context context2 = context;
            final Bitmap bitmap2 = sentBitmap;
            final int i = radius;
            AsyncTask.execute(new Runnable() {
                @RequiresApi(api = 17)
                public void run() {
                    RenderScript rs = RenderScript.create(context2);
                    Bitmap bitmap = bitmap2;
                    MipmapControl mipmapControl = MipmapControl.MIPMAP_NONE;
                    Allocation input = Allocation.createFromBitmap(rs, bitmap, MipmapControl.MIPMAP_NONE, 1);
                    Allocation output = Allocation.createTyped(rs, input.getType());
                    ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                    script.setRadius((float) i);
                    script.setInput(input);
                    script.forEach(output);
                    output.copyTo(bitmap);
                }
            });
            return bitmap;
        }
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return null;
        }
        int i2;
        int y;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[(w * h)];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = (radius + radius) + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int[] vmin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[(divsum * 256)];
        for (i2 = 0; i2 < divsum * 256; i2++) {
            dv[i2] = i2 / divsum;
        }
        int yi = 0;
        int yw = 0;
        int[][] stack = (int[][]) Array.newInstance(Integer.TYPE, new int[]{div, 3});
        int r1 = radius + 1;
        for (y = 0; y < h; y++) {
            int x;
            for (i2 = -radius; i2 <= radius; i2++) {
                int p = pix[Math.min(wm, Math.max(i2, 0)) + yi];
                int[] sir = stack[i2 + radius];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & 255;
                int rbs = r1 - Math.abs(i2);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i2 > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            int stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                int[]sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min((x + radius) + 1, wm);
                }
                int p = pix[vmin[x] + yw];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & 255;
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (int x = 0; x < w; x++) {
            bsum = 0;
            gsum = 0;
            rsum = 0;
            boutsum = 0;
            goutsum = 0;
            routsum = 0;
            binsum = 0;
            ginsum = 0;
            rinsum = 0;
            int yp = (-radius) * w;
            for (i2 = -radius; i2 <= radius; i2++) {
                yi = Math.max(0, yp) + x;
               int[] sir = stack[i2 + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                int rbs = r1 - Math.abs(i2);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i2 > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i2 < hm) {
                    yp += w;
                }
            }
            yi = x;
            int stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (((ViewCompat.MEASURED_STATE_MASK & pix[yi]) | (dv[rsum] << 16)) | (dv[gsum] << 8)) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                int[]sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                int p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
