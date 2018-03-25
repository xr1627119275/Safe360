package com.xr.safe360.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 16271 on 2018/3/18.
 */

public class SpUtils {

    private static SharedPreferences sp;

    /**
     * 写入boolean标识
     * @param context
     * @param key 存储名称
     * @param value boolean
     */
    public static void putBoolean(Context context, String key, boolean value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 读取boolean标识
     * @param context
     * @param key  键值
     * @param defValue 默认值
     * @return
     */
    public static boolean getBoolean(Context context, String key,boolean defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defValue);
    }

    public static void putString(Context context, String key, String value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key,String defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key,defValue);
    }

    public static void putInt(Context context, String key, int value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key,int defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key,defValue);
    }
    public static void remove(Context context, String key) {
        if(sp==null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }
}
