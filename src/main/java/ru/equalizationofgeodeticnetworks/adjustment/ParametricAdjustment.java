package ru.equalizationofgeodeticnetworks.adjustment;

import ru.equalizationofgeodeticnetworks.logger.Logging;
import ru.equalizationofgeodeticnetworks.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.utils.matrix.GaussianEliminationSolver;
import ru.equalizationofgeodeticnetworks.utils.matrix.LinearSystemSolver;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Параметрическое уравнивание (итерационный метод наименьших квадратов).
 * Реализует интерфейс AdjustmentMethod.
 * @param LinearSystemSolver solver - имплементатор стратегии вычисления матриц.
 * {@code GaussianEliminationSolver} является методом по умолчанию.
 */
public class ParametricAdjustment implements AdjustmentMethod {

    private static final Logger LOG = Logging.getLogger(ParametricAdjustment.class);
    private final LinearSystemSolver solver;

    public ParametricAdjustment() {
        this.solver = new GaussianEliminationSolver();
    }

    public ParametricAdjustment(LinearSystemSolver solver) {
        this.solver = solver;
    }

    @Override
    public void adjust(double[] params, double[][] covar, List<Measurement> measurements,
                       double eps, int maxIter) {
        int n = params.length;
        int m = measurements.size();

        boolean converged = false;
        int iter = 0;
        while (!converged && iter < maxIter) {
            iter++;
            double[] paramsOld = params.clone();

            double[][] A = new double[m][n];
            double[] w = new double[m];
            double[] P = new double[m];

            for (int i = 0; i < m; i++) {
                Measurement meas = measurements.get(i);
                double exp = meas.expected(params);
                w[i] = meas.residual(exp);
                P[i] = meas.getWeight();
                double[] deriv = new double[n];
                meas.derivatives(params, deriv);
                System.arraycopy(deriv, 0, A[i], 0, n);
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

            double[] delta = solver.solve(N, b);

            for (int i = 0; i < n; i++) {
                params[i] += delta[i];
            }

            double maxDelta = 0.0;
            for (double d : delta) {
                if (Math.abs(d) > maxDelta) maxDelta = Math.abs(d);
            }

            Logging.log(LOG, Level.INFO, String.format(Locale.US,
                    "Параметрический проход %d: max поправка = %.6f м", iter, maxDelta));

            if (maxDelta < eps) converged = true;
        }
        if (!converged) {
            Logging.log(LOG, Level.WARNING, String.format(
                    "Параметрический процесс не сошёлся за %d проходов", maxIter));
        }

        double[][] N = new double[n][n];
        for (int i = 0; i < m; i++) {
            double pi = measurements.get(i).getWeight();
            double[] deriv = new double[n];
            measurements.get(i).derivatives(params, deriv);
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    N[j][k] += pi * deriv[j] * deriv[k];
                }
            }
        }
        double[][] invN = solver.invert(N);
        for (int i = 0; i < n; i++) {
            System.arraycopy(invN[i], 0, covar[i], 0, n);
        }
    }
}
