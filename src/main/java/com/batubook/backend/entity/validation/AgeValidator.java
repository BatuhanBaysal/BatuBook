package com.batubook.backend.entity.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return false;
        }

        return dateOfBirth.isBefore(LocalDate.now().minusYears(18));
    }
}