package com.onsdigital.performance.reporter.splunk;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SplunkQueryBuilder {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy:HH:mm:ss");

    public static String buildQuery(String input, Date startDate, Date endDate) {
        String query = input;

        // trim the known elements at the start of the query allowing us to insert the start / end times.
        query = query.replaceFirst("^search", "")
                .trim()
                .replaceFirst("\\*", "")
                .trim();

        query = query.replaceFirst("earliest=\\d\\d/\\d\\d/\\d\\d\\d\\d:\\d\\d:\\d\\d:\\d\\d", "")
                .trim();

        query = query.replaceFirst("latest=\\d\\d/\\d\\d/\\d\\d\\d\\d:\\d\\d:\\d\\d:\\d\\d", "")
                .trim();

        String start = "";
        String end = "";

        if (startDate != null) {
            start = " earliest=" + dateFormatter.format(startDate);
        }

        if (endDate != null) {
            end = " latest=" + dateFormatter.format(endDate);
        }

        query = String.format("search *%s%s %s", start, end, query);

        return query;
    }

    public static String buildQuery(String input) {
        return buildQuery(input, null, null);
    }
}
