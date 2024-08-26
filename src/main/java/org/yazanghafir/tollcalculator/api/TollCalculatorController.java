package org.yazanghafir.tollcalculator.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.yazanghafir.tollcalculator.application.handler.TollCalculator;
import org.yazanghafir.tollcalculator.application.query.TollFeeAmountRetriever;
import org.yazanghafir.tollcalculator.application.validation.TollCalculatorRequestValidator;
import org.yazanghafir.tollcalculator.domain.entities.Vehicle;
import org.yazanghafir.tollcalculator.domain.responses.TollCalculatorResponse;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/toll")
public class TollCalculatorController {

    private final TollCalculatorRequestValidator requestValidator;
    private final TollCalculator tollCalculator;

    public TollCalculatorController(TollCalculatorRequestValidator requestValidator, TollCalculator tollCalculator) {
        this.requestValidator = requestValidator;
        this.tollCalculator = tollCalculator;
    }

    @PostMapping("/vehicle")
    public TollCalculatorResponse calculateToll(
            @RequestParam String vehiclePlate,
            @RequestParam String vehicleType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) List<Date> vehicleDateTimes) {

        try
        {
            // Validate both vehicle type and date times
            String validationMessage = requestValidator.validateRequest(vehicleType, vehicleDateTimes);
            if (validationMessage != null) {
                return new TollCalculatorResponse(0, false, validationMessage);
            }

            // Create a vehicle object from the request parameters
            Vehicle vehicle = new Vehicle(vehiclePlate, vehicleType, vehicleDateTimes);

            // Calculate the total toll fee for the vehicle
            int totalTollFee = tollCalculator.calculateToll(vehicle);

            // Return the total toll fee as a response
            return new TollCalculatorResponse(
                    totalTollFee, true, "Total toll fee: " + totalTollFee + " SEK");
        }
        catch (Exception ex)
        {
            return new TollCalculatorResponse(
                    0, false, "Error processing the request. Error: " + ex.getMessage());
        }
    }
}