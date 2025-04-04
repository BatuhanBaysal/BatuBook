package com.batubook.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

public enum Gender {

    MALE, FEMALE, UNDISCLOSED;

    private static final Logger logger = LoggerFactory.getLogger(Gender.class);

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) {
            return UNDISCLOSED;
        }

        return Arrays.stream(Gender.values())
                .filter(mt -> mt.name().equalsIgnoreCase(value.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Invalid Gender value: {}", value);
                    return new RuntimeException("Invalid Gender value: " + value);
                });
    }

    @JsonValue
    public String toJson() {
        String enumName = name().toLowerCase();
        return enumName.replace("ı", "i");
    }
}