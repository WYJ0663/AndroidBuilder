package com.hotpatch.hack;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import com.hotpatch.hack.res.GradleDynamic;
import com.hotpatch.util.ActivityManager;
import com.hotpatch.util.LogUtil;

import java.lang.reflect.Array;
import java.util.List;


/**
 * Created by hp on 2016/4/6.
 */
public class HotPatchApplication extends Application {


    private static boolean sIsStart;

    @Override
    public void onCreate() {
        super.onCreate();

        init(this);
    }

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static void init(Context context) {
        if (sIsStart) {
            return;
        }
        sIsStart = true;

        mContext = context;

        LogUtil.e("Application = " + context.getClass().getName());

        try {
            //注入java
            HotPatch.dexInject();
            //注入res
            GradleDynamic.applyDynamicRes();
            //======== 以下是测试是否成功注入 =================
            Object object = HotPatch.getObject();
            int length = Array.getLength(object);
            LogUtil.e("length = " + length);
        } catch (Exception e) {
            LogUtil.w(Log.getStackTraceString(e));
        }

        registerBroadcast(mContext);
    }

//    private static boolean sIsStartEnter;
//    private static boolean sIsStartExit;
//
//    public static void onMethodEnter(Context context) {
//        if (sIsStartEnter) {
//            return;
//        }
//        sIsStartEnter = true;
//        mContext = context;
//        LogUtil.e("Application = " + context.getClass().getName());
//        try {
//            //注入java
//            HotPatch.dexInject();
//            //======== 以下是测试是否成功注入 =================
//            Object object = HotPatch.getObject();
//            int length = Array.getLength(object);
//            LogUtil.e("length = " + length);
//        } catch (Exception e) {
//            LogUtil.w("dex" + Log.getStackTraceString(e));
//        }
//    }
//
//    public static void onMethodExit(Context context) {
//        if (sIsStartExit) {
//            return;
//        }
//        sIsStartExit = true;
//        mContext = context;
//        try {
//            //注入res
//            GradleDynamic.applyDynamicRes();
//        } catch (Exception e) {
//            LogUtil.w("res" + Log.getStackTraceString(e));
//        }
//        registerBroadcast(mContext);
//    }

//    private static boolean hasPermission(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            } else {
//                LogUtil.i("has no permission");
//                return false;
//            }
//        }
//        return true;
//    }

    ////////////////////////////////重启应用//////////////////////////////////////////////////////////
    //     if (isMainProcess(context)) {
//        startServer(context);
//        registerBroadcast(context);
//    }

    public static String getMainProcessName(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).processName;
    }

    public static boolean isMainProcess(Context context) {
        try {
            String main = getMainProcessName(context);
            String current = getCurrentProcessName(context);
            LogUtil.i("main=" + main + "， current=" + current);
            return main.equals(current);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getCurrentProcessName(Context context) {
        int pid = Process.myPid();
        String currentProcessName = "";
        android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (android.app.ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
            if (pid == processInfo.pid) {
                currentProcessName = processInfo.processName;
            }
        }
        return currentProcessName;
    }


    private static void registerBroadcast(Context context) {
        IntentFilter intentFilter = new IntentFilter(RESTART_BROADCAST);
        context.registerReceiver(new MyReceiver(), intentFilter);
    }

    public static final String RESTART_BROADCAST = "HOT_RESTART_BROADCAST";

    public static class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (RESTART_BROADCAST.equals(intent.getAction()) && isMainProcess(context)) {
                LogUtil.i("重启");
                restart(context);
            }
        }
    }

    private static void restart(Context context) {
        ActivityManager.restart(context, true);
    }

}