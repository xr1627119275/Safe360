package com.xr.safe360.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by 16271 on 2018/3/21.
 */

public class ServiceUtil {
    public static boolean isRunning(Context context,String serviceName){
        ActivityManager mManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = mManager.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info:runningServices){
            String className = info.service.getClassName();
            if(className.equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
