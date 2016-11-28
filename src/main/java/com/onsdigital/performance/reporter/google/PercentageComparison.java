package com.onsdigital.performance.reporter.google;

import com.onsdigital.performance.reporter.interfaces.CompositeMetricProvider;
import com.onsdigital.performance.reporter.interfaces.MetricProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.MetricDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enables a percentage of two google analytics dimensions to be calculated.
 * <p>
 * A single MetricDefinition is provided which defines a base analytics query.
 * Two separate dimension values are provided in the meta field of the Metric.
 * <p>
 * A single metric is returned which provides a percentage of the first dimension compared to the other.
 * <p>
 * Example: % of users that have downloaded from a dataset page.
 * Query: User sessions
 * Dimension A: User sessions that have looked at a dataset page and have a download event
 * Dimension B: User sessions that have looked at a dataset page
 */
public class PercentageComparison implements CompositeMetricProvider {

    private static final String ComparisonFieldKey = "comparisonField";
    private MetricProvider metricProvider;

    @Override
    public Metric getMetric(MetricDefinition metricDefinition) throws IOException {

        Metric metricA = getInputMetric(metricDefinition, "comparisonValueA");
        Metric metricB = getInputMetric(metricDefinition, "comparisonValueB");

        return createOutputMetric(metricA, metricB);
    }

    private Metric getInputMetric(MetricDefinition metricDefinition, String dimension) throws IOException {
        // Take the given metric definition and copy the query from it into two separate definitions.
        MetricDefinition request = createDefinitionUsingFilter(metricDefinition, dimension);

        // Call out to Google analytics for the individual metric data to compare.
        return metricProvider.getMetric(request);
    }

    private static Metric createOutputMetric(Metric metricA, Metric metricB) {
        // Create the result metric
        Metric metric = new Metric();
        metric.values = new ArrayList<>();

        if (metric.columns != null)
        metric.columns = metricA.columns; // copy column headers

        metric.values = calculateMetricValues(metricA, metricB);

        return metric;
    }

    private static List<List<String>> calculateMetricValues(Metric metricA, Metric metricB) {
        List<List<String>> values = new ArrayList<>();

        if (metricA != null && metricB != null) {
            for (int rowIndex = 0; rowIndex < metricA.values.size(); rowIndex++) {
                List<String> rowA = metricA.values.get(rowIndex);
                List<String> rowB = metricB.values.get(rowIndex);

                ArrayList<String> newRow = calculateResultRowValues(rowA, rowB);
                values.add(newRow);
            }
        }
        return values;
    }

    /**
     * calculate the result values from the given rows.
     *
     * @param rowA - the first row to use values from
     * @param rowB - the second row to use values from
     * @return - the row of result values
     */
    private static ArrayList<String> calculateResultRowValues(List<String> rowA, List<String> rowB) {
        ArrayList<String> newRow = new ArrayList<>();
        newRow.add(rowA.get(0)); // add the date label in the first column.

        for (int columnIndex = 1; columnIndex < rowA.size(); columnIndex++) {
            String calculatedValue;
            try {

                double a = Double.parseDouble(rowA.get(columnIndex));
                double b = Double.parseDouble(rowB.get(columnIndex));
                calculatedValue = Double.toString(calculatePercentage(a, b));

            } catch(NumberFormatException e) {
                calculatedValue = rowA.get(columnIndex);
            }

            newRow.add(calculatedValue);
        }
        return newRow;
    }

    /**
     * Create a new MetricDefinition, using the query from the given MetricDefinition, and also adding a dimension
     * value to the query as defined in the metadata of the sourceDefinition.
     *
     * @param sourceDefinition - The MetricDefinition to create the result from.
     * @param filterKey - The key of the filter in the meta map
     * @return - The newly created MetricDefinition.
     */
    private static MetricDefinition createDefinitionUsingFilter(MetricDefinition sourceDefinition, String filterKey) {

        if (sourceDefinition == null)
            throw new IllegalArgumentException("parameter MetricDefinition cannot be null.");

        if (sourceDefinition.meta == null)
            throw new IllegalArgumentException("parameter MetricDefinition cannot have null meta field.");

        if (!sourceDefinition.meta.containsKey(ComparisonFieldKey))
            throw new IllegalArgumentException("Could not find a value for 'comparisonField' in the meta data map of the sourceDefinition.");

        if (!sourceDefinition.meta.containsKey(filterKey))
            throw new IllegalArgumentException("Could not find the filter: " + filterKey + " in the meta data map of the sourceDefinition");

        MetricDefinition result = new MetricDefinition();

        result.query = new HashMap<>();

        if (sourceDefinition.query != null) {
            for (Map.Entry<String, String> entry : sourceDefinition.query.entrySet()) {
                result.query.put(entry.getKey(), entry.getValue());
            }
        }

        result.query.put(ComparisonFieldKey, sourceDefinition.meta.get(filterKey));
        return result;
    }

    /**
     * Setter to allow for dependency injection
     *
     * @param metricProvider
     */
    @Override
    public void setMetricProvider(MetricProvider metricProvider) {
        this.metricProvider = metricProvider;
    }

    // determine the percentage of a compared to b.
    private static double calculatePercentage(double a, double b) {
        return (100 / b) * a;
    }
}
