package ru.equalizationofgeodeticnetworks.utils.matrix;

/**
 * Интерфейс для решения систем линейных уравнений.
 */
public interface LinearSystemSolver {

    /**
     * Решает систему N * x = b.
     * @param N матрица коэффициентов (n x n)
     * @param b вектор правой части (n)
     * @return решение x
     */
    double[] solve(double[][] N, double[] b);

    /**
     * Обращает матрицу.
     * @param A исходная матрица
     * @return обратная матрица
     */
    double[][] invert(double[][] A);
}
