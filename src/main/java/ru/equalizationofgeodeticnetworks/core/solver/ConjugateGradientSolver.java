package ru.equalizationofgeodeticnetworks.core.solver;

import org.ejml.data.DMatrixSparseCSC;
import org.ejml.sparse.csc.CommonOps_DSCC;
import org.springframework.stereotype.Component;

@Component
public class ConjugateGradientSolver {
    private final int maxIter;
    private final double tolerance;

    public ConjugateGradientSolver(int maxIter, double tolerance) {
        this.maxIter = maxIter;
        this.tolerance = tolerance;
    }

    public double[] solve(DMatrixSparseCSC A, double[] b) {
        int n = A.numRows;
        double[] x = new double[n];
        double[] r = new double[n];
        double[] p = new double[n];
        double[] Ap = new double[n];

        CommonOps_DSCC.mult(A, x, Ap);
        for (int i = 0; i < n; i++) r[i] = b[i] - Ap[i];
        System.arraycopy(r, 0, p, 0, n);
        double rsold = dot(r, r);
        double toleranceSq = tolerance * tolerance;

        for (int iter = 0; iter < maxIter; iter++) {
            CommonOps_DSCC.mult(A, p, Ap);
            double alpha = rsold / dot(p, Ap);
            for (int i = 0; i < n; i++) {
                x[i] += alpha * p[i];
                r[i] -= alpha * Ap[i];
            }
            double rsnew = dot(r, r);
            if (rsnew < toleranceSq) break;
            for (int i = 0; i < n; i++) {
                p[i] = r[i] + (rsnew / rsold) * p[i];
            }
            rsold = rsnew;
        }
        return x;
    }

    private double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }
}
