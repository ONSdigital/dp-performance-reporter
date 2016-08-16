package com.onsdigital.performance.reporter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    @Test
    public void getSplunkUsernameShouldReturnDefault() throws Exception {
        String expected = "admin";
        String actual = Configuration.getSplunkUsername();
        assertEquals(expected, actual);
    }

    @Test
    public void getSplunkPasswordShouldReturnDefault() throws Exception {
        String expected = "changeme";
        String actual = Configuration.getSplunkPassword();
        assertEquals(expected, actual);
    }

    @Test
    public void getSplunkHostShouldReturnDefault() throws Exception {
        String expected = "localhost";
        String actual = Configuration.getSplunkPassword();
        assertEquals(expected, actual);
    }

    @Test
    public void getSplunkPortShouldReturnDefault() throws Exception {
        String expected = "8089";
        String actual = Configuration.getSplunkPassword();
        assertEquals(expected, actual);
    }

    @Test
    public void getInfluxDbUsernameShouldReturnDefault() throws Exception {
        String expected = "";
        String actual = Configuration.getSplunkPassword();
        assertEquals(expected, actual);
    }

    @Test
    public void getInfluxDbPasswordShouldReturnDefault() throws Exception {
        String expected = "";
        String actual = Configuration.getSplunkPassword();
        assertEquals(expected, actual);
    }

    @Test
    public void getInfluxdbUrlShouldReturnDefault() throws Exception {
        String expected = "http://localhost:8086";
        String actual = Configuration.getSplunkPassword();
        assertEquals(expected, actual);
    }
}
