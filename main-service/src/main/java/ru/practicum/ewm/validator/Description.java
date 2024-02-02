package ru.practicum.ewm.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = DescriptionValidator.class)
public @interface Description {
    String message() default "{Размер description должен находиться от 20 до 7000.}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
