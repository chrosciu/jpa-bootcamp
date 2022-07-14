package com.chrosciu.validator;

import com.chrosciu.domain.Employee;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoCommonNamesValidator implements ConstraintValidator<NoCommonNames, Employee> {
    @Override
    public boolean isValid(Employee employee, ConstraintValidatorContext constraintValidatorContext) {
        if ("Jan".equals(employee.getFirstName()) && "Kowalski".equals(employee.getLastName())) {
            return false;
        }
        return true;
     }
}
