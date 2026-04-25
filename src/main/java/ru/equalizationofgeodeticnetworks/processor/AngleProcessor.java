package ru.equalizationofgeodeticnetworks.processor;

import ru.equalizationofgeodeticnetworks.network.NetworkData;
import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.utils.Geometry;

import java.util.Map;

public class AngleProcessor implements MeasurementProcessor {

    private final Map<String, Double> startDirs;
    private final Map<String, Double> endDirs;

    public AngleProcessor(Map<String, Double> startDirs, Map<String, Double> endDirs) {
        this.startDirs = startDirs;
        this.endDirs = endDirs;
    }

    @Override
    public ProcessingState process(ProcessingState state, NetworkData.RawMeasurement rm) {
        if (rm.type != NetworkData.RawMeasurement.Type.ANGLE) {
            throw new IllegalArgumentException("AngleProcessor получил не угол");
        }

        Map<String, Point> known = state.getKnown();
        Double currentDir = state.getCurrentDir();
        String currentStation = state.getCurrentStation();

        String station = rm.station;
        if (!known.containsKey(station))
            throw new IllegalStateException("Станция " + station + " не известна на момент обработки угла");

        double beta = rm.angleValue;

        if (rm.back == null) {
            Double startDir = startDirs.get(station);
            if (startDir == null)
                throw new IllegalStateException("Для угла с задним направлением (null) не задан начальный дирекционный угол станции " + station);
            currentDir = startDir + beta - Math.PI;
        } else {
            if (currentDir == null)
                throw new IllegalStateException("Нет текущего направления перед обработкой угла на станции " + station);
            if (!station.equals(currentStation))
                throw new IllegalStateException("Угол на станции " + station + ", а ожидалась " + currentStation);
            currentDir = currentDir + beta - Math.PI;
        }
        currentDir = Geometry.normalizeAngle(currentDir);

        state.setCurrentDir(currentDir);
        return state;
    }
}
