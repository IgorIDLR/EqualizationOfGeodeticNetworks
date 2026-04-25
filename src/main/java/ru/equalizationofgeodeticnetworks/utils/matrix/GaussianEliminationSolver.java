package ru.equalizationofgeodeticnetworks.utils.matrix;

/**
 * Решение систем линейных уравнений методом Гаусса.
 * Использует утилиты MatrixUtils.
 */
public class GaussianEliminationSolver implements LinearSystemSolver {

    @Override
    public double[] solve(double[][] N, double[] b) {
        return MatrixUtils.solveLinearSystem(N, b);
    }

    @Override
    public double[][] invert(double[][] A) {
        return MatrixUtils.invertMatrix(A);
    }
}
