package com.qiqi.utils;

import com.google.gson.Gson;
import com.qiqi.entity.ConfigEntity;

import java.util.ArrayList;
import java.util.List;


public class BuildUtils {
    private static ConfigEntity sConfigEntity;

    public static ConfigEntity getConfigEntity() {
        return sConfigEntity;
    }

    public static void initConfig() {
        String path = ".\\build\\my\\build_info.json";
        initConfig(path);
    }

    public static void initDebugConfig() {
        String path = "..\\out\\my\\build_info.json";//test
        initConfig(path);
    }

    public static void initConfig(String path) {
        Gson gson = new Gson();
        String json = FileUtil.readContents(path);
//        Log.i("json:" + json);
        sConfigEntity = gson.fromJson(json, ConfigEntity.class);
    }

    public static String getBuildMyPath() {
        return sConfigEntity.out_dir;
    }

    public static String getBuildToolPath() {
        return sConfigEntity.build_tool_dir;
    }

    public static String getApkToolJar() {
//        return "D:\\develop\\project\\mine\\Apktool\\brut.apktool\\apktool-cli\\build\\libs\\apktool-cli-all.jar";//test
        return getBuildToolPath() + "\\lib\\apktool-cli-all.jar";
    }

    public static String getBuildApk() {
        return BuildUtils.getBuildMyPath() + "\\apk\\signed.apk";
    }

    public static String getBuildResApk() {
        return BuildUtils.getBuildMyPath() + "\\apk\\signed_res.apk";
    }

    public static String getResourcesPath() {
        return BuildUtils.getBuildMyPath() + "\\resources";
    }

    public static String getPackageName() {
//        if (!StringUtil.isEmpty(sConfigEntity.package_name) && !sConfigEntity.package_name.equals("null")) {
//            return sConfigEntity.package_name;
//        }
        return sConfigEntity.package_name_manifest;
    }

    public static String getJarCmdPath() {
        return sConfigEntity.java_home + "\\bin\\jar.exe";
    }

    public static String getJavacCmdPath() {
        return sConfigEntity.java_home + "\\bin\\javac.exe";
    }

    public static String getJavaCmdPath() {
        return sConfigEntity.java_home + "\\bin\\java.exe";
    }

    public static String getAndroidJarPath() {
        return sConfigEntity.compile_sdk_directory + "\\android.jar";
    }

    public static String getDxCmdPath() {//build-tools\28.0.3
        return sConfigEntity.build_tools_directory + "\\dx.bat";
    }

    public static String getD8CmdPath() {//build-tools\28.0.3
        return sConfigEntity.sdk_directory + "\\build-tools\\28.0.3\\d8.bat";
    }


    public static String getAaptCmdPath() {//build-tools\28.0.3
        return sConfigEntity.build_tools_directory + "\\aapt.exe";
    }

    public static String getAapt2CmdPath() {//build-tools\28.0.3
        return sConfigEntity.build_tools_directory + "\\aapt2.exe";
    }

    public static String geFreelineAaptCmdPath() {//build-tools\28.0.3
        return sConfigEntity.build_tools_directory + "\\FreelineAapt.exe";
    }

    public static String getAdbCmdPath() {
        return sConfigEntity.sdk_directory + "\\platform-tools\\adb.exe";
    }

    public static String getAntPath() {
        return "E:\\develop\\software\\apache-ant-1.9.14\\bin";
    }

    public static List<String> getScanPathList() {
        return sConfigEntity.scan_src;
    }

    public static List<String> getScanResPathList() {
        return sConfigEntity.scan_res;
    }

    public static List<String> getSuperClassList() {
        return FileUtil.getStrings(getSuperClassListPath());
    }

    public static String getApkPath() {
        return sConfigEntity.out_dir + "\\apk\\signed.apk";
    }

    public static String getSuperClassListPath() {
        return sConfigEntity.out_dir + "\\super_class_list.txt";
    }

    public static List<String> getJarPathList() {
        String path = sConfigEntity.out_dir + "\\jar_list.txt";
        return FileUtil.getStrings(path);
    }

    public static String getJavaInfoPath() {
        return getBuildMyPath() + "\\java_info.txt";
    }

    public static String getKotlinInfoPath() {
        return getBuildMyPath() + "\\kotlin_info.txt";
    }

    public static List<String> getIncreaseClassList() {
        Log.i("related_class_list.txt");
        String path = sConfigEntity.out_dir + "\\increase_class_list.txt";
        return FileUtil.getStrings(path);
    }

    public static List<String> getExpelClassList() {
        Log.i("expel_class_list.txt");
        String path = sConfigEntity.out_dir + "\\expel_class_list.txt";
        return FileUtil.getStrings(path);
    }

    public static String getResInfoPath() {
        return getBuildMyPath() + "\\resources_info.txt";
    }

    public static List<String> getIncreaseResList() {
        Log.i("related_class_list.txt");
        String path = sConfigEntity.out_dir + "\\increase_res_list.txt";
        return FileUtil.getStrings(path);
    }

    public static List<String> getExpelResList() {
        Log.i("related_class_list.txt");
        String path = sConfigEntity.out_dir + "\\expel_res_list.txt";
        return FileUtil.getStrings(path);
    }

    public static String getExternalCacheDir(){
        return "sdcard/Android/data/" + BuildUtils.getPackageName() + "/cache";
    }
}
