package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.Metrics;

public interface MetricsProvider {

    Metrics getMetrics(String dbName, String metricName);

}
