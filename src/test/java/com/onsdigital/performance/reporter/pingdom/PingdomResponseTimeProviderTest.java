package com.onsdigital.performance.reporter.pingdom;

import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.pingdom.model.Summary;
import com.onsdigital.performance.reporter.pingdom.model.SummaryResponseTime;
import com.onsdigital.performance.reporter.pingdom.model.SummaryStatus;
import org.junit.Assert;
import org.junit.Test;

public class PingdomResponseTimeProviderTest {

    @Test
    public void mapPingdomSummaryToMetric() throws Exception {

        // Given an example response from pingdom
        Summary summary = new Summary();
        summary.responsetime = new SummaryResponseTime();
        summary.responsetime.avgresponse = 1;
        summary.responsetime.from = 2;
        summary.responsetime.to = 3;

        summary.status = new SummaryStatus();
        summary.status.totaldown = 15;
        summary.status.totalunknown = 5;
        summary.status.totalup = 30;

        // When the mapPingdomSummaryToMetric method is called
        Metric metric = PingdomResponseTimeProvider.mapPingdomSummaryToMetric(summary);

        // Then a metric instance is returned having mapped the data from the pingdom response.
        Assert.assertNotNull(metric);
        Assert.assertEquals(9, metric.columns.size());

        String expectedFrom = Long.toString(summary.responsetime.from);
        String expectedTo = Long.toString(summary.responsetime.to);
        String expectedAverage = Long.toString(summary.responsetime.avgresponse);
        String expectedUp = Long.toString(summary.status.totalup);
        String expectedDown = Long.toString(summary.status.totaldown);
        String expectedUnknown = Long.toString(summary.status.totalunknown);

        Assert.assertEquals(expectedFrom, metric.values.get(0).get(0));
        Assert.assertEquals(expectedTo, metric.values.get(0).get(1));
        Assert.assertEquals(expectedAverage, metric.values.get(0).get(2));
        Assert.assertEquals(expectedUp, metric.values.get(0).get(3));
        Assert.assertEquals(expectedDown, metric.values.get(0).get(4));
        Assert.assertEquals(expectedUnknown, metric.values.get(0).get(5));

        // The percentages of the status values have been calculated and added correctly.
        Assert.assertEquals("60", metric.values.get(0).get(6));
        Assert.assertEquals("30", metric.values.get(0).get(7));
        Assert.assertEquals("10", metric.values.get(0).get(8));
    }
}
