package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext constraintValidatorContext) {
        if (dateTime != null) {
            return dateTime.isAfter(LocalDateTime.now());
        }
        return true;
    }
}
