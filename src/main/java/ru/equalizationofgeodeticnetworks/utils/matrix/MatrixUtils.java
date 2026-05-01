package ru.equalizationofgeodeticnetworks.utils.matrix;

import lombok.experimental.UtilityClass;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.interfaces.linsol.LinearSolverDense;

@UtilityClass
public class MatrixUtils {

    public static double[] solveLinearSystem(double[][] A, double[] b) {
        DMatrixRMaj matrix = new DMatrixRMaj(A);
        DMatrixRMaj vector = new DMatrixRMaj(b.length, 1, true, b);
        LinearSolverDense<DMatrixRMaj> solver = LinearSolverFactory_DDRM.linear(matrix.getNumRows());
        if (!solver.setA(matrix)) throw new RuntimeException("Матрица вырождена");
        DMatrixRMaj x = new DMatrixRMaj(matrix.getNumCols(), 1);
        solver.solve(vector, x);
        return x.getData();
    }

    public static double[][] invertMatrix(double[][] A) {
        DMatrixRMaj matrix = new DMatrixRMaj(A);
        DMatrixRMaj inv = new DMatrixRMaj(matrix.getNumRows(), matrix.getNumCols());
        if (!CommonOps_DDRM.invert(matrix, inv)) throw new RuntimeException("Матрица вырождена");
        return inv.getData();
    }

    public static double[] multiply(double[][] A, double[] b) {
        DMatrixRMaj mat = new DMatrixRMaj(A);
        DMatrixRMaj vec = new DMatrixRMaj(b.length, 1, true, b);
        DMatrixRMaj res = new DMatrixRMaj(mat.getNumRows(), 1);
        CommonOps_DDRM.mult(mat, vec, res);
        return res.getData();
    }

    public static double[][] multiply(double[][] A, double[][] B) {
        DMatrixRMaj matA = new DMatrixRMaj(A);
        DMatrixRMaj matB = new DMatrixRMaj(B);
        DMatrixRMaj res = new DMatrixRMaj(matA.getNumRows(), matB.getNumCols());
        CommonOps_DDRM.mult(matA, matB, res);
        return res.getData();
    }

    public static void multiply(double[] x, double[][] A, double[] y) {
        DMatrixRMaj vec = new DMatrixRMaj(x.length, 1, true, x);
        DMatrixRMaj mat = new DMatrixRMaj(A);
        DMatrixRMaj res = new DMatrixRMaj(A.length, 1);
        CommonOps_DDRM.mult(mat, vec, res);
        System.arraycopy(res.getData(), 0, y, 0, y.length);
    }
}
