package com.chrosciu.example;

import com.chrosciu.domain.Person;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Slf4j
public class ManualPojoValidation {
    public static void main(String[] args) {
        Person person = Person.builder()
                .name("     ")
                .build();

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Person>> violations = validator.validate(person);
            log.info("Violations: {}", violations);
        }
    }
}
