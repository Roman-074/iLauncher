package com.benny.openlauncher.base;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.benny.openlauncher.R;
import com.benny.openlauncher.base.utils.BaseConstant;
import com.benny.openlauncher.base.utils.BaseUtils;
import com.benny.openlauncher.base.utils.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class BaseActivity extends AppCompatActivity {
    private static final String PREF_TIME_PROCESS_UPDATE = "pref_key_time_final_process_update";
    public BaseActivity activity;
    public BaseApplication baseApplication;
    private long timeDelay = 86400000;

    private class DownloadFileApk extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        private DownloadFileApk() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.pDialog = new ProgressDialog(BaseActivity.this.activity);
            this.pDialog.setMessage(BaseActivity.this.getString(R.string.base_downloading));
            this.pDialog.setIndeterminate(false);
            this.pDialog.setMax(100);
            this.pDialog.setProgressStyle(1);
            this.pDialog.setCancelable(true);
            this.pDialog.show();
        }

        protected String doInBackground(String... params) {
            try {
                String filePath = Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".apk";
                URL url = new URL(params[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(filePath);
                byte[] data = new byte[1024];
                long total = 0;
                while (true) {
                    int count = input.read(data);
                    if (count == -1) {
                        output.flush();
                        output.close();
                        input.close();
                        return filePath;
                    }
                    total += (long) count;
                    publishProgress(new Integer[]{Integer.valueOf((int) ((100 * total) / ((long) lenghtOfFile)))});
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                Log.e("Error download file: " + e.getMessage());
                return "";
            }
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (this.pDialog != null) {
                this.pDialog.setProgress(values[0].intValue());
            }
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (this.pDialog != null) {
                this.pDialog.dismiss();
            }
            if (s.equals("")) {
                Toast.makeText(BaseActivity.this.activity, BaseActivity.this.getResources().getString(R.string.base_download_fail), Toast.LENGTH_SHORT).show();
            } else {
                BaseActivity.this.activity.startActivity(new Intent("android.intent.action.VIEW").setDataAndType(Uri.parse("file://" + s), "application/vnd.android.package-archive"));
            }
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.baseApplication = (BaseApplication) getApplication();
        this.activity = this;
    }

    public void processUpdate() {
        if (System.currentTimeMillis() - this.baseApplication.pref.getLong(PREF_TIME_PROCESS_UPDATE, 0) < this.timeDelay) {
            Log.e("chua du thoi gian delay update");
            return;
        }
        AlertDialog dialog;
        Log.d("vao update");
        this.baseApplication.editor.putLong(PREF_TIME_PROCESS_UPDATE, System.currentTimeMillis());
        this.baseApplication.editor.apply();
        String version_manifest = "0";
        try {
            version_manifest = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + "";
        } catch (NameNotFoundException e) {
        }
        if (System.currentTimeMillis() - this.baseApplication.pref.getLong(BaseConstant.KEY_LASTTIME_SHOW_UPDATEPOPUP, 0) <= ((long) (this.baseApplication.getBaseConfig().getUpdate().getOffset_show() * 1000))) {
            Log.d("offset time show update popup");
        } else if (this.baseApplication.getBaseConfig().getUpdate().getStatus() == 1) {
            if (Integer.parseInt(version_manifest) < Integer.parseInt(this.baseApplication.getBaseConfig().getUpdate().getVersion())) {
                dialog = Dialog_Update(true);
                //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();
                this.baseApplication.editor.putLong(BaseConstant.KEY_LASTTIME_SHOW_UPDATEPOPUP, System.currentTimeMillis());
                this.baseApplication.editor.apply();
                return;
            }
            Log.d("version manifest lon hơn. khong update");
        } else if (this.baseApplication.getBaseConfig().getUpdate().getStatus() == 2) {
            dialog = Dialog_Update(false);
            //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.show();
            this.baseApplication.editor.putLong(BaseConstant.KEY_LASTTIME_SHOW_UPDATEPOPUP, System.currentTimeMillis());
            this.baseApplication.editor.apply();
        }
    }

    private AlertDialog Dialog_Update(boolean two_button) {
        Builder builder = new Builder(this);
        builder.setTitle(this.baseApplication.getBaseConfig().getUpdate().getTitle());
        builder.setMessage(this.baseApplication.getBaseConfig().getUpdate().getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.base_update_bt_ok), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (BaseActivity.this.baseApplication.getBaseConfig().getUpdate().getType().equals("market")) {
                    BaseUtils.gotoUrl(BaseActivity.this, BaseActivity.this.baseApplication.getBaseConfig().getUpdate().getUrl_store());
                } else if (VERSION.SDK_INT < 23 || (ContextCompat.checkSelfPermission(BaseActivity.this.activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(BaseActivity.this.activity, "android.permission.READ_EXTERNAL_STORAGE") == 0)) {
                    new DownloadFileApk().execute(new String[]{BaseActivity.this.baseApplication.getBaseConfig().getUpdate().getUrl_store()});
                } else {
                    ActivityCompat.requestPermissions(BaseActivity.this.activity, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, BaseConstant.REQUEST_PERMISSION_STORAGE);
                    return;
                }
                dialog.dismiss();
            }
        });
        if (two_button) {
            builder.setNegativeButton(getResources().getString(R.string.base_update_bt_no), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        return builder.create();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BaseConstant.REQUEST_PERMISSION_STORAGE) {
            if (ContextCompat.checkSelfPermission(this.activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this.activity, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
                new DownloadFileApk().execute(new String[]{this.baseApplication.getBaseConfig().getUpdate().getUrl_store()});
                return;
            }
            BaseUtils.gotoUrl(this, this.baseApplication.getBaseConfig().getUpdate().getUrl_store());
        }
    }
}
