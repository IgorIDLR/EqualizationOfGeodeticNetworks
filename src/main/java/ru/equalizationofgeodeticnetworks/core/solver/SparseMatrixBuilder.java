package ru.equalizationofgeodeticnetworks.core.solver;

import org.ejml.data.DMatrixSparseCSC;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.sparse.csc.CommonOps_DSCC;
import org.springframework.stereotype.Component;

@Component
public class SparseMatrixBuilder {
    private DMatrixSparseTriplet triplet;
    private int rows, cols;

    public SparseMatrixBuilder(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.triplet = new DMatrixSparseTriplet(rows, cols, 1000);
    }

    public void addElement(int row, int col, double value) {
        triplet.addItem(row, col, value);
    }

    public DMatrixSparseCSC buildSparseA() {
        DMatrixSparseCSC sparse = new DMatrixSparseCSC(rows, cols, triplet.nz_length);
        triplet.sortIndices(1);
        triplet.createSortedFormat(sparse, 1);
        return sparse;
    }

    public DMatrixSparseCSC buildNormalMatrix() {
        DMatrixSparseCSC A = buildSparseA();
        DMatrixSparseCSC AT = new DMatrixSparseCSC(A.numCols, A.numRows, A.nz_length);
        CommonOps_DSCC.transpose(A, AT, null);
        DMatrixSparseCSC R = new DMatrixSparseCSC(AT.numRows, A.numCols, AT.nz_length + A.nz_length);
        CommonOps_DSCC.mult(AT, A, R);
        return R;
    }
}
