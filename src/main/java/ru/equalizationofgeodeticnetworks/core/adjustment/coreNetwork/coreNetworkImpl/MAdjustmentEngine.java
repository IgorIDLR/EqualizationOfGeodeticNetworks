package ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.equalizationofgeodeticnetworks.configuration.properties.RobustAdjustmentProperties;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.AdjustmentEngine;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MAdjustmentEngine implements AdjustmentEngine {
    private final String method;
    private final RobustAdjustmentProperties robustProps;

    @Override
    public void adjust(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter) {
        int n = X.length;
        double[] deriv = new double[n];
        double[] AQ = new double[n];
        double[] K = new double[n];
        double[][] newQ = new double[n][n];

        boolean converged = false;
        int iter = 0;
        double[] oldWeights = new double[measurements.size()];
        for (int i = 0; i < measurements.size(); i++) oldWeights[i] = measurements.get(i).getWeight();

        while (!converged && iter < maxIter) {
            iter++;
            double[] Xold = X.clone();

            for (Measurement m : measurements) update(X, Q, m, deriv, AQ, K, newQ);

            for (int i = 0; i < measurements.size(); i++) {
                Measurement m = measurements.get(i);
                double expected = m.expected(X);
                double residual = m.residual(expected);
                double sigma = Math.sqrt(1.0 / m.getWeight());
                double u = Math.abs(residual / sigma);
                double newWeight = m.getWeight();

                if ("HUBER".equalsIgnoreCase(method)) {
                    double c = robustProps.getHuberC();
                    if (u > c) newWeight = m.getWeight() * c / u;
                } else if ("TUKEY".equalsIgnoreCase(method)) {
                    double c = robustProps.getTukeyC();
                    if (u <= c) newWeight = m.getWeight() * (1 - Math.pow(u/c, 2)) * (1 - Math.pow(u/c, 2));
                    else newWeight = 0;
                }
                m.setWeight(newWeight);
            }

            double maxWeightChange = 0.0;
            for (int i = 0; i < measurements.size(); i++) {
                double change = Math.abs(measurements.get(i).getWeight() - oldWeights[i]);
                if (change > maxWeightChange) maxWeightChange = change;
                oldWeights[i] = measurements.get(i).getWeight();
            }
            double maxDelta = 0.0;
            for (int i = 0; i < n; i++) maxDelta = Math.max(maxDelta, Math.abs(X[i] - Xold[i]));
            log.info("M-оценка {} проход {}: max поправка = {}, max изменение весов = {}", method, iter, maxDelta, maxWeightChange);
            if (maxDelta < eps && maxWeightChange < eps) converged = true;
        }
    }

    private void update(double[] X, double[][] Q, Measurement m,
                        double[] deriv, double[] AQ, double[] K, double[][] newQ) {
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