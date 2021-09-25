package com.qiqi.utils;


import java.util.ArrayList;
import java.util.List;

@Deprecated
public class WinProgram {
    public static void main(String[] args) {
        killBigJavaExe();
    }

    /**
     * 强制关闭大的java.exe程序
     *
     * @return
     */
    public static boolean killBigJavaExe() {
        boolean has = false;
        List<String> list = CmdUtil.cmd2("tasklist");
        for (String s : list) {
            String[] infos = s.split("\\s+");
            if ("java.exe".equals(infos[0]) && "Console".equals(infos[2]) && change(infos[4]) > 3 * 1024 * 1024) {
                Log.i("" + infos.length + " " + s);
                List<String> cmd = new ArrayList<>();
                cmd.add("taskkill");
                cmd.add("/pid");
                cmd.add(infos[1]);
                cmd.add("-f");
                CmdUtil.cmd(cmd);
                has = true;
            }
        }
        Log.i("taskkill " + has);
        return has;
    }

    public static long change(String s) {
        try {
            return Long.parseLong(s.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
