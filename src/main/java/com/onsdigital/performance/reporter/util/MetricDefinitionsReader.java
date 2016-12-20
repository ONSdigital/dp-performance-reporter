package com.onsdigital.performance.reporter.util;

import com.google.gson.Gson;
import com.onsdigital.performance.reporter.model.MetricDefinitions;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MetricDefinitionsReader {

    private static MetricDefinitionsReader reportDefinitionsReader = new MetricDefinitionsReader();

    private MetricDefinitionsReader() { }

    // Get singleton instance
    public static MetricDefinitionsReader instance() { return reportDefinitionsReader; }

    /**
     * Read metric definitions from the given JSON filename
     * @param filename - the filename to read the definitions from.
     * @return - the populated MetricDefinitions.
     * @throws FileNotFoundException
     */
    public MetricDefinitions readMetricDefinitions(String filename) throws FileNotFoundException {
        String file = getFilePath(filename);
        FileReader fileReader = new FileReader(file);
        return new Gson().fromJson(fileReader, MetricDefinitions.class);
    }

    String getFilePath(String filename) throws FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(filename);

        if (resource == null)
            throw new FileNotFoundException(filename);

        try {
            // URL.getPath URL encodes the path which causes an issue when the path is already URL encoded.
            // Using URI.getPath so that its not URL encoded again.
            URI uri = new URI(resource.toString());

            return uri.getPath();
        } catch (URISyntaxException e) {
            throw new FileNotFoundException(String.format("The filename %s was not found. The format of the filename is invalid.", filename));
        }
    }
}
