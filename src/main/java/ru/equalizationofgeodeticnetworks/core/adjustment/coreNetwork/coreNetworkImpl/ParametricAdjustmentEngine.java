package ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl;

import lombok.extern.slf4j.Slf4j;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.AdjustmentEngine;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.utils.matrix.MatrixUtils;

import java.util.List;

@Slf4j
public class ParametricAdjustmentEngine implements AdjustmentEngine {

    @Override
    public void adjust(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter) {
        int n = X.length;
        int m = measurements.size();

        boolean converged = false;
        int iter = 0;
        while (!converged && iter < maxIter) {
            iter++;
            double[] Xold = X.clone();

            double[][] A = new double[m][n];
            double[] w = new double[m];
            double[] P = new double[m];
            for (int i = 0; i < m; i++) {
                Measurement mm = measurements.get(i);
                double exp = mm.expected(X);
                w[i] = mm.residual(exp);
                P[i] = mm.getWeight();
                mm.derivatives(X, A[i]);
            }

            double[][] N = new double[n][n];
            double[] b = new double[n];
            for (int i = 0; i < m; i++) {
                double pi = P[i];
                for (int j = 0; j < n; j++) {
                    b[j] += pi * A[i][j] * w[i];
                    for (int k = 0; k < n; k++) {
                        N[j][k] += pi * A[i][j] * A[i][k];
                    }
                }
            }

            double[] delta = MatrixUtils.solveLinearSystem(N, b);
            for (int i = 0; i < n; i++) X[i] += delta[i];

            double maxDelta = 0.0;
            for (double d : delta) maxDelta = Math.max(maxDelta, Math.abs(d));
            log.info("Параметрический проход {}: max поправка = {} м", iter, maxDelta);
            if (maxDelta < eps) converged = true;
        }

        double[][] N = new double[n][n];
        for (int i = 0; i < m; i++) {
            double pi = measurements.get(i).getWeight();
            double[] deriv = new double[n];
            measurements.get(i).derivatives(X, deriv);
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    N[j][k] += pi * deriv[j] * deriv[k];
                }
            }
        }
        double[][] invN = MatrixUtils.invertMatrix(N);
        for (int i = 0; i < n; i++) System.arraycopy(invN[i], 0, Q[i], 0, n);
    }
}
