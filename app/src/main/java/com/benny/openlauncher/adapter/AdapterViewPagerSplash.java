package com.benny.openlauncher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.benny.openlauncher.activity.SplashActivity;
import com.benny.openlauncher.fragment.SplashFragment;
import com.benny.openlauncher.util.Constant;
import java.util.HashMap;

public class AdapterViewPagerSplash extends FragmentStatePagerAdapter {
    private HashMap<Integer, Fragment> mapFragment = new HashMap();
    private SplashActivity splashActivity;

    public AdapterViewPagerSplash(FragmentManager fm, SplashActivity splashActivity) {
        super(fm);
        this.splashActivity = splashActivity;
    }

    public Fragment getItemAt(int i) {
        return getItem(i);
    }

    public Fragment getItem(int i) {
        if (this.mapFragment.containsKey(Integer.valueOf(i))) {
            return (Fragment) this.mapFragment.get(Integer.valueOf(i));
        }
        Fragment fragment = SplashFragment.newInstance(Constant.BG[i]);
        this.mapFragment.put(Integer.valueOf(i), fragment);
        return fragment;
    }

    public int getCount() {
        return Constant.BG.length;
    }
}
