package com.onsdigital.performance.reporter.model;

/**
 * An individual response time measurement.
 */
public class ResponseTime {
    public int time;
    public String status;
    public int responsetime;
    public String statusdesc;

    public ResponseTime(int time, String status, int responsetime, String statusdesc) {
        this.time = time;
        this.status = status;
        this.responsetime = responsetime;
        this.statusdesc = statusdesc;
    }
}
