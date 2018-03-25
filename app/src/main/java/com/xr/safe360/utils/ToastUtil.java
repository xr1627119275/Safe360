package com.xr.safe360.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 16271 on 2018/3/16.
 */

public class ToastUtil {
    /**
     * @param context 上下文
     * @param msg 打印文本内容
     */
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
