package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.Metrics;

import java.io.IOException;
import java.text.ParseException;

public interface ResponseTimeProvider {

    Metrics getResponseTimes() throws IOException, ParseException;

}
