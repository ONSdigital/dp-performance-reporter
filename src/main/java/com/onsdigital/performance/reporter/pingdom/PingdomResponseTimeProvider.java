package com.onsdigital.performance.reporter.pingdom;

import com.google.api.services.analytics.model.RealtimeData;
import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.util.ReportDefinitionsReader;
import com.onsdigital.performance.reporter.interfaces.ResponseTimeProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.MetricDefinition;
import com.onsdigital.performance.reporter.model.MetricDefinitions;
import com.onsdigital.performance.reporter.model.Metrics;
import com.onsdigital.performance.reporter.pingdom.model.PingdomReportType;
import com.onsdigital.performance.reporter.pingdom.model.Summary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PingdomResponseTimeProvider implements ResponseTimeProvider {

    private static Log log = LogFactory.getLog(PingdomResponseTimeProvider.class);

    private PingdomClient pingdomClient;

    public PingdomResponseTimeProvider() {

        String username = Configuration.getPingdomUsername();
        String password = Configuration.getPingdomPassword();
        String applicationKey = Configuration.getPingdomApplicationKey();

        pingdomClient = new PingdomClient(username, password, applicationKey);
    }

    public Metrics getResponseTimes() throws IOException {

        MetricDefinitions metricDefinitions = new ReportDefinitionsReader().readMetricDefinitions("pingdomReports.json");
        Metrics metrics = new Metrics();

        for (MetricDefinition metricDefinition : metricDefinitions.metrics) {

            log.debug("Running Pingdom report: " + metricDefinition.name);
            Metric metric;

            metric = getMetric(metricDefinition);

            metric.name = metricDefinition.name;
            metric.definition = metricDefinition;

            metrics.add(metric);
        }

        return metrics;


    }

    private Metric getMetric(MetricDefinition metricDefinition) throws IOException {

        Metric metric = new Metric();

        String start = metricDefinition.query.get("start-date");
        String end = metricDefinition.query.get("end-date");



        int checkId = Integer.parseInt(metricDefinition.query.get("check-id"));
        PingdomReportType type = PingdomReportType.valueOf(metricDefinition.query.get("type"));

        switch (type) {
            case average:
                metric = getAverageSummaryMetric(metricDefinition, checkId);
                break;
        }

        return metric;

    }

    private Metric getAverageSummaryMetric(MetricDefinition metricDefinition, int checkId) throws IOException {

        Summary summary = pingdomClient.getAverageSummary(checkId);

        Metric metric = new Metric();
        metric.columns = new ArrayList<String>();
        metric.columns.add("from");
        metric.columns.add("to");
        metric.columns.add("averageResponseTime");
        metric.columns.add("totalTimeUp");
        metric.columns.add("totalTimeDown");
        metric.columns.add("totalTimeUnknown");

        metric.values = new ArrayList<List<String>>();
        List<String> values = new ArrayList<String>();
        values.add(Integer.toString(summary.responsetime.from));
        values.add(Integer.toString(summary.responsetime.to));
        values.add(Integer.toString(summary.responsetime.avgresponse));
        values.add(Integer.toString(summary.status.totalup));
        values.add(Integer.toString(summary.status.totaldown));
        values.add(Integer.toString(summary.status.totalunknown));
        metric.values.add(values);

        return metric;
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

}
