package com.chrosciu.example;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class Person {
    @NotBlank
    private String name;

    public static void main(String[] args) {
        Person person = Person.builder().name("    ").build();
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);
            log.info("{}", constraintViolations);
        }
    }
}


