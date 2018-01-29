package com.marklogic.support;

public class Configuration {

    public static final String HOSTNAME = "localhost";
    public static final int PORT = 65534;
    public static final String USERNAME = "q";
    public static final String PASSWORD = "q";

    public static final String URI = String.format("http://%s:%d/", Configuration.HOSTNAME, Configuration.PORT);

}
