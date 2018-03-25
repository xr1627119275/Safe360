package com.xr.safe360.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.xr.safe360.R;
import com.xr.safe360.db.domain.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16271 on 2018/3/25.
 */

public class ProcessInfoProvider {

    public static int getProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    public static long getAvailSpace(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    public static long getTotalSpace(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(memoryInfo);
//        return memoryInfo.totalMem;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader("/proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lineOne = bufferedReader.readLine();
            char[] chars = lineOne.toCharArray();
            StringBuffer stringBuffer = new StringBuffer();
            for (char c : chars) {
                if (c >= '0' && c <= '9') {
                    stringBuffer.append(c);
                }
            }
            return Long.parseLong(stringBuffer.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null && bufferedReader != null) {
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }


    public static List<ProcessInfo> getProcessInfoList(Context context) {
        ArrayList<ProcessInfo> processInfoList = new ArrayList<>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.packageName = info.processName;
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                processInfo.icon = applicationInfo.loadIcon(pm);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                processInfo.name = info.processName;
                processInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();

            }
            processInfoList.add(processInfo);

        }
        return processInfoList;

    }

    public static void killProcess(Context context,ProcessInfo processInfo) {
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(processInfo.packageName);

    }

}
