package com.onsdigital.performance.reporter.model;

import java.util.Map;

public class MetricDefinition {
    public String name;
    public Frequency frequency;
    public Map<String,String> query;
    public Map<String,String> meta;
    public String providerClass;
}
