package com.qiqi.other;


import com.qiqi.findclass.FindFileSuperClass;
import com.qiqi.utils.BuildUtils;
import com.qiqi.utils.ByteReader;
import com.qiqi.utils.FileReader;
import com.qiqi.utils.FileScanHelper;
import com.qiqi.utils.FileUtil;
import com.qiqi.utils.Log;
import com.qiqi.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class FindSuperClassMain {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
//        BuildUtils.initDebugConfig();
        BuildUtils.initConfig();
        new FindSuperClassMain().find();
        Log.i("执行时间：" + (System.currentTimeMillis() - start) / 1000 + "秒");
    }

    private void find() throws IOException {
        List<String> allJarLiat = BuildUtils.getJarPathList();//文件jar list
        for (String jar : allJarLiat) {
            if (!StringUtil.isEmpty(jar)) {
                File file = new File(jar);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        readFile(file.getAbsolutePath());
                    } else if (jar.endsWith("\\classes.jar")) {
                        readZipFile(file.getAbsolutePath());
                    }
                }
            }

            if (mData.size() > 0) {
                FileUtil.writeFile(mData, BuildUtils.getSuperClassListPath());
            }
        }
    }

    private List<FileScanHelper.FileInfo> getJavaList() {
        FileScanHelper helper = new FileScanHelper();
        List<String> pathList = BuildUtils.getScanPathList();
        for (String path : pathList) {
            if (FileUtil.dirExists(path)) {
                Log.i("扫描" + path);
                helper.scanJava(new File(path));
            }
        }
        String infoTxt = BuildUtils.getBuildMyPath() + "\\java_info.txt";
        Map<String, FileScanHelper.FileInfo> allJavaMap = FileScanHelper.readFile(infoTxt);
        Map<String, FileScanHelper.FileInfo> updateMap = new HashMap<>();
        List<FileScanHelper.FileInfo> javaLit = new ArrayList<>();
        for (FileScanHelper.FileInfo info : helper.pathList) {
            FileScanHelper.FileInfo search = allJavaMap.get(info.path);
            if (search == null) {
                Log.i("增加 " + info.path);
                javaLit.add(info);
            } else if (!search.eq(info)) {
                Log.i("修改 " + info.path);
                javaLit.add(info);
            }
        }


        List<String> classList = BuildUtils.getSuperClassList();
        for (String s :classList){
            if (!StringUtil.isEmpty(s) && s.contains(",")) {
                String[] classes = s.split(",");
                if (classes != null && classes.length >= 2) {
                    for (int i = 1; i<classes.length;i++){
                        if (javaLit.contains(classes)){

                            break;
                        }
                    }
                }
            }
        }
        return javaLit;
    }

    List<String> mData = new ArrayList<>();

    private void readFile(String path) throws IOException {

        FileScanHelper helper = new FileScanHelper();
        helper.scanClass(new File(path));
        int i = 0;
        for (String p : helper.pathStringList) {
            FileReader reader = new FileReader(p);
            FindFileSuperClass find = new FindFileSuperClass(p);
            addClass(find.safeRead(reader));
//            System.out.println((i++) + " " + find.read(reader)[0]);
        }

    }

    private void addClass(String[] classes) {
        if (classes != null && classes.length >= 2) {
            if (!"java/lang/Object".equals(classes[1])
                    && !"null".equals(classes[1])
                    && !"null".equals(classes[0])
                    && !StringUtil.isEmpty(classes[1])
                    && !StringUtil.isEmpty(classes[0])) {
                Log.i(classes[0]);
                StringBuilder sb = new StringBuilder();
                for (String clazz : classes) {
                    sb.append(clazz).append(",");
                }
                mData.add(sb.toString());
            }
        }
    }

    public void readZipFile(String path) {
        ZipFile compressedFile = null;
        try {
            compressedFile = new ZipFile(path);
            Enumeration<? extends ZipEntry> e = compressedFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                InputStream is = compressedFile.getInputStream(entry);
                ByteReader reader = new ByteReader(is);
                FindFileSuperClass find = new FindFileSuperClass(entry.getName());
                addClass(find.safeRead(reader));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.safeClose(compressedFile);
        }
    }

}
