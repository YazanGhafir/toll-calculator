package org.yazanghafir.tollcalculator.application.query;

import org.springframework.stereotype.Service;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationFilePath;
import org.yazanghafir.tollcalculator.domain.configuration.TollFee;
import org.yazanghafir.tollcalculator.domain.configuration.TollFees;
import org.yazanghafir.tollcalculator.domain.entities.TollFeeRange;
import org.yazanghafir.tollcalculator.infrastructure.configuration.ConfigurationLoader;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TollFeeAmountRetriever implements ITollFeeAmountRetriever {

    private final ConfigurationLoader<TollFees> configLoader;

    public TollFeeAmountRetriever(ConfigurationLoader<TollFees> configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public int getTollFeeAmount(LocalTime timeOfDay) {
        List<TollFeeRange> tollFeeRanges = loadTollFeeData();

        return tollFeeRanges.stream()
                .filter(range -> !timeOfDay.isBefore(range.getStartTime()) && !timeOfDay.isAfter(range.getEndTime()))
                .map(TollFeeRange::getFeeAmount)
                .findFirst()
                .orElse(0); // Default fee when no matching range is found.
    }

    @Override
    public List<TollFeeRange> loadTollFeeData() {
        List<TollFeeRange> tollFeeRanges = new ArrayList<>();

        try {
            Optional<TollFees> tollFeeData = configLoader.loadConfiguration(ConfigurationFilePath.TOLL_FEES, TollFees.class);

            if (tollFeeData.isPresent()) {
                for (TollFee timePoint : tollFeeData.get().getTollFees()) {
                    for (String timeRangeStr : timePoint.getTimePoints()) {
                        String[] timeRangeParts = timeRangeStr.split("-");
                        if (timeRangeParts.length == 2) {
                            LocalTime startTime = LocalTime.parse(timeRangeParts[0]);
                            LocalTime endTime = LocalTime.parse(timeRangeParts[1]);

                            TollFeeRange range = new TollFeeRange(timePoint.getFeeAmount(), startTime, endTime);
                            tollFeeRanges.add(range);
                        } else {
                            System.err.println("Invalid time range format: " + timeRangeStr);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error processing time range. Error: " + ex.getMessage());
        }

        return tollFeeRanges;
    }
}