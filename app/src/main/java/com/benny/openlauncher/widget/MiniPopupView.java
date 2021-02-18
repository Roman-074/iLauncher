package com.benny.openlauncher.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.Utils;

import com.benny.openlauncher.R;
import com.benny.openlauncher.util.LauncherAction.Action;
import com.benny.openlauncher.util.Tool;

import io.codetail.widget.RevealFrameLayout;

public class MiniPopupView extends RevealFrameLayout {
    private boolean haveWidowDisplayed;

    static class ClearRamViewHolder {
        @BindView(R.id.available_ram)
        TextView availableRam;
        @BindView(R.id.available_storage)
        TextView availableStorage;

        ClearRamViewHolder(View view) {
            ButterKnife.bind((Object) this, view);
        }
    }


    static class VolumeDialogViewHolder {
        @BindView(R.id.sb_media)
        SeekBar sbMedia;
        @BindView(R.id.sb_notification)
        SeekBar sbNotification;
        @BindView(R.id.sb_ringtone)
        SeekBar sbRingtone;
        @BindView(R.id.sb_system)
        SeekBar sbSystem;

        VolumeDialogViewHolder(View view) {
            ButterKnife.bind((Object) this, view);
        }

        private OnSeekBarChangeListener getSoundChangeListener(final AudioManager audioManager, final int type) {
            return new OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(type, progress, 0);
                    VolumeDialogViewHolder.this.sbRingtone.setProgress(audioManager.getStreamVolume(2));
                    VolumeDialogViewHolder.this.sbNotification.setProgress(audioManager.getStreamVolume(5));
                    VolumeDialogViewHolder.this.sbSystem.setProgress(audioManager.getStreamVolume(1));
                    VolumeDialogViewHolder.this.sbMedia.setProgress(audioManager.getStreamVolume(3));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };
        }
    }


    public MiniPopupView(Context context) {
        super(context);
    }

    public MiniPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiniPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.haveWidowDisplayed) {
            return super.onTouchEvent(event);
        }
        closeAllWindow();
        this.haveWidowDisplayed = false;
        return true;
    }

    @SuppressLint({"DefaultLocale"})
    public void showActionWindow(Action action, float x, float y) {
        View window = null;
        Context context = getContext();
        switch (action) {
            case ClearRam:
                window = LayoutInflater.from(getContext()).inflate(R.layout.window_storage_info, this, false);
                ClearRamViewHolder clearRamViewHolder = new ClearRamViewHolder(window);
                clearRamViewHolder.availableRam.setText(Html.fromHtml(Tool.getRAM_Info(context)));
                clearRamViewHolder.availableStorage.setText(Html.fromHtml(Tool.getStorage_Info(context)));
                break;
            case VolumeDialog:
                @SuppressLint("WrongConstant") AudioManager audioManager = (AudioManager) getContext().getSystemService(Tool.BASE_TYPE_AUDIO);
                window = LayoutInflater.from(getContext()).inflate(R.layout.window_volume, this, false);
                VolumeDialogViewHolder volumeDialogViewHolder = new VolumeDialogViewHolder(window);
                volumeDialogViewHolder.sbRingtone.setMax(audioManager.getStreamMaxVolume(2));
                volumeDialogViewHolder.sbNotification.setMax(audioManager.getStreamMaxVolume(5));
                volumeDialogViewHolder.sbSystem.setMax(audioManager.getStreamMaxVolume(1));
                volumeDialogViewHolder.sbMedia.setMax(audioManager.getStreamMaxVolume(3));
                volumeDialogViewHolder.sbRingtone.setProgress(audioManager.getStreamVolume(2));
                volumeDialogViewHolder.sbNotification.setProgress(audioManager.getStreamVolume(5));
                volumeDialogViewHolder.sbSystem.setProgress(audioManager.getStreamVolume(1));
                volumeDialogViewHolder.sbMedia.setProgress(audioManager.getStreamVolume(3));
                volumeDialogViewHolder.sbRingtone.setOnSeekBarChangeListener(volumeDialogViewHolder.getSoundChangeListener(audioManager, 2));
                volumeDialogViewHolder.sbNotification.setOnSeekBarChangeListener(volumeDialogViewHolder.getSoundChangeListener(audioManager, 5));
                volumeDialogViewHolder.sbSystem.setOnSeekBarChangeListener(volumeDialogViewHolder.getSoundChangeListener(audioManager, 1));
                volumeDialogViewHolder.sbMedia.setOnSeekBarChangeListener(volumeDialogViewHolder.getSoundChangeListener(audioManager, 3));
                break;
        }
        displayWindow(window, x, y);
    }

    public void closeAllWindow() {
        displayWindow(null, 0.0f, 0.0f);
    }

    private void displayWindow(final View window, float x, final float y) {
        if (window == null) {
            removeAllViews();
            return;
        }
        window.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (window != null) {
                    window.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (y + ((float) (window.getHeight() / 2)) > ((float) MiniPopupView.this.getHeight())) {
                        window.setY((float) ((MiniPopupView.this.getHeight() - window.getHeight()) - ((MarginLayoutParams) window.getLayoutParams()).bottomMargin));
                    } else {
                        window.setY(y - ((float) (window.getHeight() / 2)));
                    }
                }
            }
        });
        this.haveWidowDisplayed = true;
        addView(window);
    }
}
