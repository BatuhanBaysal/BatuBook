package com.batubook.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Gender {

    MALE, FEMALE, UNDISCLOSED;

    private static final Logger logger = LoggerFactory.getLogger(Gender.class);

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) {
            return UNDISCLOSED;
        }

        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid gender value: {}", value);
            return UNDISCLOSED;
        }
    }

    @JsonValue
    public String toJson() {
        String enumName = name().toLowerCase();
        return enumName.replace("ı", "i");
    }
}