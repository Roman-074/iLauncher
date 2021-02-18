package com.benny.openlauncher.adapter;

import android.service.notification.StatusBarNotification;

public interface NotificationAdapterListener {
    void onClickClear();

    void onClickNotification(StatusBarNotification statusBarNotification);
}
