package com.busticket;

import com.busticket.configuration.ApplicationConfiguration;
import com.busticket.util.DateParserUtil;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RestServer is the main entry point for the Bus Ticketer REST service
 * Lightweight HTTP server using Java 21 features without framework dependencies
 *
 * Usage:
 *   java -jar bus-ticketer-service.jar
 *   java -Dapp.profile=development -jar bus-ticketer-service.jar
 *   java -Dserver.port=9090 -jar bus-ticketer-service.jar
 */
public class RestServer {

    private static final Logger LOGGER = Logger.getLogger(RestServer.class.getName());

    // Configuration Constants
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_HOSTNAME = "0.0.0.0";
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;
    private static final String APP_NAME = "Bus Ticketer Service";
    private static final String APP_VERSION = "1.0.0";

    // Time Constants
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;

    // Server instance
    private HttpServer server;
    private ExecutorService executor;
    private final int port;
    private final String hostname;
    private final int threadPoolSize;
    private final LocalDateTime startTime;

    /**
     * Constructor with default configuration
     */
    public RestServer() {
        this(DEFAULT_PORT, DEFAULT_HOSTNAME, DEFAULT_THREAD_POOL_SIZE);
    }

    /**
     * Constructor with custom configuration
     */
    public RestServer(int port, String hostname, int threadPoolSize) {
        this.port = port;
        this.hostname = hostname;
        this.threadPoolSize = threadPoolSize;
        this.startTime = LocalDateTime.now();
    }

    /**
     * Start the REST server with all endpoints
     */
    public void start() throws IOException {
        try {
            // Initialize application configuration (loads journeys and fares)
            ApplicationConfiguration.getInstance();

            // Create HTTP server
            server = HttpServer.create(new InetSocketAddress(hostname, port), 0);

            // Register API endpoints
            registerControllers();

            // Register health check endpoint
            registerHealthCheck();

            // Register info endpoint
            registerInfoEndpoint();

            // Set thread pool executor
            executor = Executors.newFixedThreadPool(threadPoolSize);
            server.setExecutor(executor);

            // Start server
            server.start();

            // Print startup information
            printStartupBanner();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                "Failed to create HTTP server on " + hostname + ":" + port, e);
            throw e;
        }
    }

    /**
     * Register all API controllers
     * Note: Currently using Tomcat servlet deployment - this method is deprecated
     */
    private void registerControllers() {
        // Controllers moved to servlet-based implementation for Tomcat deployment
        // See: com.busticket.servlet.AvailabilityServlet
        // See: com.busticket.servlet.ReservationServlet
    }

    /**
     * Register health check endpoint (using text blocks - Java 13+)
     */
    private void registerHealthCheck() {
        server.createContext("/health", exchange -> {
            String response = """
                {
                  "status": "UP",
                  "service": "Bus Ticketer Service",
                  "version": "1.0.0",
                  "timestamp": "%s",
                  "uptime": "%s"
                }""".formatted(
                DateParserUtil.getCurrentIsoDateTime(),
                formatUptime()
            );

            byte[] responseBytes = response.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (var os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        });
    }

    /**
     * Register info endpoint
     */
    private void registerInfoEndpoint() {
        server.createContext("/info", exchange -> {
            String response = """
                {
                  "name": "Bus Ticketer Service",
                  "version": "1.0.0",
                  "description": "Advanced bus ticketing system with availability checking and reservations",
                  "java": "%s",
                  "started": "%s",
                  "endpoints": {
                    "health": "/health",
                    "availability": "/api/v1/reservation/availability",
                    "reservations": "/api/v1/reservation/book"
                  }
                }""".formatted(
                System.getProperty("java.version"),
                DateParserUtil.formatToIso(startTime)
            );

            byte[] responseBytes = response.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (var os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        });
    }

    /**
     * Graceful shutdown of server
     */
    public void stop() {
        if (server != null) {
            server.stop(5);
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

    /**
     * Calculate uptime since server started
     */
    private String formatUptime() {
        long seconds = ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
        long hours = seconds / SECONDS_PER_HOUR;
        long minutes = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
        long secs = seconds % SECONDS_PER_MINUTE;

        if (hours > 0) {
            return "%dh %dm %ds".formatted(hours, minutes, secs);
        } else if (minutes > 0) {
            return "%dm %ds".formatted(minutes, secs);
        } else {
            return "%ds".formatted(secs);
        }
    }

    /**
     * Print startup banner with ASCII art (Java 13+ text blocks)
     */
    private void printStartupBanner() {
        String banner = """
            ╔═══════════════════════════════════════════════════════════════╗
            ║                                                               ║
            ║              🚌  Bus Ticketer Service  🚌                     ║
            ║                                                               ║
            ║           Advanced Bus Booking & Reservation System           ║
            ║                    Version 1.0.0                             ║
            ║                                                               ║
            ╚═══════════════════════════════════════════════════════════════╝
            """;

        System.out.println(banner);
        System.out.println("✅ Service started successfully");
        System.out.println("📡 Server running on http://" + hostname + ":" + port);
        System.out.println("🕐 Started at: " + DateParserUtil.formatToIso(startTime));
        System.out.println("☕ Java Version: " + System.getProperty("java.version"));
        System.out.println();
        printEndpoints();
    }

    /**
     * Print available endpoints
     */
    private void printEndpoints() {
        System.out.println("📚 Available Endpoints:");
        System.out.println();
        System.out.println("System:");
        System.out.println("  GET  /health                         - Health check");
        System.out.println("  GET  /info                          - Service information");
        System.out.println();
        System.out.println("🎫 Bus Ticketing APIs:");
        System.out.println();
        System.out.println("Availability Check:");
        System.out.println("  GET  /api/v1/reservation/availability      - Check seat availability and fares (query params)");
        System.out.println("  POST /api/v1/reservation/availability      - Check seat availability (JSON body)");
        System.out.println();
        System.out.println("Reservation:");
        System.out.println("  POST /api/v1/reservation/book          - Book a ticket with passenger details");
        System.out.println();
        System.out.println("🔗 Test with curl:");
        System.out.println("  curl http://localhost:" + port + "/health");
        System.out.println("  curl http://localhost:" + port + "/info");
        System.out.println();
        System.out.println("Press Ctrl+C to stop the server");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
    }

    /**
     * Main entry point (Java 21)
     *
     * Supports:
     *   java -jar bus-ticketer-service.jar
     *   java -Dapp.profile=development -jar bus-ticketer-service.jar
     *   java -Dserver.port=9090 -jar bus-ticketer-service.jar
     *   java -Dserver.hostname=localhost -jar bus-ticketer-service.jar
     */
    public static void main(String[] args) {
        try {
            // Load configuration from system properties or defaults
            int port = Integer.parseInt(
                System.getProperty("server.port", String.valueOf(DEFAULT_PORT))
            );
            String hostname = System.getProperty("server.hostname", DEFAULT_HOSTNAME);
            int threadPoolSize = Integer.parseInt(
                System.getProperty("server.thread.pool.size", String.valueOf(DEFAULT_THREAD_POOL_SIZE))
            );

            // Create and start server
            RestServer server = new RestServer(port, hostname, threadPoolSize);
            server.start();

            // Add graceful shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n🛑 Shutting down server...");
                server.stop();
                LOGGER.info("✅ Server stopped successfully");
            }));

            // Keep server running
            Thread.currentThread().join();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create HTTP server", e);
            System.exit(1);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid configuration values for port or thread pool size", e);
            System.exit(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Server interrupted", e);
            System.exit(0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error starting server", e);
            System.exit(1);
        }
    }
}
