package com.busticket.service;

import com.busticket.domain.entity.BusFare;
import com.busticket.domain.response.FareResponse;
import com.busticket.service.DataStore;
import com.busticket.service.FareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FareService
 */
public class FareServiceTest {

    private FareService fareService;
    private DataStore dataStore;

    @BeforeEach
    public void setUp() {
        // Reset singleton instances for testing
        dataStore = DataStore.getInstance();
        dataStore.reset();
        fareService = FareService.getInstance();
    }

    @Test
    public void testCalculateFareSuccess() {
        // Arrange: We have predefined stops and fares from DataStore initialization
        int fromStopId = 1;  // Stop A
        int toStopId = 3;    // Stop C

        // Act
        Optional<FareResponse> response = fareService.calculateFare(fromStopId, toStopId);

        // Assert
        assertTrue(response.isPresent(), "Fare should be found");
        FareResponse fare = response.get();
        assertEquals(1, fare.getFromStopId(), "Correct from stop");
        assertEquals(3, fare.getToStopId(), "Correct to stop");
        assertNotNull(fare.getFare(), "Fare price should not be null");
    }

    @Test
    public void testCalculateFareInvalidStops() {
        // Arrange: Use non-existent stop IDs
        int fromStopId = 999;
        int toStopId = 998;

        // Act
        Optional<FareResponse> response = fareService.calculateFare(fromStopId, toStopId);

        // Assert
        assertFalse(response.isPresent(), "Fare should not be found for invalid stops");
    }

    @Test
    public void testGetAllFares() {
        // Act
        Collection<BusFare> fares = fareService.getAllFares();

        // Assert
        assertNotNull(fares, "Fares collection should not be null");
        assertTrue(fares.size() > 0, "Should have fares");
    }

    @Test
    public void testAddFare() {
        // Arrange
        int fromStopId = 1;
        int toStopId = 4;
        BigDecimal price = new BigDecimal("15.50");

        // Act
        BusFare newFare = fareService.addFare(fromStopId, toStopId, price);

        // Assert
        assertNotNull(newFare, "New fare should not be null");
        assertEquals(fromStopId, newFare.getFromStopId(), "From stop should match");
        assertEquals(toStopId, newFare.getToStopId(), "To stop should match");
        assertEquals(price, newFare.getPrice(), "Price should match");
    }

}
