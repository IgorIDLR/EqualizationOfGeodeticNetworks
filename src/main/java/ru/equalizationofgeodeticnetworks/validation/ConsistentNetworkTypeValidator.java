package ru.equalizationofgeodeticnetworks.validation;

import ru.equalizationofgeodeticnetworks.persistence.dto.AdjustmentRequest;
import ru.equalizationofgeodeticnetworks.persistence.dto.FixedPointDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConsistentNetworkTypeValidator implements ConstraintValidator<ConsistentNetworkType, AdjustmentRequest> {
    @Override
    public boolean isValid(AdjustmentRequest req, ConstraintValidatorContext context) {
        if (req.getNetworkType() == AdjustmentRequest.NetworkType.POLYGONOMETRY) {
            for (FixedPointDto fp : req.getFixedPoints()) {
                if (fp.getX() == null || fp.getY() == null) return false;
            }
        } else if (req.getNetworkType() == AdjustmentRequest.NetworkType.LEVELING) {
            for (FixedPointDto fp : req.getFixedPoints()) {
                if (fp.getHeight() == null) return false;
            }
        }
        return true;
    }
}
