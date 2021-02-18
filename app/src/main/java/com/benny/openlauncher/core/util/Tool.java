package com.benny.openlauncher.core.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.BaseApplication;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.core.MainApplication;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.IconProvider;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.core.viewutil.ItemGestureListener.ItemGestureCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Tool {
    public static float DEFAULT_BACKOFF_MULT=1.0f;
    public static  float DEFAULT_IMAGE_BACKOFF_MULT = 2.0f;
    public static final int AUDIO_STREAM = 192;
    public static final float DEFAULT_BOTTOM_PADDING_FRACTION = 0.08f;
    public static final String BASE_TYPE_AUDIO = "audio";
    public static final String TABLE_NAME = "notification";
    public static final int CACHE_ERROR_CODE = 2002;
    public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000;
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String PARAMETER_SHARE_DIALOG_CONTENT_PHOTO = "photo";


    @SuppressLint("WrongConstant")
    public static void hideKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 2);
    }

    @SuppressLint("WrongConstant")
    public static void showKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService("input_method")).toggleSoftInputFromWindow(view.getWindowToken(), 1, 2);
    }

    @SuppressLint("WrongConstant")
    public static void visibleViews(View... views) {
        if (views != null) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(0);
                    view.animate().alpha(DEFAULT_BACKOFF_MULT).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator());
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    public static void visibleViews(long duration, View... views) {
        if (views != null) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(0);
                    view.animate().alpha(DEFAULT_BACKOFF_MULT).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator());
                }
            }
        }
    }

    public static void invisibleViews(View... views) {
        if (views != null) {
            for (final View view : views) {
                if (view != null) {
                    view.animate().alpha(0.0f).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                        @SuppressLint("WrongConstant")
                        public void run() {
                            view.setVisibility(4);
                        }
                    });
                }
            }
        }
    }

    public static void invisibleViews(long duration, View... views) {
        if (views != null) {
            for (final View view : views) {
                if (view != null) {
                    view.animate().alpha(0.0f).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                        @SuppressLint("WrongConstant")
                        public void run() {
                            view.setVisibility(4);
                        }
                    });
                }
            }
        }
    }

    public static void goneViews(View... views) {
        if (views != null) {
            for (final View view : views) {
                if (view != null) {
                    view.animate().alpha(0.0f).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                        public void run() {
                            view.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    }

    public static void goneViews(long duration, View... views) {
        if (views != null) {
            for (final View view : views) {
                if (view != null) {
                    view.animate().alpha(0.0f).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                        public void run() {
                            view.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    }

    public static void createScaleInScaleOutAnim(final View view, final Runnable endAction) {
        view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80).setInterpolator(new AccelerateDecelerateInterpolator());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                view.animate().scaleX(DEFAULT_BACKOFF_MULT).scaleY(DEFAULT_BACKOFF_MULT).setDuration(80).setInterpolator(new AccelerateDecelerateInterpolator());
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        endAction.run();
                    }
                }, 80);
            }
        }, 80);
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int str) {
        Toast.makeText(context, context.getResources().getString(str), Toast.LENGTH_SHORT).show();
    }

    public static float dp2px(float dp, Context context) {
        return TypedValue.applyDimension(1, dp, context.getResources().getDisplayMetrics());
    }

    public static int dp2px(int dp, Context context) {
        if (context == null) {
            return dp;
        }
        return Math.round(TypedValue.applyDimension(1, (float) dp, context.getResources().getDisplayMetrics()));

    }

    public static int sp2px(Context context, float spValue) {
        return (int) ((context.getResources().getDisplayMetrics().scaledDensity * spValue) + 0.5f);
    }

    public static int clampInt(int target, int min, int max) {
        return Math.max(min, Math.min(max, target));
    }

    public static float clampFloat(float target, float min, float max) {
        return Math.max(min, Math.min(max, target));
    }

    @SuppressLint("WrongConstant")
    public static void startApp(Context context, App app) {
        try {
            if (BaseUtils.isInstalled(context, app.getPackageName())) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setFlags(268435456);
                intent.setClassName(app.getPackageName(), app.getClassName());
                context.startActivity(intent);
                if (app.getPackageName().equals(context.getPackageName())) {
                    try {
                        ((BaseApplication) context.getApplicationContext()).putEvents(BaseApplication.EVENTS_NAME_CLICK_ICON_SETTINGS);
                    } catch (Exception e) {
                    }
                }
            } else {
                if (Home.launcher != null) {
                    Home.launcher.resetFirstShowPopup();
                }
                BaseUtils.gotoStore(context, app.getPackageName());
            }
            Home.consumeNextResume = true;
            ((MainApplication) context.getApplicationContext()).dbHelper.addRecent(app.getPackageName());
        } catch (Exception e2) {
            toast(context, (int) R.string.toast_app_uninstalled);
        }
    }

    public static void startApp(Context context, Intent intent) {
        try {
            if (BaseUtils.isInstalled(context, intent.getComponent().getPackageName())) {
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(intent.getComponent().getPackageName());
                    if (launchIntent != null) {
                        context.startActivity(launchIntent);
                    }
                }
                if (intent.getComponent().getPackageName().equals(context.getPackageName())) {
                    try {
                        ((BaseApplication) context.getApplicationContext()).putEvents(BaseApplication.EVENTS_NAME_CLICK_ICON_SETTINGS);
                    } catch (Exception e2) {
                    }
                }
            } else {
                if (Home.launcher != null) {
                    Home.launcher.resetFirstShowPopup();
                }
                BaseUtils.gotoStore(context, intent.getComponent().getPackageName());
            }
            Home.consumeNextResume = true;
            ((MainApplication) context.getApplicationContext()).dbHelper.addRecent(intent.getComponent().getPackageName());
        } catch (Exception e3) {
            toast(context, (int) R.string.toast_app_uninstalled);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Point convertPoint(Point fromPoint, View fromView, View toView) {
        int[] fromCoord = new int[2];
        int[] toCoord = new int[2];
        fromView.getLocationOnScreen(fromCoord);
        toView.getLocationOnScreen(toCoord);
        return new Point((fromCoord[0] - toCoord[0]) + fromPoint.x, (fromCoord[1] - toCoord[1]) + fromPoint.y);
    }

    public static void vibrate(View view) {
        view.performHapticFeedback(0);
    }

    public static void print(Object o) {
        if (o != null) {
            Log.d("Hey", o.toString());
        }
    }

    public static void print(Object... o) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : o) {
            sb.append(obj.toString()).append("  ");
        }
        Log.d("Hey", sb.toString());
    }

    @SuppressLint("WrongConstant")
    public static boolean isIntentActionAvailable(Context context, String action) {
        return context.getPackageManager().queryIntentActivities(new Intent(action), 65536).size() > 0;
    }

    public static String getIntentAsString(Intent intent) {
        if (intent == null) {
            return "";
        }
        return intent.toUri(0);
    }

    public static Intent getIntentFromString(String string) {
        if (string == null || string.isEmpty()) {
            return new Intent();
        }
        try {
            Intent intent = new Intent();
            return Intent.parseUri(string, 0);
        } catch (URISyntaxException e) {
            return new Intent();
        }
    }

    public static IconProvider getIcon(Context context, Item item) {
        if (item == null) {
            return null;
        }
        return item.getIconProvider();
    }

    public static Drawable getIcon(Context context, String filename) {
        if (filename == null || context == null) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir() + "/icons/" + filename + ".png");
        if (bitmap != null) {
            return new BitmapDrawable(context.getResources(), bitmap);
        }
        return null;
    }

    public static void saveIcon(Context context, Bitmap icon, String filename) {
        File directory = new File(context.getFilesDir() + "/icons");
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(context.getFilesDir() + "/icons/" + filename + ".png");
        removeIcon(context, filename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            icon.compress(CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void removeIcon(Context context, String filename) {
        File file = new File(context.getFilesDir() + "/icons/" + filename + ".png");
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static OnTouchListener getItemOnTouchListener(Item item, ItemGestureCallback itemGestureCallback) {
        return new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Home.touchX = (int) motionEvent.getX();
                Home.touchY = (int) motionEvent.getY();
                return false;
            }
        };
    }

    public static <A extends App> List<A> getRemovedApps(List<A> oldApps, List<A> newApps) {
        List<A> removed = new ArrayList();
        if (oldApps.size() != 0) {
            for (int i = 0; i < oldApps.size(); i++) {
                if (!newApps.contains(oldApps.get(i))) {
                    removed.add(oldApps.get(i));
                    break;
                }
            }
        }
        return removed;
    }
}
