package ru.equalizationofgeodeticnetworks.approxStrategy;

import ru.equalizationofgeodeticnetworks.network.NetworkData;
import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.processor.AngleProcessor;
import ru.equalizationofgeodeticnetworks.processor.DistanceProcessor;
import ru.equalizationofgeodeticnetworks.processor.MeasurementProcessor;
import ru.equalizationofgeodeticnetworks.processor.ProcessingState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraverseInitialApprox implements InitialApproxStrategy {

    @Override
    public Map<String, Point> computeApprox(
            List<NetworkData.RawMeasurement> rawMeasurements,
            Map<String, Point> fixedPoints,
            Map<String, Double> startDirs,
            Map<String, Double> endDirs,
            List<String> unknownNames) {

        Map<NetworkData.RawMeasurement.Type, MeasurementProcessor> processors = new HashMap<>();
        processors.put(NetworkData.RawMeasurement.Type.ANGLE, new AngleProcessor(startDirs, endDirs));
        processors.put(NetworkData.RawMeasurement.Type.DISTANCE, new DistanceProcessor());

        Map<String, Point> known = new HashMap<>(fixedPoints);
        ProcessingState state = new ProcessingState(known, null, null);

        for (NetworkData.RawMeasurement rm : rawMeasurements) {
            MeasurementProcessor processor = processors.get(rm.type);
            if (processor == null) {
                throw new IllegalArgumentException("Неизвестный тип измерения: " + rm.type);
            }
            state = processor.process(state, rm);
        }

        Map<String, Point> result = new HashMap<>();
        for (String name : unknownNames) {
            Point p = state.getKnown().get(name);
            if (p == null) {
                throw new IllegalStateException("Не удалось вычислить приближение для точки " + name +
                        " (возможно, измерения добавлены не в порядке хода)");
            }
            result.put(name, p);
        }
        return result;
    }
}
