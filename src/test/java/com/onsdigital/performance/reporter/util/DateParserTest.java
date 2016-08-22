package com.onsdigital.performance.reporter.util;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DateParserTest {

    @Test
    public void parseStartDateShouldParseDate() throws Exception {
        String input = "2016-06-22";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.of(2016, 06, 22).atStartOfDay()).getTime();

        Date result = DateParser.parseStartDate(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseStartDateShouldParseToday() throws Exception {
        String input = "today";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay()).getTime();

        Date result = DateParser.parseStartDate(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseStartDateShouldParseYesterday() throws Exception {
        String input = "yesterday";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay().minusDays(1)).getTime();

        Date result = DateParser.parseStartDate(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseStartDateShouldParseDaysAgo() throws Exception {
        String input = "5daysAgo";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay().minusDays(5)).getTime();

        Date result = DateParser.parseStartDate(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseEndDateShouldParseDate() throws Exception {
        String input = "2016-06-22";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.of(2016, 06, 22)
                .atStartOfDay()
                .plusDays(1))
                .getTime();

        Date result = DateParser.parseEndDate(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseEndDateShouldParseToday() throws Exception {
        String input = "today";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusDays(1)).getTime();

        Date result = DateParser.parseEndDate(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseEndDateShouldParseYesterday() throws Exception {
        String input = "yesterday";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay()).getTime();

        Date result = DateParser.parseEndDate(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseEndDateShouldParseDaysAgo() throws Exception {
        String input = "5daysAgo";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay().minusDays(4)).getTime();

        Date result = DateParser.parseEndDate(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }
}
