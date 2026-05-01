package ru.equalizationofgeodeticnetworks.utils;

import lombok.experimental.UtilityClass;
import ru.equalizationofgeodeticnetworks.model.point.Point;

@UtilityClass
public final class Geometry {

    private static final double TWO_PI = 2.0 * Math.PI;
    private static final double EPS = 1e-12;

    public static double normalizeAngle(double a) {
        a %= TWO_PI;
        if (a < 0) a += TWO_PI;
        return a;
    }

    public static double normalizeDifference(double d) {
        d %= TWO_PI;
        if (d > Math.PI) d -= TWO_PI;
        else if (d < -Math.PI) d += TWO_PI;
        return d;
    }

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
        double dx = xTo - xFrom, dy = yTo - yFrom;
        double s2 = dx*dx + dy*dy;
        if (s2 <= EPS) return;
        double invS2 = 1.0 / s2;
        double ddx =  dy * invS2, ddy = -dx * invS2;
        if (idxFrom >= 0) {
            deriv[idxFrom]   += sign * ddx;
            deriv[idxFrom+1] += sign * ddy;
        }
        if (idxTo >= 0) {
            deriv[idxTo]     += sign * (-dy * invS2);
            deriv[idxTo+1]   += sign * ( dx * invS2);
        }
    }
}
