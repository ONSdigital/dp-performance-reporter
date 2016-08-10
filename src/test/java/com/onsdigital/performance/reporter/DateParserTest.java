package com.onsdigital.performance.reporter;

import com.onsdigital.performance.reporter.util.DateParser;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DateParserTest {

    @Test
    public void parseShouldParseDate() throws Exception {
        String input = "2016-06-22";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.of(2016, 06, 22).atStartOfDay()).getTime();

        Date result = DateParser.parse(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseShouldParseToday() throws Exception {
        String input = "today";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay()).getTime();

        Date result = DateParser.parse(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseShouldParseYesterday() throws Exception {
        String input = "yesterday";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay().minusDays(1)).getTime();

        Date result = DateParser.parse(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }

    @Test
    public void parseShouldParseDaysAgo() throws Exception {
        String input = "5daysAgo";
        long expectedTimestamp = Timestamp.valueOf(LocalDate.now().atStartOfDay().minusDays(5)).getTime();

        Date result = DateParser.parse(input);

        assertNotNull(result);
        assertEquals(expectedTimestamp, result.getTime());
    }
}
