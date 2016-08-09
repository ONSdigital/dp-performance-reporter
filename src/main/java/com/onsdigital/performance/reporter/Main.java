package com.onsdigital.performance.reporter;

import com.onsdigital.performance.reporter.google.GoogleAnalyticsProvider;
import com.onsdigital.performance.reporter.influxdb.InfluxDbMetricsProvider;
import com.onsdigital.performance.reporter.interfaces.FileUploader;
import com.onsdigital.performance.reporter.interfaces.MetricsProvider;
import com.onsdigital.performance.reporter.interfaces.ResponseTimeProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.Metrics;
import com.onsdigital.performance.reporter.pingdom.PingdomResponseTimeProvider;
import com.onsdigital.performance.reporter.s3.s3FileUploader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {

    private static Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws Exception {

        FileUploader fileUploader = new s3FileUploader();

        log.debug("Starting performance reporter.");

        log.debug("Gathering analytics data from Google.");
        Metrics analytics = new GoogleAnalyticsProvider().getAnalytics();
        fileUploader.uploadJsonForObject(analytics, "analytics.json");

        log.debug("Gathering response times from Pingdom.");
        ResponseTimeProvider responseTimeProvider = new PingdomResponseTimeProvider();
        Metric metric = responseTimeProvider.getResponseTimes("Website (EN) CloudFlare");
        fileUploader.uploadJsonForObject(metric, "responsetimes.json");

        log.debug("Gathering metrics from InfluxDB.");
        MetricsProvider metricsProvider = new InfluxDbMetricsProvider();
        Metrics metrics = metricsProvider.getMetrics("aTimeSeries", "cpu");
        fileUploader.uploadJsonForObject(metrics, "metrics.json");

        log.debug("Finished");
    }
}
