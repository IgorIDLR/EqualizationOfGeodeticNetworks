package ru.equalizationofgeodeticnetworks.model.traverse;

import ru.equalizationofgeodeticnetworks.core.weight.WeightCalculator;
import ru.equalizationofgeodeticnetworks.model.point.Point;
import ru.equalizationofgeodeticnetworks.utils.converter.AngleConverter;
import ru.equalizationofgeodeticnetworks.utils.Geometry;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PolygonometricTraverse extends Traverse {
    private final Map<String, Point> fixedPoints = new HashMap<>();
    private final Map<String, Double> startDirs = new HashMap<>();
    private final Map<String, Double> endDirs = new HashMap<>();
    private final List<String> unknownNames = new ArrayList<>();
    private final List<Object> rawMeasurementsInOrder = new ArrayList<>();
    private WeightCalculator weightCalculator;
    private AngleConverter angleConverter;

    public PolygonometricTraverse(WeightCalculator weightCalculator, AngleConverter angleConverter) {
        this.weightCalculator = weightCalculator;
        this.angleConverter = angleConverter;
        detectTraverseType();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WeightCalculator weightCalculator;
        private AngleConverter angleConverter;
        private final Map<String, Point> fixedPoints = new LinkedHashMap<>();
        private final Map<String, Double> startDirs = new LinkedHashMap<>();
        private final Map<String, Double> endDirs = new LinkedHashMap<>();
        private final List<String> unknownNames = new ArrayList<>();
        private final List<Object> rawMeasurements = new ArrayList<>();

        public Builder weightCalculator(WeightCalculator wc) { this.weightCalculator = wc; return this; }
        public Builder angleConverter(AngleConverter ac) { this.angleConverter = ac; return this; }
        public Builder addFixedPoint(String name, double x, double y) { fixedPoints.put(name, new Point(x, y)); return this; }
        public Builder addUnknownPoint(String name) { unknownNames.add(name); return this; }
        public Builder setStartDir(String name, double deg) { startDirs.put(name, Math.toRadians(deg)); return this; }
        public Builder setEndDir(String name, double deg) { endDirs.put(name, Math.toRadians(deg)); return this; }
        public Builder addDistance(String p1, String p2, double value, double sigma) {
            RawDistance rd = new RawDistance(p1, p2, value, sigma);
            rawMeasurements.add(rd);
            return this;
        }
        public Builder addAngle(String station, String back, String forward, double valueDeg, double sigmaDeg) {
            RawAngle ra = new RawAngle(station, back, forward, valueDeg, sigmaDeg);
            rawMeasurements.add(ra);
            return this;
        }
        public PolygonometricTraverse build() {
            if (weightCalculator == null) throw new IllegalStateException("WeightCalculator must be set");
            if (angleConverter == null) throw new IllegalStateException("AngleConverter must be set");
            PolygonometricTraverse t = new PolygonometricTraverse(weightCalculator, angleConverter);
            t.fixedPoints.putAll(fixedPoints);
            t.startDirs.putAll(startDirs);
            t.endDirs.putAll(endDirs);
            t.unknownNames.addAll(unknownNames);
            t.rawMeasurementsInOrder.addAll(rawMeasurements);
            for (Object obj : t.rawMeasurementsInOrder) {
                if (obj instanceof RawDistance rd) {
                    if (rd.sigma > 0) rd.weight = 1.0 / (rd.sigma * rd.sigma);
                    else rd.weight = weightCalculator.computeDistanceWeight(rd.value, rd.sigma);
                } else if (obj instanceof RawAngle ra) {
                    if (ra.sigmaDeg > 0) {
                        double sigmaRad = angleConverter.degreesToRadians(ra.sigmaDeg);
                        ra.weight = 1.0 / (sigmaRad * sigmaRad);
                    } else {
                        ra.weight = weightCalculator.computeAngleWeight(ra.valueDeg, ra.sigmaDeg);
                    }
                }
            }
            t.detectTraverseType();
            return t;
        }
    }

    @Getter
    public static class RawDistance {
        private final String p1, p2;
        private final double value, sigma;
        private double weight;
        public RawDistance(String p1, String p2, double value, double sigma) {
            this.p1 = p1; this.p2 = p2; this.value = value; this.sigma = sigma;
        }
        public void setWeight(double w) { this.weight = w; }
    }

    @Getter
    public static class RawAngle {
        private final String station, back, forward;
        private final double valueDeg, sigmaDeg;
        private double weight;
        public RawAngle(String station, String back, String forward, double valueDeg, double sigmaDeg) {
            this.station = station; this.back = back; this.forward = forward;
            this.valueDeg = valueDeg; this.sigmaDeg = sigmaDeg;
        }
        public void setWeight(double w) { this.weight = w; }
    }

    @Override
    protected void detectTraverseType() {
        int fixedCount = fixedPoints.size();
        int startDirCount = startDirs.size();
        int endDirCount = endDirs.size();
        int unknownCount = unknownNames.size();

        if (fixedCount == 0) {
            isFreeNetwork = true;
            isOpenTraverse = true;
        } else if (fixedCount == 1 && startDirCount == 0 && endDirCount == 0) {
            isFreeNetwork = true;
            isOpenTraverse = true;
        } else if (fixedCount == 1 && startDirCount == 1 && endDirCount == 0) {
            isFreeNetwork = false;
            isOpenTraverse = true;
        } else if (fixedCount == 1 && startDirCount == 0 && endDirCount == 1) {
            isFreeNetwork = false;
            isOpenTraverse = true;
        } else if (fixedCount >= 2 && startDirCount >= 1 && endDirCount >= 1) {
            isFreeNetwork = false;
            isOpenTraverse = false;
            hasConditions = (unknownCount > 0);
        } else {
            isFreeNetwork = false;
            isOpenTraverse = true;
            hasConditions = false;
        }
    }

    @Override
    public void computeInitialApprox() {
        if (unknownNames.isEmpty()) return;
        if (!initialApprox.isEmpty() && initialApprox.size() == unknownNames.size() * 2) {
            approxComputed = true;
            return;
        }
        Map<String, Point> known = new HashMap<>(fixedPoints);
        Double currentDir = null;
        String currentStation = null;
        for (Object obj : rawMeasurementsInOrder) {
            if (obj instanceof RawAngle ra) {
                if (!known.containsKey(ra.getStation()))
                    throw new IllegalStateException("Станция " + ra.getStation() + " неизвестна");
                double beta = Math.toRadians(ra.getValueDeg());
                if (ra.getBack() == null) {
                    Double startDir = startDirs.get(ra.getStation());
                    if (startDir == null) throw new IllegalStateException("Нет начального дирекционного угла для " + ra.getStation());
                    currentDir = startDir + beta - Math.PI;
                    currentStation = ra.getStation();
                } else {
                    if (currentDir == null) throw new IllegalStateException("Нет текущего направления");
                    if (!ra.getStation().equals(currentStation))
                        throw new IllegalStateException("Угол на " + ra.getStation() + ", ожидалась " + currentStation);
                    currentDir = currentDir + beta - Math.PI;
                }
                currentDir = Geometry.normalizeAngle(currentDir);
            } else if (obj instanceof RawDistance rd) {
                if (currentDir == null) throw new IllegalStateException("Нет направления для расстояния " + rd.getP1() + "-" + rd.getP2());
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
        initialApprox.clear();
        for (String name : unknownNames) {
            Point p = known.get(name);
            if (p == null) throw new IllegalStateException("Не удалось вычислить приближение для " + name);
            initialApprox.add(p.getX());
            initialApprox.add(p.getY());
        }
        approxComputed = true;
    }
}
