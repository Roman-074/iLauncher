package com.benny.openlauncher.lock;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class TextViewSanFranciscoThin extends AppCompatTextView {
    public TextViewSanFranciscoThin(Context context) {
        super(context);
        setTypeface(Utils.getTypefaceRobotoThin(context));
        setTextColor(-1);
    }

    public TextViewSanFranciscoThin(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Utils.getTypefaceRobotoThin(context));
        setTextColor(-1);
    }

    public TextViewSanFranciscoThin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Utils.getTypefaceRobotoThin(context));
        setTextColor(-1);
    }
}
