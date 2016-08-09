package com.onsdigital.performance.reporter;

public class Configuration {

    // Pingdom
    private static String PINGDOM_USERNAME_ENV = "pingdom_username";
    private static String PINGDOM_PASSWORD_ENV = "pingdom_password";
    private static String PINGDOM_APPLICATION_KEY_ENV = "pingdom_application_key";

    // AWS - getting credentials from default ~/.aws/credentials location
    private static String AWS_BUCKET_NAME = "aws_bucket_name";

    // Google analytics - credentials JSON file read automatically by Google client library. Added here for completeness
    private static String GOOGLE_CRENDENTIALS_LOCATION_ENV = "GOOGLE_APPLICATION_CREDENTIALS"; // read by Google client
    private static String GOOGLE_PROFILE_ID_ENV = "google_profile_id";

    // Influx
    private static String INFLUXDB_USERNAME_ENV = "influxdb_username";
    private static String INFLUXDB_PASSWORD_ENV = "influxdb_password";
    private static String INFLUXDB_URL_ENV = "influxdb_url";
    private static String INFLUXDB_URL_DEFAULT = "http://localhost:8086";

    public static String getPingdomUsername() {
        return getValue(PINGDOM_USERNAME_ENV);
    }

    public static String getPingdomPassword() {
        return getValue(PINGDOM_PASSWORD_ENV);
    }

    public static String getPingdomApplicationKey() {
        return getValue(PINGDOM_APPLICATION_KEY_ENV);
    }

    public static String getAwsBucketName() {
        return getValue(AWS_BUCKET_NAME);
    }

    public static String getGoogleProfileId() {
        return getValue(GOOGLE_PROFILE_ID_ENV);
    }

    public static String getInfluxDbUsername() {
        return getValue(INFLUXDB_USERNAME_ENV);
    }

    public static String getInfluxDbPassword() {
        return getValue(INFLUXDB_PASSWORD_ENV);
    }

    public static String getInfluxdbUrl() {
        return getValueOrDefault(INFLUXDB_URL_ENV, INFLUXDB_URL_DEFAULT);
    }

    static String getValueOrDefault(String key, String defaultValue) {
        String value = getValue(key);
        if (value == null || value.length() == 0)
            value = defaultValue;

        return value;
    }

    static String getValue(String key) {

        String value = System.getProperty(key);
        if (value == null || value.length() == 0)
            value = System.getenv(key);

        if (value == null || value.length() == 0)
            throw new RuntimeException("Could not find configuration value for " + key);

        return value;
    }
}
