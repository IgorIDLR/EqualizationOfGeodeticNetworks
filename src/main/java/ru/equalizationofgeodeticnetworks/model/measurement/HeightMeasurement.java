package ru.equalizationofgeodeticnetworks.model.measurement;

public class HeightMeasurement extends Measurement {

    private final int idxFrom, idxTo;
    private final boolean fromFixed, toFixed;
    private final double fixedFrom, fixedTo;

    public HeightMeasurement(String name, double observed, double weight,
                             int idxFrom, int idxTo,
                             double fixedFrom, double fixedTo,
                             boolean fromFixed, boolean toFixed) {
        super(name, observed, weight);
        this.idxFrom = idxFrom;
        this.idxTo = idxTo;
        this.fixedFrom = fixedFrom;
        this.fixedTo = fixedTo;
        this.fromFixed = fromFixed;
        this.toFixed = toFixed;
    }

    @Override
    public double expected(double[] X) {
        double hFrom = fromFixed ? fixedFrom : X[idxFrom];
        double hTo   = toFixed   ? fixedTo   : X[idxTo];
        return hTo - hFrom;
    }

    @Override
    public void derivatives(double[] X, double[] deriv) {
        if (!fromFixed) deriv[idxFrom] = -1.0;
        if (!toFixed)   deriv[idxTo]   = +1.0;
    }
}
