package ru.equalizationofgeodeticnetworks.approxStrategy;

import ru.equalizationofgeodeticnetworks.network.NetworkData;
import ru.equalizationofgeodeticnetworks.point.Point;

import java.util.List;
import java.util.Map;

public interface InitialApproxStrategy {
    Map<String, Point> computeApprox(
            List<NetworkData.RawMeasurement> rawMeasurements,
            Map<String, Point> fixedPoints,
            Map<String, Double> startDirs,
            Map<String, Double> endDirs,
            List<String> unknownNames
    );
}
