package ru.equalizationofgeodeticnetworks.traverse;

import ru.equalizationofgeodeticnetworks.adjustment.AdjustmentMethod;
import ru.equalizationofgeodeticnetworks.approxStrategy.InitialApproxStrategy;
import ru.equalizationofgeodeticnetworks.approxStrategy.TraverseInitialApprox;
import ru.equalizationofgeodeticnetworks.measurement.IndexMapper;
import ru.equalizationofgeodeticnetworks.measurement.MeasurementFactory;
import ru.equalizationofgeodeticnetworks.network.NetworkData;
import ru.equalizationofgeodeticnetworks.point.Point;

import java.util.Map;

public class PolygonometricTraverse extends Traverse {

    private NetworkData data;
    private IndexMapper indexMapper;

    public PolygonometricTraverse(AdjustmentMethod method) {
        super(method);
        this.data = new NetworkData();
    }

    public void addFixedPoint(String name, double x, double y) {
        data.addFixedPoint(name, x, y);
    }

    public void addUnknownPoint(String name) {
        data.addUnknownPoint(name);
    }

    public void setStartDir(String pointName, double dirDeg) {
        data.setStartDir(pointName, Math.toRadians(dirDeg));
    }

    public void setEndDir(String pointName, double dirDeg) {
        data.setEndDir(pointName, Math.toRadians(dirDeg));
    }

    public void addDistance(String p1, String p2, double value, double sigma) {
        data.addDistance(p1, p2, value, sigma);
    }

    public void addAngle(String station, String back, String forward, double valueDeg, double sigmaDeg) {
        data.addAngle(station, back, forward, valueDeg, sigmaDeg);
    }

    @Override
    public void buildInitialApprox() {
        InitialApproxStrategy approxStrategy = new TraverseInitialApprox();
        Map<String, Point> approx = approxStrategy.computeApprox(
                data.getRawMeasurements(),
                data.getFixedPoints(),
                data.getStartDirs(),
                data.getEndDirs(),
                data.getUnknownNames()
        );
        indexMapper = new IndexMapper(data.getUnknownNames());
        int n = indexMapper.getParamCount();
        params = new double[n];
        for (String name : data.getUnknownNames()) {
            int idx = indexMapper.getIndex(name);
            Point p = approx.get(name);
            params[idx] = p.getX();
            params[idx + 1] = p.getY();
        }
    }

    @Override
    public void solve(double eps, int maxIter) {
        buildInitialApprox();
        MeasurementFactory factory = new MeasurementFactory(data, indexMapper);
        measurements.clear();
        measurements.addAll(factory.createMeasurements());
        super.solve(eps, maxIter);
    }
}
