package org.mryrt.file_service.Auth.Annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.mryrt.file_service.Auth.Validator.LogInRequestValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LogInRequestValidator.class)
public @interface ValidLogInRequest {
    String message() default "Invalid log-in request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}