package com.benny.openlauncher.core.interfaces;

import java.util.List;

public interface AppDeleteListener<A extends App> {
    boolean onAppDeleted(List<A> list);
}
