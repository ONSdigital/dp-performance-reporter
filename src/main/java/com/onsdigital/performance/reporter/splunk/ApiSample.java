package com.onsdigital.performance.reporter.splunk;

import com.splunk.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

public class ApiSample {

    public static void main(String[] args) throws InterruptedException, IOException, ParseException {

        HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

//        // Create a map of arguments and add login parameters
//        ServiceArgs loginArgsForInput = new ServiceArgs();
//        loginArgsForInput.setToken("Splunk 05F0FA15-B639-463A-9145-60BEB1E0A5C3");
//        loginArgsForInput.setHost("localhost");
//        loginArgsForInput.setPort(8088);
//        loginArgsForInput.setScheme("http");
//
//        // Create a Service instance and log in with the argument map
//        Service inputService = Service.connect(loginArgsForInput);
//
//        RequestMessage message = new RequestMessage();
//        message.setContent("{\"event\": {\"duration\": 456}}");
//        inputService.send("/services/collector/event", message);

        ServiceArgs loginArgsForQuery = new ServiceArgs();
        loginArgsForQuery.setUsername("admin");
        loginArgsForQuery.setPassword("changemenow");
        loginArgsForQuery.setHost("localhost");
        loginArgsForQuery.setPort(8089);

        Service queryService = Service.connect(loginArgsForQuery);

        Index myIndex = queryService.getIndexes().get("sandbox");

// Specify  values to apply to the event
        Args eventArgs = new Args();
        eventArgs.put("duration", "789");
        eventArgs.put("host", "local");

// Submit an event over HTTP
        myIndex.submit(eventArgs, "{\"duration\": 789}");


//        for (Application app : queryService.getApplications().values()) {
//            System.out.println(app.getName());
//        }

        // Create a simple search job
        String mySearch = "search * (index=\"sandbox\") | spath duration | timechart avg(duration)";
        //String mySearch = "search * (index=\"sandbox\") | spath duration | timechart count";
        Job job = queryService.getJobs().create(mySearch);

// Wait for the job to finish
        while (!job.isDone()) {
            Thread.sleep(100);
        }

        JobResultsArgs resultsArgs = new JobResultsArgs();
        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);

// Display results
        InputStream results = job.getResults(resultsArgs);
        String line;
        System.out.println("Results from the search job as XML:\n");
        BufferedReader br = new BufferedReader(new InputStreamReader(results, "UTF-8"));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();

        System.out.println("done");
    }

}
