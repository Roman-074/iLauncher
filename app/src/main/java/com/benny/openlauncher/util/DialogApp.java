package com.benny.openlauncher.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.benny.openlauncher.R;


public class DialogApp {
    private static Activity activity;
    private static AlertDialog dialog;
    private static Thread threadDelay;
    private static String title = "";

    public interface DialogAppCallback {
        void cancelDialog();

        void okDialog();
    }

    public static void showNotify(Activity activity, String title, DialogAppCallback callback) {
        showNotify(activity, title, null, activity.getString(R.string.ok), callback);
    }

    public static void showNotify(Activity activity, String title, int id_btnOk, DialogAppCallback callback) {
        showNotify(activity, title, null, activity.getString(id_btnOk), callback);
    }

    public static void showNotify(Activity activity, int id_title, DialogAppCallback callback) {
        showNotify(activity, activity.getString(id_title), null, activity.getString(R.string.ok), callback);
    }

    public static void showNotify(Activity activity, int id_title, int id_btnOk, DialogAppCallback callback) {
        showNotify(activity, activity.getString(id_title), null, activity.getString(id_btnOk), callback);
    }

    public static void showNotify(Activity activity, int id_title, int id_btnCancel, int id_btnOk, DialogAppCallback callback) {
        showNotify(activity, activity.getString(id_title), activity.getString(id_btnCancel), activity.getString(id_btnOk), callback);
    }

    public static void showNotify(Activity activity, String title, int id_btnCancel, int id_btnOk, DialogAppCallback callback) {
        showNotify(activity, title, activity.getString(id_btnCancel), activity.getString(id_btnOk), callback);
    }

    public static void showNotify(Activity activity, String title, String btnCancel, String btnOk, final DialogAppCallback callback) {
        if (title != null && title.length() != 0) {
            try {
                cancel();
                final Builder builder = new Builder(activity);
                builder.setMessage((CharSequence) title).setCancelable(false);
                if (btnCancel != null) {
                    builder.setNegativeButton((CharSequence) btnCancel, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DialogApp.cancel();
                            if (callback != null) {
                                callback.cancelDialog();
                            }
                        }
                    });
                }
                builder.setPositiveButton((CharSequence) btnOk, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogApp.cancel();
                        if (callback != null) {
                            callback.okDialog();
                        }
                    }
                });
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            DialogApp.dialog = builder.create();
                            DialogApp.dialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    public static void showNotify(Activity activity, String title, String content, String btnCancel, String btnOk, final DialogAppCallback callback) {
        if (title != null && title.length() != 0) {
            try {
                cancel();
                final Builder builder = new Builder(activity);
                builder.setTitle((CharSequence) title).setMessage((CharSequence) content).setCancelable(false);
                if (btnCancel != null) {
                    builder.setNegativeButton((CharSequence) btnCancel, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DialogApp.cancel();
                            if (callback != null) {
                                callback.cancelDialog();
                            }
                        }
                    });
                }
                builder.setPositiveButton((CharSequence) btnOk, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogApp.cancel();
                        if (callback != null) {
                            callback.okDialog();
                        }
                    }
                });
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            DialogApp.dialog = builder.create();
                            DialogApp.dialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    public static void setTimeDelay(final int timeDelay) {
        if (threadDelay == null) {
            threadDelay = new Thread() {
                public void run() {
                    try {
                        synchronized (DialogApp.threadDelay) {
                            DialogApp.threadDelay.wait((long) (timeDelay * 1000));
                        }
                        if (DialogApp.threadDelay != null) {
                            DialogApp.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DialogApp.threadDelay = null;
                }
            };
            threadDelay.start();
        }
    }

    public static boolean isShowing() {
        if (dialog == null) {
            return false;
        }
        return dialog.isShowing();
    }

    public static void setTitle(String title) {
        if (dialog != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        DialogApp.dialog.setMessage(DialogApp.title);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void cancel() {
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        if (DialogApp.dialog != null) {
                            DialogApp.dialog.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                if (threadDelay != null) {
                    synchronized (threadDelay) {
                        threadDelay.notify();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            threadDelay = null;
        } catch (Exception e2) {
        }
    }
}
