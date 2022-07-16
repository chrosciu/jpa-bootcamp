package com.chrosciu.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class JanuszProhibitedValidator implements ConstraintValidator<JanuszProhibited, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && value.toLowerCase().contains("janusz")) {
            return false;
        }
        return true;
    }
}
