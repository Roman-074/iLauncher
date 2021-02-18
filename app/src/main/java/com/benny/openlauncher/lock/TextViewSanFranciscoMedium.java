package com.benny.openlauncher.lock;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class TextViewSanFranciscoMedium extends AppCompatTextView {
    public TextViewSanFranciscoMedium(Context context) {
        super(context);
        setTypeface(Utils.getTypefaceRobotoMedium(context));
    }

    public TextViewSanFranciscoMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Utils.getTypefaceRobotoMedium(context));
    }

    public TextViewSanFranciscoMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Utils.getTypefaceRobotoMedium(context));
    }
}
