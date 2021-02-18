package com.benny.openlauncher.core.util;

import java.util.ArrayList;

public class Definitions {
    public static final int ACTION_LAUNCHER = 8;
    public static final int DOCK_DEFAULT_CENTER_ITEM_INDEX_X = 2;
    public static final boolean ENABLE_ITEM_TOUCH_LISTENER = false;
    public static final String INT_SEP = "#";
    public static final boolean IS_IPORN_X = true;
    public static final int NO_SCALE = -1;
    public static ArrayList<String> packageNameDefaultApps = new ArrayList();
    public static ArrayList<String> packageNameDefaultContacts = new ArrayList();

    public enum ItemPosition {
        Dock,
        Desktop
    }

    public enum ItemState {
        Hidden,
        Visible
    }
}
