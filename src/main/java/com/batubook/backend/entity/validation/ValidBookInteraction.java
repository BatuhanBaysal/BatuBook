package com.batubook.backend.entity.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookInteractionValidator.class)
@Documented
public @interface ValidBookInteraction {

    String message() default "To like a book, you must first read it.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
