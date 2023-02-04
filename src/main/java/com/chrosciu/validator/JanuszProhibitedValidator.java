package com.chrosciu.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class JanuszProhibitedValidator implements ConstraintValidator<JanuszProhibited, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s != null && s.toLowerCase().contains("janusz")) {
            return false;
        }
        return true;
    }
}
