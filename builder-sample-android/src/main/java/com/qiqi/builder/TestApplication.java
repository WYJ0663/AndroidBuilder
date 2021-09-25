package com.qiqi.builder;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.k.android.app.KGApplication;

public class TestApplication extends Application {
    private KGApplication mKGApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mKGApplication = new KGApplication(this, 1, false, 0, 0, new Intent());
        mKGApplication.onBaseContextAttached(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
