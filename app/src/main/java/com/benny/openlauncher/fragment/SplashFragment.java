package com.benny.openlauncher.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.bumptech.glide.Glide;


public class SplashFragment extends Fragment {
    private FrameLayout frame;
    private int idDrawable = -1;
    @BindView(R.id.ivPreview)
    ImageView ivPreview;
    private View view;

    public static SplashFragment newInstance(int idDrawable) {
        Bundle args = new Bundle();
        args.putInt("idDrawable", idDrawable);
        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.idDrawable = getArguments().getInt("idDrawable");
    }

    public void onStart() {
        super.onStart();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.frame != null) {
            this.frame.removeAllViews();
            this.frame = null;
        }
        this.frame = new FrameLayout(getActivity());
        if (this.view == null) {
            this.view = inflater.inflate(R.layout.activity_splash_item, container, false);
            ButterKnife.bind((Object) this, this.view);
        }
        Glide.with((Fragment) this).load(Integer.valueOf(this.idDrawable)).into(this.ivPreview);
        this.frame.addView(this.view);
        return this.frame;
    }
}
