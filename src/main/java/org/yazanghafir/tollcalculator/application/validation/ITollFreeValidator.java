package org.yazanghafir.tollcalculator.application.validation;

import org.yazanghafir.tollcalculator.domain.entities.Vehicle;

import java.time.LocalDate;

public interface ITollFreeValidator {
    boolean isTollFreeVehicle(String type);

    boolean isTollFreeDate(LocalDate date);

    boolean isTollFree(Vehicle vehicle);
}
