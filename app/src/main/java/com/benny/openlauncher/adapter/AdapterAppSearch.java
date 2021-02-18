package com.benny.openlauncher.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.IconDrawer;
import com.benny.openlauncher.core.util.Tool;
import com.benny.openlauncher.customview.FloatingActionMenuCustom;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu.Item;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton.Builder;
import com.oguzdev.circularfloatingactionmenu.library.animation.DefaultAnimationHandler;

import java.util.ArrayList;

public class AdapterAppSearch extends Adapter<AdapterAppSearch.AppSearchViewHolder> {
    private Home activity;
    private AdapterAppSearchListener adapterAppSearchListener;
    private ArrayList<App> appList;
    private FloatingActionMenuCustom circleMenu;
    private int positionCurrent = -1;

    class AppSearchViewHolder extends ViewHolder {
        public ImageView ivIcon;
        public TextView tvLabel;

        public AppSearchViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (AdapterAppSearch.this.positionCurrent != AppSearchViewHolder.this.getAdapterPosition() || !AdapterAppSearch.this.circleMenu.isOpen()) {
                        AdapterAppSearch.this.closeMenu();
                        if (AdapterAppSearch.this.appList.size() > AppSearchViewHolder.this.getAdapterPosition() && AppSearchViewHolder.this.getAdapterPosition() >= 0) {
                            Tool.startApp(AdapterAppSearch.this.activity, (App) AdapterAppSearch.this.appList.get(AppSearchViewHolder.this.getAdapterPosition()));
                            if (AdapterAppSearch.this.adapterAppSearchListener != null) {
                                AdapterAppSearch.this.adapterAppSearchListener.onClickItem();
                            }
                            try {
                                ((BaseApplication) AdapterAppSearch.this.activity.getApplication()).putEvents(BaseApplication.EVENTS_NAME_CLICK_ICON_ON_SEARCH_BAR);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AdapterAppSearch.this.positionCurrent = AppSearchViewHolder.this.getAdapterPosition();
                    AdapterAppSearch.this.showMenu(v);
                    return false;
                }
            });

            this.ivIcon =(ImageView)itemView.findViewById(R.id.ivIcon);
            this.tvLabel =(TextView)itemView.findViewById(R.id.tvLabel);
            }
        }

        class impleIconDrawer implements IconDrawer {
            private ImageView ivIcon;

            impleIconDrawer(ImageView ivIcon) {
                this.ivIcon = ivIcon;
            }

            public void onIconAvailable(Drawable drawable, int index) {
                this.ivIcon.setImageDrawable(drawable);
            }

            public void onIconCleared(Drawable placeholder, int index) {
            }
        }

        public AdapterAppSearch(Home activity, ArrayList<App> appList, AdapterAppSearchListener adapterAppSearchListener) {
            this.appList = appList;
            this.activity = activity;
            this.adapterAppSearchListener = adapterAppSearchListener;
            initMenu();
        }

        public AppSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AppSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_bar_new_activity_item, null));
        }

        @SuppressLint("ResourceType")
        public void onBindViewHolder(AppSearchViewHolder holder, int position) {
            ((App) this.appList.get(position)).getIconProvider().loadIconIntoIconDrawer(new impleIconDrawer(holder.ivIcon), (int) this.activity.getResources().getDimension(17104896), 0);
            holder.tvLabel.setText(((App) this.appList.get(position)).getLabel());
        }

        public int getItemCount() {
            return this.appList.size();
        }

        private void initMenu() {
            ImageView ivOpen = new ImageView(this.activity);
            ImageView ivInfo = new ImageView(this.activity);
            ImageView ivUnistall = new ImageView(this.activity);
            ivOpen.setImageResource(R.drawable.ic_open_in_new_blue_a700_48dp);
            ivInfo.setImageResource(R.drawable.ic_info_blue_a700_48dp);
            ivUnistall.setImageResource(R.drawable.ic_delete_forever_blue_a700_48dp);
            Builder builder = new Builder(this.activity);
            SubActionButton sbOpen = builder.setContentView(ivOpen).build();
            sbOpen.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AdapterAppSearch.this.closeMenu();
                    Log.d("HuyAnh", "onClick object: " + ((App) AdapterAppSearch.this.appList.get(AdapterAppSearch.this.positionCurrent)).toString());
                    Tool.startApp(AdapterAppSearch.this.activity, (App) AdapterAppSearch.this.appList.get(AdapterAppSearch.this.positionCurrent));
                }
            });
            SubActionButton sbInfo = builder.setContentView(ivInfo).build();
            sbInfo.setOnClickListener(new OnClickListener() {
                @SuppressLint("WrongConstant")
                public void onClick(View view) {
                    AdapterAppSearch.this.closeMenu();
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", ((App) AdapterAppSearch.this.appList.get(AdapterAppSearch.this.positionCurrent)).getPackageName(), null));
                    intent.addFlags(268435456);
                    AdapterAppSearch.this.activity.startActivity(intent);
                }
            });
            View sbUnistall = builder.setContentView(ivUnistall).build();
            sbUnistall.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AdapterAppSearch.this.closeMenu();
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.setData(Uri.parse("package:" + ((App) AdapterAppSearch.this.appList.get(AdapterAppSearch.this.positionCurrent)).getPackageName()));
                    AdapterAppSearch.this.activity.startActivity(intent);
                }
            });
            ArrayList<Item> subActionItems = new ArrayList();
            subActionItems.add(new Item(sbOpen, BaseUtils.genpx(this.activity, 40), BaseUtils.genpx(this.activity, 40)));
            subActionItems.add(new Item(sbInfo, BaseUtils.genpx(this.activity, 40), BaseUtils.genpx(this.activity, 40)));
            subActionItems.add(new Item(sbUnistall, BaseUtils.genpx(this.activity, 40), BaseUtils.genpx(this.activity, 40)));
            this.circleMenu = new FloatingActionMenuCustom(this.activity.desktop, -30, 90, this.activity.getResources().getDimensionPixelSize(R.dimen.distance_menu_touch), subActionItems, new DefaultAnimationHandler(), true, null);
        }

        public void closeMenu() {
            if (this.circleMenu != null && this.circleMenu.isOpen()) {
                this.circleMenu.close(true);
            }
        }

        private void showMenu(View view) {
            if (this.circleMenu != null) {
                this.circleMenu.setMainView(view);
                if (this.circleMenu.isOpen()) {
                    this.circleMenu.close(false);
                }
                this.circleMenu.open(true);
            }
        }
    }
