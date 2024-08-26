package org.yazanghafir.tollcalculator.application.query;

import org.yazanghafir.tollcalculator.domain.entities.TollFeeRange;

import java.time.LocalTime;
import java.util.List;

public interface ITollFeeAmountRetriever {
    int getTollFeeAmount(LocalTime timeOfDay);

    List<TollFeeRange> loadTollFeeData();
}
