package com.benny.openlauncher.core.model;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.FastItem.AppItem;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.DragAction.Action;
import com.benny.openlauncher.core.widget.AppDrawerVertical;
import com.benny.openlauncher.core.widget.AppItemView;
import com.benny.openlauncher.core.widget.AppItemView.Builder;
import com.benny.openlauncher.core.widget.AppItemView.LongPressCallBack;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class DrawerAppItem extends AbstractItem<DrawerAppItem, DrawerAppItem.ViewHolder> implements AppItem<DrawerAppItem, DrawerAppItem.ViewHolder> {
    private App app;
    private LongPressCallBack onLongClickCallback = new LongPressCallBack() {
        public boolean readyForDrag(View view) {
            return Setup.appSettings().getDesktopStyle() != 1;
        }

        public void afterDrag(View view) {
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppItemView appItemView;
        Builder builder = new Builder(this.appItemView, Setup.appSettings().getDrawerIconSize()).withOnTouchGetPosition(null, null).setLabelVisibility(Setup.appSettings().isDrawerShowLabel()).setTextColor(Setup.appSettings().getDrawerLabelColor()).setFastAdapterItem();

        ViewHolder(View itemView) {
            super(itemView);
            this.appItemView = (AppItemView) itemView;
            this.appItemView.setTargetedWidth(AppDrawerVertical.itemWidth);
            this.appItemView.setTargetedHeightPadding(AppDrawerVertical.itemHeightPadding);

        }
    }

    public DrawerAppItem(App app) {
        this.app = app;
    }

    public int getType() {
        return R.id.id_adapter_drawer_app_item;
    }

    public int getLayoutRes() {
        return R.layout.item_app;
    }

    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public App getApp() {
        return this.app;
    }

    public void bindView(ViewHolder holder, List payloads) {
        holder.builder.setAppItem(this.app).withOnLongClick(this.app, Action.APP_DRAWER, this.onLongClickCallback);
        holder.appItemView.load();
        super.bindView(holder, payloads);
    }

    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.appItemView.reset();
    }
}
