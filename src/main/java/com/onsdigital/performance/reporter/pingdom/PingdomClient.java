package com.onsdigital.performance.reporter.pingdom;

import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.pingdom.model.*;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class PingdomClient {

    // base url of com.onsdigital.performance.reporter.pingdom
    public static final String API_BASE_URL = "https://api.pingdom.com/api/2.0/";

    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private String applicationKey;
    private PingdomService pingdom;

    public static void main(String[] args) throws IOException {

        String username = Configuration.getPingdomUsername();
        String password = Configuration.getPingdomPassword();
        String applicationKey = Configuration.getPingdomApplicationKey();

        PingdomClient pingdom = new PingdomClient(username, password, applicationKey);

        List<Check> checks = pingdom.getChecks();

        for (Check check : checks) {
            System.out.println(check.id);
            System.out.println(check.name);
            System.out.println(check.type);
        }

        List<Result> results = pingdom.getResults(2253101);
        for (Result result : results) {
            System.out.println("result.status = " + result.status);
            System.out.println("result.statusdesc = " + result.statusdesc);
            System.out.println("result.responsetime = " + result.responsetime);
        }

    }

    /**
     * Create a new instance of the Pingdom client for the given username and password.
     * @param username
     * @param password
     * @param applicationKey
     */
    public PingdomClient(String username, String password, String applicationKey) {
        this.applicationKey = applicationKey;
        pingdom = createService(PingdomService.class, username, password);
    }

    /**
     * Get a list of Pingdom checks registered on this account.
     * @return
     * @throws IOException
     */
    public List<Check> getChecks() throws IOException {
        Response<ChecksResponse> response = pingdom.getChecks(applicationKey).execute();
        return response.body().checks;
    }

    /**
     * Get a list of Pingdom results for the given checkId
     * @return
     * @throws IOException
     */
    public List<Result> getResults(int checkId) throws IOException {
        Response<ResultsResponse> response = pingdom.getResults(applicationKey, checkId).execute();
        return response.body().results;
    }

    public Summary getAverageSummary(int checkId) throws IOException {
        Response<SummaryResponse> response = pingdom.getAverage(applicationKey, checkId).execute();
        return response.body().summary;
    }
    
    // added this to handle basic auth in service calls.
    private <S> S createService(Class<S> serviceClass, String username, String password) {
        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic =
                    "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            httpClient.addInterceptor(logging);

            httpClient.addInterceptor(new Interceptor() {
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
