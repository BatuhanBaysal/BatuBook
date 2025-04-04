package com.batubook.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

public enum MessageType {

    PERSONAL, BOOK, REVIEW, QUOTE;

    private static final Logger logger = LoggerFactory.getLogger(MessageType.class);

    @JsonCreator
    public static MessageType fromString(String value) {
        if (value == null) {
            return PERSONAL;
        }

        return Arrays.stream(MessageType.values())
                .filter(mt -> mt.name().equalsIgnoreCase(value.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Invalid Message Type value: {}", value);
                    return new RuntimeException("Invalid Message Type value: " + value);
                });
    }

    @JsonValue
    public String toJson() {
        String enumName = name().toLowerCase();
        return enumName.replace("ı", "i");
    }
}