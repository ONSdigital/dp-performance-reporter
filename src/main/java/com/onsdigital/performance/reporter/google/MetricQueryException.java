package com.onsdigital.performance.reporter.google;

/**
 * Exception to use when something is wrong comparing two Metrics
 */
public class MetricQueryException extends Exception {
    public MetricQueryException(String message) {
        super(message);
    }

    public MetricQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
