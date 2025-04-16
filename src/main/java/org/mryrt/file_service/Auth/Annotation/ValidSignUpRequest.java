package org.mryrt.file_service.Auth.Annotation;

import jakarta.validation.Constraint;
import org.mryrt.file_service.Auth.Validator.SignUpRequestValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SignUpRequestValidator.class)
public @interface ValidSignUpRequest {
}