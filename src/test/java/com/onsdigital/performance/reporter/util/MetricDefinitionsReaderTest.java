package com.onsdigital.performance.reporter.util;

import com.onsdigital.performance.reporter.model.MetricDefinitions;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void getFilenameShouldNotUrlEncode() throws Exception {
        // Given a JSON config file that does not exist
        String filename = "%2fExistsWithEncodedSlash.json";

        // When the readMetricDefinitions method is called
        String filePath = MetricDefinitionsReader.instance().getFilePath(filename);

        System.out.println("filePath = " + filePath);
        // The JSON is parsed into the metricsDefinitions model
        assertNotNull(filePath);
        assertTrue(filePath.endsWith(filename));
    }
}
