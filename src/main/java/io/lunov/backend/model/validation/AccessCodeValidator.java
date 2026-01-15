package io.lunov.backend.model.validation;

import io.lunov.backend.model.dto.session.SessionInfoDTO;
import io.lunov.backend.model.entity.Session;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccessCodeValidator implements ConstraintValidator<AccessCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return true;
    }

    protected void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
