package ru.equalizationofgeodeticnetworks.utils.matrix;

import ru.equalizationofgeodeticnetworks.logger.Logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixUtils {

    private static final Logger LOG = Logging.getLogger(MatrixUtils.class);
    private static final double EPS = 1e-18;

    private MatrixUtils() {}

    public static double[] solveLinearSystem(double[][] N, double[] b) {
        int n = b.length;
        double[][] a = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(N[i], 0, a[i], 0, n);
            a[i][n] = b[i];
        }

        for (int col = 0; col < n; col++) {
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(a[row][col]) > Math.abs(a[maxRow][col]))
                    maxRow = row;
            }
            double[] temp = a[col];
            a[col] = a[maxRow];
            a[maxRow] = temp;

            double pivot = a[col][col];
            if (Math.abs(pivot) < EPS) {
                Logging.log(LOG, Level.SEVERE, "Матрица вырождена при решении СЛАУ");
                throw new RuntimeException("Матрица вырождена");
            }

            for (int j = col; j <= n; j++)
                a[col][j] /= pivot;

            for (int row = col + 1; row < n; row++) {
                double factor = a[row][col];
                for (int j = col; j <= n; j++)
                    a[row][j] -= factor * a[col][j];
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = a[i][n];
            for (int j = i + 1; j < n; j++)
                x[i] -= a[i][j] * x[j];
        }
        return x;
    }

    public static double[][] invertMatrix(double[][] A) {
        int n = A.length;
        double[][] aug = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n + i] = 1.0;
        }

        for (int col = 0; col < n; col++) {
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(aug[row][col]) > Math.abs(aug[maxRow][col]))
                    maxRow = row;
            }
            double[] temp = aug[col];
            aug[col] = aug[maxRow];
            aug[maxRow] = temp;

            double pivot = aug[col][col];
            if (Math.abs(pivot) < EPS) {
                Logging.log(LOG, Level.SEVERE, "Матрица вырождена при обращении");
                throw new RuntimeException("Матрица вырождена");
            }

            for (int j = 0; j < 2 * n; j++)
                aug[col][j] /= pivot;

            for (int row = 0; row < n; row++) {
                if (row != col) {
                    double factor = aug[row][col];
                    for (int j = 0; j < 2 * n; j++)
                        aug[row][j] -= factor * aug[col][j];
                }
            }
        }

        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++)
            System.arraycopy(aug[i], n, inv[i], 0, n);
        return inv;
    }
}
