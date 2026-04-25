package ru.equalizationofgeodeticnetworks.measurement;

import java.util.Map;

public class HeightMeasurement extends Measurement {

    private int idxFrom, idxTo;
    private double fixedFrom, fixedTo;
    private boolean fromFixed, toFixed;

    public HeightMeasurement(String name, double observed, double weight,
                             int idxFrom, int idxTo,
                             Map<String, Double> fixedHeights) {
        super(name, observed, weight);
        this.idxFrom = idxFrom;
        this.idxTo = idxTo;
        if (idxFrom < 0) {
            fromFixed = true;
            fixedFrom = fixedHeights.get(name.split("-")[0]); // упрощение
        }
        if (idxTo < 0) {
            toFixed = true;
            fixedTo = fixedHeights.get(name.split("-")[1]);
        }
    }

    @Override
    public double expected(double[] X) {
        double hFrom = fromFixed ? fixedFrom : X[idxFrom];
        double hTo = toFixed ? fixedTo : X[idxTo];
        return hTo - hFrom;
    }

    @Override
    public void derivatives(double[] X, double[] deriv) {
        if (!fromFixed) {
            deriv[idxFrom] = -1.0;
        }
        if (!toFixed) {
            deriv[idxTo] = 1.0;
        }
    }
}

