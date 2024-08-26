package org.yazanghafir.tollcalculator.domain.configuration;

import lombok.Getter;

@Getter
public enum ConfigurationFilePath {
    TOLL_FEES("TollFees.json"),
    VEHICLE_TYPES("VehicleTypes.json");

    private final String filePath;

    ConfigurationFilePath(String filePath) {
        this.filePath = filePath;
    }
}
