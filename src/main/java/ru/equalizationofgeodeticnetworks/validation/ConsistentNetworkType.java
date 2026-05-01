package ru.equalizationofgeodeticnetworks.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConsistentNetworkTypeValidator.class)
public @interface ConsistentNetworkType {
    String message() default "Данные не соответствуют типу сети";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
