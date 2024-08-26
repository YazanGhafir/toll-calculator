package org.yazanghafir.tollcalculator.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class TollFeeRange {
    private final int feeAmount;
    private final LocalTime startTime;
    private final LocalTime endTime;
}