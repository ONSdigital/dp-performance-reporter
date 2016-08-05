package com.onsdigital.performance.reporter.influxdb;

import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.interfaces.MetricsProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.MetricSeries;
import com.onsdigital.performance.reporter.model.Metrics;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class InfluxDbMetricsProvider implements MetricsProvider {

    private static Log log = LogFactory.getLog(InfluxDbMetricsProvider.class);

    private InfluxDB influxDB;

    public InfluxDbMetricsProvider() {

        String url = Configuration.getInfluxdbUrl();
        String username = Configuration.getInfluxDbUsername();
        String password = Configuration.getInfluxDbPassword();

        log.debug("Connecting to InfluxDb instance " + url);
        this.influxDB = InfluxDBFactory.connect(url, username, password);
    }

    /**
     * Gather metrics for the given metric name from the given DB name.
     * @param dbName
     * @param metricName
     * @return
     */
    @Override
    public Metrics getMetrics(String dbName, String metricName) {

        Metrics metrics = new Metrics();

        log.debug("Gathering metrics: " + metricName + " from DB: " + dbName);
        Query query = new Query(String.format("SELECT * FROM %s", metricName), dbName);
        QueryResult queryResult = influxDB.query(query);

        for (QueryResult.Result result : queryResult.getResults()) {

            Metric metric = new Metric();

            for (QueryResult.Series series : result.getSeries()) {

                MetricSeries metricSeries = new MetricSeries();
                metricSeries.name = series.getName();
                metricSeries.columns = series.getColumns();
                metricSeries.values = series.getValues();
                metricSeries.tags = series.getTags();

                metric.add(metricSeries);
            }

            metrics.add(metric);
        }

        return metrics;
    }
}
