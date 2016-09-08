package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.Metrics;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/**
 * Generic interface for gathering metrics.
 */
public interface MetricsProvider {

    Metrics getMetrics() throws FileNotFoundException, URISyntaxException;
}
