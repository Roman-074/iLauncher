package com.benny.openlauncher.util;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.content.ContextCompat;
import com.benny.openlauncher.App;
import com.benny.openlauncher.R;
import com.benny.openlauncher.core.interfaces.SettingsManager;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.util.LauncherAction.ActionDisplayItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AppSettings extends AppSettingsBase implements SettingsManager {
    private AppSettings(Context context) {
        super(context);
    }

    public static AppSettings get() {
        return new AppSettings(App.get());
    }

    public int getDesktopColumnCount() {
        return getInt(R.string.pref_key__desktop_columns, 4);
    }

    public int getDesktopRowCount() {
        return getInt(R.string.pref_key__desktop_rows, 5);
    }

    public int getDesktopStyle() {
        return getIntOfStringPref(R.string.pref_key__desktop_style, 1);
    }

    public void setDesktopStyle(int style) {
        setInt(R.string.pref_key__desktop_style, style);
    }

    public boolean getSearchBarEnable() {
        return getBool(R.string.pref_key__search_bar_enable, true);
    }

    public boolean isDesktopFullscreen() {
        return getBool(R.string.pref_key__desktop_fullscreen, true);
    }

    public boolean isDesktopShowIndicator() {
        return getBool(R.string.pref_key__desktop_show_position_indicator, true);
    }

    public boolean isDesktopShowLabel() {
        return getBool(R.string.pref_key__desktop_show_label, true);
    }

    public String getSearchBarBaseURI() {
        return getString((int) R.string.pref_key__search_bar_base_uri, (int) R.string.pref_default__search_bar_base_uri);
    }

    public boolean getSearchBarForceBrowser() {
        return getBool(R.string.pref_key__search_bar_force_browser, false);
    }

    public boolean isSearchBarTimeEnabled() {
        return true;
    }

    public SimpleDateFormat getUserDateFormat() {
        return null;
    }

    public boolean isResetSearchBarOnOpen() {
        return false;
    }

    public boolean isSearchGridListSwitchEnabled() {
        return false;
    }

    public boolean isSearchUseGrid() {
        return false;
    }

    public void setSearchUseGrid(boolean enabled) {
    }

    public int getSearchGridSize() {
        return 1;
    }

    public int getSearchLabelLines() {
        return Integer.MAX_VALUE;
    }

    public boolean enableImageCaching() {
        return true;
    }

    public int getDesktopColor() {
        return getInt(R.string.pref_key__desktop_background_color, 0);
    }

    public int getDesktopIconSize() {
        return getIconSize();
    }

    public boolean getDockEnable() {
        return getBool(R.string.pref_key__dock_enable, true);
    }

    public int getDockIconSize() {
        return getIconSize();
    }

    public void setDockEnable(boolean enable) {
        setBool(R.string.pref_key__dock_enable, enable);
    }

    public int getDockSize() {
        return getInt(R.string.pref_key__dock_size, 4);
    }

    public boolean isDockShowLabel() {
        return getBool(R.string.pref_key__dock_show_label, false);
    }

    public int getDockColor() {
        return getInt(R.string.pref_key__dock_background_color, ContextCompat.getColor(this.context, R.color.default_background_dock));
    }

    public int getDrawerColumnCount() {
        return getInt(R.string.pref_key__drawer_columns, 5);
    }

    public int getDrawerRowCount() {
        return getInt(R.string.pref_key__drawer_rows, 6);
    }

    public int getDrawerStyle() {
        return getIntOfStringPref(R.string.pref_key__drawer_style, 1);
    }

    public boolean isDrawerShowCardView() {
        return getBool(R.string.pref_key__drawer_show_card_view, true);
    }

    public boolean isDrawerRememberPosition() {
        return getBool(R.string.pref_key__drawer_remember_position, true);
    }

    public boolean isDrawerShowIndicator() {
        return getBool(R.string.pref_key__drawer_show_position_indicator, true);
    }

    public boolean isDrawerShowLabel() {
        return getBool(R.string.pref_key__drawer_show_label, true);
    }

    public int getDrawerBackgroundColor() {
        return getInt(R.string.pref_key__drawer_background_color, 0);
    }

    public int getVerticalDrawerHorizontalMargin() {
        return 8;
    }

    public int getVerticalDrawerVerticalMargin() {
        return 16;
    }

    public int getDrawerIconSize() {
        return getIconSize();
    }

    public int getDrawerFastScrollerColor() {
        return ContextCompat.getColor(Setup.appContext(), R.color.colorAccent);
    }

    public int getDrawerCardColor() {
        return getInt(R.string.pref_key__drawer_card_color, -1);
    }

    public int getDrawerLabelColor() {
        return getInt(R.string.pref_key__drawer_label_color, -12303292);
    }

    public int getPopupColor() {
        return getInt(R.string.pref_key__desktop_folder_color, -1);
    }

    public int getPopupLabelColor() {
        return getDrawerLabelColor();
    }

    public int getMinibarBackgroundColor() {
        return getInt(R.string.pref_key__minibar_background_color, ContextCompat.getColor(this.context, R.color.colorPrimaryDark));
    }

    public boolean getGestureDockSwipeUp() {
        return getBool(R.string.pref_key__dock_swipe_up, false);
    }

    public boolean isGestureFeedback() {
        return getBool(R.string.pref_key__desktop_gesture_feedback, false);
    }

    public int getIconSize() {
        return getInt(R.string.pref_key__icon_size, 46);
    }

    public String getIconPack() {
        return getString((int) R.string.pref_key__icon_pack, "");
    }

    public void setIconPack(String value) {
        setString(R.string.pref_key__icon_pack, value);
    }

    public boolean isAppFirstLaunch() {
        return getBool(R.string.pref_key__first_start, true);
    }

    @SuppressLint({"ApplySharedPref"})
    public void setAppFirstLaunch(boolean value) {
        this.prefApp.edit().putBoolean(this.context.getString(R.string.pref_key__first_start), value).commit();
    }

    public boolean isDesktopLock() {
        return getBool(R.string.pref_key__desktop_lock, false);
    }

    public void setDesktopLock(boolean value) {
        setBool(R.string.pref_key__desktop_lock, value);
    }

    public int getDesktopPageCurrent() {
        return getInt(R.string.pref_key__desktop_current_position, 1);
    }

    public void setDesktopPageCurrent(int value) {
        setInt(R.string.pref_key__desktop_current_position, value);
    }

    public boolean getMinibarEnable() {
        return getBool(R.string.pref_key__minibar_enable, false);
    }

    public void setMinibarEnable(boolean value) {
        setBool(R.string.pref_key__minibar_enable, value);
    }

    public ArrayList<String> getMinibarArrangement() {
        ArrayList<String> ret = getStringList(R.string.pref_key__minibar_arrangement);
        if (ret.isEmpty()) {
            for (ActionDisplayItem item : LauncherAction.actionDisplayItems) {
                ret.add("0" + item.label.toString());
            }
            setMinibarArrangement(ret);
        }
        return ret;
    }

    public void setMinibarArrangement(ArrayList<String> value) {
        setStringList(R.string.pref_key__minibar_arrangement, value);
    }

    public ArrayList<String> getHiddenAppsList() {
        return getStringList(R.string.pref_key__hidden_apps);
    }

    public void setHiddenAppsList(ArrayList<String> value) {
        setStringList(R.string.pref_key__hidden_apps, value);
    }

    public boolean getAppRestartRequired() {
        return getBool(R.string.pref_key__queue_restart, false);
    }

    @SuppressLint({"ApplySharedPref"})
    public void setAppRestartRequired(boolean value) {
        this.prefApp.edit().putBoolean(this.context.getString(R.string.pref_key__queue_restart), value).commit();
    }

    public void setFirstAddDock() {
        setBool(R.string.pref_key_is_first_add_dock, false);
    }

    public boolean isFirstAddDock() {
        return getBool(R.string.pref_key_is_first_add_dock, true);
    }

    public int getDesktopEffect() {
        return getInt(R.string.pref_key_effect_desktop, 0);
    }

    public void setDesktopEffect(int position) {
        setInt(R.string.pref_key_effect_desktop, position);
    }

    public void setFirstWallpaper() {
        setBool(R.string.pref_key_is_first_wall_paper, false);
    }

    public boolean isFirstWallpaper() {
        return getBool(R.string.pref_key_is_first_wall_paper, true);
    }

    public String getPackageMusicPlayer() {
        return getString((int) R.string.pref_key_music_player, "");
    }

    public void setPackageMusicPlayer(String packageName) {
        setString(R.string.pref_key_music_player, packageName);
    }

    public boolean isFirstHelpSwipeDown() {
        return getBool(R.string.pref_key_first_help_swipe_down, true);
    }

    public void setHelpSwipeDown() {
        setBool(R.string.pref_key_first_help_swipe_down, false);
    }

    public int getBackgroundIconDesktopColor() {
        return getInt(R.string.pref_key__desktop_background_icon_color, ContextCompat.getColor(this.context, R.color.default_background_icon));
    }

    public boolean isLockScreenEnable() {
        return getBool(R.string.pref_key__lock_screen_enable, false);
    }

    public void setLockScreenEnable(boolean enable) {
        setBool(R.string.pref_key__lock_screen_enable, enable);
    }

    public boolean isLockScreenEnablePassCode() {
        return getBool(R.string.pref_key__lock_screen_enable_passcode, false);
    }

    public void setLockScreenEnablePassCode(boolean enable) {
        setBool(R.string.pref_key__lock_screen_enable_passcode, enable);
    }

    public void setPassCodeLockScreen(String enable) {
        setString(R.string.pref_key__content_passcode, enable);
    }

    public String getPassCodeLockScreen() {
        return getString((int) R.string.pref_key__content_passcode, "");
    }

    public boolean isIpornX() {
        return getBool(R.string.pref_key__on_off_iporn, true);
    }

    public boolean isSortApplications() {
        return getBool(R.string.pref_key__sort_applications, false);
    }

    public void setFirstCategoryApps() {
        setBool(R.string.pref_key_is_first_category_apps, false);
    }

    public boolean isFirstCategoryApps() {
        return getBool(R.string.pref_key_is_first_category_apps, true);
    }
}
