package ru.equalizationofgeodeticnetworks.model.measurement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Measurement {
    protected final String name;
    protected final double observed;
    protected double weight;

    public Measurement(String name, double observed, double weight) {
        this.name = name;
        this.observed = observed;
        this.weight = weight;
    }

    public abstract double expected(double[] X);
    public abstract void derivatives(double[] X, double[] deriv);
    public double residual(double expected) {
        return observed - expected;
    }
}
