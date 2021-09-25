package com.qiqi.builder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"发送标准广播 " + intent.getAction(), Toast.LENGTH_LONG).show();

        Log.e("y","发送标准广播 " + intent.getAction());
    }
}
