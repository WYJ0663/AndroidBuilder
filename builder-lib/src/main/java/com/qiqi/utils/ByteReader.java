package com.qiqi.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ByteReader implements Closeable {
    protected InputStream mIs = null;

    public ByteReader() {
    }

    public ByteReader(InputStream is) {
        mIs = is;
    }

    public int read(byte b[], int off, int len) {
        try {
            return mIs.read(b, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int readInt() {
        byte[] bytes = new byte[4];
        int len = read(bytes, 0, bytes.length);
        return ((bytes[0] & 0XFF) << 24) |
                ((bytes[1] & 0XFF) << 16) |
                ((bytes[2] & 0XFF) << 8) |
                (bytes[3] & 0XFF);
    }

    public byte read() {
        byte[] bytes = new byte[1];
        int len = read(bytes, 0, bytes.length);
        return bytes[0];
    }

    public byte[] reads(int len) {
        byte[] bytes = new byte[len];
        read(bytes, 0, bytes.length);
        return bytes;
    }

    public short readShort() {
        byte[] bytes = new byte[2];
        int len = read(bytes, 0, bytes.length);
        return (short) (((bytes[0] & 0XFF) << 8) |
                (bytes[1] & 0XFF));
    }

    public long readLong() {
        byte[] bytes = new byte[8];
        int len = read(bytes, 0, bytes.length);
        return ((bytes[7] & 0XFF) << 56) |
                ((bytes[6] & 0XFF) << 48) |
                ((bytes[5] & 0XFF) << 40) |
                ((bytes[4] & 0XFF) << 32) |
                ((bytes[3] & 0XFF) << 24) |
                ((bytes[2] & 0XFF) << 16) |
                ((bytes[1] & 0XFF) << 8) |
                (bytes[0] & 0XFF);
    }

    public String readString(int off, int len, Charset charset) {
        byte[] bytes = new byte[len];
        read(bytes, off, bytes.length);
        return new String(bytes, charset);
    }

    public String readString(int off, int len) {
        return readString(off, len, StandardCharsets.UTF_8);
    }

    public String readString(int len) {
        return readString(0, len, StandardCharsets.UTF_8);
    }

    public long skip(int len) {
        try {
            return mIs.skip(len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void close() {
        if (mIs != null) {
            try {
                mIs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
