package org.yazanghafir.tollcalculator.application.validation;


import de.jollyday.Holiday;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import org.springframework.stereotype.Service;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationFilePath;
import org.yazanghafir.tollcalculator.domain.configuration.VehicleType;
import org.yazanghafir.tollcalculator.domain.configuration.VehicleTypes;
import org.yazanghafir.tollcalculator.domain.entities.Vehicle;
import org.yazanghafir.tollcalculator.infrastructure.configuration.ConfigurationLoader;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TollFreeValidator implements ITollFreeValidator {

    /**
     * Checks if the vehicle type is toll-free based on the data from the Toll Vehicle Types JSON.
     *
     * @param type The type of the vehicle.
     * @return true if the vehicle is toll-free, false otherwise.
     */
    @Override
    public boolean isTollFreeVehicle(String type) {
        try {
            ConfigurationLoader<VehicleTypes> configUploader = new ConfigurationLoader<>();
            Optional<VehicleTypes> vehicleTypesOptional = configUploader.loadConfiguration(
                    ConfigurationFilePath.VEHICLE_TYPES, VehicleTypes.class);

            if (vehicleTypesOptional.isPresent()) {
                VehicleTypes vehicleTypes = vehicleTypesOptional.get();

                if (vehicleTypes.getVehicleTypes() != null) {
                    for (VehicleType vehicleType : vehicleTypes.getVehicleTypes()) {
                        if (vehicleType.getVehicleType().equalsIgnoreCase(type)) {
                            return vehicleType.getIsTollFreeVehicle();
                        }
                    }
                }
            }

            return false;
        } catch (Exception ex) {
            System.err.println("Error processing when checking the vehicle type for toll fee. Error: "
                    + ex.getMessage());
            return false;
        }
    }

    /**
     * Checks if the given date is a Swedish holiday or a weekend.
     *
     * @param date The date to check.
     * @return true if the date is a holiday or a weekend, false otherwise.
     */
    @Override
    public boolean isTollFreeDate(LocalDate date) {
        // Check if the date is a weekend
        if (date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7 || date.getMonthValue() == 7) {
            return true;
        }

        // Check if the date is a Swedish public holiday
        HolidayManager holidayManager = HolidayManager.getInstance(ManagerParameters.create("se"));
        Set<Holiday> holidays = holidayManager.getHolidays(date.getYear(), "se");

        // Convert the Set<Holiday> to Set<LocalDate>
        Set<LocalDate> holidayDates = holidays.stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());

        // Check if the date or the day before is a holiday
        if (holidayDates.contains(date) || holidayDates.contains(date.minusDays(1))) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the given vehicle is toll-free based on its type and the dates it was used.
     *
     * @param vehicle The vehicle to check.
     * @return true if the vehicle is toll-free on the given dates, false otherwise.
     */
    @Override
    public boolean isTollFree(Vehicle vehicle) {
        // Check if the vehicle type is toll-free
        if (isTollFreeVehicle(vehicle.getVehicleType())) {
            return true;
        }

        // Check if any of the dates the vehicle was used are toll-free
        for (Date date : vehicle.getVehicleDateTimes()) {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (isTollFreeDate(localDate)) {
                return true;
            }
        }

        return false;
    }
}