package com.qiqi.utils;

public class Log {
    public static void i(String s) {
        System.out.println(s);
    }

    public static void e(String s) {
        String logString = " [Error] " + s;
        System.out.println(logString);
        synchronized (Log.class) {
            FileUtil.writeAppend("D:\\log2.txt", DateUtil.getCurDateTime() + " : " + logString);
        }
    }

    public static String getStackTraceString(Throwable tr) {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(tr.getMessage()).append("\n");
            for (StackTraceElement element : tr.getStackTrace()) {
                sb.append(element.toString()).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
