package com.batubook.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

public enum ActionType {

    REPOST, SAVE;

    private static final Logger logger = LoggerFactory.getLogger(ActionType.class);

    @JsonCreator
    public static ActionType fromString(String value) {
        if (value == null) {
            logger.error("ActionType value cannot be null");
            throw new IllegalArgumentException("ActionType value cannot be null");
        }

        return Arrays.stream(ActionType.values())
                .filter(mt -> mt.name().equalsIgnoreCase(value.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Invalid Action Type value: {}", value);
                    return new RuntimeException("Invalid Action Type value: " + value);
                });
    }

    @JsonValue
    public String toJson() {
        String enumName = name().toLowerCase();
        return enumName.replace("ı", "i");
    }
}