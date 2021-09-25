package com.qiqi.ide;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.qiqi.ide.util.DateUtil;
import com.qiqi.ide.util.NotificationUtils;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * https://plugins.jetbrains.com/docs/intellij/setting-up-environment.html?from=jetbrains.org#configuring-intellij-platform-sdk
 * The thread from CastAction.java has been separated into this modificated Runnable implementation.
 * <p/>
 * Created by 3mill on 2015-08-19.
 */
public class ActionRunnabe implements Runnable {

    private static Process running;
    private static long runTime;

    private File dir;
    private String name;
    private AnActionEvent event;

    private Project mProject;

    public ActionRunnabe(String name, AnActionEvent e) {

        mProject = e.getProject();
        FileDocumentManager.getInstance().saveAllDocuments();

        this.dir = new File(mProject.getBasePath());
        this.name = name;
        this.event = e;
    }

    @Override
    public void run() {
        if (running != null && System.currentTimeMillis() - runTime < 5000) {
            return;
        }

        try {
            if (running != null) {
                running.destroy();
            }
            NotificationUtils.infoNotification(DateUtil.getCurDateTime());
            NotificationUtils.infoNotification("start " + dir.getAbsolutePath() + " " + name);
//            popupBollon(true, "start " + bat.getAbsolutePath());

            Process p = Runtime.getRuntime().exec(new String[]{"java", "-jar", "-Dfile.encoding=UTF-8"
                    , mProject.getBasePath() + "\\build_tool\\lib\\hot.jar"
                    , dir + "\\build", name}, null, dir);
            running = p;
            runTime = System.currentTimeMillis();

            StringBuilder sb = new StringBuilder();
            streamForwarder(p.getInputStream(), true, sb);
            streamForwarder(p.getErrorStream(), false, sb);
//            popupBollon(true, sb.toString());
//            NotificationUtils.infoNotification(sb.toString());
        } catch (Exception e) {
            NotificationUtils.infoNotification(e.toString());
            popupBollon(false, e.toString());
        } finally {
            running = null;
        }
    }

    private void popupBollon(final boolean isOK, final String output) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                String msg = output;
//                if (isOK && output.length() > 1512) {
//                    try {
//                        File tmp = File.createTempFile("lcast_log", ".txt");
//                        FileOutputStream fos = new FileOutputStream(tmp);
//                        fos.write(output.getBytes());
//                        fos.close();
//
//                        msg = output.substring(0, 1500) + "...";
//                        msg += "\n<a href=\"file://" + tmp.getAbsolutePath() + "\">see log</a>";
//                    } catch (Exception e) {
//                    }
//                }

                StatusBar statusBar = WindowManager.getInstance()
                        .getStatusBar(mProject);
                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(msg, isOK ? MessageType.INFO : MessageType.ERROR, new HyperlinkListener() {
                            @Override
                            public void hyperlinkUpdate(HyperlinkEvent e) {
                                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                    try {
                                        java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
                                    } catch (Exception ex) {
                                    }
                                }
                            }
                        })
                        .setFadeoutTime(isOK ? 6000 : 6000)
                        .createBalloon()
                        .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                                Balloon.Position.atRight);
            }
        });
    }


    public void streamForwarder(final InputStream is, boolean isOK, StringBuilder sb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                InputStreamReader in = null;
                try {
                    in = new InputStreamReader(is, StandardCharsets.UTF_8);
                    br = new BufferedReader(in);
                    String line;
                    while ((line = br.readLine()) != null) {
//                if (isOK) {
//                    NotificationUtils.infoNotification(line);
//                } else {
//                    NotificationUtils.errorNotification(line);
//                }
//                FreelineTerminal.getInstance(mProject).initAndExecute(null);
                        NotificationUtils.infoNotification(line);
                        sb.append(line).append("\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    safeClose(in);
                    safeClose(br);
                }
            }
        }).start();
    }

    public static void safeClose(Closeable reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
