package org.yazanghafir.tollcalculator.domain.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TollFee {
    private int feeAmount;
    private List<String> timePoints;
}
