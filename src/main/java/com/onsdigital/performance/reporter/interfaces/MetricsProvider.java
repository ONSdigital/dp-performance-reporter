package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.Metrics;

import java.io.IOException;
import java.text.ParseException;

/**
 * Generic interface for gathering metrics.
 */
public interface MetricsProvider {

    Metrics getMetrics() throws IOException, ParseException, InterruptedException;
}
