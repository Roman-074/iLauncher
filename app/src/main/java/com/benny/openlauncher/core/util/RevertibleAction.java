package com.benny.openlauncher.core.util;

public interface RevertibleAction {
    void consumeRevert();

    void revertLastItem();

    void setLastItem(Object... objArr);
}
