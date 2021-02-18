package com.benny.openlauncher.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.util.AppSettings;
import com.benny.openlauncher.util.LauncherAction;
import com.benny.openlauncher.util.LauncherAction.ActionDisplayItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
@SuppressLint("ResourceType")
public class MinibarEditFragment extends Fragment implements ItemTouchCallback {
    private FastItemAdapter<AppItem> adapter;
    private Context context;
    @BindView(2131296657)
    RecyclerView rv;

    public static class AppItem extends AbstractItem<AppItem, AppItem.ViewHolder> {
        public boolean edited;
        public boolean enable;
        public final long id;
        public final ActionDisplayItem item;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkbox;
            TextView description;
            ImageView icon;
            TextView label;

            public ViewHolder(View itemView) {
                super(itemView);
                this.label = (TextView) itemView.findViewById(R.id.tv);
                this.description = (TextView) itemView.findViewById(R.id.tv2);
                this.icon = (ImageView) itemView.findViewById(R.id.iv);
                this.checkbox = (CheckBox) itemView.findViewById(R.id.cb);
            }
        }

        public AppItem(long id, ActionDisplayItem item, boolean enable) {
            this.id = id;
            this.item = item;
            this.enable = enable;
        }

        public int getType() {
            return 0;
        }

        public int getLayoutRes() {
            return R.layout.item_minibar_edit;
        }

        public ViewHolder getViewHolder(View v) {
            return new ViewHolder(v);
        }

        public void bindView(ViewHolder holder, List payloads) {
            holder.label.setText(this.item.label.toString());
            holder.description.setText(this.item.description);
            holder.icon.setImageResource(this.item.icon);
            holder.checkbox.setChecked(this.enable);
            holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    AppItem.this.edited = true;
                    AppItem.this.enable = b;
                }
            });
            super.bindView(holder, payloads);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind((Activity) this.context);
        this.adapter = new FastItemAdapter();
        new ItemTouchHelper(new SimpleDragCallback((ItemTouchCallback) this)).attachToRecyclerView(this.rv);
        this.rv.setLayoutManager(new LinearLayoutManager(this.context));
        this.rv.setAdapter(this.adapter);
        int i = 0;
        Iterator it = AppSettings.get().getMinibarArrangement().iterator();
        while (it.hasNext()) {
            boolean z;
            String act = (String) it.next();
            FastItemAdapter fastItemAdapter = this.adapter;
            long j = (long) i;
            ActionDisplayItem actionItemFromString = LauncherAction.getActionItemFromString(act.substring(1));
            if (act.charAt(0) == '0') {
                z = true;
            } else {
                z = false;
            }
            fastItemAdapter.add(new AppItem(j, actionItemFromString, z));
            i++;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_minibar_edit, container, false);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    public void onPause() {
        ArrayList<String> minibarArrangement = new ArrayList();
        for (AppItem item : this.adapter.getAdapterItems()) {
            if (item.enable) {
                minibarArrangement.add("0" + item.item.label.toString());
            } else {
                minibarArrangement.add("1" + item.item.label.toString());
            }
        }
        AppSettings.get().setMinibarArrangement(minibarArrangement);
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public boolean itemTouchOnMove(int oldPosition, int newPosition) {
        Collections.swap(this.adapter.getAdapterItems(), oldPosition, newPosition);
        this.adapter.notifyAdapterDataSetChanged();
        return false;
    }

    public void itemTouchDropped(int i, int i1) {
    }
}
