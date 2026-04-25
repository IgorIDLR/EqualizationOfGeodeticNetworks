package ru.equalizationofgeodeticnetworks.measurement;

import ru.equalizationofgeodeticnetworks.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.point.Point;

public class DistanceMeasurement extends Measurement {
    private int idx1, idx2;
    private Point fixed1, fixed2;

    public DistanceMeasurement(String name, double observed, double weight, int idx1, int idx2) {
        super(name, observed, weight);
        this.idx1 = idx1;
        this.idx2 = idx2;
    }

    public DistanceMeasurement(String name, double observed, double weight, int idx1, Point fixed2) {
        super(name, observed, weight);
        this.idx1 = idx1;
        this.idx2 = -1;
        this.fixed2 = fixed2;
    }

    public DistanceMeasurement(String name, double observed, double weight, Point fixed1, int idx2) {
        super(name, observed, weight);
        this.idx1 = -1;
        this.idx2 = idx2;
        this.fixed1 = fixed1;
    }

    public DistanceMeasurement(String name, double observed, double weight, Point fixed1, Point fixed2) {
        super(name, observed, weight);
        this.idx1 = -1;
        this.idx2 = -1;
        this.fixed1 = fixed1;
        this.fixed2 = fixed2;
    }

    @Override
    public double expected(double[] X) {
        double x1 = (idx1 >= 0) ? X[idx1] : fixed1.getX();
        double y1 = (idx1 >= 0) ? X[idx1+1] : fixed1.getY();
        double x2 = (idx2 >= 0) ? X[idx2] : fixed2.getX();
        double y2 = (idx2 >= 0) ? X[idx2+1] : fixed2.getY();
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    public void derivatives(double[] X, double[] deriv) {
        double x1 = (idx1 >= 0) ? X[idx1] : fixed1.getX();
        double y1 = (idx1 >= 0) ? X[idx1+1] : fixed1.getY();
        double x2 = (idx2 >= 0) ? X[idx2] : fixed2.getX();
        double y2 = (idx2 >= 0) ? X[idx2+1] : fixed2.getY();
        double dx = x1 - x2;
        double dy = y1 - y2;
        double s = Math.sqrt(dx*dx + dy*dy);
        if (s < 1e-12) return;
        if (idx1 >= 0) {
            deriv[idx1]   = dx / s;
            deriv[idx1+1] = dy / s;
        }
        if (idx2 >= 0) {
            deriv[idx2]   = -dx / s;
            deriv[idx2+1] = -dy / s;
        }
    }
}