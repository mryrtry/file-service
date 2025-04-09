package org.mryrt.file_service.Auth.Annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.mryrt.file_service.Auth.Validator.LogInValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = "Username is required")
@Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
@Pattern(regexp = "^[a-zA-Z0-9_\\s]+$", message = "Username can only contain letters, numbers, and underscores")
@Constraint(validatedBy = LogInValidator.class)
public @interface Login {
    String message() default "Invalid username";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}