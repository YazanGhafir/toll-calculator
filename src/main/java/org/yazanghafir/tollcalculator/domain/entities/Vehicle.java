package org.yazanghafir.tollcalculator.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class Vehicle {
    private String vehiclePlate;
    private String vehicleType;
    private List<Date> vehicleDateTimes;
}
