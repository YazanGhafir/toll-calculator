package org.yazanghafir.tollcalculator.application.handler;

import org.yazanghafir.tollcalculator.domain.entities.Vehicle;

public interface ITollCalculator {
    int calculateToll(Vehicle vehicle);
}
