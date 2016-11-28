package com.onsdigital.performance.reporter.google;

import com.onsdigital.performance.reporter.interfaces.MetricProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.MetricDefinition;

import java.io.IOException;

/**
 * Dummy metric provider returns two different responses one after the other.
 */
public class DummyMetricProvider implements MetricProvider {

    private int callNumber = 1;

    private final Metric metric1;
    private final Metric metric2;

    private MetricDefinition metricDefinition1;
    private MetricDefinition metricDefinition2;

    public DummyMetricProvider(Metric metric1, Metric metric2) {
        this.metric1 = metric1;
        this.metric2 = metric2;
    }

    @Override
    public Metric getMetric(MetricDefinition metricDefinition) throws IOException {

        if (callNumber == 1) {
            metricDefinition1 = metricDefinition;
            callNumber++;
            return metric1;
        } else {
            metricDefinition2 = metricDefinition;
            callNumber = 1; // reset the call number to provide the first response again.
            return metric2;
        }
    }

    public MetricDefinition getMetricDefinition1() {
        return metricDefinition1;
    }

    public MetricDefinition getMetricDefinition2() {
        return metricDefinition2;
    }
}
