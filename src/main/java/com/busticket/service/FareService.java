package com.busticket.service;

import com.busticket.domain.entity.BusFare;
import com.busticket.domain.response.FareResponse;
import com.busticket.domain.entity.Stop;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

/**
 * FareService handles all fare calculations and management
 * Uses in-memory DataStore for fare data
 */
public class FareService {
    private final com.busticket.service.DataStore dataStore;
    private static FareService instance;

    private FareService() {
        this.dataStore = com.busticket.service.DataStore.getInstance();
    }

    /**
     * Get singleton instance of FareService
     */
    public static synchronized FareService getInstance() {
        if (instance == null) {
            instance = new FareService();
        }
        return instance;
    }

    /**
     * Calculate fare between two stops
     */
    public Optional<FareResponse> calculateFare(int fromStopId, int toStopId) {
        Optional<?> fromStopOpt = dataStore.getStop(fromStopId);
        Optional<?> toStopOpt = dataStore.getStop(toStopId);

        if (fromStopOpt.isEmpty() || toStopOpt.isEmpty()) {
            return Optional.empty();
        }

        Stop fromStop = (Stop) fromStopOpt.get();
        Stop toStop = (Stop) toStopOpt.get();

        // Fetch fare from data store using stop IDs
        Optional<BusFare> fare = dataStore.getFareByStops(fromStopId, toStopId);
        if (fare.isPresent()) {
            BusFare busFare = fare.get();
            FareResponse response = new FareResponse(
                    fromStop,
                    toStop,
                    busFare.getPrice(),
                    fromStopId,
                    toStopId,
                    busFare.getLastUpdated(),
                    false
            );
            return Optional.of(response);
        }

        return Optional.empty();
    }

    /**
     * Get all fares
     */
    public Collection<BusFare> getAllFares() {
        return dataStore.getAllFares();
    }

    /**
     * Add new fare
     */
    public BusFare addFare(int fromStopId, int toStopId, BigDecimal price) {
        return dataStore.addFare(fromStopId, toStopId, price);
    }
}
