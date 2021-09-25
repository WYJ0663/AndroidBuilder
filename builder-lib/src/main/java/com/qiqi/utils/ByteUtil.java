package com.qiqi.utils;

public class ByteUtil {
    /**
     * 2进制转16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String byteToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = bytes.length - 1; i >= 0; i--) {
            String strHex = Integer.toHexString(bytes[i]);
            if (strHex.length() > 3) {
                sb.append(strHex.substring(6));
            } else {
                if (strHex.length() < 2) {
                    sb.append("0" + strHex);
                } else {
                    sb.append(strHex);
                }
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * @param value
     * @return
     */
    public static byte[] short2Byte(short value) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) ((value >> 8) & 0XFF);
        bytes[0] = (byte) (value & 0XFF);
        return bytes;
    }
}
