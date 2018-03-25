package com.xr.safe360.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 16271 on 2018/3/16.
 */

public class StreamUtil {

    /**
     * @param inputStream 流对象
     * @return 返回字符串 null为异常
     */
    public static String streamTostring(InputStream inputStream) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = 1;
        try {
            while ((temp = inputStream.read(buffer))!=-1){
                bos.write(buffer,0,temp);
            }

            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
