package com.benny.openlauncher.lock;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class TextViewSanFranciscoLight extends AppCompatTextView {
    public TextViewSanFranciscoLight(Context context) {
        super(context);
        setTypeface(Utils.getTypefaceRobotoLight(context));
    }

    public TextViewSanFranciscoLight(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Utils.getTypefaceRobotoLight(context));
    }

    public TextViewSanFranciscoLight(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Utils.getTypefaceRobotoLight(context));
    }
}
