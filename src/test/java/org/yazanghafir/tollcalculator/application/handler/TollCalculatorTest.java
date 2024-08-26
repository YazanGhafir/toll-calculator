package org.yazanghafir.tollcalculator.application.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yazanghafir.tollcalculator.application.handler.TollCalculator;
import org.yazanghafir.tollcalculator.application.query.TollFeeAmountRetriever;
import org.yazanghafir.tollcalculator.application.validation.TollFreeValidator;
import org.yazanghafir.tollcalculator.domain.entities.Vehicle;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TollCalculatorTest {

    @Mock
    private TollFreeValidator tollFreeValidator;

    @Mock
    private TollFeeAmountRetriever tollFeeRetriever;

    @InjectMocks
    private TollCalculator tollCalculator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateToll_TollFreeTime() {
        // Arrange
        Date date1 = Date.from(LocalDateTime.of(2024, 8, 26, 5, 30)
                .atZone(ZoneId.systemDefault()).toInstant()); // 05:30 (Toll-free time)
        Vehicle vehicle = new Vehicle("ABC123", "Car", Arrays.asList(date1));
        when(tollFreeValidator.isTollFree(vehicle)).thenReturn(false);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(5, 30))).thenReturn(0);

        // Act
        int totalFee = tollCalculator.calculateToll(vehicle);

        // Assert
        assertEquals(0, totalFee);
    }

    @Test
    void testCalculateToll_LowFeeTime() {
        // Arrange
        Date date1 = Date.from(LocalDateTime.of(2024, 8, 26, 6, 15)
                .atZone(ZoneId.systemDefault()).toInstant()); // 06:15 (8 SEK)
        Vehicle vehicle = new Vehicle("ABC123", "Car", Arrays.asList(date1));
        when(tollFreeValidator.isTollFree(vehicle)).thenReturn(false);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(6, 15))).thenReturn(8);

        // Act
        int totalFee = tollCalculator.calculateToll(vehicle);

        // Assert
        assertEquals(8, totalFee);
    }

    @Test
    void testCalculateToll_HighFeeTime() {
        // Arrange
        Date date1 = Date.from(LocalDateTime.of(2024, 8, 26, 7, 30)
                .atZone(ZoneId.systemDefault()).toInstant()); // 07:30 (18 SEK)
        Vehicle vehicle = new Vehicle("ABC123", "Car", Arrays.asList(date1));
        when(tollFreeValidator.isTollFree(vehicle)).thenReturn(false);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(7, 30))).thenReturn(18);

        // Act
        int totalFee = tollCalculator.calculateToll(vehicle);

        // Assert
        assertEquals(18, totalFee);
    }

    @Test
    void testCalculateToll_MultiplePassagesWithinOneHour() {
        // Arrange
        Date date1 = Date.from(LocalDateTime.of(2024, 8, 26, 7, 15)
                .atZone(ZoneId.systemDefault()).toInstant()); // 07:15 (18 SEK)
        Date date2 = Date.from(LocalDateTime.of(2024, 8, 26, 7, 45)
                .atZone(ZoneId.systemDefault()).toInstant()); // 07:45 (18 SEK)
        Vehicle vehicle = new Vehicle("ABC123", "Car", Arrays.asList(date1, date2));
        when(tollFreeValidator.isTollFree(vehicle)).thenReturn(false);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(7, 15))).thenReturn(18);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(7, 45))).thenReturn(18);

        // Act
        int totalFee = tollCalculator.calculateToll(vehicle);

        // Assert
        assertEquals(18, totalFee); // Only the highest fee within the hour should be charged
    }

    @Test
    void testCalculateToll_MultiplePassagesExceedingOneHour() {
        // Arrange
        Date date1 = Date.from(LocalDateTime.of(2024, 8, 26, 6, 45)
                .atZone(ZoneId.systemDefault()).toInstant()); // 06:45 (13 SEK)
        Date date2 = Date.from(LocalDateTime.of(2024, 8, 26, 8, 15)
                .atZone(ZoneId.systemDefault()).toInstant()); // 08:15 (13 SEK)
        Vehicle vehicle = new Vehicle("ABC123", "Car", Arrays.asList(date1, date2));
        when(tollFreeValidator.isTollFree(vehicle)).thenReturn(false);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(6, 45))).thenReturn(13);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(8, 15))).thenReturn(13);

        // Act
        int totalFee = tollCalculator.calculateToll(vehicle);

        // Assert
        assertEquals(26, totalFee); // Fees from different intervals should be summed up
    }

    @Test
    void testCalculateToll_CappedAt60SEK() {
        // Arrange
        Date date1 = Date.from(LocalDateTime.of(2024, 8, 26, 6, 15)
                .atZone(ZoneId.systemDefault()).toInstant()); // 06:15 (8 SEK)
        Date date2 = Date.from(LocalDateTime.of(2024, 8, 26, 7, 15)
                .atZone(ZoneId.systemDefault()).toInstant()); // 07:15 (18 SEK)
        Date date3 = Date.from(LocalDateTime.of(2024, 8, 26, 15, 45)
                .atZone(ZoneId.systemDefault()).toInstant()); // 15:45 (18 SEK)
        Date date4 = Date.from(LocalDateTime.of(2024, 8, 26, 16, 45)
                .atZone(ZoneId.systemDefault()).toInstant()); // 16:45 (18 SEK)
        Vehicle vehicle = new Vehicle("ABC123", "Car", Arrays.asList(date1, date2, date3, date4));
        when(tollFreeValidator.isTollFree(vehicle)).thenReturn(false);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(6, 15))).thenReturn(8);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(7, 15))).thenReturn(18);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(15, 45))).thenReturn(18);
        when(tollFeeRetriever.getTollFeeAmount(LocalTime.of(16, 45))).thenReturn(18);

        // Act
        int totalFee = tollCalculator.calculateToll(vehicle);

        // Assert
        assertEquals(60, totalFee); // Total fee should be capped at 60 SEK
    }
}

