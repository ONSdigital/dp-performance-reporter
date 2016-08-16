package com.onsdigital.performance.reporter.google;

import com.google.api.services.analytics.model.GaData;
import com.onsdigital.performance.reporter.model.Metric;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GoogleAnalyticsProviderTest {

    @Test
    public void mapDataToMetricShouldMapAllExpectedData() throws Exception {

        // Given a response object from google analytics
        GaData gaData = buildDummyGaData();

        // When the mapDataToMetric method is called
        Metric metric = GoogleAnalyticsProvider.mapDataToMetric(gaData);

        // Then the returned metric object has all expected values mapped
        assertNotNull(metric);

        assertEquals(2, metric.columns.size());
        assertEquals("time", metric.columns.get(0));
        assertEquals("value", metric.columns.get(1));

        assertEquals(1, metric.values.size());
        assertEquals(2, metric.values.get(0).size());
        assertEquals("123", metric.values.get(0).get(0));
        assertEquals("321", metric.values.get(0).get(1));

    }

    private GaData buildDummyGaData() {
        GaData gaData = new GaData();
        List<GaData.ColumnHeaders> headers = getColumnHeaders();
        gaData.setColumnHeaders(headers);

        List<List<String>> rows = new ArrayList<>();

        List<String> row = new ArrayList<>();
        row.add("123");
        row.add("321");
        rows.add(row);

        gaData.setRows(rows);
        return gaData;
    }


    private List<GaData.ColumnHeaders> getColumnHeaders() {
        List<GaData.ColumnHeaders> headers = new ArrayList<>();

        GaData.ColumnHeaders timeHeader = new GaData.ColumnHeaders();
        timeHeader.setName("time");
        headers.add(timeHeader);

        GaData.ColumnHeaders valueHeader = new GaData.ColumnHeaders();
        valueHeader.setName("value");
        headers.add(valueHeader);

        return headers;
    }
}
