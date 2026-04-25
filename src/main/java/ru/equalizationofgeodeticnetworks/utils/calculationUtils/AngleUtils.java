package ru.equalizationofgeodeticnetworks.utils.calculationUtils;

import ru.equalizationofgeodeticnetworks.utils.constantsUtils.MathConstants;

public class AngleUtils {

    private AngleUtils() {}

    public static double normalizeAngle(double a) {
        a %= MathConstants.TWO_PI;
        if (a < 0) a += MathConstants.TWO_PI;
        return a;
    }

    public static double normalizeDifference(double d) {
        d %= MathConstants.TWO_PI;
        if (d > Math.PI) d -= MathConstants.TWO_PI;
        else if (d < -Math.PI) d += MathConstants.TWO_PI;
        return d;
    }
}
