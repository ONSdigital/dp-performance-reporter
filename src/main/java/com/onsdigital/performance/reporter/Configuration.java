package com.onsdigital.performance.reporter;

public class Configuration {

    // Pingdom
    private static String PINGDOM_USERNAME_ENV = "pingdom_username";
    private static String PINGDOM_PASSWORD_ENV = "pingdom_password";
    private static String PINGDOM_APPLICATION_KEY_ENV = "pingdom_application_key";

    // AWS
    private static String AWS_BUCKET_NAME = "aws_bucket_name";

    // Google analytics

    // Influx


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

    static String getValue(String key) {

        String value = System.getProperty(key);
        if (value == null || value.length() == 0) {
            value = System.getenv(key);
        }

        if (value == null || value.length() == 0)
            throw new RuntimeException("Could not find configuration value for " + key);

        return value;
    }
}
