package ru.equalizationofgeodeticnetworks.core.reliability;

import ru.equalizationofgeodeticnetworks.config.ReliabilityProperties;
import ru.equalizationofgeodeticnetworks.model.*;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.utils.matrix.MatrixUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReliabilityAnalyzer {

    private final ReliabilityProperties props;

    public ReliabilityResult analyze(double[] X, double[][] Q, List<Measurement> measurements) {
        int n = X.length;
        int m = measurements.size();

        double[][] A = new double[m][n];
        double[] P = new double[m];
        for (int i = 0; i < m; i++) {
            Measurement mm = measurements.get(i);
            mm.derivatives(X, A[i]);
            P[i] = mm.getWeight();
        }

        double[][] AQ = MatrixUtils.multiply(A, Q);
        double[] redundancy = new double[m];
        for (int i = 0; i < m; i++) {
            double aQa = 0;
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    aQa += A[i][j] * Q[j][k] * A[i][k];
            redundancy[i] = 1.0 - P[i] * aQa;
        }

        double lambda;
        if (props.isLambdaCalculated()) {
            double chi1 = new ChiSquaredDistribution(1).inverseCumulativeProbability(1 - props.getAlpha());
            double chi2 = new ChiSquaredDistribution(1).inverseCumulativeProbability(props.getBeta());
            lambda = chi1 + chi2;
        } else {
            lambda = props.getLambdaFixed();
        }
        double s0 = computeUnitWeightError(measurements, X);

        List<Double> mdb = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            double aQa = 0;
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    aQa += A[i][j] * Q[j][k] * A[i][k];
            double mdbVal = s0 * Math.sqrt(lambda / (P[i] * aQa * (1 - redundancy[i])));
            mdb.add(mdbVal);
        }
        return new ReliabilityResult(redundancy, mdb, s0);
    }

    private double computeUnitWeightError(List<Measurement> measurements, double[] X) {
        int n = measurements.size();
        int t = X.length;
        if (n <= t) return 0;
        double vPv = 0;
        for (Measurement m : measurements) {
            double exp = m.expected(X);
            double res = m.residual(exp);
            vPv += res * res * m.getWeight();
        }
        return Math.sqrt(vPv / (n - t));
    }
}
