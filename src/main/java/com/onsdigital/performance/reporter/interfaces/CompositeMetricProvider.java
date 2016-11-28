package com.onsdigital.performance.reporter.interfaces;

/**
 * Interface to allow MetricProvider classes to be wrapped
 */
public interface CompositeMetricProvider extends MetricProvider {

    /**
     * Set the MetricProvider implementation to use within.
     * @param metricProvider
     */
    void setMetricProvider(MetricProvider metricProvider);
}
