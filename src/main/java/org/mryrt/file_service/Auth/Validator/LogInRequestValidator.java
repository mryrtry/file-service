package org.mryrt.file_service.Auth.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mryrt.file_service.Auth.Annotation.ValidLogInRequest;
import org.mryrt.file_service.Auth.Model.LogInRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class LogInRequestValidator implements ConstraintValidator<ValidLogInRequest, LogInRequest> {

    @Autowired
    RequestValidator requestValidator;

    @Override
    public boolean isValid(LogInRequest logInRequest, ConstraintValidatorContext context) {
        requestValidator.validate(logInRequest.getUsername(), logInRequest.getPassword(), true);
        return true;
    }

}