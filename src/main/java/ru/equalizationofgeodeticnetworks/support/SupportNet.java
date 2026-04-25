package ru.equalizationofgeodeticnetworks.support;

import ru.equalizationofgeodeticnetworks.adjustment.AdjustmentMethod;
import ru.equalizationofgeodeticnetworks.network.Network;
import ru.equalizationofgeodeticnetworks.point.Point;

import java.util.List;
import java.util.Locale;

public class SupportNet {

    private Network network;
    private double sigmaAngle;
    private double sigmaDist;
    private double defaultEps = 1e-4;
    private int defaultMaxIter = 20;
    public boolean leftDegrees = false;

    private String firstPoint;
    private double startDeg;
    private String endPoint;
    private double endDeg;


    public SupportNet(double sigmaAngle, double sigmaDist) {
        Locale.setDefault(Locale.US);
        this.network = new Network();
        this.sigmaAngle = sigmaAngle / 3600; this.sigmaDist = sigmaDist;
    }

    public SupportNet(double sigmaAngle, double sigmaDist, AdjustmentMethod adjustmentMethod) {
        this(sigmaAngle, sigmaDist); this.network = new Network(adjustmentMethod);
    }

    public void setLogging() { this.network.setLoggingInfo(); }

    public void addFixedPoints(List<Point> list, double prevAng, double endAng) {
        list.forEach(o -> this.network.addFixedPoint(o.getName(), o.getX(), o.getY()));
        this.network.setStartDir(this.firstPoint = list.getFirst().getName(), this.startDeg = prevAng);
        this.network.setEndDir(this.endPoint = list.getLast().getName(), this.endDeg = endAng);
    }

    public void addFixedPoints(Point p1, Point p2, Point e1, Point e2) {
        this.addFixedPoints(List.of(p2, e1),
                SupportDegreesConverter.calculateAzimuth(p1, p2),
                SupportDegreesConverter.calculateAzimuth(e1, e2)
        );
    }

    public ResultPrinter.Builder setPrintParametrs() { return this.network.getConfigurationPrint(); }

    public void addDimensions(List<SupportDimension> list) {
        String prev = null;
        String next = null;
        //public void addAngle(String station, String back, String forward, double valueDeg, double sigmaDeg)
        for (int i = 0; i < list.size(); i++) {
            SupportDimension sD = list.get(i);
            next = ((i + 1) < list.size()) ? list.get(i + 1).getName() : null;
            this.network.addUnknownPoint(sD.getName());
            this.network.addAngle(sD.getName(), prev, next, this.leftDegrees ? 360 - sD.getAngl() : sD.getAngl(), this.sigmaAngle);
            if (next != null) this.network.addDistance(sD.getName(), next, sD.getDistant(), this.sigmaDist);
            prev = sD.getName();
        }
    }

    public void solve() { this.network.solve(this.defaultEps, this.defaultMaxIter); }
}
