package ru.equalizationofgeodeticnetworks.network;

import ru.equalizationofgeodeticnetworks.point.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkData {

    private final Map<String, Point> fixedPoints = new HashMap<>();
    private final List<String> unknownNames = new ArrayList<>();
    private final Map<String, Double> startDirs = new HashMap<>();
    private final Map<String, Double> endDirs = new HashMap<>();
    private final List<RawMeasurement> rawMeasurements = new ArrayList<>();

    public static class RawMeasurement {
        public enum Type { DISTANCE, ANGLE }
        public Type type;
        public String p1, p2;
        public double distValue, distSigma;
        public String station, back, forward;
        public double angleValue, angleSigma; // в радианах
    }

    public void addFixedPoint(String name, double x, double y) {
        fixedPoints.put(name, new Point(x, y));
    }

    public void addUnknownPoint(String name) {
        if (!fixedPoints.containsKey(name))
            unknownNames.add(name);
    }

    public void setStartDir(String pointName, double dirRad) {
        startDirs.put(pointName, dirRad);
    }

    public void setEndDir(String pointName, double dirRad) {
        endDirs.put(pointName, dirRad);
    }

    public void addDistance(String p1, String p2, double value, double sigma) {
        RawMeasurement rm = new RawMeasurement();
        rm.type = RawMeasurement.Type.DISTANCE;
        rm.p1 = p1;
        rm.p2 = p2;
        rm.distValue = value;
        rm.distSigma = sigma;
        rawMeasurements.add(rm);
    }

    public void addAngle(String station, String back, String forward, double valueRad, double sigmaRad) {
        RawMeasurement rm = new RawMeasurement();
        rm.type = RawMeasurement.Type.ANGLE;
        rm.station = station;
        rm.back = back;
        rm.forward = forward;
        rm.angleValue = valueRad;
        rm.angleSigma = sigmaRad;
        rawMeasurements.add(rm);
    }

    public Map<String, Point> getFixedPoints() { return fixedPoints; }
    public List<String> getUnknownNames() { return unknownNames; }
    public Map<String, Double> getStartDirs() { return startDirs; }
    public Map<String, Double> getEndDirs() { return endDirs; }
    public List<RawMeasurement> getRawMeasurements() { return rawMeasurements; }
}
