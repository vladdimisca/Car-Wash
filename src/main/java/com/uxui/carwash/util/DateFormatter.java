package com.uxui.carwash.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateFormatter {
    private final static String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_TIME_PATTERN);

    public static String formatDate(LocalDateTime dateTime) {
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return SIMPLE_DATE_FORMAT.format(date);
    }
}
