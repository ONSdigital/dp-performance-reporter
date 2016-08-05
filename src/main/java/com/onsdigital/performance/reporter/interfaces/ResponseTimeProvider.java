package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.ResponseTimes;

import java.io.IOException;

public interface ResponseTimeProvider {

    ResponseTimes getResponseTimes(String checkIdentifier) throws IOException;

}
