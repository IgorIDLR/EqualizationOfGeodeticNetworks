package ru.equalizationofgeodeticnetworks.adjustment;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Класс реализующий {@link AdjustmentMethod}. Является основным классом для прогона и нарищивания матрицы обратных весов <b>Q</b>.
 *
 */
public class RecurrentAdjustment implements AdjustmentMethod {

    private static final Logger LOG = Logging.getLogger(RecurrentAdjustment.class);

    @Override
    public void adjust(double[] params, double[][] covar, List<Measurement> measurements,
                       double eps, int maxIter) {
        int n = params.length;
        double[] deriv = new double[n];
        double[] AQ = new double[n];
        double[] K = new double[n];

        boolean converged = false;
        int iter = 0;
        while (!converged && iter < maxIter) {
            iter++;
            double[] paramsOld = params.clone();

            for (Measurement m : measurements) {
                update(params, covar, m, deriv, AQ, K);
            }

            double maxDelta = 0.0;
            for (int i = 0; i < n; i++) {
                double delta = Math.abs(params[i] - paramsOld[i]);
                if (delta > maxDelta) maxDelta = delta;
            }

            Logging.log(LOG, Level.INFO, String.format(Locale.US,
                    "Рекуррентный проход %d: max поправка = %.6f м", iter, maxDelta));

            if (maxDelta < eps) converged = true;
        }
        if (!converged) {
            Logging.log(LOG, Level.WARNING, String.format(
                    "Рекуррентный процесс не сошёлся за %d проходов", maxIter));
        }
    }

    private void update(double[] params, double[][] covar, Measurement m,
                        double[] deriv, double[] AQ, double[] K) {
        for (int i = 0; i < deriv.length; i++) deriv[i] = 0.0;
        m.derivatives(params, deriv);
        double expected = m.expected(params);
        double residual = m.residual(expected);
        double weight = m.getWeight();

        int n = params.length;

        for (int j = 0; j < n; j++) {
            double sum = 0.0;
            for (int k = 0; k < n; k++) {
                sum += covar[k][j] * deriv[k];
            }
            AQ[j] = sum;
        }

        double AQA = 0.0;
        for (int i = 0; i < n; i++) {
            AQA += deriv[i] * AQ[i];
        }
        double S = 1.0 / weight + AQA;

        for (int i = 0; i < n; i++) {
            K[i] = AQ[i] / S;
        }

        for (int i = 0; i < n; i++) {
            params[i] += K[i] * residual;
        }

        double[][] newQ = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                newQ[i][j] = covar[i][j] - K[i] * AQ[j];
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                double avg = (newQ[i][j] + newQ[j][i]) * 0.5;
                covar[i][j] = avg;
                covar[j][i] = avg;
            }
            covar[i][i] = newQ[i][i];
        }
    }
}
