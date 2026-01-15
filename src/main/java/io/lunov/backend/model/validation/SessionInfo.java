package io.lunov.backend.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {SessionInfoSearchValidator.class})
public @interface SessionInfo {
    String message() default "invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}