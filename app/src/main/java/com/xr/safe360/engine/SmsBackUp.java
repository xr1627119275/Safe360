package com.xr.safe360.engine;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 16271 on 2018/3/23.
 */

public class SmsBackUp {


    public static void backup(Context context, String path, CallBack callBack) {
        FileOutputStream fos = null;
        Cursor cursor = null;

        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int index = 0;
        cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
        try {
            fos = new FileOutputStream(file);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "smss");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (callBack != null) {
                callBack.setMax(cursor.getCount());
            }
            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null, "sms");
                xmlSerializer.startTag(null, "address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null, "address");

                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(format.format(new Date(Long.parseLong(cursor.getString(1)))));
                xmlSerializer.endTag(null, "date");

                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null, "type");

                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null, "body");

                xmlSerializer.endTag(null, "sms");
                index++;
                Thread.sleep(500);
                if (callBack != null) {
                    callBack.setProgress(index);
                }
            }
            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && fos != null) {

                try {
                    cursor.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface CallBack {
        public void setMax(int max);

        public void setProgress(int progress);
    }
}
