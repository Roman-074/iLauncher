package com.benny.openlauncher.core.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.view.GravityCompat;
import android.widget.ImageView;
import android.widget.TextView;
import com.benny.openlauncher.core.interfaces.IconDrawer;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.viewutil.GroupIconDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;


public class SimpleIconProvider extends BaseIconProvider {
    protected Drawable drawable;
    protected int drawableResource;

    private class impleRunnable implements Runnable {
        private BaseTarget baseTarget;
        private Context context;
        private String iconUrl = "";

        public impleRunnable(Context context, String iconUrl, BaseTarget baseTarget) {
            this.context = context;
            this.iconUrl = iconUrl;
            this.baseTarget = baseTarget;
        }

        public void run() {
            Glide.with(this.context).load(this.iconUrl).into(this.baseTarget);
        }
    }

    public SimpleIconProvider(Drawable drawable) {
        this.drawable = drawable;
        this.drawableResource = -1;
    }

    public SimpleIconProvider(int drawableResource) {
        this.drawable = null;
        this.drawableResource = drawableResource;
    }

    public SimpleIconProvider(String iconUrl, Context context) {
        this.drawableResource = -1;
        ((Activity) context).runOnUiThread(new impleRunnable(context, iconUrl, new BaseTarget<BitmapDrawable>() {
            public void onResourceReady(BitmapDrawable bitmapDrawable, Transition<? super BitmapDrawable> transition) {
                SimpleIconProvider.this.drawable = bitmapDrawable;
            }

            public void getSize(SizeReadyCallback sizeReadyCallback) {
                sizeReadyCallback.onSizeReady(Tool.AUDIO_STREAM, Tool.AUDIO_STREAM);
            }

            public void removeCallback(SizeReadyCallback sizeReadyCallback) {
            }
        }));
    }

    private Drawable getDrawable() {
        if (this.drawable != null) {
            return this.drawable;
        }
        if (this.drawableResource > 0) {
            return Setup.appContext().getResources().getDrawable(this.drawableResource);
        }
        return null;
    }

    public void loadIcon(IconTargetType type, int forceSize, Object target, Object... args) {
        switch (type) {
            case ImageView:
                ((ImageView) target).setImageDrawable(scaleDrawable(getDrawable(), forceSize));
                return;
            case TextView:
                TextView tv = (TextView) target;
                int gravity = ((Integer) args[0]).intValue();
                Drawable d = scaleDrawable(getDrawable(), forceSize);
                if (gravity == 3 || gravity == GravityCompat.START) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
                    return;
                } else if (gravity == 5 || gravity == GravityCompat.END) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
                    return;
                } else if (gravity == 48) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
                    return;
                } else if (gravity == 80) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, d);
                    return;
                } else {
                    return;
                }
            case IconDrawer:
                ((IconDrawer) target).onIconAvailable(getDrawable(), ((Integer) args[0]).intValue());
                return;
            default:
                return;
        }
    }

    public void cancelLoad(IconTargetType type, Object target) {
    }

    public Drawable getDrawableSynchronously(int forceSize) {
        return scaleDrawable(getDrawable(), forceSize);
    }

    public boolean isGroupIconDrawable() {
        return this.drawable != null && (this.drawable instanceof GroupIconDrawable);
    }

    private Drawable scaleDrawable(Drawable drawable, int forceSize) {
        if (drawable == null || forceSize == -1) {
            return drawable;
        }
        forceSize = Tool.dp2px(forceSize, Setup.appContext());
        return new BitmapDrawable(Setup.appContext().getResources(), Bitmap.createScaledBitmap(Tool.drawableToBitmap(drawable), forceSize, forceSize, true));
    }
}
