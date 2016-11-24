package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.MetricDefinitions;
import com.onsdigital.performance.reporter.model.Metrics;

/**
 * Generic interface for gathering metrics.
 */
public interface MetricsProvider {

    /**
     * Generic method to retrieve Metrics from the given MetricDefinitions.
     * @param metricDefinitions - defines the metrics to gather.
     * @return = the populated metrics.
     */
    Metrics getMetrics(MetricDefinitions metricDefinitions);
}
