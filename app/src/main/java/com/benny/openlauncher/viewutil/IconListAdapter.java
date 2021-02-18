package com.benny.openlauncher.viewutil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.benny.openlauncher.R;

import java.util.List;

public class IconListAdapter extends BaseAdapter {
    private Context c;
    private List<Integer> icons;
    private List<String> labels;

    public IconListAdapter(Context c, List labels, List icons) {
        this.c = c;
        this.labels = labels;
        this.icons = icons;
    }

    public int getCount() {
        return this.labels.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(this.c).inflate(R.layout.item_minibar, parent, false);
        } else {
            view = convertView;
        }
        TextView tv = (TextView) view.findViewById(R.id.tv);
        ((ImageView) view.findViewById(R.id.iv)).setImageResource(((Integer) this.icons.get(position)).intValue());
        return view;
    }
}
