package ru.equalizationofgeodeticnetworks.model.traverse;

import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

import java.util.ArrayList;
import java.util.List;

public abstract class Traverse {
    protected final List<Measurement> measurements = new ArrayList<>();
    protected final List<String> pointNames = new ArrayList<>();
    protected final List<Double> initialApprox = new ArrayList<>();
    protected boolean approxComputed = false;
    protected boolean isFreeNetwork = false;
    protected boolean isOpenTraverse = false;
    protected boolean hasConditions = false;

    public abstract void addFixedPoint(String name, double... values);
    public abstract void addUnknownPoint(String name);
    public abstract void computeInitialApprox();
    public void addMeasurement(Measurement m) { measurements.add(m); }

    public List<Measurement> getMeasurements() { return measurements; }
    public List<String> getPointNames() { return pointNames; }
    public List<Double> getInitialApprox() { return initialApprox; }
    public boolean isApproxComputed() { return approxComputed; }
    public boolean isFreeNetwork() { return isFreeNetwork; }
    public boolean isOpenTraverse() { return isOpenTraverse; }
    public boolean hasConditions() { return hasConditions; }

    protected void detectTraverseType() {}
}
