package com.qiqi.ide.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil {

    public static String getCurDateTimeSave() {
        return formatDate(new Date(), "yyyy-MM-dd HH-mm-ss");
    }

    public static String getCurDateTimeSQL() {
        return formatDate(new Date(), "yyyyMMddHHmmss");
    }

    public static String getCurDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getMonthDate() {
        return formatDate(new Date(), "yyyyMM");
    }

    public static String getCurDate() {
        return formatDate(new Date(), "yyyy-MM-dd");
    }

    public static String getCurDateSQL() {
        return formatDate(new Date(), "yyyy_MM_dd");
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        return formater.format(date);
    }




}
