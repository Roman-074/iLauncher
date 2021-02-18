package com.benny.openlauncher.customview;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.adapter.NotificationAdapter;
import com.benny.openlauncher.adapter.NotificationAdapterListener;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.service.NotificationServiceCustom;
import com.benny.openlauncher.util.ItemOffsetDecoration;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator;

public class LockScreenMain extends RelativeLayout {
    private BaseApplication application;
    private final int durationAnimationRcView = 500;
    private Handler handler = new Handler();
    private ArrayList<StatusBarNotification> listNotification = new ArrayList();
    private LockScreenMainListener lockScreenMainListener;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private NotificationAdapter notificationAdapter = null;
    @BindView(R.id.lockmain_rcView)
    RecyclerView rcView;
    @BindView(R.id.tvShimmer)
    ShimmerTextView tvShimmer;

    public void setLockScreenMainListener(LockScreenMainListener lockScreenMainListener) {
        this.lockScreenMainListener = lockScreenMainListener;
    }

    public LockScreenMain(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        this.application = (BaseApplication) getContext().getApplicationContext();
        View view = inflate(getContext(), R.layout.view_lock_screen_main, null);
        addView(view);
        ButterKnife.bind((Object) this, view);
        Shimmer shimmer = new Shimmer();
        shimmer.setRepeatCount(-1).setDuration(2000).setStartDelay(1000).setDirection(0);
        shimmer.start(this.tvShimmer);
        this.listNotification.add(0, null);
        this.notificationAdapter = new NotificationAdapter(getContext(), this.listNotification, new NotificationAdapterListener() {
            public void onClickClear() {
                try {
                    LockScreenMain.this.listNotification.clear();
                    LockScreenMain.this.listNotification.add(0, null);
                    LockScreenMain.this.notificationAdapter.notifyDataSetChanged();
                    if (VERSION.SDK_INT >= 19) {
                        NotificationServiceCustom.myService.cancelAllNotifications();
                    }
                    if (LockScreenMain.this.listNotification.size() > 3) {
                        LockScreenMain.this.tvShimmer.setVisibility(GONE);
                    } else {
                        LockScreenMain.this.tvShimmer.setVisibility(VISIBLE);
                    }
                } catch (Exception e) {
                }
            }

            public void onClickNotification(StatusBarNotification sbn) {
                if (VERSION.SDK_INT >= 19) {
                    try {
                        if (sbn.getNotification().contentIntent != null) {
                            sbn.getNotification().contentIntent.send();
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (sbn.getNotification().deleteIntent != null) {
                            sbn.getNotification().deleteIntent.send();
                        }
                    } catch (Exception e2) {
                    }
                    try {
                        if (sbn.getNotification().fullScreenIntent != null) {
                            sbn.getNotification().fullScreenIntent.send();
                        }
                    } catch (Exception e3) {
                    }
                }
                if (LockScreenMain.this.lockScreenMainListener != null) {
                    LockScreenMain.this.lockScreenMainListener.unLock();
                }
                if (VERSION.SDK_INT >= 21) {
                    NotificationServiceCustom.myService.cancelNotification(sbn.getKey());
                } else if (VERSION.SDK_INT >= 19) {
                    NotificationServiceCustom.myService.cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
                }
            }
        });
        this.rcView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.rcView.setAdapter(this.notificationAdapter);
        this.rcView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelOffset(R.dimen.padding_rcView)));
        this.rcView.setItemAnimator(new FadeInRightAnimator());
        this.rcView.getItemAnimator().setAddDuration(500);
        this.rcView.getItemAnimator().setRemoveDuration(500);
        this.rcView.getItemAnimator().setMoveDuration(500);
        this.rcView.getItemAnimator().setChangeDuration(500);
        if (VERSION.SDK_INT >= 19) {
            initNotification();
        }
    }

    @RequiresApi(api = 19)
    public synchronized void removeNotification(final StatusBarNotification sbn) {
        try {
            this.handler.post(new Runnable() {
                public void run() {
                    Log.d("removeNotification lock screen main " + sbn.getTag() + " " + sbn.getId() + " " + sbn.getPackageName());
                    int i = 1;
                    while (i < LockScreenMain.this.listNotification.size()) {
                        Log.i(i + ": " + sbn.getTag() + " " + sbn.getId() + " " + sbn.getPackageName());
                        if ((((StatusBarNotification) LockScreenMain.this.listNotification.get(i)).getTag() + "").equals(sbn.getTag()) && ((StatusBarNotification) LockScreenMain.this.listNotification.get(i)).getId() == sbn.getId() && ((StatusBarNotification) LockScreenMain.this.listNotification.get(i)).getPackageName().equals(sbn.getPackageName())) {
                            Log.v("remove " + i);
                            LockScreenMain.this.listNotification.remove(i);
                            LockScreenMain.this.notificationAdapter.notifyItemRemoved(i);
                        } else {
                            i++;
                        }
                    }
                    if (LockScreenMain.this.listNotification.size() > 3) {
                        LockScreenMain.this.tvShimmer.setVisibility(GONE);
                    } else {
                        LockScreenMain.this.tvShimmer.setVisibility(VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("error removeNotification lock screen main: " + e.getMessage());
        }
    }

    @RequiresApi(api = 19)
    public void addNotification(final StatusBarNotification sbn) {
        try {
            this.handler.post(new Runnable() {
                public void run() {
                    Log.v("addNotification lock screen main: " + sbn.getTag() + " " + sbn.getId() + " " + sbn.getPackageName());
                    Bundle bundle = sbn.getNotification().extras;
                    String title = bundle.getString(NotificationCompat.EXTRA_TITLE);
                    CharSequence notificationText = bundle.getCharSequence(NotificationCompat.EXTRA_TEXT);
                    if ((title != null && !title.equals("")) || (notificationText != null && !notificationText.equals(""))) {
                        LockScreenMain.this.rcView.scrollToPosition(0);
                        if (LockScreenMain.this.listNotification.size() == 0) {
                            LockScreenMain.this.listNotification.add(0, null);
                        }
                        LockScreenMain.this.listNotification.add(1, sbn);
                        LockScreenMain.this.notificationAdapter.notifyItemInserted(1);
                        if (LockScreenMain.this.listNotification.size() > 3) {
                            LockScreenMain.this.tvShimmer.setVisibility(GONE);
                        } else {
                            LockScreenMain.this.tvShimmer.setVisibility(VISIBLE);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("error addNotification lock screen main: " + e.getMessage());
        }
    }

    @RequiresApi(api = 19)
    public synchronized void initNotification() {
        synchronized (this) {
            synchronized (this) {
                Log.d("start initNotification lock screen main");
                try {
                    if (NotificationServiceCustom.myService != null) {
                        try {
                            this.listNotification.clear();
                            this.listNotification.add(0, null);
                            StatusBarNotification[] statusBarNotifications = NotificationServiceCustom.myService.getActiveNotifications();
                            if (statusBarNotifications != null && statusBarNotifications.length > 0) {
                                for (StatusBarNotification statusBarNotification : statusBarNotifications) {
                                    Bundle bundle = statusBarNotification.getNotification().extras;
                                    String title = bundle.getString(NotificationCompat.EXTRA_TITLE);
                                    CharSequence notificationText = bundle.getCharSequence(NotificationCompat.EXTRA_TEXT);
                                    if (!((title == null || title.equals("")) && (notificationText == null || notificationText.equals("")))) {
                                        this.listNotification.add(statusBarNotification);
                                    }
                                }
                            }
                            this.notificationAdapter.notifyDataSetChanged();
                            if (this.listNotification.size() > 3) {
                                this.tvShimmer.setVisibility(GONE);
                            } else {
                                this.tvShimmer.setVisibility(VISIBLE);
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        Log.d("NotificationServiceCustom null");
                    }
                } catch (Exception e2) {
                    Log.e("error lockscreen main initNotification: " + e2.getMessage());
                }
            }
        }
    }

    public void startUpdateTime() {
        stopUpdateTime();
        this.mTimer = new Timer();
        this.mTimerTask = new TimerTask() {
            public void run() {
                if (LockScreenMain.this.handler == null) {
                    LockScreenMain.this.handler = new Handler();
                }
                LockScreenMain.this.handler.post(new Runnable() {
                    public void run() {
                        if (LockScreenMain.this.notificationAdapter != null) {
                            LockScreenMain.this.notificationAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        };
        this.mTimer.schedule(this.mTimerTask, 1000, 10000);
    }

    public void stopUpdateTime() {
        try {
            if (this.mTimerTask != null) {
                this.mTimerTask.cancel();
            }
            this.mTimerTask = null;
        } catch (Exception e) {
        }
        try {
            if (this.mTimer != null) {
                this.mTimer.cancel();
            }
            this.mTimer = null;
        } catch (Exception e2) {
        }
    }
}
