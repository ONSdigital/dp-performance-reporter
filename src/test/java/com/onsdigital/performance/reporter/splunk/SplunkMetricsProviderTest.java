package com.onsdigital.performance.reporter.splunk;

import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.splunk.model.Result;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SplunkMetricsProviderTest {

    @Test
    public void mapResultToMetricShouldHandleNullFields() throws Exception {
        Result result = new Result();
        Metric metric = SplunkMetricsProvider.MapResultToMetric(result);

        assertNotNull(metric);
        assertNotNull(metric.columns);
        assertNotNull(metric.values);
    }
}
