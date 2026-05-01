package ru.equalizationofgeodeticnetworks.core.solver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.configuration.properties.AdjustmentProperties;
import ru.equalizationofgeodeticnetworks.configuration.properties.SparseSolverProperties;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.utils.matrix.MatrixUtils;

import java.util.List;

@Slf4j
@Component
public class ParametricSparseSolver {

    private final AdjustmentProperties props;
    private final SparseSolverProperties sparseProps;
    private final ConjugateGradientSolver cg;
    private final SparseMatrixBuilder builder;

    public ParametricSparseSolver(AdjustmentProperties props,
                                  SparseSolverProperties sparseProps,
                                  ConjugateGradientSolver cg) {
        this.props = props;
        this.sparseProps = sparseProps;
        this.cg = cg;
        this.builder = new SparseMatrixBuilder(0, 0);
    }

    public void solve(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter) {
        int n = X.length;
        int m = measurements.size();
        boolean converged = false;
        int iter = 0;
        while (!converged && iter < maxIter) {
            iter++;
            double[] Xold = X.clone();

            // Построение разреженной A и правой части
            var localBuilder = new SparseMatrixBuilder(m, n);
            double[] b = new double[n];
            for (int i = 0; i < m; i++) {
                Measurement mm = measurements.get(i);
                double exp = mm.expected(X);
                double residual = mm.residual(exp);
                double w = residual * mm.getWeight();
                double[] deriv = new double[n];
                mm.derivatives(X, deriv);
                for (int j = 0; j < n; j++) {
                    if (Math.abs(deriv[j]) > 1e-12) {
                        localBuilder.addElement(i, j, deriv[j] * Math.sqrt(mm.getWeight()));
                    }
                    b[j] += deriv[j] * w;
                }
            }
            var A = localBuilder.buildSparseA();
            var R = localBuilder.buildNormalMatrix();

            double[] delta;
            if (n > sparseProps.getMaxNetworkSizeDense() && props.isUseSparse()) {
                delta = cg.solve(R, b);
                log.debug("Решено методом CG (n={})", n);
            } else {
                // fallback на плотный метод через нормальные уравнения
                double[][] N = new double[n][n];
                double[] bdense = new double[n];
                for (int i = 0; i < m; i++) {
                    Measurement mm = measurements.get(i);
                    double exp = mm.expected(X);
                    double residual = mm.residual(exp);
                    double weight = mm.getWeight();
                    double[] deriv = new double[n];
                    mm.derivatives(X, deriv);
                    for (int j = 0; j < n; j++) {
                        bdense[j] += weight * deriv[j] * residual;
                        for (int k = 0; k < n; k++) {
                            N[j][k] += weight * deriv[j] * deriv[k];
                        }
                    }
                }
                delta = MatrixUtils.solveLinearSystem(N, bdense);
            }
            for (int i = 0; i < n; i++) X[i] += delta[i];
            double maxDelta = 0.0;
            for (double d : delta) maxDelta = Math.max(maxDelta, Math.abs(d));
            log.info("Параметрический проход {}: max поправка = {}", iter, maxDelta);
            if (maxDelta < eps) converged = true;
        }
        // Оценка ковариационной матрицы (упрощённо)
        log.warn("Ковариационная матрица после разреженного решения не вычисляется");
    }
}
