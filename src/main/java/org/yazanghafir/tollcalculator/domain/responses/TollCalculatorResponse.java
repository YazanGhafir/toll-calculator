package org.yazanghafir.tollcalculator.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TollCalculatorResponse extends Response {
    private double tollFee;
    private boolean success;
    private String message;
}
