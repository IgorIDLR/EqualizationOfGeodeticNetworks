package ru.equalizationofgeodeticnetworks.core.adjustment.freeNetwork.freeNetworkImpl;

import lombok.extern.slf4j.Slf4j;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import ru.equalizationofgeodeticnetworks.core.adjustment.freeNetwork.FreeNetworkAdjustment;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.utils.matrix.MatrixUtils;

import java.util.List;

@Slf4j
public class PseudoInverseFreeNetworkSolver implements FreeNetworkAdjustment {
    private FixationType fixationType = FixationType.FIX_CENTROID;

    public void setFixationType(FixationType type) {
        this.fixationType = type;
    }

    @Override
    public void adjustFree(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter) {
        int n = X.length;
        int m = measurements.size();
        double[][] R = new double[n][n];
        double[] b = new double[n];
        boolean converged = false;
        int iter = 0;

        while (!converged && iter < maxIter) {
            iter++;
            double[] Xold = X.clone();
            for (int i = 0; i < n; i++) {
                b[i] = 0.0;
                for (int j = 0; j < n; j++) R[i][j] = 0.0;
            }

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

            for (int i = 0; i < m; i++) {
                double pi = P[i];
                for (int j = 0; j < n; j++) {
                    b[j] += pi * A[i][j] * w[i];
                    for (int k = 0; k < n; k++) {
                        R[j][k] += pi * A[i][j] * A[i][k];
                    }
                }
            }

            double[][] Rinv = pseudoInverse(R);
            double[] delta = MatrixUtils.multiply(Rinv, b);
            for (int i = 0; i < n; i++) delta[i] = -delta[i];
            for (int i = 0; i < n; i++) X[i] += delta[i];

            double maxDelta = 0.0;
            for (double d : delta) maxDelta = Math.max(maxDelta, Math.abs(d));
            log.info("Свободное уравнивание (SVD), итерация {}: max поправка = {}", iter, maxDelta);
            if (maxDelta < eps) converged = true;
        }

        double[][] Qinv = pseudoInverse(R);
        for (int i = 0; i < n; i++) System.arraycopy(Qinv[i], 0, Q[i], 0, n);
    }

    private double[][] pseudoInverse(double[][] R) {
        DMatrixRMaj matrix = new DMatrixRMaj(R);
        SingularValueDecomposition<DMatrixRMaj> svd = DecompositionFactory_DDRM.svd(matrix.numRows, matrix.numCols, true, true, false);
        if (!svd.decompose(matrix)) throw new RuntimeException("SVD decomposition failed");
        DMatrixRMaj U = svd.getU(null, false);
        DMatrixRMaj V = svd.getV(null, false);
        int n = matrix.numCols, m = matrix.numRows;
        int rank = svd.numberOfSingularValues();
        double[] singularValues = new double[rank];
        for (int i = 0; i < rank; i++) {
            singularValues[i] = svd.getSingularValue(i);
        }
        double[][] Splus = new double[n][m];
        double maxSingular = singularValues[0];
        double tolerance = maxSingular * 1e-12;
        for (int i = 0; i < rank; i++) {
            if (singularValues[i] > tolerance) {
                Splus[i][i] = 1.0 / singularValues[i];
            }
        }
        DMatrixRMaj SplusMat = new DMatrixRMaj(Splus);
        DMatrixRMaj Vt = new DMatrixRMaj(V.numCols, V.numRows);
        CommonOps_DDRM.transpose(V, Vt);
        DMatrixRMaj temp = new DMatrixRMaj(V.numCols, U.numRows);
        CommonOps_DDRM.mult(Vt, SplusMat, temp);
        DMatrixRMaj pinv = new DMatrixRMaj(temp.numRows, U.numCols);
        CommonOps_DDRM.mult(temp, U, pinv);
        return pinv.getData();
    }
}