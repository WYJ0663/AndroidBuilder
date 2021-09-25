package com.hotpatch.hack.res;

import android.util.Log;
import com.hotpatch.hack.HotPatchApplication;
import com.hotpatch.util.LogUtil;

import java.io.File;

public class GradleDynamic {

    public static boolean applyDynamicRes() {
        File apkFile = new File(HotPatchApplication.getContext().getExternalCacheDir(), "patch_resources.apk");
        if (!apkFile.exists()) {
            return false;
        }
        LogUtil.i("dynamicResPath: " + apkFile.getAbsolutePath());
        try {
            MonkeyPatcher.monkeyPatchApplication(HotPatchApplication.getContext(), apkFile.getAbsolutePath());//freeline
//                TinkerResourcePatcher.isResourceCanPatch(app);
//                TinkerResourcePatcher.monkeyPatchExistingResources(app, dynamicResPath);
            LogUtil.i("GradleDynamic apply dynamic resource successfully");
        } catch (Throwable throwable) {
            LogUtil.w(Log.getStackTraceString(throwable));
        }
        return true;
    }

}
