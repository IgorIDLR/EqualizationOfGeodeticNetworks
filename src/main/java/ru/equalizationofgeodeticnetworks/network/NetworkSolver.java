package ru.equalizationofgeodeticnetworks.network;

import ru.equalizationofgeodeticnetworks.adjustment.AdjustmentMethod;
import ru.equalizationofgeodeticnetworks.approxStrategy.InitialApproxStrategy;
import ru.equalizationofgeodeticnetworks.measurement.IndexMapper;
import ru.equalizationofgeodeticnetworks.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.measurement.MeasurementFactory;
import ru.equalizationofgeodeticnetworks.point.Point;
import ru.equalizationofgeodeticnetworks.support.ResultPrinter;

import java.util.List;
import java.util.Map;

public class NetworkSolver {

    private final NetworkData data;
    private final IndexMapper indexMapper;
    private final InitialApproxStrategy approxStrategy;
    private final MeasurementFactory measurementFactory;
    private final AdjustmentMethod adjustmentMethod;
    private final ResultPrinter.Builder printerBuilder;

    public NetworkSolver(NetworkData data,
                         InitialApproxStrategy approxStrategy,
                         AdjustmentMethod adjustmentMethod) {
        this.data = data;
        this.indexMapper = new IndexMapper(data.getUnknownNames());
        this.approxStrategy = approxStrategy;
        this.measurementFactory = new MeasurementFactory(data, indexMapper);
        this.adjustmentMethod = adjustmentMethod;
        this.printerBuilder = new ResultPrinter.Builder(data, indexMapper);
    }

    public ResultPrinter.Builder configurePrinter() { return printerBuilder; }

    public void solve(double eps, int maxIter) {
        Map<String, Point> approx = approxStrategy.computeApprox(
                data.getRawMeasurements(),
                data.getFixedPoints(),
                data.getStartDirs(),
                data.getEndDirs(),
                data.getUnknownNames()
        );

        double[] X0 = buildXFromApprox(approx);
        List<Measurement> measurements = measurementFactory.createMeasurements();

        int n = X0.length;
        double[][] Q = new double[n][n];
        for (int i = 0; i < n; i++) Q[i][i] = 1e10;

        double[] X = X0.clone();
        adjustmentMethod.adjust(X, Q, measurements, eps, maxIter);

        ResultPrinter printer = printerBuilder.initialX(X0).build();
        printer.print(X, measurements, Q, maxIter);
    }

    private double[] buildXFromApprox(Map<String, Point> approx) {
        int n = indexMapper.getParamCount();
        double[] X = new double[n];
        for (String name : data.getUnknownNames()) {
            int idx = indexMapper.getIndex(name);
            Point p = approx.get(name);
            if (p == null) {
                throw new IllegalStateException("Отсутствует приближение для точки " + name);
            }
            X[idx] = p.getX();
            X[idx + 1] = p.getY();
        }
        return X;
    }
}
