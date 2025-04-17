package org.mryrt.file_service.Auth.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mryrt.file_service.Auth.Annotation.ValidSignUpRequest;
import org.mryrt.file_service.Auth.Model.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class SignUpRequestValidator implements ConstraintValidator<ValidSignUpRequest, SignUpRequest> {

    @Autowired
    RequestValidator requestValidator;

    @Override
    public boolean isValid(SignUpRequest signUpRequest, ConstraintValidatorContext context) {
        requestValidator.validate(signUpRequest.getUsername(), signUpRequest.getPassword(), false);
        return true;
    }

}