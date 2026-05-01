package ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork;

import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import java.util.List;

public interface AdjustmentEngine {
    void adjust(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter);
}
