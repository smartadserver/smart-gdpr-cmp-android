package com.smartadserver.android.smartcmp.util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    static private final String DATE_FORMAT_MILLISECOND = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static private final String DATE_FORMAT_SECOND = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Parse a date from a string.
     *
     * @param stringDate The string that needs to be parsed as a date (the date needs to be in ISO 8601 format).
     * @return A date if the string is valid, null otherwise.
     */
    @SuppressLint("SimpleDateFormat")
    static public Date dateFromString(@NonNull String stringDate) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_MILLISECOND);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return format.parse(stringDate);
        } catch (ParseException ignored) {
        }

        // If exception is catch when using the date format with millisecond, we try without millisecond.
        format = new SimpleDateFormat(DATE_FORMAT_SECOND);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            // if it does not work either, return null date is invalid.
            return null;
        }
    }
}
