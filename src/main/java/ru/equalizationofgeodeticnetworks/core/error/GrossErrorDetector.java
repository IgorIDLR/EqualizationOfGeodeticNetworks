package ru.equalizationofgeodeticnetworks.core.error;

import ru.equalizationofgeodeticnetworks.model.GrossError;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GrossErrorDetector implements ErrorDetector {

    @Override
    public List<GrossError> detect(List<Measurement> measurements, double[] X, double[][] Q, double t, double aprioriSigma) {
        List<GrossError> errors = new ArrayList<>();
        int n = X.length;
        double[][] currentQ = new double[n][n];
        for (int i = 0; i < n; i++) currentQ[i][i] = 1e10;

        for (Measurement m : measurements) {
            double[] deriv = new double[n];
            m.derivatives(X, deriv);
            double[] Z = new double[n];
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    Z[j] += currentQ[j][k] * deriv[k];
                }
            }
            double AQA = 0.0;
            for (int j = 0; j < n; j++) AQA += deriv[j] * Z[j];
            double N = 1.0 / m.getWeight() + AQA;
            double expected = m.expected(X);
            double residual = m.residual(expected);
            double limit = t * aprioriSigma * Math.sqrt(N);
            if (Math.abs(residual) > limit) {
                errors.add(new GrossError(m.getName(), residual, limit));
            }
            double[] K = new double[n];
            for (int j = 0; j < n; j++) K[j] = Z[j] / N;
            double[][] newQ = new double[n][n];
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    newQ[j][k] = currentQ[j][k] - K[j] * Z[k];
                }
            }
            currentQ = newQ;
        }
        return errors;
    }
}
