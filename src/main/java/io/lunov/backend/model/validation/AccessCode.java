package io.lunov.backend.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AccessCodeValidator.class})
public @interface AccessCode {
    String message() default "Invalid Access Code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
