package ru.equalizationofgeodeticnetworks.core.statistics.statisticsCollectorImpl;

import ru.equalizationofgeodeticnetworks.core.statistics.StatisticsCollector;
import ru.equalizationofgeodeticnetworks.model.*;
import ru.equalizationofgeodeticnetworks.model.measurement.AngleMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.DistanceMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.HeightMeasurement;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.statistics.AdjustmentStatistics;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DefaultStatisticsCollector implements StatisticsCollector {
    @Override
    public AdjustmentStatistics collect(double[] X, double[][] Q, List<Measurement> measurements) {
        AdjustmentStatistics stats = new AdjustmentStatistics();
        stats.setTotalMeasurements(measurements.size());
        stats.setDistanceCount((int) measurements.stream().filter(m -> m instanceof DistanceMeasurement).count());
        stats.setAngleCount((int) measurements.stream().filter(m -> m instanceof AngleMeasurement).count());
        stats.setHeightDiffCount((int) measurements.stream().filter(m -> m instanceof HeightMeasurement).count());
        stats.setDegreesOfFreedom(measurements.size() - X.length);
        stats.setUnitWeightError(computeUnitWeightError(measurements, X));
        stats.setAbsoluteError(computeAbsoluteError(X, Q));
        stats.setRelativeError(computeRelativeError(measurements, stats.getAbsoluteError()));
        Map<String, Double> coordErrors = new LinkedHashMap<>();
        for (int i = 0; i < X.length; i += 2) {
            coordErrors.put("Point" + (i/2), Math.hypot(Math.sqrt(Q[i][i]), Math.sqrt(Q[i+1][i+1])));
        }
        stats.setCoordinateErrors(coordErrors);
        return stats;
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
}
