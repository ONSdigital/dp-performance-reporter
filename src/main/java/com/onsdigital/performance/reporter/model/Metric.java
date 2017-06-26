package com.onsdigital.performance.reporter.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Metric {
    public String name;
    public MetricDefinition definition;
    public Map<String, String> tags;
    public List<String> columns;
    public List<List<String>> values;

    public Date lastRun;
}
