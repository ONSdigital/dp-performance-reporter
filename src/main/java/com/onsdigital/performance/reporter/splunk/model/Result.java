package com.onsdigital.performance.reporter.splunk.model;

import java.util.List;
import java.util.Map;

/**
 * The result model that is returned from a Splunk query.
 */
public class Result {

    public List<Map<String,String>> fields; // aka column headers and metadata
    public List<Map<String,String>> results; // the actual data

}
