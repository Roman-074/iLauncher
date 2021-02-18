package com.benny.openlauncher.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.benny.openlauncher.R;
import com.benny.openlauncher.adapter.SelectIconAdapter;
import com.benny.openlauncher.adapter.SelectIconAdapterListener;
import com.benny.openlauncher.base.utils.BaseConstant;
import com.benny.openlauncher.base.utils.Log;
import com.benny.openlauncher.core.interfaces.App;
import com.benny.openlauncher.core.interfaces.AppUpdateListener;
import com.benny.openlauncher.core.interfaces.IconDrawer;
import com.benny.openlauncher.core.manager.Setup;
import com.benny.openlauncher.core.model.Item;
import com.benny.openlauncher.util.Constant;
import com.benny.openlauncher.util.ItemOffsetDecoration;
import java.util.ArrayList;
import java.util.List;

public class SelectIconActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private Item item = null;
    @BindView(R.id.ivBG)
    ImageView ivBG;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.select_icon_ivDefault)
    ImageView ivDefault;
    private ArrayList<App> list = new ArrayList();
    @BindView(R.id.select_icon_rcView)
    RecyclerView rcView;
    private SelectIconAdapter selectIconAdapter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("my", "onCreate: SelectIconActivity");
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.select_icon_activity);
        ButterKnife.bind((Activity) this);
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            if (wallpaperManager.getDrawable() != null) {
                this.ivBG.setImageDrawable(wallpaperManager.getDrawable());
            }
        } catch (Exception e) {
            Log.e("error set bg lock screen");
        }
        this.ivBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SelectIconActivity.this.onBackPressed();
            }
        });
        if (Home.launcher == null) {
            onBackPressed();
            return;
        }
        Home home = (Home) Home.launcher;
        this.item = Home.itemEdit;
        if (this.item == null) {
            onBackPressed();
            return;
        }
        try {
            Setup.appLoader().findItemApp(this.item).getIconProvider().loadIconIntoIconDrawer(new IconDrawer() {
                public void onIconAvailable(Drawable drawable, int index) {
                    SelectIconActivity.this.ivDefault.setImageDrawable(drawable);
                }

                public void onIconCleared(Drawable placeholder, int index) {
                }
            }, -1, 0);
            this.ivDefault.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SelectIconActivity.this.item.setIconProvider(Setup.appLoader().findItemApp(SelectIconActivity.this.item).getIconProvider());
                    SelectIconActivity.this.onBackPressed();
                }
            });
        } catch (Exception e2) {
            this.item.getIconProvider().loadIconIntoIconDrawer(new IconDrawer() {
                public void onIconAvailable(Drawable drawable, int index) {
                    SelectIconActivity.this.ivDefault.setImageDrawable(drawable);
                }

                public void onIconCleared(Drawable placeholder, int index) {
                }
            }, -1, 0);
        }
        this.list.addAll(Setup.appLoader().getAllApps(this));
        this.rcView.setLayoutManager(new GridLayoutManager(this, 5));
        this.selectIconAdapter = new SelectIconAdapter(this, this.list, new SelectIconAdapterListener() {
            public void onClick(int position) {
                if (SelectIconActivity.this.list.size() > position) {
                    SelectIconActivity.this.item.setIconProvider(((App) SelectIconActivity.this.list.get(position)).getIconProvider());
                }
                SelectIconActivity.this.onBackPressed();
            }
        });
        this.rcView.setAdapter(this.selectIconAdapter);
        this.rcView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelOffset(R.dimen.select_icon_padding_rcview)));
        findViewById(R.id.rlChoseFile).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VERSION.SDK_INT < 23 || (ContextCompat.checkSelfPermission(SelectIconActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(SelectIconActivity.this, "android.permission.READ_EXTERNAL_STORAGE") == 0)) {
                    Intent intent = new Intent("android.intent.action.PICK");
                    intent.setType("image/*");
                    SelectIconActivity.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constant.REQUEST_PERMISSION_CAMERA_FLASH_LIGHT);
                    return;
                }
                ActivityCompat.requestPermissions(SelectIconActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, BaseConstant.REQUEST_PERMISSION_STORAGE);
            }
        });
        try {
            if (this.list.size() == 0) {
                Setup.appLoader().addUpdateListener(new AppUpdateListener<App>() {
                    public boolean onAppUpdated(final List<App> apps) {
                        if (SelectIconActivity.this.handler != null) {
                            SelectIconActivity.this.handler.post(new Runnable() {
                                public void run() {
                                    try {
                                        SelectIconActivity.this.list.addAll(apps);
                                        SelectIconActivity.this.selectIconAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            });
                        }
                        return false;
                    }
                });
                Setup.appLoader().onAppUpdated(this, null);
            }
        } catch (Exception e3) {
        }
    }

    public void onBackPressed() {
        setResult(-1);
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_PERMISSION_CAMERA_FLASH_LIGHT && resultCode == -1 && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    this.item.changeIconProvider(new BitmapDrawable(getResources(), Media.getBitmap(getContentResolver(), imageUri)));
                    Log.v("ok done khong can get file path");
                    onBackPressed();
                } catch (Exception e) {
                    try {
                        String[] filePathColumn = new String[]{"_data"};
                        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        String filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                        Log.v("file path: " + filePath);
                        cursor.close();
                        this.item.changeIconProvider(Drawable.createFromPath(filePath));
                        onBackPressed();
                    } catch (Exception e2) {
                    }
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BaseConstant.REQUEST_PERMISSION_STORAGE && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constant.REQUEST_PERMISSION_CAMERA_FLASH_LIGHT);
        }
    }
}
