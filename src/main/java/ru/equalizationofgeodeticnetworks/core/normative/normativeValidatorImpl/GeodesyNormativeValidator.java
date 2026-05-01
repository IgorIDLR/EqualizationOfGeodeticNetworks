package ru.equalizationofgeodeticnetworks.core.normative.normativeValidatorImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.core.normative.NormativeValidator;
import ru.equalizationofgeodeticnetworks.model.measurement.DistanceMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class GeodesyNormativeValidator implements NormativeValidator {

    @Override
    public NormativeResult validate(String networkType, String accuracyClass, double[] X, double[][] Q, List<Measurement> measurements) {
        double s0 = computeUnitWeightError(measurements, X);
        double absError = computeAbsoluteError(X, Q);
        double relError = computeRelativeError(measurements, absError);
        boolean passes = false;
        String requirement = "";
        if ("POLYGONOMETRY".equalsIgnoreCase(networkType)) {
            if (accuracyClass.startsWith("1:")) {
                double limit = 1.0 / Double.parseDouble(accuracyClass.substring(2));
                passes = relError <= limit;
                requirement = "Относительная ошибка ≤ " + accuracyClass;
            } else {
                passes = absError <= 0.05;
                requirement = "Абсолютная ошибка ≤ 0.05 м";
            }
        } else if ("LEVELING".equalsIgnoreCase(networkType)) {
            double maxHeightError = computeMaxHeightError(X, Q);
            passes = maxHeightError <= 0.01;
            requirement = "СКО высоты ≤ 10 мм";
        }
        return new NormativeResult(passes, requirement, s0, absError, relError);
    }
    private double computeUnitWeightError(List<Measurement> measurements, double[] X) {
        int n = measurements.size(), t = X.length;
        if (n <= t) return 0;
        double vPv = 0;
        for (Measurement m : measurements) {
            double exp = m.expected(X);
            double res = m.residual(exp);
            vPv += res * res * m.getWeight();
        }
        return Math.sqrt(vPv / (n - t));
    }
    private double computeAbsoluteError(double[] X, double[][] Q) {
        double maxPos = 0;
        for (int i = 0; i < X.length; i += 2) {
            double sx = Math.sqrt(Q[i][i]);
            double sy = Math.sqrt(Q[i+1][i+1]);
            maxPos = Math.max(maxPos, Math.hypot(sx, sy));
        }
        return maxPos;
    }
    private double computeRelativeError(List<Measurement> measurements, double absError) {
        double totalLength = 0;
        for (Measurement m : measurements) {
            if (m instanceof DistanceMeasurement) totalLength += m.getObserved();
        }
        return totalLength == 0 ? 0 : absError / totalLength;
    }
    private double computeMaxHeightError(double[] X, double[][] Q) {
        double maxH = 0;
        for (int i = 0; i < X.length; i++) maxH = Math.max(maxH, Math.sqrt(Q[i][i]));
        return maxH;
    }
    public record NormativeResult(boolean passes, String requirement, double s0, double absoluteError, double relativeError) {
        @Override public String toString() {
            return String.format(Locale.US, "Соответствие нормативу: %s\nТребование: %s\ns0 = %.6f\nАбсолютная ошибка = %.4f м\nОтносительная ошибка = 1/%.0f",
                    passes ? "ДА" : "НЕТ", requirement, s0, absoluteError, 1.0/relativeError);
        }
    }
}
