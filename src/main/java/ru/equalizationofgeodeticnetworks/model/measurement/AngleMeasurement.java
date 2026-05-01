package ru.equalizationofgeodeticnetworks.model.measurement;

import ru.equalizationofgeodeticnetworks.model.point.Point;
import ru.equalizationofgeodeticnetworks.utils.Geometry;

public class AngleMeasurement extends Measurement {

    private final int idxStation, idxBack, idxForward;
    private final Point fixedStation, fixedBack, fixedForward;
    private final boolean useFixedDirBack, useFixedDirForward;
    private final double fixedDirBack, fixedDirForward;

    public AngleMeasurement(String name, double observed, double weight,
                            int idxStation, Point fixedStation,
                            int idxBack, Point fixedBack,
                            int idxForward, Point fixedForward,
                            boolean useFixedDirBack, double fixedDirBack,
                            boolean useFixedDirForward, double fixedDirForward) {
        super(name, observed, weight);
        this.idxStation = idxStation;
        this.fixedStation = fixedStation;
        this.idxBack = idxBack;
        this.fixedBack = fixedBack;
        this.idxForward = idxForward;
        this.fixedForward = fixedForward;
        this.useFixedDirBack = useFixedDirBack;
        this.fixedDirBack = fixedDirBack;
        this.useFixedDirForward = useFixedDirForward;
        this.fixedDirForward = fixedDirForward;
    }

    @Override
    public double expected(double[] X) {
        double alphaBack, alphaForward;
        if (useFixedDirBack) alphaBack = fixedDirBack;
        else {
            if (idxStation >= 0) {
                alphaBack = (idxBack >= 0) ? Geometry.direction(X, idxStation, idxBack)
                        : Geometry.direction(X, idxStation, fixedBack);
            } else {
                alphaBack = (idxBack >= 0) ? Geometry.direction(fixedStation, X, idxBack)
                        : Geometry.direction(fixedStation.getX(), fixedStation.getY(), fixedBack.getX(), fixedBack.getY());
            }
        }
        if (useFixedDirForward) alphaForward = fixedDirForward;
        else {
            if (idxStation >= 0) {
                alphaForward = (idxForward >= 0) ? Geometry.direction(X, idxStation, idxForward)
                        : Geometry.direction(X, idxStation, fixedForward);
            } else {
                alphaForward = (idxForward >= 0) ? Geometry.direction(fixedStation, X, idxForward)
                        : Geometry.direction(fixedStation.getX(), fixedStation.getY(), fixedForward.getX(), fixedForward.getY());
            }
        }
        double beta = alphaForward - alphaBack + Math.PI;
        return Geometry.normalizeAngle(beta);
    }

    @Override
    public void derivatives(double[] X, double[] deriv) {
        if (!useFixedDirForward) {
            Geometry.addDirectionDerivatives(deriv, idxStation, idxForward,
                    X, fixedStation, fixedForward, +1.0);
        }
        if (!useFixedDirBack) {
            Geometry.addDirectionDerivatives(deriv, idxStation, idxBack,
                    X, fixedStation, fixedBack, -1.0);
        }
    }

    @Override
    public double residual(double expected) {
        return Geometry.normalizeDifference(observed - expected);
    }
}
