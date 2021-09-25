package com.qiqi;


import com.qiqi.utils.BuildUtils;
import com.qiqi.utils.CmdUtil;
import com.qiqi.utils.CollectUtil;
import com.qiqi.utils.FileScanHelper;
import com.qiqi.utils.FileUtil;
import com.qiqi.utils.Log;
import com.qiqi.utils.StringUtil;
import com.qiqi.utils.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 扫描java--》扫描缓存class--》增量编译class--》打包dex（dex\\cache\\classes）-->推倒sd卡--》重启app
 */
public class DexBuilder extends BaseBuilder {

    public static void main(String[] args) throws Exception {
        BuildUtils.initConfig();
        new DexBuilder().start();
    }

    public DexBuilder() {
        super(BuildUtils.getJavaInfoPath(), BuildUtils.getScanPathList()
                , BuildUtils.getIncreaseClassList(), BuildUtils.getExpelClassList());
    }

    public void start() throws Exception {
        long start = System.currentTimeMillis();
        preStart();
        if (mCompileList.size() > 0) {
            compile();
            classes2DexUseD8();
            pushDex2SD();
            Main.restart();
        }
        Log.i("dex执行时间：" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    @Override
    protected void scanFile(FileScanHelper helper, String path) {
        helper.scanJavaAndKotlin(new File(path));
    }

    @Override
    protected boolean canIncrease(String path, String name) {
        return path.endsWith(File.separator + name + ".java");
    }

    @Override
    protected boolean canExpel(String path, String name) {
        return path.endsWith(File.separator + name + ".java");
    }

    public void compile() throws Exception {
        String classDestPath = BuildUtils.getBuildMyPath() + "\\dex\\classes";
        File classDestFile = new File(classDestPath);
        FileUtil.deleteDir(classDestFile);
        FileUtil.ensumeDir(classDestFile);

        compileJava(mCompileList);
        compileKotlin(mCompileList);
    }

    public void compileJava(Set<String> list) throws Exception {
        Set<String> javaList = getList(list, ".java");
        if (CollectUtil.isEmpty(javaList)) {
            return;
        }
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getJavacCmdPath());
        cmdArgs.add("-encoding");
        cmdArgs.add("UTF-8");
        cmdArgs.add("-g");
        cmdArgs.add("-target");
        cmdArgs.add("1.8");
        cmdArgs.add("-source");
        cmdArgs.add("1.8");
        cmdArgs.addAll(compileClassPath(javaList));
        CmdUtil.cmd(cmdArgs);
    }

    private Set<String> getList(Set<String> list, String suffix) {
        Set<String> set = new HashSet<>();
        for (String s : list) {
            if (s.endsWith(suffix)) {
                set.add(s);
            }
        }
        return set;
    }

    public void compileKotlin(Set<String> list) throws Exception {
        Set<String> ktList = getList(list, ".kt");
        if (CollectUtil.isEmpty(ktList)) {
            return;
        }
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add("kotlinc.bat");
        cmdArgs.add("-jvm-target");
        cmdArgs.add("1.8");
        cmdArgs.addAll(compileClassPath(ktList));
        CmdUtil.cmd(cmdArgs);
    }

    public List<String> compileClassPath(Set<String> javaLit) throws IOException {

        List<String> cmdArgs = new ArrayList<>();
        List<String> classPath = new ArrayList<>();

        //引用编译的jar，class
        String androidJar = BuildUtils.getAndroidJarPath();
        classPath.add(androidJar);//android.jar

        String cacheClassDestPath = BuildUtils.getBuildMyPath() + "\\dex\\cache\\classes";
        classPath.add(cacheClassDestPath);//cache class

        String rClassDestPath = BuildUtils.getResourcesPath() + "\\classes";
        classPath.add(rClassDestPath);//r class

        String mainPath = BuildUtils.getBuildMyPath() + "\\jar";
        File mainFile = new File(mainPath);
        if (mainFile.exists()) {
            for (File jarFile : mainFile.listFiles()) {
                classPath.add(jarFile.getAbsoluteFile().getAbsolutePath());//all jar
            }
        }

        String RJar = BuildUtils.getBuildMyPath() + "\\resources\\R.jar";
        classPath.add(RJar);//R jar
        List<String> allJarLiat = BuildUtils.getJarPathList();//文件jar list
        for (
                String jar : allJarLiat) {
            if (jar != null && (!FileUtil.fileExists(RJar) || !jar.endsWith(".R.jar"))) {
                classPath.add(jar);//all jar
            }
        }

        cmdArgs.add("-cp");
        cmdArgs.add(joinClasspath(classPath));

        //输出路径
        String classDestPath = BuildUtils.getBuildMyPath() + "\\dex\\classes";
        cmdArgs.add("-d");
        cmdArgs.add(classDestPath);

        String src_list_txt = BuildUtils.getBuildMyPath() + "\\dex\\src_list.txt";
        FileUtil.deleteFile(new

                File(src_list_txt));
        StringBuffer javaPath = new StringBuffer();
        for (
                String java : javaLit) {
            javaPath.append(java + "\n");
        }
        FileUtil.write2file(javaPath.toString().getBytes(), new File(src_list_txt));
        cmdArgs.add("@" + src_list_txt);

        return cmdArgs;
    }

    private List<String> getCacheClassList() throws IOException {
        String cacheClassDestPath = BuildUtils.getBuildMyPath() + "\\dex\\cache\\classes";
        FileUtil.ensumeDir(new File(cacheClassDestPath));
        List<String> pathList = new ArrayList<>();
        FileUtil.scanDir(new File(cacheClassDestPath), pathList);
        return pathList;
    }

    private String getCacheClass() throws IOException {
        String cacheClassDestPath = BuildUtils.getBuildMyPath() + "\\dex\\cache\\classes";
        FileUtil.ensumeDir(new File(cacheClassDestPath));
        List<String> pathList = new ArrayList<>();
        FileUtil.scanDir(new File(cacheClassDestPath), pathList);
        if (pathList.size() > 0) {
            String src_list_txt = BuildUtils.getBuildMyPath() + "\\dex\\cache_src_list.txt";
            FileUtil.deleteFile(new File(src_list_txt));
            StringBuffer classPath = new StringBuffer();
            for (String java : pathList) {
                classPath.append(java + "\n");
            }
            FileUtil.write2file(classPath.toString().getBytes(), new File(src_list_txt));
            return src_list_txt;
        }
        return null;
    }

    private String joinClasspath(List<String> collection) {
        StringBuilder sb = new StringBuilder();

        boolean window = true;
        for (String s : collection) {
            if (!StringUtil.isEmpty(s) && (new File(s).exists())) {
                sb.append(s);
                if (window) {
                    sb.append(";");
                } else {
                    sb.append(":");
                }
            }
        }
        return sb.toString();
    }

    @Deprecated
    public void pack2Jar() {
        String destPath = BuildUtils.getBuildMyPath() + "\\dex";
        String classPath = BuildUtils.getBuildMyPath() + "\\dex\\classes";
        FileUtil.deleteFile(destPath);
        Log.i("jar classPath : " + classPath);
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getJarCmdPath());
        cmdArgs.add("-cvf");
        cmdArgs.add("patch.jar");
        cmdArgs.add("-C");
        cmdArgs.add(classPath);
        cmdArgs.add(".");
        CmdUtil.cmd(cmdArgs);

    }

    @Deprecated
    public void JarToDex() {
        Log.i(new File("").getAbsolutePath());
        String jarfile = new File("").getAbsolutePath() + "\\patch.jar";
        String dexOut = BuildUtils.getBuildMyPath() + "\\dex\\patch_dex.jar";
        FileUtil.deleteFile(dexOut);
        File dxFile = new File(BuildUtils.getDxCmdPath());
        List<String> dexargs = new ArrayList<>();
        dexargs.add(dxFile.getAbsolutePath());
        dexargs.add("--dex");
//        dexargs.add("--no-locals")
//        dexargs.add("--force-jumbo");
//         添加此标志以规避dx.jar对"java/"、"javax/"的检查
//        dexargs.add("--core-library")
        // disableDexMerger="${dex.disable.merger}"

        dexargs.add("--output=" + dexOut);
        dexargs.add(jarfile);

        CmdUtil.cmd(dexargs);
    }

    //d8
    public void classes2DexUseD8() {
        String jarPath = BuildUtils.getBuildMyPath() + "\\dex\\patch_dex_temp.jar";
        String dexOut = BuildUtils.getBuildMyPath() + "\\dex\\patch_dex.jar";
        String classPath = BuildUtils.getBuildMyPath() + "\\dex\\classes";

        File file = new File(classPath);
        if (file.isDirectory()) {
            long time = System.currentTimeMillis();
            ZipUtil.zip(jarPath, classPath);
            Log.i("class zip time：" + (System.currentTimeMillis() - time));
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(BuildUtils.getD8CmdPath());
        cmd.add("--debug");
        cmd.add("--min-api");
        cmd.add("26");//26解决java8特性问题
        cmd.add("--lib");
        cmd.add(BuildUtils.getAndroidJarPath());
        cmd.add("--output");
        cmd.add(dexOut);
        cmd.add(jarPath);
//        cmd.add("-JXms1024M");
//        cmd.add("-JXmx2048M");
        CmdUtil.cmd(cmd);
    }

    //    E:\develop\software\Android\sdk\build-tools\25.0.3\dx.bat --dex  --output=.\patch_dex.jar .\build
    @Deprecated
    public void classes2Dex() {
        String dexOut = BuildUtils.getBuildMyPath() + "\\dex\\patch_dex.jar";
        String classPath = BuildUtils.getBuildMyPath() + "\\dex\\classes";
        Log.i("jar classPath : " + classPath);
        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getDxCmdPath());
        cmdArgs.add("--dex");
        cmdArgs.add("--output=" + dexOut);
        cmdArgs.add(classPath);
        CmdUtil.cmd(cmdArgs);
    }

    public static void pushDex2SD() {
        String dexPath = BuildUtils.getBuildMyPath() + "\\dex\\patch_dex.jar";

        List<String> cmdArgs = new ArrayList<>();
        cmdArgs.add(BuildUtils.getAdbCmdPath());
        cmdArgs.add("push");
        cmdArgs.add(dexPath);
        cmdArgs.add(BuildUtils.getExternalCacheDir());

        CmdUtil.cmd(cmdArgs);
    }
//
//    private void saveCacheJavaList(List<FileScanHelper.FileInfo> javaLit) {
//        if (USE_CACHE) {
//            String src_list_txt = BuildUtils.getBuildMyPath() + "\\dex\\cache_compile_java.txt";
//            FileUtil.deleteFile(new File(src_list_txt));
//            FileUtil.ensumeDir(new File(src_list_txt).getParentFile());
//            FileScanHelper.writeFile(javaLit, src_list_txt);
//        }
//    }
//
//    private List<String> getCacheJavaList(List<FileScanHelper.FileInfo> javaLit) {
//        String src_list_txt = BuildUtils.getBuildMyPath() + "\\dex\\cache_compile_java.txt";
//        File file = new File(src_list_txt);
//        List<String> newJavaLit = new ArrayList<>();
//        if (file.exists() && USE_CACHE) {
//            FileUtil.ensumeDir(file.getParentFile());
//            Map<String, FileScanHelper.FileInfo> cacheMap = FileScanHelper.readFile(src_list_txt);
//            for (FileScanHelper.FileInfo info : javaLit) {
//                FileScanHelper.FileInfo search = cacheMap.get(info.path);
//                if (search == null) {
//                    Log.i("Cache增加 " + info.path);
//                    newJavaLit.add(info.path);
//                } else if (!search.eq(info)) {
//                    Log.i("Cache修改 " + info.path);
//                    newJavaLit.add(info.path);
//                }
//            }
//        } else {
//            for (FileScanHelper.FileInfo info : javaLit) {
//                newJavaLit.add(info.path);
//            }
//        }
//        return newJavaLit;
//    }

}
