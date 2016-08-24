package com.onsdigital.performance.reporter.splunk;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class SplunkQueryBuilderTest {

    @Test
    public void buildQueryShouldAddSearchIfMissing() throws Exception {
        String input = "(index=sandbox) metricsType=REQUEST_TIME";
        String expected = "search * (index=sandbox) metricsType=REQUEST_TIME";

        String actual = SplunkQueryBuilder.buildQuery(input);

        assertEquals(expected, actual);
    }

    @Test
    public void buildQueryShouldInsertStartAndEndDate() throws Exception {
        String input = "(index=sandbox)";
        String expected = "search * earliest=06/22/2016:00:00:00 latest=06/24/2016:00:00:00 (index=sandbox)";

        long startTime = Timestamp.valueOf(LocalDate.of(2016, 06, 22).atStartOfDay()).getTime();
        long endTime = Timestamp.valueOf(LocalDate.of(2016, 06, 24).atStartOfDay()).getTime();

        String actual = SplunkQueryBuilder.buildQuery(input, new Date(startTime), new Date(endTime));

        assertEquals(expected, actual);
    }

    @Test
    public void buildQueryShouldReplaceExistingStartAndEndDate() throws Exception {
        String input = "search * earliest=01/01/1970:00:00:00      latest=01/01/1970:00:00:00    (index=sandbox)";
        String expected = "search * earliest=06/22/2016:00:00:00 latest=06/24/2016:00:00:00 (index=sandbox)";

        long startTime = Timestamp.valueOf(LocalDate.of(2016, 06, 22).atStartOfDay()).getTime();
        long endTime = Timestamp.valueOf(LocalDate.of(2016, 06, 24).atStartOfDay()).getTime();

        String actual = SplunkQueryBuilder.buildQuery(input, new Date(startTime), new Date(endTime));

        assertEquals(expected, actual);
    }
}
