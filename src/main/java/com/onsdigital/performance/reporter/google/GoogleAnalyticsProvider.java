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
import com.onsdigital.performance.reporter.interfaces.MetricProvider;
import com.onsdigital.performance.reporter.interfaces.MetricsProvider;
import com.onsdigital.performance.reporter.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class GoogleAnalyticsProvider implements MetricsProvider, MetricProvider {

    private static Log log = LogFactory.getLog(GoogleAnalyticsProvider.class);

    private static final String APPLICATION_NAME = "dp-performance-reporter";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static Analytics analytics;
    private static String tableId;

    public GoogleAnalyticsProvider() throws GeneralSecurityException, IOException {
        analytics = initializeAnalytics();
        tableId = "ga:" + Configuration.getGoogleProfileId();
    }

    /**
     * Produce metrics from google analytics based on the JSON configuration file.
     *
     * @return - the metrics gathered from Google Analytics
     */
    @Override
    public Metrics getMetrics(MetricDefinitions metricDefinitions) {

        Metrics metrics = new Metrics();

        for (MetricDefinition metricDefinition : metricDefinitions.metrics) {

            log.debug("Running Google Analytics report: " + metricDefinition.name);

            try {
                Metric metric;

                if (metricDefinition.frequency != null && metricDefinition.frequency.equals(Frequency.realtime)) {
                    metric = getRealTimeMetric(metricDefinition); // Google has a separate API for realtime data.
                } else {
                    metric = getMetric(metricDefinition);
                }

                metric.name = metricDefinition.name;
                metric.definition = metricDefinition;
                metrics.add(metric);

            } catch (IOException e) {
                log.error("Exception getting Google Analytics metric: " + metricDefinition.name, e);
            }
        }

        return metrics;
    }

    @Override
    public Metric getMetric(MetricDefinition metricDefinition) throws IOException {

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
        return mapDataToMetric(data);
    }

    private Metric getRealTimeMetric(MetricDefinition metricDefinition) throws IOException {
        RealtimeData data = analytics.data().realtime().get(tableId, metricDefinition.query.get("metrics")) // Metrics.
                .execute();
        return mapDataToMetric(data);
    }

    Metric mapDataToMetric(RealtimeData data) {
        Metric metric = new Metric();
        metric.columns = new ArrayList<>();
        for (RealtimeData.ColumnHeaders columnHeaders : data.getColumnHeaders()) {
            metric.columns.add(columnHeaders.getName());
        }

        metric.values = data.getRows();

        return metric;
    }

    static Metric mapDataToMetric(GaData data) {

        Metric metric = new Metric();

        if (data == null)
            return metric;

        metric.columns = new ArrayList<>();
        for (GaData.ColumnHeaders columnHeaders : data.getColumnHeaders()) {
            metric.columns.add(columnHeaders.getName());
        }

        metric.values = data.getRows();

        return metric;
    }

    private static Analytics initializeAnalytics() throws GeneralSecurityException, IOException {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        PrivateKey privateKey = parsePrivateKey(Configuration.getGooglePrivateKey());

        GoogleCredential credential = new GoogleCredential.Builder()
                .setServiceAccountScopes(Arrays.asList(AnalyticsScopes.ANALYTICS_READONLY))
                .setServiceAccountPrivateKey(privateKey)
                .setServiceAccountId(Configuration.getGoogleAccountId())
                .setJsonFactory(JSON_FACTORY)
                .setTransport(httpTransport)
                .build();

        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    private static PrivateKey parsePrivateKey(String key) throws GeneralSecurityException {
        key = key.replace("-----BEGIN PRIVATE KEY-----\\n", "");
        key = key.replace("-----END PRIVATE KEY-----", "");
        key = key.replace("\\n", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
