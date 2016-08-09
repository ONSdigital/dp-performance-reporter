package com.onsdigital.performance.reporter.pingdom;

import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.interfaces.ResponseTimeProvider;
import com.onsdigital.performance.reporter.model.Metric;
import com.onsdigital.performance.reporter.pingdom.model.Check;
import com.onsdigital.performance.reporter.pingdom.model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PingdomResponseTimeProvider implements ResponseTimeProvider {

    private PingdomClient pingdomClient;

    public PingdomResponseTimeProvider() {

        String username = Configuration.getPingdomUsername();
        String password = Configuration.getPingdomPassword();
        String applicationKey = Configuration.getPingdomApplicationKey();

        pingdomClient = new PingdomClient(username, password, applicationKey);
    }

    /**
     * Get the recorded response times for the given check identifier.
     * @param checkIdentifier
     * @return
     * @throws IOException
     */
    public Metric getResponseTimes(String checkIdentifier) throws IOException {

        Metric metric = new Metric();

        metric.columns = new ArrayList<String>();
        metric.columns.add("time");
        metric.columns.add("status");
        metric.columns.add("responseTime");
        metric.columns.add("statusDescription");

        int checkId = 0;
        for (Check check : pingdomClient.getChecks()) {
            if (check.name.equals(checkIdentifier))
                checkId = check.id;
        }

        metric.values = new ArrayList<List<String>>();

        for (Result result : pingdomClient.getResults(checkId)) {

            List<String> values = new ArrayList<String>();

            values.add(Integer.toString(result.time));
            values.add(result.status);
            values.add(Integer.toString(result.responsetime));
            values.add(result.statusdesclong);

            metric.values.add(values);
        }

        return metric;
    }

}
