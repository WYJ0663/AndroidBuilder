package com.k.android.app;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
//import com.hotpatch.hack.HotPatchApplication;

/**
 * Created by yijunwu on 2019/9/26.
 */

public class KGApplication {

    private Application mApplication;

    public KGApplication(Application app, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                         long appStartElapsedTime, long appStartMillisTime, Intent result) {
        mApplication = app;
    }

    public void onBaseContextAttached(Context base) {
//        HotPatchApplication.init(base);
    }

    public void onCreate() {
//        HotPatchApplication.init(getAttachApplication());

    }

    private Application getAttachApplication() {
        return mApplication;
    }
}
