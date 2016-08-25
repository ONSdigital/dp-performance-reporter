package com.onsdigital.performance.reporter.util;

import com.onsdigital.performance.reporter.model.MetricDefinitions;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertNotNull;

public class MetricDefinitionsReaderTest {

    @Test
    public void readMetricDefinitionsShouldReadJsonConfigFile() throws Exception {
        // Given a JSON config file
        String filename = "exampleReports.json";

        // When the readMetricDefinitions method is called
        MetricDefinitions metricDefinitions = MetricDefinitionsReader.instance().readMetricDefinitions(filename);

        // The JSON is parsed into the metricsDefinitions model
        assertNotNull(metricDefinitions);
    }

    @Test(expected = FileNotFoundException.class)
    public void readMetricDefinitionsShouldThrowFileNotFound() throws Exception {
        // Given a JSON config file that does not exist
        String filename = "doesNotExist.json";

        // When the readMetricDefinitions method is called
        MetricDefinitions metricDefinitions = MetricDefinitionsReader.instance().readMetricDefinitions(filename);

        // The JSON is parsed into the metricsDefinitions model
        assertNotNull(metricDefinitions);
    }
}
