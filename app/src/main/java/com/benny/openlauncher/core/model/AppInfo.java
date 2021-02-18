package com.benny.openlauncher.core.model;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String code = null;
    private final Drawable icon;
    private String name = null;
    private boolean selected = false;

    public AppInfo(String code, String name, Drawable icon, boolean selected) {
        this.code = code;
        this.name = name;
        this.icon = icon;
        this.selected = selected;
    }

    public String getCode() {
        return this.code;
    }

    public Drawable getImage() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean paramBoolean) {
        this.selected = paramBoolean;
    }
}
