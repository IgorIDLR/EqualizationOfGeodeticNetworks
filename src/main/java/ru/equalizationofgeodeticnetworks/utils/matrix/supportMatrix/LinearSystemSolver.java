package ru.equalizationofgeodeticnetworks.utils.matrix.supportMatrix;

public interface LinearSystemSolver {
    double[] solve(double[][] A, double[] b);
    double[][] invert(double[][] A);
}
