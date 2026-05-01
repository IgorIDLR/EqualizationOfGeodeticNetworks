package ru.equalizationofgeodeticnetworks.utils.matrix.supportMatrix;

import ru.equalizationofgeodeticnetworks.utils.matrix.MatrixUtils;

public class GaussianEliminationSolver implements LinearSystemSolver {
    @Override
    public double[] solve(double[][] A, double[] b) {
        return MatrixUtils.solveLinearSystem(A, b);
    }
    @Override
    public double[][] invert(double[][] A) {
        return MatrixUtils.invertMatrix(A);
    }
}
