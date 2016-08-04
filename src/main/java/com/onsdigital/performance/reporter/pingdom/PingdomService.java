package com.onsdigital.performance.reporter.pingdom;

import com.onsdigital.performance.reporter.pingdom.model.ChecksResponse;
import com.onsdigital.performance.reporter.pingdom.model.ResultsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * The interface definition of the Pingdom API.
 *
 * The Retrofit library parses this interface definition to create API requests.
 */
interface PingdomService {

    @GET("checks")
    Call<ChecksResponse> getChecks(@Header("App-Key") String applicationKey);

    @GET("results/{checkId}")
    Call<ResultsResponse> getResults(@Header("App-Key") String applicationKey, @Path("checkId") int checkId);
}

