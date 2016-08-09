package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.Metric;

import java.io.IOException;

public interface ResponseTimeProvider {

    Metric getResponseTimes(String checkIdentifier) throws IOException;

}
