package com.benny.openlauncher.base.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.base.utils.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class BaseConfig {
    private ads_network_new ads_network_new = new ads_network_new();
    private config_ads config_ads = new config_ads();
    private key key = new key();
    private ArrayList<more_apps> more_apps = new ArrayList();
    private ArrayList<shortcut_dynamic> shortcut_dynamic = new ArrayList();
    private thumnail_config thumnail_config = new thumnail_config();
    private Update update = new Update();

    public class Update {
        String description = "";
        int offset_show = 0;
        String packagename = "";
        int status = 0;
        String title = "";
        String type = "";
        String url_store = "";
        String version = "";

        public int getStatus() {
            return this.status;
        }

        public int getOffset_show() {
            return this.offset_show;
        }

        public String getTitle() {
            return this.title;
        }

        public String getDescription() {
            return this.description;
        }

        public String getUrl_store() {
            return this.url_store;
        }

        public String getType() {
            return this.type;
        }

        public String getVersion() {
            return this.version;
        }

        public String getPackagename() {
            return this.packagename;
        }
    }

    public class ads_network_new {
        String banner = "admob";
        String popup = "admob";
        String thumbai = "admob";

        public String getBanner() {
            return this.banner;
        }

        public String getPopup() {
            return this.popup;
        }

        public String getThumbai() {
            return this.thumbai;
        }
    }

    public class config_ads {
        int offset_time_show_popup = 150;
        int time_hidden_to_click_banner = 0;
        int time_hidden_to_click_popup = 0;
        int time_start_show_popup = 15;

        public int getOffset_time_show_popup() {
            return this.offset_time_show_popup;
        }

        public void setOffset_time_show_popup(int offset_time_show_popup) {
            this.offset_time_show_popup = offset_time_show_popup;
        }

        public int getTime_hidden_to_click_banner() {
            return this.time_hidden_to_click_banner;
        }

        public int getTime_hidden_to_click_popup() {
            return this.time_hidden_to_click_popup;
        }

        public int getTime_start_show_popup() {
            return this.time_start_show_popup;
        }

        public void setTime_start_show_popup(int time_start_show_popup) {
            this.time_start_show_popup = time_start_show_popup;
        }
    }

    public class key {
    }

    public class more_apps implements Serializable {
        String banner = "";
        String description = "";
        String icon = "";
        String packagename = "";
        String popup = "";
        String thumbai = "";
        String title = "";
        String type = "";
        String url_store = "";

        public String getPopup() {
            if (this.popup == null || this.popup.equals("")) {
                return this.thumbai;
            }
            return this.popup;
        }

        public String getTitle() {
            return this.title;
        }

        public String getDescription() {
            return this.description;
        }

        public String getPackagename() {
            return this.packagename;
        }

        public String getType() {
            return this.type;
        }

        public String getIcon() {
            return this.icon;
        }

        public String getUrl_store() {
            return this.url_store;
        }

        public String getBanner() {
            return this.banner;
        }

        public String getThumbai() {
            return this.thumbai;
        }
    }

    public class shortcut_dynamic {
        private String icon = "";
        Drawable iconDrawable = null;
        private int id = -1;
        private String link = "";
        private String name_shotcut = "";
        private String package_name = "";

        public shortcut_dynamic() {
        }

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIcon() {
            return this.icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName_shotcut() {
            return this.name_shotcut;
        }

        public void setName_shotcut(String name_shotcut) {
            this.name_shotcut = name_shotcut;
        }

        public String getLink() {
            return this.link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPackage_name() {
            return this.package_name;
        }

        public void setPackage_name(String package_name) {
            this.package_name = package_name;
        }

        public Drawable getIconDrawable() {
            return this.iconDrawable;
        }

        public void initIconDrawale(Context context) {
            if (getIconDrawable() == null) {
                Glide.with(context).asBitmap().load(getIcon()).into(new SimpleTarget<Bitmap>() {
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        shortcut_dynamic.this.setIconDrawable(new BitmapDrawable(resource));
                    }
                });
            }
        }

        public void setIconDrawable(Drawable iconDrawable) {
            this.iconDrawable = iconDrawable;
        }
    }

    public class thumnail_config {
        int offset_show_thumbai_detail_news = 10;
        int offset_show_thumbai_end_detail_news = 8;
        int offset_video_to_show_thumbai = 6;
        int random_show_popup_hdv = 20;
        int random_show_thumbai_detail_hdv = 50;
        int random_show_thumbai_hdv = 0;
        int start_show_thumbai_detail_news = 4;
        int start_video_show_thumbai = 6;

        public int getRandom_show_popup_hdv() {
            return this.random_show_popup_hdv;
        }

        public int getOffset_video_to_show_thumbai() {
            if (this.offset_video_to_show_thumbai == 0) {
                return 6;
            }
            return this.offset_video_to_show_thumbai;
        }

        public int getRandom_show_thumbai_hdv() {
            return this.random_show_thumbai_hdv;
        }

        public int getRandom_show_thumbai_detail_hdv() {
            return this.random_show_thumbai_detail_hdv;
        }

        public int getStart_video_show_thumbai() {
            if (this.start_video_show_thumbai == 0) {
                return 6;
            }
            return this.start_video_show_thumbai;
        }

        public int getOffset_show_thumbai_detail_news() {
            if (this.offset_show_thumbai_detail_news == 0) {
                return 10;
            }
            return this.offset_show_thumbai_detail_news;
        }

        public int getStart_show_thumbai_detail_news() {
            if (this.start_show_thumbai_detail_news == 0) {
                return 4;
            }
            return this.start_show_thumbai_detail_news;
        }

        public int getOffset_show_thumbai_end_detail_news() {
            if (this.offset_show_thumbai_end_detail_news == 0) {
                return 8;
            }
            return this.offset_show_thumbai_end_detail_news;
        }

        public void setRandom_show_popup_hdv(int random_show_popup_hdv) {
            this.random_show_popup_hdv = random_show_popup_hdv;
        }

        public void setRandom_show_thumbai_hdv(int random_show_thumbai_hdv) {
            this.random_show_thumbai_hdv = random_show_thumbai_hdv;
        }
    }

    public void initMoreApps(Context context) {
        int i = 0;
        while (i < this.more_apps.size()) {
            try {
                if (BaseUtils.isInstalled(context, ((more_apps) this.more_apps.get(i)).getPackagename())) {
                    this.more_apps.remove(i);
                } else {
                    i++;
                }
            } catch (Exception e) {
                Log.e("error initMoreApps: " + e.getMessage());
            }
        }
        i = 0;
        while (i < this.shortcut_dynamic.size()) {
            boolean isInstalled = false;
            String[] listPackageName = ((shortcut_dynamic) this.shortcut_dynamic.get(i)).getPackage_name().contains(",") ? ((shortcut_dynamic) this.shortcut_dynamic.get(i)).getPackage_name().split(",") : new String[]{((shortcut_dynamic) this.shortcut_dynamic.get(i)).getPackage_name()};
            for (String packageName : listPackageName) {
                if (BaseUtils.isInstalled(context, packageName)) {
                    isInstalled = true;
                    break;
                }
            }
            if (isInstalled) {
                this.shortcut_dynamic.remove(i);
            } else {
                try {
                    ((shortcut_dynamic) this.shortcut_dynamic.get(i)).setPackage_name(listPackageName[0]);
                } catch (Exception e2) {
                    Log.e("error setPackage_name 0 shorcut_dynamic: " + e2.getMessage());
                }
                i++;
            }
        }
        try {
            Iterator it = this.shortcut_dynamic.iterator();
            while (it.hasNext()) {
                ((shortcut_dynamic) it.next()).initIconDrawale(context);
            }
        } catch (Exception e3) {
        }
    }

    public ArrayList<shortcut_dynamic> getShortcut_dynamic() {
        return this.shortcut_dynamic;
    }

    public Update getUpdate() {
        return this.update;
    }

    public key getKey() {
        return this.key;
    }

    public config_ads getConfig_ads() {
        return this.config_ads;
    }

    public ads_network_new getAds_network_new() {
        return this.ads_network_new;
    }

    public thumnail_config getThumnail_config() {
        return this.thumnail_config;
    }

    public ArrayList<more_apps> getMore_apps() {
        return this.more_apps;
    }
}
