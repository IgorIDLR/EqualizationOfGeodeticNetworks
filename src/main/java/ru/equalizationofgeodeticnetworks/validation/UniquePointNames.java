package ru.equalizationofgeodeticnetworks.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniquePointNamesValidator.class)
public @interface UniquePointNames {
    String message() default "Имена точек должны быть уникальны";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}