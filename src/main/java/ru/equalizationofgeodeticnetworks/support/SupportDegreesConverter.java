package ru.equalizationofgeodeticnetworks.support;

import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.utils.constantsUtils.MathConstants;

public class SupportDegreesConverter {

    public static double degreesInAng(double d, double m, double s) { return d + m/60 + d/3600; }

    public static double[] degreesInAng(double d) {
        var retD = new double[3];
        retD[0] = Math.floor(d % 360);
        retD[1] = Math.floor((d = (d - retD[0])) * 60);
        retD[2] = (d - retD[1]/60) * 3600;
        return retD;
    }

    public static double calculateAzimuth(Point p1, Point p2) {
        double az = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
        return Math.toDegrees(az < 0 ? MathConstants.TWO_PI + az : az);
    }
}
