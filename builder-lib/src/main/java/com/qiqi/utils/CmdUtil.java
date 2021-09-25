package com.qiqi.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WYJ on 2016-08-17.
 */
public class CmdUtil {

    public static List<String> cmd2(String cmd) {
        return cmd2(cmd, null);
    }

    public static List<String> cmd2(String cmd, String pathName) {
        List<String> result = new ArrayList<>();
        System.out.println(cmd);
        BufferedReader br = null;
        try {
            Process p;
            if (pathName == null) {
                p = Runtime.getRuntime().exec(cmd);
            } else {
                p = Runtime.getRuntime().exec(cmd, null, new File(pathName));
            }
//            System.out.println("succeed info");
            br = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line = null;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                result.add(line);
            }

//            System.out.println("error info");
            br = new BufferedReader(new InputStreamReader(p.getErrorStream(), Charset.forName("GBK")));
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static boolean cmd(List<String> list) {
        return cmd(getCmd(list));
//        exec(list);
//        return true;
    }

    public static boolean cmd(String cmd) {
        System.out.println(cmd);
        try {
            Process p = Runtime.getRuntime().exec(cmd);

            streamForwarder(p.getErrorStream());
            streamForwarder(p.getInputStream());

            return true;
        } catch (Exception e) {
            System.out.println("报错");
            e.printStackTrace();
        }
        return false;
    }


    public static String getCmd(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for (String value : list) {
            sb.append(value).append(" ");
        }
        return sb.toString();
    }

    public static String[] getCmdArray(List<String> list) {
        String[] s = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            s[i] = list.get(i);
            ;
        }
        return s;
    }


    public static void exec(List<String> list) {
        Log.i("[cmd]" + getCmd(list));

        Process ps = null;
        BufferedReader br = null;
        InputStreamReader in = null;
        InputStreamReader in2 = null;

        try {
            ProcessBuilder builder = new ProcessBuilder(getCmdArray(list));
            ps = builder.start();

            streamForwarder(ps.getErrorStream());
            streamForwarder(ps.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.safeClose(in);
            FileUtil.safeClose(in2);
            FileUtil.safeClose(br);
        }
    }


    public static void streamForwarder(InputStream is) {
        BufferedReader br = null;
        InputStreamReader in = null;
        try {
            br = new BufferedReader(in = new InputStreamReader(is, Charset.forName("GBK")));
            String line;
            while ((line = br.readLine()) != null) {
                Log.i(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            FileUtil.safeClose(in);
            FileUtil.safeClose(br);
        }
    }


    public static void streamForwarderThread(final InputStream is) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                InputStreamReader in = null;
                try {
                    br = new BufferedReader(in = new InputStreamReader(is, Charset.forName("GBK")));
                    String line;
                    while ((line = br.readLine()) != null) {
                        Log.i(line);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    FileUtil.safeClose(in);
                    FileUtil.safeClose(br);
                }
            }
        });

    }

}
