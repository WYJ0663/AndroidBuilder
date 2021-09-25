package com.qiqi.builder;

import android.util.Log;
import com.qiqi.common.TestB;

/**
 * Created by yijunwu on 2019/9/21.
 */

public class TestA {
    public TestA() {
        Log.d("yijunwu", Util.getLog());
    }


    public static String getLog() {
        TestB.newText();

        return "TestA2222222222222 ";

    }

}
