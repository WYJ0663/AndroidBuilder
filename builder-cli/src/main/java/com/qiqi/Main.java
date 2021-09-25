package com.qiqi;

import com.qiqi.utils.BuildUtils;
import com.qiqi.utils.CmdUtil;
import com.qiqi.utils.CollectUtil;
import com.qiqi.utils.FileUtil;
import com.qiqi.utils.Log;
import com.qiqi.utils.WinProgram;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args != null && args.length >= 2) {
            Log.i(args[0]);
            Log.i(args[1]);
            BuildUtils.initConfig(args[0] + "\\my\\build_info.json");
            switch (args[1]) {
                case "dex":
                    new DexBuilder().start();
                    break;
                case "res":
                    new ResAapt2Builder().start();
                    break;
                case "install":
                    clear();
                    install();
                    restartApp();
                    break;
                case "reset":
                    clear();
                    DexBuilder.pushDex2SD();
                    ResAapt2Builder.pushRes2SD();
                    restartApp();
                    break;
                case "clear":
                    clear();
                    restartApp();
                    break;
                case "delete":
                    delete();
                    WinProgram.killBigJavaExe();
                    break;
                case "restart":
                    restart();
                    break;
            }
        } else {
            Log.i("参数错误");
        }
    }

    private static void delete() {
        //rd /s /q out
//        List<String> cmdArgs = new ArrayList<>();
//        cmdArgs.add("rd");
//        cmdArgs.add("/s");
//        cmdArgs.add("/q");
//        cmdArgs.add(BuildUtils.getBuildMyPath());
//        CmdUtil.cmd(cmdArgs);

        FileUtil.deleteDir(new File(BuildUtils.getBuildMyPath()));
        Log.i("删除完成");
    }


    public static void install() {
        //    adb install -r out/my/apk/signed.apk
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("install");
        cmdArgs.add("-r");
        cmdArgs.add(BuildUtils.getApkPath());
        CmdUtil.cmd(cmdArgs);
    }

    public static void restart() {
        if (!checkRun()) {
            restartApp();
            return;
        }
        // 通过广播重启
        // adb shell am broadcast -a HOT_RESTART_BROADCAST
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("shell");
        cmdArgs.add("am");
        cmdArgs.add("broadcast");
        cmdArgs.add("-a");
        cmdArgs.add("HOT_RESTART_BROADCAST");
        CmdUtil.cmd(cmdArgs);
    }

    public static boolean checkRun() {
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("shell");
        cmdArgs.add("pidof");
        cmdArgs.add(BuildUtils.getPackageName());
        List<String> re = CmdUtil.cmd2(CmdUtil.getCmd(cmdArgs));
        if (!CollectUtil.isEmpty(re)) {
            try {
                if (Long.valueOf(re.get(0).trim()) > 0) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void restartApp() {
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("shell");
        cmdArgs.add("am");
        cmdArgs.add("force-stop");
        cmdArgs.add(BuildUtils.getPackageName());
        CmdUtil.cmd(cmdArgs);
        cmdArgs.clear();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("shell");
        cmdArgs.add("am");
        cmdArgs.add("start");
        cmdArgs.add("-n");
        cmdArgs.add(BuildUtils.getPackageName() + "/" + BuildUtils.getConfigEntity().boot_activity);
        CmdUtil.cmd(cmdArgs);
    }

    public static void clear() {
//        adb shell  rm -r /sdcard/patch_dex.jar
//        adb shell  rm -r /sdcard/patch_resources.apk
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("shell");
        cmdArgs.add("rm");
        cmdArgs.add("-r");
        cmdArgs.add(BuildUtils.getExternalCacheDir() + "/patch_dex.jar");
        CmdUtil.cmd(cmdArgs);
        cmdArgs.clear();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("shell");
        cmdArgs.add("rm");
        cmdArgs.add("-r");
        cmdArgs.add(BuildUtils.getExternalCacheDir() + "/patch_resources.apk");
        CmdUtil.cmd(cmdArgs);
        CmdUtil.cmd(cmdArgs);
    }


}
