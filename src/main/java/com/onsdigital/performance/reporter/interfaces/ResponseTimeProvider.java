package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.Metrics;

import java.io.IOException;

public interface ResponseTimeProvider {

    Metrics getResponseTimes() throws IOException;

}
