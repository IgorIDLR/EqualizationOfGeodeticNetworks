package ru.equalizationofgeodeticnetworks.traverse;

import ru.equalizationofgeodeticnetworks.adjustment.AdjustmentMethod;
import ru.equalizationofgeodeticnetworks.measurement.HeightMeasurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelingTraverse extends Traverse {

    private Map<String, Double> fixedHeights; // фиксированные отметки
    private List<String> pointNames; // порядок точек
    private List<HeightDifference> rawDiffs; // сырые превышения

    public static class HeightDifference {
        String from, to;
        double value, sigma;
    }

    public LevelingTraverse(AdjustmentMethod method) {
        super(method);
        fixedHeights = new HashMap<>();
        pointNames = new ArrayList<>();
        rawDiffs = new ArrayList<>();
    }

    public void addFixedPoint(String name, double height) {
        fixedHeights.put(name, height);
    }

    public void addUnknownPoint(String name) {
        pointNames.add(name);
    }

    public void addHeightDifference(String from, String to, double value, double sigma) {
        HeightDifference hd = new HeightDifference();
        hd.from = from;
        hd.to = to;
        hd.value = value;
        hd.sigma = sigma;
        rawDiffs.add(hd);
    }

    @Override
    public void buildInitialApprox() {
        // Простейший способ: последовательное вычисление отметок по ходу
        Map<String, Double> known = new HashMap<>(fixedHeights);
        for (HeightDifference hd : rawDiffs) {
            if (known.containsKey(hd.from) && !known.containsKey(hd.to)) {
                double hTo = known.get(hd.from) + hd.value;
                known.put(hd.to, hTo);
            } else if (!known.containsKey(hd.from) && known.containsKey(hd.to)) {
                double hFrom = known.get(hd.to) - hd.value;
                known.put(hd.from, hFrom);
            }
        }
        params = new double[pointNames.size()];
        for (int i = 0; i < pointNames.size(); i++) {
            String name = pointNames.get(i);
            Double h = known.get(name);
            if (h == null) throw new IllegalStateException("Не удалось вычислить приближение для " + name);
            params[i] = h;
        }
    }

    @Override
    public void solve(double eps, int maxIter) {
        buildInitialApprox();
        // Преобразуем сырые превышения в Measurement
        measurements.clear();
        for (HeightDifference hd : rawDiffs) {
            double weight = 1.0 / (hd.sigma * hd.sigma);
            String name = hd.from + "-" + hd.to;
            // Создадим класс HeightMeasurement, наследующий Measurement
            measurements.add(new HeightMeasurement(name, hd.value, weight,
                    getIndex(hd.from), getIndex(hd.to), fixedHeights));
        }
        super.solve(eps, maxIter);
    }

    private int getIndex(String name) {
        // Индекс параметра (отметки) в массиве params
        for (int i = 0; i < pointNames.size(); i++) {
            if (pointNames.get(i).equals(name)) return i;
        }
        return -1; // фиксированная точка
    }
}
