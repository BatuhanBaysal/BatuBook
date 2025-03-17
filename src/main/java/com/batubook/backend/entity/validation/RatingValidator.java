package com.batubook.backend.entity.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class RatingValidator implements ConstraintValidator<ValidRating, BigDecimal> {

    @Override
    public void initialize(ValidRating constraintAnnotation) {

    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.compareTo(BigDecimal.valueOf(1)) < 0 || value.compareTo(BigDecimal.valueOf(5)) > 0) {
            return false;
        }

        BigDecimal[] validRatings = { BigDecimal.valueOf(1), BigDecimal.valueOf(1.5), BigDecimal.valueOf(2),
                BigDecimal.valueOf(2.5), BigDecimal.valueOf(3), BigDecimal.valueOf(3.5),
                BigDecimal.valueOf(4), BigDecimal.valueOf(4.5), BigDecimal.valueOf(5) };

        for (BigDecimal validRating : validRatings) {
            if (validRating.compareTo(value) == 0) {
                return true;
            }
        }

        return false;
    }
}