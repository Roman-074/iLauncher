package com.benny.openlauncher.customview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class LockScreenAdapter extends PagerAdapter {
    private LockScreenCode lockScreenCode;
    private LockScreenMain lockScreenMain;

    public LockScreenMain getLockScreenMain() {
        return this.lockScreenMain;
    }

    public LockScreenCode getLockScreenCode() {
        return this.lockScreenCode;
    }

    public LockScreenAdapter(Context context) {
        this.lockScreenMain = new LockScreenMain(context);
        this.lockScreenCode = new LockScreenCode(context);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(this.lockScreenCode, 0);
        container.addView(this.lockScreenMain, 1);
        if (position == 0) {
            return this.lockScreenCode;
        }
        return this.lockScreenMain;
    }

    public int getCount() {
        return 2;
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
