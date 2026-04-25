package ru.equalizationofgeodeticnetworks.adjustment;

import ru.equalizationofgeodeticnetworks.measurement.Measurement;

import java.util.List;

@FunctionalInterface
public interface AdjustmentMethod {
    /**
     * Основной метод выполняющий уравнивание.
     *
     * @param params       начальные параметры
     * @param measurements список измерений
     * @param covar        начальная ковариационная матрица
     * @param eps          порог сходимости
     * @param maxIter      максимальное число итераций
     */
    public void adjust(double[] params, double[][] covar, List<Measurement> measurements, double eps, int maxIter);
}
