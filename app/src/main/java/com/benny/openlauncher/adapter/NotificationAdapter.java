package com.benny.openlauncher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.app.NotificationCompat;
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
import com.benny.openlauncher.App;
import com.benny.openlauncher.R;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.base.utils.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NotificationAdapter extends Adapter<android.support.v7.widget.RecyclerView.ViewHolder> {
    private App application;
    private Context context;
    private ArrayList<StatusBarNotification> list = new ArrayList();
    private NotificationAdapterListener notificationAdapterListener;

    class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        @BindView(R.id.header_all)
        View header_all;
        @BindView(R.id.ivDeleteNotifications)
        ImageView header_ivDeleteNotifications;
        @BindView(R.id.tvDate)
        TextView header_tvDate;
        @BindView(R.id.tvTime)
        TextView header_tvTime;
        @BindView(R.id.item_all)
        View item_all;
        @BindView(R.id.item_rlHeader_ivIcon)
        ImageView ivIcon;
        @BindView(R.id.item_ivIcon)
        ImageView ivIconNotification;
        @BindView(R.id.item_tvMsg)
        TextView tvMsg;
        @BindView(R.id.item_rlHeader_tvName)
        TextView tvName;
        @BindView(R.id.item_rlHeader_tvTime)
        TextView tvTime;
        @BindView(R.id.item_tvTitle)
        TextView tvTitle;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (NotificationAdapter.this.notificationAdapterListener != null && NotificationAdapter.this.list.get(ViewHolder.this.getAdapterPosition()) != null) {
                            NotificationAdapter.this.notificationAdapterListener.onClickNotification((StatusBarNotification) NotificationAdapter.this.list.get(ViewHolder.this.getAdapterPosition()));
                        }
                    } catch (Exception e) {
                    }
                }
            });
            ButterKnife.bind((Object) this, v);
            this.tvTitle.setTypeface(NotificationAdapter.this.application.getBaseTypeface().getBold());
            this.tvName.setTypeface(NotificationAdapter.this.application.getBaseTypeface().getRegular());
            this.tvTime.setTypeface(NotificationAdapter.this.application.getBaseTypeface().getRegular());
            this.tvMsg.setTypeface(NotificationAdapter.this.application.getBaseTypeface().getRegular());
            this.header_tvTime.setTypeface(NotificationAdapter.this.application.getBaseTypeface().getLight());
            this.header_tvDate.setTypeface(NotificationAdapter.this.application.getBaseTypeface().getRegular());
            this.header_ivDeleteNotifications.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (NotificationAdapter.this.notificationAdapterListener != null) {
                        NotificationAdapter.this.notificationAdapterListener.onClickClear();
                    }
                }
            });
        }
    }

    public NotificationAdapter(Context context, ArrayList<StatusBarNotification> list, NotificationAdapterListener notificationAdapterListener) {
        this.context = context;
        this.application = (App) context.getApplicationContext();
        this.list = list;
        this.notificationAdapterListener = notificationAdapterListener;
    }

    public int getItemCount() {
        return this.list.size();
    }

    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_lock_screen_main_all, parent, false));
    }

    @SuppressLint("WrongConstant")
    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder, int position) {
        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (position == 0) {
                viewHolder.item_all.setVisibility(8);
                viewHolder.header_all.setVisibility(0);
                viewHolder.header_tvTime.setText(getTime());
                viewHolder.header_tvDate.setText(getDayOfWeek() + ", " + getMonth() + " - " + getDayOfMonth());
                if (this.list.size() > 1) {
                    viewHolder.header_ivDeleteNotifications.setVisibility(0);
                    return;
                } else {
                    viewHolder.header_ivDeleteNotifications.setVisibility(8);
                    return;
                }
            }
            viewHolder.item_all.setVisibility(0);
            viewHolder.header_all.setVisibility(8);
            if (VERSION.SDK_INT >= 19) {
                StatusBarNotification statusBarNotification = (StatusBarNotification) this.list.get(position);
                Bundle bundle = statusBarNotification.getNotification().extras;
                try {
                    PackageManager packageManager = this.context.getPackageManager();
                    viewHolder.ivIcon.setImageDrawable(packageManager.getApplicationIcon(statusBarNotification.getPackageName()));
                    viewHolder.tvName.setText((String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(statusBarNotification.getPackageName(), 128)));
                    viewHolder.ivIconNotification.setImageDrawable(packageManager.getResourcesForApplication(statusBarNotification.getPackageName()).getDrawable(bundle.getInt(NotificationCompat.EXTRA_SMALL_ICON)));
                } catch (Exception e) {
                }
                viewHolder.tvTime.setText(BaseUtils.convertTimeToString1(this.context, ((int) (System.currentTimeMillis() - statusBarNotification.getPostTime())) / 1000));
                viewHolder.tvTitle.setText(bundle.getString(NotificationCompat.EXTRA_TITLE));
                viewHolder.tvMsg.setText(bundle.getCharSequence(NotificationCompat.EXTRA_TEXT));
            }
        } catch (Exception e2) {
            Log.e("error onBindViewHolder CommentAdapter: " + e2.getMessage());
        }
    }

    private String getTime() {
        try {
            return new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            return "N/A";
        }
    }

    @SuppressLint("WrongConstant")
    private String getDayOfWeek() {
        try {
            switch (Calendar.getInstance().get(7)) {
                case 1:
                    return this.context.getString(R.string.lock_screen_main_day_of_week_cn);
                case 2:
                    return this.context.getString(R.string.lock_screen_main_day_of_week_t2);
                case 3:
                    return this.context.getString(R.string.lock_screen_main_day_of_week_t3);
                case 4:
                    return this.context.getString(R.string.lock_screen_main_day_of_week_t4);
                case 5:
                    return this.context.getString(R.string.lock_screen_main_day_of_week_t5);
                case 6:
                    return this.context.getString(R.string.lock_screen_main_day_of_week_t6);
                case 7:
                    return this.context.getString(R.string.lock_screen_main_day_of_week_t7);
            }
        } catch (Exception e) {
        }
        return "N/A";
    }

    @SuppressLint("WrongConstant")
    private String getMonth() {
        try {
            switch (Calendar.getInstance().get(2)) {
                case 0:
                    return this.context.getString(R.string.lock_screen_main_month_1);
                case 1:
                    return this.context.getString(R.string.lock_screen_main_month_2);
                case 2:
                    return this.context.getString(R.string.lock_screen_main_month_3);
                case 3:
                    return this.context.getString(R.string.lock_screen_main_month_4);
                case 4:
                    return this.context.getString(R.string.lock_screen_main_month_5);
                case 5:
                    return this.context.getString(R.string.lock_screen_main_month_6);
                case 6:
                    return this.context.getString(R.string.lock_screen_main_month_7);
                case 7:
                    return this.context.getString(R.string.lock_screen_main_month_8);
                case 8:
                    return this.context.getString(R.string.lock_screen_main_month_9);
                case 9:
                    return this.context.getString(R.string.lock_screen_main_month_10);
                case 10:
                    return this.context.getString(R.string.lock_screen_main_month_11);
                case 11:
                    return this.context.getString(R.string.lock_screen_main_month_12);
            }
        } catch (Exception e) {
        }
        return "N/A";
    }

    @SuppressLint("WrongConstant")
    private String getDayOfMonth() {
        return Calendar.getInstance().get(5) + "";
    }
}
