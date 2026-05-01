package ru.equalizationofgeodeticnetworks.validation;

import ru.equalizationofgeodeticnetworks.persistence.dto.PointNamesContainer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class UniquePointNamesValidator implements ConstraintValidator<UniquePointNames, PointNamesContainer> {
    @Override
    public boolean isValid(PointNamesContainer container, ConstraintValidatorContext context) {
        Set<String> names = new HashSet<>();
        for (var fp : container.getFixedPoints()) {
            if (!names.add(fp.getName())) return false;
        }
        for (var up : container.getUnknownPoints()) {
            if (!names.add(up.getName())) return false;
        }
        return true;
    }
}
