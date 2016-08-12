package com.onsdigital.performance.reporter.splunk;

import com.google.gson.Gson;
import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.interfaces.MetricsProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.MetricDefinition;
import com.onsdigital.performance.reporter.model.MetricDefinitions;
import com.onsdigital.performance.reporter.model.Metrics;
import com.onsdigital.performance.reporter.splunk.model.Result;
import com.onsdigital.performance.reporter.util.DateParser;
import com.onsdigital.performance.reporter.util.MetricDefinitionsReader;
import com.splunk.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SplunkMetricsProvider implements MetricsProvider {

    private static Log log = LogFactory.getLog(SplunkMetricsProvider.class);

    private static Service splunkService;
    private static Gson gson = new Gson();

    public SplunkMetricsProvider() {
        // https://answers.splunk.com/answers/67327/splunk-java-sdk-connection-to-splunk-failed.html
        // "The new Java sdk no longer implements SSL v3, instead TLS v1.2 should be used"
        HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

        // read login credentials from environment variables.
        ServiceArgs loginArguments = new ServiceArgs();
        loginArguments.setUsername(Configuration.getSplunkUsername());
        loginArguments.setPassword(Configuration.getSplunkPassword());
        loginArguments.setHost(Configuration.getSplunkHost());
        loginArguments.setPort(Configuration.getSplunkPort());

        splunkService = Service.connect(loginArguments);
    }

    @Override
    public Metrics getMetrics() throws IOException, ParseException, InterruptedException {

        // Read definitions of metrics to gather from JSON config file.
        MetricDefinitions metricDefinitions = MetricDefinitionsReader.instance().readMetricDefinitions("splunkReports.json");
        Metrics metrics = new Metrics();

        for (MetricDefinition metricDefinition : metricDefinitions.metrics) {

            log.debug("Running Splunk query: " + metricDefinition.name);
            Metric metric = getMetric(metricDefinition);

            // Put the original metrics definition into the metric.
            metric.name = metricDefinition.name;
            metric.definition = metricDefinition;

            metrics.add(metric);
        }

        return metrics;
    }

    private Metric getMetric(MetricDefinition metricDefinition) throws ParseException, InterruptedException, IOException {
        Metric metric = null;

        String start = metricDefinition.query.get("start-date");
        String end = metricDefinition.query.get("end-date");

        Date startDate = DateParser.parse(start);
        Date endDate = DateParser.parse(end);

        String query = metricDefinition.query.get("query");

        // run the query against Splunk
        Job job = splunkService.getJobs().create(query);
        while (!job.isDone())
            Thread.sleep(10);

        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);

        try (InputStream results = job.getResults(resultsArgs)){
            Result result = gson.fromJson(new InputStreamReader(results), Result.class);
            metric = MapResultToMetric(result);
        }

        return metric;
    }

    /**
     * Map the splunk result model to the generic metric model.
     * @param result
     * @return
     */
    private Metric MapResultToMetric(Result result) {

        Metric metric = new Metric();

        // take the name from each 'field' for the column headers
        metric.columns = new ArrayList<>();
        for (Map<String, String> field : result.fields) {
            metric.columns.add(field.get("name"));
        }

        // The results are a map which do not always contain a value.
        metric.values = new ArrayList<>();
        for (Map<String, String> row : result.results) {
            List<String> values = new ArrayList<>();

            // iterate each expected column, and default to zero if the value is not there.
            for (String column : metric.columns) {
                String value = row.get(column);

                if (value == null)
                    value = "0";

                values.add(value);
            }

            metric.values.add(values);
        }

        return metric;
    }
}
