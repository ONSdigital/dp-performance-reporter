package com.onsdigital.performance.reporter.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.RealtimeData;
import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.ReportDefinitionsReader;
import com.onsdigital.performance.reporter.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GoogleAnalyticsProvider {

    private static Log log = LogFactory.getLog(GoogleAnalyticsProvider.class);

    private static final String APPLICATION_NAME = "dp-performance-reporter";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static Analytics analytics;
    private static String tableId;

    public GoogleAnalyticsProvider() throws Exception {
        analytics = initializeAnalytics();
        tableId = "ga:" + Configuration.getGoogleProfileId();
    }

    public Metrics getAnalytics() throws IOException {

        MetricDefinitions metricDefinitions = new ReportDefinitionsReader().readReportDefinitions("googleAnalyticsReports.json");

        Metrics metrics = new Metrics();

        for (MetricDefinition metricDefinition : metricDefinitions.metrics) {

            log.debug("Running Google Analytics report: " + metricDefinition.name);
            Metric metric;

            if (metricDefinition.frequency != null && metricDefinition.frequency.equals(Frequency.realtime)) {
                metric = getRealTimeMetric(metricDefinition); // Google has a separate API for realtime data.
            } else {
                metric = getMetric(metricDefinition);
            }

            metric.name = metricDefinition.name;
            metric.definition = metricDefinition;

            metrics.add(metric);
        }

        return metrics;
    }

    private Metric getMetric(MetricDefinition metricDefinition) throws IOException {

        String metrics = metricDefinition.query.get("metrics");
        String startDate = metricDefinition.query.get("start-date");
        String endDate = metricDefinition.query.get("end-date");
        String sort = metricDefinition.query.get("sort");
        String filters = metricDefinition.query.get("filters");
        String dimensions = metricDefinition.query.get("dimensions");
        String maxResults = metricDefinition.query.get("max-results");

        Analytics.Data.Ga.Get get = analytics.data().ga().get(tableId, // Table Id.
                startDate,
                endDate,
                metrics);

        if (dimensions != null)
            get.setDimensions(dimensions);

        if (sort != null)
            get.setSort(sort);

        if (filters != null)
            get.setFilters(filters);

        if (maxResults != null)
            get.setMaxResults(Integer.parseInt(maxResults));

        GaData data = get.execute();

        return mapDataToMetric(metricDefinition, data);
    }

    private Metric getRealTimeMetric(MetricDefinition metricDefinition) throws IOException {
        RealtimeData data = analytics.data().realtime().get(tableId, metricDefinition.query.get("metrics")) // Metrics.
                .execute();
        return mapDataToMetric(metricDefinition, data);
    }

    private Metric mapDataToMetric(MetricDefinition metricDefinition, RealtimeData data) {
        Metric metric = new Metric();
        metric.columns = new ArrayList<String>();
        for (RealtimeData.ColumnHeaders columnHeaders : data.getColumnHeaders()) {
            metric.columns.add(columnHeaders.getName());
        }

        metric.values = data.getRows();

        return metric;
    }

    private Metric mapDataToMetric(MetricDefinition metricDefinition, GaData data) {
        Metric metric = new Metric();
        metric.columns = new ArrayList<String>();
        for (GaData.ColumnHeaders columnHeaders : data.getColumnHeaders()) {
            metric.columns.add(columnHeaders.getName());
        }

        metric.values = data.getRows();

        return metric;
    }

    private static Analytics initializeAnalytics() throws Exception {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = GoogleCredential.getApplicationDefault()
                .createScoped(Arrays.asList(AnalyticsScopes.ANALYTICS_READONLY));

        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }
}
