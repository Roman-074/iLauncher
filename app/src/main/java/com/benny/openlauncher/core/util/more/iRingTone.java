package com.benny.openlauncher.core.util.more;

import android.content.Context;

import com.benny.openlauncher.R;

import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.BaseIconProvider;
import com.benny.openlauncher.util.AppManager;

/**
 * Created by Phí Văn Tuấn on 31/7/2018.
 */

public class iRingTone extends AppManager.App {
    private Context context;

    public iRingTone(Context context) {
        super(context);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String getClassName() {
        return context.getResources().getString(R.string.iRingtone);
    }

    @Override
    public BaseIconProvider getIconProvider() {
        return Setup.imageLoader().createIconProvider( R.drawable.bell_iphone_for_android);
    }

    @Override
    public String getLabel() {
        return context.getResources().getString(R.string.label_iRingtone);
    }

    @Override
    public String getPackageName() {
        return context.getResources().getString(R.string.iRingtone);
    }
}
