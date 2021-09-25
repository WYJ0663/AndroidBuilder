package com.qiqi.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileReader extends ByteReader {

    private String mPath;

    public FileReader(String path) {
        mPath = path;
        try {
            mIs = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
