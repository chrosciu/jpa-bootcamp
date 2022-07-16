package com.chrosciu.app;

import com.chrosciu.domain.Company;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomValidation {

    public static void main(String[] args) {
        Company company = Company.builder().name("Januszex").build();
        Company otherCompany = Company.builder().name("Mirex").build();
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Company>> constraintViolations = validator.validate(company);
            log.info("{}", constraintViolations);
            constraintViolations = validator.validate(otherCompany);
            log.info("{}", constraintViolations);
        }
    }
}
