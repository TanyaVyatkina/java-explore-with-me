package ru.practicum.ewm.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = AnnotationValidator.class)
public @interface Annotation {
    String message() default "{Размер annotation должен находиться от 20 до 2000.}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}