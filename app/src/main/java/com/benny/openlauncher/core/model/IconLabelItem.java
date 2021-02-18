package com.benny.openlauncher.core.model;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.core.interfaces.FastItem.DesktopOptionsItem;
import com.benny.openlauncher.core.interfaces.FastItem.LabelItem;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.util.BaseIconProvider;
import com.benny.openlauncher.core.util.Tool;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.List;

public class IconLabelItem extends AbstractItem<IconLabelItem, IconLabelItem.ViewHolder> implements LabelItem<IconLabelItem, IconLabelItem.ViewHolder>, DesktopOptionsItem<IconLabelItem, IconLabelItem.ViewHolder> {
    private boolean bold;
    private float drawablePadding;
    private int forceSize;
    private int gravity;
    private int iconGravity;
    protected BaseIconProvider iconProvider;
    protected String label;
    protected OnClickListener listener;
    private boolean matchParent;
    private int maxTextLines;
    private int textColor;
    private int textGravity;
    private Typeface typeface;
    private int width;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView;
        }
    }

    public IconLabelItem(Item item) {
        BaseIconProvider iconProvider;
        String str = null;
        this.iconProvider = null;
        this.label = null;
        this.forceSize = -1;
        this.textColor = -12303292;
        this.gravity = 16;
        this.matchParent = true;
        this.width = -1;
        this.bold = false;
        this.textGravity = 16;
        this.maxTextLines = Integer.MAX_VALUE;
        if (item != null) {
            iconProvider = item.getIconProvider();
        } else {
            iconProvider = null;
        }
        this.iconProvider = iconProvider;
        if (item != null) {
            str = item.getLabel();
        }
        this.label = str;
    }

    public IconLabelItem(Context context, int icon, int label) {
        this.iconProvider = null;
        this.label = null;
        this.forceSize = -1;
        this.textColor = -12303292;
        this.gravity = 16;
        this.matchParent = true;
        this.width = -1;
        this.bold = false;
        this.textGravity = 16;
        this.maxTextLines = Integer.MAX_VALUE;
        this.iconProvider = Setup.imageLoader().createIconProvider(icon);
        this.label = context.getString(label);
    }

    public IconLabelItem(Context context, int label) {
        this(context, 0, label);
    }

    public IconLabelItem(Context context, int icon, String label, int forceSize) {
        this(null);
        this.label = label;
        this.iconProvider = Setup.imageLoader().createIconProvider(icon);
        this.forceSize = forceSize;
    }

    public IconLabelItem(Context context, int icon, int label, int forceSize) {
        this(null);
        this.label = context.getString(label);
        this.iconProvider = Setup.imageLoader().createIconProvider(icon);
        this.forceSize = forceSize;
    }

    public IconLabelItem(Context context, BaseIconProvider iconProvider, String label, int forceSize) {
        this(null);
        this.label = label;
        this.iconProvider = iconProvider;
        this.forceSize = forceSize;
    }

    public IconLabelItem(Context context, Drawable icon, String label, int forceSize) {
        this(null);
        this.label = label;
        this.iconProvider = Setup.imageLoader().createIconProvider(icon);
        this.forceSize = forceSize;
    }

    public IconLabelItem(Context context, Drawable icon, int label, int forceSize) {
        this(null);
        this.label = context.getString(label);
        this.iconProvider = Setup.imageLoader().createIconProvider(icon);
        this.forceSize = forceSize;
    }

    public IconLabelItem withIconGravity(int iconGravity) {
        this.iconGravity = iconGravity;
        return this;
    }

    public IconLabelItem withDrawablePadding(Context context, int drawablePadding) {
        this.drawablePadding = (float) Tool.dp2px(drawablePadding, context);
        return this;
    }

    public IconLabelItem withTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public IconLabelItem withBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public IconLabelItem withTypeface(Typeface typeface) {
        this.typeface = typeface;
        return this;
    }

    public IconLabelItem withGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public IconLabelItem withTextGravity(int textGravity) {
        this.textGravity = textGravity;
        return this;
    }

    public IconLabelItem withMatchParent(boolean matchParent) {
        this.matchParent = matchParent;
        return this;
    }

    public IconLabelItem withWidth(int width) {
        this.width = width;
        return this;
    }

    public IconLabelItem withOnClickListener(@Nullable OnClickListener listener) {
        this.listener = listener;
        return this;
    }

    public IconLabelItem withMaxTextLines(int maxTextLines) {
        this.maxTextLines = maxTextLines;
        return this;
    }

    public void setIcon(int resId) {
        this.iconProvider = Setup.imageLoader().createIconProvider(resId);
    }

    public String getLabel() {
        return this.label;
    }

    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public int getLayoutRes() {
        return R.layout.item_icon_label;
    }

    public int getType() {
        return R.id.id_adapter_icon_label_item;
    }

    public void bindView(ViewHolder holder, List payloads) {
        if (this.matchParent) {
            holder.itemView.getLayoutParams().width = -1;
        }
        if (this.width != -1) {
            holder.itemView.getLayoutParams().width = this.width;
        }
        holder.textView.setMaxLines(this.maxTextLines);
        holder.textView.setText(this.maxTextLines != 0 ? getLabel() : "");
        holder.textView.setGravity(this.gravity);
        holder.textView.setTypeface(this.typeface);
        holder.textView.setCompoundDrawablePadding((int) this.drawablePadding);
        holder.textView.setGravity(this.textGravity);
        if (this.bold) {
            holder.textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        Setup.logger().log(this, 4, null, "IconLabelItem - forceSize: %d", Integer.valueOf(this.forceSize));
        this.iconProvider.loadIconIntoTextView(holder.textView, this.forceSize, this.iconGravity);
        holder.textView.setTextColor(this.textColor);
        if (this.listener != null) {
            holder.itemView.setOnClickListener(this.listener);
        }
        super.bindView(holder, payloads);
    }

    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        if (this.iconProvider != null) {
            this.iconProvider.cancelLoad(holder.textView);
        }
        holder.textView.setText("");
        holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
}
