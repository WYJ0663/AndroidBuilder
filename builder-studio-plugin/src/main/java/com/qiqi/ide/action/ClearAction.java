package com.qiqi.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.qiqi.ide.ActionRunnabe;

/**
 * Created by mmin18 on 7/29/15.
 */
public class ClearAction extends AnAction {

    public void actionPerformed(final AnActionEvent e) {
        new Thread(new ActionRunnabe("clear", e)).start();
    }

}
