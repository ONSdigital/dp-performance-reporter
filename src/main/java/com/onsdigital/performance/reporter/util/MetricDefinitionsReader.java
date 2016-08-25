package com.onsdigital.performance.reporter.util;

import com.google.gson.Gson;
import com.onsdigital.performance.reporter.model.MetricDefinitions;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

public class MetricDefinitionsReader {

    private static MetricDefinitionsReader reportDefinitionsReader = new MetricDefinitionsReader();

    private MetricDefinitionsReader() { }

    // Get singleton instance
    public static MetricDefinitionsReader instance() { return reportDefinitionsReader; }

    /**
     * Read metric definitions from the given JSON filename
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public MetricDefinitions readMetricDefinitions(String filename) throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(filename);

        if (resource == null)
            throw new FileNotFoundException(filename);

        String file = resource.getFile();
        FileReader fileReader = new FileReader(file);
        MetricDefinitions reportDefinitions = new Gson().fromJson(fileReader, MetricDefinitions.class);
        return reportDefinitions;
    }
}
