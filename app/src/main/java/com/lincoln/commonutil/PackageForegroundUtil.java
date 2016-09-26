package com.lincoln.commonutil;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.util.List;

/**
 * 判断应用是否在前台工具类
 * Created by lincoln on 16/9/13.
 */
public class PackageForegroundUtil {

    /**
     * 获取前台应用包名
     */
    public static String getForegroundPackageName(Context context){
        String result = "";
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){//大于L版本
                    result = getTopPackage(context);
            }else{
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                if (!tasks.isEmpty()) {
                    ComponentName topActivity = tasks.get(0).topActivity;
                    result = topActivity.getPackageName();
                }else{
                    LogUtil.d("tasks is empty");
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }


        return result;

    }


    /**
     * 获取最近使用记录
     */
    static UsageStatsManager sUsageStatsManager;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String  getTopPackage(Context context){
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 10000;
        if (sUsageStatsManager == null) {
            sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }
        String result = "";
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.getPackageName();
            }
        }
        if (!android.text.TextUtils.isEmpty(result)) {
            return result;
        }
        return result;
    }

    /**
     * 判断是否需要授权
     */
    public static boolean needPermissionForBlocking(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOpsManager.checkOp(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
                return  (mode != AppOpsManager.MODE_ALLOWED);
            } catch (PackageManager.NameNotFoundException e) {
                return true;
            }
        }else {
            return false;
        }

    }
    /**
     * 授权
     */
    public  static void requetUsageAccessSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        context.startActivity(intent);
    }

}
