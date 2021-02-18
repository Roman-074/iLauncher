package com.benny.openlauncher.adapter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.Utils;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.base.dao.BaseConfig;
import com.benny.openlauncher.base.utils.Log;
import com.bumptech.glide.Glide;


public class SettingsMoreApps extends Adapter<android.support.v7.widget.RecyclerView.ViewHolder> {
    private BaseApplication application;
    private Context context;
    private SettingsMoreAppsListener settingsMoreAppsListener;

    class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        @BindView(R.id.more_app_item_iv)
        ImageView ivIcon;
        @BindView(R.id.more_app_item_tv)
        TextView tvName;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (SettingsMoreApps.this.settingsMoreAppsListener != null) {
                            SettingsMoreApps.this.settingsMoreAppsListener.onClick((BaseConfig.more_apps) SettingsMoreApps.this.application.getBaseConfig().getMore_apps().get(ViewHolder.this.getAdapterPosition()));
                        }
                    } catch (Exception e) {
                    }
                }
            });
            ButterKnife.bind((Object) this, v);
            this.tvName.setTypeface(SettingsMoreApps.this.application.getBaseTypeface().getRegular());
        }
    }


    public SettingsMoreApps(Context context, SettingsMoreAppsListener settingsMoreAppsListener) {
        this.context = context;
        this.application = (BaseApplication) context.getApplicationContext();
        this.settingsMoreAppsListener = settingsMoreAppsListener;
    }

    public int getItemCount() {
        return this.application.getBaseConfig().getMore_apps().size();
    }

    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_settings_more_apps_item, parent, false));
    }

    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder, int position) {
        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            BaseConfig.more_apps more_apps = (BaseConfig.more_apps) this.application.getBaseConfig().getMore_apps().get(position);
            viewHolder.tvName.setText(more_apps.getTitle());
            if (!more_apps.getIcon().equals("")) {
                Glide.with(this.context).load(more_apps.getIcon()).into(viewHolder.ivIcon);
            }
        } catch (Exception e) {
            Log.e("error onBindViewHolder SettingsMoreApps: " + e.getMessage());
        }
    }
}
