package com.benny.openlauncher.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.interfaces.App;
import java.util.ArrayList;

public class SelectIconAdapter extends Adapter<android.support.v7.widget.RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<App> list;
    private SelectIconAdapterListener selectIconAdapterListener;

    class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        private ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (SelectIconAdapter.this.selectIconAdapterListener != null) {
                        SelectIconAdapter.this.selectIconAdapterListener.onClick(ViewHolder.this.getAdapterPosition());
                    }
                }
            });
            this.ivIcon = (ImageView) itemView.findViewById(R.id.select_icon_item_iv);
        }
    }

    public SelectIconAdapter(Context context, ArrayList<App> list, SelectIconAdapterListener selectIconAdapterListener) {
        this.context = context;
        this.list = list;
        this.selectIconAdapterListener = selectIconAdapterListener;
    }

    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_icon_activity_item, parent, false));
    }

    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).ivIcon.setImageDrawable(((App) this.list.get(position)).getIconProvider().getDrawableSynchronously(-1));
    }

    public int getItemCount() {
        return this.list.size();
    }
}
