package com.batubook.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Role {

    ADMIN, USER;

    private static final Logger logger = LoggerFactory.getLogger(Role.class);

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) {
            return USER;
        }

        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role value: {}", value);
            return USER;
        }
    }

    @JsonValue
    public String toJson() {
        String enumName = name().toLowerCase();
        return enumName.replace("ı", "i");
    }
}