package com.benny.openlauncher.core.interfaces;

import com.benny.openlauncher.core.util.BaseIconProvider;

public interface App {
    String getClassName();

    <T extends BaseIconProvider> T getIconProvider();

    String getLabel();

    String getPackageName();
}
