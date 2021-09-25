package com.qiqi.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扫描java文件更改
 */
public class FileScanHelper {

    public List<FileInfo> pathList = new ArrayList<>();

    public List<String> pathStringList = new ArrayList<>();

    public void scan(File file, String[] suffix) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                assert files != null;
                for (File f : files) {
                    scan(f, suffix);
                }
            } else {
                if (can(file, suffix)) {
                    pathStringList.add(file.getAbsolutePath());
                    pathList.add(new FileInfo(file.getAbsolutePath(), file.lastModified(), file.length()));
                }
            }
        }
    }

    private boolean can(File file, String[] suffix) {
        if (file != null && file.isFile() && suffix != null && suffix.length > 0) {
            for (String s : suffix) {
                if ("*".equals(s)) {
                    return true;
                }
                if (file.getAbsolutePath().endsWith(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void scanJar(File file) {
        scan(file, ".jar");
    }

    public void scanDex(File file) {
        scan(file, ".dex");
    }

    public void scanSo(File file) {
        scan(file, ".so");
    }

    public void scanJava(File file) {
        scan(file, ".java");
    }

    public void scanJavaAndKotlin(File file) {
        scan(file, new String[]{".java", ".kt"});
    }

    public void scan(File file) {
        scan(file, "*");
    }

    public void scanClass(File file) {
        scan(file, ".class");
    }

    public void scan(File file, String suffix) {
        scan(file, new String[]{suffix});
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static Map<String, FileInfo> readFile(String dataPath) {
        InputStream is;
        BufferedReader reader = null;
        Map<String, FileInfo> map = new HashMap<>();
        try {
            is = new FileInputStream(dataPath);

            String line;
            reader = new BufferedReader(new InputStreamReader(is));
            line = reader.readLine();
            int count = 0;
            while (line != null) {
                if (line.equals("")) {
                    line = reader.readLine();
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 3) {
                    map.put(data[0], new FileInfo(data[0], Long.valueOf(data[1]), Long.valueOf(data[2])));
                }
                line = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(reader);
        }
        return map;
    }


    public static void writeFile(List<FileInfo> list, String out) {
        if (isEmpty(list)) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        for (FileInfo info : list) {
            sb.append(info.path).append(",")
                    .append(info.lastModified).append(",")
                    .append(info.length).append("\n");
        }
        FileUtil.writeFile(out, sb.toString());
    }

    public static void safeClose(Closeable reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static class FileInfo {
        public String path;
        public long lastModified;
        public long length;

        public FileInfo(String path, long lastModified, long length) {
            this.path = path;
            this.lastModified = lastModified;
            this.length = length;
        }

        public boolean eq(FileInfo info) {
            return this.path.equals(info.path) && this.lastModified == info.lastModified && this.length == info.length;
        }
    }


}
