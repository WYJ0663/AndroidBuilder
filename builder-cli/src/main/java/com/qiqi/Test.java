package com.qiqi;

import com.qiqi.utils.BuildUtils;

class Test {
    public static void main(String[] args) throws Exception {
        BuildUtils.initConfig();
        new ResAapt2Builder().start();
//        new Main().restart();
    }
}
