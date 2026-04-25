package ru.equalizationofgeodeticnetworks.measurement;

import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.utils.Geometry;

public class AngleMeasurement extends Measurement {

    private int idxStation, idxBack, idxForward;
    private Point fixedStation, fixedBack, fixedForward;
    private boolean useFixedDirBack, useFixedDirForward;
    private double fixedDirBack, fixedDirForward;

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

        // Заднее направление: от задней точки к станции
        if (useFixedDirBack) {
            alphaBack = fixedDirBack;
        } else {
            if (idxBack >= 0) { // задняя точка определяемая
                if (idxStation >= 0) {
                    // обе определяемые: от задней к станции
                    alphaBack = Geometry.direction(X, idxBack, idxStation);
                } else {
                    // станция фиксированная, задняя определяемая: от задней к fixedStation
                    alphaBack = Geometry.direction(X, idxBack, fixedStation);
                }
            } else { // задняя точка фиксированная
                if (idxStation >= 0) {
                    // станция определяемая, задняя фиксированная: от fixedBack к станции
                    alphaBack = Geometry.direction(fixedBack, X, idxStation);
                } else {
                    // обе фиксированные
                    alphaBack = Geometry.direction(fixedBack.getX(), fixedBack.getY(), fixedStation.getX(), fixedStation.getY());
                }
            }
        }

        // Переднее направление: от станции к передней точке
        if (useFixedDirForward) {
            alphaForward = fixedDirForward;
        } else {
            if (idxStation >= 0) {
                if (idxForward >= 0) {
                    alphaForward = Geometry.direction(X, idxStation, idxForward);
                } else {
                    alphaForward = Geometry.direction(X, idxStation, fixedForward);
                }
            } else {
                if (idxForward >= 0) {
                    alphaForward = Geometry.direction(fixedStation, X, idxForward);
                } else {
                    alphaForward = Geometry.direction(fixedStation.getX(), fixedStation.getY(), fixedForward.getX(), fixedForward.getY());
                }
            }
        }

        double beta = alphaForward - alphaBack + Math.PI;
        return Geometry.normalizeAngle(beta);
    }

    @Override
    public void derivatives(double[] X, double[] deriv) {
        if (!useFixedDirForward) {
            Point fromFixed = (idxStation >= 0) ? null : fixedStation;
            Point toFixed   = (idxForward >= 0) ? null : fixedForward;
            Geometry.addDirectionDerivatives(deriv, idxStation, idxForward,
                    X, fromFixed, toFixed, +1.0);
        }
        if (!useFixedDirBack) {
            // Заднее направление: от задней точки к станции
            Point fromFixed = (idxBack >= 0) ? null : fixedBack;
            Point toFixed   = (idxStation >= 0) ? null : fixedStation;
            Geometry.addDirectionDerivatives(deriv, idxBack, idxStation,
                    X, fromFixed, toFixed, -1.0);
        }
    }

    @Override
    public double residual(double expected) {
        return Geometry.normalizeDifference(observed - expected);
    }
}
