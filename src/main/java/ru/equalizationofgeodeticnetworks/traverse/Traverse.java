package ru.equalizationofgeodeticnetworks.traverse;

import ru.equalizationofgeodeticnetworks.adjustment.AdjustmentMethod;
import ru.equalizationofgeodeticnetworks.measurement.Measurement;

import java.util.List;

public abstract class Traverse {
    protected final List<Measurement> measurements;
    protected double[] params;          // параметры (координаты или отметки)
    protected double[][] covar;          // ковариационная матрица
    protected AdjustmentMethod method;

    public Traverse(AdjustmentMethod method) {
        this.method = method;
        this.measurements = new java.util.ArrayList<>();
    }

    public void setAdjustmentMethod(AdjustmentMethod method) {
        this.method = method;
    }

    public void addMeasurement(Measurement m) {
        measurements.add(m);
    }

    public abstract void buildInitialApprox(); // построение начальных приближений

    public void solve(double eps, int maxIter) {
        buildInitialApprox();
        int n = params.length;
        covar = new double[n][n];
        for (int i = 0; i < n; i++) covar[i][i] = 1e10;
        method.adjust(params, covar, measurements, eps, maxIter);
    }

    public double[] getParams() { return params; }
    public double[][] getCovar() { return covar; }
    public List<Measurement> getMeasurements() { return measurements; }
}
