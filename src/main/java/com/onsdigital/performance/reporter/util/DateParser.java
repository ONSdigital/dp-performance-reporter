package com.onsdigital.performance.reporter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Parse a date string into a Java date object. Meets the same specification as the Google analytics API date fields.
 *
 * "Date values can be for a specific date by using the pattern YYYY-MM-DD or relative by using today, yesterday, or
 * the NdaysAgo pattern. Values must match [0-9]{4}-[0-9]{2}-[0-9]{2}|today|yesterday|[0-9]+(daysAgo)."
 */
public class DateParser {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parseStartDate(String input) throws ParseException {

        if (input.equals("today"))
            return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (input.equals("yesterday"))
            return Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (input.endsWith("daysAgo")) {
            int days = Integer.parseInt(input.substring(0, input.length() - "daysAgo".length()));
            return Date.from(LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        Date date = format.parse(input);

        return date;
    }

    /**
     * For end dates we go to the end of the day, ie, the start of the next day.
     * @param input
     * @return
     * @throws ParseException
     */
    public static Date parseEndDate(String input) throws ParseException {

        Date date = parseStartDate(input);
        long epochSecond = date.toInstant().atZone(ZoneId.systemDefault()).plusDays(1).toEpochSecond() * 1000;

        return new Date(epochSecond);
    }
}
