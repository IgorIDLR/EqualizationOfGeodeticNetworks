package ru.equalizationofgeodeticnetworks.core.adjustment.freeNetwork;

import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

import java.util.List;

public interface FreeNetworkAdjustment {
    void adjustFree(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter);
    enum FixationType { FIX_ONE_POINT, FIX_CENTROID, FIX_TWO_POINTS }
}
