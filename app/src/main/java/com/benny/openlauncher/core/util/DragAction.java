package com.benny.openlauncher.core.util;

public class DragAction {
    public Action action;

    public enum Action {
        APP_DRAWER,
        APP,
        SHORTCUT,
        GROUP,
        ACTION,
        WIDGET
    }

    public DragAction(Action action) {
        this.action = action;
    }
}
