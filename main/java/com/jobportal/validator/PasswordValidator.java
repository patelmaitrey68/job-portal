package com.jobportal.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.jobportal.dto.Password;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    
    private String regexp;
    
    @Override
    public void initialize(Password passwordAnnotation) {
        this.regexp = passwordAnnotation.regexp();
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true; // Let @NotBlank handle null validation
        }
        return password.matches(regexp);
    }
}

