package com.onsdigital.performance.reporter.pingdom.model;

/**
 * A single Pingdom ping result.
 */
public class Result {
    public int probeid;
    public int time;
    public String status;
    public int responsetime;
    public String statusdesc;
    public String statusdesclong;
}

//{
//        "probeid" : 33,
//        "time" : 1294235764,
//        "status" : "up",
//        "responsetime" : 91,
//        "statusdesc" : "OK",
//        "statusdesclong" : "OK"
//}
