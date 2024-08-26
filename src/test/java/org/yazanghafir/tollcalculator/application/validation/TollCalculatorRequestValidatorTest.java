package org.yazanghafir.tollcalculator.application.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yazanghafir.tollcalculator.application.validation.TollCalculatorRequestValidator;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationFilePath;
import org.yazanghafir.tollcalculator.domain.configuration.VehicleType;
import org.yazanghafir.tollcalculator.domain.configuration.VehicleTypes;
import org.yazanghafir.tollcalculator.infrastructure.configuration.ConfigurationLoader;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TollCalculatorRequestValidatorTest {

    @Mock
    private ConfigurationLoader<VehicleTypes> configLoader;

    @InjectMocks
    private TollCalculatorRequestValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateRequest_ValidRequest() {
        // Arrange
        VehicleType validVehicleType = new VehicleType("Car", false);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(validVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        LocalDate date = LocalDate.of(2024, 8, 26);
        Date vehicleDateTime = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Act
        String validationMessage = validator.validateRequest("Car", Arrays.asList(vehicleDateTime));

        // Assert
        assertEquals(null, validationMessage); // Valid request, should return null
    }

    @Test
    void testValidateRequest_InvalidVehicleType() {
        // Arrange
        VehicleType validVehicleType = new VehicleType("Car", false);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(validVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        LocalDate date = LocalDate.of(2024, 8, 26);
        Date vehicleDateTime = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Act
        String validationMessage = validator.validateRequest("Truck", Arrays.asList(vehicleDateTime));

        // Assert
        assertEquals("Invalid vehicle type provided. Available types are: Car.", validationMessage);
    }

    @Test
    void testValidateRequest_InvalidVehicleDateTimes() {
        // Arrange
        VehicleType validVehicleType = new VehicleType("Car", false);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(validVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        LocalDate date1 = LocalDate.of(2024, 8, 26);
        LocalDate date2 = LocalDate.of(2024, 8, 27); // A different day
        Date vehicleDateTime1 = Date.from(date1.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date vehicleDateTime2 = Date.from(date2.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Act
        String validationMessage = validator.validateRequest("Car", Arrays.asList(vehicleDateTime1, vehicleDateTime2));

        // Assert
        assertEquals("All vehicle date times must be within the same day.", validationMessage);
    }

    @Test
    void testValidateVehicleType_ValidType() {
        // Arrange
        VehicleType validVehicleType = new VehicleType("Car", false);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(validVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        // Act
        String validationMessage = validator.validateVehicleType("Car");

        // Assert
        assertEquals(null, validationMessage); // Valid type, should return null
    }

    @Test
    void testValidateVehicleType_InvalidType() {
        // Arrange
        VehicleType validVehicleType = new VehicleType("Car", false);
        VehicleTypes vehicleTypes = new VehicleTypes(Arrays.asList(validVehicleType));
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.of(vehicleTypes));

        // Act
        String validationMessage = validator.validateVehicleType("Truck");

        // Assert
        assertEquals("Invalid vehicle type provided. Available types are: Car.", validationMessage);
    }

    @Test
    void testValidateVehicleType_ConfigurationLoadFailure() {
        // Arrange
        when(configLoader.loadConfiguration(ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class))
                .thenReturn(Optional.empty());

        // Act
        String validationMessage = validator.validateVehicleType("Car");

        // Assert
        assertEquals("Vehicle types configuration could not be loaded.", validationMessage);
    }

    @Test
    void testValidateVehicleDateTimes_AllSameDay() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 8, 26);
        Date vehicleDateTime1 = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date vehicleDateTime2 = Date.from(date.atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant());

        // Act
        String validationMessage = validator.validateVehicleDateTimes(Arrays.asList(vehicleDateTime1, vehicleDateTime2));

        // Assert
        assertEquals(null, validationMessage); // All dates are the same, should return null
    }

    @Test
    void testValidateVehicleDateTimes_DifferentDays() {
        // Arrange
        LocalDate date1 = LocalDate.of(2024, 8, 26);
        LocalDate date2 = LocalDate.of(2024, 8, 27); // A different day
        Date vehicleDateTime1 = Date.from(date1.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date vehicleDateTime2 = Date.from(date2.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Act
        String validationMessage = validator.validateVehicleDateTimes(Arrays.asList(vehicleDateTime1, vehicleDateTime2));

        // Assert
        assertEquals("All vehicle date times must be within the same day.", validationMessage);
    }
}
