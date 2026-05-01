package ru.equalizationofgeodeticnetworks.core.statistics.statisticsCollectorImpl;

import ru.equalizationofgeodeticnetworks.core.reliability.ReliabilityResult;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AdjustmentStatistics {

    private String networkType;
    private int totalMeasurements;
    private int distanceCount;
    private int angleCount;
    private int heightDiffCount;
    private int degreesOfFreedom;
    private double unitWeightError;
    private double absoluteError;
    private double relativeError;
    private double traverseLinearClosure;
    private double traverseAngularClosure;
    private List<GrossErrorReport> grossErrors;
    private Map<String, Double> coordinateErrors;
    private Map<String, double[]> confidenceIntervals;
    private ReliabilityResult reliabilityResult;

    @Data public static class GrossErrorReport {
        private final String measurementName;
        private final double residual;
        private final double limit;
    }
}
