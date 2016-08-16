package com.onsdigital.performance.reporter;

public class Configuration {

    // Pingdom
    private static String PINGDOM_USERNAME_ENV = "PINGDOM_USERNAME";
    private static String PINGDOM_PASSWORD_ENV = "PINGDOM_PASSWORD";
    private static String PINGDOM_APPLICATION_KEY_ENV = "PINGDOM_APPLICATION_KEY";

    // AWS - getting credentials from default ~/.aws/credentials location
    private static String AWS_BUCKET_NAME = "AWS_BUCKET_NAME";

    // Google analytics
    private static String GOOGLE_PRIVATE_KEY_ENV = "GOOGLE_PRIVATE_KEY";
    private static String GOOGLE_ACCOUNT_ID_ENV = "GOOGLE_ACCOUNT_ID";
    private static String GOOGLE_PROFILE_ID_ENV = "GOOGLE_PROFILE_ID";

    // Influx
    private static String INFLUXDB_USERNAME_ENV = "INFLUXDB_USERNAME";
    private static String INFLUXDB_PASSWORD_ENV = "INFLUXDB_PASSWORD";
    private static String INFLUXDB_URL_ENV = "INFLUXDB_URL";
    private static String INFLUXDB_URL_DEFAULT = "http://localhost:8086";

    // Splunk
    private static String SPLUNK_USERNAME_ENV = "SPLUNK_USERNAME";
    private static String SPLUNK_PASSWORD_ENV = "SPLUNK_PASSWORD";
    private static String SPLUNK_HOST_ENV = "SPLUNK_HOST";
    private static String SPLUNK_PORT_ENV = "SPLUNK_PORT";

    public static String getSplunkUsername() {
        return getValueOrDefault(SPLUNK_USERNAME_ENV, "admin");
    }

    public static String getSplunkPassword() {
        return getValueOrDefault(SPLUNK_PASSWORD_ENV, "changeme");
    }

    public static String getSplunkHost() {
        return getValueOrDefault(SPLUNK_HOST_ENV, "localhost");
    }

    public static int getSplunkPort() {
        return Integer.parseInt(getValueOrDefault(SPLUNK_PORT_ENV, "8089"));
    }

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

    public static String getGooglePrivateKey() {
        return getValue(GOOGLE_PRIVATE_KEY_ENV);
    }

    public static String getGoogleProfileId() {
        return getValue(GOOGLE_PROFILE_ID_ENV);
    }

    public static String getGoogleAccountId() {
        return getValue(GOOGLE_ACCOUNT_ID_ENV);
    }

    public static String getInfluxDbUsername() {
        return getValueOrDefault(INFLUXDB_USERNAME_ENV, "");
    }

    public static String getInfluxDbPassword() {
        return getValueOrDefault(INFLUXDB_PASSWORD_ENV, "");
    }

    public static String getInfluxdbUrl() {
        return getValueOrDefault(INFLUXDB_URL_ENV, INFLUXDB_URL_DEFAULT);
    }

    static String getValueOrDefault(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.length() == 0)
            value = System.getenv(key);

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
