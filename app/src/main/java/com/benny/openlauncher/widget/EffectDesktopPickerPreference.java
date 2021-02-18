package com.benny.openlauncher.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.NumberPicker;

import com.benny.openlauncher.R;


public class EffectDesktopPickerPreference extends DialogPreference {
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;
    private final int maxValue;
    private  final int minValue;
    private NumberPicker picker;
    private int value;
    private  boolean wrapSelectorWheel=false;

    public EffectDesktopPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 16842897);
    }

    @SuppressLint("ResourceType")
    public EffectDesktopPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        //super(context,attrs,defStyleAttr);
        super(new ContextThemeWrapper(context, R.style.NumberPickerTextColorStyle), attrs, defStyleAttr);
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
        String[] listName = new String[13];
        for (int i = 0; i < listName.length; i++) {
            if (i == 0) {
                listName[i] = getContext().getResources().getString(R.string.pref_title__effect_desktop_dialog_default);
            } else {
                listName[i] = getContext().getResources().getString(R.string.pref_title__effect_desktop_dialog_title) + " " + i;
            }
        }
        this.picker.setDisplayedValues(listName);
        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(this.picker);
        return dialogView;
    }

    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        this.picker.setMinValue(0);
        this.picker.setMaxValue(12);
        this.picker.setWrapSelectorWheel(this.wrapSelectorWheel);
        if (getValue() != 0) {
            this.picker.setValue(getValue());
        }
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
