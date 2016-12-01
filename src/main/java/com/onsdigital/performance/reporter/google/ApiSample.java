package com.onsdigital.performance.reporter.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.*;
import com.onsdigital.performance.reporter.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;


/**
 * A simple example of how to access the Google Analytics API using a service
 * account.
 */
public class ApiSample {


    private static final String APPLICATION_NAME = "Hello Analytics";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static void main(String[] args) {
        try {
            Analytics analytics = initializeAnalytics();

            String profileId = Configuration.getGoogleProfileId();  // getFirstProfileId(analytics);

            System.out.println("First Profile Id: " + profileId);
            printResults(getResults(analytics, profileId));


            String googleTableId = "ga:" + profileId;
            GaData gaData = executeDataQuery(analytics, googleTableId);

            System.out.println("-----------------printReportInfo-------------------");
            printReportInfo(gaData);
            System.out.println("----------------printProfileInfo--------------------");
            printProfileInfo(gaData);
            System.out.println("-----------------printQueryInfo-------------------");
            printQueryInfo(gaData);
            System.out.println("-----------------printPaginationInfo-------------------");
            printPaginationInfo(gaData);
            System.out.println("------------------printTotalsForAllResults------------------");
            printTotalsForAllResults(gaData);
            System.out.println("---------------printColumnHeaders---------------------");
            printColumnHeaders(gaData);
            System.out.println("-----------------printDataTable-------------------");
            printDataTable(gaData);

            System.out.println("-----------------real time data-------------------");
            RealtimeData realtimeData = executeRealTimeQuery(analytics, googleTableId);
            printDataTable(realtimeData);

            System.out.println("end");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static RealtimeData executeRealTimeQuery(Analytics analytics, String tableId) throws IOException {
        return analytics.data().realtime().get(tableId, "rt:activeUsers") // Metrics.
                .execute();
    }

    private static GaData executeDataQuery(Analytics analytics, String tableId) throws IOException {

        // see all event types
        return analytics.data().ga().get(tableId, // Table Id.
                "1daysAgo", // Start date. // 2016-08-01
                "today", // End date. 2016-08-08
                "ga:sessions") // Metrics.
                .setDimensions("ga:pagePath") // ga:source,ga:keyword
                .setFilters("ga:pagePath=~/bulletins/*")
                .execute();
    }

    private static Analytics initializeAnalytics() throws Exception {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        PrivateKey privateKey = parsePrivateKey(Configuration.getGooglePrivateKey());

        GoogleCredential credential = new GoogleCredential.Builder()
                .setServiceAccountScopes(Arrays.asList(AnalyticsScopes.ANALYTICS_READONLY))
                .setServiceAccountPrivateKey(privateKey)
                .setServiceAccountId(Configuration.getGoogleAccountId())
                .setJsonFactory(JSON_FACTORY)
                .setTransport(httpTransport)
                .build();

        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    private static PrivateKey parsePrivateKey(String key) throws GeneralSecurityException {
        key = key.replace("-----BEGIN PRIVATE KEY-----\\n", "");
        key = key.replace("-----END PRIVATE KEY-----", "");
        key = key.replace("\\n", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }

    private static String getFirstProfileId(Analytics analytics) throws IOException {
        // Get the first view (profile) ID for the authorized user.
        String profileId = null;

        // Query for the list of all accounts associated with the service account.
        Accounts accounts = analytics.management().accounts().list().execute();

        if (accounts.getItems().isEmpty()) {
            System.err.println("No accounts found");
        } else {
            String firstAccountId = accounts.getItems().get(0).getId();

            // Query for the list of properties associated with the first account.
            Webproperties properties = analytics.management().webproperties()
                    .list(firstAccountId).execute();

            if (properties.getItems().isEmpty()) {
                System.err.println("No Webproperties found");
            } else {
                String firstWebpropertyId = properties.getItems().get(0).getId();

                // Query for the list views (profiles) associated with the property.
                Profiles profiles = analytics.management().profiles()
                        .list(firstAccountId, firstWebpropertyId).execute();

                if (profiles.getItems().isEmpty()) {
                    System.err.println("No views (profiles) found");
                } else {
                    // Return the first (view) profile associated with the property.
                    profileId = profiles.getItems().get(0).getId();
                }
            }
        }
        return profileId;
    }

    private static GaData getResults(Analytics analytics, String profileId) throws IOException {
        // Query the Core Reporting API for the number of sessions
        // in the past seven days.
        return analytics.data().ga()
                .get("ga:" + profileId, "7daysAgo", "today", "ga:sessions")
                .execute();
    }

    private static void printResults(GaData results) {
        // Parse the response from the Core Reporting API for
        // the profile name and number of sessions.
        if (results != null && !results.getRows().isEmpty()) {
            System.out.println("View (Profile) Name: "
                    + results.getProfileInfo().getProfileName());
            System.out.println("Total Sessions: " + results.getRows().get(0).get(0));
        } else {
            System.out.println("No results found");
        }
    }

    private static void printReportInfo(GaData gaData) {
        System.out.println();
        System.out.println("Response:");
        System.out.println("ID:" + gaData.getId());
        System.out.println("Self link: " + gaData.getSelfLink());
        System.out.println("Kind: " + gaData.getKind());
        System.out.println("Contains Sampled Data: " + gaData.getContainsSampledData());
    }

    /**
     * Prints general information about the profile from which this report was accessed.
     *
     * @param gaData the data returned from the API.
     */
    private static void printProfileInfo(GaData gaData) {
        GaData.ProfileInfo profileInfo = gaData.getProfileInfo();

        System.out.println("Profile Info");
        System.out.println("Account ID: " + profileInfo.getAccountId());
        System.out.println("Web Property ID: " + profileInfo.getWebPropertyId());
        System.out.println("Internal Web Property ID: " + profileInfo.getInternalWebPropertyId());
        System.out.println("Profile ID: " + profileInfo.getProfileId());
        System.out.println("Profile Name: " + profileInfo.getProfileName());
        System.out.println("Table ID: " + profileInfo.getTableId());
    }

    /**
     * Prints the values of all the parameters that were used to query the API.
     *
     * @param gaData the data returned from the API.
     */
    private static void printQueryInfo(GaData gaData) {
        GaData.Query query = gaData.getQuery();

        System.out.println("Query Info:");
        System.out.println("Ids: " + query.getIds());
        System.out.println("Start Date: " + query.getStartDate());
        System.out.println("End Date: " + query.getEndDate());
        System.out.println("Metrics: " + query.getMetrics()); // List
        System.out.println("Dimensions: " + query.getDimensions()); // List
        System.out.println("Sort: " + query.getSort()); // List
        System.out.println("Segment: " + query.getSegment());
        System.out.println("Filters: " + query.getFilters());
        System.out.println("Start Index: " + query.getStartIndex());
        System.out.println("Max Results: " + query.getMaxResults());
    }

    /**
     * Prints common pagination information.
     *
     * @param gaData the data returned from the API.
     */
    private static void printPaginationInfo(GaData gaData) {
        System.out.println("Pagination Info:");
        System.out.println("Previous Link: " + gaData.getPreviousLink());
        System.out.println("Next Link: " + gaData.getNextLink());
        System.out.println("Items Per Page: " + gaData.getItemsPerPage());
        System.out.println("Total Results: " + gaData.getTotalResults());
    }

    /**
     * Prints the total metric value for all rows the query matched.
     *
     * @param gaData the data returned from the API.
     */
    private static void printTotalsForAllResults(GaData gaData) {
        System.out.println("Metric totals over all results:");
        Map<String, String> totalsMap = gaData.getTotalsForAllResults();
        for (Map.Entry<String, String> entry : totalsMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * Prints the information for each column. The reporting data from the API is returned as rows of
     * data. The column headers describe the names and types of each column in rows.
     *
     * @param gaData the data returned from the API.
     */
    private static void printColumnHeaders(GaData gaData) {
        System.out.println("Column Headers:");

        for (GaData.ColumnHeaders header : gaData.getColumnHeaders()) {
            System.out.println("Column Name: " + header.getName());
            System.out.println("Column Type: " + header.getColumnType());
            System.out.println("Column Data Type: " + header.getDataType());
        }
    }

    /**
     * Prints all the rows of data returned by the API.
     *
     * @param gaData the data returned from the API.
     */
    private static void printDataTable(GaData gaData) {
        if (gaData.getTotalResults() > 0) {
            System.out.println("Data Table:");

            // Print the column names.
            for (GaData.ColumnHeaders header : gaData.getColumnHeaders()) {
                System.out.format("%-32s", header.getName());
            }
            System.out.println();

            // Print the rows of data.
            for (List<String> rowValues : gaData.getRows()) {
                for (String value : rowValues) {
                    System.out.format("%-32s", value);
                }
                System.out.println();
            }
        } else {
            System.out.println("No data");
        }
    }

    private static void printDataTable(RealtimeData data) {
        if (data.getTotalResults() > 0) {
            System.out.println("Data Table:");

            // Print the column names.
            for (RealtimeData.ColumnHeaders header : data.getColumnHeaders()) {
                System.out.format("%-32s", header.getName());
            }
            System.out.println();

            // Print the rows of data.
            for (List<String> rowValues : data.getRows()) {
                for (String value : rowValues) {
                    System.out.format("%-32s", value);
                }
                System.out.println();
            }
        } else {
            System.out.println("No data");
        }
    }
}