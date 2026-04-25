package ru.equalizationofgeodeticnetworks.approxStrategy;

import ru.equalizationofgeodeticnetworks.network.NetworkData;
import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.utils.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitialApproxCalculator {

    private final List<NetworkData.RawMeasurement> rawMeasurements;
    private final Map<String, Point> fixedPoints;
    private final Map<String, Double> startDirs;
    private final Map<String, Double> endDirs;
    private final List<String> unknownNames;

    public InitialApproxCalculator(List<NetworkData.RawMeasurement> rawMeasurements,
                                   Map<String, Point> fixedPoints,
                                   Map<String, Double> startDirs,
                                   Map<String, Double> endDirs,
                                   List<String> unknownNames) {
        this.rawMeasurements = rawMeasurements;
        this.fixedPoints = fixedPoints;
        this.startDirs = startDirs;
        this.endDirs = endDirs;
        this.unknownNames = unknownNames;
    }

    /**
     * Вычисляет приближённые координаты определяемых точек последовательным ходом.
     * @return карта имя точки -> Point с приближёнными координатами
     * @throws IllegalStateException если измерения добавлены не в порядке хода
     */
    public Map<String, Point> computeApprox() {
        Map<String, Point> known = new HashMap<>(fixedPoints);
        Double currentDir = null;
        String currentStation = null;

        for (NetworkData.RawMeasurement rm : rawMeasurements) {
            if (rm.type == NetworkData.RawMeasurement.Type.ANGLE) {
                String station = rm.station;
                if (!known.containsKey(station))
                    throw new IllegalStateException("Станция " + station + " не известна на момент обработки угла");

                double beta = rm.angleValue;

                if (rm.back == null) {
                    // Первый угол на исходном пункте
                    Double startDir = startDirs.get(station);
                    if (startDir == null)
                        throw new IllegalStateException("Для угла с задним направлением (null) не задан начальный дирекционный угол станции " + station);
                    currentDir = startDir + beta - Math.PI;
                } else {
                    // Угол на промежуточной или конечной точке
                    if (currentDir == null)
                        throw new IllegalStateException("Нет текущего направления перед обработкой угла на станции " + station);
                    if (!station.equals(currentStation))
                        throw new IllegalStateException("Угол на станции " + station + ", а ожидалась " + currentStation);
                    currentDir = currentDir + beta - Math.PI;
                }
                currentDir = Geometry.normalizeAngle(currentDir);
            } else if (rm.type == NetworkData.RawMeasurement.Type.DISTANCE) {
                if (currentDir == null)
                    throw new IllegalStateException("Нет текущего направления перед обработкой расстояния " + rm.p1 + "-" + rm.p2);

                boolean known1 = known.containsKey(rm.p1);
                boolean known2 = known.containsKey(rm.p2);

                if (known1 && !known2) {
                    Point p1 = known.get(rm.p1);
                    double x2 = p1.getX() + rm.distValue * Math.cos(currentDir);
                    double y2 = p1.getY() + rm.distValue * Math.sin(currentDir);
                    known.put(rm.p2, new Point(x2, y2));
                    currentStation = rm.p2;
                } else if (!known1 && known2) {
                    Point p2 = known.get(rm.p2);
                    double backDir = currentDir + Math.PI;
                    double x1 = p2.getX() + rm.distValue * Math.cos(backDir);
                    double y1 = p2.getY() + rm.distValue * Math.sin(backDir);
                    known.put(rm.p1, new Point(x1, y1));
                    currentStation = rm.p1;
                } else if (known1 && known2) {
                    // Контрольное измерение – обновляем текущую станцию, если одна из точек совпадает с ней
                    if (rm.p1.equals(currentStation) && !rm.p2.equals(currentStation)) {
                        currentStation = rm.p2;
                    } else if (rm.p2.equals(currentStation) && !rm.p1.equals(currentStation)) {
                        currentStation = rm.p1;
                    }
                    // Если ни одна не равна текущей станции или обе равны (невозможно), оставляем без изменений
                } else {
                    throw new IllegalStateException("Обе точки неизвестны: " + rm.p1 + ", " + rm.p2);
                }
            }
        }

        Map<String, Point> result = new HashMap<>();
        for (String name : unknownNames) {
            Point p = known.get(name);
            if (p == null)
                throw new IllegalStateException("Не удалось вычислить приближение для точки " + name +
                        " (возможно, измерения добавлены не в порядке хода)");
            result.put(name, p);
        }
        return result;
    }
}
