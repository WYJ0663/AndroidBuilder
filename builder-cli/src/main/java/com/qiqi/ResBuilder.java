package com.qiqi;


import com.qiqi.utils.BuildUtils;
import com.qiqi.utils.CmdUtil;
import com.qiqi.utils.FileScanHelper;
import com.qiqi.utils.FileUtil;
import com.qiqi.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * aapt，全量编译资源
 */
@Deprecated
public class ResBuilder {

    public static void main(String[] args) throws Exception {
        new ResBuilder().start();
    }

    private Map<String, FileScanHelper.FileInfo> allMap;

    private void start() throws Exception {
        long start = System.currentTimeMillis();
        Log.i("开始执行res");
//        BuildUtils.initDebugConfig();
        BuildUtils.initConfig();

        decodeRes();

        boolean is = scanRes();

        if (is) {
            aaptBuildRes();

            pushRes2SD();

            Main.restartApp();

            compile();
        } else {
            Log.i("无修改资源");
        }

         Log.i("res执行时间：" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    private boolean scanRes() {
        List<FileScanHelper.FileInfo> infoList = getResList();
        if (infoList != null && !infoList.isEmpty()) {
            for (FileScanHelper.FileInfo info : infoList) {
                File file = new File(info.path);
                File parentFile = file.getParentFile();
                String root = BuildUtils.getResourcesPath() + "\\apk\\res";
                String des = root.equals(parentFile.getAbsolutePath()) ? root : root + "\\" + parentFile.getName();
                FileUtil.fileCopy(info.path, des + "\\" + file.getName());
            }
            return true;
        }

        return false;
    }

    private void aaptBuildRes() {
//        java -jar apktool.jar b %filePath%
        List<String> cmd = new ArrayList<>();
        cmd.add(BuildUtils.getJavaCmdPath());
        cmd.add("-jar -Dfile.encoding=UTF-8");
        cmd.add(BuildUtils.getApkToolJar());
        cmd.add("b");
        cmd.add(BuildUtils.getResourcesPath() + "\\apk");
        cmd.add("-o");
        cmd.add(BuildUtils.getResourcesPath() + "\\patch_resources.apk");
        cmd.add("-r-path");
        cmd.add(BuildUtils.getResourcesPath());
        CmdUtil.cmd(cmd);
    }

    private void decodeRes() {
        String out = BuildUtils.getResourcesPath() + "\\apk";
        if (FileUtil.fileExists(out + "\\AndroidManifest.xml")) {
            Log.i("不需要解压资源");
            return;
        }

        String apkPath = BuildUtils.getBuildApk();
//        String apkResPath = BuildUtils.getBuildResApk();
//        ZipUtil.changeResApk(apkPath, apkResPath);

        //        java - jar apktools.jar d -f - s % DIRNAME % -o % filePath %
        List<String> cmd = new ArrayList<>();
        cmd.add(BuildUtils.getJavaCmdPath());
        cmd.add("-jar -Dfile.encoding=UTF-8");
        cmd.add(BuildUtils.getApkToolJar());
        cmd.add("d");
        cmd.add("-f");
        cmd.add("-s");
        cmd.add(apkPath);
        cmd.add("-o");
        cmd.add(out);
        cmd.add("-just-res");
        CmdUtil.cmd(cmd);
    }

    private List<FileScanHelper.FileInfo> getResList() {
        FileScanHelper helper = new FileScanHelper();
        List<String> pathList = BuildUtils.getScanResPathList();
        for (String path : pathList) {
            if (FileUtil.dirExists(path)) {
                Log.i("扫描" + path);
                helper.scan(new File(path));
            }
        }
        String infoTxt = BuildUtils.getBuildMyPath() + "\\resources_info.txt";
        allMap = FileScanHelper.readFile(infoTxt);
        List<FileScanHelper.FileInfo> javaLit = new ArrayList<>();
        for (FileScanHelper.FileInfo info : helper.pathList) {
            if (info.path.contains("\\res\\values\\")) {
                continue;
            }
            FileScanHelper.FileInfo search = allMap.get(info.path);
            if (search == null) {
                 Log.i("res增加 " + info.path);
                javaLit.add(info);
            } else if (!search.eq(info)) {
                 Log.i("res修改 " + info.path);
                javaLit.add(info);
            }
        }

        return javaLit;
    }

    private static void pushRes2SD() {
        String dexPath = BuildUtils.getBuildMyPath() + "\\resources\\patch_resources.apk";

        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("push");
        cmdArgs.add(dexPath);
        cmdArgs.add(BuildUtils.getExternalCacheDir());

        CmdUtil.cmd(cmdArgs);
    }

    public static void compile() {
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getJavacCmdPath());
        cmdArgs.add("-encoding");
        cmdArgs.add("UTF-8");
        cmdArgs.add("-g");
        cmdArgs.add("-target");
        cmdArgs.add("1.8");
        cmdArgs.add("-source");
        cmdArgs.add("1.8");

        String destPath = BuildUtils.getResourcesPath() + "\\classes";
        FileUtil.deleteDir(new File(destPath));
        FileUtil.ensumeDir(new File(destPath));
        cmdArgs.add("-d");
        cmdArgs.add(destPath);
        cmdArgs.add(BuildUtils.getResourcesPath() + "\\R.java");

        CmdUtil.cmd(cmdArgs);
    }
}
