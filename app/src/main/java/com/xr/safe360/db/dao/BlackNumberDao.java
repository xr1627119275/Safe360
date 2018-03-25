package com.xr.safe360.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xr.safe360.db.BlackNumberOpenHelper;
import com.xr.safe360.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16271 on 2018/3/22.
 */

public class BlackNumberDao {

    private final BlackNumberOpenHelper blackNumberOpenHelper;
    private static BlackNumberDao blackNumberDao = null;

    private BlackNumberDao(Context context) {
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    public void insert(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone",phone);
        contentValues.put("mode",mode);
        db.insert("blacknumber",null,contentValues);
        db.close();
    }

    public void delete(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        db.delete("blacknumber","phone=?",new String[]{phone});

        db.close();
    }

    public void update(String phone,String mode){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mode",mode);
        db.update("blacknumber",contentValues,"phone=?",new String[]{phone});
        db.close();
    }

    public List<BlackNumberInfo> findAll(){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        ArrayList<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }
    public List<BlackNumberInfo> find(int index){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;",new String[]{index+""});
        ArrayList<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }


    public int getCount(){
        int count = 0;
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber;",null);
        if(cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        return count;
    }
    public int getMode(String phone){
        int mode = 0;
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber",new String[]{"mode"},"phone = ?",new String[]{phone},null,null,null);
        if(cursor.moveToNext()){
            mode = cursor.getInt(0);
        }
        return mode;
    }
}
