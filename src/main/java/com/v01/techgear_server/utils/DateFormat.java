package com.v01.techgear_server.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormat {
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}
