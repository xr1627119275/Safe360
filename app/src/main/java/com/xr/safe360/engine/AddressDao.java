package com.xr.safe360.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by 16271 on 2018/3/21.
 */

public class AddressDao {

    public static String path = "/data/data/com.xr.safe360/files/address.db";
    private static final String TAG = "AddressDao";
    private static String mAddress = "未知号码";

    public static String getAddress(String phone) {
        mAddress = "未知号码";
        String regex = "^1[3-8]\\d{9}";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (phone.matches(regex)) {

            phone = phone.substring(0, 7);
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);
                Cursor data2 = db.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
                if (data2.moveToNext()) {
                    Log.d(TAG, "getAddress: " + data2.getString(0));
                    mAddress = data2.getString(0);
                }
            }
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mAddress = "报警电话";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "服务电话";
                    break;
                case 7:
                case 8:
                    mAddress = "本地电话";
                    break;
                case 11:
                    String area1 = phone.substring(1, 3);
                    Cursor cursor1 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area1}, null, null, null);
                    if (cursor1.moveToNext()) {
                        mAddress = cursor1.getString(0);
                    }
                    break;
                case 12:
                    String area2 = phone.substring(1, 3);
                    Cursor cursor2 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area2}, null, null, null);
                    if (cursor2.moveToNext()) {
                        mAddress = cursor2.getString(0);
                    }
                    break;
            }
        }

        return mAddress;

    }
}
