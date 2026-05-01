package ru.equalizationofgeodeticnetworks.core.approx;

import ru.equalizationofgeodeticnetworks.model.point.Point;
import java.util.List;
import java.util.Map;

public interface InitialApproxProvider {
    Map<String, Point> computeApprox(List<?> rawMeasurements,
                                     Map<String, Point> fixedPoints,
                                     Map<String, Double> startDirs,
                                     Map<String, Double> endDirs,
                                     List<String> unknownNames);
}
