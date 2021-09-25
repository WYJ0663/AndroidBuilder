package com.qiqi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getCurDateTimeName() {
        return formatDate(new Date(), "yyyy-MM-dd-HH-mm-ss");
    }

    public static String getCurDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }
    public static String formatDate(Date date, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        return formater.format(date);
    }
}
