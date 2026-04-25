package ru.equalizationofgeodeticnetworks.utils;

import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.utils.calculationUtils.AngleUtils;
import ru.equalizationofgeodeticnetworks.utils.calculationUtils.DirectionUtils;
import ru.equalizationofgeodeticnetworks.utils.calculationUtils.DistanceUtils;

public class Geometry {
    private Geometry() {}

    public static double normalizeAngle(double a) {
        return AngleUtils.normalizeAngle(a);
    }

    public static double normalizeDifference(double d) {
        return AngleUtils.normalizeDifference(d);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return DistanceUtils.distance(x1, y1, x2, y2);
    }

    public static double distance(double[] X, int idx, Point fixed) {
        return DistanceUtils.distance(X, idx, fixed);
    }

    public static double distance(double[] X, int idx1, int idx2) {
        return DistanceUtils.distance(X, idx1, idx2);
    }

    public static double derivativeDistX(double[] X, int idx, Point fixed) {
        return DistanceUtils.derivativeDistX(X, idx, fixed);
    }

    public static double derivativeDistY(double[] X, int idx, Point fixed) {
        return DistanceUtils.derivativeDistY(X, idx, fixed);
    }

    public static double derivativeDistX(double[] X, int idx1, int idx2) {
        return DistanceUtils.derivativeDistX(X, idx1, idx2);
    }

    public static double derivativeDistY(double[] X, int idx1, int idx2) {
        return DistanceUtils.derivativeDistY(X, idx1, idx2);
    }

    public static void addDistanceDerivatives(double[] deriv,
                                              int idx1, int idx2,
                                              double[] X,
                                              Point fixed1, Point fixed2,
                                              double sign) {
        DistanceUtils.addDistanceDerivatives(deriv, idx1, idx2, X, fixed1, fixed2, sign);
    }

    public static double direction(double x1, double y1, double x2, double y2) {
        return DirectionUtils.direction(x1, y1, x2, y2);
    }

    public static double direction(double[] X, int idxFrom, Point to) {
        return DirectionUtils.direction(X, idxFrom, to);
    }

    public static double direction(Point from, double[] X, int idxTo) {
        return DirectionUtils.direction(from, X, idxTo);
    }

    public static double direction(double[] X, int idxFrom, int idxTo) {
        return DirectionUtils.direction(X, idxFrom, idxTo);
    }

    public static void addDirectionDerivatives(double[] deriv,
                                               int idxFrom, int idxTo,
                                               double[] X,
                                               Point fixedFrom, Point fixedTo,
                                               double sign) {
        DirectionUtils.addDirectionDerivatives(deriv, idxFrom, idxTo, X, fixedFrom, fixedTo, sign);
    }
}
