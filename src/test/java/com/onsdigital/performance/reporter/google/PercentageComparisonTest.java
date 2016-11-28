package com.onsdigital.performance.reporter.google;

import com.onsdigital.performance.reporter.interfaces.MetricProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.MetricDefinition;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PercentageComparisonTest {

    private PercentageComparison percentageComparison = new PercentageComparison();

    @Test(expected = IllegalArgumentException.class)
    public void getMetricShouldThrowExceptionIfSourceDefinitionIsNull() throws Exception {
        percentageComparison.getMetric(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMetricShouldThrowExceptionIfMetadataIsNull() throws Exception {
        MetricDefinition sourceDefinition = new MetricDefinition();
        percentageComparison.getMetric(sourceDefinition);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMetricShouldThrowExceptionIfMetadataDoesNotContainComparisonFieldValue() throws Exception {

        // Given a MetricDefinition without specifying a ComparisonField in the metadata.
        MetricDefinition sourceDefinition = new MetricDefinition();
        sourceDefinition.meta = new HashMap<>();

        // When getMetric is called
        percentageComparison.getMetric(sourceDefinition);

        // Then an IllegalArgumentException is thrown.
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMetricShouldThrowExceptionIfMetadataDoesNotContainComparisonFieldA() throws Exception {
        MetricDefinition sourceDefinition = new MetricDefinition();
        sourceDefinition.meta = new HashMap<>();
        sourceDefinition.meta.put("comparisonField", "filters");
        percentageComparison.getMetric(sourceDefinition);
    }

    @Test
    public void getMetricWithNullQueryShouldStillExecute() throws Exception {

        // Given a metric definition with a null query.
        MetricDefinition metricDefinition = new MetricDefinition();
        metricDefinition.meta = new HashMap<>();
        metricDefinition.meta.put("comparisonField", "filters");
        metricDefinition.meta.put("comparisonValueA", "filters");
        metricDefinition.meta.put("comparisonValueB", "filters");

        percentageComparison.setMetricProvider(new DummyMetricProvider(null, null));

        // When the createDefinitionUsingFilter function is called
        percentageComparison.getMetric(metricDefinition);

        // Then an exception is thrown because no filter was found.
    }

    @Test
    public void getMetricShouldUseTheSourceQuery() throws Exception {

        // Given a MetricDefinition that defines a query.
        MetricDefinition metricDefinition = new MetricDefinition();
        metricDefinition.meta = new HashMap<>();
        metricDefinition.meta.put("comparisonField", "filters");
        metricDefinition.meta.put("comparisonValueA", "filters");
        metricDefinition.meta.put("comparisonValueB", "filters");
        metricDefinition.query = new HashMap<>();
        metricDefinition.query.put("dimension", "date");

        // When getMetric is called having set the DummyMetricProvider
        DummyMetricProvider dummyMetricProvider = new DummyMetricProvider(null, null);
        percentageComparison.setMetricProvider(dummyMetricProvider);
        percentageComparison.getMetric(metricDefinition);

        // Then the query sent to the MetricProvider should contain the query from the input MetricProvider.
        Assert.assertTrue(dummyMetricProvider.getMetricDefinition1().query.containsKey("dimension"));
        Assert.assertEquals("date", dummyMetricProvider.getMetricDefinition1().query.get("dimension"));

        Assert.assertTrue(dummyMetricProvider.getMetricDefinition2().query.containsKey("dimension"));
        Assert.assertEquals("date", dummyMetricProvider.getMetricDefinition2().query.get("dimension"));
    }

    @Test
    public void getMetricShouldUseTheSourceMeta() throws Exception {

        // Given a MetricDefinition that defines a comparison filter in the meta map
        MetricDefinition metricDefinition = new MetricDefinition();
        metricDefinition.meta = new HashMap<>();
        metricDefinition.meta.put("comparisonField", "filters");
        metricDefinition.meta.put("comparisonValueA", "filters for request A");
        metricDefinition.meta.put("comparisonValueB", "filters for request B");
        metricDefinition.query = new HashMap<>();

        // When getMetric is called having set the DummyMetricProvider
        DummyMetricProvider dummyMetricProvider = new DummyMetricProvider(null, null);
        percentageComparison.setMetricProvider(dummyMetricProvider);
        percentageComparison.getMetric(metricDefinition);

        // Then the query sent to the MetricProvider should contain the values defines in the meta section for the comparison.
        Assert.assertTrue(dummyMetricProvider.getMetricDefinition1().query.containsKey("filters"));
        Assert.assertEquals("filters for request A", dummyMetricProvider.getMetricDefinition1().query.get("filters"));

        Assert.assertTrue(dummyMetricProvider.getMetricDefinition2().query.containsKey("filters"));
        Assert.assertEquals("filters for request B", dummyMetricProvider.getMetricDefinition2().query.get("filters"));
    }

    @Test
    public void getMetricReturnsExpectedValue() throws Exception {

        // Given two stubbed responses from a metrics provider
        Metric metric1 = new Metric();
        metric1.values = new ArrayList<>();
        metric1.values.add(Arrays.asList("2016-02", "20", "40"));

        Metric metric2 = new Metric();
        metric2.values = new ArrayList<>();
        metric2.values.add(Arrays.asList("2016-02", "200", "800"));

        MetricProvider dummyMetricProvider = new DummyMetricProvider(metric1, metric2);
        percentageComparison.setMetricProvider(dummyMetricProvider);

        MetricDefinition metricDefinition = new MetricDefinition();
        metricDefinition.meta = new HashMap<>();
        metricDefinition.meta.put("comparisonField", "filters");
        metricDefinition.meta.put("comparisonValueA", "filters");
        metricDefinition.meta.put("comparisonValueB", "filters");
        metricDefinition.query = new HashMap<>();

        // When getMetric is called
        Metric resultMetric = percentageComparison.getMetric(metricDefinition);

        // Then the expected response is returned with calculated percentages.
        List<String> firstRowOfValues = resultMetric.values.get(0);
        Assert.assertEquals("2016-02", firstRowOfValues.get(0));
        Assert.assertEquals("10.0", firstRowOfValues.get(1)); // 20 as a percent of 200 = 10%
        Assert.assertEquals("5.0", firstRowOfValues.get(2)); // 40 as a percent of 800 = 5%
    }

    @Test
    public void getMetricReturnsExpectedColumns() throws Exception {

        // Given two stubbed responses from a metrics provider with column headers.
        Metric metric1 = new Metric();
        metric1.values = new ArrayList<>();
        metric1.columns = Arrays.asList("date", "value1", "value2");
        metric1.values.add(Arrays.asList("2016-02", "20", "40"));

        Metric metric2 = new Metric();
        metric2.values = new ArrayList<>();
        metric2.values.add(Arrays.asList("2016-02", "200", "800"));

        MetricProvider dummyMetricProvider = new DummyMetricProvider(metric1, metric2);
        percentageComparison.setMetricProvider(dummyMetricProvider);

        MetricDefinition metricDefinition = new MetricDefinition();
        metricDefinition.meta = new HashMap<>();
        metricDefinition.meta.put("comparisonField", "filters");
        metricDefinition.meta.put("comparisonValueA", "filters");
        metricDefinition.meta.put("comparisonValueB", "filters");
        metricDefinition.query = new HashMap<>();

        // When getMetric is called
        Metric resultMetric = percentageComparison.getMetric(metricDefinition);

        // Then the expected response is returned with column headers
        Assert.assertEquals("date", resultMetric.columns.get(0));
        Assert.assertEquals("value1", resultMetric.columns.get(1));
        Assert.assertEquals("value2", resultMetric.columns.get(2));
    }
}
