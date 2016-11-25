package com.onsdigital.performance.reporter;

import com.onsdigital.performance.reporter.google.GoogleAnalyticsProvider;
import com.onsdigital.performance.reporter.interfaces.FileUploader;
import com.onsdigital.performance.reporter.interfaces.MetricProvider;
import com.onsdigital.performance.reporter.model.*;
import com.onsdigital.performance.reporter.pingdom.PingdomResponseTimeProvider;
import com.onsdigital.performance.reporter.s3.s3FileUploader;
import com.onsdigital.performance.reporter.splunk.SplunkMetricsProvider;
import com.onsdigital.performance.reporter.util.MetricDefinitionsReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static Log log = LogFactory.getLog(Main.class);
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        try {
            while (true) {
                updateReports();
                Thread.sleep(5 * 60 * 1000); // 5 minutes
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void updateReports() {

        final FileUploader fileUploader = new s3FileUploader();
        log.debug("Starting performance reporter.");

        // Run Google analytics report on another thread.
        executorService.submit(() -> RunAnalyticsReport(fileUploader));

        // Run Pingdom response times report on another thread.
        executorService.submit(() -> RunResponseTimesReport(fileUploader));

        // Run Splunk reports
        executorService.submit(() -> RunMetricsReport(fileUploader));
    }

    private static void RunMetricsReport(FileUploader fileUploader) {
        log.debug("Gathering metrics from Splunk.");

        try {
            MetricDefinitions metricDefinitions = MetricDefinitionsReader.instance().readMetricDefinitions("splunkReports.json");
            MetricProvider metricProvider = new SplunkMetricsProvider();

            // Read definitions of metrics to gather from JSON config file.
            Metrics metrics = new Metrics();

            for (MetricDefinition metricDefinition : metricDefinitions.metrics) {

                log.debug("Running Splunk query: " + metricDefinition.name);
                Metric metric;
                try {
                    metric = metricProvider.getMetric(metricDefinition);

                    // Put the original metrics definition into the metric.
                    metric.name = metricDefinition.name;
                    metric.definition = metricDefinition;
                    metrics.add(metric);
                } catch (Exception e) {
                    log.error("Exception getting Splunk metric: " + metricDefinition.name, e);
                }
            }

            fileUploader.uploadJsonForObject(metrics, "metrics.json");

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.debug("Finished generating metrics report.");
    }

    private static void RunResponseTimesReport(FileUploader fileUploader) {
        log.debug("Gathering response times from Pingdom.");

        try {
            MetricDefinitions metricDefinitions = MetricDefinitionsReader.instance().readMetricDefinitions("pingdomReports.json");
            MetricProvider responseTimeProvider = new PingdomResponseTimeProvider();
            Metrics metrics = new Metrics();

            for (MetricDefinition metricDefinition : metricDefinitions.metrics) {

                log.debug("Running Pingdom report: " + metricDefinition.name);

                try {
                    Metric metric = responseTimeProvider.getMetric(metricDefinition);
                    metric.name = metricDefinition.name;
                    metric.definition = metricDefinition;
                    metrics.add(metric);
                } catch (IOException e) {
                    log.error("Exception getting Pingdom metric: " + metricDefinition.name, e);
                }
            }

            fileUploader.uploadJsonForObject(metrics, "responsetimes.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.debug("Finished generating response times report.");
    }

    private static void RunAnalyticsReport(FileUploader fileUploader) {
        log.debug("Gathering analytics data from Google.");

        try {
            MetricProvider googleAnalyticsProvider = new GoogleAnalyticsProvider();
            MetricDefinitions metricDefinitions = MetricDefinitionsReader.instance().readMetricDefinitions("googleAnalyticsReports.json");

            Metrics metrics = new Metrics();

            for (MetricDefinition metricDefinition : metricDefinitions.metrics) {

                log.debug("Running Google Analytics report: " + metricDefinition.name);

                try {
                    Metric metric;

                    if (metricDefinition.providerClass == null || metricDefinition.providerClass.isEmpty()) {
                        metric = googleAnalyticsProvider.getMetric(metricDefinition);
                    } else {
                        MetricProvider metricProvider = (MetricProvider) Class.forName(metricDefinition.providerClass).newInstance();
                        metric = metricProvider.getMetric(metricDefinition);
                    }

                    if (metric != null) {
                        metric.name = metricDefinition.name;
                        metric.definition = metricDefinition;
                        metrics.add(metric);
                    }

                } catch (IOException e) {
                    log.error("Exception getting Google Analytics metric: " + metricDefinition.name, e);
                }
            }

            fileUploader.uploadJsonForObject(metrics, "analytics.json");
        } catch (Exception e) {
            log.error("Unexpected exception thrown when running analytics report.", e);
        }

        log.debug("Finished generating analytics report.");
    }
}
