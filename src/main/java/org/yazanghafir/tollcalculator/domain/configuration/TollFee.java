package org.yazanghafir.tollcalculator.domain.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TollFee {
    private int feeAmount;
    private List<String> timePoints;
}
