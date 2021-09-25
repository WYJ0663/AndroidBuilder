package com.qiqi.builder;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.qiqi.common.TestB;

public class MainActivity extends Activity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView2 = findViewById(R.id.text1);
        textView2.setText(new TestKT().getText());

        textView = findViewById(R.id.text);
        textView.setText(TestB.getLog());
//        AssetManager
        Log.e("BugFixApplication", "MainActivity1 =  " + TestA.getLog());


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        findViewById(R.id.sendBroadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //adb shell am broadcast -a HOT_RESTART_BROADCAST
//                intent.setComponent(new ComponentName(MainActivity.this,"MyBroadcastReceiver2"));
//                intent.setAction("MyBroadcastReceiver");
                intent.setAction("HOT_RESTART_BROADCAST");
                sendBroadcast(intent);
                Log.i("yijunwu", "2 sendBroadcast onClick");
            }
        });
        IntentFilter  intentFilter = new IntentFilter();
        intentFilter.addAction("MyBroadcastReceiver");
        registerReceiver(new MyBroadcastReceiver(),intentFilter);
    }

    public void test(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }
}
