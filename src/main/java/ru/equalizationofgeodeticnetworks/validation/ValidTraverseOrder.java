package ru.equalizationofgeodeticnetworks.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTraverseOrderValidator.class)
public @interface ValidTraverseOrder {
    String message() default "Измерения должны быть в порядке: угол, расстояние, угол, расстояние...";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
