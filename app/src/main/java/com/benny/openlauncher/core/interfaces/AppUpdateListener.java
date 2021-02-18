package com.benny.openlauncher.core.interfaces;

import java.util.List;

public interface AppUpdateListener<A extends App> {
    boolean onAppUpdated(List<A> list);
}
