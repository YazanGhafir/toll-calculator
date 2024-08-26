package org.yazanghafir.tollcalculator.application.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yazanghafir.tollcalculator.application.query.TollFeeAmountRetriever;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationFilePath;
import org.yazanghafir.tollcalculator.domain.configuration.TollFee;
import org.yazanghafir.tollcalculator.domain.configuration.TollFees;
import org.yazanghafir.tollcalculator.domain.entities.TollFeeRange;
import org.yazanghafir.tollcalculator.infrastructure.configuration.ConfigurationLoader;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TollFeeAmountRetrieverTest {

    @Mock
    private ConfigurationLoader<TollFees> configLoader;

    @InjectMocks
    private TollFeeAmountRetriever tollFeeAmountRetriever;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTollFeeAmount_WithinRange() {
        // Arrange
        TollFeeRange range1 = new TollFeeRange(8, LocalTime.of(6, 0), LocalTime.of(6, 29));
        TollFeeRange range2 = new TollFeeRange(13, LocalTime.of(6, 30), LocalTime.of(6, 59));
        when(configLoader.loadConfiguration(ConfigurationFilePath.TOLL_FEES, TollFees.class))
                .thenReturn(Optional.of(createMockTollFees(range1, range2)));

        // Act
        int fee = tollFeeAmountRetriever.getTollFeeAmount(LocalTime.of(6, 15));

        // Assert
        assertEquals(8, fee);
    }

    @Test
    void testGetTollFeeAmount_ExactStartTime() {
        // Arrange
        TollFeeRange range1 = new TollFeeRange(18, LocalTime.of(7, 0), LocalTime.of(7, 59));
        when(configLoader.loadConfiguration(ConfigurationFilePath.TOLL_FEES, TollFees.class))
                .thenReturn(Optional.of(createMockTollFees(range1)));

        // Act
        int fee = tollFeeAmountRetriever.getTollFeeAmount(LocalTime.of(7, 0));

        // Assert
        assertEquals(18, fee);
    }

    @Test
    void testGetTollFeeAmount_ExactEndTime() {
        // Arrange
        TollFeeRange range1 = new TollFeeRange(18, LocalTime.of(7, 0), LocalTime.of(7, 59));
        when(configLoader.loadConfiguration(ConfigurationFilePath.TOLL_FEES, TollFees.class))
                .thenReturn(Optional.of(createMockTollFees(range1)));

        // Act
        int fee = tollFeeAmountRetriever.getTollFeeAmount(LocalTime.of(7, 59));

        // Assert
        assertEquals(18, fee);
    }

    @Test
    void testGetTollFeeAmount_OutOfRange() {
        // Arrange
        TollFeeRange range1 = new TollFeeRange(8, LocalTime.of(6, 0), LocalTime.of(6, 29));
        when(configLoader.loadConfiguration(ConfigurationFilePath.TOLL_FEES, TollFees.class))
                .thenReturn(Optional.of(createMockTollFees(range1)));

        // Act
        int fee = tollFeeAmountRetriever.getTollFeeAmount(LocalTime.of(5, 59));

        // Assert
        assertEquals(0, fee); // Out of range, should return 0
    }

    @Test
    void testGetTollFeeAmount_NoMatchingRanges() {
        // Arrange
        when(configLoader.loadConfiguration(ConfigurationFilePath.TOLL_FEES, TollFees.class))
                .thenReturn(Optional.of(new TollFees(new ArrayList<>())));

        // Act
        int fee = tollFeeAmountRetriever.getTollFeeAmount(LocalTime.of(6, 15));

        // Assert
        assertEquals(0, fee); // No ranges, should return 0
    }

    @Test
    void testLoadTollFeeData_ValidConfig() {
        // Arrange
        TollFee tollFee1 = new TollFee(8, Arrays.asList("06:00-06:29", "08:30-14:59"));
        TollFee tollFee2 = new TollFee(18, Arrays.asList("07:00-07:59", "15:30-16:59"));
        TollFees tollFees = new TollFees(Arrays.asList(tollFee1, tollFee2));
        when(configLoader.loadConfiguration(ConfigurationFilePath.TOLL_FEES, TollFees.class))
                .thenReturn(Optional.of(tollFees));

        // Act
        List<TollFeeRange> tollFeeRanges = tollFeeAmountRetriever.loadTollFeeData();

        // Assert
        assertEquals(4, tollFeeRanges.size());
        assertEquals(8, tollFeeRanges.get(0).getFeeAmount());
        assertEquals(LocalTime.of(6, 0), tollFeeRanges.get(0).getStartTime());
        assertEquals(LocalTime.of(6, 29), tollFeeRanges.get(0).getEndTime());
        assertEquals(18, tollFeeRanges.get(2).getFeeAmount());
        assertEquals(LocalTime.of(7, 0), tollFeeRanges.get(2).getStartTime());
        assertEquals(LocalTime.of(7, 59), tollFeeRanges.get(2).getEndTime());
    }

    @Test
    void testLoadTollFeeData_InvalidTimeRange() {
        // Arrange
        TollFee tollFee1 = new TollFee(8, Arrays.asList("invalid-time-range"));
        TollFees tollFees = new TollFees(Arrays.asList(tollFee1));
        when(configLoader.loadConfiguration(ConfigurationFilePath.TOLL_FEES, TollFees.class))
                .thenReturn(Optional.of(tollFees));

        // Act
        List<TollFeeRange> tollFeeRanges = tollFeeAmountRetriever.loadTollFeeData();

        // Assert
        assertEquals(0, tollFeeRanges.size()); // Invalid time range, no valid TollFeeRange created
    }

    private TollFees createMockTollFees(TollFeeRange... ranges) {
        List<TollFee> tollFees = new ArrayList<>();
        for (TollFeeRange range : ranges) {
            TollFee tollFee = new TollFee(
                    range.getFeeAmount(),
                    Arrays.asList(range.getStartTime().toString() + "-" + range.getEndTime().toString())
            );
            tollFees.add(tollFee);
        }
        return new TollFees(tollFees);
    }
}
