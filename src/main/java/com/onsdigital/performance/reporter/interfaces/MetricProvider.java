package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.MetricDefinition;

/**
 * Generic interface for gathering metrics.
 */
public interface MetricProvider {

    /**
     * Generic method to retrieve an individual Metric from the given MetricDefinition.
     * @param metricDefinition - defines the metric to gather.
     * @return = the populated metric.
     */
    Metric getMetric(MetricDefinition metricDefinition);
}
