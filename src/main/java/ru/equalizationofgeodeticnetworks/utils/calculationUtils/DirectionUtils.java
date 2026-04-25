package ru.equalizationofgeodeticnetworks.utils.calculationUtils;

import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.utils.constantsUtils.MathConstants;

public class DirectionUtils {

    private DirectionUtils() {}

    public static double direction(double x1, double y1, double x2, double y2) {
        return Math.atan2(y2 - y1, x2 - x1);
    }

    public static double direction(double[] X, int idxFrom, Point to) {
        return Math.atan2(to.getY() - X[idxFrom + 1], to.getX() - X[idxFrom]);
    }

    public static double direction(Point from, double[] X, int idxTo) {
        return Math.atan2(X[idxTo + 1] - from.getY(), X[idxTo] - from.getX());
    }

    public static double direction(double[] X, int idxFrom, int idxTo) {
        return Math.atan2(X[idxTo + 1] - X[idxFrom + 1], X[idxTo] - X[idxFrom]);
    }

    public static void addDirectionDerivatives(double[] deriv,
                                               int idxFrom, int idxTo,
                                               double[] X,
                                               Point fixedFrom, Point fixedTo,
                                               double sign) {
        double xFrom = (idxFrom >= 0) ? X[idxFrom] : fixedFrom.getX();
        double yFrom = (idxFrom >= 0) ? X[idxFrom + 1] : fixedFrom.getY();
        double xTo   = (idxTo >= 0)   ? X[idxTo]   : fixedTo.getX();
        double yTo   = (idxTo >= 0)   ? X[idxTo + 1] : fixedTo.getY();

        double dx = xTo - xFrom;
        double dy = yTo - yFrom;
        double s2 = dx * dx + dy * dy;
        if (s2 <= MathConstants.EPS) return;

        double invS2 = 1.0 / s2;
        double ddx = dy * invS2;
        double ddy = -dx * invS2;

        if (idxFrom >= 0) {
            deriv[idxFrom]   += sign * ddx;
            deriv[idxFrom + 1] += sign * ddy;
        }
        if (idxTo >= 0) {
            deriv[idxTo]     += sign * (-dy * invS2);
            deriv[idxTo + 1] += sign * (dx * invS2);
        }
    }
}
