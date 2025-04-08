package com.batubook.backend.entity.validation;

import com.batubook.backend.entity.BookInteractionEntity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BookInteractionValidator implements ConstraintValidator<ValidBookInteraction, BookInteractionEntity> {

    @Override
    public boolean isValid(BookInteractionEntity bookInteraction, ConstraintValidatorContext context) {
        return !(bookInteraction.getIsLiked() && !bookInteraction.getIsRead());
    }
}