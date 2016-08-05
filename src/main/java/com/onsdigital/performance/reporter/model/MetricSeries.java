package com.onsdigital.performance.reporter.model;

import java.util.List;
import java.util.Map;

public class MetricSeries {
    public String name;
    public Map<String, String> tags;
    public List<String> columns;
    public List<List<Object>> values;
}
