package org.yazanghafir.tollcalculator.application.validation;

import de.jollyday.Holiday;
import de.jollyday.HolidayManager;
import de.jollyday.HolidayType;
import de.jollyday.ManagerParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yazanghafir.tollcalculator.application.validation.TollFreeValidator;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationFilePath;
import org.yazanghafir.tollcalculator.domain.configuration.VehicleType;
import org.yazanghafir.tollcalculator.domain.configuration.VehicleTypes;
import org.yazanghafir.tollcalculator.domain.entities.Vehicle;
import org.yazanghafir.tollcalculator.infrastructure.configuration.ConfigurationLoader;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TollFreeValidatorTest {

    @Mock
    private ConfigurationLoader<VehicleTypes> configLoader;

    @Mock
    private HolidayManager holidayManager;

    @InjectMocks
    private TollFreeValidator tollFreeValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsTollFreeVehicle_TollFreeVehicle() {
        // Arrange
        VehicleType tollFreeVehicleType = new VehicleType("Emergency", true);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(tollFreeVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        // Act
        boolean isTollFree = tollFreeValidator.isTollFreeVehicle("Emergency");

        // Assert
        assertTrue(isTollFree);
    }

    @Test
    void testIsTollFreeVehicle_NotTollFreeVehicle() {
        // Arrange
        VehicleType notTollFreeVehicleType = new VehicleType("Car", false);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(notTollFreeVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        // Act
        boolean isTollFree = tollFreeValidator.isTollFreeVehicle("Car");

        // Assert
        assertFalse(isTollFree);
    }

    @Test
    void testIsTollFreeDate_Weekend() {
        // Arrange
        LocalDate saturday = LocalDate.of(2024, 8, 24); // A Saturday

        // Act
        boolean isTollFree = tollFreeValidator.isTollFreeDate(saturday);

        // Assert
        assertTrue(isTollFree);
    }

    @Test
    void testIsTollFreeDate_Holiday() {
        // Arrange
        LocalDate christmas = LocalDate.of(2024, 12, 25); // Christmas
        Holiday holiday = new Holiday(christmas, "Christmas Day", HolidayType.OFFICIAL_HOLIDAY);
        Set<Holiday> holidays = new HashSet<>(Collections.singletonList(holiday));

        when(holidayManager.getHolidays(christmas.getYear(), "se")).thenReturn(holidays);

        // Act
        boolean isTollFree = tollFreeValidator.isTollFreeDate(christmas);

        // Assert
        assertTrue(isTollFree);
    }

    @Test
    void testIsTollFreeDate_NotHolidayOrWeekend() {
        // Arrange
        LocalDate weekday = LocalDate.of(2024, 8, 26); // A Monday (not a holiday)

        // Act
        boolean isTollFree = tollFreeValidator.isTollFreeDate(weekday);

        // Assert
        assertFalse(isTollFree);
    }

    @Test
    void testIsTollFree_VehicleAndDateBothTollFree() {
        // Arrange
        VehicleType tollFreeVehicleType = new VehicleType("Emergency", true);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(tollFreeVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        LocalDate saturday = LocalDate.of(2024, 8, 24); // A Saturday
        Date date = Date.from(saturday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Vehicle vehicle = new Vehicle("ABC123", "Emergency", Arrays.asList(date));

        // Act
        boolean isTollFree = tollFreeValidator.isTollFree(vehicle);

        // Assert
        assertTrue(isTollFree);
    }

    @Test
    void testIsTollFree_NotTollFreeVehicleButTollFreeDate() {
        // Arrange
        VehicleType notTollFreeVehicleType = new VehicleType("Car", false);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(notTollFreeVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        LocalDate saturday = LocalDate.of(2024, 8, 24); // A Saturday
        Date date = Date.from(saturday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Vehicle vehicle = new Vehicle("ABC123", "Car", Arrays.asList(date));

        // Act
        boolean isTollFree = tollFreeValidator.isTollFree(vehicle);

        // Assert
        assertTrue(isTollFree); // Date is toll-free even if the vehicle is not
    }

    @Test
    void testIsTollFree_TollFreeVehicleButNotTollFreeDate() {
        // Arrange
        VehicleType tollFreeVehicleType = new VehicleType("Emergency", true);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(tollFreeVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        LocalDate weekday = LocalDate.of(2024, 8, 26); // A Monday
        Date date = Date.from(weekday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Vehicle vehicle = new Vehicle("ABC123", "Emergency", Arrays.asList(date));

        // Act
        boolean isTollFree = tollFreeValidator.isTollFree(vehicle);

        // Assert
        assertTrue(isTollFree); // Vehicle is toll-free even if the date is not
    }

    @Test
    void testIsTollFree_NeitherTollFreeVehicleNorDate() {
        // Arrange
        VehicleType notTollFreeVehicleType = new VehicleType("Car", false);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(notTollFreeVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        LocalDate weekday = LocalDate.of(2024, 8, 26); // A Monday
        Date date = Date.from(weekday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Vehicle vehicle = new Vehicle("ABC123", "Car", Arrays.asList(date));

        // Act
        boolean isTollFree = tollFreeValidator.isTollFree(vehicle);

        // Assert
        assertFalse(isTollFree); // Neither vehicle nor date is toll-free
    }
}
