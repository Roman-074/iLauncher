package com.benny.openlauncher.activity;

import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.benny.openlauncher.R;
import com.benny.openlauncher.fragment.HideAppsSelectionFragment;
import com.benny.openlauncher.util.AppManager;
import java.util.ArrayList;
import java.util.List;

public class HideAppsSelectionActivity extends AppCompatActivity {

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList;
        private final List<String> mFragmentTitleList;

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            this.mFragmentList = new ArrayList();
            this.mFragmentTitleList = new ArrayList();
        }

        public Fragment getItem(int position) {
            return (Fragment) this.mFragmentList.get(position);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return (CharSequence) this.mFragmentTitleList.get(position);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("my", "onCreate: HideAppsSelectionActivity");
        setContentView((int) R.layout.activity_hide_apps);
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(Integer.MIN_VALUE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setupViewPager((ViewPager) findViewById(R.id.viewpager));
        setTitle(getString(R.string.pref_title__hide_apps));
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    protected void onDestroy() {
        AppManager.getInstance(this).recreateAfterGettingApps = true;
        AppManager.getInstance(this).init();
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HideAppsSelectionFragment(), String.valueOf("Skip"));
        viewPager.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
