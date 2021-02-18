package com.benny.openlauncher.viewutil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.TextDrawable.IBuilder;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.Home;
import com.benny.openlauncher.core.util.Tool;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class QuickCenterItem {

    public static class ContactContent {
        public Intent data;
        public Bitmap icon;
        public String name;
        public String number;

        public ContactContent(String name, String number, Intent data, Bitmap icon) {
            this.name = name;
            this.data = data;
            this.icon = icon;
            this.number = number;
        }

        public boolean equals(Object obj) {
            return (obj instanceof ContactContent) && this.data.equals(((ContactContent) obj).data);
        }
    }

    public static class ContactItem extends AbstractItem<ContactItem, ContactItem.ViewHolder> {
        private ContactContent info;

        class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            ImageView imageView;

            ViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view;
            }
        }

        public ContactItem(ContactContent info) {
            this.info = info;
        }

        public int getType() {
            return 0;
        }

        public int getLayoutRes() {
            return R.layout.view_contact;
        }

        public void bindView(ViewHolder viewHolder, List payloads) {
            super.bindView(viewHolder, payloads);
            Tool.print(Boolean.valueOf(this.info.icon == null));
            if (this.info.icon != null) {
                viewHolder.imageView.setImageDrawable(new RoundDrawable(this.info.icon));
            } else {
                int color1 = ColorGenerator.MATERIAL.getRandomColor();
                IBuilder builder = TextDrawable.builder().round();
                String name = (this.info.name == null || this.info.name.isEmpty()) ? this.info.number : this.info.name;
                viewHolder.imageView.setImageDrawable(builder.build(name.substring(0, 1), color1));
            }
            viewHolder.imageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (Home.launcher != null) {
                        if (ContextCompat.checkSelfPermission(view.getContext(), "android.permission.CALL_PHONE") == 0) {
                            Home.launcher.startActivity(ContactItem.this.info.data);
                            return;
                        }
                        Tool.toast(view.getContext(), "Unable to call the person without Manifest.permission.CALL_PHONE granted");
                        ActivityCompat.requestPermissions(Home.launcher, new String[]{"android.permission.CALL_PHONE"}, com.benny.openlauncher.core.activity.Home.REQUEST_PERMISSION_CALL);
                    }
                }
            });
        }

        public ViewHolder getViewHolder(View v) {
            return new ViewHolder(v);
        }
    }

    public static class NoteContent {
        public String content;
        public String date;

        public NoteContent(String date, String content) {
            this.content = content;
            this.date = date;
        }

        public boolean equals(Object obj) {
            return (obj instanceof NoteContent) && this.content.equals(((NoteContent) obj).content) && this.date.equals(((NoteContent) obj).date);
        }
    }

    public static class NoteItem extends AbstractItem<NoteItem, NoteItem.ViewHolder> {
        private FastItemAdapter<NoteItem> adapter;
        public String date;
        public String description;

        protected class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            protected TextView date;
            protected TextView description;

            public ViewHolder(View view) {
                super(view);
                this.date = (TextView) view.findViewById(R.id.tv);
                this.description = (TextView) view.findViewById(R.id.tv2);
            }
        }

        public NoteItem(String date, String description, FastItemAdapter<NoteItem> adapter) {
            this.date = date;
            this.description = description;
            this.adapter = adapter;
        }

        public int getType() {
            return R.id.item_note;
        }

        public int getLayoutRes() {
            return R.layout.item_note;
        }

        public void bindView(final ViewHolder viewHolder, List payloads) {
            super.bindView(viewHolder, payloads);
            viewHolder.date.setText(Html.fromHtml("<b><big>Note</big></b><br><small>" + this.date + "</small>"));
            viewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    if (view.getContext() instanceof Home) {
                        NoteItem.this.adapter.remove(viewHolder.getAdapterPosition());
                    }
                    return true;
                }
            });
            viewHolder.description.setText(Html.fromHtml("<big>" + this.description + "</big>"));
        }

        public ViewHolder getViewHolder(View v) {
            return new ViewHolder(v);
        }
    }
}
