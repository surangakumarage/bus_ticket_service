package com.busticket.configuration;

/**
 * Server configuration properties
 */
public class ServerConfiguration {
    public static final String HOSTNAME = "0.0.0.0";
    public static final int PORT = 8080;
    public static final int THREAD_POOL_SIZE = 10;
    public static final int SHUTDOWN_TIMEOUT = 0;

    // API Configuration
    public static final String CONTEXT_PATH = "/bus-ticket-service";
    public static final String API_BASE_PATH = CONTEXT_PATH + "/api";
    public static final String FARES_PATH = API_BASE_PATH + "/fares";
    public static final String STOPS_PATH = API_BASE_PATH + "/stops";
    public static final String HEALTH_PATH = "/health";

    // Cache Configuration
    public static final long DEFAULT_CACHE_TTL_MILLIS = 3600000; // 1 hour
    public static final int CACHE_MAX_SIZE = 1000; // Unlimited in current implementation

    // HTTP Configuration
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String CORS_ALLOW_ORIGIN = "*";
    public static final String CORS_ALLOW_METHODS = "GET, POST, PUT, DELETE, OPTIONS";
    public static final String CORS_ALLOW_HEADERS = "Content-Type";

    // Timeout Configuration (milliseconds)
    public static final int REQUEST_TIMEOUT = 30000;
    public static final int CONNECT_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 10000;

    // Validation Configuration
    public static final int MAX_REQUEST_BODY_SIZE = 1048576; // 1 MB
    public static final double LATITUDE_MIN = -90.0;
    public static final double LATITUDE_MAX = 90.0;
    public static final double LONGITUDE_MIN = -180.0;
    public static final double LONGITUDE_MAX = 180.0;

    private ServerConfiguration() {
        // Utility class - cannot instantiate
    }

    public static String getServerUrl() {
        return "http://" + HOSTNAME + ":" + PORT;
    }
}
