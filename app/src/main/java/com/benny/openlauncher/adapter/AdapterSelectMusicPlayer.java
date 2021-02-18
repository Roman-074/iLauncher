package com.benny.openlauncher.adapter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.SelectMusicPlayer;
import com.benny.openlauncher.core.manager.Setup;
import java.util.ArrayList;
import java.util.List;

public class AdapterSelectMusicPlayer extends Adapter<AdapterSelectMusicPlayer.AppSearchViewHolder> {
    private SelectMusicPlayer activity;
    private List<ResolveInfo> apps = new ArrayList();
    private PackageManager packageManager;

    class AppSearchViewHolder extends ViewHolder {
        private ImageView ivIcon;
        private TextView tvLabel;

        public AppSearchViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (Setup.appSettings() != null) {
                        Setup.appSettings().setPackageMusicPlayer(((ResolveInfo) AdapterSelectMusicPlayer.this.apps.get(AppSearchViewHolder.this.getAdapterPosition())).activityInfo.packageName);
                        AdapterSelectMusicPlayer.this.activity.finish();
                    }
                }
            });
            this.ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            this.tvLabel = (TextView) itemView.findViewById(R.id.tvLabel);
        }
    }

    public AdapterSelectMusicPlayer(SelectMusicPlayer activity) {
        this.activity = activity;
        this.packageManager = activity.getPackageManager();
        this.apps = this.packageManager.queryBroadcastReceivers(new Intent("android.intent.action.MEDIA_BUTTON"), 0);
    }

    public AdapterSelectMusicPlayer.AppSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdapterSelectMusicPlayer.AppSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_music_player_item, null));
    }
@Override
    public void onBindViewHolder(AdapterSelectMusicPlayer.AppSearchViewHolder holder, int position) {
        holder.ivIcon.setImageDrawable(((ResolveInfo) this.apps.get(position)).activityInfo.loadIcon(this.packageManager));
        holder.tvLabel.setText(((ResolveInfo) this.apps.get(position)).activityInfo.loadLabel(this.packageManager));
    }

    public int getItemCount() {
        return this.apps.size();
    }
}
