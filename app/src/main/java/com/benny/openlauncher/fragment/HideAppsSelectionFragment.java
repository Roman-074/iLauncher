package com.benny.openlauncher.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.model.AppInfo;
import com.benny.openlauncher.util.AppManager;
import com.benny.openlauncher.util.AppManager.App;
import com.benny.openlauncher.util.AppSettings;
import java.util.ArrayList;

public class HideAppsSelectionFragment extends Fragment {
    static final boolean $assertionsDisabled = (!HideAppsSelectionFragment.class.desiredAssertionStatus());
    private static final boolean DEBUG = true;
    private static final String TAG = "RequestActivity";
    private AppAdapter appInfoAdapter;
    private ListView grid;
    private ArrayList<String> list_activities = new ArrayList();
    private ArrayList<AppInfo> list_activities_final = new ArrayList();
    @SuppressLint({"StaticFieldLeak"})
    private ViewSwitcher switcherLoad;
    private AsyncWorkerList taskList = new AsyncWorkerList();
    private Typeface tf;
    private ViewSwitcher viewSwitcher;

    private class AppAdapter extends ArrayAdapter<AppInfo> {
        private AppAdapter(Context context, ArrayList<AppInfo> adapterArrayList) {
            super(context, R.layout.request_item_list, adapterArrayList);
        }

        @SuppressLint("WrongConstant")
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = ((LayoutInflater) HideAppsSelectionFragment.this.getActivity().getSystemService("layout_inflater")).inflate(R.layout.request_item_list, parent, false);
                holder = new ViewHolder();
                holder.apkIcon = (ImageView) convertView.findViewById(R.id.IVappIcon);
                holder.apkName = (TextView) convertView.findViewById(R.id.TVappName);
                holder.apkPackage = (TextView) convertView.findViewById(R.id.TVappPackage);
                holder.checker = (CheckBox) convertView.findViewById(R.id.CBappSelect);
                holder.switcherChecked = (ViewSwitcher) convertView.findViewById(R.id.viewSwitcherChecked);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AppInfo appInfo = (AppInfo) getItem(position);
            holder.apkPackage.setText(appInfo.getCode());
            holder.apkPackage.setTypeface(HideAppsSelectionFragment.this.tf);
            holder.apkName.setText(appInfo.getName());
            holder.apkIcon.setImageDrawable(appInfo.getImage());
            holder.switcherChecked.setInAnimation(null);
            holder.switcherChecked.setOutAnimation(null);
            holder.checker.setChecked(appInfo.isSelected());
            if (appInfo.isSelected()) {
                if (holder.switcherChecked.getDisplayedChild() == 0) {
                    holder.switcherChecked.showNext();
                }
            } else if (holder.switcherChecked.getDisplayedChild() == 1) {
                holder.switcherChecked.showPrevious();
            }
            return convertView;
        }
    }

    public class AsyncWorkerList extends AsyncTask<String, Integer, String> {
        private AsyncWorkerList() {
        }

        protected void onPreExecute() {
            HideAppsSelectionFragment.this.list_activities.addAll(AppSettings.get().getHiddenAppsList());
            super.onPreExecute();
        }

        protected String doInBackground(String... arg0) {
            try {
                HideAppsSelectionFragment.this.prepareData();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            HideAppsSelectionFragment.this.populateView();
            HideAppsSelectionFragment.this.switcherLoad.showNext();
            super.onPostExecute(result);
        }
    }

    private class ViewHolder {
        ImageView apkIcon;
        TextView apkName;
        TextView apkPackage;
        CheckBox checker;
        ViewSwitcher switcherChecked;

        private ViewHolder() {
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.request, container, false);
        this.switcherLoad = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcherLoadingMain);
        ((FloatingActionButton) rootView.findViewById(R.id.fab_rq)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                HideAppsSelectionFragment.this.confirmSelection();
            }
        });
        if (this.taskList.getStatus() == Status.PENDING) {
            this.taskList.execute(new String[0]);
        }
        if (this.taskList.getStatus() == Status.FINISHED) {
            new AsyncWorkerList().execute(new String[0]);
        }
        return rootView;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    private void confirmSelection() {
        Thread actionSend_Thread = new Thread() {
            public void run() {
                int selected = 0;
                ArrayList<String> hiddenList = new ArrayList();
                for (int i = 0; i < HideAppsSelectionFragment.this.list_activities_final.size(); i++) {
                    if (((AppInfo) HideAppsSelectionFragment.this.list_activities_final.get(i)).isSelected()) {
                        hiddenList.add(((AppInfo) HideAppsSelectionFragment.this.list_activities_final.get(i)).getCode());
                        selected++;
                    }
                }
                if (selected == 0) {
                    Snackbar.make(HideAppsSelectionFragment.this.grid, (int) R.string.request_toast, -2).setAction(HideAppsSelectionFragment.this.getString(R.string.ok), new OnClickListener() {
                        public void onClick(View view) {
                            HideAppsSelectionFragment.this.getActivity().finish();
                        }
                    }).show();
                    return;
                }
                AppSettings.get().setHiddenAppsList(hiddenList);
                HideAppsSelectionFragment.this.getActivity().finish();
            }
        };
        if (!actionSend_Thread.isAlive()) {
            actionSend_Thread.start();
        }
    }

    private void prepareData() {
        for (App app : AppManager.getInstance(getContext()).getNonFilteredApps()) {
            this.list_activities_final.add(new AppInfo(app.getPackageName() + "/" + app.getClassName(), app.label, app.iconProvider.getDrawableSynchronously(-1), this.list_activities.contains(app.getPackageName() + "/" + app.getClassName())));
        }
    }

    private void populateView() {
        this.grid = (ListView) getActivity().findViewById(R.id.appgrid);
        if ($assertionsDisabled || this.grid != null) {
            this.grid.setFastScrollEnabled(true);
            this.grid.setFastScrollAlwaysVisible(false);
            this.appInfoAdapter = new AppAdapter(getActivity(), this.list_activities_final);
            this.grid.setAdapter(this.appInfoAdapter);
            this.grid.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> AdapterView, View view, int position, long row) {
                    AppInfo appInfo = (AppInfo) AdapterView.getItemAtPosition(position);
                    CheckBox checker = (CheckBox) view.findViewById(R.id.CBappSelect);
                    ViewSwitcher icon = (ViewSwitcher) view.findViewById(R.id.viewSwitcherChecked);
                    checker.toggle();
                    appInfo.setSelected(checker.isChecked());
                    if (appInfo.isSelected()) {
                        Log.v(HideAppsSelectionFragment.TAG, "Selected App: " + appInfo.getName());
                        if (icon.getDisplayedChild() == 0) {
                            icon.showNext();
                            return;
                        }
                        return;
                    }
                    Log.v(HideAppsSelectionFragment.TAG, "Deselected App: " + appInfo.getName());
                    if (icon.getDisplayedChild() == 1) {
                        icon.showPrevious();
                    }
                }
            });
            return;
        }
        throw new AssertionError();
    }
}
