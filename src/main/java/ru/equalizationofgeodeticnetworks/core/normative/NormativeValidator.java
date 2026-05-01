package ru.equalizationofgeodeticnetworks.core.normative;

import ru.equalizationofgeodeticnetworks.core.normative.normativeValidatorImpl.GeodesyNormativeValidator;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import java.util.List;

public interface NormativeValidator {
    GeodesyNormativeValidator.NormativeResult validate(String networkType, String accuracyClass, double[] X, double[][] Q, List<Measurement> measurements);
}
