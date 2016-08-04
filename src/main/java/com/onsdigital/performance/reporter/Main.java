package com.onsdigital.performance.reporter;

import com.onsdigital.performance.reporter.model.ResponseTimes;
import com.onsdigital.performance.reporter.pingdom.PingdomResponseTimeProvider;
import com.onsdigital.performance.reporter.s3.s3FileUploader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class Main {

    private static Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws IOException {

        log.debug("Starting performance reporter.");

        log.debug("Gathering response times from Pingdom.");
        ResponseTimeProvider responseTimeProvider = new PingdomResponseTimeProvider();
        ResponseTimes responseTimes = responseTimeProvider.getResponseTimes("Website (EN) CloudFlare");

        log.debug("Uploading response times.");
        FileUploader fileUploader = new s3FileUploader();
        fileUploader.uploadJsonForObject(responseTimes, "responsetimes.json");

        log.debug("Finished");
    }
}
