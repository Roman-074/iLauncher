package com.benny.openlauncher.base.dao;

import android.content.Context;
import android.graphics.Typeface;

import com.benny.openlauncher.lock.Values;


public class BaseTypeface {
    private Typeface Black;
    private Typeface BlackItalic;
    private Typeface Bold;
    private Typeface BoldItalic;
    private Typeface Italic;
    private Typeface Light;
    private Typeface LightItalic;
    private Typeface Medium;
    private Typeface MediumItalic;
    private Typeface Regular;
    private Typeface Thin;
    private Typeface ThinItalic;
    private Context context;

    public BaseTypeface(Context context) {
        this.context = context;
    }

    public Typeface getBlack() {
        if (this.Black == null) {
            this.Black = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-Black.ttf");
        }
        return this.Black;
    }

    public Typeface getBlackItalic() {
        if (this.BlackItalic == null) {
            this.BlackItalic = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-BlackItalic.ttf");
        }
        return this.BlackItalic;
    }

    public Typeface getBold() {
        if (this.Bold == null) {
            this.Bold = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-Bold.ttf");
        }
        return this.Bold;
    }

    public Typeface getBoldItalic() {
        if (this.BoldItalic == null) {
            this.BoldItalic = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-BoldItalic.ttf");
        }
        return this.BoldItalic;
    }

    public Typeface getItalic() {
        if (this.Italic == null) {
            this.Italic = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-Italic.ttf");
        }
        return this.Italic;
    }

    public Typeface getLight() {
        if (this.Light == null) {
            this.Light = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-Light.ttf");
        }
        return this.Light;
    }

    public Typeface getLightItalic() {
        if (this.LightItalic == null) {
            this.LightItalic = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-LightItalic.ttf");
        }
        return this.LightItalic;
    }

    public Typeface getMedium() {
        if (this.Medium == null) {
            this.Medium = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-Medium.ttf");
        }
        return this.Medium;
    }

    public Typeface getMediumItalic() {
        if (this.MediumItalic == null) {
            this.MediumItalic = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-MediumItalic.ttf");
        }
        return this.MediumItalic;
    }

    public Typeface getRegular() {
        if (this.Regular == null) {
            this.Regular = Typeface.createFromAsset(this.context.getAssets(), Values.ROBOTO_REGULAR);
        }
        return this.Regular;
    }

    public Typeface getThin() {
        if (this.Thin == null) {
            this.Thin = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-Thin.ttf");
        }
        return this.Thin;
    }

    public Typeface getThinItalic() {
        if (this.ThinItalic == null) {
            this.ThinItalic = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-ThinItalic");
        }
        return this.ThinItalic;
    }
}
