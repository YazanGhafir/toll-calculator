package org.yazanghafir.tollcalculator.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.yazanghafir.tollcalculator.application.query.TollFeeAmountRetriever;
import org.yazanghafir.tollcalculator.application.validation.TollCalculatorRequestValidator;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationFilePath;
import org.yazanghafir.tollcalculator.domain.configuration.TollFees;
import org.yazanghafir.tollcalculator.domain.entities.Vehicle;
import org.yazanghafir.tollcalculator.infrastructure.configuration.ConfigurationLoader;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class TollCalculatorController {

    private final TollFeeAmountRetriever tollFeeChecker;
    private final TollCalculatorRequestValidator requestValidator;

    public TollCalculatorController(TollFeeAmountRetriever tollFeeChecker, TollCalculatorRequestValidator requestValidator) {
        this.tollFeeChecker = tollFeeChecker;
        this.requestValidator = requestValidator;
    }

    @GetMapping("/calculateToll")
    public String calculateToll(
            @RequestParam String vehiclePlate,
            @RequestParam String vehicleType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) List<Date> vehicleDateTimes) {

        // Validate both vehicle type and date times
        String validationMessage = requestValidator.validateRequest(vehicleType, vehicleDateTimes);
        if (validationMessage != null) {
            return validationMessage; // Return validation error message if any validation fails
        }

        // Convert vehicleDateTimes to LocalTime
        List<LocalTime> localVehicleTimes = vehicleDateTimes.stream()
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime())
                .collect(Collectors.toList());

        // Assuming you want to use the first time of the day for toll calculation
        LocalTime timeOfDay = localVehicleTimes.get(0);

        return tollFeeChecker.getTollFeeAmount(timeOfDay) + " SEK";
    }
}