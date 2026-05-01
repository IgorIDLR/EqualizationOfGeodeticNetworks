package ru.equalizationofgeodeticnetworks.core.approx.approxProviderImpl;

import ru.equalizationofgeodeticnetworks.core.approx.InitialApproxProvider;
import ru.equalizationofgeodeticnetworks.model.point.Point;
import ru.equalizationofgeodeticnetworks.model.traverse.PolygonometricTraverse;
import ru.equalizationofgeodeticnetworks.utils.Geometry;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class PolygonometryApproxProvider implements InitialApproxProvider {
    @Override
    public Map<String, Point> computeApprox(List<?> rawMeasurements,
                                            Map<String, Point> fixedPoints,
                                            Map<String, Double> startDirs,
                                            Map<String, Double> endDirs,
                                            List<String> unknownNames) {
        Map<String, Point> known = new HashMap<>(fixedPoints);
        Double currentDir = null;
        String currentStation = null;
        for (Object obj : rawMeasurements) {
            if (obj instanceof PolygonometricTraverse.RawAngle ra) {
                if (!known.containsKey(ra.getStation()))
                    throw new IllegalStateException("Станция " + ra.getStation() + " неизвестна");
                double beta = Math.toRadians(ra.getValueDeg());
                if (ra.getBack() == null) {
                    Double startDir = startDirs.get(ra.getStation());
                    if (startDir == null)
                        throw new IllegalStateException("Нет начального дирекционного угла для " + ra.getStation());
                    currentDir = startDir + beta - Math.PI;
                    currentStation = ra.getStation();
                } else {
                    if (currentDir == null)
                        throw new IllegalStateException("Нет текущего направления");
                    if (!ra.getStation().equals(currentStation))
                        throw new IllegalStateException("Угол на " + ra.getStation() + ", ожидалась " + currentStation);
                    currentDir = currentDir + beta - Math.PI;
                }
                currentDir = Geometry.normalizeAngle(currentDir);
            } else if (obj instanceof PolygonometricTraverse.RawDistance rd) {
                if (currentDir == null)
                    throw new IllegalStateException("Нет направления для расстояния " + rd.getP1() + "-" + rd.getP2());
                boolean known1 = known.containsKey(rd.getP1());
                boolean known2 = known.containsKey(rd.getP2());
                if (known1 && !known2) {
                    Point p1 = known.get(rd.getP1());
                    double x2 = p1.getX() + rd.getValue() * Math.cos(currentDir);
                    double y2 = p1.getY() + rd.getValue() * Math.sin(currentDir);
                    known.put(rd.getP2(), new Point(x2, y2));
                    currentStation = rd.getP2();
                } else if (!known1 && known2) {
                    Point p2 = known.get(rd.getP2());
                    double backDir = currentDir + Math.PI;
                    double x1 = p2.getX() + rd.getValue() * Math.cos(backDir);
                    double y1 = p2.getY() + rd.getValue() * Math.sin(backDir);
                    known.put(rd.getP1(), new Point(x1, y1));
                    currentStation = rd.getP1();
                } else if (known1 && known2) {
                    if (rd.getP1().equals(currentStation)) currentStation = rd.getP2();
                    else if (rd.getP2().equals(currentStation)) currentStation = rd.getP1();
                } else {
                    throw new IllegalStateException("Обе точки неизвестны: " + rd.getP1() + ", " + rd.getP2());
                }
            }
        }
        Map<String, Point> result = new HashMap<>();
        for (String name : unknownNames) {
            Point p = known.get(name);
            if (p == null) throw new IllegalStateException("Не удалось вычислить приближение для " + name);
            result.put(name, p);
        }
        return result;
    }
}
