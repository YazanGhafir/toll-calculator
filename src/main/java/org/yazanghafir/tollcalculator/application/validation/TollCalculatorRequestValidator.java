package org.yazanghafir.tollcalculator.application.validation;

import org.springframework.stereotype.Service;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationFilePath;
import org.yazanghafir.tollcalculator.domain.configuration.TollFees;
import org.yazanghafir.tollcalculator.domain.configuration.VehicleType;
import org.yazanghafir.tollcalculator.domain.configuration.VehicleTypes;
import org.yazanghafir.tollcalculator.infrastructure.configuration.ConfigurationLoader;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TollCalculatorRequestValidator {
    private final ConfigurationLoader<VehicleTypes> configLoader;

    public TollCalculatorRequestValidator(ConfigurationLoader<VehicleTypes> configLoader) {
        this.configLoader = configLoader;
    }

    /**
     * Validates both the vehicle type and that all vehicleDateTimes are within the same day.
     *
     * @param vehicleType      The type of the vehicle to validate.
     * @param vehicleDateTimes The list of vehicle date times to validate.
     * @return A validation message if either check fails, otherwise null.
     */
    public String validateRequest(String vehicleType, List<Date> vehicleDateTimes) {
        // Validate vehicle type
        String typeValidationMessage = validateVehicleType(vehicleType);
        if (typeValidationMessage != null) {
            return typeValidationMessage;
        }

        // Validate vehicle date times
        String dateValidationMessage = validateVehicleDateTimes(vehicleDateTimes);
        if (dateValidationMessage != null) {
            return dateValidationMessage;
        }

        return null; // Valid if both checks pass
    }

    /**
     * Validates that all vehicleDateTimes are within the same day.
     *
     * @param vehicleDateTimes The list of vehicle date times to validate.
     * @return A validation message if the dates are not within the same day, otherwise null.
     */
    public String validateVehicleDateTimes(List<Date> vehicleDateTimes) {
        if (vehicleDateTimes == null || vehicleDateTimes.isEmpty()) {
            return "No date times provided.";
        }

        LocalDate firstDate = vehicleDateTimes.get(0).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        for (Date date : vehicleDateTimes) {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!localDate.equals(firstDate)) {
                return "All vehicle date times must be within the same day.";
            }
        }

        return null; // Valid if all dates are within the same day
    }

    /**
     * Validates if the vehicleType is one of the included vehicleTypes.
     *
     * @param vehicleType The type of the vehicle to validate.
     * @return A validation message if the vehicleType is not valid, otherwise null.
     */
    public String validateVehicleType(String vehicleType) {
        try {
            Optional<VehicleTypes> vehicleTypesOptional = configLoader.loadConfiguration(
                    ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class);

            if (vehicleTypesOptional.isPresent()) {
                VehicleTypes vehicleTypes = vehicleTypesOptional.get();

                if (vehicleTypes.getVehicleTypes() != null) {
                    boolean isValidType = vehicleTypes.getVehicleTypes().stream()
                            .anyMatch(type -> type.getVehicleType().equalsIgnoreCase(vehicleType));
                    if (!isValidType) {
                        String availableTypes = vehicleTypes.getVehicleTypes().stream()
                                .map(VehicleType::getVehicleType)
                                .collect(Collectors.joining(", "));
                        return "Invalid vehicle type provided. Available types are: " + availableTypes + ".";
                    }
                }
            } else {
                return "Vehicle types configuration could not be loaded.";
            }
        } catch (Exception ex) {
            return "Error processing vehicle type validation. Error: " + ex.getMessage();
        }

        return null; // Valid if the vehicle type is in the list
    }
}
