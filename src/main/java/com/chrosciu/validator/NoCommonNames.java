package com.chrosciu.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoCommonNamesValidator.class)
public @interface NoCommonNames {
    String message() default "Jan Kowalski is a common name and cannot be used!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
