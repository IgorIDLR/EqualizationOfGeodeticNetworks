package ru.equalizationofgeodeticnetworks.network.builder;

import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.model.traverse.LevelingTraverse;
import ru.equalizationofgeodeticnetworks.model.traverse.PolygonometricTraverse;
import ru.equalizationofgeodeticnetworks.model.traverse.Traverse;

import java.util.List;
import java.util.Map;

@Component
public class InitialApproxBuilder {

    public double[] buildApprox(List<Traverse> traverses, Map<String, Integer> indexMap) {
        for (Traverse t : traverses) {
            if (!t.isApproxComputed()) t.computeInitialApprox();
        }
        int n = indexMap.size() * 2;
        double[] X = new double[n];
        int[] counters = new int[n];
        for (Traverse t : traverses) {
            if (t instanceof PolygonometricTraverse) {
                PolygonometricTraverse pt = (PolygonometricTraverse) t;
                List<Double> approx = pt.getInitialApprox();
                List<String> unknown = pt.getUnknownNames();
                for (int i = 0; i < unknown.size(); i++) {
                    String name = unknown.get(i);
                    Integer idx = indexMap.get(name);
                    if (idx != null) {
                        X[idx*2] += approx.get(i*2);
                        X[idx*2+1] += approx.get(i*2+1);
                        counters[idx*2]++; counters[idx*2+1]++;
                    }
                }
            } else if (t instanceof LevelingTraverse) {
                LevelingTraverse lt = (LevelingTraverse) t;
                List<Double> approx = lt.getInitialApprox();
                List<String> unknown = lt.getUnknownNames();
                for (int i = 0; i < unknown.size(); i++) {
                    String name = unknown.get(i);
                    Integer idx = indexMap.get(name);
                    if (idx != null) {
                        X[idx] += approx.get(i);
                        counters[idx]++;
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            if (counters[i] > 0) X[i] /= counters[i];
        }
        return X;
    }
}
