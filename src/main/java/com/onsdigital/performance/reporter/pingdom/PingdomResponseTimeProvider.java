package com.onsdigital.performance.reporter.pingdom;

import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.interfaces.MetricProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.model.MetricDefinition;
import com.onsdigital.performance.reporter.pingdom.model.PingdomReportType;
import com.onsdigital.performance.reporter.pingdom.model.Summary;
import com.onsdigital.performance.reporter.pingdom.model.SummaryStatus;
import com.onsdigital.performance.reporter.util.DateParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PingdomResponseTimeProvider implements MetricProvider {

    private static Log log = LogFactory.getLog(PingdomResponseTimeProvider.class);

    private PingdomClient pingdomClient;

    public PingdomResponseTimeProvider() {

        String username = Configuration.getPingdomUsername();
        String password = Configuration.getPingdomPassword();
        String applicationKey = Configuration.getPingdomApplicationKey();

        pingdomClient = new PingdomClient(username, password, applicationKey);
    }

    @Override
    public Metric getMetric(MetricDefinition metricDefinition) throws IOException {

        Metric metric = new Metric();

        String start = metricDefinition.query.get("start-date");
        String end = metricDefinition.query.get("end-date");

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = DateParser.parseStartDate(start);
            endDate = DateParser.parseEndDate(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int checkId = Integer.parseInt(metricDefinition.query.get("check-id"));
        PingdomReportType type = PingdomReportType.valueOf(metricDefinition.query.get("type"));

        switch (type) {
            case average:
                metric = getAverageSummaryMetric(checkId, startDate, endDate);
                break;
        }

        return metric;

    }

    private Metric getAverageSummaryMetric(int checkId, Date startDate, Date endDate) throws IOException {
        Summary summary = pingdomClient.getAverageSummary(checkId, startDate.getTime(), endDate.getTime());
        return mapPingdomSummaryToMetric(summary);
    }

    static Metric mapPingdomSummaryToMetric(Summary summary) {
        Metric metric = new Metric();
        metric.columns = new ArrayList<>();
        metric.columns.add("from");
        metric.columns.add("to");
        metric.columns.add("averageResponseTime");
        metric.columns.add("totalTimeUp");
        metric.columns.add("totalTimeDown");
        metric.columns.add("totalTimeUnknown");
        metric.columns.add("percentageTimeUp");
        metric.columns.add("percentageTimeDown");
        metric.columns.add("percentageTimeUnknown");

        SummaryStatus status = summary.status;

        double msPerPercent = (status.totalup + status.totaldown + status.totalunknown) / 100.0;

        long percentageUp = Math.round(status.totalup / msPerPercent);
        long percentageDown = Math.round(status.totaldown / msPerPercent);
        long percentageUnknown = Math.round(status.totalunknown / msPerPercent);

        metric.values = new ArrayList<>();
        List<String> values = new ArrayList<>();
        values.add(Long.toString(summary.responsetime.from));
        values.add(Long.toString(summary.responsetime.to));
        values.add(Long.toString(summary.responsetime.avgresponse));
        values.add(Long.toString(status.totalup));
        values.add(Long.toString(status.totaldown));
        values.add(Long.toString(status.totalunknown));
        values.add(Long.toString(percentageUp));
        values.add(Long.toString(percentageDown));
        values.add(Long.toString(percentageUnknown));
        metric.values.add(values);
        return metric;
    }
}
