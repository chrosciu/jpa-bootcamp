package com.chrosciu.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JanuszProhibitedValidator.class)
public @interface JanuszProhibited {
    String message() default "Precz z Januszexami";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
