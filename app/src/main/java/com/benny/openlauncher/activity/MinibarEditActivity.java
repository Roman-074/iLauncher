package com.benny.openlauncher.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
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

public class MinibarEditActivity extends AppCompatActivity implements ItemTouchCallback {
    private FastItemAdapter<Item> adapter;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tb)
    Toolbar tb;

    public static class Item extends AbstractItem<Item, Item.ViewHolder> {
        public boolean edited;
        public boolean enable;
        public final long id;
        public final ActionDisplayItem item;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox cb;
            ImageView iv;
            TextView tv;
            TextView tv2;

            public ViewHolder(View itemView) {
                super(itemView);
                this.tv = (TextView) itemView.findViewById(R.id.tv);
                this.tv2 = (TextView) itemView.findViewById(R.id.tv2);
                this.iv = (ImageView) itemView.findViewById(R.id.iv);
                this.cb = (CheckBox) itemView.findViewById(R.id.cb);
            }
        }

        public Item(long id, ActionDisplayItem item, boolean enable) {
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
            holder.tv.setText(this.item.label.toString());
            holder.tv2.setText(this.item.description);
            holder.iv.setImageResource(this.item.icon);
            holder.cb.setChecked(this.enable);
            holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Item.this.edited = true;
                    Item.this.enable = b;
                }
            });
            super.bindView(holder, payloads);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("my", "onCreate: MinibarEditActivity");
        setContentView((int) R.layout.activity_minibar_edit);
        ButterKnife.bind((Activity) this);
        setSupportActionBar(this.tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.minibar);
        this.adapter = new FastItemAdapter();
        new ItemTouchHelper(new SimpleDragCallback((ItemTouchCallback) this)).attachToRecyclerView(this.rv);
        this.rv.setLayoutManager(new LinearLayoutManager(this));
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
            fastItemAdapter.add(new Item(j, actionItemFromString, z));
            i++;
        }
        setResult(-1);
    }

    protected void onPause() {
        ArrayList<String> minibarArrangement = new ArrayList();
        for (Item item : this.adapter.getAdapterItems()) {
            if (item.enable) {
                minibarArrangement.add("0" + item.item.label.toString());
            } else {
                minibarArrangement.add("1" + item.item.label.toString());
            }
        }
        AppSettings.get().setMinibarArrangement(minibarArrangement);
        super.onPause();
    }

    protected void onStop() {
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
