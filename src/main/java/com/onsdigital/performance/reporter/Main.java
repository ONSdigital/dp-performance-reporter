package com.onsdigital.performance.reporter;

import com.onsdigital.performance.reporter.google.GoogleAnalyticsProvider;
import com.onsdigital.performance.reporter.interfaces.FileUploader;
import com.onsdigital.performance.reporter.interfaces.MetricsProvider;
import com.onsdigital.performance.reporter.interfaces.ResponseTimeProvider;
import com.onsdigital.performance.reporter.model.Metrics;
import com.onsdigital.performance.reporter.pingdom.PingdomResponseTimeProvider;
import com.onsdigital.performance.reporter.s3.s3FileUploader;
import com.onsdigital.performance.reporter.splunk.SplunkMetricsProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.ParseException;
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
            MetricsProvider metricsProvider = new SplunkMetricsProvider();
            Metrics metrics = metricsProvider.getMetrics();
            fileUploader.uploadJsonForObject(metrics, "metrics.json");

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.debug("Finished generating metrics report.");
    }

    private static void RunResponseTimesReport(FileUploader fileUploader) {
        log.debug("Gathering response times from Pingdom.");

        try {
            ResponseTimeProvider responseTimeProvider = new PingdomResponseTimeProvider();
            Metrics metrics = responseTimeProvider.getResponseTimes();
            fileUploader.uploadJsonForObject(metrics, "responsetimes.json");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        log.debug("Finished generating response times report.");
    }

    private static void RunAnalyticsReport(FileUploader fileUploader) {
        log.debug("Gathering analytics data from Google.");

        Metrics analytics;
        try {
            analytics = new GoogleAnalyticsProvider().getAnalytics();
            fileUploader.uploadJsonForObject(analytics, "analytics.json");
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.debug("Finished generating analytics report.");
    }
}
