package ru.equalizationofgeodeticnetworks.measurement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * {@code Measurement} - абстрактный класс для хранения сырых данных.
 *
 */
@AllArgsConstructor
@NoArgsConstructor
public abstract class Measurement {

    @Getter protected String name;
    @Getter protected double observed;
    @Getter protected double weight;

    public abstract double expected(double[] X);
    public abstract void derivatives(double[] X, double[] deriv);

    public double residual(double expected) { return observed - expected; }
}
