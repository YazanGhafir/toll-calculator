package org.yazanghafir.tollcalculator.application.validation;

import java.util.Date;
import java.util.List;

public interface ITollCalculatorRequestValidator {
    String validateRequest(String vehicleType, List<Date> vehicleDateTimes);

    String validateVehicleDateTimes(List<Date> vehicleDateTimes);

    String validateVehicleType(String vehicleType);
}
