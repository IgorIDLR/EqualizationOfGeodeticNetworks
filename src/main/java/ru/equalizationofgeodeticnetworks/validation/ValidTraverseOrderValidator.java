package ru.equalizationofgeodeticnetworks.validation;

import ru.equalizationofgeodeticnetworks.persistence.dto.AngleMeasurementDto;
import ru.equalizationofgeodeticnetworks.persistence.dto.DistanceMeasurementDto;
import ru.equalizationofgeodeticnetworks.persistence.dto.MeasurementDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidTraverseOrderValidator implements ConstraintValidator<ValidTraverseOrder, List<MeasurementDto>> {
    @Override
    public boolean isValid(List<MeasurementDto> measurements, ConstraintValidatorContext context) {
        boolean expectingAngle = true;
        for (MeasurementDto m : measurements) {
            if (expectingAngle && !(m instanceof AngleMeasurementDto)) return false;
            if (!expectingAngle && !(m instanceof DistanceMeasurementDto)) return false;
            expectingAngle = !expectingAngle;
        }
        return true;
    }
}
