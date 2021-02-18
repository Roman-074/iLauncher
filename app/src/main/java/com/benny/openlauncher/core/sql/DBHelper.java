package com.benny.openlauncher.core.sql;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.benny.openlauncher.core.interfaces.App;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "vmblauncher.db";
    private static final int VERSION = 2;
    private String CAT_NAME = "catName";
    private String CAT_PACKAGE_NAME = "packageName";
    private String CAT_TABLE_NAME = "cat";
    private String DB_PATH = "";
    private String RECENT_TABLE_ID = "id";
    private String RECENT_TABLE_NAME = "recent_app";
    private String RECENT_TABLE_PACKAGENAME = "packagename";
    private String RECENT_TABLE_STATUS = NotificationCompat.CATEGORY_STATUS;
    private String RECENT_TABLE_TIME = "time";
    private final Context myContext;
    private SQLiteDatabase myDataBase;
    private boolean needUpdate;
    private SharedPreferences sharedPreferences;
    private long timeRecent = 86400000;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 11);
        this.DB_PATH = new ContextWrapper(context).getFilesDir().getAbsolutePath() + "/databases/";
        if (!new File(this.DB_PATH).exists()) {
            new File(this.DB_PATH).mkdirs();
        }
        this.myContext = context;
        this.sharedPreferences = context.getSharedPreferences(context.getPackageName(), 0);
        if (this.sharedPreferences.getInt("version", 0) < 2) {
            this.needUpdate = true;
            Editor editor = this.sharedPreferences.edit();
            editor.putInt("version", 2);
            editor.commit();
        }
    }

    public void createDataBase() throws IOException {
        if (!checkDataBase()) {
            getReadableDatabase();
            copyDataBase();
        } else if (this.needUpdate) {
            Log.d("HuyAnh", "--------------- UPDATE DB");
            getReadableDatabase();
            openDataBase();
        } else {
            getReadableDatabase();
            openDataBase();
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(this.DB_PATH + DB_NAME);
        if (dbFile.exists()) {
            return true;
        }
        dbFile.getParentFile().mkdirs();
        return false;
    }

    private void copyDataBase() {
        try {
            InputStream myInput = this.myContext.getAssets().open(DB_NAME);
            String outFileName = this.DB_PATH + DB_NAME;
            File root = new File(this.DB_PATH);
            if (!root.exists()) {
                root.mkdirs();
            }
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            while (true) {
                int length = myInput.read(buffer);
                if (length > 0) {
                    myOutput.write(buffer, 0, length);
                } else {
                    myOutput.flush();
                    myOutput.close();
                    myInput.close();
                    return;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this.myContext, "There is a data copy error!", Toast.LENGTH_SHORT).show();
        }
    }

    public void openDataBase() {
        try {
            this.myDataBase = SQLiteDatabase.openDatabase(this.DB_PATH + DB_NAME, null, 16);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        if (this.myDataBase != null) {
            this.myDataBase.close();
        }
        super.close();
    }

    public void onCreate(SQLiteDatabase arg0) {
    }

    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    public Cursor getData(String table, String[] columns, String selection) {
        if (this.myDataBase != null) {
            return this.myDataBase.query(table, columns, selection, null, null, null, null);
        }
        return null;
    }

    public Cursor getData(String query) {
        if (this.myDataBase != null) {
            return this.myDataBase.rawQuery(query, null);
        }
        return null;
    }

    public void insertData(String table, ContentValues contentValues) {
        try {
            this.myDataBase = SQLiteDatabase.openDatabase(this.DB_PATH + DB_NAME, null, 16);
            if (this.myDataBase != null) {
                this.myDataBase.insert(table, null, contentValues);
            } else {
                Toast.makeText(this.myContext, "Error adding data", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "error insert data sql: " + e.getMessage());
        }
    }

    public void updateData(String table, ContentValues contentValues, String filter) {
        try {
            if (this.myDataBase != null) {
                this.myDataBase.update(table, contentValues, filter, null);
            } else {
                Toast.makeText(this.myContext, "Lỗi cập nhật dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "error update data sql: " + e.getMessage());
        }
    }

    public void deleteData(String table, String filter) {
        try {
            if (this.myDataBase != null) {
                this.myDataBase.delete(table, filter, null);
            } else {
                Toast.makeText(this.myContext, "Lỗi xóa dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "error delete data sql: " + e.getMessage());
        }
    }

    public String getCatName(String packageName) {
        try {
            Cursor cursor = this.myDataBase.rawQuery("SELECT * FROM " + this.CAT_TABLE_NAME + " WHERE " + this.CAT_PACKAGE_NAME + " = \"" + packageName + "\"", null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String catName = cursor.getString(2);
                    cursor.close();
                    return catName;
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("HuyAnh", "error get cat name: " + e.getMessage());
        }
        return "null";
    }

    public void addRecent(String packageName) {
        try {
            ContentValues values = new ContentValues();
            values.put(this.RECENT_TABLE_PACKAGENAME, packageName);
            values.put(this.RECENT_TABLE_TIME, Long.valueOf(System.currentTimeMillis()));
            values.put(this.RECENT_TABLE_STATUS, "0");
            insertData(this.RECENT_TABLE_NAME, values);
        } catch (Exception e) {
            Log.e("HuyAnh", "error add recent: " + e.getMessage());
        }
    }

    public ArrayList<String> getListRecent(ArrayList<App> listIn) {
        Cursor cursor;
        ArrayList<String> listInStr = new ArrayList();
        Iterator it = listIn.iterator();
        while (it.hasNext()) {
            listInStr.add(((App) it.next()).getPackageName());
        }
        ArrayList<String> list = new ArrayList();
        try {
            cursor = this.myDataBase.rawQuery("select " + this.RECENT_TABLE_PACKAGENAME + " from " + this.RECENT_TABLE_NAME + " WHERE " + this.RECENT_TABLE_TIME + " > " + (System.currentTimeMillis() - this.timeRecent) + " GROUP BY " + this.RECENT_TABLE_PACKAGENAME + " order by count(" + this.RECENT_TABLE_PACKAGENAME + ") desc", null);
            if (cursor == null) {
                Toast.makeText(this.myContext, "Lỗi đọc dữ liệu", Toast.LENGTH_SHORT).show();
            } else {
                int tempCount = 0;
                if (cursor.moveToFirst()) {
                    if (!listInStr.contains(cursor.getString(0))) {
                        list.add(cursor.getString(0));
                        listInStr.add(cursor.getString(0));
                        tempCount = 1;
                    }
                    while (cursor.moveToNext() && tempCount < 4) {
                        if (!listInStr.contains(cursor.getString(0))) {
                            list.add(cursor.getString(0));
                            listInStr.add(cursor.getString(0));
                            tempCount++;
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
        }
        try {
            cursor = this.myDataBase.rawQuery("select " + this.RECENT_TABLE_PACKAGENAME + " from " + this.RECENT_TABLE_NAME + " GROUP BY " + this.RECENT_TABLE_PACKAGENAME + " order by " + this.RECENT_TABLE_TIME + " desc", null);
            if (cursor == null) {
                Toast.makeText(this.myContext, "Lỗi đọc dữ liệu", Toast.LENGTH_SHORT).show();
            } else {
                if (cursor.moveToFirst()) {
                    if (!listInStr.contains(cursor.getString(0))) {
                        list.add(cursor.getString(0));
                        listInStr.add(cursor.getString(0));
                    }
                    while (cursor.moveToNext() && listInStr.size() < 10) {
                        if (!listInStr.contains(cursor.getString(0))) {
                            list.add(cursor.getString(0));
                            listInStr.add(cursor.getString(0));
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e2) {
        }
        return list;
    }
}
