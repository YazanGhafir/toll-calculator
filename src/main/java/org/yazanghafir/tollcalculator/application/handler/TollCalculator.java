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

    public int calculateToll(Vehicle vehicle) {
        // Check if the vehicle type or any of the dates are toll-free
        if (tollFreeValidator.isTollFree(vehicle)) {
            return 0; // Toll-free, so the total fee is 0
        }

        List<LocalDateTime> orderedPassageDates = vehicle.getVehicleDateTimes().stream()
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .sorted()
                .collect(Collectors.toList());

        int totalFee = 0;
        final LocalDateTime firstPassage = orderedPassageDates.get(0);
        LocalDateTime intervalStart = firstPassage;

        for (LocalDateTime passageTime : orderedPassageDates) {
            // Skip passage times that occurred before the current interval
            if (passageTime.isBefore(intervalStart)) {
                continue;
            }

            // Calculate the end time of the current interval (1 hour later)
            final LocalDateTime finalIntervalStart = intervalStart;
            final LocalDateTime intervalEnd = intervalStart.plusHours(1);

            int intervalMaxFee = orderedPassageDates.stream()
                    .filter(date -> !date.isBefore(finalIntervalStart) && date.isBefore(intervalEnd))
                    .mapToInt(date -> tollFeeRetriever.getTollFeeAmount(date.toLocalTime()))
                    .max()
                    .orElse(0);

            totalFee += intervalMaxFee;

            // Update intervalStart to the end of the current interval
            intervalStart = intervalEnd;
        }

        // Cap the total fee at 60 SEK
        return Math.min(totalFee, 60);
    }
}