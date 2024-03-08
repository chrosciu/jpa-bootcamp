package eu.chrost.validator;

import eu.chrost.domain.Company;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MirexProhibitedValidator implements ConstraintValidator<MirexProhibited, Company> {
    @Override
    public boolean isValid(Company company, ConstraintValidatorContext context) {
        return company.getName() == null || !company.getName().contains("Mirex");
    }
}
