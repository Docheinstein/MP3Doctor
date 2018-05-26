package org.docheinstein.mp3doctor.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/** Contains utilities for time. */
public class TimeUtil {

    /**
     * Contains some common patterns that can be used
     * for the methods of this class.
     */
    public static class Patterns {
        public static final String DATE_TIME = "dd/MM/yyyy HH:mm:ss";
        public static final String TIME = "HH:mm:ss";
    }

    /**
     * Returns the string that is composed using the current millis and
     * the given pattern.
     * @param pattern the date/time pattern
     * @return the string that uses the current millis for create the given pattern
     *
     * @see #millisToString(String, long)
     * @see #dateToString(String, Date, TimeZone)
     */
    public static String millisToString(String pattern) {
        return millisToString(pattern, System.currentTimeMillis());
    }

    /**
     * Returns the string that is composed using the given millis and
     * the given pattern.
     * @param pattern the date/time pattern
     * @param millis the amount of millis the pattern is created with
     * @return the string that uses the millis for create the given pattern
     *
     * @see #millisToString(String)
     * @see #dateToString(String, Date, TimeZone)
     */
    public static String millisToString(String pattern, long millis) {
        return dateToString(pattern, new Date(millis), TimeZone.getTimeZone("GMT"));
    }

    /**
     * Returns the string that is composed using the given pattern,
     * the default timezone and the current date.
     * @param pattern the date/time pattern
     * @return  the string that is composed using the given pattern,
     *          the default timezone and the current date
     *
     * @see #dateToString(String, Date, TimeZone)
     */
    public static String dateToString(String pattern) {
        return dateToString(pattern, new Date());
    }

    /**
     * Returns the string that is composed using the given pattern, date
     * and the default timezone.
     * @param pattern the date/time pattern
     * @param date the date
     * @return  the string that is composed using the given pattern, date and
     *          the default timezone
     *
     * @see #dateToString(String, Date, TimeZone)
     */
    public static String dateToString(String pattern, Date date) {
        return dateToString(pattern, date, TimeZone.getDefault());
    }

    /**
     * Returns the string that is composed using the given pattern, date and timezone.
     * @param pattern the date/time pattern
     * @param date the date
     * @param zone the timezone
     * @return the string that is composed using the given pattern, date and timezone
     */
    public static String dateToString(String pattern, Date date, TimeZone zone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(zone);
        return simpleDateFormat.format(date);
    }
}
