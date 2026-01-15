package io.lunov.backend.model.validation;

import io.lunov.backend.model.dto.session.SessionSearchDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SessionInfoSearchValidator implements ConstraintValidator<SessionInfo, SessionSearchDTO> {
    @Override
    public boolean isValid(SessionSearchDTO value, ConstraintValidatorContext context) {
        if (value.getAccessType() == null &&  value.getContentType() == null) {
           return  false;
        }
        return true;
    }
}
