package com.busticket.configuration;

import com.busticket.service.DataStore;
import com.busticket.service.FareService;
import com.busticket.service.JourneyService;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Application configuration - initializes and configures core services
 * Pure in-memory storage for all data
 */
public class ApplicationConfiguration {
    private static ApplicationConfiguration instance;
    private DataStore dataStore;
    private FareService fareService;
    private JourneyService journeyService;

    private ApplicationConfiguration() {
        initialize();
    }

    /**
     * Get singleton instance
     */
    public static synchronized ApplicationConfiguration getInstance() {
        if (instance == null) {
            instance = new ApplicationConfiguration();
        }
        return instance;
    }

    /**
     * Initialize all services
     */
    private void initialize() {
        // Initialize DataStore (in-memory storage)
        this.dataStore = DataStore.getInstance();

        // Initialize services
        this.fareService = FareService.getInstance();
        this.journeyService = JourneyService.getInstance();

        // Load sample journeys for production
        loadSampleJourneys();

        System.out.println("✅ Application Configuration initialized successfully");
        System.out.println("✅ In-Memory Storage: ACTIVE (all data)");
        System.out.println("Thread Pool Size: " + ServerConfiguration.THREAD_POOL_SIZE);
    }

    /**
     * Load sample journeys for testing  use
     * Creates 1 return journey pair per route per day (forward in morning, return in evening)
     * Routes: A↔B (Rs.50), A↔C (Rs.100), A↔D (Rs.150), B↔C (Rs.50), B↔D (Rs.100), C↔D (Rs.50)
     *
     * IMPORTANT: Seat Sharing Across Routes
     * - All forward journeys on the same day share the same bus (and therefore seats)
     * - All return journeys on the same day share a different bus (and therefore seats)
     * - Example: If seat 1A is booked on A→B, it's unavailable on B→C, A→C, etc. (same day)
     */
    private void loadSampleJourneys() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Define all routes as pairs: forward and return
        String[][] routes = {
            {"A", "B"}, {"A", "C"}, {"A", "D"},
            {"B", "C"}, {"B", "D"}, {"C", "D"}
        };

        int journeyCounter = 101;
        int busIdForForwardJourneys = 1;
        int busIdForReturnJourneys = 2;

        // Create journeys for 3 days
        for (int day = 0; day < 3; day++) {
            LocalDate journeyDate = today.plusDays(day);
            LocalDateTime baseTime = now.plusDays(day);

            // Bus IDs: Each day gets 2 buses (one for forward routes, one for return routes)
            int forwardBusId = busIdForForwardJourneys + (day * 2);
            int returnBusId = busIdForReturnJourneys + (day * 2);

            // Create forward and return journeys for each route
            for (String[] route : routes) {
                String origin = route[0];
                String destination = route[1];
                String journeyNum = String.format("JN-%d%d", journeyCounter / 100, journeyCounter % 100);

                // Forward journey (morning - 8 AM) - Uses forward bus
                // All forward journeys on same day share the same bus, so seats are shared
                journeyService.createJourney(forwardBusId, journeyNum, journeyDate, baseTime.plusHours(8),
                    origin, destination, origin + "->" + destination, 40);

                // Return journey (evening - 6 PM) - Uses return bus
                // All return journeys on same day share the same bus, so seats are shared
                journeyService.createJourney(returnBusId, journeyNum + "R", journeyDate, baseTime.plusHours(18),
                    destination, origin, destination + "->" + origin, 40);

                journeyCounter++;
            }
        }
    }

    /**
     * Get DataStore instance
     */
    public DataStore getDataStore() {
        return dataStore;
    }

    /**
     * Get FareService instance
     */
    public FareService getFareService() {
        return fareService;
    }

    /**
     * Reset all services
     */
    public void reset() {
        dataStore.reset();
        System.out.println("Application Configuration reset");
    }

    /**
     * Print configuration details
     */
    public void printConfiguration() {
        System.out.println("\n=== Application Configuration ===");
        System.out.println("Hostname: " + ServerConfiguration.HOSTNAME);
        System.out.println("Port: " + ServerConfiguration.PORT);
        System.out.println("Thread Pool Size: " + ServerConfiguration.THREAD_POOL_SIZE);
        System.out.println("Max Request Body: " + ServerConfiguration.MAX_REQUEST_BODY_SIZE + " bytes");
        System.out.println("Content Type: " + ServerConfiguration.CONTENT_TYPE_JSON);
        System.out.println("Storage: Pure In-Memory (HashMap/ArrayList)");
        System.out.println("=====================================\n");
    }
}
