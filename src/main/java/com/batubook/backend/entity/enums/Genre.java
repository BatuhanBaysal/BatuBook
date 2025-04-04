package com.batubook.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

public enum Genre {

    NOVEL, ADVENTURE, SCIENCE_FICTION, FANTASY, HORROR, THRILLER, CRIME, DYSTOPIA, ROMANCE;

    private static final Logger logger = LoggerFactory.getLogger(Genre.class);

    @JsonCreator
    public static Genre fromString(String value) {
        if (value == null) {
            logger.error("Genre value cannot be null");
            throw new IllegalArgumentException("Genre value cannot be null");
        }

        return Arrays.stream(Genre.values())
                .filter(mt -> mt.name().equalsIgnoreCase(value.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Invalid Genre value: {}", value);
                    return new RuntimeException("Invalid Genre value: " + value);
                });
    }

    @JsonValue
    public String toJson() {
        String enumName = name().toLowerCase();
        return enumName.replace("ı", "i");
    }
}