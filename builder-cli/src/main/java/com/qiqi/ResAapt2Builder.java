package com.qiqi;


import com.qiqi.utils.Aapt2ValueHelper;
import com.qiqi.utils.BuildUtils;
import com.qiqi.utils.CmdUtil;
import com.qiqi.utils.FileScanHelper;
import com.qiqi.utils.FileUtil;
import com.qiqi.utils.Log;
import com.qiqi.utils.ZipUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 使用aapt2，实现增量编译
 */
public class ResAapt2Builder extends BaseBuilder {

    public static void main(String[] args) throws Exception {
        BuildUtils.initConfig();
        new ResAapt2Builder().start();
    }

    public ResAapt2Builder() {
        super(BuildUtils.getResInfoPath(), BuildUtils.getScanResPathList()
                , BuildUtils.getIncreaseResList(), BuildUtils.getExpelResList());
    }

    private Map<String, FileScanHelper.FileInfo> allMap;

    public void start() throws Exception {
        long start = System.currentTimeMillis();
        Log.i("开始执行res");
//        BuildUtils.initDebugConfig();

        decodeRes();

        preStart();

        if (mCompileList != null && !mCompileList.isEmpty()) {
            aapt2Compile(mCompileList);

            aapt2LinkAll();

            Log.i("编译完成时间：" + (System.currentTimeMillis() - start) / 1000 + "秒");

            pushRes2SD();

            Main.restart();

            compileRJava();
        } else {
            Log.i("无修改资源");
        }

        Log.i("res执行时间：" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    private void aapt2CompileAll() {
        String out = BuildUtils.getResourcesPath() + "\\apk\\aapt2build";
        String outZip = out + "\\resources.zip";
        String outRes = out + "\\res";

        FileUtil.ensumeDir(new File(out));

        List<String> cmd = new ArrayList<>();
        cmd.add(BuildUtils.getAapt2CmdPath());
        cmd.add("compile");
        cmd.add("-o");
        cmd.add(outZip);
        cmd.add("--dir");
        cmd.add(BuildUtils.getResourcesPath() + "\\apk\\res");
        CmdUtil.cmd(cmd);

        ZipUtil.unZip(outZip, outRes);
    }

    //https://developer.android.google.cn/studio/command-line/aapt2?hl=zh_cn
    private void aapt2Compile(Set<String> list) {
        String out = BuildUtils.getResourcesPath() + "\\apk\\aapt2build";
        String outRes = out + "\\res";

        FileUtil.ensumeDir(new File(outRes));

        List<String> cmd = new ArrayList<>();
        cmd.add(BuildUtils.getAapt2CmdPath());
        cmd.add("compile");
        cmd.add("-o");
        cmd.add(outRes);

        Aapt2ValueHelper helper = new Aapt2ValueHelper();
        for (String path : list) {
            if (path.contains("\\res\\values\\") && path.endsWith(".xml")) {//去除重复value
                helper.parserXml(path);
            } else {
                cmd.add(path);
            }
        }
        helper.changeXml();
        cmd.addAll(helper.getPathList());

        String valuesPath = BuildUtils.getBuildMyPath() + "\\values\\builder_values.xml";
        if (FileUtil.fileExists(valuesPath)) {
            cmd.add(valuesPath);
        }
        CmdUtil.cmd(cmd);
    }

    private void aapt2LinkAll() {
        String out = BuildUtils.getResourcesPath() + "\\apk\\aapt2build";
        String outZip = out + "\\resources_new.zip";
        String outRes = out + "\\res";
        String outApk = BuildUtils.getResourcesPath() + "\\patch_resources.apk";

        ZipUtil.zip(outZip, outRes);

        List<String> cmd = new ArrayList<>();
        cmd.add(BuildUtils.getAapt2CmdPath());

        cmd.add("link");
        cmd.add("--rename-manifest-package");
        cmd.add(BuildUtils.getPackageName());
        cmd.add("--extra-packages");
        cmd.add(BuildUtils.getPackageName());
        cmd.add("-o");
        cmd.add(outApk);
        cmd.add("--manifest");
        cmd.add(BuildUtils.getBuildToolPath() + "\\AndroidManifest.xml");
//        cmd.add("-v");
        cmd.add("-I");
        cmd.add(BuildUtils.getAndroidJarPath());
        cmd.add(outZip);
        cmd.add("--java");
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

        aapt2CompileAll();
    }


    public static void pushRes2SD() {
        String dexPath = BuildUtils.getResourcesPath() + "\\patch_resources.apk";

        List<String> cmd = new ArrayList<>();
        cmd.add(BuildUtils.getAdbCmdPath());
        cmd.add("push");
        cmd.add(dexPath);
        cmd.add(BuildUtils.getExternalCacheDir());

        CmdUtil.cmd(cmd);
    }

    public static void compileRJava() {
        List<String> cmd = new ArrayList<>();
        cmd.add(BuildUtils.getJavacCmdPath());
        cmd.add("-encoding");
        cmd.add("UTF-8");
        cmd.add("-g");
        cmd.add("-target");
        cmd.add("1.8");
        cmd.add("-source");
        cmd.add("1.8");

        String destPath = BuildUtils.getResourcesPath() + "\\classes";
        FileUtil.deleteDir(new File(destPath));
        FileUtil.ensumeDir(new File(destPath));
        cmd.add("-d");
        cmd.add(destPath);
        cmd.add(BuildUtils.getResourcesPath() + "\\" + BuildUtils.getPackageName().replace(".", "\\") + "\\R.java");

        CmdUtil.cmd(cmd);
    }
}
