package org.yazanghafir.tollcalculator.application.handler;

import org.springframework.stereotype.Service;
import org.yazanghafir.tollcalculator.application.query.TollFeeAmountRetriever;
import org.yazanghafir.tollcalculator.application.validation.TollFreeValidator;
import org.yazanghafir.tollcalculator.domain.entities.Vehicle;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TollCalculator {

    private final TollFreeValidator tollFreeValidator;
    private final TollFeeAmountRetriever tollFeeRetriever;

    public TollCalculator(TollFreeValidator tollFreeValidator, TollFeeAmountRetriever tollFeeRetriever) {
        this.tollFreeValidator = tollFreeValidator;
        this.tollFeeRetriever = tollFeeRetriever;
    }

    /**
     * This method calculates the total fees per vehicle per day.
     * It first checks if it is Toll free day or vehicle, then the total fee is 0
     * Then it order the passage dates. Then it start with the first passage date
     * and calculate the maximum fee that should be paid for all passages during
     * 1 hour from the first passage and add that to the total fee.
     * Then we jump one hour ahead and find the first passage after this hour, start
     * from it and repeat the process until reaching the latest passage time and finally
     * return the total fee. It the total exceeds 60 it returns 60.
     *
     * @param vehicle      The vehicle object.
     * @return The calculated total amount fees that should be paid
     */
    public int calculateToll(Vehicle vehicle) {
        // Check if the vehicle type or any of the dates are toll-free
        if (tollFreeValidator.isTollFree(vehicle)) {
            return 0; // Toll-free, so the total fee is 0
        }

        // Convert vehicleDateTimes to LocalDateTime and sort them
        List<LocalDateTime> orderedPassageDates = vehicle.getVehicleDateTimes().stream()
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .sorted()
                .collect(Collectors.toList());

        int totalFee = 0;
        LocalDateTime intervalStart = orderedPassageDates.get(0);

        for (LocalDateTime passageTime : orderedPassageDates) {
            // Skip passage times that occurred before the current interval
            if (passageTime.isBefore(intervalStart)) {
                continue;
            }

            // Set the current interval start time
            intervalStart = passageTime;

            // Calculate the end time of the current interval (1 hour later)
            LocalDateTime intervalEnd = intervalStart.plusHours(1);
            LocalDateTime finalIntervalStart = intervalStart;

            // Select the highest fee within the current interval and add it to the totalFee
            int intervalMaxFee = orderedPassageDates.stream()
                    .filter(date -> !date.isBefore(finalIntervalStart) && date.isBefore(intervalEnd))
                    .mapToInt(date -> tollFeeRetriever.getTollFeeAmount(date.toLocalTime()))
                    .max()
                    .orElse(0);

            totalFee += intervalMaxFee;

            // Update intervalStart to the end of the current interval
            intervalStart = intervalEnd;
        }

        return Math.min(totalFee, 60);
    }
}