package com.busticket.servlet;

import com.busticket.configuration.ApplicationConfiguration;
import com.busticket.util.LoggingConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * AppInitializer - Servlet context listener for application initialization
 * Initializes services when the application starts
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    private static final Logger LOGGER = LoggingConfig.getLogger("com.busticket.servlet.AppInitializer");

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize logging configuration first
        LoggingConfig.initialize();

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║              🚌  Bus Ticketer Service  🚌                     ║");
        System.out.println("║                                                               ║");
        System.out.println("║           Advanced Bus Booking & Reservation System           ║");
        System.out.println("║                    Version 1.0.0                             ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        try {
            // Initialize application configuration
            ApplicationConfiguration config = ApplicationConfiguration.getInstance();
            config.printConfiguration();

            System.out.println("✅ Service started successfully");
            System.out.println("\n📚 Available Endpoints:\n");
            System.out.println("System:");
            System.out.println("  GET  /health                         - Health check");
            System.out.println("  GET  /info                          - Service information");
            System.out.println();
            System.out.println("🎫 Bus Ticketing APIs:\n");
            System.out.println("Availability Check:");
            System.out.println("  GET  /api/v1/reservation/availability      - Check seat availability (query params)");
            System.out.println("  POST /api/v1/reservation/availability      - Check seat availability (JSON body)");
            System.out.println();
            System.out.println("Reservation:");
            System.out.println("  POST /api/v1/reservation/book          - Book a ticket with passenger details");
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════════\n");

        } catch (Exception e) {
            System.err.println("❌ Error initializing application: " + e.getMessage());
            e.printStackTrace();
            LOGGER.severe("Failed to initialize application: " + e.getMessage());
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("\n🛑 Application shutting down...");
        LOGGER.info("Application shutting down...");
        System.out.println("✅ Application stopped successfully\n");
    }
}
