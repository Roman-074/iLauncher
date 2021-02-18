package com.benny.openlauncher.util;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;

import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.util.AppManager.App;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class IconPackHelper {
    public static void themePacs(AppManager appManager, int iconSize, String resPacName, List<App> apps) {
        Resources themeRes = null;
        int intresiconback = 0;
        int intresiconfront = 0;
        int intresiconmask = 0;
        new Paint(2).setAntiAlias(true);
        new Paint(2).setAntiAlias(true);
        Paint paint = new Paint(2);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
        if (resPacName.compareTo("") != 0) {
            try {
                themeRes = appManager.getPackageManager().getResourcesForApplication(resPacName);
            } catch (Exception e) {
            }
            if (themeRes != null) {
                String[] backAndMaskAndFront = getIconBackAndMaskResourceName(themeRes, resPacName);
                if (backAndMaskAndFront[0] != null) {
                    intresiconback = themeRes.getIdentifier(backAndMaskAndFront[0], "drawable", resPacName);
                }
                if (backAndMaskAndFront[1] != null) {
                    intresiconmask = themeRes.getIdentifier(backAndMaskAndFront[1], "drawable", resPacName);
                }
                if (backAndMaskAndFront[2] != null) {
                    intresiconfront = themeRes.getIdentifier(backAndMaskAndFront[2], "drawable", resPacName);
                }
            }
        }
        Options uniformOptions = new Options();
        uniformOptions.inScaled = false;
        uniformOptions.inDither = false;
        uniformOptions.inPreferredConfig = Config.ARGB_8888;
        float scaleFactor = getScaleFactor(themeRes, resPacName);
        Bitmap back = null;
        Bitmap mask = null;
        Bitmap front = null;
        if (!(resPacName.compareTo("") == 0 || themeRes == null)) {
            if (intresiconback != 0) {
                try {
                    back = BitmapFactory.decodeResource(themeRes, intresiconback, uniformOptions);
                } catch (Exception e2) {
                }
            }
            if (intresiconmask != 0) {
                try {
                    mask = BitmapFactory.decodeResource(themeRes, intresiconmask, uniformOptions);
                } catch (Exception e3) {
                }
            }
            if (intresiconfront != 0) {
                try {
                    front = BitmapFactory.decodeResource(themeRes, intresiconfront, uniformOptions);
                } catch (Exception e4) {
                }
            }
        }
        Options options = new Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.RGB_565;
        options.inDither = true;
        for (int I = 0; I < apps.size(); I++) {
            if (themeRes != null) {
                int intres = 0;
                String iconResource = getResourceName(themeRes, resPacName, "ComponentInfo{" + ((App) apps.get(I)).packageName + "/" + ((App) apps.get(I)).className + "}");
                if (iconResource != null) {
                    intres = themeRes.getIdentifier(iconResource, "drawable", resPacName);
                }
                if (intres != 0) {
                    ((App) apps.get(I)).iconProvider = Setup.imageLoader().createIconProvider(new BitmapDrawable(BitmapFactory.decodeResource(themeRes, intres, uniformOptions)));
                } else {
                    try {
                        Bitmap orig = Bitmap.createBitmap(((App) apps.get(I)).getIconProvider().getDrawableSynchronously(-1).getIntrinsicWidth(), ((App) apps.get(I)).getIconProvider().getDrawableSynchronously(-1).getIntrinsicHeight(), Config.ARGB_8888);
                        ((App) apps.get(I)).getIconProvider().getDrawableSynchronously(-1).setBounds(0, 0, ((App) apps.get(I)).getIconProvider().getDrawableSynchronously(-1).getIntrinsicWidth(), ((App) apps.get(I)).getIconProvider().getDrawableSynchronously(-1).getIntrinsicHeight());
                        ((App) apps.get(I)).getIconProvider().getDrawableSynchronously(-1).draw(new Canvas(orig));
                        Bitmap scaledOrig = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
                        Bitmap scaledBitmap = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
                        Canvas canvas = new Canvas(scaledBitmap);
                        if (back != null) {
                            canvas.drawBitmap(back, getResizedMatrix(back, iconSize, iconSize), paint);
                        }
                        Canvas canvas2 = new Canvas(scaledOrig);
                        orig = getResizedBitmap(orig, (int) (((float) iconSize) * scaleFactor), (int) (((float) iconSize) * scaleFactor));
                        canvas2.drawBitmap(orig, (float) ((scaledOrig.getWidth() - (orig.getWidth() / 2)) - (scaledOrig.getWidth() / 2)), (float) ((scaledOrig.getWidth() - (orig.getWidth() / 2)) - (scaledOrig.getWidth() / 2)), paint);
                        if (mask != null) {
                            canvas2.drawBitmap(mask, getResizedMatrix(mask, iconSize, iconSize), paint);
                        }
                        if (back != null) {
                            canvas.drawBitmap(getResizedBitmap(scaledOrig, iconSize, iconSize), 0.0f, 0.0f, paint);
                        } else {
                            canvas.drawBitmap(getResizedBitmap(scaledOrig, iconSize, iconSize), 0.0f, 0.0f, paint);
                        }
                        if (front != null) {
                            canvas.drawBitmap(front, getResizedMatrix(front, iconSize, iconSize), paint);
                        }
                        ((App) apps.get(I)).iconProvider = Setup.imageLoader().createIconProvider(new BitmapDrawable(appManager.getContext().getResources(), scaledBitmap));
                    } catch (Exception e5) {
                    }
                }
            }
        }
    }

    private static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / ((float) width);
        float scaleHeight = ((float) newHeight) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    private static Matrix getResizedMatrix(Bitmap bm, int newHeight, int newWidth) {
        float scaleWidth = ((float) newWidth) / ((float) bm.getWidth());
        float scaleHeight = ((float) newHeight) / ((float) bm.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return matrix;
    }

    private static float getScaleFactor(Resources res, String string) {
        float scaleFactor = Tool.DEFAULT_BACKOFF_MULT;
        XmlResourceParser xrp = null;
        XmlPullParser xpp = null;
        try {
            int n = res.getIdentifier("appfilter", "xml", string);
            if (n != 0) {
                xrp = res.getXml(n);
                System.out.println(n);
            } else {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setValidating(false);
                xpp = factory.newPullParser();
                xpp.setInput(res.getAssets().open("appfilter.xml"), null);
            }
            if (n != 0) {
                while (xrp.getEventType() != 1 && scaleFactor == Tool.DEFAULT_BACKOFF_MULT) {
                    if (xrp.getEventType() == 2) {
                        try {
                            if (xrp.getName().equals("scale")) {
                                scaleFactor = Float.parseFloat(xrp.getAttributeValue(0));
                            }
                        } catch (Exception e) {
                        }
                    }
                    xrp.next();
                }
                return scaleFactor;
            }
            while (xpp.getEventType() != 1 && scaleFactor == Tool.DEFAULT_BACKOFF_MULT) {
                if (xpp.getEventType() == 2) {
                    try {
                        if (xpp.getName().equals("scale")) {
                            scaleFactor = Float.parseFloat(xpp.getAttributeValue(0));
                        }
                    } catch (Exception e2) {
                    }
                }
                xpp.next();
            }
            return scaleFactor;
        } catch (Exception e3) {
            System.out.println(e3);
            return scaleFactor;
        }
    }

    private static String getResourceName(Resources res, String string, String componentInfo) {
        String resource = null;
        XmlResourceParser xrp = null;
        XmlPullParser xpp = null;
        try {
            int n = res.getIdentifier("appfilter", "xml", string);
            if (n != 0) {
                xrp = res.getXml(n);
            } else {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setValidating(false);
                xpp = factory.newPullParser();
                xpp.setInput(res.getAssets().open("appfilter.xml"), null);
            }
            if (n != 0) {
                while (xrp.getEventType() != 1 && resource == null) {
                    if (xrp.getEventType() == 2) {
                        try {
                            if (xrp.getName().equals("item") && xrp.getAttributeValue(0).compareTo(componentInfo) == 0) {
                                resource = xrp.getAttributeValue(1);
                            }
                        } catch (Exception e) {
                        }
                    }
                    xrp.next();
                }
                return resource;
            }
            while (xpp.getEventType() != 1 && resource == null) {
                if (xpp.getEventType() == 2) {
                    try {
                        if (xpp.getName().equals("item") && xpp.getAttributeValue(0).compareTo(componentInfo) == 0) {
                            resource = xpp.getAttributeValue(1);
                        }
                    } catch (Exception e2) {
                    }
                }
                xpp.next();
            }
            return resource;
        } catch (Exception e3) {
            System.out.println(e3);
            return resource;
        }
    }

    private static String[] getIconBackAndMaskResourceName(Resources res, String packageName) {
        String[] resource = new String[3];
        XmlResourceParser xrp = null;
        XmlPullParser xpp = null;
        try {
            int n = res.getIdentifier("appfilter", "xml", packageName);
            if (n != 0) {
                xrp = res.getXml(n);
            } else {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setValidating(false);
                xpp = factory.newPullParser();
                xpp.setInput(res.getAssets().open("appfilter.xml"), null);
            }
            String s;
            if (n != 0) {
                while (xrp.getEventType() != 1 && (resource[0] == null || resource[1] == null || resource[2] == null)) {
                    if (xrp.getEventType() == 2) {
                        try {
                            s = xrp.getName();
                            if (s.equals("iconback")) {
                                resource[0] = xrp.getAttributeValue(0);
                            }
                            if (s.equals("iconmask")) {
                                resource[1] = xrp.getAttributeValue(0);
                            }
                            if (s.equals("iconupon")) {
                                resource[2] = xrp.getAttributeValue(0);
                            }
                        } catch (Exception e) {
                        }
                    }
                    xrp.next();
                }
                return resource;
            }
            while (xpp.getEventType() != 1 && (resource[0] == null || resource[1] == null || resource[2] == null)) {
                if (xpp.getEventType() == 2) {
                    try {
                        s = xpp.getName();
                        if (s.equals("iconback")) {
                            resource[0] = xpp.getAttributeValue(0);
                        }
                        if (s.equals("iconmask")) {
                            resource[1] = xpp.getAttributeValue(0);
                        }
                        if (s.equals("iconupon")) {
                            resource[2] = xpp.getAttributeValue(0);
                        }
                    } catch (Exception e2) {
                    }
                }
                xpp.next();
            }
            return resource;
        } catch (Exception e3) {
            System.out.println(e3);
        }
        return resource;
    }
}
