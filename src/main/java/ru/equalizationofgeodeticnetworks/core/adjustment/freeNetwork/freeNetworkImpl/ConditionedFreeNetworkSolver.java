package ru.equalizationofgeodeticnetworks.core.adjustment.freeNetwork.freeNetworkImpl;

import lombok.extern.slf4j.Slf4j;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl.ParametricAdjustmentEngine;
import ru.equalizationofgeodeticnetworks.core.adjustment.freeNetwork.FreeNetworkAdjustment;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConditionedFreeNetworkSolver implements FreeNetworkAdjustment {
    private FixationType fixationType = FixationType.FIX_CENTROID;
    private static final double LARGE_WEIGHT = 1e10;

    public void setFixationType(FixationType type) { this.fixationType = type; }

    @Override
    public void adjustFree(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter) {
        log.info("Свободное уравнивание с фиксацией типа {}", fixationType);
        List<Measurement> extended = new ArrayList<>(measurements);
        int n = X.length, pointCount = n / 2;
        switch (fixationType) {
            case FIX_ONE_POINT:
                extended.add(new PseudoMeasurement("fix_x0", 0.0, LARGE_WEIGHT, 0));
                extended.add(new PseudoMeasurement("fix_y0", 0.0, LARGE_WEIGHT, 1));
                break;
            case FIX_CENTROID:
                double[] coeffX = new double[n];
                double[] coeffY = new double[n];
                for (int i = 0; i < pointCount; i++) {
                    coeffX[i*2] = 1.0 / pointCount;
                    coeffY[i*2+1] = 1.0 / pointCount;
                }
                extended.add(new CentroidMeasurement("centroid_x", 0.0, LARGE_WEIGHT, coeffX));
                extended.add(new CentroidMeasurement("centroid_y", 0.0, LARGE_WEIGHT, coeffY));
                break;
            case FIX_TWO_POINTS:
                if (pointCount >= 2) {
                    extended.add(new PseudoMeasurement("fix_x0", 0.0, LARGE_WEIGHT, 0));
                    extended.add(new PseudoMeasurement("fix_y0", 0.0, LARGE_WEIGHT, 1));
                    int lastX = (pointCount-1)*2;
                    int lastY = lastX+1;
                    extended.add(new PseudoMeasurement("fix_x_last", 0.0, LARGE_WEIGHT, lastX));
                    extended.add(new PseudoMeasurement("fix_y_last", 0.0, LARGE_WEIGHT, lastY));
                } else {
                    log.warn("Недостаточно точек, применяем FIX_CENTROID");
                    double[] coeffX2 = new double[n];
                    double[] coeffY2 = new double[n];
                    for (int i = 0; i < pointCount; i++) {
                        coeffX2[i*2] = 1.0 / pointCount;
                        coeffY2[i*2+1] = 1.0 / pointCount;
                    }
                    extended.add(new CentroidMeasurement("centroid_x", 0.0, LARGE_WEIGHT, coeffX2));
                    extended.add(new CentroidMeasurement("centroid_y", 0.0, LARGE_WEIGHT, coeffY2));
                }
                break;
        }
        ParametricAdjustmentEngine engine = new ParametricAdjustmentEngine();
        engine.adjust(X, Q, extended, eps, maxIter);
    }

    private static class PseudoMeasurement extends Measurement {
        private final int idx;
        public PseudoMeasurement(String name, double value, double weight, int idx) {
            super(name, value, weight);
            this.idx = idx;
        }
        @Override
        public double expected(double[] X) { return X[idx]; }
        @Override
        public void derivatives(double[] X, double[] deriv) { deriv[idx] = 1.0; }
        @Override
        public double residual(double expected) { return getObserved() - expected; }
    }

    private static class CentroidMeasurement extends Measurement {
        private final double[] coeff;
        public CentroidMeasurement(String name, double value, double weight, double[] coeff) {
            super(name, value, weight);
            this.coeff = coeff;
        }
        @Override
        public double expected(double[] X) {
            double sum = 0;
            for (int i = 0; i < coeff.length; i++) sum += coeff[i] * X[i];
            return sum;
        }
        @Override
        public void derivatives(double[] X, double[] deriv) {
            for (int i = 0; i < coeff.length; i++) deriv[i] = coeff[i];
        }
        @Override
        public double residual(double expected) { return getObserved() - expected; }
    }
}
