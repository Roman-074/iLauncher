package com.benny.openlauncher.core.interfaces;

import java.text.SimpleDateFormat;

public interface SettingsManager {
    boolean enableImageCaching();

    boolean getAppRestartRequired();

    int getBackgroundIconDesktopColor();

    int getDesktopColor();

    int getDesktopColumnCount();

    int getDesktopEffect();

    int getDesktopIconSize();

    int getDesktopPageCurrent();

    int getDesktopRowCount();

    int getDesktopStyle();

    int getDockColor();

    boolean getDockEnable();

    int getDockIconSize();

    int getDockSize();

    int getDrawerBackgroundColor();

    int getDrawerCardColor();

    int getDrawerColumnCount();

    int getDrawerFastScrollerColor();

    int getDrawerIconSize();

    int getDrawerLabelColor();

    int getDrawerRowCount();

    int getDrawerStyle();

    boolean getGestureDockSwipeUp();

    String getPackageMusicPlayer();

    String getPassCodeLockScreen();

    int getPopupColor();

    int getPopupLabelColor();

    String getSearchBarBaseURI();

    boolean getSearchBarEnable();

    int getSearchGridSize();

    int getSearchLabelLines();

    SimpleDateFormat getUserDateFormat();

    int getVerticalDrawerHorizontalMargin();

    int getVerticalDrawerVerticalMargin();

    boolean isAppFirstLaunch();

    boolean isDesktopFullscreen();

    boolean isDesktopLock();

    boolean isDesktopShowIndicator();

    boolean isDesktopShowLabel();

    boolean isDockShowLabel();

    boolean isDrawerRememberPosition();

    boolean isDrawerShowCardView();

    boolean isDrawerShowIndicator();

    boolean isDrawerShowLabel();

    boolean isFirstAddDock();

    boolean isFirstCategoryApps();

    boolean isFirstHelpSwipeDown();

    boolean isFirstWallpaper();

    boolean isGestureFeedback();

    boolean isIpornX();

    boolean isLockScreenEnable();

    boolean isLockScreenEnablePassCode();

    boolean isResetSearchBarOnOpen();

    boolean isSearchBarTimeEnabled();

    boolean isSearchGridListSwitchEnabled();

    boolean isSearchUseGrid();

    boolean isSortApplications();

    void setAppFirstLaunch(boolean z);

    void setAppRestartRequired(boolean z);

    void setDesktopEffect(int i);

    void setDesktopLock(boolean z);

    void setDesktopPageCurrent(int i);

    void setFirstAddDock();

    void setFirstCategoryApps();

    void setFirstWallpaper();

    void setHelpSwipeDown();

    void setLockScreenEnable(boolean z);

    void setLockScreenEnablePassCode(boolean z);

    void setPackageMusicPlayer(String str);

    void setPassCodeLockScreen(String str);

    void setSearchUseGrid(boolean z);
}
