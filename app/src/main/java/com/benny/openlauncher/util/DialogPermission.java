package com.benny.openlauncher.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.benny.openlauncher.R;
import com.bumptech.glide.Glide;


public class DialogPermission {
    public static final int TYPE_DATA = 1;
    public static final int TYPE_NOTIFICATION = 2;
    private static Activity activity;
    private static AlertDialog dialog;

    public static synchronized void showDialog(Activity activity, final DialogAppCallback callback, int type) {
        synchronized (DialogPermission.class) {
            synchronized (DialogPermission.class) {
                try {
                    cancel();
                    final Builder builder = new Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    builder.setCancelable(false);
                    View view = inflater.inflate(R.layout.dialog_permission_layout, null);
                    RelativeLayout layoutStep1_Notify=(RelativeLayout)view.findViewById(R.id.layout_notify_step1);
                    LinearLayout layoutStep1_Appusage=(LinearLayout)view.findViewById(R.id.layout_app_usage_step1);
                    LinearLayout layoutStep2_Notify=(LinearLayout)view.findViewById(R.id.layout_notify_step2) ;
                    LinearLayout layoutStep2_Appusage=(LinearLayout)view.findViewById(R.id.layout_app_usage_step2) ;
                    ImageView imageViewStep1 = (ImageView) view.findViewById(R.id.dialog_permission_img_step_1);
                    ImageView imageViewStep2 = (ImageView) view.findViewById(R.id.dialog_permission_img_step_2);
                    if (type == 1) {
                        layoutStep1_Appusage.setVisibility(View.VISIBLE);
                        layoutStep2_Appusage.setVisibility(View.VISIBLE);
                        layoutStep1_Notify.setVisibility(View.GONE);
                        layoutStep2_Notify.setVisibility(View.GONE);
//                        Glide.with(activity).load(Integer.valueOf(R.drawable.dialog_ic_permission_step_1)).into(imageViewStep1);
//                        Glide.with(activity).load(Integer.valueOf(R.drawable.dialog_ic_permission_step_2)).into(imageViewStep2);
                    } else if (type == 2) {
                        layoutStep1_Appusage.setVisibility(View.GONE);
                        layoutStep2_Appusage.setVisibility(View.GONE);
                        layoutStep1_Notify.setVisibility(View.VISIBLE);
                        layoutStep2_Notify.setVisibility(View.VISIBLE);
                    }
                    builder.setView(view);
                    builder.setPositiveButton(activity.getString(R.string.ok), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DialogPermission.cancel();
                            if (callback != null) {
                                callback.okDialog();
                            }
                        }
                    });
                    builder.setNegativeButton(activity.getString(R.string.cancel), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DialogPermission.cancel();
                            if (callback != null) {
                                callback.cancelDialog();
                            }
                        }
                    });
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                DialogPermission.dialog = builder.create();
                                DialogPermission.dialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        }
    }

    public static void cancel() {
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        if (DialogPermission.dialog != null) {
                            DialogPermission.dialog.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DialogPermission.dialog = null;
                }
            });
        } catch (Exception e) {
        }
    }
}
