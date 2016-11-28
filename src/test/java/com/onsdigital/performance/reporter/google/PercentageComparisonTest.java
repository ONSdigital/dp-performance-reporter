package com.onsdigital.performance.reporter.google;

import com.onsdigital.performance.reporter.model.MetricDefinition;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PercentageComparisonTest {

    private PercentageComparison percentageComparison = new PercentageComparison();

    @Test
    public void calculateResultRowValuesReturnsCorrectValues() throws Exception {

        // Given two input rows with a label in the first column and numbers in the others
        List<String> rowA = Arrays.asList("2016-02", "20", "40");
        List<String> rowB = Arrays.asList("2016-02", "200", "800");

        // When the calculateResultRowValues function is called
        ArrayList<String> result = percentageComparison.calculateResultRowValues(rowA, rowB);

        // Then the returned row has the correctly calculated values
        Assert.assertEquals("2016-02", result.get(0));
        Assert.assertEquals("10.0", result.get(1)); // 20 as a percent of 200 = 10%
        Assert.assertEquals("5.0", result.get(2)); // 40 as a percent of 800 = 5%
    }

    @Test
    public void calculateResultRowValuesReturnsOriginalValueIfNotANumber() throws Exception {

        // Given two input rows, with values that are not valid numbers
        List<String> inputRow = Arrays.asList("2016", "this is clearly not a number");

        // When the calculateResultRowValues function is called
        ArrayList<String> resultRow = percentageComparison.calculateResultRowValues(inputRow, inputRow);

        // Then the result row has the original values in it.
        Assert.assertEquals(inputRow.get(0), resultRow.get(0));
        Assert.assertEquals(inputRow.get(1), resultRow.get(1));
    }

    @Test
    public void calculatePercentageReturnsExpectedPercentage() throws Exception {
        
        // Given input values of 40 and 200
        int a = 40;
        int b = 200;

        // When the calculatePercentage function is called
        double result = PercentageComparison.calculatePercentage(a, b);

        // Then the result is 20 - the correct percentage value
        double expected = 20; // 40 as a percentage of 200 = 20
        Assert.assertEquals(expected, result, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDefinitionWithFilterShouldThrowExceptionIfSourceDefinitionIsNull() throws Exception {
        percentageComparison.createDefinitionUsingFilter("", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDefinitionWithFilterShouldThrowExceptionIfMetadataIsNull() throws Exception {
        MetricDefinition sourceDefinition = new MetricDefinition();
        percentageComparison.createDefinitionUsingFilter("", sourceDefinition);
    }

    @Test
    public void createDefinitionWithDimensionShouldCopyQuery() throws Exception {

        // Given a metric definition with a query
        MetricDefinition sourceDefinition = new MetricDefinition();
        sourceDefinition.meta = new HashMap<>();
        sourceDefinition.meta.put("filterA", "this is a filter");
        sourceDefinition.query = new HashMap<>();
        sourceDefinition.query.put("testee", "woo");

        // When the createDefinitionUsingFilter function is called
        MetricDefinition result = percentageComparison.createDefinitionUsingFilter("filterA", sourceDefinition);

        // Then the query in the result contains the values that were in the original definitions query.
        Assert.assertEquals(result.query.get("testee"), "woo");
        Assert.assertEquals(result.query.get("filters"), "this is a filter");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDefinitionWithoutFilterShouldThrow() throws Exception {

        // Given a metric definition with a query, but not a filter
        MetricDefinition sourceDefinition = new MetricDefinition();
        sourceDefinition.query = new HashMap<>();
        sourceDefinition.meta = new HashMap<>();
        sourceDefinition.query.put("testee", "woo");

        // When the createDefinitionUsingFilter function is called
        MetricDefinition result = percentageComparison.createDefinitionUsingFilter("filterA", sourceDefinition);

        // Then an exception is thrown because no filter was found.
    }
}
