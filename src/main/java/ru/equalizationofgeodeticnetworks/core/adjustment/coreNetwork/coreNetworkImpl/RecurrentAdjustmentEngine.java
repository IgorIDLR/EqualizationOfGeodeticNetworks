package ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl;

import lombok.extern.slf4j.Slf4j;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.AdjustmentEngine;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class RecurrentAdjustmentEngine implements AdjustmentEngine {
    private double[][] newQ;

    @Override
    public void adjust(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter) {
        int n = X.length;
        double[] deriv = new double[n];
        double[] AQ = new double[n];
        double[] K = new double[n];
        if (newQ == null || newQ.length != n) newQ = new double[n][n];

        boolean converged = false;
        int iter = 0;
        while (!converged && iter < maxIter) {
            iter++;
            double[] Xold = X.clone();
            for (Measurement m : measurements) update(X, Q, m, deriv, AQ, K);
            double maxDelta = 0.0;
            for (int i = 0; i < n; i++) maxDelta = Math.max(maxDelta, Math.abs(X[i] - Xold[i]));
            log.info("Рекуррентный проход {}: max поправка = {} м", iter, maxDelta);
            if (maxDelta < eps) converged = true;
        }
        if (!converged) log.warn("Рекуррентный процесс не сошёлся за {} проходов", maxIter);
    }

    private void update(double[] X, double[][] Q, Measurement m,
                        double[] deriv, double[] AQ, double[] K) {
        Arrays.fill(deriv, 0.0);
        m.derivatives(X, deriv);
        double expected = m.expected(X);
        double residual = m.residual(expected);
        double weight = m.getWeight();
        int n = X.length;

        for (int j = 0; j < n; j++) {
            double sum = 0.0;
            for (int k = 0; k < n; k++) sum += Q[k][j] * deriv[k];
            AQ[j] = sum;
        }
        double AQA = 0.0;
        for (int i = 0; i < n; i++) AQA += deriv[i] * AQ[i];
        double N = 1.0 / weight + AQA;

        for (int i = 0; i < n; i++) K[i] = AQ[i] / N;
        for (int i = 0; i < n; i++) X[i] += K[i] * residual;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) newQ[i][j] = Q[i][j] - K[i] * AQ[j];
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                double avg = (newQ[i][j] + newQ[j][i]) * 0.5;
                Q[i][j] = avg;
                Q[j][i] = avg;
            }
            Q[i][i] = newQ[i][i];
        }
    }
}