package ru.equalizationofgeodeticnetworks.core.statistics;

import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.statistics.AdjustmentStatistics;
import java.util.List;

public interface StatisticsCollector {
    AdjustmentStatistics collect(double[] X, double[][] Q, List<Measurement> measurements);
}