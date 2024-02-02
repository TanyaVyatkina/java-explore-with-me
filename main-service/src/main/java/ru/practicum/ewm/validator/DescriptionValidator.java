package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DescriptionValidator implements ConstraintValidator<Description, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s != null) {
            return s.length() >= 20 && s.length() <= 7000;
        }
        return true;
    }
}
