package org.yazanghafir.tollcalculator.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationFilePath;
import org.yazanghafir.tollcalculator.domain.configuration.ConfigurationObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class ConfigurationLoader<T extends ConfigurationObject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<T> loadConfiguration(ConfigurationFilePath filePathType, Class<T> valueType) {
        String jsonFilePath = filePathType.getFilePath();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath)) {
            if (inputStream != null) {
                T dataObj = objectMapper.readValue(inputStream, valueType);
                return Optional.ofNullable(dataObj);
            } else {
                System.err.println("Error: JSON file " + jsonFilePath + " not found.");
            }
        } catch (IOException ex) {
            System.err.println("Error occurred when loading data from the JSON file " + jsonFilePath + ": " + ex.getMessage());
        }

        return Optional.empty();
    }
}
