package com.benny.openlauncher.core.util.more;

import android.content.Context;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.BaseIconProvider;
import com.benny.openlauncher.util.AppManager;

/**
 * Created by Phí Văn Tuấn on 31/7/2018.
 */

public class LiveWallPaper extends AppManager.App {
    private Context context;

    public LiveWallPaper(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public String getClassName() {
        return context.getResources().getString(R.string.livewallpaper);
    }

    @Override
    public BaseIconProvider getIconProvider() {
        return Setup.imageLoader().createIconProvider((int) R.drawable.ic_livewall);
    }

    @Override
    public String getLabel() {
        return context.getResources().getString(R.string.label_livewall);
    }

    @Override
    public String getPackageName() {
        return context.getResources().getString(R.string.livewallpaper);
    }
}
