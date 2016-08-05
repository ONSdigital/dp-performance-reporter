package com.onsdigital.performance.reporter.pingdom;

import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.interfaces.ResponseTimeProvider;
import com.onsdigital.performance.reporter.model.ResponseTime;
import com.onsdigital.performance.reporter.model.ResponseTimes;
import com.onsdigital.performance.reporter.pingdom.model.Check;
import com.onsdigital.performance.reporter.pingdom.model.Result;

import java.io.IOException;


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
    public ResponseTimes getResponseTimes(String checkIdentifier) throws IOException {

        ResponseTimes responseTimes = new ResponseTimes();

        int checkId = 0;
        for (Check check : pingdomClient.getChecks()) {
            if (check.name.equals(checkIdentifier))
                checkId = check.id;
        }

        for (Result result : pingdomClient.getResults(checkId)) {

            ResponseTime responseTime = new ResponseTime(
                    result.time,
                    result.status,
                    result.responsetime,
                    result.statusdesclong);

            responseTimes.add(responseTime);
        }

        return responseTimes;
    }

}
