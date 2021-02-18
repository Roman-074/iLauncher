package com.benny.openlauncher.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.NumberPicker;

import com.benny.openlauncher.R;


public class NumberPickerPreference1 extends DialogPreference {
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;
    private final int maxValue;
    private final int minValue;
    private NumberPicker picker;
    private int value;
    private final boolean wrapSelectorWheel;

    public NumberPickerPreference1(Context context, AttributeSet attrs) {
        this(context, attrs, 16842897);
    }

    @SuppressLint("ResourceType")
    public NumberPickerPreference1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference);
        this.minValue = a.getInteger(0, 0);
        this.maxValue = a.getInteger(1, 100);
        this.wrapSelectorWheel = a.getBoolean(2, true);
        a.recycle();
    }

    protected View onCreateDialogView() {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        this.picker = new NumberPicker(getContext());
        this.picker.setLayoutParams(layoutParams);
        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(this.picker);
        return dialogView;
    }

    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        Log.i("jj", "onBindDialogView: " + this.minValue + " " + this.maxValue);
        this.picker.setMinValue(25);
        this.picker.setMaxValue(110);
        this.picker.setWrapSelectorWheel(this.wrapSelectorWheel);
        this.picker.setValue(getValue());
    }

    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            this.picker.clearFocus();
            int newValue = this.picker.getValue();
            if (callChangeListener(Integer.valueOf(newValue))) {
                setValue(newValue);
            }
        }
    }

    protected Object onGetDefaultValue(TypedArray a, int index) {
        return Integer.valueOf(a.getInt(index, this.minValue));
    }

    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(this.minValue) : ((Integer) defaultValue).intValue());
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }
}
