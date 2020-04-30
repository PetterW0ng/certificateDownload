package org.pkucare.util;


import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {

    public static String format(Date date, String yyyyMMddHHmm) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(yyyyMMddHHmm);
        return simpleDateFormat.format(date);
    }
}
