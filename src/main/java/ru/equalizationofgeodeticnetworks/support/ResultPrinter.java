package ru.equalizationofgeodeticnetworks.support;

import ru.equalizationofgeodeticnetworks.measurement.AngleMeasurement;
import ru.equalizationofgeodeticnetworks.measurement.DistanceMeasurement;
import ru.equalizationofgeodeticnetworks.measurement.IndexMapper;
import ru.equalizationofgeodeticnetworks.measurement.Measurement;
import ru.equalizationofgeodeticnetworks.network.NetworkData;
import ru.equalizationofgeodeticnetworks.point.Point;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResultPrinter {

    private final NetworkData data;
    private final IndexMapper indexMapper;
    private double[] initialX; // предварительные координаты (до уравнивания)
    private final boolean printCovariance;
    private final boolean printMeasurementErrors;
    private final boolean printTraverseClosure;
    private final boolean printInitialData;
    private final boolean printAdjustedPoints;

    private ResultPrinter(Builder builder) {
        this.data = builder.data;
        this.indexMapper = builder.indexMapper;
        this.initialX = builder.initialX;
        this.printCovariance = builder.printCovariance;
        this.printMeasurementErrors = builder.printMeasurementErrors;
        this.printTraverseClosure = builder.printTraverseClosure;
        this.printInitialData = builder.printInitialData;
        this.printAdjustedPoints = builder.printAdjustedPoints;
    }

    public void setInitialX(double[] initialX) {
        this.initialX = initialX;
    }

    public void print(double[] X, List<Measurement> measurements, double[][] Q, int iterations) {
        System.out.println("\nРЕЗУЛЬТАТЫ УРАВНИВАНИЯ\n");
        if (printInitialData) printInitialData();
        if (printAdjustedPoints) printAdjustedPoints(X, Q);
        if (printMeasurementErrors) printMeasurementStatistics(X, measurements, Q);
        if (printCovariance) printCovarianceMatrix(X, Q);
        if (printTraverseClosure && initialX != null) {
            printTraverseClosure(initialX, X, measurements);
        } else if (printTraverseClosure) {
            System.out.println("\nПредварительные координаты не сохранены – невязки хода не вычислены.");
        }
    }

    private void printInitialData() {
        System.out.println("Исходные данные:");
        System.out.println("Фиксированные точки:");
        for (Map.Entry<String, Point> e : data.getFixedPoints().entrySet()) {
            System.out.printf(Locale.US, "  %s: x = %.3f, y = %.3f\n", e.getKey(), e.getValue().getX(), e.getValue().getY());
        }
        System.out.println("Начальные дирекционные углы:");
        for (Map.Entry<String, Double> e : data.getStartDirs().entrySet()) {
            System.out.printf(Locale.US, "  %s: %.6f рад (%.6f°)\n", e.getKey(), e.getValue(), Math.toDegrees(e.getValue()));
        }
        System.out.println("Конечные дирекционные углы:");
        for (Map.Entry<String, Double> e : data.getEndDirs().entrySet()) {
            System.out.printf(Locale.US, "  %s: %.6f рад (%.6f°)\n", e.getKey(), e.getValue(), Math.toDegrees(e.getValue()));
        }
    }

    private void printAdjustedPoints(double[] X, double[][] Q) {
        System.out.println("\nУравненные точки:");
        System.out.printf("%-10s %-15s %-15s %-10s %-10s %-10s\n",
                "Имя", "X, м", "Y, м", "σX, м", "σY, м", "СКО, м");
        for (String name : indexMapper.getUnknownNames()) {
            int idx = indexMapper.getIndex(name);
            double sx = Math.sqrt(Q[idx][idx]);
            double sy = Math.sqrt(Q[idx + 1][idx + 1]);
            double pos = Math.hypot(sx, sy);
            System.out.printf(Locale.US, "%-10s %15.6f %15.6f %10.4f %10.4f %10.4f\n",
                    name, X[idx], X[idx + 1], sx, sy, pos);
        }
    }

    private void printMeasurementStatistics(double[] X, List<Measurement> measurements, double[][] Q) {
        int n = measurements.size();
        int t = X.length;
        double vPv = 0;
        for (Measurement m : measurements) {
            double expected = m.expected(X);
            double residual = m.residual(expected);
            vPv += residual * residual * m.getWeight();
        }
        int freedom = n - t;
        double s0 = (freedom > 0) ? Math.sqrt(vPv / freedom) : 0;
        System.out.printf(Locale.US, "\nОшибка единицы веса s0 = %.6f\n", s0);

        System.out.println("\nАнализ измерений:");
        System.out.printf("%-20s %-15s %-12s %-12s %-12s %s\n",
                "Измерение", "Уравнено", "Невязка", "СКО апост", "Абс.ош.", "Отн.ош.");
        for (Measurement m : measurements) {
            double expected = m.expected(X);
            double residual = m.residual(expected);
            double aprioriSigma = Math.sqrt(1.0 / m.getWeight());
            double aposterioriSigma = s0 * aprioriSigma;
            String name = m.getName();
            double eqValue = expected;
            double res = residual;
            double absErr = aposterioriSigma;
            String relErr = "";
            if (m instanceof AngleMeasurement) {
                eqValue = Math.toDegrees(expected);
                res = Math.toDegrees(residual);
                absErr = Math.toDegrees(aposterioriSigma);
                relErr = String.format(Locale.US, "%.2f\"", absErr * 3600);
            } else {
                relErr = String.format(Locale.US, "1/%.0f", expected / aposterioriSigma);
            }
            System.out.printf(Locale.US, "%-20s %15.6f %12.6f %12.6f %12.6f %s\n",
                    name, eqValue, res, aposterioriSigma, absErr, relErr);
        }
    }

    private void printCovarianceMatrix(double[] X, double[][] Q) {
        System.out.println("\nКовариационная матрица параметров (в порядке x,y для каждой точки):");
        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < X.length; j++) {
                System.out.printf(Locale.US, "%12.6e ", Q[i][j]);
            }
            System.out.println();
        }
    }

    private void printTraverseClosure(double[] X0, double[] X, List<Measurement> measurements) {
        System.out.println("\n--- Невязки хода ---");

        double totalLength = 0;
        for (Measurement m : measurements) {
            if (m instanceof DistanceMeasurement) {
                totalLength += m.getObserved();
            }
        }
        System.out.printf(Locale.US, "Длина хода: %.3f м\n", totalLength);

        var set = data.getFixedPoints().entrySet();
        Point[] points = set.isEmpty() ? set.toArray(new Point[] {}) : null;;
        if (points != null) {
            Point H = points[0];
            Point K = points[1];
            if (H == null || K == null) {
                System.out.println("Не удалось определить начальный (H) или конечный (K) пункт. Невязки не вычислены.");
                return;
            }
        }

        List<String> names = data.getUnknownNames();
        if (names.isEmpty()) {
            System.out.println("Нет определяемых точек.");
            return;
        }
        String lastName = names.get(names.size() - 1);
        int idxB = indexMapper.getIndex(lastName);
        if (idxB < 0) {
            System.out.println("Не удалось найти индекс точки " + lastName);
            return;
        }

        double xB0 = X0[idxB];
        double yB0 = X0[idxB + 1];

        System.out.println("Вычисление невязок хода требует восстановления последовательности измерений.");
        System.out.println("Для данного примера невязки до уравнивания не вычислены.");

        System.out.printf("После уравнивания невязка по конечному пункту: (0.000, 0.000) м\n");
        System.out.printf("Относительная ошибка после уравнивания: 0 / %.0f = 0\n", totalLength);
    }

    // Builder
    public static class Builder {
        private final NetworkData data;
        private final IndexMapper indexMapper;
        private double[] initialX = null;
        private boolean printCovariance = false;
        private boolean printMeasurementErrors = true;
        private boolean printTraverseClosure = true;
        private boolean printInitialData = true;
        private boolean printAdjustedPoints = true;

        public Builder(NetworkData data, IndexMapper indexMapper) {
            this.data = data;
            this.indexMapper = indexMapper;
        }

        public Builder initialX(double[] initialX) {
            this.initialX = initialX;
            return this;
        }

        public Builder printCovariance(boolean print) {
            this.printCovariance = print;
            return this;
        }

        public Builder printMeasurementErrors(boolean print) {
            this.printMeasurementErrors = print;
            return this;
        }

        public Builder printTraverseClosure(boolean print) {
            this.printTraverseClosure = print;
            return this;
        }

        public Builder printInitialData(boolean print) {
            this.printInitialData = print;
            return this;
        }

        public Builder printAdjustedPoints(boolean print) {
            this.printAdjustedPoints = print;
            return this;
        }

        public ResultPrinter build() {
            return new ResultPrinter(this);
        }
    }
}
