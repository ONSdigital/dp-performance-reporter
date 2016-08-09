package com.onsdigital.performance.reporter.pingdom;

import com.onsdigital.performance.reporter.pingdom.model.ChecksResponse;
import com.onsdigital.performance.reporter.pingdom.model.ResultsResponse;
import com.onsdigital.performance.reporter.pingdom.model.SummaryResponse;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * The interface definition of the Pingdom API.
 *
 * The Retrofit library parses this interface definition to create API requests.
 */
interface PingdomService {

    @GET("checks")
    Call<ChecksResponse> getChecks(
            @Header("App-Key") String applicationKey);

    @GET("summary.average/{checkId}?includeuptime=true")
    Call<SummaryResponse> getAverage(
            @Header("App-Key") String applicationKey,
            @Path("checkId") int checkId);

    @GET("results/{checkId}")
    Call<ResultsResponse> getResults(
            @Header("App-Key") String applicationKey,
            @Path("checkId") int checkId);
}

