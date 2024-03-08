package eu.chrost.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MirexProhibitedValidator.class)
public @interface MirexProhibited {
    String message() default "Precz z Januszeksami!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
