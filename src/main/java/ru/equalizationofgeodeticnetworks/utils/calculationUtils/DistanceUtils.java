package ru.equalizationofgeodeticnetworks.utils.calculationUtils;

import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.utils.constantsUtils.MathConstants;

public class DistanceUtils {

    private DistanceUtils() {}

    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(double[] X, int idx, Point fixed) {
        double dx = X[idx] - fixed.getX();
        double dy = X[idx + 1] - fixed.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(double[] X, int idx1, int idx2) {
        double dx = X[idx1] - X[idx2];
        double dy = X[idx1 + 1] - X[idx2 + 1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double derivativeDistX(double[] X, int idx, Point fixed) {
        double dx = X[idx] - fixed.getX();
        double dy = X[idx + 1] - fixed.getY();
        double s = Math.sqrt(dx * dx + dy * dy);
        return (s > MathConstants.EPS) ? dx / s : 0.0;
    }

    public static double derivativeDistY(double[] X, int idx, Point fixed) {
        double dx = X[idx] - fixed.getX();
        double dy = X[idx + 1] - fixed.getY();
        double s = Math.sqrt(dx * dx + dy * dy);
        return (s > MathConstants.EPS) ? dy / s : 0.0;
    }

    public static double derivativeDistX(double[] X, int idx1, int idx2) {
        double dx = X[idx1] - X[idx2];
        double dy = X[idx1 + 1] - X[idx2 + 1];
        double s = Math.sqrt(dx * dx + dy * dy);
        return (s > MathConstants.EPS) ? dx / s : 0.0;
    }

    public static double derivativeDistY(double[] X, int idx1, int idx2) {
        double dx = X[idx1] - X[idx2];
        double dy = X[idx1 + 1] - X[idx2 + 1];
        double s = Math.sqrt(dx * dx + dy * dy);
        return (s > MathConstants.EPS) ? dy / s : 0.0;
    }

    public static void addDistanceDerivatives(double[] deriv,
                                              int idx1, int idx2,
                                              double[] X,
                                              Point fixed1, Point fixed2,
                                              double sign) {
        double x1 = (idx1 >= 0) ? X[idx1] : fixed1.getX();
        double y1 = (idx1 >= 0) ? X[idx1 + 1] : fixed1.getY();
        double x2 = (idx2 >= 0) ? X[idx2] : fixed2.getX();
        double y2 = (idx2 >= 0) ? X[idx2 + 1] : fixed2.getY();

        double dx = x1 - x2;
        double dy = y1 - y2;
        double s = Math.sqrt(dx * dx + dy * dy);
        if (s <= MathConstants.EPS) return;

        double invS = 1.0 / s;
        double ddx = dx * invS;
        double ddy = dy * invS;

        if (idx1 >= 0) {
            deriv[idx1] += sign * ddx;
            deriv[idx1 + 1] += sign * ddy;
        }
        if (idx2 >= 0) {
            deriv[idx2] += sign * (-ddx);
            deriv[idx2 + 1] += sign * (-ddy);
        }
    }
}
