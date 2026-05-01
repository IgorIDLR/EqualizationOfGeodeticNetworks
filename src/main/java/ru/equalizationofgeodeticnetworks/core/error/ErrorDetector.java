package ru.equalizationofgeodeticnetworks.core.error;

import ru.equalizationofgeodeticnetworks.model.GrossError;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import java.util.List;

public interface ErrorDetector {
    List<GrossError> detect(List<Measurement> measurements, double[] X, double[][] Q, double t, double aprioriSigma);
}
