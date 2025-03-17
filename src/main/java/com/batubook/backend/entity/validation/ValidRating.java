package com.batubook.backend.entity.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RatingValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRating {

    String message() default "Invalid rating. Rating must be a whole number or half-point (1, 1.5, 2, etc.)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}